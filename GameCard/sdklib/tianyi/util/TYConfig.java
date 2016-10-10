package com.sdk.tianyi.util;

import com.lordcard.net.http.HttpURL;

public class TYConfig {

	/** 支付类型*/
	public static String PAY_CODE ="tele_ty";
	
	/** 后台订单生成地址 */
	public static String TYPAY_URL = HttpURL.HTTP_PATH + "game/esurfing/addPayOrder.sc";
//	/** 更新或获取支付内容地址 */
//	public static String TYPAY_CHANGE_URL = HttpURL.HTTP_PATH + "game/esurfing/getESurfingPayPoint.sc";
}
