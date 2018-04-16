package com.ydclient.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.ydclient.adapter.GvSceneBlindAdapter;
import com.ydclient.model.DeviceItemsModel;

import frame.ydclient.socket.MessageCallBack;
import frame.ydclient.socket.MyCon;

public class SceneBlindItemsPage extends Activity implements CallStartActivityForResult {
	private GridView mgvBlind;
	private GvSceneBlindAdapter mGvSceneBlindAdapter;
	private Gson gson;
	private FrameLayout mflFloorChoose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_scene_gv_blind);
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
		MyCon.con(SceneBlindItemsPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6202, "{}");
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
		this.mgvBlind = (GridView) findViewById(R.id.gvBlind);
		this.mflFloorChoose = (FrameLayout) findViewById(R.id.flFloorChoose);
		this.gson = new Gson();
		mGvSceneBlindAdapter = new GvSceneBlindAdapter(SceneBlindItemsPage.this, this);
		mgvBlind.setAdapter(mGvSceneBlindAdapter);
	}

	private MessageCallBack messageCallBack = new MessageCallBack(SceneBlindItemsPage.this) {
		@Override
		protected void onRead(int mark, int type, String content) {
			// showToast(type + "," + content);
			switch (type) {
			case 6202:
				try {
					JSONObject resJsonObject = new JSONObject(content);
					if (0 == resJsonObject.getInt("result")) {
						showToast(resJsonObject.getString("msg"));
						DeviceItemsModel deviceItemsModel = gson.fromJson(content, DeviceItemsModel.class);
						mGvSceneBlindAdapter.refreshItems(deviceItemsModel.devices);
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
				break;
			default:
				break;
			}
		}
	};

	private void showToast(String str) {
		Toast.makeText(SceneBlindItemsPage.this, str, Toast.LENGTH_SHORT).show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			setResult(RESULT_OK, data);
			finish();
		}
	}

	@Override
	public void start(Intent intent) {
		startActivityForResult(intent, 6500);
	};
}
