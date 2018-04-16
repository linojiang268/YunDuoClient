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
import com.ydclient.model.CommandInfo;
import com.ydclient.model.DeviceInfo;
import com.ydclient.model.DeviceUpdateModel;
import com.ydclient.type.TypeDevice;

import frame.ydclient.socket.MessageCallBack;
import frame.ydclient.socket.MyCon;

public class TvLearnPage extends Activity {
	private Button mbtSave;
	private EditText metName;
	private ImageButton mibtPower, mibtNoSound, mibtAdd, mibtSub, mibtAddM, mibtSubM;
	private ImageButton mibtTop, mibtBottom, mibtLeft, mibtRight;
	private TextView mtvOk, mtvMenu, mtvBack;
	private DeviceInfo mDeviceInfo;
	private HashMap<String, CommandInfo> mCommandMap;
	private ProgressDialog mProgressDialog;
	private int mLearnType;
	private String mLearnName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_learn_tv);
		Intent intent = getIntent();
		// if (null != intent && intent.hasExtra("DeviceInfo")) {
		// mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
		// }
		// this.findView();
		if (null != intent) {
			if (intent.hasExtra("control_device_mac") && intent.getStringExtra("control_device_mac").trim().length() > 0) {
				mDeviceInfo = new DeviceInfo(null, TypeDevice.TV_UN_FB, "0", getString(R.string.main_tv), "0");
				mDeviceInfo.setMark(intent.getStringExtra("control_device_mac").trim());
				this.findView();
				return;
			}
			if (null != intent && intent.hasExtra("DeviceInfo")) {
				mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
				if (null != mDeviceInfo) {
					this.findView();
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
	};

	@Override
	protected void onStop() {
		MyCon.removeListener(messageCallBack);
		super.onStop();
	}

	private void findView() {
		this.mProgressDialog = new ProgressDialog(TvLearnPage.this);
		this.mbtSave = (Button) findViewById(R.id.btSave);
		this.metName = (EditText) findViewById(R.id.etName);
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
		mCommandMap = new HashMap<String, CommandInfo>();
		mbtSave.setOnClickListener(clickListener);
		mLearnType = -1;
		if (null != mDeviceInfo) {
			metName.setText(mDeviceInfo.getName());
		}
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ibtPower:
				if (null != mDeviceInfo && null != mDeviceInfo.type && TypeDevice.hasFeedBack(mDeviceInfo.getType().intValue())) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.learn_waiting));
				mProgressDialog.show();
				mLearnType = 6303;
				mLearnName = "电源";
				MyCon.con(TvLearnPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6011, "{}");
				break;
			case R.id.ibtNoSound:
				if (null != mDeviceInfo && null != mDeviceInfo.type && TypeDevice.hasFeedBack(mDeviceInfo.getType().intValue())) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.learn_waiting));
				mProgressDialog.show();
				mLearnType = 6310;
				mLearnName = "静音";
				MyCon.con(TvLearnPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6011, "{}");
				break;
			case R.id.ibtAdd:
				if (null != mDeviceInfo && null != mDeviceInfo.type && TypeDevice.hasFeedBack(mDeviceInfo.getType().intValue())) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.learn_waiting));
				mProgressDialog.show();
				mLearnType = 6306;
				mLearnName = "音量+";
				MyCon.con(TvLearnPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6011, "{}");
				break;
			case R.id.ibtSub:
				if (null != mDeviceInfo && null != mDeviceInfo.type && TypeDevice.hasFeedBack(mDeviceInfo.getType().intValue())) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.learn_waiting));
				mProgressDialog.show();
				mLearnType = 6307;
				mLearnName = "音量-";
				MyCon.con(TvLearnPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6011, "{}");
				break;
			case R.id.ibtAddM:
				if (null != mDeviceInfo && null != mDeviceInfo.type && TypeDevice.hasFeedBack(mDeviceInfo.getType().intValue())) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.learn_waiting));
				mProgressDialog.show();
				mLearnType = 6308;
				mLearnName = "节目+";
				MyCon.con(TvLearnPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6011, "{}");
				break;
			case R.id.ibtSubM:
				if (null != mDeviceInfo && null != mDeviceInfo.type && TypeDevice.hasFeedBack(mDeviceInfo.getType().intValue())) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.learn_waiting));
				mProgressDialog.show();
				mLearnType = 6309;
				mLearnName = "节目-";
				MyCon.con(TvLearnPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6011, "{}");
				break;
			case R.id.ibtTop:
				if (null != mDeviceInfo && null != mDeviceInfo.type && TypeDevice.hasFeedBack(mDeviceInfo.getType().intValue())) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.learn_waiting));
				mProgressDialog.show();
				mLearnType = 6311;
				mLearnName = "上";
				MyCon.con(TvLearnPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6011, "{}");
				break;
			case R.id.ibtBottom:
				if (null != mDeviceInfo && null != mDeviceInfo.type && TypeDevice.hasFeedBack(mDeviceInfo.getType().intValue())) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.learn_waiting));
				mProgressDialog.show();
				mLearnType = 6312;
				mLearnName = "下";
				MyCon.con(TvLearnPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6011, "{}");
				break;
			case R.id.ibtLeft:
				if (null != mDeviceInfo && null != mDeviceInfo.type && TypeDevice.hasFeedBack(mDeviceInfo.getType().intValue())) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.learn_waiting));
				mProgressDialog.show();
				mLearnType = 6313;
				mLearnName = "左";
				MyCon.con(TvLearnPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6011, "{}");
				break;
			case R.id.ibtRight:
				if (null != mDeviceInfo && null != mDeviceInfo.type && TypeDevice.hasFeedBack(mDeviceInfo.getType().intValue())) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.learn_waiting));
				mProgressDialog.show();
				mLearnType = 6314;
				mLearnName = "右";
				MyCon.con(TvLearnPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6011, "{}");
				break;
			case R.id.tvOk:
				if (null != mDeviceInfo && null != mDeviceInfo.type && TypeDevice.hasFeedBack(mDeviceInfo.getType().intValue())) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.learn_waiting));
				mProgressDialog.show();
				mLearnType = 6315;
				mLearnName = "OK";
				MyCon.con(TvLearnPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6011, "{}");
				break;
			case R.id.tvMenu:
				if (null != mDeviceInfo && null != mDeviceInfo.type && TypeDevice.hasFeedBack(mDeviceInfo.getType().intValue())) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.learn_waiting));
				mProgressDialog.show();
				mLearnType = 6316;
				mLearnName = "菜单";
				MyCon.con(TvLearnPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6011, "{}");
				break;
			case R.id.tvBack:
				if (null != mDeviceInfo && null != mDeviceInfo.type && TypeDevice.hasFeedBack(mDeviceInfo.getType().intValue())) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.learn_waiting));
				mProgressDialog.show();
				mLearnType = 6317;
				mLearnName = "返回";
				MyCon.con(TvLearnPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6011, "{}");
				break;
			case R.id.btSave:
				if (metName.getText().toString().trim().length() <= 0) {
					showToast("请输入设备名称");
					return;
				}
				if (null == mDeviceInfo) {
					mDeviceInfo = new DeviceInfo(null, TypeDevice.TV_UN_FB, "0", metName.getText().toString().trim(), "0");
				} else {
					mDeviceInfo.setName(metName.getText().toString().trim());
				}
				DeviceUpdateModel deviceUpdateModel = new DeviceUpdateModel();
				deviceUpdateModel.deviceInfo = mDeviceInfo;
				List<CommandInfo> commandInfoList = null;
				Set<Entry<String, CommandInfo>> commandSet = mCommandMap.entrySet();
				for (Entry<String, CommandInfo> entry : commandSet) {
					if (null == commandInfoList) {
						commandInfoList = new ArrayList<CommandInfo>();
					}
					commandInfoList.add(entry.getValue());
				}
				deviceUpdateModel.commandInfos = commandInfoList;
				MyCon.con(TvLearnPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6002, new Gson().toJson(deviceUpdateModel));
				showToast(getString(R.string.save_waiting));
				break;
			default:
				break;
			}
		}
	};
	private MessageCallBack messageCallBack = new MessageCallBack(TvLearnPage.this) {
		@Override
		protected void onRead(int mark, int type, String content) {
			// showToast(type + "," + content);
			switch (type) {
			case 6001:
			case 6011:
				try {
					JSONObject resJsonObject = new JSONObject(content);
					if (0 == resJsonObject.getInt("result") && mLearnType > 0) {
						mCommandMap.put(String.valueOf(mLearnType), new CommandInfo(null, mLearnType, resJsonObject.getString("command"), mLearnName));
						mProgressDialog.dismiss();
						showToast(resJsonObject.getString("msg"));
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
					showToast(getString(R.string.error_protocol));
				}
				break;
			case 6002:
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

	private void showToast(String str) {
		Toast.makeText(TvLearnPage.this, str, Toast.LENGTH_SHORT).show();
	}
}
