package frame.ydclient.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.ydclient.data.MyNetData;
import com.ydclient.xmpp.MyXMPP;

/**
 * Socket连接类
 * 
 * @author ouArea
 * 
 */
public class MySocketCon {
	private final String TAG = "MySocketCon";
	private Context context;
	private Handler mHandler;
	private String mAddress, mPort;
	private final int tryNum = 3, timeOut = 30000;

	public Socket con = null;
	public DataOutputStream outStream;
	public DataInputStream inStream;
	// private byte[] readBytes;

	private final int multiThreadNum = 3;
	private ExecutorService pool;
	{
		// charset = Charset.forName("UTF-8");
		java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
		java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
	}

	/**
	 * 初始化配置(客户端)
	 * 
	 * @param context
	 * @param handler
	 * @param address
	 * @param port
	 */
	public MySocketCon(Context context) {
		super();
		this.context = context;
		// this.readBytes = new byte[1024];
	}

	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}

	/**
	 * 连接
	 */
	public boolean connect(String address, Integer port) {
		this.mAddress = address;
		Log.i("连接服务器", address + "," + port);
		this.mPort = String.valueOf(port);
		int num;
		InetSocketAddress inetSocketAddress = new InetSocketAddress(mAddress, Integer.parseInt(mPort));
		for (num = 0; num < tryNum; num++) {
			try {
				Socket socket = new Socket();
				socket.connect(inetSocketAddress, timeOut);
				con = socket;
				inStream = new DataInputStream(socket.getInputStream());
				outStream = new DataOutputStream(socket.getOutputStream());
				if (null != mHandler) {
					mHandler.sendEmptyMessage(0);
				}
				return true;
			} catch (ConnectException e) {
				e.printStackTrace();
				Log.e(TAG, "连接超时");
			} catch (SocketException e) {
				e.printStackTrace();
				Log.e(TAG, "连接错误");
			} catch (IOException ee) {
				ee.printStackTrace();
				Log.e(TAG, "io错误");
			}
		}
		if (tryNum == num) {
			close();
		}
		return false;
	}

	/**
	 * 发送
	 * 
	 * @Title: sendZlib
	 * @Description: TODO
	 * @param type
	 * @param msg
	 * @author: ouArea
	 * @return void
	 * @throws
	 */
	public void sendZlib(int mark, int type, String msg) {
		if (null == context) {
			return;
		}
		if (MyNetData.isModel(context)) {
			if (null == sendThread || !sendThread.isAlive()) {
				sendThread = new SendThread();
				getPool().execute(sendThread);
				getPool().shutdown();
				while (null == sendThread || null == sendThread.mHandler) {
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("mark", mark);
				jsonObject.put("type", type);
				jsonObject.put("msg", msg);
			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}
			// Bundle bundle = new Bundle();
			// bundle.putInt("mark", mark);
			// bundle.putInt("type", type);
			// bundle.putString("msg", msg);
			// Message message = new Message();
			// message.setData(bundle);
			// sendThread.mHandler.sendMessage(message);
			Message message = new Message();
			message.obj = jsonObject.toString();
			sendThread.mHandler.sendMessage(message);
		} else {
			try {
				MyXMPP.sendMulMsg(mark, type, msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected SendThread sendThread;

	// 发送线程队列
	class SendThread extends Thread {
		public Handler mHandler;

		public void run() {
			Looper.prepare();
			mHandler = new Handler() {
				public void handleMessage(Message msg) {
					// Bundle bundle = msg.getData();
					// sendZlibSynchronized(bundle.getInt("mark"),
					// bundle.getInt("type"), bundle.getString("msg"));
					sendZlibSynchronized((String) msg.obj);
				}
			};
			Looper.loop();
		}
	}

	/**
	 * 同步发送
	 * 
	 * @Title: sendZlibSynchronizedvoid
	 * @Description: TODO
	 * @param type
	 * @param msg
	 * @author: ouArea
	 * @return void
	 * @throws
	 */
	// public synchronized void sendZlibSynchronized(int mark, int type, String
	// msg) {
	// try {
	// if (mark < 0) {
	// outStream.writeInt(mark);
	// outStream.flush();
	// return;
	// }
	// // 类型
	// // byte[] head1 = ByteTool.shortToByteArray((short) type);
	// // byte[] tmp = new byte[1];
	// // tmp[0] = head1[0];
	// // head1[0] = head1[1];
	// // head1[1] = tmp[0];
	// // 压缩后的长度和压缩后的消息体
	// byte[] msgBodyAll = msg.getBytes("UTF-8");
	// byte[] msgBodyCompress = ByteTool.compress(msgBodyAll);
	// // byte[] head2 = ByteTool.shortToByteArray((short)
	// // msgBodyCompress.length);
	// // tmp[0] = head2[0];
	// // head2[0] = head2[1];
	// // head2[1] = tmp[0];
	// // 组合
	// // byte[] all = new byte[head1.length + head2.length +
	// // msgBodyCompress.length];
	// // System.arraycopy(head1, 0, all, 0, head1.length);
	// // System.arraycopy(head2, 0, all, head1.length, head2.length);
	// // System.arraycopy(msgBodyCompress, 0, all, head1.length +
	// // head2.length, msgBodyCompress.length);
	// outStream.writeInt(mark);
	// outStream.writeInt(type);
	// outStream.writeInt(msgBodyCompress.length);
	// outStream.write(msgBodyCompress, 0, msgBodyCompress.length);
	// outStream.flush();
	// Log.i("DC_send", type + "," + msg);
	// } catch (IOException e) {
	// e.printStackTrace();
	// closeWithException();
	// // throw new OutLineException("IOE");
	// } catch (NullPointerException e) {
	// e.printStackTrace();
	// closeWithException();
	// // throw new OutLineException("NullPinter");
	// }
	// }
	public void sendZlibSynchronized(String msg) {
		try {
			outStream.writeUTF(msg);
			outStream.flush();
			Log.i("send", msg);
		} catch (IOException e) {
			e.printStackTrace();
			closeWithException();
			// throw new OutLineException("IOE");
		} catch (NullPointerException e) {
			e.printStackTrace();
			closeWithException();
			// throw new OutLineException("NullPinter");
		}
	}

	/**
	 * 读取
	 * 
	 * @Title: readZlib
	 * @Description: TODO
	 * @param readBody
	 * @return
	 * @author: ouArea
	 * @return boolean
	 * @throws
	 */
	// public boolean readZlib(ReadBody readBody) {
	// try {
	// readBody.mark = inStream.readInt();
	// if (readBody.mark < 0) {
	// return true;
	// }
	// readBody.type = inStream.readInt();
	// int length = inStream.readInt();
	// byte[] msgBodyBytes = new byte[length];
	// for (int i = 0; i < msgBodyBytes.length; i++) {
	// msgBodyBytes[i] = inStream.readByte();
	// }
	// byte[] decodeBytes = ByteTool.decompress(msgBodyBytes);
	// readBody.msg = new String(decodeBytes, "UTF-8");
	// Log.i("DC_read", readBody.type + "," + readBody.msg);
	// return true;
	// } catch (IOException e) {
	// e.printStackTrace();
	// closeWithException();
	// return false;
	// } catch (NullPointerException e) {
	// e.printStackTrace();
	// closeWithException();
	// return false;
	// }
	// }
	public boolean readZlib(ReadBody readBody) {
		try {
			if (readBody.parse(inStream.readUTF())) {
				Log.i("read", readBody.type + "," + readBody.msg);
				return true;
			}
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			closeWithException();
			return false;
		} catch (NullPointerException e) {
			e.printStackTrace();
			closeWithException();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			closeWithException();
			return false;
		}
	}

	private ExecutorService getPool() {
		if (null == pool || pool.isShutdown()) {
			pool = Executors.newFixedThreadPool(multiThreadNum);
		}
		return pool;
	}

	private void destoryPool() {
		if (null != pool) {
			try {
				pool.shutdownNow();
			} catch (Exception e) {
				e.printStackTrace();
			}
			pool = null;
		}
	}

	public boolean isClose = false;

	/**
	 * 关闭
	 */
	public void close() {
		if (isClose) {
			return;
		}
		isClose = true;
		if (null != sendThread) {
			if (null != sendThread.mHandler) {
				sendThread.mHandler.getLooper().quit();
			}
			sendThread.mHandler = null;
		}
		sendThread = null;
		if (null != inStream) {
			try {
				inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			inStream = null;
		}
		if (null != outStream) {
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			outStream = null;
		}
		if (null != con) {
			try {
				con.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			con = null;
		}
		destoryPool();
	}

	private void closeWithException() {
		close();
		if (null != mHandler) {
			mHandler.sendEmptyMessage(-1);
			mHandler = null;
		}
	}
}
