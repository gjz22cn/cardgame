/**
 * PushNoticeBroadcastReceiver.java [v 1.0.0]
 * classes : com.lordcard.ui.view.notification.PushNoticeBroadcastReceiver
 * auth : yinhongbiao
 * time : 2013 2013-3-29 上午11:49:23
 */
package com.lordcard.ui.view.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 推送服务启动广播 接收系统开机广播
 */
public class PushNoticeBroadcastReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Intent newIntent = new Intent(context, NotificationService.class);
			context.startService(newIntent);
		}
	}

}
