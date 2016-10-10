package com.sdk.vac.util;

import com.zzyddz.shui.R;

import java.util.HashMap;
import java.util.Map;

import com.infinit.multimode_billing5.net.MultimodeConfig;
import com.infinit.multimode_billing_vac.ui.MultiModePay;
import com.infinit.multimode_billing_vac.ui.MultiModePay.VacCallBack;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Database;
import com.lordcard.entity.JsonResult;
import com.lordcard.net.http.HttpRequest;
import com.lordcard.network.base.ThreadPool;
import com.lordcard.prerecharge.PrerechargeManager;
import com.lordcard.ui.view.Assistant;
import com.sdk.util.PaySite;
import com.sdk.util.vo.PayPoint;
import com.sdk.vac.VACConfig;
import com.sdk.vac.pay.VacPayOrder;

public class VACPayUtil {
	
	public static void goPay(final PayPoint point,final String paySiteTag){

		try {
			ThreadPool.startWork(new Runnable() {

				public void run() {
					// 先提交充值订单
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("goodsName", point.getName()); 	//购买的物品名称
					paramMap.put("money",String.valueOf(point.getMoney()));
					paramMap.put("payFromType",paySiteTag);		//充值的标识位
					paramMap.put("payNo","2");

					/**判断是不是预充值**/
					if (PaySite.PREPARERECHARGE.equalsIgnoreCase(paySiteTag)  && null != PrerechargeManager.mPayRecordOrder.getPreOrderNo()) {
						paramMap.put(PrerechargeManager.PRERECHARGE_ORDER_PARAMS_PREORDERNO, PrerechargeManager.mPayRecordOrder.getPreOrderNo());
						paramMap.put(PrerechargeManager.PRERECHARGE_ORDER_PARAMS_PREORDERTYPE, "1");
					}
					
					if(Assistant.ASSID != null && Assistant.BTNCODE != null){
					
						paramMap.put("asstId", Assistant.ASSID);
						paramMap.put("btnCode", Assistant.BTNCODE);
						Assistant.ASSID = null;
						Assistant.BTNCODE = null;
					}
					if (Database.JOIN_ROOM != null) {
						paramMap.put("payFromItem",Database.JOIN_ROOM.getCode());
					}
					String resultJson = HttpRequest.addPayOrder(VACConfig.VACPAY_URL, paramMap);
					JsonResult result = JsonHelper.fromJson(resultJson, JsonResult.class);
					if (JsonResult.SUCCESS.equals(result.getMethodCode())) {
						final VacPayOrder payOrder = JsonHelper.fromJson(result.getMethodMessage(), VacPayOrder.class);
						final String gameName = Database.currentActivity.getResources().getString(R.string.app_name);
						Database.currentActivity.runOnUiThread(new Runnable() {

							public void run() {
								MultimodeConfig.setCallbackUrl(payOrder.getPostURL()); // 服务器回调接口
								MultiModePay.getInstance().vacPay(Database.currentActivity, payOrder.getCompany(), gameName, payOrder.getProductName(), payOrder.getPhone(), String.valueOf(point.getMoney()), payOrder.getPayNo(), payOrder.getOriderid(), new VacCallBack() {

									public void VacResult(String status, String errorMsg) {
										if (status.equals("00000")) {
											DialogUtils.toastTip("支付成功");
										} else {
//											DialogUtils.toastTip("支付失败");
										}
									}
								});
							}
						});
					}
				}
			});
		} catch (Exception e) {}
	
	}
	

//	public static void goPay(final String money, final String payFromType, final Room room) {
//		try {
//			ThreadPool.startWork(new Runnable() {
//
//				public void run() {
//					// 先提交充值订单
//					Map<String, String> paramMap = new HashMap<String, String>();
//					paramMap.put("money", money);
//					paramMap.put("payFromType", payFromType);
//
//					/**判断是不是预充值**/
//					if(payFromType != null && payFromType.equalsIgnoreCase(SDKConstant.PRRECHARGE) && null != PrerechargeManager.mPayRecordOrder.getPreOrderNo()){
//						paramMap.put(PrerechargeManager.PRERECHARGE_ORDER_PARAMS_PREORDERNO, PrerechargeManager.mPayRecordOrder.getPreOrderNo());
//						paramMap.put(PrerechargeManager.PRERECHARGE_ORDER_PARAMS_PREORDERTYPE, "1");
//					}
//					if(Assistant.ASSID != null && Assistant.BTNCODE != null){
//					
//						paramMap.put("asstId", Assistant.ASSID);
//						paramMap.put("btnCode", Assistant.BTNCODE);
//						Assistant.ASSID = null;
//						Assistant.BTNCODE = null;
//					}
//					if (room != null) {
//						paramMap.put("payFromItem", room.getCode());
//					}
//					String resultJson = HttpRequest.addPayOrder(VACConfig.VACPAY_URL, paramMap);
//					JsonResult result = JsonHelper.fromJson(resultJson, JsonResult.class);
//					if (JsonResult.SUCCESS.equals(result.getMethodCode())) {
//						final VacPayOrder payOrder = JsonHelper.fromJson(result.getMethodMessage(), VacPayOrder.class);
//						final String gameName = Database.currentActivity.getResources().getString(R.string.app_name);
//						Database.currentActivity.runOnUiThread(new Runnable() {
//
//							public void run() {
//								MultimodeConfig.setCallbackUrl(payOrder.getPostURL()); // 服务器回调接口
//								MultiModePay.getInstance().vacPay(Database.currentActivity, payOrder.getCompany(), gameName, payOrder.getProductName(), payOrder.getPhone(), money, payOrder.getPayNo(), payOrder.getOriderid(), new VacCallBack() {
//
//									public void VacResult(String status, String errorMsg) {
//										if (status.equals("00000")) {
//											DialogUtils.toastTip("支付成功");
//										} else {
////											DialogUtils.toastTip("支付失败");
//										}
//									}
//								});
//							}
//						});
//					}
//				}
//			});
//		} catch (Exception e) {}
//	}
//	
//	/**
//	 * 获取配置的金额
//	 * @param money
//	 * @return
//	 */
//	public static int getMatchMoney(int money){
//		if(Database.JOIN_ROOM != null  && money <= 0){
//			if (SDKConstant.PAY_ROOM == 0) {
//				String roomCode = Database.JOIN_ROOM.getCode();
//				String money2 = SDKConfig.ROMM_PAY.get(roomCode);
//				for (int i = 0; i < VACConfig.BILLPOINT_LIST.size(); i++) {
//					if (VACConfig.BILLPOINT_LIST.get(i).getPrice().equals(money2)) {
//						money = VACConfig.BILLPOINT_LIST.get(i).getPrice();
//						break;
//					}
//				}
//			} else {
//				long roomLimit = Database.JOIN_ROOM.getLimit();
//				for (int i = 0; i < VACConfig.BILLPOINT_LIST.size(); i++) {
//					if (VACConfig.BILLPOINT_LIST.get(i).getPrice() * 10000 <= roomLimit) {
//						money = VACConfig.BILLPOINT_LIST.get(i).getPrice();
//						break;
//					} else if (i == VACConfig.BILLPOINT_LIST.size() - 1) {
//						money = VACConfig.BILLPOINT_LIST.get(i).getPrice();
//					}
//				}
//			}
//		}
//		
//		int signMoney = Integer.parseInt(SDKConfig.SIGN_MONEY);
//		if(money < signMoney){
//			money = signMoney;
//		}
//		
//		return money;
//	}
}
