package com.lordcard.ui.view.dialog;


import com.zzyddz.shui.R;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdk.constant.SDKConstant;
import com.sdk.jd.sms.util.JDSMSPayUtil;
import com.sdk.util.PaySite;
import com.sdk.util.PayTipUtils;
import com.sdk.util.SDKFactory;

public class JiPaiQiChargeDialog extends BaseDialog {

	private String chargeNoticeMsg = null;
	private TextView chargeNoticeTextView = null;
	private Context context;
	public JiPaiQiChargeDialog(Context context,String chargeNoticeMsg) {
		super(context, R.style.process_dialog_black);
		this.chargeNoticeMsg = chargeNoticeMsg;
		this.context = context;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.dialog_close_btn:
				dismiss();
				break;
			case R.id.btn_jipaiqi_charge:
				JDSMSPayUtil.setContext(context);
				PayTipUtils.showTip(0,PaySite.RECORED_CARD); //配置的提示方式
//				if(SmsPayUtil.canUseSmsPay()){
////					SDKFactory.smsPay(0, SDKConstant.PLAYING);
//				}
//				SDKFactory.smsPay(0, SDKConstant.PLAYING);
				dismiss();
				break;
		}
		
	}

	@Override
	public void initContentView(Context context) {
		/****/
		setCancelable(false);

		/** Set dialog animation when dialog show() or dismiss() **/
		setDialogAnimation(R.style.pre_recharge_dialog_anim_style);

		/** Set dialog gravity of the window **/
		setGravity(Gravity.CENTER);

		/** Set view to the dialog from xml **/
		setContentView(R.layout.dialog_chargefor_jipaiqi);

		/** Get the view top layout **/
		mainLayout = (RelativeLayout) findViewById(R.id.jipaiqi_charge_root_ingame);
		chargeNoticeTextView = (TextView) findViewById(R.id.text_charge_jipaiqi_notice);
		if(null != chargeNoticeMsg && !TextUtils.isEmpty(chargeNoticeMsg))
			chargeNoticeTextView.setText(chargeNoticeMsg);
		else 
			chargeNoticeTextView.setText(context.getString(R.string.text_jipaiqitips));
		((TextView) findViewById(R.id.dialog_title_tv)).setText(R.string.text_jipaiqi);
		/** Initialize buttons for dialog **/
		initButtons(null, (Button) mainLayout.findViewById(R.id.btn_jipaiqi_charge),
				(Button) mainLayout.findViewById(R.id.dialog_close_btn));
	}
	
	public void refreshNoticeInfo(String msg){
		if(null != msg && !TextUtils.isEmpty(msg))
			chargeNoticeTextView.setText(msg);
	}

}
