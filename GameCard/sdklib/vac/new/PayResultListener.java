package com.sdk.vac;

import com.lordcard.common.util.DialogUtils;
import com.unicom.dcLoader.Utils;
import com.unicom.dcLoader.Utils.UnipayPayResultListener;

public class PayResultListener implements UnipayPayResultListener {

	@Override
	public void PayResult(String paycode, int flag, String desc) {
		if (flag == Utils.SUCCESS_SMS) { //如果是短信发送成功或者延时超过指定时间，SDK都返回成功，开发者可以在在desc中可以看到成功结果的描述
			DialogUtils.toastTip("支付成功");
		}else if (flag == Utils.SUCCESS_3RDPAY) { //SDK使用第三方支付返回成功
			DialogUtils.toastTip("支付成功");
			//添加发放道具的逻辑,desc中放支付方式
		} else if (flag == Utils.FAILED) {
			DialogUtils.toastTip("支付失败");
			//desc中放支付失败的原因
		} else if (flag == Utils.CANCEL) {
			DialogUtils.toastTip("支付取消");
			//desc中放在哪个界面取消
		} else if (flag == Utils.OTHERPAY) {
			//这里写调用联通之外的其他第三方支付方式的处理逻辑即可
		}
	}
}
