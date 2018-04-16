package com.ydclient.activity;

import org.json.JSONArray;
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
 * 修改报警设置
 * 
 * @author ouArea
 * 
 */
public class SetSecurityUsersPage extends Activity {
	private EditText metUser1, metUser2, metUser3, metUser4, metUser5;
	private Button mbtSave;
	private JSONArray muserJsonArray;
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_set_security_users);
		this.findViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyCon.addListener(messageCallBack);
		if (null == muserJsonArray) {
			MyCon.con(SetSecurityUsersPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 5003, "{}");
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

	private void findViews() {
		this.mProgressDialog = new ProgressDialog(SetSecurityUsersPage.this);
		this.metUser1 = (EditText) findViewById(R.id.etUser1);
		this.metUser2 = (EditText) findViewById(R.id.etUser2);
		this.metUser3 = (EditText) findViewById(R.id.etUser3);
		this.metUser4 = (EditText) findViewById(R.id.etUser4);
		this.metUser5 = (EditText) findViewById(R.id.etUser5);
		this.mbtSave = (Button) findViewById(R.id.btSave);
		mbtSave.setOnClickListener(clickListener);
	}

	private void setViews() {
		if (null != muserJsonArray) {
			try {
				if (muserJsonArray.length() > 0) {
					metUser1.setText((String) muserJsonArray.get(0));
				}
				if (muserJsonArray.length() > 1) {
					metUser2.setText((String) muserJsonArray.get(1));
				}
				if (muserJsonArray.length() > 2) {
					metUser3.setText((String) muserJsonArray.get(2));
				}
				if (muserJsonArray.length() > 3) {
					metUser4.setText((String) muserJsonArray.get(3));
				}
				if (muserJsonArray.length() > 4) {
					metUser5.setText((String) muserJsonArray.get(4));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.btSave:
				if (null == muserJsonArray) {
					return;
				}
				JSONArray jsonArray = new JSONArray();
				if (metUser1.getText().toString().trim().length() > 0) {
					jsonArray.put(metUser1.getText().toString().trim());
				}
				if (metUser2.getText().toString().trim().length() > 0) {
					jsonArray.put(metUser2.getText().toString().trim());
				}
				if (metUser3.getText().toString().trim().length() > 0) {
					jsonArray.put(metUser3.getText().toString().trim());
				}
				if (metUser4.getText().toString().trim().length() > 0) {
					jsonArray.put(metUser4.getText().toString().trim());
				}
				if (metUser5.getText().toString().trim().length() > 0) {
					jsonArray.put(metUser5.getText().toString().trim());
				}
				MyCon.con(SetSecurityUsersPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 5004, "{\"security_phone_numbers\":" + jsonArray.toString() + "}");
				break;
			default:
				break;
			}
		}
	};
	private MessageCallBack messageCallBack = new MessageCallBack(SetSecurityUsersPage.this) {
		@Override
		protected void onRead(int mark, int type, String content) {
			// showToast(type + "," + content);
			switch (type) {
			case 5003:
				try {
					JSONObject jsonObject = new JSONObject(content);
					if (0 == jsonObject.getInt("result")) {
						showToast("获取成功");
						muserJsonArray = jsonObject.getJSONObject("msg").getJSONArray("security_phone_numbers");
						setViews();
					} else {
						showToast(jsonObject.getString("msg"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
					showToast(getString(R.string.error_protocol));
				}
				mProgressDialog.dismiss();
				break;
			case 5004:
				try {
					JSONObject jsonObject = new JSONObject(content);
					if (0 == jsonObject.getInt("result")) {
						showToast(jsonObject.getString("msg"));
						finish();
					} else {
						showToast(jsonObject.getString("msg"));
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
		Toast.makeText(SetSecurityUsersPage.this, str, Toast.LENGTH_SHORT).show();
	}
}
