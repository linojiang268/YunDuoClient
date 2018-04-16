package com.ydclient.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.ydclient.model.DeviceInfo;

import frame.ydclient.socket.MessageCallBack;
import frame.ydclient.socket.MyCon;

public class BlindCtrlPage extends Activity {
	private ImageButton mibtOpen, mibtClose, mibtPause;
	// private Gson gson;
	private DeviceInfo mDeviceInfo;
	private TextView mtvName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_ctrl_blind);
		// this.gson=new Gson();
		Intent intent = getIntent();
		if (null != intent && intent.hasExtra("DeviceInfo")) {
			mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
			if (null != mDeviceInfo) {
				this.findView();
				return;
			}
		}
		showToast(getString(R.string.error_page));
		finish();
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
	}

	@Override
	protected void onStop() {
		MyCon.removeListener(messageCallBack);
		super.onStop();
	}

	private void findView() {
		this.mibtOpen = (ImageButton) findViewById(R.id.ibtOpen);
		this.mibtClose = (ImageButton) findViewById(R.id.ibtClose);
		this.mibtPause = (ImageButton) findViewById(R.id.ibtPause);
		this.mtvName = (TextView) findViewById(R.id.tvName);
		mibtOpen.setOnClickListener(clickListener);
		mibtClose.setOnClickListener(clickListener);
		mibtPause.setOnClickListener(clickListener);
		mtvName.setText(mDeviceInfo.getName());
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.ibtOpen:
				MyCon.con(BlindCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6203, "{\"_id\":" + mDeviceInfo.get_id() + "}");
				break;
			case R.id.ibtClose:
				MyCon.con(BlindCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6204, "{\"_id\":" + mDeviceInfo.get_id() + "}");
				break;
			case R.id.ibtPause:
				MyCon.con(BlindCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6206, "{\"_id\":" + mDeviceInfo.get_id() + "}");
				break;
			default:
				break;
			}
		}
	};

	private MessageCallBack messageCallBack = new MessageCallBack(BlindCtrlPage.this) {
		@Override
		protected void onRead(int mark, int type, String content) {
			// showToast(type + "," + content);
			switch (type) {
			case 6203:
			case 6204:
			case 6206:
			case 6005:
				try {
					showToast(new JSONObject(content).getString("msg"));
				} catch (JSONException e) {
					e.printStackTrace();
					showToast(getString(R.string.error_protocol));
				}
				break;
			default:
				break;
			}
		}
	};

	private void showToast(String str) {
		Toast.makeText(BlindCtrlPage.this, str, Toast.LENGTH_SHORT).show();
	}
}
