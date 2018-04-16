package com.ydclient.model;

public class CommandInfo {
	/**
	 * 数据库id
	 */
	public Integer _id = null;
	public Integer type;
	public String command;
	public String name;

	public CommandInfo() {
		super();
	}

	public CommandInfo(Integer _id, Integer type, String command, String name) {
		super();
		this._id = _id;
		this.type = type;
		this.command = command;
		this.name = name;
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

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
