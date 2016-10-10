package com.sdk.weipai.uti;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;

import com.bx.pay.BXPay;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Database;
import com.lordcard.entity.JsonResult;
import com.lordcard.net.http.HttpRequest;
import com.sdk.weipai.pay.WPConstant;
import com.sdk.weipai.pay.WiipayOrder;

public class WPPayUtil {
	BXPay bxPay;

	public void WPPay(final Context ctx, final String money,
			final String payCode) {
		bxPay = new BXPay(ctx);

		try {
			new Thread() {

				@Override
				public void run() {
					super.run();
					// 先提交充值订单
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("payCode", payCode);
					String resultJson = HttpRequest.addPayOrder(
							WPConstant.WPPAY_URL, paramMap);
					JsonResult result = JsonHelper.fromJson(resultJson,
							JsonResult.class);
					if (JsonResult.SUCCESS.equals(result.getMethodCode())) {
						final WiipayOrder mmorder = JsonHelper.fromJson(
								result.getMethodMessage(), WiipayOrder.class);
						Database.currentActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Map<String, String> devPrivate = new HashMap<String, String>();
								devPrivate.put("orderNo",mmorder.getOrderNo() );
								bxPay.setDevPrivate(devPrivate);
								bxPay.pay(payCode, new BXPay.PayCallback() {
									// 支付后返回的信息接收方法
									@Override
									public void pay(Map<String, String> resultInfo) {
										// TODO Auto-generated method stub
//										String result = resultInfo.get("result");// fail:支付失败，success:成功支付，error:本地联网失败，pass已经是付费用户，cancel表示用户取消支付
//										System.out.println("返回代码是:" + result);
//										new AlertDialog.Builder(ctx).setTitle("微派返回状态")
//												.setMessage("返回代码是:" + result)
//												.setPositiveButton("确定", null).show();
									}
								});
							}
						});
		
					}
				}

			}.start();
			// 传订单号
		
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
