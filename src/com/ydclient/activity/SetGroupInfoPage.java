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
 * 设置远程参数
 * 
 * @author ouArea
 * 
 */
public class SetGroupInfoPage extends Activity {
	private EditText metGroupName, metGroupPass, metGroupPassAgain;
	private Button mbtSave;
	private ProgressDialog mProgressDialog;
	private String groupName, groupPass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_set_group_info);
		this.findViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyCon.addListener(messageCallBack);
		if (null == groupName || null == groupPass) {
			MyCon.con(SetGroupInfoPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 5005, "{}");
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
		this.mProgressDialog = new ProgressDialog(SetGroupInfoPage.this);
		this.metGroupName = (EditText) findViewById(R.id.etGroupName);
		this.metGroupPass = (EditText) findViewById(R.id.etGroupPass);
		this.metGroupPassAgain = (EditText) findViewById(R.id.etGroupPassAgain);
		this.mbtSave = (Button) findViewById(R.id.btSave);
		mbtSave.setOnClickListener(clickListener);
	}

	private void setViews() {
		if (null != groupName && null != groupPass) {
			metGroupName.setText(groupName);
			metGroupPass.setText(groupPass);
		}
	}

	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.btSave:
				if (metGroupName.getText().toString().trim().length() >= 6 && metGroupPass.getText().toString().trim().length() >= 6 && metGroupPassAgain.getText().toString().trim().length() >= 6) {
					if (metGroupPass.getText().toString().trim().equals(metGroupPassAgain.getText().toString().trim())) {
						try {
							mProgressDialog.setMessage(getString(R.string.set_group_info_waiting));
							mProgressDialog.show();
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("groupName", metGroupName.getText().toString().trim());
							jsonObject.put("groupPass", metGroupPass.getText().toString().trim());
							MyCon.con(SetGroupInfoPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 5006, jsonObject.toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						showToast(getString(R.string.set_group_pass_please_input_all_right));
					}
				} else {
					showToast(getString(R.string.set_group_pass_please_input_all_right));
				}
				break;
			default:
				break;
			}
		}
	};
	private MessageCallBack messageCallBack = new MessageCallBack(SetGroupInfoPage.this) {
		@Override
		protected void onRead(int mark, int type, String content) {
			// showToast(type + "," + content);
			switch (type) {
			case 5005:
				try {
					JSONObject jsonObject = new JSONObject(content);
					if (0 == jsonObject.getInt("result")) {
						showToast("获取成功");
						JSONObject resJsonObject = jsonObject.getJSONObject("msg");
						groupName = resJsonObject.getString("groupName");
						groupPass = resJsonObject.getString("groupPass");
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
			case 5006:
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
		Toast.makeText(SetGroupInfoPage.this, str, Toast.LENGTH_SHORT).show();
	}
}
