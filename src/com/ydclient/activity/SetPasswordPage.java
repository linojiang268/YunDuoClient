package com.ydclient.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mobstat.StatService;

import frame.ydclient.socket.MessageCallBack;
import frame.ydclient.socket.MyCon;

/**
 * 修改密码
 * 
 * @author ouArea
 * 
 */
public class SetPasswordPage extends Activity {
	private EditText metOldPass, metNewPass, metNewPassAgain;
	private Button mbtSave;
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_set_password);
		this.findViews();
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

	private void findViews() {
		this.mProgressDialog = new ProgressDialog(SetPasswordPage.this);
		this.metOldPass = (EditText) findViewById(R.id.etOldPass);
		this.metNewPass = (EditText) findViewById(R.id.etNewPass);
		this.metNewPassAgain = (EditText) findViewById(R.id.etNewPassAgain);
		this.mbtSave = (Button) findViewById(R.id.btSave);
		mbtSave.setOnClickListener(clickListener);
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.btSave:
				if (metOldPass.getText().toString().trim().length() > 0 && metNewPass.getText().toString().trim().length() >= 6 && metNewPassAgain.getText().toString().trim().length() >= 6) {
					if (metNewPass.getText().toString().trim().equals(metNewPassAgain.getText().toString().trim())) {
						try {
							mProgressDialog.setMessage(getString(R.string.set_password_waiting));
							mProgressDialog.show();
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("oldpassword", metOldPass.getText().toString().trim());
							jsonObject.put("newpassword", metNewPassAgain.getText().toString().trim());
							MyCon.con(SetPasswordPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 5002, jsonObject.toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						showToast(getString(R.string.set_please_input_all_right));
					}
				} else {
					showToast(getString(R.string.set_please_input_all_right));
				}
				break;
			default:
				break;
			}
		}
	};
	private MessageCallBack messageCallBack = new MessageCallBack(SetPasswordPage.this) {
		@Override
		protected void onRead(int mark, int type, String content) {
			// showToast(type + "," + content);
			switch (type) {
			case 5002:
				try {
					if (0 == new JSONObject(content).getInt("result")) {
						showToast(new JSONObject(content).getString("msg"));
						finish();
					} else {
						showToast(new JSONObject(content).getString("msg"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
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
		Toast.makeText(SetPasswordPage.this, str, Toast.LENGTH_SHORT).show();
	}
}
