package com.ydclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ydclient.model.AirParamInfo;
import com.ydclient.model.DeviceInfo;
import com.ydclient.model.SceneCommandMsg;

public class SceneAirPage extends Activity {
	private Gson gson;
	private DeviceInfo mDeviceInfo;
	private SceneCommandMsg mSceneCommandMsg;
	private TextView mtvName;

	private Button mbtSave;
	private ImageButton mibtPower, mibtTemperatureAdd, mibtTemperatureSub, mibtModel, mibtTimingAdd, mibtTimingSub;
	private Button mbtWindSpeed, mbtWindDirection;
	private TextView mtvTemperature, mtvTiming;
	private AirParamInfo mAirParamInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_scene_air);
		Intent intent = getIntent();
		if (null != intent && intent.hasExtra("DeviceInfo")) {
			mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
			if (null != mDeviceInfo) {
				// if (null != intent && intent.hasExtra("SceneCommandMsg")) {
				// mSceneCommandMsg = (SceneCommandMsg)
				// intent.getSerializableExtra("SceneCommandMsg");
				// }
				this.findView();
				if (mDeviceInfo.content.length() > 3) {
					mAirParamInfo = gson.fromJson(mDeviceInfo.content, AirParamInfo.class);
				} else {
					mAirParamInfo = new AirParamInfo(null, mDeviceInfo._id, 1, 0, 0, 0, 25, 0l, null, null);
				}
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
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		StatService.onPause(this);
		super.onPause();
	}

	private void findView() {
		this.gson = new GsonBuilder().disableHtmlEscaping().create();
		this.mibtPower = (ImageButton) findViewById(R.id.ibtPower);
		this.mibtTemperatureAdd = (ImageButton) findViewById(R.id.ibtTemperatureAdd);
		this.mibtTemperatureSub = (ImageButton) findViewById(R.id.ibtTemPeratureSub);
		this.mibtModel = (ImageButton) findViewById(R.id.ibtModel);
		this.mbtWindSpeed = (Button) findViewById(R.id.btWindSpeed);
		this.mbtWindDirection = (Button) findViewById(R.id.btWindDirection);
		this.mibtTimingAdd = (ImageButton) findViewById(R.id.ibtTimingAdd);
		this.mibtTimingSub = (ImageButton) findViewById(R.id.ibtTimingSub);
		this.mtvTemperature = (TextView) findViewById(R.id.tvTemperature);
		this.mtvTiming = (TextView) findViewById(R.id.tvTiming);
		this.mtvName = (TextView) findViewById(R.id.tvName);
		this.mbtSave = (Button) findViewById(R.id.btSave);
		mtvName.setText(mDeviceInfo.getName());
		mibtPower.setOnClickListener(clickListener);
		mibtTemperatureAdd.setOnClickListener(clickListener);
		mibtTemperatureSub.setOnClickListener(clickListener);
		mibtModel.setOnClickListener(clickListener);
		mbtWindSpeed.setOnClickListener(clickListener);
		mbtWindDirection.setOnClickListener(clickListener);
		mibtTimingAdd.setOnClickListener(clickListener);
		mibtTimingSub.setOnClickListener(clickListener);
		mbtSave.setOnClickListener(clickListener);
	}

	private void setView() {
		switch (mAirParamInfo.power) {
		case 0:
			mibtPower.setBackgroundResource(R.drawable.ctrl_power_unchecked);
			mibtTemperatureAdd.setClickable(true);
			mibtTemperatureSub.setClickable(true);
			mibtModel.setClickable(true);
			mbtWindSpeed.setClickable(true);
			mbtWindDirection.setClickable(true);
			mibtTimingAdd.setClickable(true);
			mibtTimingSub.setClickable(true);
			break;
		case 1:
			mibtPower.setBackgroundResource(R.drawable.ctrl_power_checked);
			mibtTemperatureAdd.setClickable(false);
			mibtTemperatureSub.setClickable(false);
			mibtModel.setClickable(false);
			mbtWindSpeed.setClickable(false);
			mbtWindDirection.setClickable(false);
			mibtTimingAdd.setClickable(false);
			mibtTimingSub.setClickable(false);
			break;
		default:
			break;
		}
		mtvTemperature.setText(mAirParamInfo.temperature + "c");
		switch (mAirParamInfo.model) {
		case 0:
			mibtModel.setBackgroundResource(R.drawable.bt_ctrl_health_style);
			break;
		case 1:
			mibtModel.setBackgroundResource(R.drawable.bt_ctrl_cold_style);
			break;
		case 2:
			mibtModel.setBackgroundResource(R.drawable.bt_ctrl_subtraction_style);
			break;
		case 3:
			mibtModel.setBackgroundResource(R.drawable.bt_ctrl_addition_style);
			break;
		case 4:
			mibtModel.setBackgroundResource(R.drawable.bt_ctrl_hot_style);
			break;
		default:
			break;
		}
		switch (mAirParamInfo.wind_speed) {
		case 0:
			// 小
			mbtWindSpeed.setText(R.string.air_wind_speed_small);
			break;
		case 1:
			mbtWindSpeed.setText(R.string.air_wind_speed_middle);
			break;
		case 2:
			mbtWindSpeed.setText(R.string.air_wind_speed_big);
			break;
		default:
			break;
		}
		switch (mAirParamInfo.wind_direction) {
		case 0:
			// 停
			mbtWindDirection.setText(R.string.air_wind_direction_stop);
			break;
		case 1:
			mbtWindDirection.setText(R.string.air_wind_direction_move);
			break;
		default:
			break;
		}
		mtvTiming.setText("0sec");
	}

	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.ibtPower:
				changePower();
				break;
			case R.id.ibtTemperatureAdd:
				changeTemperatureAdd();
				break;
			case R.id.ibtTemPeratureSub:
				changeTemperatureSub();
				break;
			case R.id.ibtModel:
				changeModel();
				break;
			case R.id.btWindSpeed:
				changeWindSpeed();
				break;
			case R.id.btWindDirection:
				changeWindDirection();
				break;
			case R.id.ibtTimingAdd:
				break;
			case R.id.ibtTimingSub:
				break;
			case R.id.btSave:
				mSceneCommandMsg = new SceneCommandMsg();
				mSceneCommandMsg.d_id = mDeviceInfo.get_id();
				mSceneCommandMsg.c_type = 6404;
				mSceneCommandMsg.c_msg = gson.toJson(mAirParamInfo);
				mSceneCommandMsg.name = getString(R.string.main_air) + "：" + mDeviceInfo.getName() + "_" + mAirParamInfo.getStatusStr(SceneAirPage.this);
				Intent intent = new Intent();
				intent.putExtra("SceneCommandMsg", gson.toJson(mSceneCommandMsg));
				setResult(RESULT_OK, intent);
				finish();
				break;
			default:
				break;
			}
		}
	};

	private void changePower() {
		if (mAirParamInfo.power >= 1) {
			mAirParamInfo.power = 0;
		} else {
			++mAirParamInfo.power;
		}
		setView();
	}

	private void changeTemperatureAdd() {
		if (mAirParamInfo.temperature >= 32) {
			mAirParamInfo.temperature = 32;
		} else {
			++mAirParamInfo.temperature;
		}
		setView();
	}

	private void changeTemperatureSub() {
		if (mAirParamInfo.temperature <= 16) {
			mAirParamInfo.temperature = 16;
		} else {
			--mAirParamInfo.temperature;
		}
		setView();
	}

	private void changeModel() {
		if (mAirParamInfo.model >= 4) {
			mAirParamInfo.model = 0;
		} else {
			++mAirParamInfo.model;
		}
		setView();
	}

	private void changeWindSpeed() {
		if (mAirParamInfo.wind_speed >= 2) {
			mAirParamInfo.wind_speed = 0;
		} else {
			++mAirParamInfo.wind_speed;
		}
		setView();
	}

	private void changeWindDirection() {
		if (mAirParamInfo.wind_direction >= 1) {
			mAirParamInfo.wind_direction = 0;
		} else {
			++mAirParamInfo.wind_direction;
		}
		setView();
	}

	private void showToast(String str) {
		Toast.makeText(SceneAirPage.this, str, Toast.LENGTH_SHORT).show();
	}
}
