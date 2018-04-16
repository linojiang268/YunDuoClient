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

public class TvCtrlPage extends Activity {
	private ImageButton mibtPower, mibtNoSound, mibtAdd, mibtSub, mibtAddM, mibtSubM;
	private ImageButton mibtTop, mibtBottom, mibtLeft, mibtRight;
	private TextView mtvOk, mtvMenu, mtvBack;
	// private Gson gson;
	private DeviceInfo mDeviceInfo;
	private TextView mtvName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_ctrl_tv);
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
	};

	@Override
	protected void onStop() {
		MyCon.removeListener(messageCallBack);
		super.onStop();
	}

	private void findView() {
		this.mibtPower = (ImageButton) findViewById(R.id.ibtPower);
		this.mibtNoSound = (ImageButton) findViewById(R.id.ibtNoSound);
		this.mibtAdd = (ImageButton) findViewById(R.id.ibtAdd);
		this.mibtSub = (ImageButton) findViewById(R.id.ibtSub);
		this.mibtAddM = (ImageButton) findViewById(R.id.ibtAddM);
		this.mibtSubM = (ImageButton) findViewById(R.id.ibtSubM);
		// ------
		this.mibtTop = (ImageButton) findViewById(R.id.ibtTop);
		this.mibtBottom = (ImageButton) findViewById(R.id.ibtBottom);
		this.mibtLeft = (ImageButton) findViewById(R.id.ibtLeft);
		this.mibtRight = (ImageButton) findViewById(R.id.ibtRight);
		this.mtvOk = (TextView) findViewById(R.id.tvOk);
		this.mtvMenu = (TextView) findViewById(R.id.tvMenu);
		this.mtvBack = (TextView) findViewById(R.id.tvBack);
		this.mtvName = (TextView) findViewById(R.id.tvName);
		mibtPower.setOnClickListener(clickListener);
		mibtNoSound.setOnClickListener(clickListener);
		mibtAdd.setOnClickListener(clickListener);
		mibtSub.setOnClickListener(clickListener);
		mibtAddM.setOnClickListener(clickListener);
		mibtSubM.setOnClickListener(clickListener);
		mibtTop.setOnClickListener(clickListener);
		mibtBottom.setOnClickListener(clickListener);
		mibtLeft.setOnClickListener(clickListener);
		mibtRight.setOnClickListener(clickListener);
		mtvOk.setOnClickListener(clickListener);
		mtvMenu.setOnClickListener(clickListener);
		mtvBack.setOnClickListener(clickListener);
		mtvName.setText(mDeviceInfo.getName());
	}

	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.ibtPower:
				MyCon.con(TvCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6303, "{\"_id\":" + mDeviceInfo.get_id() + "}");
				break;
			case R.id.ibtNoSound:
				MyCon.con(TvCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6310, "{\"_id\":" + mDeviceInfo.get_id() + "}");
				break;
			case R.id.ibtAdd:
				MyCon.con(TvCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6306, "{\"_id\":" + mDeviceInfo.get_id() + "}");
				break;
			case R.id.ibtSub:
				MyCon.con(TvCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6307, "{\"_id\":" + mDeviceInfo.get_id() + "}");
				break;
			case R.id.ibtAddM:
				MyCon.con(TvCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6308, "{\"_id\":" + mDeviceInfo.get_id() + "}");
				break;
			case R.id.ibtSubM:
				MyCon.con(TvCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6309, "{\"_id\":" + mDeviceInfo.get_id() + "}");
				break;
			case R.id.ibtTop:
				MyCon.con(TvCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6311, "{\"_id\":" + mDeviceInfo.get_id() + "}");
				break;
			case R.id.ibtBottom:
				MyCon.con(TvCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6312, "{\"_id\":" + mDeviceInfo.get_id() + "}");
				break;
			case R.id.ibtLeft:
				MyCon.con(TvCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6313, "{\"_id\":" + mDeviceInfo.get_id() + "}");
				break;
			case R.id.ibtRight:
				MyCon.con(TvCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6314, "{\"_id\":" + mDeviceInfo.get_id() + "}");
				break;
			case R.id.tvOk:
				MyCon.con(TvCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6315, "{\"_id\":" + mDeviceInfo.get_id() + "}");
				break;
			case R.id.tvMenu:
				MyCon.con(TvCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6316, "{\"_id\":" + mDeviceInfo.get_id() + "}");
				break;
			case R.id.tvBack:
				MyCon.con(TvCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6317, "{\"_id\":" + mDeviceInfo.get_id() + "}");
				break;
			default:
				break;
			}
		}
	};

	private MessageCallBack messageCallBack = new MessageCallBack(TvCtrlPage.this) {
		@Override
		protected void onRead(int mark, int type, String content) {
			// showToast(type + "," + content);
			switch (type) {
			// case 6304:
			case 6303:
			case 6306:
			case 6307:
			case 6308:
			case 6309:
			case 6310:
			case 6311:
			case 6312:
			case 6313:
			case 6314:
			case 6315:
			case 6316:
			case 6317:
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
		Toast.makeText(TvCtrlPage.this, str, Toast.LENGTH_SHORT).show();
	}
}
