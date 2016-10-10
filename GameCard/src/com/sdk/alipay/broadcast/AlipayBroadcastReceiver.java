package com.sdk.alipay.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class AlipayBroadcastReceiver extends BroadcastReceiver {

	public static final String NEWEST_SERVICE = "aliservice";

	@Override
	public void onReceive(Context context, Intent intent) {

		// 接收广播
		if (intent.getAction().equals("com.xrl.creditcard.install")) {
			SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(context);
			SharedPreferences.Editor mEditor = mPerferences.edit();
			mEditor.putBoolean(NEWEST_SERVICE, true);
			Log.v("AlipayBroadcastReceiver", "支付宝服务已经安装");
			mEditor.commit();
		}

	}

}
