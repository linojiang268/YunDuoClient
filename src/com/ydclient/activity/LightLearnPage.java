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
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.ydclient.model.CommandInfo;
import com.ydclient.model.DeviceInfo;
import com.ydclient.model.DeviceUpdateModel;
import com.ydclient.type.TypeDevice;

import frame.ydclient.socket.MessageCallBack;
import frame.ydclient.socket.MyCon;

public class LightLearnPage extends Activity {
	private Button mbtSave;
	private EditText metName;
	private ImageButton mibtOpen, mibtClose;
	private ImageView mivIcon;
	private DeviceInfo mDeviceInfo;
	private HashMap<String, CommandInfo> mCommandMap;
	private ProgressDialog mProgressDialog;
	private int mLearnType;
	private String mLearnName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_learn_light);
		Intent intent = getIntent();
		if (null != intent && intent.hasExtra("DeviceInfo")) {
			mDeviceInfo = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
			// if (null != mDeviceInfo) {
			// this.findView();
			// return;
			// }
		}
		// showToast(getString(R.string.error_page));
		// finish();
		this.findView();
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
		this.mProgressDialog = new ProgressDialog(LightLearnPage.this);
		this.mbtSave = (Button) findViewById(R.id.btSave);
		this.metName = (EditText) findViewById(R.id.etName);
		this.mibtOpen = (ImageButton) findViewById(R.id.ibtOpen);
		this.mibtClose = (ImageButton) findViewById(R.id.ibtClose);
		this.mivIcon = (ImageView) findViewById(R.id.ivIcon);
		mibtOpen.setOnClickListener(clickListener);
		mibtClose.setOnClickListener(clickListener);
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
			case R.id.ibtOpen:
				if (null != mDeviceInfo && null != mDeviceInfo.type && TypeDevice.hasFeedBack(mDeviceInfo.getType().intValue())) {
					return;
				}
				mivIcon.setImageResource(R.drawable.ctrl_light_opened_icon);
				mProgressDialog.setMessage(getString(R.string.learn_waiting));
				mProgressDialog.show();
				mLearnType = 6103;
				mLearnName = "开灯";
				MyCon.con(LightLearnPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6001, "{}");
				break;
			case R.id.ibtClose:
				if (null != mDeviceInfo && null != mDeviceInfo.type && TypeDevice.hasFeedBack(mDeviceInfo.getType().intValue())) {
					return;
				}
				mivIcon.setImageResource(R.drawable.ctrl_light_closed_icon);
				mProgressDialog.setMessage(getString(R.string.learn_waiting));
				mProgressDialog.show();
				mLearnType = 6104;
				mLearnName = "关灯";
				MyCon.con(LightLearnPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6001, "{}");
				break;
			case R.id.btSave:
				if (metName.getText().toString().trim().length() <= 0) {
					showToast("请输入设备名称");
					return;
				}
				if (null == mDeviceInfo) {
					mDeviceInfo = new DeviceInfo(null, TypeDevice.LIGHT_UN_FB, "0", metName.getText().toString().trim(), "0");
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
				MyCon.con(LightLearnPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6002, new Gson().toJson(deviceUpdateModel));
				showToast(getString(R.string.save_waiting));
				break;
			default:
				break;
			}
		}
	};

	private MessageCallBack messageCallBack = new MessageCallBack(LightLearnPage.this) {
		@Override
		protected void onRead(int mark, int type, String content) {
			// showToast(type + "," + content);
			switch (type) {
			case 6001:
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
		Toast.makeText(LightLearnPage.this, str, Toast.LENGTH_SHORT).show();
	}
}
