package com.sdk.sms.egame;

import com.lordcard.network.http.HttpURL;

public class EGameSMSConfig {
	
	/** 支付类型*/
	public static String PAY_CODE ="egame_sms";
	/** 当前支付的订单号 */
	public static String PAY_ORDER = null;
	
	/** 订单生成地址 */
	public static final String PAY_ORDER_URL = HttpURL.HTTP_PATH + "game/egame/addPayOrder.sc";

}
