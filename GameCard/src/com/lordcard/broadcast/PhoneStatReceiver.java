package com.lordcard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.lordcard.common.listener.GamePhoneStateListener;

public class PhoneStatReceiver extends BroadcastReceiver {
	private boolean flag = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		// 如果是拨打电话
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			flag = true;
		} else {
			flag = true;
		}
		if (flag) {
			flag = false;
			// 获取电话通讯服务
			TelephonyManager tpm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			// 创建一个监听对象，监听电话状态改变事件
			tpm.listen(new GamePhoneStateListener(context), GamePhoneStateListener.LISTEN_CALL_STATE);
		}
	}
}