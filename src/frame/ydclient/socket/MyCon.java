package frame.ydclient.socket;

import java.util.ArrayList;

import android.content.Context;

public class MyCon {
	public static final String RECEIVER_MSG = "RECEIVER_MSG";
	private static MySocketCon con;

	public static boolean checkCon() {
		if (null == con || con.isClose) {
			return false;
		} else {
			return true;
		}
	}

	public synchronized static MySocketCon con(Context context) {
		if (null == con) {
			con = new MySocketCon(context);
		}
		return con;
	}

	public static void setCon(MySocketCon con) {
		MyCon.con = con;
	}

	public static void clear() {
		// if (null != con) {
		// con.close();
		// }
		// con = null;
		if (null != messageCallBacks) {
			messageCallBacks.clear();
		}
		messageCallBacks = null;
	}

	private static ArrayList<MessageCallBack> messageCallBacks;

	public static boolean hasListener(MessageCallBack messageCallBack) {
		if (null != messageCallBacks && messageCallBacks.contains(messageCallBack)) {
			return true;
		} else {
			return false;
		}
	}

	public static void addListener(MessageCallBack messageCallBack) {
		if (null == messageCallBacks) {
			messageCallBacks = new ArrayList<MessageCallBack>();
		}
		if (null != messageCallBack && !messageCallBacks.contains(messageCallBack)) {
			messageCallBacks.add(messageCallBack);
		}
	}

	public static void removeListener(MessageCallBack messageCallBack) {
		if (null != messageCallBack && null != messageCallBacks && messageCallBacks.contains(messageCallBack)) {
			messageCallBacks.remove(messageCallBack);
		}
	}

	public static void handMessage(int mark, int type, String message) {
		if (null != messageCallBacks) {
			for (int i = 0; i < messageCallBacks.size(); i++) {
				messageCallBacks.get(i).readMessage(mark, type, message);
			}
		}
	}
}
