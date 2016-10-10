/**
 * AliConstant.java [v 1.0.0]
 * classes : com.alipay.util.AliConstant
 * auth : yinhongbiao
 * time : 2013 2013-3-21 上午11:20:30
 */
package com.sdk.alipay;

import com.lordcard.network.http.HttpURL;

/**
 * com.alipay.util.AliConstant
 * 
 * @author Administrator <br/>
 *         create at 2013 2013-3-21 上午11:20:30
 */
public class AliConfig {

	/** 支付宝订单生成地址 */
	public static String ALIPAY_URL = HttpURL.HTTP_PATH + "game/alipay/addPayOrder.sc";

//	/** 充值数量 */
	public static int PAY_MONEY = 0;

	/** 支付宝下载地址 */
	public final static String ALI_APP_URL = "https://msp.alipay.com/x.htm";
	public static final String ALIPAY_PLUGIN_NAME = "alipay_plugin_2.5.0_1221.apk";

	public static String RSA_PRIVATE;
	public static String RSA_ALIPAY_PUBLIC;
	
	/** 支付类型*/
	public static String PAY_CODE ="alipay";

}
