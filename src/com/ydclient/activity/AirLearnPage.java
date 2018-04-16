package com.ydclient.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ydclient.model.AirParamInfo;
import com.ydclient.model.AirUpdateModel;
import com.ydclient.model.DeviceInfo;
import com.ydclient.type.TypeDevice;

import frame.ydclient.socket.MessageCallBack;
import frame.ydclient.socket.MyCon;

public class AirLearnPage extends Activity {
	private Button mbtWindSpeed, mbtWindDirection, mbtSave, mbtLearn;
	private EditText metName;
	private ImageButton mibtPower, mibtTemperatureAdd, mibtTemperatureSub, mibtModel, mibtTimingAdd, mibtTimingSub;
	private TextView mtvTemperature, mtvTiming;
	private DeviceInfo mDeviceInfo;
	private HashMap<String, AirParamInfo> mAirParamInfoMap;
	private ProgressDialog mProgressDialog;
	// private int mLearnType;
	// private String mLearnName;
	private Gson gson;
	private AirParamInfo mAirParamInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_learn_air);
		Intent intent = getIntent();
		// if (null != intent && intent.hasExtra("DeviceInfo")) {
		// mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
		// }
		// this.findView();
		if (null != intent) {
			if (intent.hasExtra("control_device_mac") && intent.getStringExtra("control_device_mac").trim().length() > 0) {
				mDeviceInfo = new DeviceInfo(null, TypeDevice.AIR_UN_FB, "0", getString(R.string.main_air), "0");
				mDeviceInfo.setMark(intent.getStringExtra("control_device_mac").trim());
				if (null != mDeviceInfo) {
					this.findView();
					if (mDeviceInfo.content.length() > 3) {
						mAirParamInfo = gson.fromJson(mDeviceInfo.content, AirParamInfo.class);
					} else {
						mAirParamInfo = new AirParamInfo(null, mDeviceInfo._id, 1, 0, 0, 0, 25, 0l, null, null);
					}
					this.setView();
					return;
				}
				return;
			}
			if (null != intent && intent.hasExtra("DeviceInfo")) {
				mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
				if (null != mDeviceInfo) {
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
		}
		finish();
		showToast("页面错误");
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
		this.gson = new GsonBuilder().disableHtmlEscaping().create();
		this.mProgressDialog = new ProgressDialog(AirLearnPage.this);
		this.mbtSave = (Button) findViewById(R.id.btSave);
		this.mbtLearn = (Button) findViewById(R.id.btLearn);
		this.metName = (EditText) findViewById(R.id.etName);
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
		mibtPower.setOnClickListener(clickListener);
		mibtTemperatureAdd.setOnClickListener(clickListener);
		mibtTemperatureSub.setOnClickListener(clickListener);
		mibtModel.setOnClickListener(clickListener);
		mbtWindSpeed.setOnClickListener(clickListener);
		mbtWindDirection.setOnClickListener(clickListener);
		mibtTimingAdd.setOnClickListener(clickListener);
		mibtTimingSub.setOnClickListener(clickListener);
		mAirParamInfoMap = new HashMap<String, AirParamInfo>();
		mbtSave.setOnClickListener(clickListener);
		mbtLearn.setOnClickListener(clickListener);
		// mLearnType = -1;
		if (null != mDeviceInfo) {
			metName.setText(mDeviceInfo.getName());
		}
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
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btLearn:
				if (null != mDeviceInfo && null != mDeviceInfo.type && TypeDevice.hasFeedBack(mDeviceInfo.getType().intValue())) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.learn_waiting));
				mProgressDialog.show();
				MyCon.con(AirLearnPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6011, "{}");
				break;
			// case R.id.ibtPower:
			// if (null != mDeviceInfo && null != mDeviceInfo.type &&
			// TypeDevice.hasFeedBack(mDeviceInfo.getType().intValue())) {
			// return;
			// }
			// mProgressDialog.setMessage(getString(R.string.learn_waiting));
			// mProgressDialog.show();
			// mLearnType = 6410;
			// mLearnName = "电源";
			// MyCon.con().sendZlib(System.currentTimeMillis(), 6011, "{}");
			// break;
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
				if (metName.getText().toString().trim().length() <= 0) {
					showToast("请输入设备名称");
					return;
				}
				if (null == mDeviceInfo) {
					mDeviceInfo = new DeviceInfo(null, TypeDevice.AIR_UN_FB, "0", metName.getText().toString().trim(), "0");
				} else {
					mDeviceInfo.setName(metName.getText().toString().trim());
				}
				AirUpdateModel airUpdateModel = new AirUpdateModel();
				airUpdateModel.deviceInfo = mDeviceInfo;
				List<AirParamInfo> airParamInfoList = null;
				Set<Entry<String, AirParamInfo>> airParamInfoSet = mAirParamInfoMap.entrySet();
				for (Entry<String, AirParamInfo> entry : airParamInfoSet) {
					if (null == airParamInfoList) {
						airParamInfoList = new ArrayList<AirParamInfo>();
					}
					airParamInfoList.add(entry.getValue());
				}
				airUpdateModel.airParamInfos = airParamInfoList;
				MyCon.con(AirLearnPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6403, gson.toJson(airUpdateModel));
				showToast(getString(R.string.save_waiting));
				break;
			default:
				break;
			}
		}
	};

	private MessageCallBack messageCallBack = new MessageCallBack(AirLearnPage.this) {
		@Override
		protected void onRead(int mark, int type, String content) {
			// showToast(type + "," + content);
			switch (type) {
			case 6001:
			case 6011:
				try {
					JSONObject resJsonObject = new JSONObject(content);
					if (0 == resJsonObject.getInt("result")) {
						AirParamInfo airParamInfo = new AirParamInfo(null, mAirParamInfo.device_id, mAirParamInfo.power, mAirParamInfo.model, mAirParamInfo.wind_speed, mAirParamInfo.wind_direction, mAirParamInfo.temperature, mAirParamInfo.timing, resJsonObject.getString("command"), "空调状态命令");
						if (0 == airParamInfo.power) {
							mAirParamInfoMap.put("kai" + mAirParamInfo.temperature + mAirParamInfo.model + mAirParamInfo.wind_speed + mAirParamInfo.wind_direction + mAirParamInfo.timing, new AirParamInfo(null, null, mAirParamInfo.power, mAirParamInfo.model, mAirParamInfo.wind_speed, mAirParamInfo.wind_direction, mAirParamInfo.temperature, mAirParamInfo.timing, resJsonObject.getString("command"), "空调状态命令"));
						} else {
							mAirParamInfoMap.put("guan", new AirParamInfo(null, null, mAirParamInfo.power, mAirParamInfo.model, mAirParamInfo.wind_speed, mAirParamInfo.wind_direction, mAirParamInfo.temperature, mAirParamInfo.timing, resJsonObject.getString("command"), "空调状态命令"));
						}
						mProgressDialog.dismiss();
						showToast(resJsonObject.getString("msg"));
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
					showToast(getString(R.string.error_protocol));
				}
				break;
			case 6403:
				try {
					JSONObject resJsonObject = new JSONObject(content);
					if (0 == resJsonObject.getInt("result")) {
						mProgressDialog.dismiss();
						showToast(resJsonObject.getString("msg"));
						finish();
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
					showToast(getString(R.string.error_protocol));
				}
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
		Toast.makeText(AirLearnPage.this, str, Toast.LENGTH_SHORT).show();
	}
}
