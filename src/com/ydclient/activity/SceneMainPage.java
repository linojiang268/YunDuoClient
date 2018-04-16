package com.ydclient.activity;

import com.baidu.mobstat.StatService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class SceneMainPage extends Activity {
	private ImageButton mibtTv, mibtBlind, mibtAir, mibtLight, mibtSecurity;

	// private ImageButton mibtScene;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_scene_main);
		this.findView();
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
		this.mibtTv = (ImageButton) findViewById(R.id.ibtTv);
		this.mibtBlind = (ImageButton) findViewById(R.id.ibtBlind);
		// this.mibtScene = (ImageButton) findViewById(R.id.ibtScene);
		this.mibtAir = (ImageButton) findViewById(R.id.ibtAir);
		this.mibtLight = (ImageButton) findViewById(R.id.ibtLight);
		this.mibtSecurity = (ImageButton) findViewById(R.id.ibtSecurity);
		mibtTv.setOnClickListener(clickListener);
		mibtBlind.setOnClickListener(clickListener);
		// mibtScene.setOnClickListener(clickListener);
		mibtAir.setOnClickListener(clickListener);
		mibtLight.setOnClickListener(clickListener);
		mibtSecurity.setOnClickListener(clickListener);
	}

	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.ibtTv:
				startActivityForResult(new Intent(SceneMainPage.this, SceneTvItemsPage.class), 6500);
				break;
			case R.id.ibtBlind:
				startActivityForResult(new Intent(SceneMainPage.this, SceneBlindItemsPage.class), 6500);
				break;
			// case R.id.ibtScene:
			// Toast.makeText(SceneMainPage.this, "场景",
			// Toast.LENGTH_SHORT).show();
			// break;
			case R.id.ibtAir:
				startActivityForResult(new Intent(SceneMainPage.this, SceneAirItemsPage.class), 6500);
				break;
			case R.id.ibtLight:
				startActivityForResult(new Intent(SceneMainPage.this, SceneLightItemsPage.class), 6500);
				break;
			case R.id.ibtSecurity:
				startActivityForResult(new Intent(SceneMainPage.this, SceneSecurityItemsPage.class), 6500);
				break;
			default:
				break;
			}
		}
	};

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
}
