package com.ydclient.type;

public class TypeDevice {
	// 奇数是无反馈类型的设备
	public final static int TV_UN_FB = 10001;
	public final static int BLIND_UN_FB = 10003;
	public final static int AIR_UN_FB = 10005;
	public final static int LIGHT_UN_FB = 10007;
	public final static int SECURITY_UN_FB = 10009;
	// 偶数是有反馈类型的设备
	public final static int TV_FB = 10002;
	public final static int BLIND_FB = 10004;
	public final static int AIR_FB = 10006;
	public final static int LIGHT_FB = 10008;
	public final static int SECURITY_FB = 10010;
	public final static int SECURITY_FB_BLIND = 10012;
	public final static int SECURITY_FB_GAS = 10014;
	public final static int SECURITY_FB_SMOKE = 10016;
	public final static int SECURITY_FB_TEMPERATURE = 10018;
	public final static int SECURITY_FB_DOOR = 10020;

	// 红外控制设备
	public final static int INFRARED_FB = 10100;

	/**
	 * 是否有反馈
	 * 
	 * @return
	 */
	public static boolean hasFeedBack(int device_type) {
		if (device_type % 2 == 0) {
			return true;
		} else {
			return false;
		}
	}
}
