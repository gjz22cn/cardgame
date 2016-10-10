package com.sdk.jifeng.uti;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Database;
import com.lordcard.entity.JsonResult;
import com.lordcard.net.http.HttpRequest;
import com.mappn.sdk.pay.GfanPay;
import com.mappn.sdk.pay.GfanPayCallback;
import com.mappn.sdk.pay.model.Order;
import com.mappn.sdk.uc.User;
import com.sdk.jifeng.pay.GFanPayOrder;
import com.sdk.jifeng.pay.JFConstant;

public class JFPayUtil {

	public void JFPay(final Context ctx, final int money,
			final String orderName,final String orderDesc,final String payNo) {

			new Thread() {

				@Override
				public void run() {
					super.run();
					// 先提交充值订单
					try {
						Map<String, String> paramMap = new HashMap<String, String>();
						paramMap.put("payNo", payNo);
						String resultJson = HttpRequest.addPayOrder(
								JFConstant.JFPAY_URL, paramMap);
						JsonResult result = JsonHelper.fromJson(resultJson,
								JsonResult.class);
						if (JsonResult.SUCCESS.equals(result.getMethodCode())) {
							final GFanPayOrder mmorder = JsonHelper.fromJson(
									result.getMethodMessage(), GFanPayOrder.class);
							Database.currentActivity.runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									Order order = new Order(orderName, orderDesc,money*10, mmorder.getOriderid());
									GfanPay.getInstance(ctx.getApplicationContext()).pay(order, new GfanPayCallback() {
										
										@Override
										public void onSuccess(User arg0, Order arg1) {
											// TODO Auto-generated method stub
											DialogUtils.toastTip("支付成功");
										}
										
										@Override
										public void onError(User user) {
											if (user != null) {
												DialogUtils.toastTip("支付失败！");
											} else {
												DialogUtils.toastTip("登录失败！");
											}
										}
									});
								}
							});
			
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
		
				}

			}.start();
			// 传订单号
		

	}
}
