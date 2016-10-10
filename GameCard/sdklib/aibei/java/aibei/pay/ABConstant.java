package com.sdk.aibei.pay;

import java.util.List;

import com.lordcard.net.http.HttpURL;

public class ABConstant {
	
	/** 后台订单生成地址 */
	public static String ABPAY_URL = HttpURL.GAME_PAY_SER + "/game/iapppay/addPayOrder.sc";
	/** 更新或获取支付内容地址 */
	public static String ABPAY_CHANGE_URL = HttpURL.GAME_PAY_SER + "/game/iapppay/getIapppayPoint.sc";
	
	/** 支付类型 */
	public static String AB_PAY_TYPE="10";
	/** 应用appid */
	public static String AB_APP_ID="10040400000001100404";
	//商品密钥
	public static String AB_APP_KEY = "QkM5MDQyNzNBQzc2QTgzMTRDRDg0QTBGREExMEJDNDRFN0VDQkYyM01UQTFOemd5TlRBNE1UQTNOVFF3T0RNNU9EY3JNall3TURjME16QTBOelF6TnpBME16UTBNemcyTVRJME1UVTROVFk1TURVeE5EWTJOemc1";
	
	public final static String ABPAY_CONTENT = "abpay_content"; // 微派支付内容存储参数

	public static String AB_MM_APPID="300002889573";
	
	public static String AB_MM_APPKEY="849D4A8DC2ED2CB8";
	
//	/**  后台控制爱贝支付	 */
	public static List<Iapppay> IAPPPAY_LIST=null;

}
