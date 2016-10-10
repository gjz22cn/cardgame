/**
 * DatabaseHelper.java [v 1.0.0]
 * classes : store.db.DatabaseHelper
 * auth : yinhongbiao
 * time : 2012 2012-11-9 下午3:27:59
 */
package com.lordcard.common.mydb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.lordcard.common.exception.CrashApplication;
import com.lordcard.constant.Constant;

/**
 * store.db.DatabaseHelper
 * 
 * @author yinhb <br/>
 *         create at 2012 2012-11-9 下午3:27:59
 */
public class GameDBHelper {

	private static SQLiteDatabase sqLiteDatabase = null;

	public synchronized static SQLiteDatabase openOrCreate() {
		if(sqLiteDatabase == null){
			sqLiteDatabase = CrashApplication.getInstance().openOrCreateDatabase(Constant.DB_NAME, Context.MODE_PRIVATE, null);
		}
		return sqLiteDatabase;
	}
	
	/** 
	 * 判断某张表是否存在 
	 * @param tabName 表名 
	 * @return 
	 */
	public static boolean tableIsExist(String tableName) {
		boolean result = false;
		if (TextUtils.isEmpty(tableName)) {
			return false;
		}
		Cursor cursor = null;
		try {
			String sql = "select count(*) as c from Sqlite_master where type ='table' and name ='" + tableName.trim() + "' ";
			cursor = sqLiteDatabase.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void close() {
		if(sqLiteDatabase != null){
			sqLiteDatabase.close();
			sqLiteDatabase = null;
		}
	}
}
