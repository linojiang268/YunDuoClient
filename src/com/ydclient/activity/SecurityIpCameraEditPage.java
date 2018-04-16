package com.ydclient.activity;

import java.util.Map;

import object.p2pipcam.nativecaller.NativeCaller;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.ydclient.activity.BridgeService.AddCameraInterface;
import com.ydclient.activity.BridgeService.IpcamClientInterface;
import com.ydclient.adapter.IpCameraSearchListAdapter;
import com.ydclient.db.IpCameraInfo;
import com.ydclient.db.IpCameraService;
import com.ydclient.ipcamera.ContentCommon;
import com.ydclient.ipcamera.SystemValue;
import com.ydclient.model.DeviceInfo;
import com.ydclient.model.DeviceUpdateModel;
import com.ydclient.type.TypeDevice;

import frame.ydclient.socket.MessageCallBack;
import frame.ydclient.socket.MyCon;

public class SecurityIpCameraEditPage extends Activity implements IpcamClientInterface, AddCameraInterface {
	private Button mbtSave, mbtLearn;
	private EditText metName;
	// private ImageButton mibtOpen, mibtClose;
	private DeviceInfo mDeviceInfo;
	// private HashMap<String, CommandInfo> mCommandMap;
	private ProgressDialog mProgressDialog;

	// private int mLearnType;
	// private String mLearnName;
	private String mMac = "0";
	private boolean isLearning = false;
	// ========================================
	private static final String STR_DID = "did";
	private static final String STR_MSG_PARAM = "msgparam";
	private static final int SEARCH_TIME = 3000;
	private EditText metDeviceId, metUser, metPwd;
	private TextView mtvStatus;
	private Button mbtConnect, mbtPlay, mbtSearch;
	// 0未连接、1在线、2正在连接
	private int tag = 0;
	private int option = ContentCommon.INVALID_OPTION;
	private int CameraType = ContentCommon.CAMERA_TYPE_MJPEG;
	private WifiManager manager = null;
	// private MyBroadCast receiver;
	private Intent intentbrod = null;
	private boolean isSearched;
	private IpCameraSearchListAdapter searchListAdapter = null;

	private IpCameraService mIpCameraService = new IpCameraService(SecurityIpCameraEditPage.this);
	private IpCameraInfo mIpCameraInfo;

	// ========================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_ipcamera_edit);
		Intent intent = getIntent();
		// if (null != intent && intent.hasExtra("DeviceInfo")) {
		// mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
		// }
		if (null != intent && intent.hasExtra("IpCameraInfo")) {
			mIpCameraInfo = (IpCameraInfo) intent.getSerializableExtra("IpCameraInfo");
		}
		this.findView();
		this.isSearched = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyCon.addListener(messageCallBack);
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		StatService.onPause(this);
		super.onPause();
	};

	@Override
	protected void onStop() {
		MyCon.removeListener(messageCallBack);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		mProgressDialog = null;
		tag = 0;
		super.onDestroy();
	}

	private void findView() {
		this.mbtSave = (Button) findViewById(R.id.btSave);
		this.mbtLearn = (Button) findViewById(R.id.btLearn);
		this.metName = (EditText) findViewById(R.id.etName);
		// this.mibtOpen = (ImageButton) findViewById(R.id.ibtOpen);
		// this.mibtClose = (ImageButton) findViewById(R.id.ibtClose);
		// mibtOpen.setOnClickListener(clickListener);
		// mibtClose.setOnClickListener(clickListener);
		mProgressDialog = new ProgressDialog(SecurityIpCameraEditPage.this);
		// mCommandMap = new HashMap<String, CommandInfo>();
		mbtSave.setOnClickListener(clickListener);
		mbtLearn.setOnClickListener(clickListener);
		// mLearnType = -1;
		// if (null != mDeviceInfo) {
		// metName.setText(mDeviceInfo.getName());
		// }
		// ========================================
		searchListAdapter = new IpCameraSearchListAdapter(this);
		manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		BridgeService.setAddCameraInterface(this);
		// receiver = new MyBroadCast();
		// IntentFilter filter = new IntentFilter();
		// filter.addAction("finish");
		// registerReceiver(receiver, filter);
		intentbrod = new Intent("drop");
		this.metDeviceId = (EditText) findViewById(R.id.etDeviceId);
		this.metUser = (EditText) findViewById(R.id.etUser);
		this.metPwd = (EditText) findViewById(R.id.etPwd);
		this.mtvStatus = (TextView) findViewById(R.id.tvStatus);
		this.mbtConnect = (Button) findViewById(R.id.btConnect);
		this.mbtPlay = (Button) findViewById(R.id.btPlay);
		this.mbtSearch = (Button) findViewById(R.id.btSearch);
		mbtConnect.setOnClickListener(clickListener);
		mbtPlay.setOnClickListener(clickListener);
		mbtSearch.setOnClickListener(clickListener);
		// ========================================
		if (null != mIpCameraInfo) {
			metName.setText(mIpCameraInfo.getName());
			metDeviceId.setText(mIpCameraInfo.getDeviceId());
			metUser.setText(mIpCameraInfo.getUser());
			metPwd.setText(mIpCameraInfo.getPwd());
		}
	}

	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btConnect:
				if (tag == 1) {
					showToast("摄像机已经处于在线状态...");
				} else if (tag == 2) {
					showToast("正在连接，请稍候...");
				} else {
					connectIpCamera();
				}
				break;
			case R.id.btPlay:
				Intent intent = new Intent(SecurityIpCameraEditPage.this, SecurityIpCameraPlayPage.class);
				startActivity(intent);
				break;
			case R.id.btSearch:
				searchCamera();
				break;
			case R.id.btLearn:
				if (true) {
					return;
				}
				if (null != mDeviceInfo && null != mDeviceInfo.type && TypeDevice.hasFeedBack(mDeviceInfo.getType().intValue())) {
					return;
				}
				isLearning = true;
				mProgressDialog.setMessage(getString(R.string.learn_waiting));
				mProgressDialog.show();
				// MyCon.con().sendZlib((int) (System.currentTimeMillis() /
				// 1000), 60111, "{}");
				break;
			// case R.id.ibtOpen:
			// if (null != mDeviceInfo && null != mDeviceInfo.type &&
			// TypeDevice.hasFeedBack(mDeviceInfo.getType().intValue())) {
			// return;
			// }
			// mProgressDialog.setMessage(getString(R.string.learn_waiting));
			// mProgressDialog.show();
			// mLearnType = 6603;
			// mLearnName = "开监控";
			// MyCon.con().sendZlib((int) (System.currentTimeMillis() / 1000),
			// 6001, "{}");
			// break;
			// case R.id.ibtClose:
			// if (null != mDeviceInfo && null != mDeviceInfo.type &&
			// TypeDevice.hasFeedBack(mDeviceInfo.getType().intValue())) {
			// return;
			// }
			// mProgressDialog.setMessage(getString(R.string.learn_waiting));
			// mProgressDialog.show();
			// mLearnType = 6604;
			// mLearnName = "关监控";
			// MyCon.con().sendZlib((int) (System.currentTimeMillis() / 1000),
			// 6001, "{}");
			// break;
			case R.id.btSave:
				if (metName.getText().toString().trim().length() <= 0) {
					showToast("请输入设备名称");
					return;
				}
				if (null == mIpCameraInfo) {
					mIpCameraInfo = new IpCameraInfo(null, metName.getText().toString().trim(), metDeviceId.getText().toString().trim(), metUser.getText().toString().trim(), metPwd.getText().toString().trim(), "0");
					if (mIpCameraService.insert(mIpCameraInfo)) {
						showToast("新增摄像头成功");
						finish();
					} else {
						showToast("新增摄像头失败");
					}
				} else {
					mIpCameraInfo.setName(metName.getText().toString().trim());
					mIpCameraInfo.setDeviceId(metDeviceId.getText().toString().trim());
					mIpCameraInfo.setUser(metUser.getText().toString().trim());
					mIpCameraInfo.setPwd(metPwd.getText().toString().trim());
					if (mIpCameraService.update(mIpCameraInfo)) {
						showToast("修改摄像头成功");
						finish();
					} else {
						showToast("修改摄像头失败");
					}
				}
				if (true) {
					return;
				}
				if (null == mDeviceInfo) {
					mDeviceInfo = new DeviceInfo(null, TypeDevice.SECURITY_UN_FB, mMac, metName.getText().toString().trim(), "0");
				} else {
					mDeviceInfo.setName(metName.getText().toString().trim());
					mDeviceInfo.setMark(mMac);
				}
				DeviceUpdateModel deviceUpdateModel = new DeviceUpdateModel();
				deviceUpdateModel.deviceInfo = mDeviceInfo;
				// List<CommandInfo> commandInfoList = null;
				// Set<Entry<String, CommandInfo>> commandSet =
				// mCommandMap.entrySet();
				// for (Entry<String, CommandInfo> entry : commandSet) {
				// if (null == commandInfoList) {
				// commandInfoList = new ArrayList<CommandInfo>();
				// }
				// commandInfoList.add(entry.getValue());
				// }
				// deviceUpdateModel.commandInfos = commandInfoList;
				MyCon.con(SecurityIpCameraEditPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6002, new Gson().toJson(deviceUpdateModel));
				showToast(getString(R.string.save_waiting));
				break;
			default:
				break;
			}
		}
	};

	private void connectIpCamera() {
		String deviceId = metDeviceId.getText().toString();
		String user = metUser.getText().toString();
		String pwd = metPwd.getText().toString();
		if (deviceId.length() <= 0) {
			showToast("请输入设备ID");
			return;
		}
		if (user.length() <= 0) {
			showToast("请输入用户名");
			return;
		}
		// in.setAction(ContentCommon.STR_CAMERA_INFO_RECEIVER);
		if (option == ContentCommon.INVALID_OPTION) {
			option = ContentCommon.ADD_CAMERA;
		}
		// Intent intent = new Intent();
		// intent.putExtra(ContentCommon.CAMERA_OPTION, option);
		// intent.putExtra(ContentCommon.STR_CAMERA_ID, deviceId);
		// intent.putExtra(ContentCommon.STR_CAMERA_USER, user);
		// intent.putExtra(ContentCommon.STR_CAMERA_PWD, pwd);
		// intent.putExtra(ContentCommon.STR_CAMERA_TYPE, CameraType);
		// progressBar.setVisibility(View.VISIBLE);
		mProgressDialog.setMessage("摄像头连接中，请稍候...");
		mProgressDialog.show();
		// sendBroadcast(in);
		SystemValue.deviceName = user;
		SystemValue.deviceId = deviceId;
		SystemValue.devicePass = pwd;
		BridgeService.setIpcamClientInterface(this);
		NativeCaller.Init();
		new Thread(new StartPPPPThread()).start();
		// overridePendingTransition(R.anim.in_from_right,
		// R.anim.out_to_left);// 进入动画
		// finish();
	}

	private MessageCallBack messageCallBack = new MessageCallBack(SecurityIpCameraEditPage.this) {
		@Override
		protected void onRead(int mark, int type, String content) {
			// showToast(type + "," + content);
			switch (type) {
			case 60111:
				if (!isLearning) {
					return;
				}
				isLearning = false;
				try {
					JSONObject resJsonObject = new JSONObject(content);
					if (0 == resJsonObject.getInt("result")) {
						// mCommandMap.put(String.valueOf(mLearnType), new
						// CommandInfo(null, mLearnType,
						// resJsonObject.getString("command"), mLearnName));
						mMac = resJsonObject.getString("command");
						mProgressDialog.dismiss();
						showToast(resJsonObject.getString("msg"));
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
					showToast(getString(R.string.error_protocol));
				}
				break;
			case 6002:
				try {
					JSONObject resJsonObject = new JSONObject(content);
					if (0 == resJsonObject.getInt("result")) {
						mProgressDialog.dismiss();
						showToast(resJsonObject.getString("msg"));
						finish();
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
					showToast(getString(R.string.error_protocol));
				}
				break;
			default:
				break;
			}
		}
	};

	private void showToast(String str) {
		Toast.makeText(SecurityIpCameraEditPage.this, str, Toast.LENGTH_SHORT).show();
	}

	// ==========================摄像头=========================================
	@Override
	public void BSMsgNotifyData(String did, int type, int param) {
		Log.d("ip", "type:" + type + " param:" + param);
		Bundle bd = new Bundle();
		Message msg = PPPPMsgHandler.obtainMessage();
		msg.what = type;
		bd.putInt(STR_MSG_PARAM, param);
		bd.putString(STR_DID, did);
		msg.setData(bd);
		PPPPMsgHandler.sendMessage(msg);
		if (type == ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS) {
			intentbrod.putExtra("ifdrop", param);
			sendBroadcast(intentbrod);
		}
	}

	@Override
	public void BSSnapshotNotify(String did, byte[] bImage, int len) {
		Log.i("ip", "BSSnapshotNotify---len" + len);
	}

	@Override
	public void callBackUserParams(String did, String user1, String pwd1, String user2, String pwd2, String user3, String pwd3) {

	}

	@Override
	public void CameraStatus(String did, int status) {

	}

	// private class MyBroadCast extends BroadcastReceiver {
	// @Override
	// public void onReceive(Context arg0, Intent arg1) {
	// SecurityIpCameraEditPage.this.finish();
	// Log.d("ip", "AddCameraActivity.this.finish()");
	// }
	//
	// }

	class StartPPPPThread implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(100);
				StartCameraPPPP();
			} catch (Exception e) {

			}
		}
	}

	private void StartCameraPPPP() {
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		int result = NativeCaller.StartPPPP(SystemValue.deviceId, SystemValue.deviceName, SystemValue.devicePass);
		Log.i("ip", "result:" + result);
	}

	@Override
	public void callBackSearchResultData(int cameraType, String strMac, String strName, String strDeviceID, String strIpAddr, int port) {
		if (!searchListAdapter.AddCamera(strMac, strName, strDeviceID)) {
			return;
		}
	}

	// ----
	private Handler PPPPMsgHandler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bd = msg.getData();
			int msgParam = bd.getInt(STR_MSG_PARAM);
			int msgType = msg.what;
			String did = bd.getString(STR_DID);
			switch (msgType) {
			case ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS:
				String resStr;
				switch (msgParam) {
				case ContentCommon.PPPP_STATUS_CONNECTING:
					resStr = "正在连接";
					if (null != mProgressDialog) {
						mProgressDialog.setMessage("摄像头连接中，请稍候...");
						mProgressDialog.show();
					}
					tag = 2;
					break;
				case ContentCommon.PPPP_STATUS_CONNECT_FAILED:
					resStr = "连接失败";
					mProgressDialog.dismiss();
					tag = 0;
					break;
				case ContentCommon.PPPP_STATUS_DISCONNECT:
					resStr = "断线";
					mProgressDialog.dismiss();
					tag = 0;
					break;
				case ContentCommon.PPPP_STATUS_INITIALING:
					resStr = "已连接, 正在初始化";
					if (null != mProgressDialog) {
						mProgressDialog.setMessage("摄像头连接中，请稍候...");
						mProgressDialog.show();
					}
					tag = 2;
					break;
				case ContentCommon.PPPP_STATUS_INVALID_ID:
					resStr = "ID号无效";
					mProgressDialog.dismiss();
					tag = 0;
					break;
				case ContentCommon.PPPP_STATUS_ON_LINE:
					resStr = "在线";
					mProgressDialog.dismiss();
					tag = 1;
					break;
				case ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE:
					resStr = "摄像机不在线";
					mProgressDialog.dismiss();
					tag = 0;
					break;
				case ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT:
					resStr = "连接超时";
					mProgressDialog.dismiss();
					tag = 0;
					break;
				case ContentCommon.PPPP_STATUS_CONNECT_ERRER:
					resStr = "密码错误";
					mProgressDialog.dismiss();
					tag = 0;
					break;
				default:
					resStr = "未知状态";
				}
				mtvStatus.setText(resStr);
				if (msgParam == ContentCommon.PPPP_STATUS_ON_LINE) {
					NativeCaller.PPPPGetSystemParams(did, ContentCommon.MSG_TYPE_GET_PARAMS);
				}
				if (msgParam == ContentCommon.PPPP_STATUS_INVALID_ID || msgParam == ContentCommon.PPPP_STATUS_CONNECT_FAILED || msgParam == ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE || msgParam == ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT || msgParam == ContentCommon.PPPP_STATUS_CONNECT_ERRER) {
					NativeCaller.StopPPPP(did);
				}
				break;
			case ContentCommon.PPPP_MSG_TYPE_PPPP_MODE:
				break;
			}
		}
	};

	private void searchCamera() {
		if (!isSearched) {
			isSearched = true;
			startSearch();
		} else {
			AlertDialog.Builder dialog = new AlertDialog.Builder(SecurityIpCameraEditPage.this);
			dialog.setTitle("搜索结果");
			dialog.setPositiveButton("刷新", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startSearch();

				}
			});
			dialog.setNegativeButton("取消", null);
			dialog.setAdapter(searchListAdapter, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int arg2) {
					Map<String, Object> mapItem = (Map<String, Object>) searchListAdapter.getItemContent(arg2);
					if (mapItem == null) {
						return;
					}
					String strName = (String) mapItem.get(ContentCommon.STR_CAMERA_NAME);
					String strDID = (String) mapItem.get(ContentCommon.STR_CAMERA_ID);
					String strUser = ContentCommon.DEFAULT_USER_NAME;
					String strPwd = ContentCommon.DEFAULT_USER_PWD;
					metUser.setText(strUser);
					metPwd.setText(strPwd);
					metDeviceId.setText(strDID);
				}
			});
			dialog.show();
		}
	}

	private void startSearch() {
		searchListAdapter.ClearAll();
		mProgressDialog.setMessage("正在搜索，请稍候...");
		mProgressDialog.show();
		new Thread(new SearchThread()).start();
		updateListHandler.postDelayed(updateThread, SEARCH_TIME);
	}

	private class SearchThread implements Runnable {
		@Override
		public void run() {
			Log.d("tag", "startSearch");
			NativeCaller.StartSearch();
		}
	}

	Runnable updateThread = new Runnable() {
		public void run() {
			NativeCaller.StopSearch();
			if (null == mProgressDialog && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			Message msg = updateListHandler.obtainMessage();
			msg.what = 1;
			updateListHandler.sendMessage(msg);
		}
	};
	// 15576341699
	Handler updateListHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				searchListAdapter.notifyDataSetChanged();
				if (searchListAdapter.getCount() > 0) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(SecurityIpCameraEditPage.this);
					dialog.setTitle("");
					dialog.setPositiveButton("刷新", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startSearch();
						}
					});
					dialog.setNegativeButton("取消", null);
					dialog.setAdapter(searchListAdapter, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int arg2) {
							Map<String, Object> mapItem = (Map<String, Object>) searchListAdapter.getItemContent(arg2);
							if (mapItem == null) {
								return;
							}

							String strName = (String) mapItem.get(ContentCommon.STR_CAMERA_NAME);
							String strDID = (String) mapItem.get(ContentCommon.STR_CAMERA_ID);
							String strUser = ContentCommon.DEFAULT_USER_NAME;
							String strPwd = ContentCommon.DEFAULT_USER_PWD;
							metUser.setText(strUser);
							metPwd.setText(strPwd);
							metDeviceId.setText(strDID);
						}
					});
					dialog.show();
				} else {
					showToast("没有搜索到,请重试...");
					isSearched = false;// 如果没有搜索到，可以再次搜索
				}
			}
		}
	};
}
