package com.ydclient.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import frame.ydclient.socket.MyCon;

public class YDClientReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (null == intent) {
			return;
		}
		String action = intent.getAction();
		if (MyCon.RECEIVER_MSG.equals(action)) {
			int mark = intent.getIntExtra("mark", -1);
			int type = intent.getIntExtra("type", -1);
			String message = intent.getStringExtra("message");
			MyCon.handMessage(mark, type, message);
		}
		// else {
		// if (checkBackGroundLogin(context)) {
		// // 后台登录
		// Intent intent_has = new Intent(context, DCClientService.class);
		// intent_has.putExtra("background", "");
		// context.getApplicationContext().startService(intent_has);
		// }
		// }
	}

	// /**
	// * 检测是否符合后台登录条件
	// *
	// * @Title: checkBackGroundLogin
	// * @Description: TODO
	// * @param context
	// * @return
	// * @author: ouArea
	// * @return boolean
	// * @throws
	// */
	// private boolean checkBackGroundLogin(Context context) {
	// SharedPreferences sharedPreferences =
	// context.getSharedPreferences("user", Context.MODE_PRIVATE);
	// if (!sharedPreferences.getBoolean("BackGroundLogin", true)) {
	// return false;
	// }
	// if (!sharedPreferences.getBoolean("ThisLoginSuccess", false)) {
	// return false;
	// }
	// return true;
	// }
}
