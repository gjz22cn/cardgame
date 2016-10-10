package com.lordcard.common.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.lordcard.common.mydb.DBHelper;
import com.lordcard.constant.CacheKey;
import com.lordcard.entity.GameUser;
import com.lordcard.network.http.GameCache;
import com.lordcard.ui.LoginActivity;

public class DataCentreBean {

	public static DataCentreBean datacentrebean = null;
	/* 表名 根据用户账号命名 */
	public final static String FIRST_NAME = "MES";
	public String TABLE_NAME = FIRST_NAME;
	//标示类型 as（游戏助理）1（个人消息）0(系统公告)
	public final static String RACE_AS = "as";
	public final static String RACE_PR = "1";
	public final static String RACE_SYS = "0";
	/* 表对应的字段 */
	public final static String DATA_ID = "id";//标示
	public final static String DATA_RACE = "race";//标示类型 
	public final static String DATA_CONTENT = "content";//消息内容
	public final static String DATA_CLICK = "click";//点击标志 0没点击 1有点击
	public final static String DATA_TIME = "time";//消息发布时间
	public final static String DATA_TITLE = "title";//标题

	private DataCentreBean() {
		GameUser gu = (GameUser)GameCache.getObj(CacheKey.GAME_USER);
		TABLE_NAME = FIRST_NAME +gu.getAccount();
	}

	/* 单例模式 */
	public synchronized static DataCentreBean getInstance() {
		if (null == datacentrebean) {
			datacentrebean = new DataCentreBean();
		}
		return datacentrebean;
	}

	/* DDL操作 */
	// 创建表结构SQL
	public String createTable() {
		return "create table if not exists " + TABLE_NAME + " (id text primary key,content text,race text,click text,time integer,title text)";
	}

	// 删除表结构SQL
	public String dropTable() {
		return "drop table if exists " + TABLE_NAME + " (id integer primary key,content text,race text,click text,time integer)";
	}

	/* DML操作 */
	/* 插入 */
	public long save(DBHelper dbHelper, ContentValues values, String[] id) {
		long size = 0;
		if (DataCentreBean.getInstance().findListId(LoginActivity.dbHelper, id).size() != 0) {
			return 0;
		} else {
			try {
				dbHelper.open();
				size = dbHelper.insert(TABLE_NAME, values);
			} catch (Exception e) {
			} finally {
				if(null !=dbHelper){
					dbHelper.close();
				}
			}
			return size;
		}
	}

	/**
	 * 更新
	 * @param dbHelper
	 * @param Values 条件值
	 * @param newValues  一般为String[],可根据自己的实际情况调整
	 * @return 更新的记录
	 */
	public long update(DBHelper dbHelper, String[] Values, String[] newValues) {
		long size = 0;
		try {
			dbHelper.open();
			ContentValues values = new ContentValues();
			values.put(DATA_CLICK, newValues[0]);
			size = dbHelper.update(TABLE_NAME, "id=?", Values, values);
		} catch (Exception e) {
		} finally {
			if(null !=dbHelper){
				dbHelper.close();
			}
		}
		return size;
	}

	/* 删除 */
	public boolean delete(DBHelper dbHelper, String whereArs, String[] values) {
		boolean b = false;
		try {
			dbHelper.open();
			b = dbHelper.delete(TABLE_NAME, whereArs, values);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if(null !=dbHelper){
				dbHelper.close();
			}
		}
		return b;
	}

	/* 获取数据库集合数据 游戏助理用 */
	public List<HashMap<String, Object>> findList(DBHelper dbHelper, String[] race) {
		Cursor cursor = null;
		List<HashMap<String, Object>> list = null;
		try {
			dbHelper.open();
			cursor = dbHelper.findList(TABLE_NAME, null, "race=?", race, null, null, null, null);
			list = cursorList(cursor, dbHelper);
		} catch (Exception e) {
		} finally {
			if (null != cursor) {
				cursor.close();
			}
			if(null !=dbHelper){
				dbHelper.close();
			}
		}
		return list;
	}

	public List<HashMap<String, Object>> findListclick(DBHelper dbHelper, String[] click) {
		List<HashMap<String, Object>> list = null;
		Cursor cursor = null;
		try {
			dbHelper.open();
			cursor = dbHelper.findList(TABLE_NAME, null, "click=?", click, null, null, null, null);
			list = cursorList(cursor, dbHelper);
		} catch (Exception e) {
		} finally {
			if (null != cursor) {
				cursor.close();
			}
			if(null != dbHelper){
				dbHelper.close();
			}
		}
		return list;
	}

	/* 查找id */
	public List<HashMap<String, Object>> findListId(DBHelper dbHelper, String[] id) {
		List<HashMap<String, Object>> list = null;
		Cursor cursor = null;
		try {
			dbHelper.open();
			cursor = dbHelper.findList(TABLE_NAME, null, "id=?", id, null, null, null, null);
			list = cursorList(cursor, dbHelper);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (null != cursor) {
				cursor.close();
			}
			if(null !=dbHelper){
				dbHelper.close();
			}
		}
		return list;
	}

	/* 获取数据库集合数据 */
	public List<HashMap<String, Object>> findList2(DBHelper dbHelper, String[] race) {
		List<HashMap<String, Object>> list = null;
		Cursor cursor = null;
		try {
			dbHelper.open();
			cursor = dbHelper.findList(TABLE_NAME, null, "race=?", race, null, null, DATA_TIME + " desc", "0,30");
			list = cursorList(cursor, dbHelper);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (null != cursor) {
				cursor.close();
			}
			if(null !=dbHelper){
				dbHelper.close();
			}
		}
		return list;
	}

	/* 游标转换为集合 */
	private List<HashMap<String, Object>> cursorList(Cursor cursor, DBHelper dbHelper) {
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		/* 有记录 */
		while (cursor.moveToNext()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			String id = cursor.getString(cursor.getColumnIndex(DATA_ID));
			String content = cursor.getString(cursor.getColumnIndex(DATA_CONTENT));
			String race = cursor.getString(cursor.getColumnIndex(DATA_RACE));
			String click = cursor.getString(cursor.getColumnIndex(DATA_CLICK));
			String time = cursor.getString(cursor.getColumnIndex(DATA_TIME));
			String title = cursor.getString(cursor.getColumnIndex(DATA_TITLE));
			map.put(DATA_ID, id);
			map.put(DATA_CONTENT, content);
			map.put(DATA_CLICK, click);
			map.put(DATA_TIME, time);
			map.put(DATA_RACE, race);
			map.put(DATA_TITLE, title);
			list.add(map);
		}
		return list;
	}
}
