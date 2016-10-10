package com.sdk.dianjin.pay.util;

import android.content.Context;

public class FastDJPay {
	public static Context ctx;

	public FastDJPay(Context context,String money) {
		if(money.equals("0")){
			money="5";
		}
		ctx = context;
		DJPayUtil.djInit();
		DJPayUtil.goDJPay(money, context);
		DJPayUtil.DJDestroy();
	}

}
