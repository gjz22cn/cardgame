package com.sdk.jifeng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
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
import com.mappn.sdk.pay.GfanPay;
import com.mappn.sdk.uc.GfanUCenter;
import com.sdk.constant.SdkConstant;
import com.sdk.constant.SdkDatabase;
import com.sdk.jifeng.pay.JFConstant;
import com.sdk.jifeng.pay.ui.JFPayActivity;
import com.sdk.jifeng.pay.ui.dialog.JFDialog;
import com.sdk.jifeng.uti.JFPayUtil;
import com.sdk.weipai.pay.WPConstant;

public class JFSDKFactory extends ISDKFactory {

	public Class<?> getDefaultPayView() {
		return JFPayActivity.class;
	}

	public Dialog getPayDialog(Context ctx, Room room, String msg) {
		return new JFDialog(ctx, room);
	}

	@Override
	public Class<?> getLoginView() {
		return null;
//		return JFLoginActivity.class;
	}

	@Override
	public void fastPay(Context ctx,String money) {
		new JFPayUtil().JFPay(ctx, Integer.valueOf(SdkDatabase.SIGN_MONEY), "充值", "由掌中游提供", SdkDatabase.SIGN_PAY_CODE);

	}
	

	@Override
	public void UpdatePayDate(Context ctx) {
		 final String UPDATE_PAY = "updatePay";
			
						Map<String,String> result = Database.ALL_SWITCH;
						String getup=result.get("pu");
						SdkDatabase.SIGN_MONEY=result.get("gf");
						SdkDatabase.SIGN_PAY_CODE=result.get("gp");
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
		final String JFPAY_CONTENT = "jfpay_content";
		SharedPreferences sharedData = Database.currentActivity
				.getApplication().getSharedPreferences(
						JFConstant.JFPAY_CONTENT, Context.MODE_PRIVATE);
		String resultJson = sharedData.getString(JFPAY_CONTENT, "");
		if (resultJson != "") {
			List<GFen> list = JsonHelper.fromJson(resultJson,
					new TypeToken<List<GFen>>() {
					}.getType());
			SdkDatabase.JFPAY_LIST = list;
		}

		if (SdkDatabase.PAY_UPDATE == true||SdkDatabase.JFPAY_LIST==null) {

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Map<String, String> paramMap = new HashMap<String, String>();
						paramMap.put("payType", JFConstant.JF_PAY_TYPE);
						String resultJson = HttpRequest.getPayMoney(
								JFConstant.JFPAY_CHANGE_URL, paramMap);

						List<GFen> list = JsonHelper.fromJson(resultJson,
								new TypeToken<List<GFen>>() {
								}.getType());
						SdkDatabase.JFPAY_LIST = list;
						SharedPreferences sharedData = Database.currentActivity
								.getApplication().getSharedPreferences(
										JFConstant.JFPAY_CONTENT,
										Context.MODE_PRIVATE);
						Editor editor = sharedData.edit();
						editor.putString(JFPAY_CONTENT, resultJson);
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
	public void getPayInit(Context ctx, ArrayList<Object> list) {
		GfanPay.getInstance(Database.currentActivity.getApplicationContext()).init();
	}

	@Override
	public void getPayLogOut(Context ctx) {
		GfanUCenter.logout(Database.currentActivity);
	}
	@Override
	public String getPayType() {
		// TODO Auto-generated method stub
		return JFConstant.JF_PAY_TYPE ;
	}
	
	
}
