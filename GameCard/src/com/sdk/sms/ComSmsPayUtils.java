package com.sdk.sms;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.entity.JsonResult;
import com.lordcard.network.http.HttpRequest;
import com.sdk.util.sms.SmsOrder;
import com.sdk.util.sms.SmsUtil;
import com.sdk.util.vo.PayPoint;

public class ComSmsPayUtils {
	
	/**
	 * 通用短信支付
	 */
	public static void goPay(final PayPoint point,final String paySiteTag){
		new Thread() {
			public void run() {
				String value = point.getValue();
				if(TextUtils.isEmpty(value)) return;
				
				// 先提交充值订单
				Map<String, String> paramMap = new HashMap<String, String>();

				paramMap.put("goodsName", point.getName()); 	//购买的物品名称
				paramMap.put("money",String.valueOf(point.getMoney()));
				paramMap.put("payTag",paySiteTag);		//充值的标识位
				paramMap.put("smsCode",value);			//业务代码
				
				// 后台生成订单
				String resultJson = HttpRequest.addPayOrder(SmsConfig.COMSMS_URL, paramMap);
				if (TextUtils.isEmpty(resultJson)) {
					return;
				}
				JsonResult result = JsonHelper.fromJson(resultJson, JsonResult.class);
				if (JsonResult.SUCCESS.equals(result.getMethodCode())) {
					SmsOrder order = JsonHelper.fromJson(result.getMethodMessage(), SmsOrder.class);
					
					SmsUtil.goPay(point.getSmsCall(), order.getSmsTxt(), order, paySiteTag);
				} else {
					DialogUtils.mesTip(result.getMethodMessage(), true);
				}

			};
		}.start();
	}
}
