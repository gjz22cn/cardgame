package com.sdk.util;

import com.lordcard.constant.Constant;
import com.lordcard.network.http.GameCache;

/**
 * 单机离线充值
 * @ClassName: OFFLinePay   
 * @Description: TODO 
 * @author yinhongbiao   
 * @date 2014-2-25 上午11:38:24
 */
public class OFFLinePay {
	
	/**
	 * 增加本地金豆
	 */
	public static void goPay(int money){
		int sumBean = 0;
		try {
			String localBean = GameCache.getStr(Constant.GAME_BEAN_CACHE);
			int bean = Integer.parseInt(localBean);
			sumBean = bean + money * 10000;
		} catch (Exception e) {
			sumBean = money * 10000;
		}
		GameCache.putStr(Constant.GAME_BEAN_CACHE,String.valueOf(sumBean));
	}
}
