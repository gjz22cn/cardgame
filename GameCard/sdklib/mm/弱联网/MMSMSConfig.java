package com.sdk.mmpay.sms;

import com.lordcard.net.http.HttpURL;

/**
 * 移动ＭＭ弱联网支付相关配置
 * @author yinhb
 * 2013-12-9 下午2:03:41
 */
public class MMSMSConfig {
	
	/** 支付类型*/
	public static String PAY_CODE ="mm_sms";
	/** 当前支付的订单号 */
	public static String PAY_ORDER = null;
	
	/** 订单生成地址 */
	public static final String PAY_ORDER_URL = HttpURL.HTTP_PATH + "game/mm/addPayOrder.sc";
//	/** 成功回调成地址 */
//	public static final String PAY_CALLBACK_URL = HttpURL.HTTP_PATH + "game/mm/callback.sc";
	
	
	
	
	
	
	
	

	
	
	
//	/** MM计费点*/
//	public static String PAY_POINT = null;
	
//	/** mm 短信          1直接发       0sdk 2点击后发	 */
//	public static final String SEND_SDK = "0";
//	public static final String SEND_DIRECT = "1";
//	public static final String SEND_CLICK = "2";
	
//	public static String SEND_TYPE = "0";
	
	/** 计费点对应金额 */
//	public static Map<String, String> CODE_MONEY = new HashMap<String, String>();
	
//	public static String PAY_CODE_1 = "30000283461201";
//	public static String PAY_CODE_2 = "30000283461202";
//	public static String PAY_CODE_4 = "30000283461203";
//	public static String PAY_CODE_5 = "30000283461204";
//	public static String PAY_CODE_8 = "30000283461205";
//	public static String PAY_CODE_10 = "30000283461206";
//	public static String PAY_CODE_15 = "30000283461207";
//	public static String PAY_CODE_20 = "30000283461208";
//	public static String PAY_CODE_25 = "30000283461209";
//	public static String PAY_CODE_30 = "30000283461210";
//	
//	static {
//		CODE_MONEY.put(PAY_CODE_2, "2");
//		CODE_MONEY.put(PAY_CODE_5, "5");
//		CODE_MONEY.put(PAY_CODE_8, "8");
//		CODE_MONEY.put(PAY_CODE_10, "10");
//		CODE_MONEY.put(PAY_CODE_15, "15");
//		CODE_MONEY.put(PAY_CODE_20, "20");
//		CODE_MONEY.put(PAY_CODE_25, "25");
//		CODE_MONEY.put(PAY_CODE_30, "30");
//	}
}
