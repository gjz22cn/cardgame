package com.lordcard.common.mydb;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lordcard.entity.ContactPeople;

public class PhoneDao {
	
	private static final String TABLE = "gl_phone";
	private static final String MD5_PHONE = "phone";
	
	/**
	 * 取所有的加密手机号
	 * 
	 * @param table
	 * @param columns
	 * @return
	 */
	public static List<String> queryAll(SQLiteDatabase sqLiteDatabase) {
		try {
			if(null ==sqLiteDatabase){
				return null;
			}
			List<String> md5PhoneList = new ArrayList<String>();
			Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						md5PhoneList.add(cursor.getString(cursor.getColumnIndex(MD5_PHONE)));
					} while (cursor.moveToNext());
				}
			}

			return md5PhoneList;

		} catch (Exception e) {
			// 表名不存在

			StringBuffer buffer = new StringBuffer();
			buffer.append("CREATE TABLE " + TABLE);
			buffer.append(" (");
			buffer.append(" _id INTEGER PRIMARY KEY,");
			buffer.append(" " + MD5_PHONE + " TEXT");

			/*
			 * for (String column : columns) { buffer.append(" "+column +
			 * " TEXT ,"); } buffer.deleteCharAt(buffer.length()-1);
			 */
			buffer.append(" )");
			sqLiteDatabase.execSQL(buffer.toString());
			return queryAll(sqLiteDatabase);
		}
	}

	/**
	 * 增加
	 * 
	 * @param people
	 */
	public static void add(ContactPeople people,SQLiteDatabase sqLiteDatabase) {
		if(null ==sqLiteDatabase){
			return ;
		}
		ContentValues values = new ContentValues();
		values.put(MD5_PHONE, people.getMd5Number());
		sqLiteDatabase.insert(TABLE, null, values);
	}

}
