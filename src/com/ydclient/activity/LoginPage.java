package com.ydclient.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.ydclient.data.MyNetData;
import com.ydclient.view.MyProgressDialog;
import com.ydclient.xmpp.MyXMPP;

import frame.ydclient.socket.MessageCallBack;
import frame.ydclient.socket.MyCon;

/**
 * 登录页面
 * 
 * @author ouArea
 * 
 */
public class LoginPage extends Activity {
	private EditText metIp, metPort, metPass;
	private CheckBox mcbSavePassword, mcbAutoLogin;
	private Button mbtLogin, mbtRemote;
	private MyProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_login);
		this.findView();
		MyCon.addListener(messageCallBack);
		// ------------------
		SharedPreferences sharedPreference = getSharedPreferences("user", MODE_PRIVATE);
		if (null != sharedPreference) {
			if (sharedPreference.contains("ip")) {
				metIp.setText(sharedPreference.getString("ip", ""));
			}
			if (sharedPreference.contains("port")) {
				metPort.setText(String.valueOf(sharedPreference.getInt("port", 6666)));
			}
			if (sharedPreference.contains("password")) {
				metPass.setText(sharedPreference.getString("password", "123456"));
			}
			if (sharedPreference.getBoolean("savePassword", true)) {
				mcbSavePassword.setChecked(true);
			} else {
				mcbSavePassword.setChecked(false);
			}
			if (sharedPreference.getBoolean("autoLogin", true)) {
				mcbAutoLogin.setChecked(true);
			} else {
				mcbAutoLogin.setChecked(false);
			}
			if (mcbSavePassword.isChecked() && mcbAutoLogin.isChecked() && sharedPreference.getBoolean("canLogin", false)) {
				login();
			}
		}
	}

	@Override
	public void onBackPressed() {
		Main.noNeedLogin = true;
		super.onBackPressed();
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
		this.mbtLogin = (Button) findViewById(R.id.btLogin);
		this.mbtRemote = (Button) findViewById(R.id.btRemote);
		this.metIp = (EditText) findViewById(R.id.etIp);
		this.metPort = (EditText) findViewById(R.id.etPort);
		this.metPass = (EditText) findViewById(R.id.etPass);
		this.mcbSavePassword = (CheckBox) findViewById(R.id.cbSavePassword);
		this.mcbAutoLogin = (CheckBox) findViewById(R.id.cbAutoLogin);
		mcbSavePassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!isChecked) {
					mcbAutoLogin.setChecked(false);
				}
			}
		});
		mcbAutoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					mcbSavePassword.setChecked(true);
				}
			}
		});
		// this.metAddress = (EditText) findViewById(R.id.etAddress);
		// this.metType = (EditText) DfindViewById(R.id.etType);
		// this.metMsg = (EditText) findViewById(R.id.etMsg);
		// this.mbtCon = (Button) findViewById(R.id.btCon);
		// this.mbtSend = (Button) findViewById(R.id.btSend);
		// mbtCon.setOnClickListener(clickListener);
		// mbtSend.setOnClickListener(clickListener);
		mbtLogin.setOnClickListener(clickListener);
		mbtRemote.setOnClickListener(clickListener);
		mbtRemote.setOnLongClickListener(longClickListener);
		metIp.setOnFocusChangeListener(focusChangeListener);
		metPass.setOnFocusChangeListener(focusChangeListener);
		metPort.setOnFocusChangeListener(focusChangeListener);
		// 测试
		// metIp.setText("yunduoserver.eicp.net");
		// metIp.setText("192.168.2.103");
		// metPort.setText("6666");
		// addressFlag = true;
		metPass.setOnClickListener(clickListener);
		this.mProgressDialog = new MyProgressDialog(LoginPage.this) {
			@Override
			protected void exit() {
				if (!Main.noNeedLogin) {
					stopService(new Intent(LoginPage.this, YDClientService.class));
				}
			}
		};
	}

	private OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			if (arg1) {
				switch (arg0.getId()) {
				case R.id.etIp:
					CharSequence textip = metIp.getText();
					if (textip instanceof Spannable) {
						Spannable spanText = (Spannable) textip;
						Selection.setSelection(spanText, textip.length());
					}
					break;
				case R.id.etPass:
					CharSequence textpass = metPass.getText();
					if (textpass instanceof Spannable) {
						Spannable spanText = (Spannable) textpass;
						Selection.setSelection(spanText, textpass.length());
					}
					break;
				case R.id.etPort:
					CharSequence textport = metPort.getText();
					if (textport instanceof Spannable) {
						Spannable spanText = (Spannable) textport;
						Selection.setSelection(spanText, textport.length());
					}
					break;
				default:
					break;
				}
			}
		}
	};
	// private boolean addressFlag;
	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.btLogin:
				MyNetData.setModel(LoginPage.this, true);
				login();
				break;
			case R.id.btRemote:
				final EditText editText = new EditText(LoginPage.this);
				if (MyXMPP.isSet(LoginPage.this)) {
					editText.setText(MyXMPP.getAdminName(LoginPage.this));
				}
				Builder builder = new AlertDialog.Builder(LoginPage.this).setTitle("请输入远程访问key").setIcon(android.R.drawable.ic_dialog_info).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (editText.getText().toString().trim().length() >= 6) {
							MyXMPP.setValue(LoginPage.this, MyNetData.yunduoGroupAddress, editText.getText().toString().trim(), MyNetData.yunduoGroupConference);
							mProgressDialog.setMessage(getString(R.string.logining_waiting));
							mProgressDialog.show();
							MyNetData.setModel(LoginPage.this, false);
							startService(new Intent(LoginPage.this, YDClientService.class));
						}
					}
				}).setNegativeButton("取消", null);
				builder.show();
				break;
			// case R.id.btCon:
			// MyNetData.loginIp = metAddress.getText().toString();
			// startService(new Intent(MainActivity.this,
			// DCClientService.class));
			// break;
			// case R.id.btSend:
			// MyCon.con().sendZlib(System.currentTimeMillis(),
			// Integer.parseInt(metType.getText().toString()),
			// metMsg.getText().toString());
			// break;
			case R.id.etPass:
				// if (addressFlag) {
				// metIp.setText("198.100.113.18");
				// metPort.setText("7776");
				// addressFlag = false;
				// } else {
				// metIp.setText("yunduoserver.eicp.net");
				// metPort.setText("6666");
				// addressFlag = true;
				// }
				break;
			default:
				break;
			}
		}
	};
	private OnLongClickListener longClickListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.btRemote:
				final EditText editText = new EditText(LoginPage.this);
				if (MyXMPP.isSet(LoginPage.this)) {
					editText.setText(MyXMPP.getAdminName(LoginPage.this));
				}
				Builder builder = new AlertDialog.Builder(LoginPage.this).setTitle("请输入待测试的远程访问key").setIcon(android.R.drawable.ic_dialog_info).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (editText.getText().toString().trim().length() >= 6) {
							MyXMPP.setValue(LoginPage.this, MyNetData.yunduoGroupAddress, editText.getText().toString().trim(), MyNetData.yunduoGroupConference);
							startActivity(new Intent(LoginPage.this, XMPPTestPage.class));
						}
					}
				}).setNegativeButton("取消", null);
				builder.show();
				break;
			default:
				break;
			}
			return false;
		}
	};

	private void login() {
		if (metIp.getText().toString().trim().length() <= 0) {
			showToast(getString(R.string.login_address_hint));
			return;
		}
		if (metPass.getText().toString().trim().length() <= 0) {
			showToast(getString(R.string.login_password_hint));
			return;
		}
		if (metPort.getText().toString().trim().length() <= 0) {
			showToast(getString(R.string.login_port_hint));
			return;
		}
		mProgressDialog.setMessage(getString(R.string.logining_waiting));
		mProgressDialog.show();
		// startActivity(new Intent(LoginPage.this, Main.class));
		// finish();
		SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		// ------------------------------------------
		// MyNetData.loginIp = metIp.getText().toString().trim();
		// MyNetData.loginPort = Integer.parseInt(metPort.getText().toString());
		// MyNetData.loginPassword = metPass.getText().toString().trim();
		editor.putString("ip", metIp.getText().toString().trim());
		editor.putInt("port", Integer.parseInt(metPort.getText().toString()));
		editor.putString("password", metPass.getText().toString().trim());
		// ------------------------------------------
		editor.putBoolean("savePassword", mcbSavePassword.isChecked());
		editor.putBoolean("autoLogin", mcbAutoLogin.isChecked());
		editor.putBoolean("canLogin", false);
		editor.commit();
		startService(new Intent(LoginPage.this, YDClientService.class));
	}

	@Override
	protected void onDestroy() {
		MyCon.removeListener(messageCallBack);
		super.onDestroy();
	}

	private MessageCallBack messageCallBack = new MessageCallBack(LoginPage.this) {
		@Override
		protected void onRead(int mark, int type, String content) {
			// showToast(type + "," + content);
			switch (type) {
			case 1:
				mProgressDialog.dismiss();
				showToast(content);
				stopService(new Intent(LoginPage.this, YDClientService.class));
				break;
			case 0:
				// try {
				// showToast(new JSONObject(content).getString("msg"));
				// startActivity(new Intent(LoginPage.this, Main.class));
				// finish();
				// } catch (JSONException e) {
				// e.printStackTrace();
				// showToast(getString(R.string.error_protocol));
				// }
				Main.noNeedLogin = true;
				mProgressDialog.dismiss();
				showToast("登录成功");
				startActivity(new Intent(LoginPage.this, Main.class));
				finish();
				break;
			default:
				break;
			}
		}

	};

	private void showToast(String str) {
		Toast.makeText(LoginPage.this, str, Toast.LENGTH_SHORT).show();
	}
}
