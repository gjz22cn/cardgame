package com.lordcard.common.mydb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lordcard.common.exception.NetException;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.DateUtil;
import com.lordcard.constant.Database;

public class ExceptionDao {

	private static final String TABLE = "gl_exception";

	private static void createTable(SQLiteDatabase sqLite) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("create table " + TABLE);
		buffer.append(" (");
		//问题，请求地址,网络情况,时间,类型,apk版本，网络是否正常,网络情况 
		//		id,cause,url,netinfo,time,type,apk,netok
		buffer.append("id integer primary key,cause text,url text,netinfo text,time varchar(32),type varchar(20),apk varchar(20),netok varchar(10)");
		buffer.append(") ");
		sqLite.execSQL(buffer.toString());
	}

	/**
	 * 取所有的加密手机号
	 * 
	 * @param table
	 * @param columns
	 * @return
	 */
	public static List<NetException> queryAll(SQLiteDatabase sqLite) {
		try {
			if (null == sqLite) {
				return null;
			}

			if (!GameDBHelper.tableIsExist(TABLE)) {
				createTable(sqLite);
			}

			List<NetException> errList = new ArrayList<NetException>();

			Cursor c = sqLite.rawQuery("select * from " + TABLE, null);
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						//						id,cause,url,netinfo,time,type,apk,netok
						NetException error = new NetException();
						error.setId(c.getInt(c.getColumnIndex("id")));
						error.setCause(c.getString(c.getColumnIndex("cause")));
						error.setUrl(c.getString(c.getColumnIndex("url")));
						error.setNetinfo(c.getString(c.getColumnIndex("netinfo")));
						error.setTime(c.getString(c.getColumnIndex("time")));
						error.setType(c.getString(c.getColumnIndex("type")));
						error.setApk(c.getString(c.getColumnIndex("apk")));
						error.setNetok(c.getString(c.getColumnIndex("netok")));
						errList.add(error);
					} while (c.moveToNext());
				}
			}

			return errList;

		} catch (Exception e) {
			//			// 表名不存在
			//			createTable(sqLite);
			//			return queryAll(sqLite);
		}
		return null;
	}

	/**
	 * 增加
	 * 
	 * @param people
	 */
	public static void add(NetException exception) {
		//没有网络 默认不是异常
		if (!ActivityUtils.isNetworkAvailable()) {
			return;
		}
		
		if (Database.currentActivity == null) {
			return;
		}

		SQLiteDatabase sqLite = GameDBHelper.openOrCreate();
		if (null == sqLite) {
			return;
		}

		if (!GameDBHelper.tableIsExist(TABLE)) {
			createTable(sqLite);
		}

		//		id,cause,url,netinfo,time,type,apk,netok
		ContentValues values = new ContentValues();
		values.put("cause", exception.getCause());
		values.put("url", exception.getUrl());
		values.put("netinfo", ActivityUtils.getNetWorkInfo());
		values.put("time", DateUtil.formatNoCharDate(new Date()));
		values.put("type", exception.getType());
		values.put("apk", ActivityUtils.getVersionCode());
		values.put("netok", String.valueOf(ActivityUtils.isNetworkAvailable()));
		sqLite.insert(TABLE, null, values);
		GameDBHelper.close();
	}

	public static void delete(SQLiteDatabase sqLite, int id) {
		try {
			sqLite.delete(TABLE, "id=?", new String[] { String.valueOf(id) });
		} catch (Exception e) {
		}
	}
}
