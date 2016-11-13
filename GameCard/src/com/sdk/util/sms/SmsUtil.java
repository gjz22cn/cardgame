package com.sdk.util.sms;

import java.util.Map;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.text.TextUtils;

import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;

/**
 * 短信发送前判断要否弹出dialog
 * @author Administrator
 */
public class SmsUtil {

	/**
	 * 短信发送支付
	 * @param payOrder
	 */
	public static void goPay(final String smsCall,final String smsText,final SmsOrder order,final String paySite) {	}
	
	/**
	 * 短信发送支付
	 * @param payOrder
	 */
	public static void sendOneSms(final String smsCall,final String smsText,final Map<String,String> param) {
		Database.currentActivity.runOnUiThread(new Runnable() {
			public void run() {
				try {
					SmsManager manager = SmsManager.getDefault();
					Intent intent = new Intent(Constant.ACTION_SMS_SEND);
					if(param != null){
						for (String key : param.keySet()) {
							intent.putExtra(key,param.get(key));
						}
					}
					PendingIntent dummyEvent = PendingIntent.getBroadcast(Database.currentActivity, 0, intent, 0);
					manager.sendTextMessage(smsCall, null, smsText, dummyEvent, dummyEvent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
