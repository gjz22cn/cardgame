package com.sdk.aibei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.reflect.TypeToken;
import com.iapppay.mpay.ifmgr.SDKApi;
import com.lordcard.common.pay.ISDKFactory;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Database;
import com.lordcard.entity.Room;
import com.lordcard.net.http.HttpRequest;
import com.sdk.aibei.pay.ABConstant;
import com.sdk.aibei.pay.Iapppay;
import com.sdk.aibei.pay.ui.ABPayActivity;
import com.sdk.aibei.pay.ui.dialog.ABDialog;
import com.sdk.aibei.uti.ABPayUtil;
import com.sdk.constant.SdkConstant;
import com.sdk.constant.SdkDatabase;

public class ABSDKFactory extends ISDKFactory {

	public Class<?> getDefaultPayView() {
		return ABPayActivity.class;
	}

	public Dialog getPayDialog(Context ctx, Room room, String msg) {
		return new ABDialog(ctx, room);
	}

	@Override
	public Class<?> getLoginView() {
		return null;
	}

	@Override
	public void fastPay(Context ctx,String money) {

		ABPayUtil.ABPay(ctx, SdkDatabase.SIGN_MONEY, SdkDatabase.SIGN_PAY_CODE);

	}
	

	@Override
	public void UpdatePayDate() {
		 final String UPDATE_PAY = "updatePay";
						Map<String,String> result = Database.ALL_SWITCH;
						String getup=result.get("pu");
						SdkDatabase.SIGN_PAY_CODE=result.get("ip");
						SdkDatabase.SIGN_MONEY=result.get("im");
						String resultJson2=result.get("rp");
						SdkDatabase.ROMM_PAY = JsonHelper.fromJson(resultJson2, new TypeToken<Map<String,String>>(){});
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
	public void getPayDate() {
		final String ABPAY_CONTENT = "abpay_content1";
		SharedPreferences sharedData = Database.currentActivity
				.getApplication().getSharedPreferences(
						ABConstant.ABPAY_CONTENT, Context.MODE_PRIVATE);
		String resultJson = sharedData.getString(ABPAY_CONTENT, "");
		if (resultJson != "") {
			List<Iapppay> list = JsonHelper.fromJson(resultJson,
					new TypeToken<List<Iapppay>>() {
					});
			ABConstant.IAPPPAY_LIST = list;
		}

		if (SdkDatabase.PAY_UPDATE == true||ABConstant.IAPPPAY_LIST ==null) {

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Map<String, String> paramMap = new HashMap<String, String>();
						paramMap.put("payType", ABConstant.AB_PAY_TYPE);
						String resultJson = HttpRequest.getPayMoney(
								ABConstant.ABPAY_CHANGE_URL, paramMap);

						List<Iapppay> list = JsonHelper.fromJson(resultJson,
								new TypeToken<List<Iapppay>>() {
								});
						ABConstant.IAPPPAY_LIST = list;
						SharedPreferences sharedData = Database.currentActivity
								.getApplication().getSharedPreferences(
										ABConstant.ABPAY_CONTENT,
										Context.MODE_PRIVATE);
						Editor editor = sharedData.edit();
						editor.putString(ABPAY_CONTENT, resultJson);
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
	public void getPayInit( ArrayList<Object> list) {

		SDKApi.init(Database.currentActivity, 1, ABConstant.AB_APP_ID);
		SDKApi.mmPayInit(ABConstant.AB_APP_ID, ABConstant.AB_MM_APPID, ABConstant.AB_MM_APPKEY, Database.currentActivity);
	}
	@Override
	public String getPayType() {
		// TODO Auto-generated method stub
		return ABConstant.AB_PAY_TYPE ;
	}
	
}
