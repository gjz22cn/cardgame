package com.sdk.vac;

import com.lordcard.net.http.HttpURL;

public class VACConfig {
	
	/** 支付类型*/
	public static String PAY_CODE ="union_wo";

	/** 后台订单生成地址 */
	public static String VACPAY_URL = HttpURL.HTTP_PATH + "game/vacpay/addPayOrder.sc";
}
