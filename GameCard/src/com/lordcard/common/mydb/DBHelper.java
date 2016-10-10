package com.lordcard.common.mydb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lordcard.common.bean.AssistantBean;
import com.lordcard.common.bean.DataCentreBean;

public class DBHelper {

	/* DDL,定义数据结构 */

	private DataBaseHelper dbHelper;

	/* DML，数据库操作 */

	private SQLiteDatabase db;

	/* 数据库名 */

	private final static String DATABASE_NAME = "qianqianyou";

	/* 版本号 */

	private final static int DATABASE_VERSION = 1;

	/* 上下文 */

	private Context mcontext;

	public DBHelper(Context mcontext) {

		super();

		this.mcontext = mcontext;

	}

	/* 静态内部类,针对DDL */

	private static class DataBaseHelper extends SQLiteOpenHelper {

		public DataBaseHelper(Context context) {

			super(context, DATABASE_NAME, null, DATABASE_VERSION);

		}

		/* 创建表结构 */

		@Override
		public void onCreate(SQLiteDatabase db) {

			// db.execSQL("create table if not exists stu(id integer primary key,name text)");
			db.execSQL(AssistantBean.getInstance().createTable());

			db.execSQL(DataCentreBean.getInstance().createTable());
			// db.execSQL("insert into stu values(1,'a1')");

			// db.execSQL("insert into stu values(2,'a2')");

		}

		/* 针对数据库升级 */

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}

	}

	/* 打开数据库,如果已经打开就使用，否则创建 */

	public synchronized  DBHelper open() {

		dbHelper = new DataBaseHelper(mcontext);

		db = dbHelper.getWritableDatabase();
		
		db.execSQL(AssistantBean.getInstance().createTable());

		db.execSQL(DataCentreBean.getInstance().createTable());
		return this;

	}

	/* 关闭数据库 */

	public synchronized void close() {

		db.close();// 先关DML

		dbHelper.close();// DDL

	}

	/* 插入 */

	public long insert(String tableName, ContentValues values) {

		return db.insert(tableName, null, values);

	}

	/**
	 * 
	 * 
	 * 
	 * 更新
	 * 
	 * 
	 * 
	 * @param tableName
	 *            表名
	 * 
	 * @param whereClause
	 *            条件
	 * 
	 * @param whereArgs
	 *            条件值
	 * 
	 * @param values
	 *            更新值
	 * 
	 * @return 更新的条数
	 * 
	 * 
	 */

	public long update(String tableName, String whereClause, String[] whereArgs, ContentValues values) {

		return db.update(tableName, values, whereClause, whereArgs);

	}

	/* 删除 */

	public boolean delete(String tableName, String whereClause, String[] whereArgs) {

		return db.delete(tableName, whereClause, whereArgs) > 0;

	}

	/**
	 * 
	 * 
	 * 
	 * 查询，多条记录
	 * 
	 * 
	 * 
	 * @param tableName
	 *            表名
	 * 
	 * @param columns
	 *            列名
	 * 
	 * @param selection
	 *            条件
	 * 
	 * @param selectionArgs
	 *            条件值
	 * 
	 * @param groupBy
	 *            分组
	 * 
	 * @param having
	 *            过滤
	 * 
	 * @param orderBy
	 *            排序
	 * 
	 * @param limit
	 *            分页(2,3),从第二条记录开始，向下取三条记录
	 * 
	 * @return 动态游标
	 * 
	 * 
	 */

	public Cursor findList(String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy,

	String having, String orderBy, String limit) {

		return db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

	}

	/**
	 * 
	 * 
	 * 
	 * 精确查询，返回一条数据
	 * 
	 * 
	 * 
	 * @param tableName
	 *            表名
	 * 
	 * @param columns
	 *            列名
	 * 
	 * @param selection
	 *            条件
	 * 
	 * @param selectionArgs
	 *            条件值
	 * 
	 * @param groupBy
	 *            分组
	 * 
	 * @param having
	 *            过滤
	 * 
	 * @param orderBy
	 *            排序
	 * 
	 * @param limit
	 *            分页(2,3),从第二条记录开始，向下取三条记录
	 * 
	 * @return 动态游标
	 * 
	 * 
	 */

	public Cursor findInfo(String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy,

	String having, String orderBy, String limit) {

		Cursor cursor = db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

		//		while (cursor.moveToNext()) {
		//
		//			cursor.moveToFirst();
		//
		//		}

		return cursor;

	}

	/* 执行sql方法 */

	public void executeSql(String sql) {

		db.execSQL(sql);

	}

}
