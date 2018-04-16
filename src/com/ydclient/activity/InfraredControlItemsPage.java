package com.ydclient.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
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
import com.google.gson.GsonBuilder;
import com.ydclient.adapter.GvInfraredControlAdapter;
import com.ydclient.model.DeviceItemsModel;
import com.ydclient.type.TypeDevice;

import frame.ydclient.socket.MessageCallBack;
import frame.ydclient.socket.MyCon;

public class InfraredControlItemsPage extends Activity {
	public final static int TYPE_TV = 10001;
	public final static int TYPE_AIR = 10002;
	private GridView mgv;
	private GvInfraredControlAdapter mGvInfraredControlAdapter;
	private ImageButton mibtSet;
	private Gson gson;
	private Builder builder;
	private FrameLayout mflFloorChoose;
	private int mType = -1;
	private DeviceItemsModel mDeviceItemsModel;
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_gv_infrared_control);
		Intent intent = getIntent();
		if (null != intent && intent.hasExtra("type")) {
			mType = intent.getIntExtra("type", -1);
			if (mType > 0) {
				this.findView();
				// 去掉楼层
				mflFloorChoose.setVisibility(View.GONE);
				// for (int i = 0; i < 20; i++) {
				// mGvTvAdapter.addItem("");
				// }
				return;
			}
		}
		showToast("页面错误");
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyCon.addListener(messageCallBack);
		SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
		if (sharedPreferences.contains("DeviceItemsModel_InfraredControl")) {
			mDeviceItemsModel = gson.fromJson(sharedPreferences.getString("DeviceItemsModel_InfraredControl", null), DeviceItemsModel.class);
			mGvInfraredControlAdapter.refreshItems(mDeviceItemsModel.devices);
		}
		MyCon.con(InfraredControlItemsPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 603402, "{}");
		if (null == mDeviceItemsModel) {
			mProgressDialog.show();
		}
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
		this.mProgressDialog = new ProgressDialog(InfraredControlItemsPage.this);
		mProgressDialog.setMessage(getString(R.string.get_waiting));
		this.mgv = (GridView) findViewById(R.id.gv);
		this.mibtSet = (ImageButton) findViewById(R.id.ibtSet);
		this.mflFloorChoose = (FrameLayout) findViewById(R.id.flFloorChoose);
		mibtSet.setOnClickListener(clickListener);
		this.gson = new GsonBuilder().disableHtmlEscaping().create();
		mGvInfraredControlAdapter = new GvInfraredControlAdapter(InfraredControlItemsPage.this);
		mGvInfraredControlAdapter.setType(mType);
		mgv.setAdapter(mGvInfraredControlAdapter);
		builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.set_device));
		builder.setItems(new String[] { getString(R.string.search_device) }, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					MyCon.con(InfraredControlItemsPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6003, "{\"type\":" + TypeDevice.INFRARED_FB + "}");
					showToast(getString(R.string.search_device_waiting));
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
				// startActivity(new Intent(TvItemsPage.this,
				// LightLearnPage.class));
				break;
			default:
				break;
			}
		}
	};

	private MessageCallBack messageCallBack = new MessageCallBack(InfraredControlItemsPage.this) {
		@Override
		protected void onRead(int mark, int type, String content) {
			// showToast(type + "," + content);
			switch (type) {
			case 6003:
			case 6004:
				try {
					showToast(new JSONObject(content).getString("msg"));
					MyCon.con(InfraredControlItemsPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 603402, "{}");
				} catch (JSONException e) {
					e.printStackTrace();
					showToast(getString(R.string.error_protocol));
				}
				break;
			case 603402:
				try {
					JSONObject resJsonObject = new JSONObject(content);
					if (0 == resJsonObject.getInt("result")) {
						showToast(resJsonObject.getString("msg"));
						mDeviceItemsModel = gson.fromJson(content, DeviceItemsModel.class);
						if (null != mDeviceItemsModel) {
							SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
							Editor editor = sharedPreferences.edit();
							editor.putString("DeviceItemsModel_InfraredControl", content);
							editor.commit();
						}
						mGvInfraredControlAdapter.refreshItems(mDeviceItemsModel.devices);
						// if (null == lightsModel.lights ||
						// lightsModel.lights.size()
						// <= 0) {
						mflFloorChoose.setVisibility(View.GONE);
						// } else {
						// mflFloorChoose.setVisibility(View.VISIBLE);
						// }
					}
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
		Toast.makeText(InfraredControlItemsPage.this, str, Toast.LENGTH_SHORT).show();
	}
}
