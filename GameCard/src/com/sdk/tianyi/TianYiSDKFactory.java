package com.sdk.tianyi;

import com.sdk.tianyi.util.TYConfig;
import com.sdk.tianyi.util.TYPayUtil;
import com.sdk.util.ISDKFactory;
import com.sdk.util.vo.PayInit;
import com.sdk.util.vo.PayPoint;

public class TianYiSDKFactory extends ISDKFactory {
	

	@Override
	public void loadPay(PayInit payInit) {
	}

	@Override
	public String getPayCode() {
		return TYConfig.PAY_CODE;
	}

	@Override
	public void goPay(PayPoint payPoint, String paySiteTag) {
		TYPayUtil.goPay(payPoint,paySiteTag);
	}
	
	@Override
	public void localPay(final PayPoint point,final String paySite) {
	}
}
