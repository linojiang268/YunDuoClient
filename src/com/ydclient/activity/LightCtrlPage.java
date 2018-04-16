package com.ydclient.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.ydclient.model.DeviceInfo;

import frame.ydclient.socket.MessageCallBack;
import frame.ydclient.socket.MyCon;

public class LightCtrlPage extends Activity {
	private ImageButton mibtOpen, mibtClose;
	private ImageView mivIcon;
	// private Gson gson;
	private DeviceInfo mDeviceInfo;
	private TextView mtvName;
	private String mParmInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_ctrl_light);
		// this.gson=new Gson();
		Intent intent = getIntent();
		if (null != intent && intent.hasExtra("DeviceInfo")) {
			mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
			if (null != mDeviceInfo) {
				this.findView();
				this.mParmInfo = mDeviceInfo.content.trim();
				this.setView();
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
		this.mivIcon = (ImageView) findViewById(R.id.ivIcon);
		this.mtvName = (TextView) findViewById(R.id.tvName);
		mibtOpen.setOnClickListener(clickListener);
		mibtClose.setOnClickListener(clickListener);
		mtvName.setText(mDeviceInfo.getName());
	}

	private void setView() {
		if (null != mParmInfo && "0".equals(mParmInfo)) {
			// 灯开着
			mivIcon.setImageResource(R.drawable.ctrl_light_opened_icon);
		} else {
			mivIcon.setImageResource(R.drawable.ctrl_light_closed_icon);
		}
	}

	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.ibtOpen:
				MyCon.con(LightCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6103, "{\"_id\":" + mDeviceInfo.get_id() + "}");
				mParmInfo = "0";
				break;
			case R.id.ibtClose:
				MyCon.con(LightCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6104, "{\"_id\":" + mDeviceInfo.get_id() + "}");
				mParmInfo = "1";
				break;
			default:
				break;
			}
			setView();
			// _id=设备id content=灯泡状态(0为开，1为关)
			MyCon.con(LightCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6105, "{\"_id\":" + mDeviceInfo._id + ",\"content\":" + mParmInfo + "}");
		}
	};

	private MessageCallBack messageCallBack = new MessageCallBack(LightCtrlPage.this) {
		@Override
		protected void onRead(int mark, int type, String content) {
			// showToast(type + "," + content);
			switch (type) {
			case 6103:
			case 6104:
			case 6005:
				try {
					showToast(new JSONObject(content).getString("msg"));
				} catch (JSONException e) {
					e.printStackTrace();
					showToast(getString(R.string.error_protocol));
				}
				break;
			case 6105:
				break;
			default:
				break;
			}
		}
	};

	private void showToast(String str) {
		Toast.makeText(LightCtrlPage.this, str, Toast.LENGTH_SHORT).show();
	}
}
