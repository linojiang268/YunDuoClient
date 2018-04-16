package com.ydclient.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class IpCameraService {
	private String TAG = "IpCameraService";
	private DbHelper dbHelper;
	private SQLiteDatabase sdb;
	private Cursor cursor;
	private String table = "ipcamera_info";

	// private String sql;

	public IpCameraService(Context context) {
		dbHelper = new DbHelper(context);
	}

	public boolean insert(IpCameraInfo ipCameraInfo) {
		boolean result = false;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sb = new StringBuffer();
		sb.append("insert into ");
		sb.append(table);
		sb.append("(name,deviceId,user,pwd,msg) values(?,?,?,?,?)");
		try {
			sdb.execSQL(sb.toString(), new Object[] { ipCameraInfo.name, ipCameraInfo.deviceId, ipCameraInfo.user, ipCameraInfo.pwd, ipCameraInfo.msg });
			sdb.setTransactionSuccessful();
			result = true;
		} catch (SQLException e) {
			Log.e(TAG, "insert method fail");
			e.printStackTrace();
		} finally {
			sdb.endTransaction();
			sdb.close();
		}
		return result;
	}

	/**
	 * 根据id删除
	 * 
	 * @param id
	 */
	public boolean delete(int... idList) {
		boolean result = false;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sb = new StringBuffer();
		sb.append("delete from ");
		sb.append(table);
		sb.append(" where id=?");
		for (int i = 0; i < idList.length - 1; i++) {
			sb.append(" or id=?");
		}
		try {
			Object[] values = new Object[idList.length];
			for (int i = 0; i < idList.length; i++) {
				values[i] = idList[i];
			}
			sdb.execSQL(sb.toString(), values);
			// sdb.execSQL(sql, new Object[] { _id });
			sdb.setTransactionSuccessful();
			result = true;
		} catch (SQLException e) {
			Log.e(TAG, "delete method fail");
			e.printStackTrace();
		} finally {
			sdb.endTransaction();
			sdb.close();
		}
		return result;
	}

	public boolean update(IpCameraInfo ipCameraInfo) {
		boolean result = false;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sb = new StringBuffer();
		sb.append("update ");
		sb.append(table);
		sb.append(" set name=?,deviceId=?,user=?,msg=? where id=?");
		try {
			sdb.execSQL(sb.toString(), new Object[] { ipCameraInfo.name, ipCameraInfo.deviceId, ipCameraInfo.user, ipCameraInfo.msg, ipCameraInfo.id });
			sdb.setTransactionSuccessful();
			result = true;
		} catch (SQLException e) {
			Log.e(TAG, "update method fail");
			e.printStackTrace();
		} finally {
			sdb.endTransaction();
			sdb.close();
		}
		return result;
	}

	// 获取列表
	public List<IpCameraInfo> getList() {
		List<IpCameraInfo> list = null;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("select * from ipcamera_info");
		try {
			cursor = sdb.rawQuery(sbSql.toString(), null);
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String deviceId = cursor.getString(cursor.getColumnIndex("deviceId"));
				String user = cursor.getString(cursor.getColumnIndex("user"));
				String pwd = cursor.getString(cursor.getColumnIndex("pwd"));
				String msg = cursor.getString(cursor.getColumnIndex("msg"));
				if (null == list) {
					list = new ArrayList<IpCameraInfo>();
				}
				list.add(new IpCameraInfo(id, name, deviceId, user, pwd, msg));
			}
			cursor.close();
			sdb.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG, "getCount method fail");
			e.printStackTrace();
		} finally {
			sdb.endTransaction();
			sdb.close();
		}
		return list;
	}
	// /**
	// * 获取某些type的消息数目
	// *
	// * @return
	// */
	// public long getCountWithType(int... typeList) {
	// long count = -1;
	// sdb = dbHelper.getWritableDatabase();
	// sdb.beginTransaction();
	// StringBuffer sbSql = new StringBuffer();
	// sbSql.append("select count(*) from device_info where type=?");
	// for (int i = 0; i < typeList.length - 1; i++) {
	// sbSql.append(" or type=?");
	// }
	// try {
	// String[] values = new String[typeList.length];
	// for (int i = 0; i < typeList.length; i++) {
	// values[0] = String.valueOf(typeList[0]);
	// }
	// cursor = sdb.rawQuery(sbSql.toString(), values);
	// cursor.moveToFirst();
	// count = cursor.getLong(0);
	// cursor.close();
	// sdb.setTransactionSuccessful();
	// } catch (SQLException e) {
	// Log.e(TAG, "getCount method fail");
	// e.printStackTrace();
	// } finally {
	// sdb.endTransaction();
	// sdb.close();
	// }
	// return count;
	// }

}
