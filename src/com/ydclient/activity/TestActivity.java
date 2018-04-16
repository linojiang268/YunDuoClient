package com.ydclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.ydclient.data.MyNetData;

import frame.ydclient.socket.MessageCallBack;
import frame.ydclient.socket.MyCon;

/**
 * 客户端
 * 
 * @author ouArea
 * 
 */
public class TestActivity extends Activity {
	private EditText metAddress, metType, metMsg;
	private Button mbtCon, mbtSend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		this.findView();
		MyCon.addListener(messageCallBack);
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		StatService.onPause(this);
		super.onPause();
	};

	private void findView() {
		this.metAddress = (EditText) findViewById(R.id.etAddress);
		this.metType = (EditText) findViewById(R.id.etType);
		this.metMsg = (EditText) findViewById(R.id.etMsg);
		this.mbtCon = (Button) findViewById(R.id.btCon);
		this.mbtSend = (Button) findViewById(R.id.btSend);
		mbtCon.setOnClickListener(clickListener);
		mbtSend.setOnClickListener(clickListener);
	}

	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.btCon:
				MyNetData.loginIp = metAddress.getText().toString();
				startService(new Intent(TestActivity.this, YDClientService.class));
				break;
			case R.id.btSend:
				MyCon.con(TestActivity.this).sendZlib((int) (System.currentTimeMillis() / 1000), Integer.parseInt(metType.getText().toString()), metMsg.getText().toString());
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyCon.removeListener(messageCallBack);
		stopService(new Intent(TestActivity.this, YDClientService.class));
	}

	private MessageCallBack messageCallBack = new MessageCallBack(TestActivity.this) {

		@Override
		protected void onRead(int mark, int type, String content) {
			showToast(type + "," + content);
		}

	};

	private void showToast(String str) {
		Toast.makeText(TestActivity.this, str, Toast.LENGTH_SHORT).show();
	}
}
