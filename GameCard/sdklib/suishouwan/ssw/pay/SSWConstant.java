package com.sdk.ssw.pay;

import java.util.List;

import com.lordcard.net.http.HttpURL;
import com.sdk.ssw.SSW;

public class SSWConstant {
	
	/** 后台订单生成地址 */
	public static String SSWPAY_URL = HttpURL.GAME_PAY_SER + "/game/ssw/addPayOrder.sc";
	/** 更新或获取支付内容地址 */
	public static String SSWPAY_CHANGE_URL = HttpURL.GAME_PAY_SER + "/game/ssw/getSSWPayPoint.sc";
	
	/** 支付类型 */
	public static String SSW_PAY_TYPE="12";
	
	public final static String SSWPAY_CONTENT = "sswpay_content"; // 支付内容存储参数
	
	public final static String SSW_ID="19";
	
	public final static String SSW_KEY="CPHIxXZHpWIgkzqUmqqeT8Jw8";
	
	public static List<SSW> SSW_LIST=null;

}
