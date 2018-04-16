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
import com.ydclient.adapter.GvSceneAirAdapter;
import com.ydclient.model.DeviceItemsModel;

import frame.ydclient.socket.MessageCallBack;
import frame.ydclient.socket.MyCon;

public class SceneAirItemsPage extends Activity implements CallStartActivityForResult {
	private GridView mgv;
	private GvSceneAirAdapter mGvSceneAirAdapter;
	private Gson gson;
	private FrameLayout mflFloorChoose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_scene_gv_air);
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
		MyCon.con(SceneAirItemsPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6402, "{}");
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
		this.mgv = (GridView) findViewById(R.id.gv);
		this.mflFloorChoose = (FrameLayout) findViewById(R.id.flFloorChoose);
		this.gson = new Gson();
		mGvSceneAirAdapter = new GvSceneAirAdapter(SceneAirItemsPage.this, this);
		mgv.setAdapter(mGvSceneAirAdapter);
	}

	private MessageCallBack messageCallBack = new MessageCallBack(SceneAirItemsPage.this) {
		@Override
		protected void onRead(int mark, int type, String content) {
			// showToast(type + "," + content);
			switch (type) {
			case 6402:
				try {
					JSONObject resJsonObject = new JSONObject(content);
					if (0 == resJsonObject.getInt("result")) {
						showToast(resJsonObject.getString("msg"));
						DeviceItemsModel deviceItemsModel = gson.fromJson(content, DeviceItemsModel.class);
						mGvSceneAirAdapter.refreshItems(deviceItemsModel.devices);
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
		Toast.makeText(SceneAirItemsPage.this, str, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 6500:
			if (RESULT_OK == resultCode) {
				setResult(resultCode, data);
				finish();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void start(Intent intent) {
		startActivityForResult(intent, 6500);
	};
}
