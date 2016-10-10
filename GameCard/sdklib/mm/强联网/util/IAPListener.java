package com.sdk.mmpay.util;

import java.util.HashMap;
import java.util.Map;

import mm.purchasesdk.OnPurchaseListener;
import mm.purchasesdk.Purchase;
import mm.purchasesdk.PurchaseCode;
import android.os.Message;
import android.text.TextUtils;

import com.lordcard.net.http.HttpRequest;
import com.lordcard.network.base.ThreadPool;
import com.sdk.mmpay.MMConfig;
import com.sdk.util.OFFLinePay;
import com.sdk.util.PaySite;
import com.sdk.util.vo.PayInit;
import com.sdk.util.vo.PayPoint;

/**
 * 支付监听
 * 
 * @author Administrator
 * 
 */
@SuppressWarnings("rawtypes")
public class IAPListener implements OnPurchaseListener {

	private PayInit payInit;
	private IAPHandler iapHandler;
	
	private String payTo;
	private PayPoint payPoint;		//当前充值的计费点

	public IAPListener(IAPHandler iapHandler, PayInit payInit) {
		this.iapHandler = iapHandler;
		this.payInit = payInit;
	}

	@Override
	public void onAfterApply() {}

	@Override
	public void onAfterDownload() {}

	@Override
	public void onBeforeApply() {}

	@Override
	public void onBeforeDownload() {}

	/**
	 * 初始化完成
	 * @see mm.purchasesdk.OnPurchaseListener#onInitFinish(int)
	 */
	@Override
	public void onInitFinish(int code) {
		if (iapHandler != null) {
			Message message = iapHandler.obtainMessage(IAPHandler.INIT_FINISH);
			String result = Purchase.getReason(code);
			message.obj = result;
			message.sendToTarget();
		}
	}

	/**
	 * 交易完成
	 */
	@Override
	public void onBillingFinish(int code, HashMap arg1) {
		if (code == PurchaseCode.ORDER_OK || (code == PurchaseCode.AUTH_OK)) {
			billingFinishCallBack();
		} else {
			MMConfig.PAY_ORDER = null;
		}
	}

	@Override
	public void onQueryFinish(int code, HashMap arg1) {}

	/**
	 * 成功后回调
	 */
	public void billingFinishCallBack() {
		if(PaySite.OFF_LINE.equals(this.payTo)){	//本地账户充值
			OFFLinePay.goPay(payPoint.getMoney());
			return;
		}
		
		ThreadPool.startWork(new Runnable() {

			public void run() {
				try {
					if (MMConfig.PAY_ORDER != null) {
						//订单充值完成
						Map<String, String> paramMap = new HashMap<String, String>();
						paramMap.put("orderNo", MMConfig.PAY_ORDER); //订单号
						paramMap.put("status", "1"); //成功
						String callBack = payInit.getCallBack();
						if (TextUtils.isEmpty(callBack)) {
							return;
						}
						String result = HttpRequest.payCallBack(callBack, paramMap);
						if (result.equals(HttpRequest.SUCCESS_STATE)) {
							MMConfig.PAY_ORDER = null;
							if (iapHandler != null) {
								//成功后同步用户物品
								HttpRequest.getGameUserGoods(false);
								Message message = iapHandler.obtainMessage(IAPHandler.SUCCESS);
								message.obj = "充值成功";
								message.sendToTarget();
							}
						} else if (result.equals(HttpRequest.FAIL_STATE)) {
							if (iapHandler != null) {
								Message message = iapHandler.obtainMessage(IAPHandler.FAIL);
								message.obj = "充值失败";
								message.sendToTarget();
							}
						} else if (result.equals(HttpRequest.TOKEN_ILLEGAL)) {
							if (iapHandler != null) {
								Message message = iapHandler.obtainMessage(IAPHandler.FAIL_TOKENID);
								message.obj = "充值失败，无效的tokenid";
								message.sendToTarget();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				MMConfig.PAY_ORDER = null;
			}
		});
	}

	@Override
	public void onUnsubscribeFinish(int arg0) {}

	
	public String getPayTo() {
		return payTo;
	}

	
	public void setPayTo(String payTo) {
		this.payTo = payTo;
	}

	
	public PayPoint getPayPoint() {
		return payPoint;
	}

	
	public void setPayPoint(PayPoint payPoint) {
		this.payPoint = payPoint;
	}
	
}
