package com.ydclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.ydclient.model.DeviceInfo;
import com.ydclient.model.SceneCommandMsg;

public class SceneBlindPage extends Activity {
	private Gson gson;
	private DeviceInfo mDeviceInfo;
	private SceneCommandMsg mSceneCommandMsg;
	private TextView mtvName;

	private LinearLayout mllOpen, mllClose;
	private CheckBox mcbOpen, mcbClose;
	private Button mbtSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_scene_blind);
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
	}

	private void findView() {
		this.gson = new Gson();
		this.mllOpen = (LinearLayout) findViewById(R.id.llOpen);
		this.mllClose = (LinearLayout) findViewById(R.id.llClose);
		this.mcbOpen = (CheckBox) findViewById(R.id.cbOpen);
		this.mcbClose = (CheckBox) findViewById(R.id.cbClose);
		this.mtvName = (TextView) findViewById(R.id.tvName);
		this.mbtSave = (Button) findViewById(R.id.btSave);
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
	}

	// 6203开、6204关
	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.btSave:
				if (mcbOpen.isChecked() || mcbClose.isChecked()) {
					mSceneCommandMsg = new SceneCommandMsg();
					mSceneCommandMsg.d_id = mDeviceInfo.get_id();
					mSceneCommandMsg.c_type = mcbOpen.isChecked() ? 6203 : 6204;
					mSceneCommandMsg.c_msg = "无";
					mSceneCommandMsg.name = mcbOpen.isChecked() ? getString(R.string.main_blind) + "：" + mDeviceInfo.getName() + "_" + getString(R.string.scene_open) : getString(R.string.main_blind) + "：" + mDeviceInfo.getName() + "_" + getString(R.string.scene_close);
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
		Toast.makeText(SceneBlindPage.this, str, Toast.LENGTH_SHORT).show();
	}
}
