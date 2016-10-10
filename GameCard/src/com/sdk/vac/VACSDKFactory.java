package com.sdk.vac;

import com.sdk.util.ISDKFactory;
import com.sdk.util.vo.PayInit;
import com.sdk.util.vo.PayPoint;
import com.sdk.vac.util.VACPayUtil;

/**
 * 联通沃商店支付
 * @author yinhb
 * 2013-12-2 下午4:09:33
 */
public class VACSDKFactory extends ISDKFactory {

	@Override
	public void loadPay(PayInit payInit) {
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
	public void localPay(final PayPoint payPoint, String paySiteTag) {
//		final String gameName = Database.currentActivity.getResources().getString(R.string.app_name);
//		Database.currentActivity.runOnUiThread(new Runnable() {
//
//			public void run() {
//				String orderNo = ComUtils.getNo();
//				String companyName = Database.currentActivity.getResources().getString(R.string.company_name);
//				String phone = Database.currentActivity.getResources().getString(R.string.company_phone);
//				MultiModePay.getInstance().vacPay(Database.currentActivity,companyName, gameName, payPoint.getName(),phone, String.valueOf(payPoint.getMoney()),payPoint.getValue(),orderNo, new VacCallBack() {
//					public void VacResult(String status, String errorMsg) {
//						if (status.equals("00000")) {
//							DialogUtils.toastTip("支付成功");
//							OFFLinePay.goPay(payPoint.getMoney());
//						} else {
////							DialogUtils.toastTip("支付失败");
//						}
//					}
//				});
//			}
//		});
	}
}
