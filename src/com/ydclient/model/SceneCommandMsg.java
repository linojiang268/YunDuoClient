package com.ydclient.model;

import java.io.Serializable;

/**
 * _id Integer,d_id Integer,c_msg text,name text
 * 
 * @author ouArea
 * 
 */
public class SceneCommandMsg implements Serializable {
	/**
	 * 数据库id
	 */
	public Integer id = null;
	public Integer _id;
	public Integer d_id;
	public Integer c_type;
	public String c_msg;
	public String name;

	public SceneCommandMsg() {
		super();
	}

	public SceneCommandMsg(Integer id, Integer _id, Integer d_id, Integer c_type, String c_msg, String name) {
		super();
		this.id = id;
		this._id = _id;
		this.d_id = d_id;
		this.c_type = c_type;
		this.c_msg = c_msg;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer get_id() {
		return _id;
	}

	public void set_id(Integer _id) {
		this._id = _id;
	}

	public Integer getD_id() {
		return d_id;
	}

	public void setD_id(Integer d_id) {
		this.d_id = d_id;
	}

	public Integer getC_type() {
		return c_type;
	}

	public void setC_type(Integer c_type) {
		this.c_type = c_type;
	}

	public String getC_msg() {
		return c_msg;
	}

	public void setC_msg(String c_msg) {
		this.c_msg = c_msg;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
