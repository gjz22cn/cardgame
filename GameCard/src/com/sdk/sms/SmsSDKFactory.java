package com.sdk.sms;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import com.sdk.util.ISDKFactory;
import com.sdk.util.sms.SmsUtil;
import com.sdk.util.vo.PayInit;
import com.sdk.util.vo.PayPoint;

/**
 * 通用短信支付，直接发送短信
 * @author Administrator
 *
 */
public class SmsSDKFactory extends ISDKFactory {

	@Override
	public void loadPay(PayInit payInit) {
	}

	@Override
	public String getPayCode() {
		return SmsConfig.PAY_CODE_COMSMS;
	}

	@Override
	public void goPay(PayPoint payPoint, String paySiteTag) {
		if(payPoint == null) return;
		ComSmsPayUtils.goPay(payPoint, paySiteTag);
	}
	
	@Override
	public void localPay(PayPoint point, String paySite) {
		String value = point.getValue();
		if(TextUtils.isEmpty(value)) return;
		
		Map<String,String> param = new HashMap<String,String>();
		param.put("smsTag","offline_recharge");
		param.put("money",String.valueOf(point.getMoney()));
		//发送短信
		SmsUtil.sendOneSms(point.getSmsCall(),value,param);
	}
}
