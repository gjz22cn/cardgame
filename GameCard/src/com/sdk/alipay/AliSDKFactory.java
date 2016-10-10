/**
 * AliPayViewFactory.java [v 1.0.0]
 * classes : com.alipay.ui.AliPayViewFactory
 * auth : yinhongbiao
 * time : 2013 2013-3-20 下午3:07:09
 */
package com.sdk.alipay;

import android.content.Intent;
import android.os.Bundle;

import com.lordcard.constant.Database;
import com.lordcard.prerecharge.PrerechargeManager;
import com.sdk.alipay.ui.AlixPayActivity;
import com.sdk.util.ISDKFactory;
import com.sdk.util.PaySite;
import com.sdk.util.vo.PayInit;
import com.sdk.util.vo.PayPoint;

/**
 * 支付宝支付 com.alipay.ui.AliPayViewFactory
 * 
 * @author Administrator <br/>
 *         create at 2013 2013-3-20 下午3:07:09
 */
public class AliSDKFactory extends ISDKFactory {
	
	/**
	 * 支付宝支付配置加载
	 */
	@Override
	public void loadPay(PayInit payInit) {}
	
	public String getPayCode() {
		return AliConfig.PAY_CODE;
	}

	@Override
	public void goPay(PayPoint payPoint, String paySite) {
		AliConfig.PAY_MONEY = payPoint.getMoney();
		Intent intent = Database.currentActivity.getIntent();
		/**判断是不是预支付，如果是传入place**/
		if (PaySite.PREPARERECHARGE.equalsIgnoreCase(paySite)) {
			Bundle placeBundle = new Bundle();
			placeBundle.putString(PrerechargeManager.PRERECHARGE_ORDER_PLACE,paySite);
			intent.putExtras(placeBundle);
		}
		intent.setClass(Database.currentActivity, AlixPayActivity.class);
		Database.currentActivity.startActivity(intent);
	}
	
	@Override
	public void localPay(PayPoint payPoint, String paySiteTag) {
		
	}
}
