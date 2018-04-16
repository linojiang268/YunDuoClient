package com.ydclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.ydclient.model.DeviceInfo;
import com.ydclient.model.SceneCommandMsg;
import com.ydclient.type.TypeDevice;

public class SceneSecurityPage extends Activity {
	private Gson gson;
	private DeviceInfo mDeviceInfo;
	private SceneCommandMsg mSceneCommandMsg;
	private TextView mtvName;

	private ImageView mivSecurityType;
	private TextView mtvSecurityType;

	private LinearLayout mllOpen, mllClose;
	private CheckBox mcbOpen, mcbClose;
	private Button mbtSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_scene_security);
		Intent intent = getIntent();
		if (null != intent && intent.hasExtra("DeviceInfo")) {
			mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
			if (null != mDeviceInfo) {
				// if (null != intent && intent.hasExtra("SceneCommandMsg")) {
				// mSceneCommandMsg = (SceneCommandMsg)
				// intent.getSerializableExtra("SceneCommandMsg");
				// }
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
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		StatService.onPause(this);
		super.onPause();
	};

	private void findView() {
		this.gson = new Gson();
		this.mllOpen = (LinearLayout) findViewById(R.id.llOpen);
		this.mllClose = (LinearLayout) findViewById(R.id.llClose);
		this.mcbOpen = (CheckBox) findViewById(R.id.cbOpen);
		this.mcbClose = (CheckBox) findViewById(R.id.cbClose);
		this.mtvName = (TextView) findViewById(R.id.tvName);
		this.mbtSave = (Button) findViewById(R.id.btSave);
		this.mivSecurityType = (ImageView) findViewById(R.id.ivSecurityType);
		this.mtvSecurityType = (TextView) findViewById(R.id.tvSecurityType);
		mtvName.setText(mDeviceInfo.getName());
		mbtSave.setOnClickListener(clickListener);
		mcbOpen.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					mcbClose.setChecked(false);
				}
			}
		});
		mcbClose.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					mcbOpen.setChecked(false);
				}
			}
		});
		mllOpen.setOnClickListener(clickListener);
		mllClose.setOnClickListener(clickListener);
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
	}

	// 6603开、6604关
	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.btSave:
				if (mcbOpen.isChecked() || mcbClose.isChecked()) {
					mSceneCommandMsg = new SceneCommandMsg();
					mSceneCommandMsg.d_id = mDeviceInfo.get_id();
					if (TypeDevice.SECURITY_UN_FB == mDeviceInfo.getType().intValue()) {
						mSceneCommandMsg.c_type = mcbOpen.isChecked() ? 6608 : 6609;
					} else {
						mSceneCommandMsg.c_type = mcbOpen.isChecked() ? 6603 : 6604;
					}
					mSceneCommandMsg.c_msg = "无";
					mSceneCommandMsg.name = mcbOpen.isChecked() ? mtvSecurityType.getText().toString() + "：" + mDeviceInfo.getName() + "_" + getString(R.string.scene_open) : mtvSecurityType.getText().toString() + "：" + mDeviceInfo.getName() + "_" + getString(R.string.scene_close);
					Intent intent = new Intent();
					intent.putExtra("SceneCommandMsg", gson.toJson(mSceneCommandMsg));
					setResult(RESULT_OK, intent);
					finish();
				} else {
					showToast(getString(R.string.scene_no_set_toast));
				}
				break;
			case R.id.llOpen:
				if (mcbOpen.isChecked()) {
					mcbOpen.setChecked(false);
				} else {
					mcbOpen.setChecked(true);
				}
				break;
			case R.id.llClose:
				if (mcbClose.isChecked()) {
					mcbClose.setChecked(false);
				} else {
					mcbClose.setChecked(true);
				}
				break;
			default:
				break;
			}
		}
	};

	private void showToast(String str) {
		Toast.makeText(SceneSecurityPage.this, str, Toast.LENGTH_SHORT).show();
	}
}
