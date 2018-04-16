package com.ydclient.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.XMPPException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ydclient.data.MyNetData;
import com.ydclient.tool.ComTool;
import com.ydclient.xmpp.MyXMPP;

import frame.ydclient.socket.MyCon;
import frame.ydclient.socket.MySocketCon;
import frame.ydclient.socket.ReadBody;

/**
 * 
 * @author ouArea
 * 
 */
public class YDClientService extends Service {
	private Timer mTimer;
	private TimerTask mTimerTask;
	private ReadThread mReadThread;
	public static boolean hasStart, hasLogin, isExit = true;
	private MySocketCon mMySocketCon;

	private YDClientReceiver mYdClientReceiver;

	private String ip, password;
	private int port;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 注册接收消息服务广播
		mYdClientReceiver = new YDClientReceiver();
		registerReceiver(mYdClientReceiver, new IntentFilter(MyCon.RECEIVER_MSG));
	}

	@Override
	public void onDestroy() {
		if (null != mYdClientReceiver) {
			unregisterReceiver(mYdClientReceiver);
		}
		clientHandler.sendEmptyMessage(2);
		exit();
		super.onDestroy();
	}

	protected void exit() {
		isExit = true;
		hasStart = false;
		// 关闭定时重连
		timeCancel();
		// 关闭读取线程
		if (null != mReadThread) {
			mReadThread.close();
		}
		mReadThread = null;
		// 关闭socket
		if (null != mMySocketCon) {
			mMySocketCon.close();
		}
		mMySocketCon = null;
		MyXMPP.closeConnection();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (null != mTimer) {
			// 服务已经开了
			Toast.makeText(YDClientService.this, "服务已开启", Toast.LENGTH_SHORT).show();
			return;
		}
		isExit = false;
		hasStart = false;
		isLogining = false;
		// 服务器开启成功
		clientHandler.sendEmptyMessage(1);
		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				checkToLogin();
			}
		};
		// ip = MyNetData.loginIp;
		// port = MyNetData.loginPort;
		// password = MyNetData.loginPassword;
		mTimer.schedule(mTimerTask, 20, 1500);
	}

	public static boolean isLogining;

	private void checkToLogin() {
		if (MyNetData.isModel(YDClientService.this)) {
			if (!isExit && !hasStart && !isLogining && ComTool.isConnect(YDClientService.this)) {
				SharedPreferences sharedPreference = getSharedPreferences("user", MODE_PRIVATE);
				if (null != sharedPreference) {
					if (sharedPreference.contains("ip")) {
						ip = sharedPreference.getString("ip", "");
					}
					if (sharedPreference.contains("port")) {
						port = sharedPreference.getInt("port", 6666);
					}
					if (sharedPreference.contains("password")) {
						password = sharedPreference.getString("password", "123456");
					}
				}
				if (null != ip && port > 0 && null != password) {
					login();
				} else {
					sendToView(new ReadBody(0, 1, "登录失败"));
				}
			}
		} else {
			if (!isExit && !isLogining && !MyXMPP.isConnect()) {
				if (MyXMPP.isSet(YDClientService.this)) {
					loginGroup();
				} else {
					sendToView(new ReadBody(0, 1, "登录失败"));
				}
			}
		}
	}

	private void loginGroup() {
		isLogining = true;
		Thread loginGroupThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MyXMPP.initXmpp(YDClientService.this, clientHandler);
					mMySocketCon = new MySocketCon(YDClientService.this);
					mMySocketCon.setHandler(clientHandler);
					sendToView(new ReadBody(0, 0, "登录成功"));
				} catch (XMPPException e) {
					e.printStackTrace();
					sendToView(new ReadBody(0, 1, "登录失败"));
				}
				isLogining = false;
			}
		});
		loginGroupThread.start();
	}

	private void login() {
		hasStart = false;
		isLogining = true;
		Thread loginThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// SharedPreferences sharedPreferences =
					// getSharedPreferences("user", MODE_PRIVATE);
					mMySocketCon = new MySocketCon(YDClientService.this);
					mMySocketCon.setHandler(clientHandler);
					if (!mMySocketCon.connect(ip, port)) {
						sendToView(new ReadBody(0, 1, "连接服务器失败"));
						isLogining = false;
						return;
					}
					// 读到服务器（连接成功，准备好读取）响应后开发发送登录信息
					if (mMySocketCon.inStream.readUTF().equals("1")) {
						Log.i("YDClientService_read", "1");
						mMySocketCon.outStream.writeUTF("1");
						mMySocketCon.outStream.flush();
						Log.i("YDClientService_send", "1");
					} else {
						sendToView(new ReadBody(0, 1, "连接服务器失败"));
						isLogining = false;
						return;
					}
					ReadBody readBody0 = new ReadBody();
					// 读到服务器（连接成功，准备好读取）响应后开发发送登录信息
					// mMySocketCon.readZlib(readBody0);
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("mark", (int) (System.currentTimeMillis() / 1000) / 1000);
					jsonObject.put("type", 5001);
					jsonObject.put("msg", "{\"password\":\"" + password + "\"}");
					mMySocketCon.sendZlibSynchronized(jsonObject.toString());
					// mMySocketCon.sendZlibSynchronized((int)
					// (System.currentTimeMillis() / 1000) / 1000, 5001,
					// "{\"password\":\"" + password + "\"}");
					if (mMySocketCon.readZlib(readBody0)) {
						try {
							JSONObject jsObject0 = new JSONObject(readBody0.msg);
							if (0 == jsObject0.getInt("result")) {
								hasStart = true;
								isLogining = false;
								MyCon.setCon(mMySocketCon);
								mReadThread = new ReadThread();
								mReadThread.start();
								sendToView(new ReadBody(0, 0, "登录成功"));
								// 后台读取的某些请求
								// MyCon.con().sendZlib(0, 1601, "{}");
								SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
								Editor editor = sharedPreferences.edit();
								// editor.putString("ip", ip);
								// editor.putInt("port", port);
								// if
								// (sharedPreferences.getBoolean("savePassword",
								// true)) {
								// editor.putString("password", password);
								// } else {
								// editor.remove("password");
								// }
								if (sharedPreferences.getBoolean("autoLogin", true)) {
									editor.putBoolean("canLogin", true);
								}
								editor.commit();
								return;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (null != mMySocketCon) {
					mMySocketCon.close();
				}
				mMySocketCon = null;
				sendToView(new ReadBody(0, 1, "登录失败"));
			}
		});
		loginThread.start();
	}

	private void sendToView(ReadBody readBody) {
		Intent intent = new Intent(MyCon.RECEIVER_MSG);
		intent.putExtra("mark", readBody.mark);
		intent.putExtra("type", readBody.type);
		intent.putExtra("message", readBody.msg);
		// FLAG_EXCLUDE_STOPPED_PACKAGES
		intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		sendBroadcast(intent);
	}

	private Handler clientHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -1:
				hasStart = false;
				// 关闭读取线程
				if (null != mReadThread) {
					mReadThread.close();
				}
				mReadThread = null;
				// 关闭socket
				if (null != mMySocketCon) {
					mMySocketCon.close();
				}
				mMySocketCon = null;
				if (!isExit) {
					// 掉线重连
					Log.e("YDClientService", "掉线重连");
					// if (!isExit && !hasStart && !isLogining &&
					// ComTool.isConnect(MessageServiceF.this)) {
					checkToLogin();
					// }
				}
				break;
			case 0:
				Log.i("YDClientService", "连接服务器成功");
				break;
			case 1:
				Log.i("YDClientService", "服务开启成功");
				break;
			case 2:
				Log.i("YDClientService", "服务关闭成功");
				break;
			case 3:
				Log.i("YDClientService", "登录成功");
				break;
			case 66:
				// Bundle bd = msg.getData();
				// metRec.append(bd.getString("from").substring(bd.getString("from").indexOf("/"))
				// + "：" + bd.getString("body") + "\n");
				ReadBody readBody = (ReadBody) msg.obj;
				// 处理某些type
				checkSomeTypes(readBody);
				sendToView(readBody);
				break;
			default:
				break;
			}
		}

	};

	public void timeCancel() {
		if (null != mTimer) {
			mTimer.cancel();
		}
		if (null != mTimerTask) {
			mTimerTask.cancel();
		}
		mTimer = null;
		mTimerTask = null;
	}

	// --------------------------------------------读取线程-------------------------------------------------------
	private ReadBody readBody;

	private class ReadThread extends Thread {
		private boolean isRun;
		private Thread heartThread;

		@Override
		public void run() {
			super.run();
			isRun = true;
			readBody = new ReadBody();
			try {
				// Log.i("YDClientService", "begin接收:" +
				// mMySocketCon.con.getInetAddress().getHostName() + "," +
				// mMySocketCon.con.getPort());
				Log.i("YDClientService", "begin接收服务端消息");
				heartThread = new Thread(new Runnable() {
					@Override
					public void run() {
						while (isRun) {
							try {
								// mMySocketCon.sendZlib(-1, 0, "0");
								mMySocketCon.sendZlibSynchronized("0");
								sleep(8000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				});
				heartThread.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			while (true) {
				if (isRun && mMySocketCon.readZlib(readBody)) {
					// 处理某些type
					checkSomeTypes(readBody);
					sendToView(readBody);
					try {
						sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					break;
				}
			}
			Log.i("YDClientService", "ReadThread close.");
		}

		public void close() {
			isRun = false;
		}

	}

	private void checkSomeTypes(ReadBody readBody) {
		switch (readBody.type) {
		case 6605:
			try {
				// showToast(new JSONObject(content).getString("msg"));
				sendToNotification(new JSONObject(readBody.msg).getString("msg"));
			} catch (JSONException e) {
				e.printStackTrace();
				// showToast(getString(R.string.error_protocol));
			}
			break;
		default:
			break;
		}
		// switch (readBody.type) {
		// case 1601:
		// Gson gson = new Gson();
		// CitiesModel citysModel = gson.fromJson(readBody.msg,
		// CitiesModel.class);
		// CitiesService citysService = new CitiesService(MessageServiceF.this);
		// if (!citysService.deleteAll()) {
		// Log.e("F", "清空之前城市列表失败");
		// }
		// if (citysService.insertCitys(citysModel)) {
		// Log.i("F", "成功修改城市列表");
		// } else {
		// Log.e("F", "修改城市列表失败");
		// }
		// break;
		// default:
		// break;
		// }
	}

	private void sendToNotification(String str) {
		NotificationManager nm = (NotificationManager) YDClientService.this.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher, "云朵", System.currentTimeMillis() / 1000);
		Intent intent = new Intent(YDClientService.this, Main.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(YDClientService.this, 0, intent, 0);
		// must set this for content view, or will throw a
		// exception
		notification.setLatestEventInfo(YDClientService.this, "接收到上传数据", str, contentIntent);
		// notification.sound = Uri.parse("android.resource://" +
		// getPackageName() + "/" + R.raw.mm);
		notification.defaults = Notification.DEFAULT_SOUND;
		nm.notify(R.string.app_name, notification);
	}
}
