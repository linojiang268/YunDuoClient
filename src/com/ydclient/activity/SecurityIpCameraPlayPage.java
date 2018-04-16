package com.ydclient.activity;

import java.nio.ByteBuffer;

import object.p2pipcam.nativecaller.NativeCaller;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ydclient.activity.BridgeService.PlayInterface;
import com.ydclient.ipcamera.AudioPlayer;
import com.ydclient.ipcamera.ContentCommon;
import com.ydclient.ipcamera.CustomBuffer;
import com.ydclient.ipcamera.CustomBufferData;
import com.ydclient.ipcamera.CustomBufferHead;
import com.ydclient.ipcamera.MyRender;
import com.ydclient.ipcamera.SystemValue;

public class SecurityIpCameraPlayPage extends Activity implements OnTouchListener, OnGestureListener, OnClickListener, PlayInterface {

	private static final String LOG_TAG = "PlayActivity";
	private static final int FULLSCREEN = 0;
	private static final int STANDARD = 1;
	private static final int MAGNIFY = 2;
	private int playmode = 1;
	private static final int AUDIO_BUFFER_START_CODE = 0xff00ff;
	private SurfaceView msfvPlay = null;
	private SurfaceHolder playHolder = null;
	private byte[] videodata = null;
	private int videoDataLen = 0;
	private int nVideoWidth = 0;
	private int nVideoHeight = 0;
	private View progressView = null;
	private boolean bProgress = true;
	private GestureDetector gt = new GestureDetector(this);
	@SuppressWarnings("unused")
	private int nSurfaceHeight = 0;
	private int nResolution = 0;
	private int nBrightness = 0;
	private int nContrast = 0;
	@SuppressWarnings("unused")
	private int nMode = 0;
	@SuppressWarnings("unused")
	private int nFlip = 0;
	@SuppressWarnings("unused")
	private int nFramerate = 0;
	private boolean bInitCameraParam = false;
	private boolean bManualExit = false;
	private TextView mtvOsd = null;
	private String strName = null;;
	private String strDID = null;;
	private int streamType = ContentCommon.MJPEG_SUB_STREAM;
	private PopupWindow popupWindow_about = null;
	private View mrlOsd = null;
	private boolean bDisplayFinished = true;
	private surfaceCallback videoCallback = new surfaceCallback();
	private int nPlayCount = 0;
	private CustomBuffer AudioBuffer = null;
	private AudioPlayer audioPlayer = null;
	private boolean bAudioStart = false;
	private int nStreamCodecType;
	private int nP2PMode = ContentCommon.PPPP_MODE_P2P_NORMAL;
	private TextView mtvTimeout = null;
	private boolean bTimeoutStarted = false;
	private int nTimeoutRemain = 180;
	private boolean isTakeVideo = false;
	private boolean isLeftRight = false;
	private boolean isUpDown = false;
	private PopupWindow mPopupWindowProgress;
	private final int BRIGHT = 1;
	private final int CONTRAST = 2;
	private boolean isHorizontalMirror = false;
	private boolean isVerticalMirror = false;
	private boolean isUpDownPressed = false;
	private boolean isShowtoping = false;
	private ImageView mivVideo;
	private ImageView mivVideoStandard;
	private ImageButton mibtPtzAudio;
	private ImageButton mibtPtzPlayMode;
	private Button mbtPtzResolutoin;
	private Animation showAnim;
	private boolean isTakepic = false;
	private boolean isMcriophone = false;
	private boolean isExit = false;
	private PopupWindow resolutionPopWindow;
	private Animation dismissAnim;
	private View mllPtzOtherSetViewAnim;
	private int timeTag = 0;
	private int timeOne = 0;
	private int timeTwo = 0;
	private ImageButton mibtBack;
	private BitmapDrawable drawable = null;
	// private LinkedList<byte[]> bs = null;
	private MyBrodCast brodCast = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getDataFromOther();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.page_ipcamera_play);
		strName = SystemValue.deviceName;
		strDID = SystemValue.deviceId;
		findView();
		// connectIpCamera();
		initPlay();
	}

	private void initPlay() {
		InitParams();
		AudioBuffer = new CustomBuffer();
		audioPlayer = new AudioPlayer(AudioBuffer);
		// myvideoRecorder = new CustomVideoRecord(this, strDID);
		BridgeService.setPlayInterface(this);
		playHolder = msfvPlay.getHolder();
		playHolder.setFormat(PixelFormat.RGB_565);
		playHolder.addCallback(videoCallback);

		msfvPlay.setOnTouchListener(this);
		msfvPlay.setLongClickable(true);

		getCameraParams();
		dismissTopAnim = AnimationUtils.loadAnimation(this, R.anim.ipcamera_ptz_top_anim_dismiss);
		showTopAnim = AnimationUtils.loadAnimation(this, R.anim.ipcamera_ptz_top_anim_show);
		showAnim = AnimationUtils.loadAnimation(this, R.anim.ipcamera_ptz_otherset_anim_show);
		dismissAnim = AnimationUtils.loadAnimation(this, R.anim.ipcamera_ptz_otherset_anim_dismiss);

		// prompt user how to control ptz when first enter play
		SharedPreferences sharePreferences = getSharedPreferences("ptzcontrol", MODE_PRIVATE);
		isPTZPrompt = sharePreferences.getBoolean("ptzcontrol", false);
		if (!isPTZPrompt) {
			Editor edit = sharePreferences.edit();
			edit.putBoolean("ptzcontrol", true);
			edit.commit();
		}
		initExitPopupWindow2();
		brodCast = new MyBrodCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction("drop");
		SecurityIpCameraPlayPage.this.registerReceiver(brodCast, filter);
	}

	private void findView() {
		this.msfvPlay = (SurfaceView) findViewById(R.id.sfvPlay);
		this.msfvPlay.setBackgroundColor(0xff000000);
		this.mibtBack = (ImageButton) findViewById(R.id.ibtBack);
		this.mgsfvPlay = (GLSurfaceView) findViewById(R.id.gsfvPlay);
		this.myRender = new MyRender(mgsfvPlay);
		this.mgsfvPlay.setRenderer(myRender);
		this.mivUp = (ImageView) findViewById(R.id.ivUp);
		this.mivDown = (ImageView) findViewById(R.id.ivDown);
		this.mivRight = (ImageView) findViewById(R.id.ivRight);
		this.mivLeft = (ImageView) findViewById(R.id.ivLeft);
		mivUp.setOnClickListener(this);
		mivDown.setOnClickListener(this);
		mivLeft.setOnClickListener(this);
		mivRight.setOnClickListener(this);
		mibtBack.setOnClickListener(this);
		this.mivVideo = (ImageView) findViewById(R.id.ivVideo);
		this.mivVideoStandard = (ImageView) findViewById(R.id.ivVideoStandard);
		progressView = (View) findViewById(R.id.progressLayout);
		this.mtvOsd = (TextView) findViewById(R.id.tvOsd);
		this.mtvTimeout = (TextView) findViewById(R.id.tvTimeout);
		this.mrlOsd = (View) findViewById(R.id.rlOsd);
		this.mibtPtzHoriMirror = (ImageButton) findViewById(R.id.ibtPtzHoriMirror);
		this.mibtPtzVertMirror = (ImageButton) findViewById(R.id.ibtPtzVertMirror);
		this.mibtPtzHoriTour = (ImageButton) findViewById(R.id.ibtPtzHoriTour);
		this.mibtPtzVertTour = (ImageButton) findViewById(R.id.ibtPtzVertTour);
		this.mibtPtzAudio = (ImageButton) findViewById(R.id.ibtPtzAudio);
		ImageButton ibtPtzBrightness = (ImageButton) findViewById(R.id.ibtPtzBrightness);
		ImageButton ibtPtzContrast = (ImageButton) findViewById(R.id.ibtPtzContrast);
		this.mbtPtzResolutoin = (Button) findViewById(R.id.btPtzResoluti);
		this.mibtPtzPlayMode = (ImageButton) findViewById(R.id.ibtPtzPlayMode);
		this.mllPtzOtherSetViewAnim = findViewById(R.id.llPtzOtherSetViewAnim);
		ImageButton ibtPtzDefaultSet = (ImageButton) findViewById(R.id.ibtPtzDefaultSet);
		mibtPtzHoriMirror.setOnClickListener(this);
		mibtPtzVertMirror.setOnClickListener(this);
		mibtPtzHoriTour.setOnClickListener(this);
		mibtPtzVertTour.setOnClickListener(this);
		mibtPtzAudio.setOnClickListener(this);
		ibtPtzBrightness.setOnClickListener(this);
		ibtPtzContrast.setOnClickListener(this);
		mbtPtzResolutoin.setOnClickListener(this);
		mibtPtzPlayMode.setOnClickListener(this);
		ibtPtzDefaultSet.setOnClickListener(this);
		mrlTopBag = (RelativeLayout) findViewById(R.id.rlTopBg);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ipcamera_top_bg);
		drawable = new BitmapDrawable(bitmap);
		drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		drawable.setDither(true);
		mrlTopBag.setBackgroundDrawable(drawable);
		mllPtzOtherSetViewAnim.setBackgroundDrawable(drawable);
	}

	class MyBrodCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (arg1.getIntExtra("ifdrop", 2) != 2) {
				PPPPMsgHandler.sendEmptyMessage(1004);
			}

		}
	}

	/**
	 * 在UI线程中刷新界面状态
	 * **/
	private Handler PPPPMsgHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1004:
				Toast.makeText(SecurityIpCameraPlayPage.this, "相机断线", 0).show();
				SecurityIpCameraPlayPage.this.finish();
				break;
			default:
				break;
			}
		}
	};

	private class surfaceCallback implements SurfaceHolder.Callback {
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			if (holder == playHolder) {
				streamType = 10;
				NativeCaller.StartPPPPLivestream(strDID, streamType);
			}
		}

		public void surfaceCreated(SurfaceHolder holder) {
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mPopupWindowProgress != null && mPopupWindowProgress.isShowing()) {
			mPopupWindowProgress.dismiss();

		}
		if (resolutionPopWindow != null && resolutionPopWindow.isShowing()) {
			resolutionPopWindow.dismiss();
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// if (!bProgress) {
			// Date date = new Date();
			// if (timeTag == 0) {
			// timeOne = date.getSeconds();
			// timeTag = 1;
			// showToast("再按一次，退出程序！");
			// } else if (timeTag == 1) {
			// timeTwo = date.getSeconds();
			// if (timeTwo - timeOne <= 3) {
			// Intent intent = new Intent("finish");
			// sendBroadcast(intent);
			// SecurityIpCameraPlayPage.this.finish();
			// timeTag = 0;
			// } else {
			// timeTag = 1;
			// showToast("再按一次，退出程序！");
			// }
			// }
			// } else {
			// showSureDialog1();
			// }
			Intent intent = new Intent("finish");
			sendBroadcast(intent);
			finish();
			return true;

		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (!bProgress) {
				showTop();
				showBottom();
			} else {
				showSureDialog1();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/****
	 * 退出确定dialog
	 * */
	public void showSureDialog1() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setIcon(R.drawable.app);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("退出" + getResources().getString(R.string.app_name));
		builder.setTitle("退出播放");
		builder.setMessage("确认退出吗？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Process.killProcess(Process.myPid());
				// Intent intent = new Intent("finish");
				// sendBroadcast(intent);//---------=============================================
				SecurityIpCameraPlayPage.this.finish();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	private void showTop() {
		if (isShowtoping) {
			isShowtoping = false;
			mrlTopBag.setVisibility(View.GONE);
			mrlTopBag.startAnimation(dismissTopAnim);
		} else {
			isShowtoping = true;
			mrlTopBag.setVisibility(View.VISIBLE);
			mrlTopBag.startAnimation(showTopAnim);
		}
	}

	private void defaultVideoParams() {
		nBrightness = 1;
		nContrast = 128;
		NativeCaller.PPPPCameraControl(strDID, 1, 0);
		NativeCaller.PPPPCameraControl(strDID, 2, 128);
		showToast("视频参数恢复默认值");
	}

	private void updateTimeout() {
		mtvTimeout.setText("转发模式观看,剩余时间:" + nTimeoutRemain + "秒");
	}

	private Handler timeoutHandle = new Handler() {
		public void handleMessage(Message msg) {

			if (nTimeoutRemain > 0) {
				nTimeoutRemain = nTimeoutRemain - 1;
				updateTimeout();
				Message msgMessage = new Message();
				timeoutHandle.sendMessageDelayed(msgMessage, 1000);
			} else {
				if (!isExit) {
					showToast("已到观看时间");
				}
				finish();
			}
		}
	};

	private void startTimeout() {
		if (!bTimeoutStarted) {
			Message msgMessage = new Message();
			timeoutHandle.sendMessageDelayed(msgMessage, 1000);
			bTimeoutStarted = true;
		}
	}

	private void setViewVisible() {
		if (bProgress) {
			bProgress = false;
			progressView.setVisibility(View.INVISIBLE);
			mrlOsd.setVisibility(View.VISIBLE);
			if (nP2PMode == ContentCommon.PPPP_MODE_P2P_RELAY) {
				updateTimeout();
				mtvTimeout.setVisibility(View.VISIBLE);
				startTimeout();
			}
			getCameraParams();
		}
	}

	private Bitmap mBmp;
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			if (msg.what == 1 || msg.what == 2) {
				setViewVisible();
			}
			if (!isPTZPrompt) {
				isPTZPrompt = true;
				showToast("请按菜单键,进行云台控制");
			}

			switch (msg.what) {
			case 1: // h264
			{
				Log.d("tagggg", "h264");
				mgsfvPlay.setVisibility(View.VISIBLE);
				mivVideo.setVisibility(View.GONE);
				int width = getWindowManager().getDefaultDisplay().getWidth();
				int height = getWindowManager().getDefaultDisplay().getHeight();
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, width * 3 / 4);
					lp.gravity = Gravity.CENTER;
					mgsfvPlay.setLayoutParams(lp);
				} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
					lp.gravity = Gravity.CENTER;
					mgsfvPlay.setLayoutParams(lp);
				}
				myRender.writeSample(videodata, nVideoWidth, nVideoHeight);
				mivVideoStandard.setVisibility(View.GONE);

			}
				break;
			case 2: // JPEG
			{
				// ptzTakeVideo.setVisibility(View.GONE);
				mgsfvPlay.setVisibility(View.GONE);
				mBmp = BitmapFactory.decodeByteArray(videodata, 0, videoDataLen);
				if (mBmp == null) {
					Log.d(LOG_TAG, "bmp can't be decode...");
					bDisplayFinished = true;
					return;
				}

				nVideoWidth = mBmp.getWidth();
				nVideoHeight = mBmp.getHeight();

				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					mivVideoStandard.setVisibility(View.GONE);
					mivVideo.setVisibility(View.VISIBLE);
					mivVideo.setImageBitmap(mBmp);

				} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					mivVideoStandard.setImageBitmap(mBmp);
					mivVideoStandard.setVisibility(View.VISIBLE);
					mivVideo.setVisibility(View.GONE);
				}
				if (isTakepic) {
					isTakepic = false;
					// takePicture(mBmp);
				}

			}
				break;
			case 3: //
			{
				displayResolution();
			}
				break;
			}

			if (msg.what == 1 || msg.what == 2) {

				// showTimeStamp();
				bDisplayFinished = true;

				nPlayCount++;
				if (nPlayCount >= 100) {
					nPlayCount = 0;
				}
			}
		}

	};

	protected void displayResolution() {
		/*
		 * 0->640x480 1->320x240 2->160x120; 3->1280x720 4->640x360 5->1280x960
		 */

		String strCurrResolution = null;

		switch (nResolution) {
		case 0:// vga
			strCurrResolution = "640x480";
			break;
		case 1:// qvga
			strCurrResolution = "320x240";
			break;
		case 2:
			strCurrResolution = "160x120";
			break;
		case 3:// 720p
			strCurrResolution = "1280x720";
			break;
		case 4:
			strCurrResolution = "640x360";
			break;
		case 5:
			strCurrResolution = "1280x960";
			break;
		default:
			return;
		}
	}

	public void initExitPopupWindow2() {
		LayoutInflater li = LayoutInflater.from(this);
		View popv = li.inflate(R.layout.dialog_ipcamera_popup_d, null);
		Button btLoad = (Button) popv.findViewById(R.id.btLoad);
		Button btPhone = (Button) popv.findViewById(R.id.btPhone);
		popupWindow_about = new PopupWindow(popv, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		popupWindow_about.setAnimationStyle(R.style.AnimationPreview);
		popupWindow_about.setFocusable(true);
		popupWindow_about.setOutsideTouchable(true);
		popupWindow_about.setBackgroundDrawable(new ColorDrawable(0));
		btLoad.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				NativeCaller.PPPPCameraControl(SystemValue.deviceId, 36, 36);
				popupWindow_about.dismiss();
				mbtPtzResolutoin.setText("VGA");
				Log.d("VGA", "VGA");
			}
		});
		btPhone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NativeCaller.PPPPCameraControl(SystemValue.deviceId, 37, 37);
				popupWindow_about.dismiss();
				mbtPtzResolutoin.setText("QVGA");
				Log.d("VGA", "QVGA");
			}
		});
		popupWindow_about.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				popupWindow_about.dismiss();
			}
		});
		popupWindow_about.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_OUTSIDE) {
					popupWindow_about.dismiss();
				}
				return false;
			}
		});
	}

	private void getCameraParams() {
		NativeCaller.PPPPGetSystemParams(strDID, ContentCommon.MSG_TYPE_GET_CAMERA_PARAMS);
	}

	private Handler msgHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				Log.d("tag", "断线了");
				showToast("断线");
				finish();
			}
		}
	};

	private Handler msgStreamCodecHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (nStreamCodecType == ContentCommon.PPPP_STREAM_TYPE_JPEG) {
				// textCodec.setText("JPEG");
			} else {
				// textCodec.setText("H.264");
			}
		}
	};

	private void InitParams() {
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		nSurfaceHeight = dm.heightPixels;
		mtvOsd.setText(strName);
	}

	private void StartAudio() {
		synchronized (this) {
			AudioBuffer.ClearAll();
			audioPlayer.AudioPlayStart();
			NativeCaller.PPPPStartAudio(strDID);
		}
	}

	private void StopAudio() {
		synchronized (this) {
			if (null != audioPlayer) {
				audioPlayer.AudioPlayStop();
			}
			if (null != AudioBuffer) {
				AudioBuffer.ClearAll();
			}
			NativeCaller.PPPPStopAudio(strDID);
		}
	}

	protected void setResolution(int Resolution) {
		Log.d("tag", "setResolution resolution:" + Resolution);
		NativeCaller.PPPPCameraControl(strDID, 0, Resolution);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);

		mBaseMatrix = new Matrix();
		mSuppMatrix = new Matrix();
		mDisplayMatrix = new Matrix();
		mivVideoStandard.setImageMatrix(mDisplayMatrix);
	}

	private boolean isDown = false;
	private boolean isSecondDown = false;
	private float x1 = 0;
	private float x2 = 0;
	private float y1 = 0;
	private float y2 = 0;

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (!isDown) {
			x1 = event.getX();
			y1 = event.getY();
			isDown = true;
		}
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;
			originalScale = getScale();
			break;
		case MotionEvent.ACTION_POINTER_UP:

			break;
		case MotionEvent.ACTION_UP:
			if (Math.abs((x1 - x2)) < 25 && Math.abs((y1 - y2)) < 25) {

				if (resolutionPopWindow != null && resolutionPopWindow.isShowing()) {
					resolutionPopWindow.dismiss();
				}

				if (mPopupWindowProgress != null && mPopupWindowProgress.isShowing()) {
					mPopupWindowProgress.dismiss();
				}
				if (!isSecondDown) {
					if (!bProgress) {
						showTop();
						showBottom();
					}
				}
				isSecondDown = false;
			} else {
			}
			x1 = 0;
			x2 = 0;
			y1 = 0;
			y2 = 0;
			isDown = false;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			isSecondDown = true;
			oldDist = spacing(event);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;

		case MotionEvent.ACTION_MOVE:
			x2 = event.getX();
			y2 = event.getY();

			int midx = getWindowManager().getDefaultDisplay().getWidth() / 2;
			int midy = getWindowManager().getDefaultDisplay().getHeight() / 2;
			if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 0f) {
					float scale = newDist / oldDist;
					Log.d("scale", "scale:" + scale);
					if (scale <= 2.0f && scale >= 0.2f) {
						// zoomTo(originalScale * scale, midx, midy);
					}
				}
			}
		}

		return gt.onTouchEvent(event);
	}

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;

	private int mode = NONE;
	private float oldDist;
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private PointF start = new PointF();
	private PointF mid = new PointF();
	float mMaxZoom = 2.0f;
	float mMinZoom = 0.3125f;
	float originalScale;
	float baseValue;
	protected Matrix mBaseMatrix = new Matrix();
	protected Matrix mSuppMatrix = new Matrix();
	private Matrix mDisplayMatrix = new Matrix();
	private final float[] mMatrixValues = new float[9];

	protected void zoomTo(float scale, float centerX, float centerY) {
		Log.d("zoomTo", "zoomTo scale:" + scale);
		if (scale > mMaxZoom) {
			scale = mMaxZoom;
		} else if (scale < mMinZoom) {
			scale = mMinZoom;
		}

		float oldScale = getScale();
		float deltaScale = scale / oldScale;
		Log.d("deltaScale", "deltaScale:" + deltaScale);
		mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
		mivVideoStandard.setScaleType(ImageView.ScaleType.MATRIX);
		mivVideoStandard.setImageMatrix(getImageViewMatrix());
	}

	protected Matrix getImageViewMatrix() {
		mDisplayMatrix.set(mBaseMatrix);
		mDisplayMatrix.postConcat(mSuppMatrix);
		return mDisplayMatrix;
	}

	protected float getScale(Matrix matrix) {
		return getValue(matrix, Matrix.MSCALE_X);
	}

	protected float getScale() {
		return getScale(mSuppMatrix);
	}

	protected float getValue(Matrix matrix, int whichValue) {
		matrix.getValues(mMatrixValues);
		return mMatrixValues[whichValue];
	}

	private float spacing(MotionEvent event) {
		try {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		} catch (Exception e) {
		}
		return 0;
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		Log.d("tag", "onDown");
		return false;
	}

	private final int MINLEN = 80;
	private RelativeLayout mrlTopBag;
	private Animation showTopAnim;
	private Animation dismissTopAnim;
	private ImageButton mibtPtzHoriMirror;
	private ImageButton mibtPtzVertMirror;
	private ImageButton mibtPtzHoriTour;
	private ImageButton mibtPtzVertTour;
	private boolean isPTZPrompt;

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		float x1 = e1.getX();
		float x2 = e2.getX();
		float y1 = e1.getY();
		float y2 = e2.getY();

		float xx = x1 > x2 ? x1 - x2 : x2 - x1;
		float yy = y1 > y2 ? y1 - y2 : y2 - y1;

		if (xx > yy) {
			if ((x1 > x2) && (xx > MINLEN)) {// left
				NativeCaller.PPPPPTZControl(strDID, ContentCommon.CMD_PTZ_RIGHT);
			} else if ((x1 < x2) && (xx > MINLEN)) {// right
				NativeCaller.PPPPPTZControl(strDID, ContentCommon.CMD_PTZ_LEFT);
			}

		} else {
			if ((y1 > y2) && (yy > MINLEN)) {// down
				NativeCaller.PPPPPTZControl(strDID, ContentCommon.CMD_PTZ_DOWN);
			} else if ((y1 < y2) && (yy > MINLEN)) {// up
				NativeCaller.PPPPPTZControl(strDID, ContentCommon.CMD_PTZ_UP);
			}
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	public void showSureDialogPlay() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("   提示：");
		builder.setMessage("确认退出播放?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				SecurityIpCameraPlayPage.this.finish();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtBack:
			bManualExit = true;
			if (!bProgress) {
				if (isTakeVideo == true) {
					showToast("本地录像中，请先停止再退出！");
				} else {
					// showSureDialogPlay();
					finish();
				}
			}
			break;
		case R.id.ivUp:
			NativeCaller.PPPPPTZControl(strDID, ContentCommon.CMD_PTZ_UP);
			Log.d("tag", "up");
			break;
		case R.id.ivDown:
			NativeCaller.PPPPPTZControl(strDID, ContentCommon.CMD_PTZ_DOWN);
			Log.d("tag", "down");
			break;
		case R.id.ivLeft:
			NativeCaller.PPPPPTZControl(strDID, ContentCommon.CMD_PTZ_LEFT);
			Log.d("tag", "left");
			break;
		case R.id.ivRight:
			NativeCaller.PPPPPTZControl(strDID, ContentCommon.CMD_PTZ_RIGHT);
			Log.d("tag", "right");
			break;
		case R.id.ibtPtzHoriMirror:
			if (isHorizontalMirror) {
				mibtPtzHoriMirror.setBackgroundColor(0x00ffffff);
				isHorizontalMirror = false;
				NativeCaller.PPPPCameraControl(strDID, 5, ContentCommon.CMD_PTZ_ORIGINAL);
				Log.d("tag", "水平镜像还原：" + ContentCommon.CMD_PTZ_ORIGINAL);
			} else {
				isHorizontalMirror = true;
				mibtPtzHoriMirror.setBackgroundColor(0xff0044aa);
				NativeCaller.PPPPCameraControl(strDID, 5, ContentCommon.CMD_PTZ_HORIZONAL_MIRROR);
				Log.d("tag", "水平镜像：" + ContentCommon.CMD_PTZ_HORIZONAL_MIRROR);
			}
			break;
		case R.id.ibtPtzVertMirror:
			if (isVerticalMirror) {
				isVerticalMirror = false;
				mibtPtzVertMirror.setBackgroundColor(0x00ffffff);
				NativeCaller.PPPPCameraControl(strDID, 5, ContentCommon.CMD_PTZ_ORIGINAL);
				Log.d("tag", "垂直镜像还原：" + ContentCommon.CMD_PTZ_ORIGINAL);
			} else {
				isVerticalMirror = true;
				mibtPtzVertMirror.setBackgroundColor(0xff0044aa);
				NativeCaller.PPPPCameraControl(strDID, 5, ContentCommon.CMD_PTZ_VERTICAL_MIRROR);
				Log.d("tag", "垂直镜像：" + ContentCommon.CMD_PTZ_VERTICAL_MIRROR);
			}
			break;

		case R.id.ibtPtzHoriTour:
			if (isLeftRight) {
				mibtPtzHoriTour.setBackgroundColor(0x000044aa);
				isLeftRight = false;
				NativeCaller.PPPPPTZControl(strDID, ContentCommon.CMD_PTZ_LEFT_RIGHT_STOP);
				Log.d("tag", "水平巡视停止:" + ContentCommon.CMD_PTZ_LEFT_RIGHT_STOP);
			} else {
				mibtPtzHoriTour.setBackgroundColor(0xff0044aa);
				isLeftRight = true;
				NativeCaller.PPPPPTZControl(strDID, ContentCommon.CMD_PTZ_LEFT_RIGHT);
				Log.d("tag", "水平巡视开始:" + ContentCommon.CMD_PTZ_LEFT_RIGHT);
			}
			break;
		case R.id.ibtPtzVertTour:
			if (isUpDown) {
				mibtPtzVertTour.setBackgroundColor(0x000044aa);
				isUpDown = false;
				NativeCaller.PPPPPTZControl(strDID, ContentCommon.CMD_PTZ_UP_DOWN_STOP);
				Log.d("tag", "垂直巡视停止:" + ContentCommon.CMD_PTZ_UP_DOWN_STOP);
			} else {
				mibtPtzVertTour.setBackgroundColor(0xff0044aa);
				isUpDown = true;
				NativeCaller.PPPPPTZControl(strDID, ContentCommon.CMD_PTZ_UP_DOWN);
				Log.d("tag", "垂直巡视开始:" + ContentCommon.CMD_PTZ_UP_DOWN);
			}
			break;
		case R.id.ibtPtzAudio:
			dismissBrightAndContrastProgress();
			if (!isMcriophone) {
				if (bAudioStart) {
					Log.d("tag", "没有声音");
					bAudioStart = false;
					mibtPtzAudio.setImageResource(R.drawable.ipcamera_ptz_audio_off);
					StopAudio();
				} else {
					Log.d("tag", "有声音");
					bAudioStart = true;
					mibtPtzAudio.setImageResource(R.drawable.ipcamera_ptz_audio_on);
					StartAudio();
				}
			}
			break;
		case R.id.ibtPtzBrightness:
			if (mPopupWindowProgress != null && mPopupWindowProgress.isShowing()) {
				mPopupWindowProgress.dismiss();
				mPopupWindowProgress = null;
			}
			setBrightOrContrast(BRIGHT);
			break;
		case R.id.ibtPtzContrast:
			if (mPopupWindowProgress != null && mPopupWindowProgress.isShowing()) {
				mPopupWindowProgress.dismiss();
				mPopupWindowProgress = null;
			}
			setBrightOrContrast(CONTRAST);
			break;
		case R.id.btPtzResoluti:
			popupWindow_about.showAtLocation(mibtBack, Gravity.CENTER, 0, 0);
			break;
		// case R.id.ptz_resolution_jpeg_qvga:
		// dismissBrightAndContrastProgress();
		// resolutionPopWindow.dismiss();
		// nResolution = 1;
		// setResolution(nResolution);
		// Log.d("tag", "jpeg resolution:" + nResolution + " qvga");
		// break;
		// case R.id.ptz_resolution_jpeg_vga:
		// dismissBrightAndContrastProgress();
		// resolutionPopWindow.dismiss();
		// nResolution = 0;
		// setResolution(nResolution);
		// Log.d("tag", "jpeg resolution:" + nResolution + " vga");
		// break;
		// case R.id.ptz_resolution_h264_qvga:
		// dismissBrightAndContrastProgress();
		// resolutionPopWindow.dismiss();
		// nResolution = 1;
		// setResolution(nResolution);
		// Log.d("tag", "h264 resolution:" + nResolution + " qvga");
		// break;
		// case R.id.ptz_resolution_h264_vga:
		// dismissBrightAndContrastProgress();
		// resolutionPopWindow.dismiss();
		// nResolution = 0;
		// setResolution(nResolution);
		// Log.d("tag", "h264 resolution:" + nResolution + " vga");
		// break;
		// case R.id.ptz_resolution_h264_720p:
		// dismissBrightAndContrastProgress();
		// resolutionPopWindow.dismiss();
		// nResolution = 3;
		// setResolution(nResolution);
		// Log.d("tag", "h264 resolution:" + nResolution + " 720p");
		// break;
		case R.id.ibtPtzPlayMode:
			dismissBrightAndContrastProgress();
			switch (playmode) {
			case FULLSCREEN:
				mibtPtzPlayMode.setImageResource(R.drawable.ipcamera_ptz_playmode_enlarge);
				mibtPtzPlayMode.setBackgroundResource(R.drawable.ipcamera_ptz_takepic_selector);
				Log.d("tg", "magnify 1");
				playmode = STANDARD;
				break;
			case MAGNIFY:
				Log.d("tg", "STANDARD 2");
				playmode = FULLSCREEN;
				mibtPtzPlayMode.setImageResource(R.drawable.ipcamera_ptz_playmode_standard);
				mibtPtzPlayMode.setBackgroundResource(R.drawable.ipcamera_ptz_takepic_selector);
				break;
			case STANDARD:
				Log.d("tg", "FULLSCREEN 3");
				playmode = MAGNIFY;
				mibtPtzPlayMode.setImageResource(R.drawable.ipcamera_ptz_playmode_fullscreen);
				mibtPtzPlayMode.setBackgroundResource(R.drawable.ipcamera_ptz_takepic_selector);
				break;
			default:
				break;
			}
			break;
		case R.id.ibtPtzDefaultSet:
			dismissBrightAndContrastProgress();
			defaultVideoParams();
			break;
		}
	}

	private void dismissBrightAndContrastProgress() {
		if (mPopupWindowProgress != null && mPopupWindowProgress.isShowing()) {
			mPopupWindowProgress.dismiss();
			mPopupWindowProgress = null;
		}
	}

	private void showBottom() {
		if (isUpDownPressed) {
			isUpDownPressed = false;
			mllPtzOtherSetViewAnim.startAnimation(dismissAnim);
			mllPtzOtherSetViewAnim.setVisibility(View.GONE);
		} else {
			isUpDownPressed = true;
			mllPtzOtherSetViewAnim.startAnimation(showAnim);
			mllPtzOtherSetViewAnim.setVisibility(View.VISIBLE);
		}
	}

	private void setBrightOrContrast(final int type) {
		Log.i(LOG_TAG, "type:" + type + "  bInitCameraParam:" + bInitCameraParam);
		if (!bInitCameraParam) {
			return;
		}
		int width = getWindowManager().getDefaultDisplay().getWidth();
		LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dialog_ipcamera_bright_progress, null);
		SeekBar seekBar = (SeekBar) layout.findViewById(R.id.sbBright);
		seekBar.setMax(255);
		switch (type) {
		case BRIGHT:
			seekBar.setProgress(nBrightness);
			break;
		case CONTRAST:
			seekBar.setProgress(nContrast);
			break;
		default:
			break;
		}
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int progress = seekBar.getProgress();
				switch (type) {
				case BRIGHT:// 亮度
					nBrightness = progress;
					NativeCaller.PPPPCameraControl(strDID, BRIGHT, nBrightness);
					break;
				case CONTRAST:// 对比度
					nContrast = progress;
					NativeCaller.PPPPCameraControl(strDID, CONTRAST, nContrast);
					break;
				default:
					break;
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
			}
		});
		mPopupWindowProgress = new PopupWindow(layout, width / 2, 180);
		mPopupWindowProgress.showAtLocation(findViewById(R.id.flPlay), Gravity.TOP, 0, 0);
	}

	private MyRender myRender = null;
	private GLSurfaceView mgsfvPlay = null;
	private ImageView mivUp = null;
	private ImageView mivDown = null;
	private ImageView mivRight = null;
	private ImageView mivLeft = null;

	@Override
	protected void onDestroy() {
		NativeCaller.StopPPPPLivestream(strDID);
		StopAudio();
		if (myRender != null) {
			myRender.destroyShaders();
		}
		if (brodCast != null) {
			unregisterReceiver(brodCast);
		}
		Log.d("tag", "PlayActivity onDestroy");

		super.onDestroy();
	}

	/***
	 * BridgeService callback
	 * 
	 * **/
	@Override
	public void callBackCameraParamNotify(String did, int resolution, int brightness, int contrast, int hue, int saturation, int flip) {
		Log.d(LOG_TAG, "CameraParamNotify...did:" + did + " brightness: " + brightness + " resolution: " + resolution + " contrast: " + contrast + " hue: " + hue + " saturation: " + saturation + " flip: " + flip);
		Log.d("tag", "contrast:" + contrast + " brightness:" + brightness);
		nBrightness = brightness;
		nContrast = contrast;
		nResolution = resolution;
		Log.d("VGA", nResolution + "");
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (nResolution == 0) {
					// vga
					mbtPtzResolutoin.setText("VGA");
				} else if (nResolution == 3) {
					// 720
					mbtPtzResolutoin.setText("720P");
				} else if (nResolution == 1) {
					// 720
					mbtPtzResolutoin.setText("QVGA");
				}
			}
		});
		Message msg = new Message();
		msg.what = 3;
		mHandler.sendMessage(msg);

		bInitCameraParam = true;
	}

	/***
	 * BridgeService callback
	 * 
	 * **/
	@Override
	public void callBaceVideoData(byte[] videobuf, int h264Data, int len, int width, int height) {
		Log.d(LOG_TAG, "Call VideoData...h264Data: " + h264Data + " len: " + len + " videobuf len: " + videobuf.length + "width==" + nVideoWidth + "height==" + nVideoHeight);
		if (!bDisplayFinished) {
			Log.d(LOG_TAG, "return bDisplayFinished");
			return;
		}
		nVideoWidth = width;
		nVideoHeight = height;
		bDisplayFinished = false;
		videodata = videobuf;
		videoDataLen = len;
		Message msg = new Message();
		if (h264Data == 1) { // H264
			if (isTakepic) {
				isTakepic = false;
				byte[] rgb = new byte[width * height * 2];
				NativeCaller.YUV4202RGB565(videobuf, rgb, width, height);
				ByteBuffer buffer = ByteBuffer.wrap(rgb);
				mBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
				mBmp.copyPixelsFromBuffer(buffer);
				// takePicture(mBmp);
			}
			msg.what = 1;
		} else { // MJPEG
			msg.what = 2;
		}
		mHandler.sendMessage(msg);
	}

	/***
	 * BridgeService callback
	 * 
	 * **/
	@Override
	public void callBackMessageNotify(String did, int msgType, int param) {
		Log.d("tag", "MessageNotify did: " + did + " msgType: " + msgType + " param: " + param);
		if (bManualExit)
			return;

		if (msgType == ContentCommon.PPPP_MSG_TYPE_STREAM) {
			nStreamCodecType = param;
			Message msgMessage = new Message();
			msgStreamCodecHandler.sendMessage(msgMessage);
			return;
		}

		if (msgType != ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS) {
			return;
		}

		if (!did.equals(strDID)) {
			return;
		}

		Message msg = new Message();
		msg.what = 1;

		msgHandler.sendMessage(msg);
	}

	/***
	 * BridgeService callback
	 * 
	 * **/
	@Override
	public void callBackAudioData(byte[] pcm, int len) {
		Log.d(LOG_TAG, "AudioData: len :+ " + len);
		if (!audioPlayer.isAudioPlaying()) {
			return;
		}
		CustomBufferHead head = new CustomBufferHead();
		CustomBufferData data = new CustomBufferData();
		head.length = len;
		head.startcode = AUDIO_BUFFER_START_CODE;
		data.head = head;
		data.data = pcm;
		AudioBuffer.addData(data);
	}

	/***
	 * BridgeService callback
	 * 
	 * **/
	@Override
	public void callBackH264Data(byte[] h264, int type, int size) {
		Log.d("tag", "CallBack_H264Data" + " type:" + type + " size:" + size);
	}

	// =======================
	private void showToast(String str) {
		Toast.makeText(SecurityIpCameraPlayPage.this, str, Toast.LENGTH_SHORT).show();
	}

	// ========================================
	// private static final String STR_DID = "did";
	// private static final String STR_MSG_PARAM = "msgparam";
	// private int option = ContentCommon.INVALID_OPTION;
	//
	// private void connectIpCamera() {
	// if (option == ContentCommon.INVALID_OPTION) {
	// option = ContentCommon.ADD_CAMERA;
	// }
	// BridgeService.setIpcamClientInterface(this);
	// NativeCaller.Init();
	// new Thread(new StartPPPPThread()).start();
	// }
	//
	// class StartPPPPThread implements Runnable {
	// @Override
	// public void run() {
	// try {
	// Thread.sleep(100);
	// StartCameraPPPP();
	// } catch (Exception e) {
	//
	// }
	// }
	// }
	//
	// private void StartCameraPPPP() {
	// try {
	// Thread.sleep(100);
	// } catch (Exception e) {
	// }
	// int result = NativeCaller.StartPPPP(SystemValue.deviceId,
	// SystemValue.deviceName, SystemValue.devicePass);
	// Log.i("ip", "result:" + result);
	// }
	//
	// @Override
	// public void BSMsgNotifyData(String did, int type, int param) {
	// Log.d("ip", "type:" + type + " param:" + param);
	// Bundle bd = new Bundle();
	// Message msg = PPPPMsgHandlerFirst.obtainMessage();
	// msg.what = type;
	// bd.putInt(STR_MSG_PARAM, param);
	// bd.putString(STR_DID, did);
	// msg.setData(bd);
	// PPPPMsgHandlerFirst.sendMessage(msg);
	// if (type == ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS) {
	// Intent intentbrod = new Intent("drop");
	// intentbrod.putExtra("ifdrop", param);
	// sendBroadcast(intentbrod);
	// }
	// }
	//
	// @Override
	// public void BSSnapshotNotify(String did, byte[] bImage, int len) {
	// Log.i("ip", "BSSnapshotNotify---len" + len);
	// }
	//
	// @Override
	// public void callBackUserParams(String did, String user1, String pwd1,
	// String user2, String pwd2, String user3, String pwd3) {
	//
	// }
	//
	// @Override
	// public void CameraStatus(String did, int status) {
	//
	// }
	//
	// private Handler PPPPMsgHandlerFirst = new Handler() {
	// public void handleMessage(Message msg) {
	// Bundle bd = msg.getData();
	// int msgParam = bd.getInt(STR_MSG_PARAM);
	// int msgType = msg.what;
	// String did = bd.getString(STR_DID);
	// switch (msgType) {
	// case ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS:
	// switch (msgParam) {
	// case ContentCommon.PPPP_STATUS_CONNECTING:
	// // resStr = "正在连接";
	// showToast("正在连接");
	// break;
	// case ContentCommon.PPPP_STATUS_INITIALING:
	// // resStr = "已连接, 正在初始化";
	// showToast("已连接，正在初始化");
	// break;
	// case ContentCommon.PPPP_STATUS_ON_LINE:
	// // resStr = "在线";
	// showToast("连接成功");
	// NativeCaller.PPPPGetSystemParams(did, ContentCommon.MSG_TYPE_GET_PARAMS);
	// // 允许播放
	// initPlay();
	// break;
	// case ContentCommon.PPPP_STATUS_DISCONNECT:
	// // resStr = "断线";
	// showToast("断线");
	// break;
	// case ContentCommon.PPPP_STATUS_INVALID_ID:
	// // resStr = "ID号无效";
	// case ContentCommon.PPPP_STATUS_CONNECT_FAILED:
	// // resStr = "连接失败";
	// case ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE:
	// // resStr = "摄像机不在线";
	// case ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT:
	// // resStr = "连接超时";
	// case ContentCommon.PPPP_STATUS_CONNECT_ERRER:
	// // resStr = "密码错误";
	// showToast("无法连接");
	// NativeCaller.StopPPPP(did);
	// SecurityIpCameraPlayPage.this.finish();
	// break;
	// default:
	// showToast("未知状态");
	// NativeCaller.StopPPPP(did);
	// SecurityIpCameraPlayPage.this.finish();
	// // resStr = "未知状态";
	// break;
	// }
	// break;
	// case ContentCommon.PPPP_MSG_TYPE_PPPP_MODE:
	// break;
	// }
	// }
	// };
}
