package com.ydclient.tool;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

public class ComTool {

	/**
	 * 获取手机型号
	 * 
	 * @Title: getPhoneModel
	 * @Description: TODO
	 * @return
	 * @author: ouArea
	 * @return String
	 * @throws
	 */
	public static String getPhoneModel() {
		return Build.MODEL;
	}

	/**
	 * 获取手机os版本号
	 * 
	 * @Title: getPhoneOSVersion
	 * @Description: TODO
	 * @return
	 * @author: ouArea
	 * @return String
	 * @throws
	 */
	public static String getPhoneOSVersion() {
		return "Android " + Build.VERSION.RELEASE;
	}

	/**
	 * 判断网络是否可用
	 * 
	 * @Title: isConnect
	 * @Description: TODO
	 * @param context
	 * @return
	 * @author: ouArea
	 * @return boolean
	 * @throws
	 */
	public static boolean isConnect(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (null != info && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			Log.v("check_net_error", e.toString());
		}
		return false;
	}

	/**
	 * 获取本机ip地址
	 * 
	 * @Title: getLocalIpAddress
	 * @Description: TODO
	 * @return
	 * @author: ouArea
	 * @return String
	 * @throws
	 */
	@SuppressWarnings("rawtypes")
	public static String getLocalIpAddress() {
		try {
			for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = (NetworkInterface) en.nextElement();
				for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("ComTool", ex.toString());
		}
		return null;
	}
}
