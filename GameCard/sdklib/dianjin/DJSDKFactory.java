package com.sdk.dianjin;

import android.app.Dialog;
import android.content.Context;

import com.lordcard.common.pay.ISDKFactory;
import com.lordcard.entity.Room;
import com.sdk.dianjin.login.DJLoginActivity;
import com.sdk.dianjin.pay.ui.DJPayActivity;
import com.sdk.dianjin.pay.ui.dialog.DJDialog;
import com.sdk.dianjin.pay.util.FastDJPay;

/**
 * android 点金 支付 com.alipay.ui.AliPayViewFactory
 * 
 * @author Administrator <br/>
 *         create at 2013 2013-3-20 下午3:07:09
 */
public class DJSDKFactory extends ISDKFactory {

	public Class<?> getDefaultPayView() {
		return DJPayActivity.class;
	}

	public Dialog getPayDialog(Context ctx, Room room, String msg) {
		return new DJDialog(ctx, room, msg);
	}

	@Override
	public Class<?> getLoginView() {
		return DJLoginActivity.class;
	}

	@Override
	public void fastPay(Context ctx,String money) {
		new FastDJPay(ctx,money);

	}

	@Override
	public String getPayType() {
		// TODO Auto-generated method stub
		return null;
	}
}
