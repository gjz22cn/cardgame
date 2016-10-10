package com.sdk.tianyi.util;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.estore.lsms.tools.ApiParameter;
import com.estore.ui.CTEStoreSDKActivity;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Database;
import com.lordcard.entity.JsonResult;
import com.lordcard.net.http.HttpRequest;
import com.lordcard.network.base.ThreadPool;
import com.lordcard.prerecharge.PrerechargeManager;
import com.lordcard.ui.view.Assistant;
import com.sdk.tianyi.pay.ESurfingOrder;
import com.sdk.util.PaySite;
import com.sdk.util.vo.PayPoint;

public class TYPayUtil {
	
	public static void goPay(final PayPoint point,final String paySite){
		ThreadPool.startWork(new Runnable() {
			public void run() {
				try {
					// 先提交充值订单
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("goodsName", point.getName()); 	//购买的物品名称
					paramMap.put("money",String.valueOf(point.getMoney()));
					paramMap.put("payFromType",paySite);
					/**判断是不是预充值**/
					if (PaySite.PREPARERECHARGE.equalsIgnoreCase(paySite)  && null != PrerechargeManager.mPayRecordOrder.getPreOrderNo()) {
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
					String resultJson = HttpRequest.addPayOrder(TYConfig.TYPAY_URL, paramMap);
					JsonResult result = JsonHelper.fromJson(resultJson, JsonResult.class);
					if (JsonResult.SUCCESS.equals(result.getMethodCode())) {
						final ESurfingOrder payOrder = JsonHelper.fromJson(result.getMethodMessage(), ESurfingOrder.class);
						Database.currentActivity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Intent intent = new Intent();
								intent.setClass(Database.currentActivity,CTEStoreSDKActivity.class);
								Bundle bundle = new Bundle();
								//示例参数值
								bundle.putString(ApiParameter.APPCHARGEID, payOrder.getAppcode());
								bundle.putString(ApiParameter.CHANNELID, "0");
								if (paySite.equals(PaySite.RECHARGE_LIST_FAST) || paySite.equals(PaySite.RECHARGE_LIST)) {
									bundle.putBoolean(ApiParameter.SCREENHORIZONTAL, false);//进入横屏XML"
								} else {
									bundle.putBoolean(ApiParameter.SCREENHORIZONTAL, true);//进入横屏XML"
								}
								bundle.putString(ApiParameter.CHARGENAME, payOrder.getProductName());
								bundle.putInt(ApiParameter.PRICETYPE, 0);
								bundle.putString(ApiParameter.PRICE, String.valueOf(point.getMoney()));
								bundle.putString(ApiParameter.REQUESTID, payOrder.getOriderId());
								intent.putExtras(bundle);
								Database.currentActivity.startActivityForResult(intent, 0);
							}
						});
					}
				} catch (Exception e) {
				}
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bundle bdl = data.getExtras();
		int payResultCode = bdl.getInt(ApiParameter.RESULTCODE);
		if (ApiParameter.CTESTORE_SENDSUCCESS == payResultCode || ApiParameter.CTESTORE_SENDTIMEOUT == payResultCode) {
			//支付短信发送成功       	
			Log.d("pay-success", "success");
			Log.d("pay-success", bdl.getString(ApiParameter.REQUESTID));
		} else {}
	}
}
