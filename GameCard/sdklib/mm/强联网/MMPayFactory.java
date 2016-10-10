package com.sdk.mmpay;

import com.lordcard.constant.Database;
import com.lordcard.network.base.ThreadPool;
import com.sdk.mmpay.util.MMPayUtil;
import com.sdk.util.ISDKFactory;
import com.sdk.util.PaySite;
import com.sdk.util.vo.PayInit;
import com.sdk.util.vo.PayPoint;

/**
 * 移动ＭＭ强联网支付
 * @author yinhb
 * 2013-12-9 下午2:00:41
 */
public class MMPayFactory extends ISDKFactory {
	
	/**
	 * 支付初始化
	 */
	@Override
	public void loadPay(PayInit payInit) {
		MMPayUtil.init(payInit);
	}
	
	/**
	 * 支付
	 * @Title: goPay  
	 * @param  payPoint 具体的充值计费点
	 * @return void
	 * @throws
	 */
	@Override
	public void goPay(PayPoint payPoint,String paySiteTag) {
		if(payPoint == null) return;
		MMPayUtil.goPay(payPoint,paySiteTag);
	}
	
	@Override
	public String getPayCode() {
		return MMConfig.PAY_CODE;
	}

	@Override
	public void localPay(final PayPoint point, String paySiteTag) {
		try {
			ThreadPool.startWork(new Runnable() {

				public void run() {
					Database.currentActivity.runOnUiThread(new Runnable() {
						public void run() {
							MMPayUtil.mListener.setPayTo(PaySite.OFF_LINE);
							MMPayUtil.mListener.setPayPoint(point);
							MMPayUtil.purchase.order(Database.currentActivity,point.getValue(), 1, MMPayUtil.mListener);
						}
					});
				}
			});
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	
	}
}
