package com.ydclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.ydclient.model.DeviceInfo;
import com.ydclient.model.SceneCommandMsg;

public class SceneTvPage extends Activity {
	private Gson gson;
	private DeviceInfo mDeviceInfo;
	private SceneCommandMsg mSceneCommandMsg;
	private TextView mtvName;

	private CheckBox mcbPower, mcbNoSound;
	private Button mbtSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_scene_tv);
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
		this.mcbPower = (CheckBox) findViewById(R.id.cbPower);
		this.mcbNoSound = (CheckBox) findViewById(R.id.cbNoSound);
		this.mtvName = (TextView) findViewById(R.id.tvName);
		this.mbtSave = (Button) findViewById(R.id.btSave);
		mtvName.setText(mDeviceInfo.getName());
		mbtSave.setOnClickListener(clickListener);
		mcbPower.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					mcbNoSound.setChecked(false);
				}
			}
		});
		mcbNoSound.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					mcbPower.setChecked(false);
				}
			}
		});
	}

	// 6303开关、6310静音
	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.btSave:
				if (mcbPower.isChecked() || mcbNoSound.isChecked()) {
					mSceneCommandMsg = new SceneCommandMsg();
					mSceneCommandMsg.d_id = mDeviceInfo.get_id();
					mSceneCommandMsg.c_type = mcbPower.isChecked() ? 6303 : 6310;
					mSceneCommandMsg.c_msg = "无";
					mSceneCommandMsg.name = mcbPower.isChecked() ? getString(R.string.main_tv) + "：" + mDeviceInfo.getName() + "_" + getString(R.string.scene_power) : getString(R.string.main_tv) + "：" + mDeviceInfo.getName() + "_" + getString(R.string.scene_no_sound);
					Intent intent = new Intent();
					intent.putExtra("SceneCommandMsg", gson.toJson(mSceneCommandMsg));
					setResult(RESULT_OK, intent);
					finish();
				} else {
					showToast(getString(R.string.scene_no_set_toast));
				}
				break;
			default:
				break;
			}
		}
	};

	private void showToast(String str) {
		Toast.makeText(SceneTvPage.this, str, Toast.LENGTH_SHORT).show();
	}
}
