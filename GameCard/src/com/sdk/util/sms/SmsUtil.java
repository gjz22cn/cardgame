package com.sdk.util.sms;

import java.util.Map;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.text.TextUtils;

import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.sdk.util.PayUtils;
import com.sdk.util.vo.PaySiteConfigItem;

/**
 * 短信发送前判断要否弹出dialog
 * @author Administrator
 */
public class SmsUtil {

	/**
	 * 短信发送支付
	 * @param payOrder
	 */
	public static void goPay(final String smsCall,final String smsText,final SmsOrder order,final String paySite) {
		Database.currentActivity.runOnUiThread(new Runnable() {

			public void run() {
				PaySiteConfigItem paySiteConfigItem = PayUtils.getPaySiteUseConfig(paySite);
				if(paySiteConfigItem == null) return ;
				
				String smsType = paySiteConfigItem.getSmsType();
				if(TextUtils.isEmpty(smsType) || "auto".equals(smsType)){			//直接发送
					SmsManager manager = SmsManager.getDefault();
					Intent intent = new Intent(Constant.ACTION_SMS_ORDER);
					intent.putExtra("orderno", order.getOrderNo());
					try {
						PendingIntent dummyEvent = PendingIntent.getBroadcast(Database.currentActivity, 0, intent, 0);
						manager.sendTextMessage(smsCall, null, smsText, dummyEvent, dummyEvent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else if("sys".equals(smsType)){	//调用系统短信
					Uri smsToUri = Uri.parse("smsto:" + smsCall);
					Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
					intent.putExtra("sms_body", smsText);
					Database.currentActivity.startActivity(intent);
				}
			}
		});
	}
	
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
