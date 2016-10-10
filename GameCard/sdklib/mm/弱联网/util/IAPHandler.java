package com.sdk.mmpay.sms.util;

import android.os.Handler;
import android.os.Message;

import com.lordcard.common.util.DialogUtils;

/**
 * 更新界面：进度条消失和显示结果文字
 * 
 * @author Administrator
 * 
 */
public class IAPHandler extends Handler {
	
	public static final int INIT_FINISH = 10000;
	public static final int BILL_FINISH = 10001;
	public static final int QUERY_FINISH = 10002;
	public static final int UNSUB_FINISH = 10003;

	public static final int SUCCESS = 10004;
	public static final int FAIL = 10005;
	public static final int FAIL_TOKENID = 10006;

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		int what = msg.what;
		switch (what) {
			case INIT_FINISH:
				break;
			case SUCCESS:
				DialogUtils.toastTip(String.valueOf(msg.obj));
				break;
			case FAIL:
				DialogUtils.toastTip(String.valueOf(msg.obj));
				break;
			case FAIL_TOKENID:
				DialogUtils.toastTip(String.valueOf(msg.obj));
				break;
			default:
				break;
		}
	}
}
