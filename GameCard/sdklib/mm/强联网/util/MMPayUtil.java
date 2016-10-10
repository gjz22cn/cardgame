package com.sdk.mmpay.util;

import java.util.HashMap;
import java.util.Map;

import mm.purchasesdk.Purchase;
import android.content.Context;

import com.lordcard.common.exception.CrashApplication;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Database;
import com.lordcard.entity.JsonResult;
import com.lordcard.net.http.HttpRequest;
import com.lordcard.network.base.ThreadPool;
import com.lordcard.prerecharge.PrerechargeManager;
import com.lordcard.ui.view.Assistant;
import com.sdk.mmpay.MMConfig;
import com.sdk.util.PaySite;
import com.sdk.util.vo.PayInit;
import com.sdk.util.vo.PayPoint;

public class MMPayUtil {

	public static IAPListener mListener;
	public static Purchase purchase;

	/**
	 * 支付方式初始化
	 */
	public static void init(final PayInit payInit) {
		Database.currentActivity.runOnUiThread(new Runnable() {
			public void run() {
				try {
					Context ctx = CrashApplication.getInstance();
					mListener = new IAPListener(new IAPHandler(),payInit);
					purchase = Purchase.getInstance();
					String appId = payInit.getAppId();
					String appKey = payInit.getAppkey();
					purchase.setAppInfo(appId, appKey);
					purchase.init(ctx, mListener);
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
			if (MMConfig.PAY_ORDER != null)
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
					
					String resultJson = HttpRequest.addPayOrder(MMConfig.PAY_ORDER_URL, paramMap);
					JsonResult result = JsonHelper.fromJson(resultJson, JsonResult.class);
					if (JsonResult.SUCCESS.equals(result.getMethodCode())) {
						MMOrder mmorder = JsonHelper.fromJson(result.getMethodMessage(), MMOrder.class);
						MMConfig.PAY_ORDER = mmorder.getOrderNo();
						Database.currentActivity.runOnUiThread(new Runnable() {
							public void run() {
								mListener.setPayTo(PaySite.ON_LINE);
								mListener.setPayPoint(point);
								purchase.order(Database.currentActivity,point.getValue(), 1, mListener);
							}
						});
					} else {
						DialogUtils.mesTip(result.getMethodMessage(), true);
					}
				}
			});
		} catch (Exception e1) {
			MMConfig.PAY_ORDER = null;
			e1.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
//	/**
//	 * MM支付
//	 * @param payCode	支付计费点
//	 * @param rechargeMoney
//	 * @param payFromType
//	 * @param room
//	 */
//	public static void goPay(final String payCode, final String payFromType, final Room room) {
//		try {
//			if (MMConfig.PAY_ORDER != null)
//				return;
//			
//			ThreadPool.startWork(new Runnable() {
//
//				public void run() {
//					String money = MMConfig.CODE_MONEY.get(payCode);
//					// 先提交充值订单
//					Map<String, String> paramMap = new HashMap<String, String>();
//					paramMap.put("goodsName", payCode); //计费点
//					paramMap.put("money", money);
//					paramMap.put("payFromType", payFromType);
//					/**判断是不是预充值**/
//					if (payFromType != null && payFromType.equalsIgnoreCase(SDKConstant.PRRECHARGE) && null != PrerechargeManager.mPayRecordOrder.getPreOrderNo()) {
//						paramMap.put(PrerechargeManager.PRERECHARGE_ORDER_PARAMS_PREORDERNO, PrerechargeManager.mPayRecordOrder.getPreOrderNo());
//						paramMap.put(PrerechargeManager.PRERECHARGE_ORDER_PARAMS_PREORDERTYPE, "1");
//					}
//					if (Assistant.ASSID != null && Assistant.BTNCODE != null) {
//						paramMap.put("asstId", Assistant.ASSID);
//						paramMap.put("btnCode", Assistant.BTNCODE);
//						Assistant.ASSID = null;
//						Assistant.BTNCODE = null;
//					}
//					if (room != null) {
//						paramMap.put("payFromItem", room.getCode());
//					}
//					String resultJson = HttpRequest.addPayOrder(MMConfig.PAY_ORDER_URL, paramMap);
//					JsonResult result = JsonHelper.fromJson(resultJson, JsonResult.class);
//					if (JsonResult.SUCCESS.equals(result.getMethodCode())) {
//						MMOrder mmorder = JsonHelper.fromJson(result.getMethodMessage(), MMOrder.class);
//						MMConfig.PAY_ORDER = mmorder.getOrderNo();
//						MMConfig.PAY_POINT = payCode;
//						Database.currentActivity.runOnUiThread(new Runnable() {
//
//							public void run() {
//								purchase.order(Database.currentActivity, payCode, 1, mListener);
//							}
//						});
//					} else {
//						DialogUtils.mesTip(result.getMethodMessage(), true);
//					}
//				}
//			});
//		} catch (Exception e1) {
//			MMConfig.PAY_ORDER = null;
//			e1.printStackTrace();
//		}
//	}

//	/**
//	 * 根据金额获取计费点
//	 * @param money
//	 * @return
//	 */
//	public static String getPayCode(long money) {
//		String payCode = MMConfig.PAY_CODE_2;
//		if (money <= 2) {
//			payCode = MMConfig.PAY_CODE_2;
//		} else if (money > 2 && money <= 5) {
//			payCode = MMConfig.PAY_CODE_5;
//		} else if (money > 5 && money <= 8) {
//			payCode = MMConfig.PAY_CODE_8;
//		} else if (money > 8 && money <= 10) {
//			payCode = MMConfig.PAY_CODE_10;
//		} else if (money > 10 && money <= 15) {
//			payCode = MMConfig.PAY_CODE_15;
//		} else if (money > 15 && money <= 20) {
//			payCode = MMConfig.PAY_CODE_20;
//		} else if (money > 20 && money <= 25) {
//			payCode = MMConfig.PAY_CODE_25;
//		} else if (money > 25) {
//			payCode = MMConfig.PAY_CODE_30;
//		}
//		return payCode;
//	}
	
//	/**
//	 * 获取配置的金额
//	 * @param money
//	 * @return
//	 */
//	public static int getMatchMoney(int money){
//		if(Database.JOIN_ROOM != null && money <= 0){
//			long limit = Database.JOIN_ROOM.getLimit();
//			if (limit <= 50000) {
//				money = 5;
//			} else if (limit >= 50000 && limit < 80000) {
//				money = 8;
//			} else if (limit >= 80000 && limit < 100000) {
//				money = 10;
//			} else if (limit >= 100000 && limit < 150000) {
//				money = 15;
//			} else if (limit >= 150000 && limit < 200000) {
//				money = 20;
//			} else if (limit >= 200000 && limit < 250000) {
//				money = 25;
//			} else if (limit >= 250000) {
//				money = 30;
//			}
//		}
//		
//		int signMoney = Integer.parseInt(SDKConfig.SIGN_MONEY);
//		if(money <= 0){
//			money = signMoney;
//		}
//		return money;
//	}
}
