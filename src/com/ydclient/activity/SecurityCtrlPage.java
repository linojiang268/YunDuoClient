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
import com.ydclient.type.TypeDevice;

import frame.ydclient.socket.MessageCallBack;
import frame.ydclient.socket.MyCon;

public class SecurityCtrlPage extends Activity {
	public int flag = (int) (System.currentTimeMillis() / 1000);
	private ImageView mivSecurityType;
	private TextView mtvSecurityType;
	private ImageButton mibtOpen, mibtClose;
	// private Gson gson;
	private DeviceInfo mDeviceInfo;
	private TextView mtvName;
	private String mParmInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_ctrl_security);
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
	};

	@Override
	protected void onStop() {
		MyCon.removeListener(messageCallBack);
		super.onStop();
	}

	private void findView() {
		this.mivSecurityType = (ImageView) findViewById(R.id.ivSecurityType);
		this.mtvSecurityType = (TextView) findViewById(R.id.tvSecurityType);
		this.mibtOpen = (ImageButton) findViewById(R.id.ibtOpen);
		this.mibtClose = (ImageButton) findViewById(R.id.ibtClose);
		this.mtvName = (TextView) findViewById(R.id.tvName);
		mibtOpen.setOnClickListener(clickListener);
		mibtClose.setOnClickListener(clickListener);
		switch (mDeviceInfo.type) {
		case TypeDevice.SECURITY_FB:
		case TypeDevice.SECURITY_UN_FB:
			mivSecurityType.setImageResource(R.drawable.ctrl_security_icon);
			mtvSecurityType.setText(R.string.main_security);
			break;
		case TypeDevice.SECURITY_FB_BLIND:
			mivSecurityType.setImageResource(R.drawable.ctrl_security_blind_icon);
			mtvSecurityType.setText(R.string.main_security_blind);
			break;
		case TypeDevice.SECURITY_FB_GAS:
			mivSecurityType.setImageResource(R.drawable.ctrl_security_gas_icon);
			mtvSecurityType.setText(R.string.main_security_gas);
			break;
		case TypeDevice.SECURITY_FB_SMOKE:
			mivSecurityType.setImageResource(R.drawable.ctrl_security_smoke_icon);
			mtvSecurityType.setText(R.string.main_security_smoke);
			break;
		case TypeDevice.SECURITY_FB_TEMPERATURE:
			mivSecurityType.setImageResource(R.drawable.ctrl_security_temperature_icon);
			mtvSecurityType.setText(R.string.main_security_temperature);
			break;
		case TypeDevice.SECURITY_FB_DOOR:
			mivSecurityType.setImageResource(R.drawable.ctrl_security_door_icon);
			mtvSecurityType.setText(R.string.main_security_door);
			break;
		default:
			mivSecurityType.setImageResource(R.drawable.ctrl_security_icon);
			mtvSecurityType.setText(R.string.main_security);
			break;
		}
		mtvName.setText(mDeviceInfo.getName());
	}

	private void setView() {
		// if (null != mParmInfo && "0".equals(mParmInfo)) {
		// // 灯开着
		// mivIcon.setImageResource(R.drawable.ctrl_light_opened_icon);
		// } else {
		// mivIcon.setImageResource(R.drawable.ctrl_light_closed_icon);
		// }
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (TypeDevice.SECURITY_UN_FB == mDeviceInfo.type.intValue()) {
				switch (arg0.getId()) {
				case R.id.ibtOpen:
					MyCon.con(SecurityCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6608, "{\"_id\":" + mDeviceInfo.get_id() + "}");
					// mParmInfo = "0";
					break;
				case R.id.ibtClose:
					MyCon.con(SecurityCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6609, "{\"_id\":" + mDeviceInfo.get_id() + "}");
					// mParmInfo = "1";
					break;
				default:
					break;
				}
				// MyCon.con().sendZlib(flag, 6606, "{\"_id\":" +
				// mDeviceInfo._id + ",\"content\":" + mParmInfo + "}");
			} else {
				switch (arg0.getId()) {
				case R.id.ibtOpen:
					MyCon.con(SecurityCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6603, "{\"_id\":" + mDeviceInfo.get_id() + "}");
					mParmInfo = "0";
					break;
				case R.id.ibtClose:
					MyCon.con(SecurityCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6604, "{\"_id\":" + mDeviceInfo.get_id() + "}");
					mParmInfo = "1";
					break;
				default:
					break;
				}
				// _id=设备id content=报警状态(0为开，1为关)
				MyCon.con(SecurityCtrlPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6606, "{\"_id\":" + mDeviceInfo._id + ",\"content\":" + mParmInfo + "}");
			}
		}
	};

	private MessageCallBack messageCallBack = new MessageCallBack(SecurityCtrlPage.this) {
		@Override
		protected void onRead(int mark, int type, String content) {
			// showToast(type + "," + content);
			switch (type) {
			case 6603:
			case 6604:
			case 6005:
				try {
					showToast(new JSONObject(content).getString("msg"));
				} catch (JSONException e) {
					e.printStackTrace();
					showToast(getString(R.string.error_protocol));
				}
				break;
			// case 6606:
			// if (flag == mark) {
			// try {
			// showToast(new JSONObject(content).getString("msg"));
			// } catch (JSONException e) {
			// e.printStackTrace();
			// showToast(getString(R.string.error_protocol));
			// }
			// }
			// break;
			case 6608:
			case 6609:
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
		Toast.makeText(SecurityCtrlPage.this, str, Toast.LENGTH_SHORT).show();
	}
}
