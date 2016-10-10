package com.lordcard.common.upgrade;

import java.util.ArrayList;
import java.util.List;

public class UPVersion {
	/**强制升级所有版本标识*/
	public static final String UP_STRONG_ALL = "-1";  
	/**版本自由升级*/
	public static final String UP_ALL = "0";  
	/**当前版本号*/
	public static int versionCode = 0;  
	/** 版本名称*/
	public static String versionName = ""; 
	/**安装包名称*/
	public static String apkName = "";  
	/**需要强制升级的版本号别如 1,2,3 0自由选择升级 -1 强制升级所有版本 多个用英语逗号分隔*/
	public static String upcodes;
	public static List<String> infolis = new ArrayList<String>();
}
