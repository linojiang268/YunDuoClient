package com.ydclient.activity;

import object.p2pipcam.nativecaller.NativeCaller;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ydclient.activity.BridgeService.IpcamClientInterface;
import com.ydclient.db.IpCameraInfo;
import com.ydclient.ipcamera.ContentCommon;
import com.ydclient.ipcamera.SystemValue;

public class SecurityIpCameraPreparePlayPage extends Activity implements IpcamClientInterface {
	private IpCameraInfo mIpCameraInfo;
	private ProgressDialog mProgressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if (null != intent && intent.hasExtra("IpCameraInfo")) {
			mIpCameraInfo = (IpCameraInfo) intent.getSerializableExtra("IpCameraInfo");
			mProgressDialog = new ProgressDialog(SecurityIpCameraPreparePlayPage.this);
			mProgressDialog.setMessage("状态检测中 请稍候");
			mProgressDialog.show();
			connectIpCamera();
			return;
		}
		showToast("页面错误");
		finish();
	}

	private void intentToPlay() {
		SystemValue.deviceName = mIpCameraInfo.user;
		SystemValue.deviceId = mIpCameraInfo.deviceId;
		SystemValue.devicePass = mIpCameraInfo.pwd;
		Intent intent = new Intent(SecurityIpCameraPreparePlayPage.this, SecurityIpCameraPlayPage.class);
		startActivity(intent);
		finish();
	}

	private void showToast(String str) {
		Toast.makeText(SecurityIpCameraPreparePlayPage.this, str, Toast.LENGTH_SHORT).show();
	}

	// ========================================
	private static final String STR_DID = "did";
	private static final String STR_MSG_PARAM = "msgparam";
	private int option = ContentCommon.INVALID_OPTION;

	private void connectIpCamera() {
		if (option == ContentCommon.INVALID_OPTION) {
			option = ContentCommon.ADD_CAMERA;
		}
		BridgeService.setIpcamClientInterface(this);
		NativeCaller.Init();
		new Thread(new StartPPPPThread()).start();
	}

	class StartPPPPThread implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(100);
				StartCameraPPPP();
			} catch (Exception e) {

			}
		}
	}

	private void StartCameraPPPP() {
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		int result = NativeCaller.StartPPPP(mIpCameraInfo.deviceId, mIpCameraInfo.user, mIpCameraInfo.pwd);
		Log.i("ip", "result:" + result);
	}

	@Override
	public void BSMsgNotifyData(String did, int type, int param) {
		Log.d("ip", "type:" + type + " param:" + param);
		Bundle bd = new Bundle();
		Message msg = PPPPMsgHandlerFirst.obtainMessage();
		msg.what = type;
		bd.putInt(STR_MSG_PARAM, param);
		bd.putString(STR_DID, did);
		msg.setData(bd);
		PPPPMsgHandlerFirst.sendMessage(msg);
		if (type == ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS) {
			Intent intentbrod = new Intent("drop");
			intentbrod.putExtra("ifdrop", param);
			sendBroadcast(intentbrod);
		}
	}

	@Override
	public void BSSnapshotNotify(String did, byte[] bImage, int len) {
		Log.i("ip", "BSSnapshotNotify---len" + len);
	}

	@Override
	public void callBackUserParams(String did, String user1, String pwd1, String user2, String pwd2, String user3, String pwd3) {

	}

	@Override
	public void CameraStatus(String did, int status) {

	}

	private Handler PPPPMsgHandlerFirst = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bd = msg.getData();
			int msgParam = bd.getInt(STR_MSG_PARAM);
			int msgType = msg.what;
			String did = bd.getString(STR_DID);
			switch (msgType) {
			case ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS:
				switch (msgParam) {
				case ContentCommon.PPPP_STATUS_CONNECTING:
					// resStr = "正在连接";
					// showToast("正在连接");
					break;
				case ContentCommon.PPPP_STATUS_INITIALING:
					// resStr = "已连接, 正在初始化";
					// showToast("已连接，正在初始化");
					break;
				case ContentCommon.PPPP_STATUS_ON_LINE:
					mProgressDialog.dismiss();
					// resStr = "在线";
					// showToast("连接成功");
					NativeCaller.PPPPGetSystemParams(did, ContentCommon.MSG_TYPE_GET_PARAMS);
					// 允许播放
					intentToPlay();
					break;
				case ContentCommon.PPPP_STATUS_DISCONNECT:
					// resStr = "断线";
					showToast("断线");
					break;
				case ContentCommon.PPPP_STATUS_INVALID_ID:
					// resStr = "ID号无效";
				case ContentCommon.PPPP_STATUS_CONNECT_FAILED:
					// resStr = "连接失败";
				case ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE:
					// resStr = "摄像机不在线";
				case ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT:
					// resStr = "连接超时";
				case ContentCommon.PPPP_STATUS_CONNECT_ERRER:
					mProgressDialog.dismiss();
					// resStr = "密码错误";
					showToast("无法连接");
					NativeCaller.StopPPPP(did);
					finish();
					break;
				default:
					mProgressDialog.dismiss();
					showToast("未知状态");
					NativeCaller.StopPPPP(did);
					SecurityIpCameraPreparePlayPage.this.finish();
					// resStr = "未知状态";
					break;
				}
				break;
			case ContentCommon.PPPP_MSG_TYPE_PPPP_MODE:
				break;
			}
		}
	};
}
