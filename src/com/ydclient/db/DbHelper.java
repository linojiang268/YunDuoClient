package com.ydclient.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库辅助类
 * 
 * @author ouArea
 * @date 2013-1-21
 */
public class DbHelper extends SQLiteOpenHelper {
	private final static String DB_NAME = "db_yunduo_datas";
	private final static int VERSION = 1;

	public DbHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建数据库、表
		String sql = "create table ipcamera_info(id Integer primary key,name text,deviceId text,user text,pwd text,msg text)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("ALTER TABLE user ADD grade integer");
	}

}
