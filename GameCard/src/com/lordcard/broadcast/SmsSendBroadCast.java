/**
 * SmsSendBroadCast.java [v 1.0.0]
 * classes : broadcast.SmsSendBroadCast
 * auth : yinhongbiao
 * time : 2012 2012-11-13 下午3:49:39
 */
package com.lordcard.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.lordcard.constant.Constant;
import com.sdk.util.OFFLinePay;

/**
 * 短信发送状态广播 broadcast.SmsSendBroadCast
 */
public class SmsSendBroadCast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Constant.ACTION_SMS_SEND.equals(intent.getAction())) {
			switch (getResultCode()) {
				case Activity.RESULT_OK: 
					Toast.makeText(context, "短信发送成功!", Toast.LENGTH_LONG).show();
					break;
				default:
//					Toast.makeText(context, "发送失败", Toast.LENGTH_LONG).show();
					break;
			}
			smsSuccess(intent);
		}
	}
	
	/**
	 * 短信发送成功
	 */
	public void smsSuccess(Intent intent){
		String smsTag = intent.getStringExtra("smsTag");
		if(TextUtils.isEmpty(smsTag)) return;
		
		if(smsTag.equals("offline_recharge")){	//单机充值
			String money = intent.getStringExtra("money");		//充值金额
			OFFLinePay.goPay(Integer.parseInt(money));	
			return;
		}
		
	}
}
