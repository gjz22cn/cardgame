/**
 * DJConstant.java [v 1.0.0]
 * classes : com.djpay.util.DJConstant
 * auth : yinhongbiao
 * time : 2013 2013-3-21 上午11:16:05
 */
package com.sdk.dianjin.pay;

import com.lordcard.net.http.HttpURL;

/**
 * com.djpay.util.DJConstant
 * 
 * @author Administrator <br/>
 *         create at 2013 2013-3-21 上午11:16:05
 */
public class DJConstant {

	/** 后台订单生成地址 */
	public static String DJPAY_URL = HttpURL.GAME_PAY_SER + "game/djpay/addPayOrder.sc";
	public static int PAY_MONEY = 0;

	// 支付金额
	public static String DJPAY_2 = "2";
	public static String DJPAY_5 = "5";
	public static String DJPAY_10 = "10";
	public static String DJPAY_20 = "20";
	public static String DJPAY_30 = "30";
	public static String DJPAY_40 = "40";
	public static String DJPAY_50 = "50";

}
