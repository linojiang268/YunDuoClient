package frame.ydclient.socket;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 
 * @ClassName NioHandler
 * @Description TODO
 * @author ouArea
 * @date 2012-11-15
 */
public abstract class MessageCallBack extends Handler {
	private final int READ = 10001;
	protected Context mContext;

	public MessageCallBack(Context content) {
		super();
		this.mContext = content;
	}

	@Override
	final public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case READ:
			onRead(msg.getData().getInt("mark"), msg.getData().getInt("type"), msg.getData().getString("content"));
			break;
		default:
			break;
		}
	}

	final public void readMessage(int mark, int type, String content) {
		Message message = new Message();
		message.what = READ;
		Bundle bundle = new Bundle();
		if (-1 != mark) {
			bundle.putInt("mark", mark);
		}
		if (-1 != type) {
			bundle.putInt("type", type);
		}
		if (null != content) {
			bundle.putString("content", content);
		}
		message.setData(bundle);
		this.sendMessage(message);
	}

	/**
	 * 读到消息
	 * 
	 * @Title: onRead
	 * @Description: TODO
	 * @param type
	 * @param content
	 * @author: ouArea
	 * @return void
	 * @throws
	 */
	protected abstract void onRead(int mark, int type, String content);

}
