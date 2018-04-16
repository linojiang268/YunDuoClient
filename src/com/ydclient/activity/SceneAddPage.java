package com.ydclient.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.ydclient.adapter.SceneAddAdapter;
import com.ydclient.model.CommandMsgItemsModel;
import com.ydclient.model.SceneCommandMsg;
import com.ydclient.model.SceneInfo;
import com.ydclient.model.SceneUpdateModel;
import com.ydclient.tool.ListViewTool;

import frame.ydclient.socket.MessageCallBack;
import frame.ydclient.socket.MyCon;

public class SceneAddPage extends Activity {
	private ProgressDialog mProgressDialog;
	private SceneInfo mSceneInfo;
	// private HashMap<String, CommandInfo> mCommandMap;
	private List<SceneCommandMsg> mSceneCommandMsgList;
	private boolean mHasOld, mHasFresh;
	private SceneAddAdapter mSceneAddAdapter;
	private EditText metName;
	private ListView mlv;
	private Button mbtLearn, mbtSet, mbtSave;
	private Gson gson;
	private Builder builder;
	private int index = -1;
	private boolean isLearning = false;
	private String mMac = "0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_scene_add);
		this.gson = new Gson();
		Intent intent = getIntent();
		if (null != intent && intent.hasExtra("SceneInfo")) {
			mSceneInfo = (SceneInfo) intent.getSerializableExtra("SceneInfo");
			// if (null != mDeviceInfo) {
			// this.findView();
			// return;
			// }
		}
		if (null == mSceneInfo) {
			mHasOld = false;
			mHasFresh = false;
		} else {
			mHasOld = true;
			mHasFresh = false;
		}
		// showToast(getString(R.string.error_page));
		// finish();
		this.findView();
		this.setView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyCon.addListener(messageCallBack);
		if (mHasOld && !mHasFresh) {
			MyCon.con(SceneAddPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6505, "{\"_id\":" + mSceneInfo.get_id() + "}");
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
		mProgressDialog = new ProgressDialog(SceneAddPage.this);
		this.metName = (EditText) findViewById(R.id.etName);
		this.mlv = (ListView) findViewById(R.id.lv);
		this.mbtSet = (Button) findViewById(R.id.btSet);
		this.mbtSave = (Button) findViewById(R.id.btSave);
		this.mbtLearn = (Button) findViewById(R.id.btLearn);
		mbtLearn.setOnClickListener(clickListener);
		mbtSet.setOnClickListener(clickListener);
		mbtSave.setOnClickListener(clickListener);
		mSceneAddAdapter = new SceneAddAdapter(SceneAddPage.this, false);
		mlv.setAdapter(mSceneAddAdapter);
		builder = new AlertDialog.Builder(SceneAddPage.this);
		builder.setTitle(getString(R.string.scene_command_set));
		builder.setItems(new String[] { getString(R.string.scene_command_set_delete) }, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					mSceneCommandMsgList.remove(index);
					showToast(getString(R.string.scene_command_set_delete_success));
					setView();
					break;
				default:
					break;
				}
			}
		});
		mlv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				index = arg2;
				builder.show();
				return false;
			}
		});
	}

	private void setView() {
		if (mHasOld && !mHasFresh) {
			mProgressDialog.setMessage(getString(R.string.scene_set_info_waiting));
			mProgressDialog.show();
			return;
		}
		if (null != mSceneInfo) {
			metName.setText(mSceneInfo.getName());
		}
		if (null != mSceneCommandMsgList) {
			mSceneAddAdapter.refreshItems(mSceneCommandMsgList);
			ListViewTool.reSetListViewHeight(mlv);
		} else {
			mSceneAddAdapter.clearItems();
			ListViewTool.reSetListViewHeight(mlv);
		}
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (mHasOld && !mHasFresh) {
				showToast(getString(R.string.scene_set_info_no_refresh));
				return;
			}
			switch (arg0.getId()) {
			case R.id.btLearn:
				isLearning = true;
				mProgressDialog.setMessage(getString(R.string.learn_waiting));
				mProgressDialog.show();
				break;
			case R.id.btSet:
				startActivityForResult(new Intent(SceneAddPage.this, SceneMainPage.class), 6500);
				break;
			case R.id.btSave:
				if (metName.getText().toString().trim().length() <= 0) {
					showToast(getString(R.string.scene_set_info_input_name_h));
					return;
				}
				if (null == mSceneInfo) {
					mSceneInfo = new SceneInfo(null, metName.getText().toString().trim(), null, mMac);
				} else {
					mSceneInfo.setCommand(mMac);
					mSceneInfo.setName(metName.getText().toString().trim());
				}
				SceneUpdateModel sceneUpdateModel = new SceneUpdateModel();
				sceneUpdateModel.sceneInfo = mSceneInfo;
				sceneUpdateModel.commandMsgs = mSceneCommandMsgList;
				MyCon.con(SceneAddPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6501, gson.toJson(sceneUpdateModel));
				break;
			default:
				break;
			}
		}
	};

	private MessageCallBack messageCallBack = new MessageCallBack(SceneAddPage.this) {
		@Override
		protected void onRead(int mark, int type, String content) {
			// showToast(type + "," + content);
			switch (type) {
			case 60111:
				if (!isLearning) {
					return;
				}
				isLearning = false;
				try {
					JSONObject resJsonObject = new JSONObject(content);
					if (0 == resJsonObject.getInt("result")) {
						// mCommandMap.put(String.valueOf(mLearnType), new
						// CommandInfo(null, mLearnType,
						// resJsonObject.getString("command"), mLearnName));
						mMac = resJsonObject.getString("command");
						mProgressDialog.dismiss();
						showToast(resJsonObject.getString("msg"));
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
					showToast(getString(R.string.error_protocol));
				}
				break;
			case 6505:
				CommandMsgItemsModel commandMsgItemsModel = gson.fromJson(content, CommandMsgItemsModel.class);
				mSceneCommandMsgList = commandMsgItemsModel.commandMsgs;
				mHasFresh = true;
				mProgressDialog.dismiss();
				// try {
				// showToast(new JSONObject(content).getString("msg"));
				// } catch (JSONException e) {
				// e.printStackTrace();
				// showToast(getString(R.string.error_protocol));
				// }
				// mSceneItemsModelUsed = gson.fromJson(content,
				// SceneItemsModel.class);
				setView();
				break;
			case 6501:
				try {
					JSONObject resJsonObject = new JSONObject(content);
					if (0 == resJsonObject.getInt("result")) {
						mProgressDialog.dismiss();
						showToast(resJsonObject.getString("msg"));
						finish();
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
		Toast.makeText(SceneAddPage.this, str, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 常用场景 自定义item
	 * 
	 * @author Administrator
	 * 
	 */
	class SceneUsedItem {
		private ImageButton mibt;
		private TextView mtv;

		public SceneUsedItem(ImageButton mibt, TextView mtv) {
			super();
			this.mibt = mibt;
			this.mtv = mtv;
		}

		// public void setText(String str) {
		// mtv.setText(str);
		// }

		public void visible(String str) {
			this.mibt.setVisibility(View.VISIBLE);
			this.mtv.setVisibility(View.VISIBLE);
			if (null != str) {
				mtv.setText(str);
			} else {
				mtv.setText(getString(R.string.main_scene));
			}
		}

		public void invisible() {
			this.mibt.setVisibility(View.INVISIBLE);
			this.mtv.setVisibility(View.INVISIBLE);
			mtv.setText(getString(R.string.main_scene));
		}
	}

	// ---------------------------------------------------
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 6500:
			if (RESULT_OK == resultCode) {
				// boolean isEdit = data.getBooleanExtra("isEdit", false);
				SceneCommandMsg sceneCommandMsg = gson.fromJson(data.getStringExtra("SceneCommandMsg"), SceneCommandMsg.class);
				if (null == mSceneCommandMsgList) {
					mSceneCommandMsgList = new ArrayList<SceneCommandMsg>();
				}
				boolean hasSet = false;
				for (int i = 0; i < mSceneCommandMsgList.size(); i++) {
					if (mSceneCommandMsgList.get(i).d_id.intValue() == sceneCommandMsg.d_id.intValue()) {
						mSceneCommandMsgList.set(i, sceneCommandMsg);
						hasSet = true;
						break;
					}
				}
				if (hasSet) {
					showToast(getString(R.string.scene_device_set_success));
				} else {
					mSceneCommandMsgList.add(sceneCommandMsg);
					showToast(getString(R.string.scene_device_add_success));
				}
				// if (isEdit) {
				// // 修改
				// } else {
				// // 新增
				// }
				setView();
			}
			break;
		default:
			break;
		}
	}
}
