package com.lordcard.common.bean;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import com.lordcard.common.mydb.DBHelper;
import com.lordcard.constant.CacheKey;
import com.lordcard.entity.GameUser;
import com.lordcard.network.http.GameCache;
import com.lordcard.ui.LoginActivity;

@SuppressLint("SimpleDateFormat")
public class AssistantBean {

	public static AssistantBean assistantBean = null;
	/* 表名 根据用户账号命名 */
	public final static String FIRST_NAME = "QIAN";
	public String TABLE_NAME = FIRST_NAME;
	/* 表对应的字段 */
	public final static String AS_ID = "id";//标示
	public final static String AS_ICON = "icon";//助手图标
	public final static String AS_SMALL_ICON = "xicon";//小助手图标
	public final static String AS_DISPLAY = "display";//显示方式 2：图片,1:文本
	public final static String AS_CONTENT = "content";//消息内容
	public final static String AS_BTNAC = "btnAc";//消息按钮json
	public final static String AS_LEVEL = "level";//消息优先级
	public final static String AS_TIME = "validTime";//有效期
	public final static String AS_CONDITION = "condition";//条件
	public final static String AS_TITLE = "title";//标题
	public final static String AS_JOINCODE = "joinCode";//条件
	public final static String AS_PUSHTIME = "pushTime";//推送时间
	public final static String AS_TYPE = "type";//游戏助理类型
	public final static String AS_ORDER = "aorder";//相同优先级后的排序，升序

	private AssistantBean() {
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		TABLE_NAME = FIRST_NAME + gu.getAccount();
	}

	/* 单例模式 */
	public synchronized static AssistantBean getInstance() {
		if (null == assistantBean) {
			assistantBean = new AssistantBean();
		}
		return assistantBean;
	}

	/* DDL操作 */
	// 创建表结构SQL
	public String createTable() {
//		String s=TABLE_NAME;
		return "create table if not exists " + TABLE_NAME + " (id integer primary key,icon text,xicon text,display integer,content text,btnAc text,level integer,validTime text,condition text,title text,joinCode text,pushTime text,type integer,aorder integer)";
		//		return "create table if not exists "+ TABLE_NAME+" (id integer primary key,icon text)";
		//		return "create table if not exists "+ TABLE_NAME+" (id integer primary key,icon text,validTime text)";
	}

	// 删除表结构SQL
	public String dropTable() {
		return "drop table if exists " + TABLE_NAME + " (id integer primary key,icon text,xicon text,display integer,content text,btnAc text,level integer,validTime text,condition text,title text,joinCode text,pushTime text,type integer,aorder integer)";
		//		return "drop table if exists " +TABLE_NAME+" (id integer primary key,icon text)";
		//		return "drop table if exists " +TABLE_NAME+" (id integer primary key,icon text,validTime text)";
	}

	/* DML操作 */
	/* 插入 */
	public long save(DBHelper dbHelper, ContentValues values, String[] id) {
		long size = 0;
		if (AssistantBean.getInstance().findList(LoginActivity.dbHelper, id).size() != 0) {
			return 0;
		} else {
			try {
				dbHelper.open();
				size = dbHelper.insert(TABLE_NAME, values);
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				dbHelper.close();
			}
			return size;
		}
	}

	/**
	 * 
	 * 
	 * 
	 * 更新
	 * 
	 * 
	 * 
	 * @param dbHelper
	 * 
	 * @param oldValues
	 *            一般为Map,可根据自己的实际情况调整
	 * 
	 * @param newValues
	 *            一般为String[],可根据自己的实际情况调整
	 * 
	 * @return 更新的记录
	 * 
	 * 
	 */
	public long update(DBHelper dbHelper, String[] Values, String[] newValues) {
		long size = 0;
		try {
			dbHelper.open();
			ContentValues values = new ContentValues();
			// values.put(DATA_ID, newValues[0]);
			values.put(AS_PUSHTIME, newValues[0]);
			size = dbHelper.update(TABLE_NAME, "id=?", Values, values);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			dbHelper.close();
		}
		return size;
	}

	public long updateJson(DBHelper dbHelper, String[] Values, String[] newValues) {
		long size = 0;
		try {
			dbHelper.open();
			ContentValues values = new ContentValues();
//			values.put(DATA_ID, newValues[0]);
			values.put(AS_ICON, newValues[0]);
			size = dbHelper.update(TABLE_NAME, "id=?", Values, values);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			dbHelper.close();
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
			dbHelper.close();
		}
		return b;
	}

	/* 获取level最大数据 */
	public List<HashMap<String, Object>> findInfo(DBHelper dbHelper) {
		Cursor cursor = null;
		List<HashMap<String, Object>> list = null;
		try {
			dbHelper.open();
			cursor = dbHelper.findInfo(TABLE_NAME, null, null, null, null, null, AS_LEVEL + " desc", null);
			list = cursor2List(cursor, dbHelper);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (null != cursor) {
				cursor.close();
			}
			if (null != dbHelper) {
				dbHelper.close();
			}
		}
		return list;
	}

	/* 获取数据库集合数据 */
	public List<HashMap<String, Object>> findList(DBHelper dbHelper) {
		List<HashMap<String, Object>> list = null;
		Cursor cursor = null;
		try {
			dbHelper.open();
			cursor = dbHelper.findList(TABLE_NAME, null, null, null, null, null, AS_LEVEL + " desc", "0,1");
			list = cursor2List(cursor, dbHelper);
		} catch (Exception e) {} finally {
			if (null != cursor) {
				cursor.close();
			}
			if (null != dbHelper) {
				dbHelper.close();
			}
		}
		return list;
	}

	/* 查找id */
	public List<HashMap<String, Object>> findList(DBHelper dbHelper, String[] id) {
		List<HashMap<String, Object>> list = null;
		Cursor cursor = null;
		try {
			dbHelper.open();
			cursor = dbHelper.findList(TABLE_NAME, null, "id=?", id, null, null, null, null);
			list = cursor2List(cursor, dbHelper);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			cursor.close();
			dbHelper.close();
		}
		return list;
	}

	/* 查找Type 根据level和order */
	public List<HashMap<String, Object>> findListType(DBHelper dbHelper, String[] type) {
		List<HashMap<String, Object>> list = null;
		Cursor cursor = null;
		try {
			dbHelper.open();
			cursor = dbHelper.findList(TABLE_NAME, null, "type=?", type, null, null, "level desc,aorder", "0,1");
			list = cursor2List(cursor, dbHelper);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			cursor.close();
			dbHelper.close();
		}
		return list;
	}

	/* 游标转换为集合 */
	private List<HashMap<String, Object>> cursor2List(Cursor cursor, DBHelper dbHelper) {
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		long str = Long.valueOf(formatter.format(curDate));
		/* 有记录 */
		while (cursor.moveToNext()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			String validTime = cursor.getString(cursor.getColumnIndex(AS_TIME));
			if (Long.valueOf(validTime) - str > 0) {
				int level = cursor.getInt(cursor.getColumnIndex(AS_LEVEL));
				int id = cursor.getInt(cursor.getColumnIndex(AS_ID));
				String icon = cursor.getString(cursor.getColumnIndex(AS_ICON));
				String xicon = cursor.getString(cursor.getColumnIndex(AS_SMALL_ICON));
				String display = cursor.getString(cursor.getColumnIndex(AS_DISPLAY));
				String content = cursor.getString(cursor.getColumnIndex(AS_CONTENT));
				String btnAc = cursor.getString(cursor.getColumnIndex(AS_BTNAC));
				String condition = cursor.getString(cursor.getColumnIndex(AS_CONDITION));
				String title = cursor.getString(cursor.getColumnIndex(AS_TITLE));
				String joinCode = cursor.getString(cursor.getColumnIndex(AS_JOINCODE));
				String pushTime = cursor.getString(cursor.getColumnIndex(AS_PUSHTIME));
				int type = cursor.getInt(cursor.getColumnIndex(AS_TYPE));
				int aorder = cursor.getInt(cursor.getColumnIndex(AS_ORDER));
				map.put(AS_ID, id);
				map.put(AS_ICON, icon);
				map.put(AS_SMALL_ICON, xicon);
				map.put(AS_DISPLAY, display);
				map.put(AS_CONTENT, content);
				map.put(AS_BTNAC, btnAc);
				map.put(AS_LEVEL, level);
				map.put(AS_TIME, validTime);
				map.put(AS_CONDITION, condition);
				map.put(AS_TITLE, title);
				map.put(AS_JOINCODE, joinCode);
				map.put(AS_PUSHTIME, pushTime);
				map.put(AS_TYPE, type);
				map.put(AS_ORDER, aorder);
				list.add(map);
			} else {
				String value[] = { validTime };
				delete(dbHelper, "validTime=?", value);
			}
		}
		//		while (cursor.moveToNext()) {
		//
		//			HashMap<String, Object> map = new HashMap<String, Object>();
		//			
		//			
		////				int level= cursor.getInt(cursor.getColumnIndex(AS_LEVEL));
		//				int id = cursor.getInt(cursor.getColumnIndex(AS_ID));
		//				String icon = cursor.getString(cursor.getColumnIndex(AS_ICON));
		////				String display = cursor.getString(cursor.getColumnIndex(AS_DISPLAY));
		////				String	content= cursor.getString(cursor.getColumnIndex(AS_CONTENT));
		////				String btnAc= cursor.getString(cursor.getColumnIndex(AS_BTNAC));
		////				String validTime= cursor.getString(cursor.getColumnIndex(AS_TIME));
		//				map.put(AS_ID, id);
		//				map.put(AS_ICON, icon);
		////				map.put(AS_DISPLAY, display);
		////				map.put(AS_CONTENT, content);
		////				map.put(AS_BTNAC, btnAc);
		////				map.put(AS_LEVEL, level);
		////				map.put(AS_TIME, validTime);
		//
		//				list.add(map);
		//			}
		return list;
	}
}
