package com.sdk.vac.util;

import java.util.HashMap;
import java.util.Map;

import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Database;
import com.lordcard.entity.JsonResult;
import com.lordcard.net.http.HttpRequest;
import com.lordcard.network.base.ThreadPool;
import com.lordcard.prerecharge.PrerechargeManager;
import com.lordcard.ui.view.Assistant;
import com.sdk.util.PaySite;
import com.sdk.util.vo.PayPoint;
import com.sdk.vac.PayResultListener;
import com.sdk.vac.VACConfig;
import com.unicom.dcLoader.Utils;

public class VACPayUtil {

	public static void goPay(final PayPoint point, final String paySite) {
		try {
			ThreadPool.startWork(new Runnable() {

				public void run() {
					// 先提交充值订单
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("goodsName", point.getName()); //购买的物品名称
					paramMap.put("money", String.valueOf(point.getMoney()));
					paramMap.put("payFromType", paySite); //充值的标识位
					paramMap.put("payNo", "2");
					/**判断是不是预充值**/
					if (PaySite.PREPARERECHARGE.equalsIgnoreCase(paySite) && null != PrerechargeManager.mPayRecordOrder.getPreOrderNo()) {
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
						paramMap.put("payFromItem", Database.JOIN_ROOM.getCode());
					}
					String resultJson = HttpRequest.addPayOrder(VACConfig.VACPAY_URL, paramMap);
					JsonResult result = JsonHelper.fromJson(resultJson, JsonResult.class);
					if (JsonResult.SUCCESS.equals(result.getMethodCode())) {
						final VacPayOrder payOrder = JsonHelper.fromJson(result.getMethodMessage(), VacPayOrder.class);
						Database.currentActivity.runOnUiThread(new Runnable() {

							public void run() {
								try {
									//支付
									Utils.getInstances().setBaseInfo(Database.currentActivity, false, true, payOrder.getPostURL());
									Utils.getInstances().pay(Database.currentActivity, point.getValue(), "", point.getName(), String.valueOf(point.getMoney()), payOrder.getOriderid(), new PayResultListener());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					}
				}
			});
		} catch (Exception e) {}
	}
}
