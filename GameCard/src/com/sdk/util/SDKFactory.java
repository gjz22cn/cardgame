/**
 * PayFactory.java [v 1.0.0]
 * classes : com.lordcard.common.pay.PayFactory
 * auth : yinhongbiao
 * time : 2013 2013-3-20 下午2:45:50
 */
package com.sdk.util;

import android.text.TextUtils;
import cn.egame.terminal.paysdk.EgamePay;

import com.lordcard.common.exception.CrashApplication;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.ui.CGChargeActivity;
import com.lordcard.ui.LoginActivity;
import com.sdk.group.GroupPayActivity;
import com.sdk.jd.sms.util.JDSMSPayUtil;
import com.sdk.sms.egame.EGameSMSpayUtil;
import com.sdk.util.vo.PayInit;
import com.sdk.util.vo.PayPoint;
import com.sdk.util.vo.PaySiteConfigItem;
import com.umeng.analytics.MobclickAgent;

/**
 * 支付方式工厂，获取当前的支付通道 com.lordcard.common.pay.PayFactory
 * 
 * @author Administrator <br/>
 *         create at 2013 2013-3-20 下午2:45:50
 */
public class SDKFactory {

	private static ISDKFactory sdkFactory;

	private SDKFactory() {}

	private static void initPaySDK(PayInit payInit) {
		try {
			if (sdkFactory == null) {
				String sdkName = payInit.getFactory();
				if (!TextUtils.isEmpty(sdkName)) {
					Object viewObject = Class.forName(sdkName).newInstance();
					sdkFactory = (ISDKFactory) viewObject;
					sdkFactory.loadPay(payInit);
				}
			}
		} catch (Exception e) {}
	}

	/**
	 * 进行支付
	 * @Title: goPay  
	 * @param  payInit	支付方式对应的始始数据
	 * @param  payPoint 具体的充值计费点
	 * @return void
	 * @throws
	 */
	public static void goPay(PayInit payInit,PayPoint payPoint,String paySite){
		try {
//			DialogUtils.mesToastTip("支付组件加载中，请稍候...");
			
			initPaySDK(payInit);
			
			PaySiteConfigItem configItem = PayUtils.getPaySiteUseConfig(paySite);
			if(configItem == null){
				return ;
			}
			
			String payTo = configItem.getPayTo();
			String vn = ActivityUtils.getVersionName();
			if(TextUtils.isEmpty(payTo) || PaySite.ON_LINE.equals(payTo)){		
				if(CGChargeActivity.isYd(JDSMSPayUtil.getContext()))
				{
					JDSMSPayUtil.goPay(payPoint, paySite);//直接走基地
				}else
				{
					EGameSMSpayUtil.goPay(payPoint, paySite);
				}
				/*int SMS_Type = JDSMSPayUtil.getOperators();
				switch(SMS_Type)
				{
				case 0://中国移动
					//基地充值
					JDSMSPayUtil.goPay(payPoint, paySite);//直接走基地
					break;
				case 1://中国电信
					EGameSMSpayUtil.goPay(payPoint, paySite);
					break;
				case 2://中国联通
					break;
				}*/
				//sdkFactory.goPay(payPoint,paySite);
				//电信充值
				//支付宝
			}
			/*if(TextUtils.isEmpty(payTo) || PaySite.ON_LINE.equals(payTo)){		
				sdkFactory.goPay(payPoint,paySite);
			}else if(PaySite.OFF_LINE.equals(payTo)){//单机账号充值
				sdkFactory.localPay(payPoint,paySite);
			}
			MobclickAgent.onEvent(CrashApplication.getInstance(),"游戏单机充值_"+vn+"_ "+paySite);
		    */
		} catch (Exception e) {}
	}
	
	
	/**
	 * sdk接入的登录页面
	 * @throws
	 */
	public static Class<?> getLoginView() {
		return LoginActivity.class;
	}
	
	/**
	 * 默认支付页面
	 * 
	 * @param ctx
	 * @return
	 */
	public static Class<?> getPayView() {
		return GroupPayActivity.class;
	}
}
