package com.ydclient.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 网络默认值、初始值
 * 
 * @author ouArea
 * 
 */
public class MyNetData {
	public final static String yunduoGroupAddress = "yunduoxmpp.ouarea.cc";
	public final static String yunduoGroupConference = "conference-yunduo.server.nightbar.cc";
	// public static String loginIp = "dachuang-server.oicp.net";
	public static String loginIp = null;
	public static int loginPort = -1;
	public static String loginPassword = null;
	// public static String connectIp = "127.0.0.1";
	// public static int connectPort = 6666;

	// --------------------------------------------------------------

	public static String httpAddress;
	public static int httpPort;

	public static String getDownLoadAddress(String pic_url) {
		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(httpAddress);
		sb.append(":");
		sb.append(httpPort);
		sb.append("/");
		sb.append(pic_url);
		return sb.toString();
	}

	// --------------------------------------------------------------
	public static final String ROOT_URL = "http://serviceapi.51obo.com/assets/upload/";
	public static final String[] ip = { "serviceapi.51obo.com", "192.168.1.229", "118.123.240.188", "192.168.1.110", "192.168.1.101", "118.123.240.187" };
	public static final String port = "80";
	public static final String urlRootPath = "";// CityLive
	public static final String urlFilePath = "index.php?";// test.php
	public static final int multiThreadNum = 3;
	public static final int socketTimeOut = 30 * 1000;
	public static final int connectTimeOut = 60 * 1000;
	public static final int defaultValue = -1; // 设置默认值，方便区分
	public static String session = ""; // session值

	public static void setSession(String new_session) {
		if (new_session != null && new_session.length() > 5) {
			session = new_session;
		}
	}

	// model为true表示直接访问，false表示服务器转发。
	public static void setModel(Context context, boolean model) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("model", model);
		editor.commit();
	}

	public static boolean isModel(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean("model", true);
	}
}
