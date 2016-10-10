package com.sdk.huajian.unicom;

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
 * 华建联通短信支付
 * @author Administrator
 *
 */
public class UnicomSDKFactory extends ISDKFactory {

	@Override
	public void loadPay(PayInit payInit) {
	}

	@Override
	public String getPayCode() {
		return HuaJianConfig.PAY_CODE_UNION;
	}

	@Override
	public void goPay(PayPoint payPoint, String paySiteTag) {
		if(payPoint == null) return;
		HuaJianSmsPay.goPay(payPoint, paySiteTag);
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
