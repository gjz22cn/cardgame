package com.sdk.ssw.uti;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Database;
import com.lordcard.entity.JsonResult;
import com.lordcard.net.http.HttpRequest;
import com.sdk.ssw.SSWOrder;
import com.sdk.ssw.pay.SSWConstant;
import com.vee.usertraffic.app.ExchangeCallback;
import com.vee.usertraffic.app.UserTrafficSDK;

public class SSWPayUtil {
	/**
	 * 检查订单交易结果
	 * 
	 * @param appID
	 *            游戏ID
	 * @param sdkResult
	 *            兑换接口所返回的字符串
	 * @param localOrderId
	 *            本地记录的此次交易生成的订单号
	 * @param money
	 *            本地记录的此次交易的金额
	 * @param appKey 
	 * 			      游戏Key
	 * @return
	 */
	public boolean checkOrder(String appID, String sdkResult,
			String localOrderId, int money,String appKey) {
		try {
			JSONObject o = new JSONObject(sdkResult);
			int code = o.getInt("error_code");
			if (code == 100) {
				String serverSign = o.getString("server_sign");
				if(TextUtils.isEmpty(serverSign)){
					return false;
				}else{
					//验签
					String sign = SignUtil.getMD5(appID+appKey+money+localOrderId);
					Log.e("test","sign = " + sign);
					if(!serverSign.toLowerCase().equals(sign.toLowerCase())){
						Log.e("error","订单被非法串改！");
						return false;
					}
				}
				
				String resultOrderId = o.getString("order_id");
				double resultMoney = o.getDouble("total_fee");
				if (!resultOrderId.endsWith(localOrderId)
						|| resultMoney != money) {
					return false;
				}
				//TODO:可选 查询服务器此订单交易结果 
				JSONObject check = UserTrafficSDK.checkOrder(appID,
						localOrderId);
				if (check.getInt("errno") == 100) {
					return true;
				}
				return false;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	/**
	 * 测试兑换
	 * 
	 * @param key
	 *            游戏KEY
	 * @param id
	 *            游戏ID
	 * @param subject
	 *            商品描述
	 * @param money  
	 *            价钱 以RMB-分为单位
	 * @param channelStr
	 *            游戏所在区
	 * @param serverStr
	 *            游戏所在服
	 * @param notify_urlStr
	 *            交易成功通知地址
	 * @param orderID
	 *            此次交易的订单号
	 */
	public void testExchange(final String key, final String id,final String channel, String subject,
			final int money, String channelStr, String serverStr,
			String notify_urlStr, final String orderID) {
		// 生成订单信息，参数按字母升序排序
		String order = "channel=" + channelStr +"&game_channel="+channel+ "&game_id=" + id
				+ "&game_key=" + key + "&notify_url=" + notify_urlStr
				+ "&order_id=" + orderID + "&server=" + serverStr + "&subject="
				+ subject + "&total_fee=" + money;
		Log.d("test", "testExchange order = " + order);
		// 加密
		String sign = SignUtil.getMD5(order);
		order = order + "&sign=" + sign;
		UserTrafficSDK.exchange(Database.currentActivity, order, new ExchangeCallback() {
			@Override
			public void callBack(final String result) {// 返回的result为JSONOBject字符串
				Log.e("test","pay result = " + result);
				new Thread(new Runnable() {

					@Override
					public void run() {
						
						boolean check = checkOrder(id, result, orderID, money,key);
						Looper.prepare();
						if (check) {
							Toast.makeText(Database.currentActivity, "成功",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(Database.currentActivity, "失败",
									Toast.LENGTH_SHORT).show();
						}
						Looper.loop();
					}
				}).start();
			}
		});
	}
	/**
	 * 充值
	 * @param ctx
	 * @param money
	 */
	public void SSWPPay(final Context ctx, final String money) {

		try {
			new Thread() {

				@Override
				public void run() {
					super.run();
					// 先提交充值订单
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("money", money);
					String resultJson = HttpRequest.addPayOrder(
							SSWConstant.SSWPAY_URL, paramMap);
					JsonResult result = JsonHelper.fromJson(resultJson,
							JsonResult.class);
					if (JsonResult.SUCCESS.equals(result.getMethodCode())) {
						final SSWOrder mmorder = JsonHelper.fromJson(
								result.getMethodMessage(), SSWOrder.class);
						Database.currentActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								testExchange(SSWConstant.SSW_KEY, SSWConstant.SSW_ID, "suishouwan",mmorder.getProductName(),
										Integer.parseInt(money)*100, null,
										null, mmorder.getNotifyUrl(), mmorder.getOriderId());
							}
						});
					}
					else{
						DialogUtils.mesTip(result.getMethodMessage(), true);
					}
				}

			}.start();
			
			// 传订单号
		
		} catch (Exception e) {
			// TODO: handle exception
		}
		

	}
	
}
