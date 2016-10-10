package com.sdk.weipai.pay;

import java.util.List;

import com.lordcard.net.http.HttpURL;

public class WPConstant {
	
	/** 后台订单生成地址 */
	public static String WPPAY_URL = HttpURL.GAME_PAY_SER + "/game/wiipay/addPayOrder.sc";
	/** 更新或获取支付内容地址 */
	public static String WPPAY_CHANGE_URL = HttpURL.GAME_PAY_SER + "/game/wiipay/getWiipayPayPoint.sc";
	
	/** 支付类型 */
	public static String WP_PAY_TYPE="9";
	
	public final static String WPPAY_CONTENT = "wppay_content"; // 微派支付内容存储参数

	/**  后台控制微派支付	 */
	public static List<Wiipay> WIIPAY_LIST=null;

}
