package com.sdk.jifeng.pay;

import com.lordcard.net.http.HttpURL;

public class JFConstant {
	
	/** 后台订单生成地址 */
	public static String JFPAY_URL = HttpURL.GAME_PAY_SER + "game/gfen/addPayOrder.sc";
	/** 更新或获取支付内容地址 */
	public static String JFPAY_CHANGE_URL = HttpURL.GAME_PAY_SER + "game/gfen/getGfenPayPoint.sc";
	
	/** 支付类型 */
	public static String JF_PAY_TYPE="3";
	
	public final static String JFPAY_CONTENT = "jfpay_content"; // 微派支付内容存储参数

	/**  后台控制机锋支付	 */
	public static List<GFen> JFPAY_LIST=null;

}
