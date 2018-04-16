package com.ydclient.model;

import java.io.Serializable;

/**
 * _id Integer primary key autoincrement,name text,isUsed Integer
 * 
 * @author tsanglaurence
 * 
 */
public class SceneInfo implements Serializable {
	/**
	 * 数据库id
	 */
	public Integer _id = null;
	public String name;
	public Integer isUsed;
	public String command;

	public SceneInfo() {
		super();
	}

	public SceneInfo(Integer _id, String name, Integer isUsed, String command) {
		super();
		this._id = _id;
		this.name = name;
		this.isUsed = isUsed;
		this.command = command;
	}

	public Integer get_id() {
		return _id;
	}

	public void set_id(Integer _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Integer isUsed) {
		this.isUsed = isUsed;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}
