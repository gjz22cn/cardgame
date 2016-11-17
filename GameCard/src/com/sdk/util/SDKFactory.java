/**
 * PayFactory.java [v 1.0.0]
 * classes : com.lordcard.common.pay.PayFactory
 * auth : yinhongbiao
 * time : 2013 2013-3-20 下午2:45:50
 */
package com.sdk.util;

import android.text.TextUtils;
//import cn.egame.terminal.paysdk.EgamePay;

import com.lordcard.common.exception.CrashApplication;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.ui.LoginActivity;


/**
 * 支付方式工厂，获取当前的支付通道 com.lordcard.common.pay.PayFactory
 * 
 * @author Administrator <br/>
 *         create at 2013 2013-3-20 下午2:45:50
 */
public class SDKFactory {
	private SDKFactory() {}
		
	/**
	 * sdk接入的登录页面
	 * @throws
	 */
	public static Class<?> getLoginView() {
		return LoginActivity.class;
	}
}
