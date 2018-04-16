package com.ydclient.activity;

import org.jivesoftware.smack.XMPPException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ydclient.view.MyProgressDialog;
import com.ydclient.xmpp.MyXMPP;

import frame.ydclient.socket.ReadBody;

public class XMPPTestPage extends Activity {
	private EditText metRec, metSend;
	private Button mbtSend;
	private MyProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_xmpp_test);
		this.findViews();
	}

	private void findViews() {
		this.mProgressDialog = new MyProgressDialog(XMPPTestPage.this) {
			@Override
			protected void exit() {
				finish();
			}
		};
		this.metRec = (EditText) findViewById(R.id.etRec);
		this.metSend = (EditText) findViewById(R.id.etSend);
		this.mbtSend = (Button) findViewById(R.id.btSend);
		mbtSend.setOnClickListener(clickListener);
		mProgressDialog.setMessage("远程测试连接初始化中...");
		mProgressDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// MyXMPP.setValue(XMPPTestPage.this, "192.168.0.116",
				// "yunduoZhanting", "conference.127.0.0.1");
				try {
					MyXMPP.initXmpp(XMPPTestPage.this, clientHandler);
					clientHandler.sendEmptyMessage(0);
				} catch (XMPPException e) {
					e.printStackTrace();
					MyXMPP.closeConnection();
					clientHandler.sendEmptyMessage(1);
				}
			}
		}).start();
	}

	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btSend:
				String msg = metSend.getText().toString().trim();
				if (!"".equals(msg)) {
					try {
						MyXMPP.sendMulMsg(6, 6, msg);
					} catch (Exception e) {
						Log.i("Main", "发送异常");
						e.printStackTrace();
						finish();
					} finally {
						metSend.setText("");
					}
				}
				break;
			default:
				break;
			}
		}
	};

	protected void onDestroy() {
		MyXMPP.closeConnection();
		// System.exit(0);
		// android.os.Process.killProcess(android.os.Process.myPid());
		super.onDestroy();
	};

	private Handler clientHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mProgressDialog.dismiss();
				showToast("远程测试访问成功");
				break;
			case 1:
				mProgressDialog.dismiss();
				showToast("远程测试访问失败");
				finish();
				break;
			case 66:
				// Bundle bd = msg.getData();
				// metRec.append(bd.getString("from").substring(bd.getString("from").indexOf("/"))
				// + "：" + bd.getString("body") + "\n");
				ReadBody readBody = (ReadBody) msg.obj;
				metRec.append("服务端返回：" + readBody.msg + "\n");
				break;
			default:
				break;
			}
		}

	};

	private void showToast(String str) {
		Toast.makeText(XMPPTestPage.this, str, Toast.LENGTH_SHORT).show();
	}
}
