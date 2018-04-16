package com.ydclient.db;

import java.io.Serializable;

/**
 * 
 * @author ouArea
 * 
 */
public class IpCameraInfo implements Serializable {
	/**
	 * 数据库id
	 */
	public Integer id = null;
	public String name;
	public String deviceId;
	public String user;
	public String pwd;
	public String msg = "0";

	public IpCameraInfo() {
		super();
	}

	public IpCameraInfo(Integer id, String name, String deviceId, String user, String pwd, String msg) {
		super();
		this.id = id;
		this.name = name;
		this.deviceId = deviceId;
		this.user = user;
		this.pwd = pwd;
		this.msg = msg;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
