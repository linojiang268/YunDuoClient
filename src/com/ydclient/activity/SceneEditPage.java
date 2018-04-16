package com.ydclient.activity;

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
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.ydclient.adapter.GvSceneEidtAdapter;
import com.ydclient.model.SceneItemsModel;

import frame.ydclient.socket.MessageCallBack;
import frame.ydclient.socket.MyCon;

public class SceneEditPage extends Activity {
	private Button mbtAdd;
	private ImageButton mibt1, mibt2, mibt3, mibt4, mibt5, mibt6;
	private ImageButton mibtDel1, mibtDel2, mibtDel3, mibtDel4, mibtDel5, mibtDel6;
	private TextView mtv1, mtv2, mtv3, mtv4, mtv5, mtv6;
	private GridView mgv;
	private GvSceneEidtAdapter mGvSceneEidtAdapter;
	private Gson gson;
	private SceneItemsModel mSceneItemsModelUsed, mSceneItemsModelNoUsed;
	private SceneUsedItem[] mSceneUsedItemList;
	private ProgressDialog mProgressDialog;
	private Builder builder;
	private int index = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_gv_scene_edit);
		this.gson = new Gson();
		this.findView();
		this.setView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyCon.addListener(messageCallBack);
		// （2）获取普通场景列表 6502
		// 发送：{}
		// 返回：{result=返回值 msg=响应信息 scenes=数组}
		// （3）获取常用场景列表 6503
		// 发送：{}
		// 返回：{result=返回值 msg=响应信息 scenes=数组}
		MyCon.con(SceneEditPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6502, "{}");
		MyCon.con(SceneEditPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6503, "{}");
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
		this.mbtAdd = (Button) findViewById(R.id.btAdd);
		this.mibt1 = (ImageButton) findViewById(R.id.ibt1);
		this.mibt2 = (ImageButton) findViewById(R.id.ibt2);
		this.mibt3 = (ImageButton) findViewById(R.id.ibt3);
		this.mibt4 = (ImageButton) findViewById(R.id.ibt4);
		this.mibt5 = (ImageButton) findViewById(R.id.ibt5);
		this.mibt6 = (ImageButton) findViewById(R.id.ibt6);
		this.mibtDel1 = (ImageButton) findViewById(R.id.ibtDel1);
		this.mibtDel2 = (ImageButton) findViewById(R.id.ibtDel2);
		this.mibtDel3 = (ImageButton) findViewById(R.id.ibtDel3);
		this.mibtDel4 = (ImageButton) findViewById(R.id.ibtDel4);
		this.mibtDel5 = (ImageButton) findViewById(R.id.ibtDel5);
		this.mibtDel6 = (ImageButton) findViewById(R.id.ibtDel6);
		this.mtv1 = (TextView) findViewById(R.id.tv1);
		this.mtv2 = (TextView) findViewById(R.id.tv2);
		this.mtv3 = (TextView) findViewById(R.id.tv3);
		this.mtv4 = (TextView) findViewById(R.id.tv4);
		this.mtv5 = (TextView) findViewById(R.id.tv5);
		this.mtv6 = (TextView) findViewById(R.id.tv6);
		this.mgv = (GridView) findViewById(R.id.gv);
		mGvSceneEidtAdapter = new GvSceneEidtAdapter(SceneEditPage.this, false);
		mgv.setAdapter(mGvSceneEidtAdapter);
		mbtAdd.setOnClickListener(clickListener);
		this.mSceneUsedItemList = new SceneUsedItem[6];
		this.addToScenUsedList(new ImageButton[] { mibt1, mibt2, mibt3, mibt4, mibt5, mibt6 }, new ImageButton[] { mibtDel1, mibtDel2, mibtDel3, mibtDel4, mibtDel5, mibtDel6 }, new TextView[] { mtv1, mtv2, mtv3, mtv4, mtv5, mtv6 });
		mibt1.setOnClickListener(clickListener);
		mibt2.setOnClickListener(clickListener);
		mibt3.setOnClickListener(clickListener);
		mibt4.setOnClickListener(clickListener);
		mibt5.setOnClickListener(clickListener);
		mibt6.setOnClickListener(clickListener);
		mtv1.setOnClickListener(clickListener);
		mtv2.setOnClickListener(clickListener);
		mtv3.setOnClickListener(clickListener);
		mtv4.setOnClickListener(clickListener);
		mtv5.setOnClickListener(clickListener);
		mtv6.setOnClickListener(clickListener);
		mibt1.setOnLongClickListener(longClickListener);
		mibt2.setOnLongClickListener(longClickListener);
		mibt3.setOnLongClickListener(longClickListener);
		mibt4.setOnLongClickListener(longClickListener);
		mibt5.setOnLongClickListener(longClickListener);
		mibt6.setOnLongClickListener(longClickListener);
		mProgressDialog = new ProgressDialog(SceneEditPage.this);
		builder = new AlertDialog.Builder(SceneEditPage.this);
		builder.setTitle(getString(R.string.scene_set));
		builder.setItems(new String[] { getString(R.string.scene_update), getString(R.string.scene_delete) }, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					Intent intent = new Intent(SceneEditPage.this, SceneAddPage.class);
					intent.putExtra("SceneInfo", mSceneItemsModelUsed.scenes.get(index));
					startActivity(intent);
					break;
				case 1:
					MyCon.con(SceneEditPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6504, "{\"_id\":" + mSceneItemsModelUsed.scenes.get(index)._id + "}");
					showToast(getString(R.string.scene_delete_waiting));
					break;
				default:
					break;
				}
			}
		});
	}

	private void setView() {
		if (null != mSceneItemsModelUsed && null != mSceneItemsModelUsed.scenes && mSceneItemsModelUsed.scenes.size() > 0) {
			for (int i = 0; i < 6; i++) {
				if (i < mSceneItemsModelUsed.scenes.size()) {
					mSceneUsedItemList[i].visible(mSceneItemsModelUsed.scenes.get(i).getName());
				} else {
					mSceneUsedItemList[i].invisible();
				}
			}
		} else {
			for (int i = 0; i < 6; i++) {
				mSceneUsedItemList[i].invisible();
			}
		}
		if (null != mSceneItemsModelNoUsed && null != mSceneItemsModelNoUsed.scenes && mSceneItemsModelNoUsed.scenes.size() > 0) {
			// mGvSceneEidtAdapter.clearItems();
			mGvSceneEidtAdapter.refreshItems(mSceneItemsModelNoUsed.scenes);
			// mgv.setNumColumns(mGvSceneEidtAdapter.getCount());
			// ListViewTool.reSetGridViewHViewWidth(mgv);
		} else {
			mGvSceneEidtAdapter.clearItems();
		}
	}

	private void addToScenUsedList(ImageButton[] ibts, ImageButton[] ibtDels, TextView[] tvs) {
		for (int i = 0; i < 6; i++) {
			mSceneUsedItemList[i] = new SceneUsedItem(ibts[i], ibtDels[i], tvs[i]);
		}
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.btAdd:
				startActivity(new Intent(SceneEditPage.this, SceneAddPage.class));
				break;
			case R.id.ibt1:
			case R.id.tv1:
				if (null == mSceneItemsModelUsed || null == mSceneItemsModelUsed.scenes || mSceneItemsModelUsed.scenes.size() < 0) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.scene_set_used_waiting));
				mProgressDialog.show();
				MyCon.con(SceneEditPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6506, "{\"_id\":" + mSceneItemsModelUsed.scenes.get(0).get_id() + ",\"isUsed\":0}");
				break;
			case R.id.ibt2:
			case R.id.tv2:
				if (null == mSceneItemsModelUsed || null == mSceneItemsModelUsed.scenes || mSceneItemsModelUsed.scenes.size() < 1) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.scene_set_used_waiting));
				mProgressDialog.show();
				// 设置为普通场景
				MyCon.con(SceneEditPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6506, "{\"_id\":" + mSceneItemsModelUsed.scenes.get(1).get_id() + ",\"isUsed\":0}");
				break;
			case R.id.ibt3:
			case R.id.tv3:
				if (null == mSceneItemsModelUsed || null == mSceneItemsModelUsed.scenes || mSceneItemsModelUsed.scenes.size() < 2) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.scene_set_used_waiting));
				mProgressDialog.show();
				MyCon.con(SceneEditPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6506, "{\"_id\":" + mSceneItemsModelUsed.scenes.get(2).get_id() + ",\"isUsed\":0}");
				break;
			case R.id.ibt4:
			case R.id.tv4:
				if (null == mSceneItemsModelUsed || null == mSceneItemsModelUsed.scenes || mSceneItemsModelUsed.scenes.size() < 3) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.scene_set_used_waiting));
				mProgressDialog.show();
				MyCon.con(SceneEditPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6506, "{\"_id\":" + mSceneItemsModelUsed.scenes.get(3).get_id() + ",\"isUsed\":0}");
				break;
			case R.id.ibt5:
			case R.id.tv5:
				if (null == mSceneItemsModelUsed || null == mSceneItemsModelUsed.scenes || mSceneItemsModelUsed.scenes.size() < 4) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.scene_set_used_waiting));
				mProgressDialog.show();
				MyCon.con(SceneEditPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6506, "{\"_id\":" + mSceneItemsModelUsed.scenes.get(4).get_id() + ",\"isUsed\":0}");
				break;
			case R.id.ibt6:
			case R.id.tv6:
				if (null == mSceneItemsModelUsed || null == mSceneItemsModelUsed.scenes || mSceneItemsModelUsed.scenes.size() < 5) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.scene_set_used_waiting));
				mProgressDialog.show();
				MyCon.con(SceneEditPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6506, "{\"_id\":" + mSceneItemsModelUsed.scenes.get(5).get_id() + ",\"isUsed\":0}");
				break;
			default:
				break;
			}
		}
	};
	private OnLongClickListener longClickListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			switch (v.getId()) {
			case R.id.ibt1:
			case R.id.tv1:
				index = 0;
				break;
			case R.id.ibt2:
			case R.id.tv2:
				index = 1;
				break;
			case R.id.ibt3:
			case R.id.tv3:
				index = 2;
				break;
			case R.id.ibt4:
			case R.id.tv4:
				index = 3;
				break;
			case R.id.ibt5:
			case R.id.tv5:
				index = 4;
				break;
			case R.id.ibt6:
			case R.id.tv6:
				index = 5;
				break;
			default:
				break;
			}
			builder.show();
			return false;
		}
	};
	private MessageCallBack messageCallBack = new MessageCallBack(SceneEditPage.this) {
		@Override
		protected void onRead(int mark, int type, String content) {
			// showToast(type + "," + content);
			switch (type) {
			case 6502:
				mSceneItemsModelNoUsed = gson.fromJson(content, SceneItemsModel.class);
				setView();
				break;
			case 6503:
				mSceneItemsModelUsed = gson.fromJson(content, SceneItemsModel.class);
				setView();
				break;
			case 6504:
			case 6506:
				mProgressDialog.dismiss();
				try {
					showToast(new JSONObject(content).getString("msg"));
				} catch (JSONException e) {
					e.printStackTrace();
					showToast(getString(R.string.error_protocol));
				}
				MyCon.con(SceneEditPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6502, "{}");
				MyCon.con(SceneEditPage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6503, "{}");
				break;
			default:
				break;
			}
		}
	};

	private void showToast(String str) {
		Toast.makeText(SceneEditPage.this, str, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 常用场景 自定义item
	 * 
	 * @author Administrator
	 * 
	 */
	class SceneUsedItem {
		private ImageButton mibt, mibtDel;
		private TextView mtv;

		public SceneUsedItem(ImageButton mibt, ImageButton mibtDel, TextView mtv) {
			super();
			this.mibt = mibt;
			this.mibtDel = mibtDel;
			this.mtv = mtv;
		}

		// public void setText(String str) {
		// mtv.setText(str);
		// }

		public void visible(String str) {
			this.mibt.setVisibility(View.VISIBLE);
			this.mibtDel.setVisibility(View.VISIBLE);
			this.mtv.setVisibility(View.VISIBLE);
			if (null != str) {
				mtv.setText(str);
			} else {
				mtv.setText(getString(R.string.main_scene));
			}
		}

		public void invisible() {
			this.mibt.setVisibility(View.INVISIBLE);
			this.mibtDel.setVisibility(View.INVISIBLE);
			this.mtv.setVisibility(View.INVISIBLE);
			mtv.setText(getString(R.string.main_scene));
		}
	}
}
