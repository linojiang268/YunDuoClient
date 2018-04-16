package com.ydclient.model;

import java.io.Serializable;

public class DeviceInfo implements Serializable {
	/**
	 * 数据库id
	 */
	public Integer _id = null;
	public Integer type;
	public String mark;
	public String name;
	public String content;

	public DeviceInfo() {
		super();
	}

	public DeviceInfo(Integer _id, Integer type, String mark, String name, String content) {
		super();
		this._id = _id;
		this.type = type;
		this.mark = mark;
		this.name = name;
		this.content = content;
	}

	public Integer get_id() {
		return _id;
	}

	public void set_id(Integer _id) {
		this._id = _id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
