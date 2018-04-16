package com.ydclient.activity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.ydclient.adapter.GvSecurityAdapter;
import com.ydclient.db.IpCameraInfo;
import com.ydclient.db.IpCameraService;
import com.ydclient.model.DeviceItemsModel;
import com.ydclient.type.TypeDevice;

import frame.ydclient.socket.MessageCallBack;
import frame.ydclient.socket.MyCon;

public class SecurityItemsPage extends Activity {
	private GridView mgvSecurity;
	private GvSecurityAdapter mgvSecurityAdapter;
	private ImageButton mibtSet;
	private Gson gson;
	private Builder builder;
	private FrameLayout mflFloorChoose;
	private DeviceItemsModel mDeviceItemsModel;
	private ProgressDialog mProgressDialog;

	private IpCameraService mIpCameraService = new IpCameraService(SecurityItemsPage.this);
	private ArrayList<IpCameraInfo> mIpCameraInfos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_gv_security);
		this.findView();
		// 去掉楼层
		mflFloorChoose.setVisibility(View.GONE);
		// for (int i = 0; i < 20; i++) {
		// mgvLightAdapter.addItem("");
		// }
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyCon.addListener(messageCallBack);
		SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
		if (sharedPreferences.contains("DeviceItemsModel_Security")) {
			mDeviceItemsModel = gson.fromJson(sharedPreferences.getString("DeviceItemsModel_Security", null), DeviceItemsModel.class);
			mgvSecurityAdapter.refreshItems(mDeviceItemsModel.devices);
		}
		mIpCameraInfos = (ArrayList<IpCameraInfo>) mIpCameraService.getList();
		mgvSecurityAdapter.addItems(mIpCameraInfos);
		MyCon.con(SecurityItemsPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6602, "{}");
		if (null == mDeviceItemsModel) {
			mProgressDialog.show();
		}
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
		this.mProgressDialog = new ProgressDialog(SecurityItemsPage.this);
		mProgressDialog.setMessage(getString(R.string.get_waiting));
		this.mgvSecurity = (GridView) findViewById(R.id.gvSecurity);
		this.mibtSet = (ImageButton) findViewById(R.id.ibtSet);
		this.mflFloorChoose = (FrameLayout) findViewById(R.id.flFloorChoose);
		mibtSet.setOnClickListener(clickListener);
		this.gson = new Gson();
		mgvSecurityAdapter = new GvSecurityAdapter(SecurityItemsPage.this, mIpCameraService);
		mgvSecurity.setAdapter(mgvSecurityAdapter);
		builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.set_device));
		builder.setItems(new String[] { getString(R.string.add_device), getString(R.string.search_device), getString(R.string.ipcamera_add) }, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					startActivity(new Intent(SecurityItemsPage.this, SecurityLearnPage.class));
					break;
				case 1:
					MyCon.con(SecurityItemsPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6003, "{\"type\":" + TypeDevice.SECURITY_FB + "}");
					showToast(getString(R.string.search_device_waiting));
					break;
				case 2:
					// 添加IpCamera
					startActivity(new Intent(SecurityItemsPage.this, SecurityIpCameraEditPage.class));
					break;
				default:
					break;
				}
			}
		});
		// builder.setNegativeButton("确定", null);
		// builder.show();
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ibtSet:
				builder.show();
				break;
			default:
				break;
			}
		}
	};

	private MessageCallBack messageCallBack = new MessageCallBack(SecurityItemsPage.this) {
		@Override
		protected void onRead(int mark, int type, String content) {
			// showToast(type + "," + content);
			switch (type) {
			case 6003:
			case 6004:
				try {
					showToast(new JSONObject(content).getString("msg"));
					MyCon.con(SecurityItemsPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6602, "{}");
				} catch (JSONException e) {
					e.printStackTrace();
					showToast(getString(R.string.error_protocol));
				}
				break;
			case 6602:
				try {
					JSONObject resJsonObject = new JSONObject(content);
					if (0 == resJsonObject.getInt("result")) {
						showToast(resJsonObject.getString("msg"));
						mDeviceItemsModel = gson.fromJson(content, DeviceItemsModel.class);
						if (null != mDeviceItemsModel) {
							SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
							Editor editor = sharedPreferences.edit();
							editor.putString("DeviceItemsModel_Security", content);
							editor.commit();
						}
						mgvSecurityAdapter.refreshItems(mDeviceItemsModel.devices);
						// if (null == lightsModel.lights ||s
						// lightsModel.lights.size()
						// <= 0) {
						mflFloorChoose.setVisibility(View.GONE);
						// } else {
						// mflFloorChoose.setVisibility(View.VISIBLE);
						// }
					}
					mIpCameraInfos = (ArrayList<IpCameraInfo>) mIpCameraService.getList();
					mgvSecurityAdapter.addItems(mIpCameraInfos);
				} catch (JSONException e1) {
					e1.printStackTrace();
					showToast(getString(R.string.error_protocol));
				}
				mProgressDialog.dismiss();
				break;
			default:
				break;
			}
		}
	};

	private void showToast(String str) {
		Toast.makeText(SecurityItemsPage.this, str, Toast.LENGTH_SHORT).show();
	}
}
