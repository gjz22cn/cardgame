package com.sdk.mmpay.sms.util;

import java.util.HashMap;
import java.util.Map;

import mm.sms.purchasesdk.OnSMSPurchaseListener;
import mm.sms.purchasesdk.PurchaseCode;
import mm.sms.purchasesdk.SMSPurchase;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.lordcard.net.http.HttpRequest;
import com.lordcard.network.base.ThreadPool;
import com.sdk.mmpay.sms.MMSMSConfig;
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
public class IAPListener implements OnSMSPurchaseListener {

	private IAPHandler iapHandler;
	
	private PayInit payInit;
	private String payTo;
	private PayPoint payPoint;		//当前充值的计费点

	public IAPListener(IAPHandler iapHandler, PayInit payInit) {
		this.iapHandler = iapHandler;
		this.payInit = payInit;
	}

	/**
	 * 初始化完成
	 * @see mm.purchasesdk.OnPurchaseListener#onInitFinish(int)
	 */
	@Override
	public void onInitFinish(int code) {
		if (iapHandler != null) {
			Message message = iapHandler.obtainMessage(IAPHandler.INIT_FINISH);
			String result = SMSPurchase.getReason(code);
			message.obj = result;
			message.sendToTarget();
		}
	}

	/**
	 * 交易完成
	 */
	@Override
	public void onBillingFinish(int code, HashMap returnObj) {
		
		if (code == PurchaseCode.ORDER_OK || (code == PurchaseCode.ORDER_OK_TIMEOUT)) {
			billingFinishCallBack(returnObj);
		} else {
			//			if (iapHandler.context instanceof MMPayActivity) {
			//			} else {
			//				MMDialog.finishGameAcitivity();
			//			}
			MMSMSConfig.PAY_ORDER = null;
		}
	}

	/**
	 * 成功后回调
	 */
	public void billingFinishCallBack(HashMap returnObj) {
		if(PaySite.OFF_LINE.equals(this.payTo)){	//本地账户充值
			OFFLinePay.goPay(payPoint.getMoney());
			return;
		}
		
		if (returnObj != null) {
			String payCode = (String) returnObj.get(OnSMSPurchaseListener.PAYCODE);
			String tradeID = (String) returnObj.get(OnSMSPurchaseListener.TRADEID);
			Log.d("pay_result", payCode + "|" + tradeID);
			ThreadPool.startWork(new Runnable() {

				public void run() {
					try {
						if (MMSMSConfig.PAY_ORDER != null) {
							//订单充值完成
							Map<String, String> paramMap = new HashMap<String, String>();
							paramMap.put("orderNo", MMSMSConfig.PAY_ORDER); //订单号
							paramMap.put("status", "1"); //成功
							String callBack = payInit.getCallBack();
							if (TextUtils.isEmpty(callBack)) {
								return;
							}
							
							String result = HttpRequest.payCallBack(callBack, paramMap);
							if (result.equals(HttpRequest.SUCCESS_STATE)) {
								MMSMSConfig.PAY_ORDER = null;
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
					MMSMSConfig.PAY_ORDER = null;
				}
			});
		}
	}

	
	
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
