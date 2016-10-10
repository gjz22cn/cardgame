package com.sdk.ssw;

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
import com.sdk.ssw.pay.SSWConstant;
import com.sdk.ssw.pay.ui.SSWPayActivity;
import com.sdk.ssw.pay.ui.dialog.SSWDialog;
import com.sdk.ssw.uti.SSWPayUtil;
import com.sdk.weipai.pay.Wiipay;
import com.sdk.weipai.uti.WPPayUtil;

public class SSWSDKFactory extends ISDKFactory {

	public Class<?> getDefaultPayView() {
		return SSWPayActivity.class;
	}

	public Dialog getPayDialog(Context ctx, Room room, String msg) {
		return new SSWDialog(ctx, room);
	}

	@Override
	public Class<?> getLoginView() {
		return null;
	}

	@Override
	public void fastPay(Context ctx,String money) {

		new SSWPayUtil().SSWPPay(ctx, SdkDatabase.SIGN_MONEY);
	}
	

	@Override
	public void UpdatePayDate(Context ctx) {
		 final String UPDATE_PAY = "updatePay";
	
						Map<String,String> result = Database.ALL_SWITCH;
						String getup=result.get("pu");
						SdkDatabase.SIGN_MONEY=result.get("ssw");
						String resultJson2=result.get("rp");
						SdkDatabase.ROMM_PAY = JsonHelper.fromJson(resultJson2, new TypeToken<Map<String,String>>(){}.getType());
						SharedPreferences sharedData = Database.currentActivity.getApplication().getSharedPreferences(SdkConstant.GAME_UPDATE_PAY, Context.MODE_PRIVATE);
						String up = sharedData.getString(UPDATE_PAY, "");
						if(getup== null || !getup.equals(up)){
							SdkDatabase.PAY_UPDATE  = true;
							Editor editor = sharedData.edit();
							editor.putString(UPDATE_PAY, getup);
							editor.commit();
						}else{
							SdkDatabase.PAY_UPDATE  = false;
						}
					
	}

	@Override
	public void getPayDate(Context ctx) {
		final String SSWPAY_CONTENT = "sswpay_content";
		SharedPreferences sharedData = Database.currentActivity
				.getApplication().getSharedPreferences(
						SSWConstant.SSWPAY_CONTENT, Context.MODE_PRIVATE);
		String resultJson = sharedData.getString(SSWPAY_CONTENT, "");
		if (resultJson != "") {
			List<SSW> list = JsonHelper.fromJson(resultJson,
					new TypeToken<List<SSW>>() {
					}.getType());
			SSWConstant.SSW_LIST= list;
		}

		if (SdkDatabase.PAY_UPDATE == true||SSWConstant.SSW_LIST ==null) {

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Map<String, String> paramMap = new HashMap<String, String>();
						paramMap.put("payType", SSWConstant.SSW_PAY_TYPE);
						String resultJson = HttpRequest.getPayMoney(
								SSWConstant.SSWPAY_CHANGE_URL, paramMap);

						List<SSW> list = JsonHelper.fromJson(resultJson,
								new TypeToken<List<SSW>>() {
								}.getType());
						SSWConstant.SSW_LIST = list;
						SharedPreferences sharedData = Database.currentActivity
								.getApplication().getSharedPreferences(
										SSWConstant.SSWPAY_CONTENT,
										Context.MODE_PRIVATE);
						Editor editor = sharedData.edit();
						editor.putString(SSWPAY_CONTENT, resultJson);
						editor.commit();
					} catch (Exception e) {
						String UPDATE_PAY = "updatePay";
						SharedPreferences sharedData2 = Database.currentActivity
								.getApplication().getSharedPreferences(
										SdkConstant.GAME_UPDATE_PAY,
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
		return SSWConstant.SSW_PAY_TYPE;
	}
	
}
