package com.sdk.sms;

import com.lordcard.network.http.HttpURL;

public class SmsConfig {
	
	/** 支付类型*/
	public static String PAY_CODE_COMSMS ="sms_com";
	/** 后台订单生成地址 */
	public static String COMSMS_URL = HttpURL.HTTP_PATH + "game/sms/addOrder.sc";
	
}
