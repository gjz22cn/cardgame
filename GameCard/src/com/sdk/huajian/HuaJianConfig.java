package com.sdk.huajian;

import com.lordcard.network.http.HttpURL;

public class HuaJianConfig {
	
	/** 支付类型*/
	public static String PAY_CODE_UNION ="union_hj";
	public static String PAY_CODE_TELE ="tele_hj";
	
	/** 后台订单生成地址 */
	public static String LTPAY_URL = HttpURL.HTTP_PATH + "game/tele/addOrder.sc";
	
}
