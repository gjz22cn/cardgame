package com.sdk.huajian.telecom;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import com.lordcard.common.util.ComUtils;
import com.sdk.huajian.HuaJianConfig;
import com.sdk.huajian.HuaJianSmsPay;
import com.sdk.util.ISDKFactory;
import com.sdk.util.sms.SmsUtil;
import com.sdk.util.vo.PayInit;
import com.sdk.util.vo.PayPoint;

/**
 * 华建电信短信支付
 */
public class TeleSDKFactory extends ISDKFactory {

	@Override
	public void loadPay(PayInit payInit) {
	}
	
	@Override
	public String getPayCode() {
		return HuaJianConfig.PAY_CODE_TELE;
	}

	@Override
	public void goPay(PayPoint payPoint, String paySite) {
		if(payPoint == null) return;
		HuaJianSmsPay.goPay(payPoint, paySite);
	}

	@Override
	public void localPay(PayPoint point, String paySite) {
		String value = point.getValue();
		if(TextUtils.isEmpty(value)) return;
		
		String [] cmdArr = value.split("_");

		String orderNo = "8888"+ComUtils.getNo();
		String smsText = cmdArr[0] + orderNo;
		
		Map<String,String> param = new HashMap<String,String>();
		param.put("smsTag","offline_recharge");
		param.put("money",String.valueOf(point.getMoney()));
		//发送短信
		SmsUtil.sendOneSms(point.getSmsCall(), smsText,param);
	}
	
}
