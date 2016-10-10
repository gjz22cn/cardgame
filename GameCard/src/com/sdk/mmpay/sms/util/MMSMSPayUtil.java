package com.sdk.mmpay.sms.util;

import java.util.HashMap;
import java.util.Map;

import mm.sms.purchasesdk.PurchaseSkin;
import mm.sms.purchasesdk.SMSPurchase;
import android.content.Context;

import com.lordcard.common.exception.CrashApplication;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Database;
import com.lordcard.entity.JsonResult;
import com.lordcard.network.base.ThreadPool;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.prerecharge.PrerechargeManager;
import com.lordcard.ui.view.Assistant;
import com.sdk.mmpay.sms.MMSMSConfig;
import com.sdk.util.PaySite;
import com.sdk.util.vo.PayInit;
import com.sdk.util.vo.PayPoint;

public class MMSMSPayUtil {

	public static IAPListener mListener;
	public static SMSPurchase purchase;

	/**
	 * 支付方式初始化
	 */
	public static void init(final PayInit payInit) {

		Database.currentActivity.runOnUiThread(new Runnable() {
			public void run() {
				try {
					Context ctx = CrashApplication.getInstance();
					mListener = new IAPListener(new IAPHandler(),payInit);
					purchase = SMSPurchase.getInstance();
					String appId = payInit.getAppId();
					String appKey = payInit.getAppkey();
					purchase.setAppInfo(appId, appKey,PurchaseSkin.SKIN_SYSTEM_TWO);
					purchase.smsInit(ctx, mListener);
				} catch (Exception e) {}
			}
		});
	}

	/**
	 * mm支付开始支付
	 * @Title: goPay  
	 * @param  payPoint
	 * @return void
	 * @throws
	 */
	public static void goPay(final PayPoint point,final String paySiteTag){
		try {
			if (MMSMSConfig.PAY_ORDER != null)
				return;
			
			ThreadPool.startWork(new Runnable() {

				public void run() {
					// 先提交充值订单
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("goodsName", point.getName()); 	//购买的物品名称
					paramMap.put("money",String.valueOf(point.getMoney()));
					paramMap.put("payFromType",paySiteTag);		//充值的标识位
					/**判断是不是预充值**/
					if (PaySite.PREPARERECHARGE.equalsIgnoreCase(paySiteTag)  && null != PrerechargeManager.mPayRecordOrder.getPreOrderNo()) {
						paramMap.put(PrerechargeManager.PRERECHARGE_ORDER_PARAMS_PREORDERNO, PrerechargeManager.mPayRecordOrder.getPreOrderNo());
						paramMap.put(PrerechargeManager.PRERECHARGE_ORDER_PARAMS_PREORDERTYPE, "1");
					}
					if (Assistant.ASSID != null && Assistant.BTNCODE != null) {
						paramMap.put("asstId", Assistant.ASSID);
						paramMap.put("btnCode", Assistant.BTNCODE);
						Assistant.ASSID = null;
						Assistant.BTNCODE = null;
					}
					if (Database.JOIN_ROOM != null) {
						paramMap.put("payFromItem",Database.JOIN_ROOM.getCode());
					}
					
					String resultJson = HttpRequest.addPayOrder(MMSMSConfig.PAY_ORDER_URL, paramMap);
					JsonResult result = JsonHelper.fromJson(resultJson, JsonResult.class);
					if (JsonResult.SUCCESS.equals(result.getMethodCode())) {
						MMOrder mmorder = JsonHelper.fromJson(result.getMethodMessage(), MMOrder.class);
						MMSMSConfig.PAY_ORDER = mmorder.getOrderNo();
						Database.currentActivity.runOnUiThread(new Runnable() {
							public void run() {
								mListener.setPayTo(PaySite.ON_LINE);
								mListener.setPayPoint(point);
								purchase.smsOrder(Database.currentActivity, point.getValue(), mListener);
							}
						});
					} else {
						DialogUtils.mesTip(result.getMethodMessage(), true);
					}
				}
			});
		} catch (Exception e1) {
			MMSMSConfig.PAY_ORDER = null;
			e1.printStackTrace();
		}
	}
}
