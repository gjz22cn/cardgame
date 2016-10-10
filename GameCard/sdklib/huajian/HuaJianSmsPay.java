package com.sdk.huajian;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.entity.JsonResult;
import com.lordcard.net.http.HttpRequest;
import com.sdk.util.sms.SmsOrder;
import com.sdk.util.sms.SmsUtil;
import com.sdk.util.vo.PayPoint;

public class HuaJianSmsPay {
	
	/**
	 * 电信短信支付
	 */
	public static void goPay(final PayPoint point,final String paySiteTag){
		new Thread() {
			public void run() {
				String value = point.getValue();
				if(TextUtils.isEmpty(value)) return;
				
				String [] cmdArr = value.split("_");
				// 先提交充值订单
				Map<String, String> paramMap = new HashMap<String, String>();

				paramMap.put("goodsName", point.getName()); 	//购买的物品名称
				paramMap.put("money",String.valueOf(point.getMoney()));
				paramMap.put("payFromType",paySiteTag);		//充值的标识位
				paramMap.put("consumeCode",cmdArr[1]);		//业务代码
				
				// 后台生成订单
				String resultJson = HttpRequest.addPayOrder(HuaJianConfig.LTPAY_URL, paramMap);
				if (TextUtils.isEmpty(resultJson)) {
					return;
				}
				JsonResult result = JsonHelper.fromJson(resultJson, JsonResult.class);
				if (JsonResult.SUCCESS.equals(result.getMethodCode())) {
					SmsOrder order = JsonHelper.fromJson(result.getMethodMessage(), SmsOrder.class);
					
					String smsText = cmdArr[0] + order.getOrderNo();
					SmsUtil.goPay(point.getSmsCall(), smsText, order, paySiteTag);
				} else {
					DialogUtils.mesTip(result.getMethodMessage(), true);
				}

			};
		}.start();
	}
}
