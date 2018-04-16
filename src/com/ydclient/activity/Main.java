package com.ydclient.activity;

import object.p2pipcam.nativecaller.NativeCaller;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.ydclient.data.MyNetData;
import com.ydclient.xmpp.MyXMPP;

import frame.ydclient.socket.MessageCallBack;
import frame.ydclient.socket.MyCon;

/**
 * 主界面
 * 
 * @author ouArea
 * 
 */
public class Main extends Activity {
	public static boolean noNeedLogin = false;
	private ImageButton mibtTv, mibtBlind, mibtScene, mibtAir, mibtLight, mibtSecurity;

	// private int i;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_main);
		// i = -1;
		noNeedLogin = false;
		this.findView();
		// 启动摄像头服务===============================================
		Intent intent = new Intent();
		intent.setClass(Main.this, BridgeService.class);
		startService(intent);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					NativeCaller.PPPPInitial("EBGAEOBOKHJMHMJMENGKFIEEHBMDHNNEGNEBBCCCBIIHLHLOCIACCJOFHHLLJEKHBFMPLMCHPHMHAGDHJNNHIFBAMC");
					NativeCaller.PPPPNetworkDetect();
					// NativeCaller.PPPPInitial("EBGAEOBOKHJMHMJMENGKFIEEHBMDHNNEGNEBBCCCBIIHLHLOCIACCJOFHHLLJEKHBFMPLMCHPHMHAGDHJNNHIFBAMC");
					// long lStartTime = new Date().getTime();
					// int nRes = NativeCaller.PPPPNetworkDetect();
					// long lEndTime = new Date().getTime();
					// if (lEndTime - lStartTime <= 1000) {
					// Thread.sleep(3000);
					// }
					// Message msg = new Message();
					// mHandler.sendMessage(msg);
				} catch (Exception e) {
				}
			}
		}).start();
		// 启动摄像头服务===============================================
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyCon.addListener(messageCallBack);
		// if (i < 0) {
		// startActivity(new Intent(Main.this, LoginPage.class));
		// i = 0;
		// } else {
		// if (!YDClientService.hasStart || YDClientService.isLogining) {
		// showToast("请先登录");
		// finish();
		// }
		// }
		if (noNeedLogin) {
			if (MyNetData.isModel(Main.this)) {
				if (!YDClientService.hasStart || YDClientService.isLogining) {
					showToast("请先登录");
					finish();
				}
			} else {
				if (!MyXMPP.isConnect()) {
					showToast("请先登录");
					finish();
				}
			}
		} else {
			startActivity(new Intent(Main.this, LoginPage.class));
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

	@Override
	protected void onDestroy() {
		stopService(new Intent(Main.this, YDClientService.class));
		// 停止摄像头服务
		stopService(new Intent(Main.this, BridgeService.class));
		super.onDestroy();
	}

	private void findView() {
		this.mibtTv = (ImageButton) findViewById(R.id.ibtTv);
		this.mibtBlind = (ImageButton) findViewById(R.id.ibtBlind);
		this.mibtScene = (ImageButton) findViewById(R.id.ibtScene);
		this.mibtAir = (ImageButton) findViewById(R.id.ibtAir);
		this.mibtLight = (ImageButton) findViewById(R.id.ibtLight);
		this.mibtSecurity = (ImageButton) findViewById(R.id.ibtSecurity);
		mibtTv.setOnClickListener(clickListener);
		mibtBlind.setOnClickListener(clickListener);
		mibtScene.setOnClickListener(clickListener);
		mibtAir.setOnClickListener(clickListener);
		mibtLight.setOnClickListener(clickListener);
		mibtSecurity.setOnClickListener(clickListener);
	}

	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.ibtTv:
				startActivity(new Intent(Main.this, TvItemsPage.class));
				break;
			case R.id.ibtBlind:
				startActivity(new Intent(Main.this, BlindItemsPage.class));
				break;
			case R.id.ibtScene:
				// Toast.makeText(Main.this, "场景", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(Main.this, ScenePage.class));
				break;
			case R.id.ibtAir:
				startActivity(new Intent(Main.this, AirItemsPage.class));
				break;
			case R.id.ibtLight:
				startActivity(new Intent(Main.this, LightItemsPage.class));
				break;
			case R.id.ibtSecurity:
				// Toast.makeText(Main.this, "安防", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(Main.this, SecurityItemsPage.class));
				break;
			default:
				break;
			}
		}
	};

	private MessageCallBack messageCallBack = new MessageCallBack(Main.this) {

		@Override
		protected void onRead(int mark, int type, String content) {
			// showToast(type + "," + content);
		}

	};

	private void showToast(String str) {
		Toast.makeText(Main.this, str, Toast.LENGTH_SHORT).show();
	}

	// --------按两次退出程序-------
	private long mExitTime;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				// Object mHelperUtils;
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// -----------------------------菜单-----------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, 1, 1, "关于");
		menu.add(Menu.NONE, 2, 2, "报警通知");
		menu.add(Menu.NONE, 3, 3, "下载最新版本");
		menu.add(Menu.NONE, 4, 4, "注销");
		menu.add(Menu.NONE, 5, 5, "设置密码");
		menu.add(Menu.NONE, 6, 6, "设置远程参数");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 1:
			AlertDialog.Builder aboutDialog = new Builder(Main.this);
			aboutDialog.setMessage("云朵智能家居管理系统 1.0.1\nauthor:云朵科技\n联系方式:13693434985\n谢谢支持！");
			aboutDialog.setTitle("关于");
			aboutDialog.create().show();
			break;
		case 2:
			// 报警通知
			startActivity(new Intent(Main.this, SetSecurityUsersPage.class));
			break;
		case 3:
			// 下载最新版本
			Uri uri = Uri.parse("http://yunduoserver.ouarea.cc/yunduo/client.apk");
			Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(downloadIntent);
			break;
		case 4:
			// 注销
			// finish();
			stopService(new Intent(Main.this, YDClientService.class));
			noNeedLogin = false;
			SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
			Editor editor = sharedPreferences.edit();
			editor.putBoolean("canLogin", false);
			editor.commit();
			startActivity(new Intent(Main.this, LoginPage.class));
			break;
		case 5:
			// 设置密码
			startActivity(new Intent(Main.this, SetPasswordPage.class));
			break;
		case 6:
			// 设置远程参数
			startActivity(new Intent(Main.this, SetGroupInfoPage.class));
			break;
		}
		return true;
	}
}
