package com.sdk.aibei.uti;


import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.iapppay.mpay.ifmgr.IPayResultCallback;
import com.iapppay.mpay.ifmgr.SDKApi;
import com.iapppay.mpay.tools.PayRequest;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Database;
import com.lordcard.entity.JsonResult;
import com.lordcard.net.http.HttpRequest;
import com.sdk.aibei.pay.ABConstant;
import com.sdk.aibei.pay.IapppayOrder;

public class ABPayUtil {
	public String appid = "10040400000001100404";
	//商品密钥
	public String appkey ="QkM5MDQyNzNBQzc2QTgzMTRDRDg0QTBGREExMEJDNDRFN0VDQkYyM01UQTFOemd5TlRBNE1UQTNOVFF3T0RNNU9EY3JNall3TURjME16QTBOelF6TnpBME16UTBNemcyTVRJME1UVTROVFk1TURVeE5EWTJOemc1";
	public String exorderno = ""; // 请务必定义外部订单号，规则可以根据自己需要随意定义
	
	public static void ABPay(final Context ctx, final String money,
			final String payCode) {

		try {
			new Thread() {

				@Override
				public void run() {
					super.run();
					// 先提交充值订单
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("payCode", payCode);
					String resultJson = HttpRequest.addPayOrder(
							ABConstant.ABPAY_URL, paramMap);
					JsonResult result = JsonHelper.fromJson(resultJson,
							JsonResult.class);
					if (JsonResult.SUCCESS.equals(result.getMethodCode())) {
						final IapppayOrder mmorder = JsonHelper.fromJson(
								result.getMethodMessage(), IapppayOrder.class);
						Database.currentActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								
								Database.currentActivity.runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										// TODO Auto-generated method stub
										ABPayUtil.startPay(Integer.valueOf(payCode), Integer.valueOf(money), mmorder.getOrderNo());
									}
								});
							}
						});

					}
				}

			}.start();
		} catch (Exception e) {
			// TODO: handle exception
		}
		


	}
	
	public static void startPay(int waresid,int price,String exorderno) {
		PayRequest payRequest = new PayRequest();
		payRequest.addParam("notifyurl", null);
		payRequest.addParam("appid", ABConstant.AB_APP_ID);
		payRequest.addParam("waresid", waresid);
		payRequest.addParam("quantity", 1);
		payRequest.addParam("exorderno", exorderno);
		payRequest.addParam("price", price*100);
		payRequest.addParam("cpprivateinfo", exorderno);//返回给后台的订单
		
		String paramUrl = payRequest.genSignedUrlParamString(ABConstant.AB_APP_KEY);
		SDKApi.startPay(Database.currentActivity, paramUrl, new IPayResultCallback() {
			@Override
			public void onPayResult(int resultCode, String signValue,
					String resultInfo) {//resultInfo = 应用编号&商品编号&外部订单号
				if (SDKApi.PAY_SUCCESS == resultCode) {
					Log.e("xx", "signValue = " + signValue);
					if (null == signValue) {
						// 没有签名值，默认采用finish()，请根据需要修改
						Log.e("xx", "signValue is null ");
						Toast.makeText(Database.currentActivity, "没有签名值", Toast.LENGTH_SHORT)
								.show();
						// //finish();
					}
					
					Log.e("yyy", signValue + " ");
					boolean flag = PayRequest.isLegalSign(signValue,ABConstant.AB_APP_KEY);
					if (flag) {
						Log.e("payexample", "islegalsign: true");
						Toast.makeText(Database.currentActivity, "支付成功", Toast.LENGTH_SHORT)
								.show();
						// 合法签名值，支付成功，请添加支付成功后的业务逻辑
					} else {
						Toast.makeText(Database.currentActivity, "支付成功，但是验证签名失败",
								Toast.LENGTH_SHORT).show();
						// 非法签名值，默认采用finish()，请根据需要修改
					}
				} else if(SDKApi.PAY_CANCEL == resultCode){
					Toast.makeText(Database.currentActivity, "取消支付", Toast.LENGTH_SHORT)
					.show();
					// 取消支付处理，默认采用finish()，请根据需要修改
					Log.e("fang", "return cancel");
				}else {
					Toast.makeText(Database.currentActivity, "支付失败", Toast.LENGTH_SHORT)
							.show();
					// 计费失败处理，默认采用finish()，请根据需要修改
					Log.e("fang", "return Error");
				}

			}
		});
	}
}
