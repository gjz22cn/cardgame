package com.sdk.vac;

import com.zzyddz.shui.R;

import com.lordcard.common.util.ComUtils;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.constant.Database;
import com.sdk.util.ISDKFactory;
import com.sdk.util.OFFLinePay;
import com.sdk.util.vo.PayInit;
import com.sdk.util.vo.PayPoint;
import com.sdk.vac.util.VACPayUtil;
import com.unicom.dcLoader.Utils;
import com.unicom.dcLoader.Utils.UnipayPayResultListener;

/**
 * 联通沃商店支付
 * @author yinhb
 * 2013-12-2 下午4:09:33
 */
public class VACSDKFactory extends ISDKFactory {

	@Override
	public void loadPay(PayInit payInit) {
		try {
			String appName = Database.currentActivity.getResources().getString(R.string.app_name);
			String companyName = Database.currentActivity.getResources().getString(R.string.company_name);
			String phone = Database.currentActivity.getResources().getString(R.string.company_phone);
			Utils.getInstances().init(Database.currentActivity,payInit.getAppkey(),payInit.getAppCode(), payInit.getAppId(),companyName, phone,appName,"uid",new PayResultListener());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getPayCode() {
		return VACConfig.PAY_CODE;
	}

	@Override
	public void goPay(PayPoint payPoint, String paySiteTag) {
		VACPayUtil.goPay(payPoint,paySiteTag);
	}

	@Override
	public void localPay(final PayPoint point, String paySiteTag) {

		try {
			Database.currentActivity.runOnUiThread(new Runnable() {
				public void run() {
					try {
						String orderId = ComUtils.randomStr(24);
						Utils.getInstances().setBaseInfo(Database.currentActivity, false, true,"");
						Utils.getInstances().pay(Database.currentActivity, point.getValue(),"", point.getName(), String.valueOf(point.getMoney()), orderId,new UnipayPayResultListener() {
							
							public void PayResult(String paycode, int flag, String desc) {
								if (flag == Utils.SUCCESS_SMS || flag == Utils.SUCCESS_3RDPAY) { //如果是短信发送成功或者延时超过指定时间，SDK都返回成功，开发者可以在在desc中可以看到成功结果的描述
									DialogUtils.toastTip("支付成功");
									OFFLinePay.goPay(point.getMoney());
								}
							}
						});
						
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			});
		} catch (Exception e) {}
	}
}
