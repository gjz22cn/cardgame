/**
 * SmsSendBroadCast.java [v 1.0.0]
 * classes : broadcast.SmsSendBroadCast
 * auth : yinhongbiao
 * time : 2012 2012-11-13 下午3:49:39
 */
package com.lordcard.broadcast;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lordcard.common.util.DialogUtils;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Constant;
import com.lordcard.entity.GameUser;
import com.lordcard.network.base.ThreadPool;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpRequest;

/**
 * 短信发送状态广播 broadcast.SmsSendBroadCast
 */
public class GroupSmsSendBroadCast extends BroadcastReceiver {

	public void onReceive(final Context context, Intent intent) {
		if (Constant.ACTION_SMS_ORDER.equals(intent.getAction())) {
			switch (getResultCode()) {
				case Activity.RESULT_OK:
					DialogUtils.toastTip("订单已经提交，金豆即将到账......!");
					final Map<String, String> paramMap = new HashMap<String, String>();
					GameUser gameUser = (GameUser)GameCache.getObj(CacheKey.GAME_USER);
					paramMap.put("loginToken", gameUser.getLoginToken());
					paramMap.put("orderNo",intent.getStringExtra("orderno"));
					ThreadPool.startWork(new Runnable() {
						public void run() {
							try {
								String resultJson2 = HttpRequest.getdou(paramMap);
								//1失败
								if (HttpRequest.SUCCESS_STATE.equals(resultJson2)) {
									DialogUtils.toastTip("您购买的金豆已经到账，请查收");
								} else {
									DialogUtils.toastTip("订单已提交，金豆到账后可在充值记录中查看。话费不足，超过月限额等可能导致购买失败。有疑问请打客服热线。");
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					break;
				default:
					DialogUtils.toastTip("订单提交失败");
					break;
			}
		}
	}
}
