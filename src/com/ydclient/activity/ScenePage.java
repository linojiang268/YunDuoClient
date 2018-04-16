package com.ydclient.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ydclient.adapter.SceneAdapter;
import com.ydclient.model.SceneItemsModel;

import frame.ydclient.socket.MessageCallBack;
import frame.ydclient.socket.MyCon;

public class ScenePage extends Activity implements OnGestureListener {
	private Button mbtEdit, mbtMove;
	private ImageButton mibt1, mibt2, mibt3, mibt4, mibt5, mibt6;
	private TextView mtv1, mtv2, mtv3, mtv4, mtv5, mtv6;
	// private GridView mgv;
	private SceneAdapter mSceneAdapter;
	private Gson gson;
	private SceneItemsModel mSceneItemsModelUsed, mSceneItemsModelNoUsed;
	private SceneUsedItem[] mSceneUsedItemList;
	private ProgressDialog mProgressDialog;

	private ListView mlv;
	private boolean mIn = false;
	private Animation mAnimationIn, mAnimationOut;
	private GestureDetector detector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_scene);
		this.gson = new GsonBuilder().disableHtmlEscaping().create();
		this.findView();
		this.setView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyCon.addListener(messageCallBack);
		if (null == mSceneItemsModelUsed) {
			mProgressDialog.setMessage(getString(R.string.get_waiting));
			mProgressDialog.show();
		}
		// （2）获取普通场景列表 6502
		// 发送：{}
		// 返回：{result=返回值 msg=响应信息 scenes=数组}
		// （3）获取常用场景列表 6503
		// 发送：{}
		// 返回：{result=返回值 msg=响应信息 scenes=数组}
		MyCon.con(ScenePage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6502, "{}");
		MyCon.con(ScenePage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6503, "{}");
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

	private void findView() {
		detector = new GestureDetector(this);
		this.mbtEdit = (Button) findViewById(R.id.btEdit);
		this.mbtMove = (Button) findViewById(R.id.btMove);
		this.mibt1 = (ImageButton) findViewById(R.id.ibt1);
		this.mibt2 = (ImageButton) findViewById(R.id.ibt2);
		this.mibt3 = (ImageButton) findViewById(R.id.ibt3);
		this.mibt4 = (ImageButton) findViewById(R.id.ibt4);
		this.mibt5 = (ImageButton) findViewById(R.id.ibt5);
		this.mibt6 = (ImageButton) findViewById(R.id.ibt6);
		this.mtv1 = (TextView) findViewById(R.id.tv1);
		this.mtv2 = (TextView) findViewById(R.id.tv2);
		this.mtv3 = (TextView) findViewById(R.id.tv3);
		this.mtv4 = (TextView) findViewById(R.id.tv4);
		this.mtv5 = (TextView) findViewById(R.id.tv5);
		this.mtv6 = (TextView) findViewById(R.id.tv6);
		mAnimationIn = new TranslateAnimation(150f, 0.0f, 0f, 0f);
		mAnimationIn.setDuration(500);
		mAnimationIn.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mlv.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mlv.setVisibility(View.VISIBLE);
				mbtMove.setBackgroundResource(R.drawable.bt_image_progress_icon_next_style);
				mIn = true;
			}
		});
		mAnimationOut = new TranslateAnimation(0f, 150.0f, 0f, 0f);
		mAnimationOut.setDuration(500);
		mAnimationOut.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mlv.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mlv.setVisibility(View.GONE);
				mbtMove.setBackgroundResource(R.drawable.bt_image_progress_icon_last_style);
				mIn = false;
			}
		});
		// this.mgv = (GridView) findViewById(R.id.gv);
		// mGvSceneAdapter = new GvSceneAdapter(ScenePage.this, false);
		// mgv.setAdapter(mGvSceneAdapter);
		this.mlv = (ListView) findViewById(R.id.lv);
		mSceneAdapter = new SceneAdapter(ScenePage.this, false);
		mlv.setAdapter(mSceneAdapter);
		mbtEdit.setOnClickListener(clickListener);
		mbtMove.setOnClickListener(clickListener);
		this.mSceneUsedItemList = new SceneUsedItem[6];
		this.addToScenUsedList(new ImageButton[] { mibt1, mibt2, mibt3, mibt4, mibt5, mibt6 }, new TextView[] { mtv1, mtv2, mtv3, mtv4, mtv5, mtv6 });
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
		mProgressDialog = new ProgressDialog(ScenePage.this);
		mlv.startAnimation(mAnimationOut);
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
			mSceneAdapter.refreshItems(mSceneItemsModelNoUsed.scenes);
			// mgv.setNumColumns(mGvSceneAdapter.getCount());
			// ListViewTool.reSetGridViewHViewWidth(mgv);
		} else {
			mSceneAdapter.clearItems();
		}
	}

	private void addToScenUsedList(ImageButton[] ibts, TextView[] tvs) {
		for (int i = 0; i < 6; i++) {
			mSceneUsedItemList[i] = new SceneUsedItem(ibts[i], tvs[i]);
		}
	}

	private void moveList() {
		if (mIn) {
			mlv.startAnimation(mAnimationOut);
		} else {
			mlv.startAnimation(mAnimationIn);
		}
		// mIn = !mIn;
	}

	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.btEdit:
				startActivity(new Intent(ScenePage.this, SceneEditPage.class));
				break;
			case R.id.btMove:
				moveList();
				break;
			case R.id.ibt1:
			case R.id.tv1:
				if (null == mSceneItemsModelUsed || null == mSceneItemsModelUsed.scenes || mSceneItemsModelUsed.scenes.size() < 0) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.scene_do_waiting));
				mProgressDialog.show();
				MyCon.con(ScenePage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6510, "{\"_id\":" + mSceneItemsModelUsed.scenes.get(0).get_id() + "}");
				break;
			case R.id.ibt2:
			case R.id.tv2:
				if (null == mSceneItemsModelUsed || null == mSceneItemsModelUsed.scenes || mSceneItemsModelUsed.scenes.size() < 1) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.scene_do_waiting));
				mProgressDialog.show();
				MyCon.con(ScenePage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6510, "{\"_id\":" + mSceneItemsModelUsed.scenes.get(1).get_id() + "}");
				break;
			case R.id.ibt3:
			case R.id.tv3:
				if (null == mSceneItemsModelUsed || null == mSceneItemsModelUsed.scenes || mSceneItemsModelUsed.scenes.size() < 2) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.scene_do_waiting));
				mProgressDialog.show();
				MyCon.con(ScenePage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6510, "{\"_id\":" + mSceneItemsModelUsed.scenes.get(2).get_id() + "}");
				break;
			case R.id.ibt4:
			case R.id.tv4:
				if (null == mSceneItemsModelUsed || null == mSceneItemsModelUsed.scenes || mSceneItemsModelUsed.scenes.size() < 3) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.scene_do_waiting));
				mProgressDialog.show();
				MyCon.con(ScenePage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6510, "{\"_id\":" + mSceneItemsModelUsed.scenes.get(3).get_id() + "}");
				break;
			case R.id.ibt5:
			case R.id.tv5:
				if (null == mSceneItemsModelUsed || null == mSceneItemsModelUsed.scenes || mSceneItemsModelUsed.scenes.size() < 4) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.scene_do_waiting));
				mProgressDialog.show();
				MyCon.con(ScenePage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6510, "{\"_id\":" + mSceneItemsModelUsed.scenes.get(4).get_id() + "}");
				break;
			case R.id.ibt6:
			case R.id.tv6:
				if (null == mSceneItemsModelUsed || null == mSceneItemsModelUsed.scenes || mSceneItemsModelUsed.scenes.size() < 5) {
					return;
				}
				mProgressDialog.setMessage(getString(R.string.scene_do_waiting));
				mProgressDialog.show();
				MyCon.con(ScenePage.this).sendZlib((int) (System.currentTimeMillis() / 1000), 6510, "{\"_id\":" + mSceneItemsModelUsed.scenes.get(5).get_id() + "}");
				break;
			default:
				break;
			}
		}
	};

	private MessageCallBack messageCallBack = new MessageCallBack(ScenePage.this) {
		@Override
		protected void onRead(int mark, int type, String content) {
			// showToast(type + "," + content);
			switch (type) {
			case 6502:
				mSceneItemsModelNoUsed = gson.fromJson(content, SceneItemsModel.class);
				setView();
				break;
			case 6503:
				mProgressDialog.dismiss();
				mSceneItemsModelUsed = gson.fromJson(content, SceneItemsModel.class);
				setView();
				break;
			case 6510:
				try {
					mProgressDialog.dismiss();
					showToast(new JSONObject(content).getString("msg"));
				} catch (JSONException e) {
					e.printStackTrace();
					showToast(getString(R.string.error_protocol));
				}
				break;
			default:
				break;
			}
		}
	};

	private void showToast(String str) {
		Toast.makeText(ScenePage.this, str, Toast.LENGTH_SHORT).show();
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

	// ----------------------------
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.detector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		if (arg0.getX() - arg1.getX() > 60) {
			// 往左
			mlv.startAnimation(mAnimationIn);
			mIn = true;
			return true;
		} else if (arg0.getX() - arg1.getX() < -60) {
			mlv.startAnimation(mAnimationOut);
			mIn = false;
			return true;
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}
	// ----------------------------
}
