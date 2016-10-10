package com.lordcard.common.listener;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.lordcard.common.util.AudioPlayUtils;
import com.lordcard.common.util.PreferenceHelper;
import com.lordcard.ui.view.dialog.LotteryDialog;

/**
 * 手机来电状态监听 com.lordcard.common.listener.GamePhoneStateListener
 * 
 * @author Administrator <br/>
 *         create at 2013 2013-3-19 下午4:09:01
 */
public class GamePhoneStateListener extends PhoneStateListener {
//	private Context context;
	private static boolean play = false;
	private boolean zhengdongOn = false;//震动是否开启

	public GamePhoneStateListener(Context context) {
//		this.context = context;
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		Log.d("phoneIdle", "phoneIdle2:state  " + state);
		switch (state) {
		// 空闲
		case TelephonyManager.CALL_STATE_IDLE://0
			if (play) {
				play = false;
				AudioPlayUtils.getInstance().ContinueBgMusic();
				if (zhengdongOn) {
					PreferenceHelper.getMyPreference().getEditor().putBoolean("zhendong", true).commit();
				}
				Log.d("phoneIdle", "-----------------CALL_STATE_IDLE  ");
			}
			break;
		// 来电
		case TelephonyManager.CALL_STATE_RINGING://1
			if (!play) {
				LotteryDialog.voiceON = false;
				if (AudioPlayUtils.getInstance().isBgisPlaying()) {
					play = true;
					AudioPlayUtils.getInstance().stopBgMusic();
				}
				if (PreferenceHelper.getMyPreference().getSetting().getBoolean("zhendong", true)) {
					zhengdongOn = true;
					PreferenceHelper.getMyPreference().getEditor().putBoolean("zhendong", false).commit();
				} else {
					zhengdongOn = false;
				}
				Log.d("phoneIdle", "----------------CALL_STATE_RINGING  ");
			}
			break;
		// 摘机（正在通话中）
		case TelephonyManager.CALL_STATE_OFFHOOK://2
			if (!play) {
				if (AudioPlayUtils.getInstance().isBgisPlaying()) {
					play = true;
					AudioPlayUtils.getInstance().stopBgMusic();
				}
				if (PreferenceHelper.getMyPreference().getSetting().getBoolean("zhendong", true)) {
					zhengdongOn = true;
					PreferenceHelper.getMyPreference().getEditor().putBoolean("zhendong", false).commit();
				}
				Log.d("phoneIdle", "-----------------CALL_STATE_OFFHOOK  ");
			}
			break;
		}
	}
}
