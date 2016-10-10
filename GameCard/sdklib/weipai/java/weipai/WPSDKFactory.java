package com.sdk.weipai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.pay.ISDKFactory;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Database;
import com.lordcard.entity.Room;
import com.lordcard.net.http.HttpRequest;
import com.sdk.constant.SdkConstant;
import com.sdk.constant.SdkDatabase;
import com.sdk.weipai.pay.WPConstant;
import com.sdk.weipai.pay.Wiipay;
import com.sdk.weipai.pay.ui.WPPayActivity;
import com.sdk.weipai.pay.ui.dialog.WPDialog;
import com.sdk.weipai.uti.WPPayUtil;

public class WPSDKFactory extends ISDKFactory {

	public Class<?> getDefaultPayView() {
		return WPPayActivity.class;
	}

	public Dialog getPayDialog(Context ctx, Room room, String msg) {
		return new WPDialog(ctx, room);
	}

	@Override
	public Class<?> getLoginView() {
		return null;
	}

	@Override
	public void fastPay(Context ctx, String money) {

		new WPPayUtil().WPPay(ctx, SdkDatabase.SIGN_MONEY, SdkDatabase.SIGN_PAY_CODE);

	}

	@Override
	public void UpdatePayDate() {
		final String UPDATE_PAY = "updatePay";

		Map<String, String> result = Database.ALL_SWITCH;
		String getup = result.get("pu");
		SdkDatabase.SIGN_PAY_CODE = result.get("wp");
		SdkDatabase.SIGN_MONEY = result.get("wm");
		String resultJson2 = result.get("rp");
		SdkDatabase.ROMM_PAY = JsonHelper.fromJson(resultJson2, new TypeToken<Map<String, String>>() {
		});
		SharedPreferences sharedData = Database.currentActivity.getApplication().getSharedPreferences(SdkConstant.GAME_UPDATE_PAY,
				Context.MODE_PRIVATE);
		String up = sharedData.getString(UPDATE_PAY, "");
		if (getup == null || !getup.equals(up)) {
			SdkDatabase.PAY_UPDATE = true;
			Editor editor = sharedData.edit();
			editor.putString(UPDATE_PAY, getup);
			editor.commit();
		} else {
			SdkDatabase.PAY_UPDATE = false;
		}

	}

	@Override
	public void getPayDate() {
		final String WPPAY_CONTENT = "wppay_content";
		SharedPreferences sharedData = Database.currentActivity.getApplication().getSharedPreferences(WPConstant.WPPAY_CONTENT, Context.MODE_PRIVATE);
		String resultJson = sharedData.getString(WPPAY_CONTENT, "");
		if (resultJson != "") {
			List<Wiipay> list = JsonHelper.fromJson(resultJson, new TypeToken<List<Wiipay>>() {
			});
			WPConstant.WIIPAY_LIST = list;
		}

		if (SdkDatabase.PAY_UPDATE == true || WPConstant.WIIPAY_LIST == null) {

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Map<String, String> paramMap = new HashMap<String, String>();
						paramMap.put("payType", WPConstant.WP_PAY_TYPE);
						String resultJson = HttpRequest.getPayMoney(WPConstant.WPPAY_CHANGE_URL, paramMap);

						List<Wiipay> list = JsonHelper.fromJson(resultJson, new TypeToken<List<Wiipay>>() {
						});
						WPConstant.WIIPAY_LIST = list;
						SharedPreferences sharedData = Database.currentActivity.getApplication().getSharedPreferences(WPConstant.WPPAY_CONTENT,
								Context.MODE_PRIVATE);
						Editor editor = sharedData.edit();
						editor.putString(WPPAY_CONTENT, resultJson);
						editor.commit();
					} catch (Exception e) {
						String UPDATE_PAY = "updatePay";
						SharedPreferences sharedData2 = Database.currentActivity.getApplication().getSharedPreferences(SdkConstant.GAME_UPDATE_PAY,
								Context.MODE_PRIVATE);
						Editor editor = sharedData2.edit();
						editor.putString(UPDATE_PAY, "");
						editor.commit();
					}

				}
			}).start();

		}
	}

	@Override
	public String getPayType() {
		// TODO Auto-generated method stub
		return WPConstant.WP_PAY_TYPE;
	}

}
