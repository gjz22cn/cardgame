package com.lordcard.ui.view.dialog;


import com.zzyddz.shui.R;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lordcard.common.util.PatternUtils;
import com.lordcard.prerecharge.PrerechargeManager;
import com.lordcard.ui.payrecord.PayRecordActivity;
import com.lordcard.ui.payrecord.PayRecordOrder;
import com.sdk.constant.SDKConstant;
import com.sdk.jd.sms.util.JDSMSPayUtil;
import com.sdk.util.PaySite;
import com.sdk.util.PayTipUtils;
import com.sdk.util.SDKFactory;

public class PreRechargeRecordDialog extends BaseDialog{
	
	private TextView textViewPrerechargeDescriptionOne = null;
	private TextView textViewPrerechargeDescriptionTwo = null;
	private TextView textViewPrerechargeDescriptionThree = null;
	private PayRecordOrder payRecordOrder = null;
//	private Handler mHandler = null;
	private TextView textPrerechargeMount = null;
	private Context context;
	public PreRechargeRecordDialog(Context context,PayRecordOrder payRecordOrder,Handler mHandler) {
		super(context, R.style.process_dialog_black);
		this.payRecordOrder = payRecordOrder;
//		this.mHandler = mHandler;
	}

	@Override
	public void initContentView(Context context) {
		 setCancelable(false);
         this.context =context;
		/** Set dialog animation when dialog show() or dismiss() **/
		setDialogAnimation(R.style.pre_recharge_dialog_anim_style);

		/** Set dialog gravity of the window **/
		setGravity(Gravity.CENTER);

		/** Set view to the dialog from xml **/
		setContentView(R.layout.pre_recharge_record);
		
		/**Get the view top layout**/
		mainLayout = (RelativeLayout) findViewById(R.id.pre_recharge_root_record);
		
		long iqBeansNum = payRecordOrder.getBaseBean() + payRecordOrder.getWinBean();
		textViewPrerechargeDescriptionOne = (TextView) mainLayout.findViewById(R.id.beans_notice);
		textViewPrerechargeDescriptionOne.setText(getStringWithParams(R.string.text_prerecharge_description_one, PatternUtils.formatIqBeans(iqBeansNum)));
		
		textViewPrerechargeDescriptionTwo = (TextView) mainLayout.findViewById(R.id.pre_recharge_beans);
		iqBeansNum = payRecordOrder.getBaseBean();
		textViewPrerechargeDescriptionTwo.setText(getStringWithParams(R.string.text_prerecharge_description_two,PatternUtils.formatIqBeans(iqBeansNum)));
		
		textViewPrerechargeDescriptionThree = (TextView) mainLayout.findViewById(R.id.win_beans);
		iqBeansNum = payRecordOrder.getWinBean();
		textViewPrerechargeDescriptionThree.setText(getStringWithParams(R.string.text_prerecharge_description_three,PatternUtils.formatIqBeans(iqBeansNum)));
		
		textPrerechargeMount = (TextView) mainLayout.findViewById(R.id.pre_recharge_text_price);
		textPrerechargeMount.setText(getStringWithParams(R.string.text_charge_yuan, (int)payRecordOrder.getMoney()));
		
		/**Initialize buttons for dialog**/
		initButtons(null, (Button)mainLayout.findViewById(R.id.charge), (Button)mainLayout.findViewById(R.id.pre_recharge_ingame_btn_cancel));
		

	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.charge:
//				mHandler.sendEmptyMessage(PayRecordActivity.REMOVE_ORDER_ROCORD);
				PrerechargeManager.clearPrerechargeInfo();
				PrerechargeManager.mPayRecordOrder = payRecordOrder;
//				SDKFactory.fastPay((int) PrerechargeManager.mPayRecordOrder.getMoney(), SDKConstant.PRRECHARGE);
				JDSMSPayUtil.setContext(context);
				double money = PrerechargeManager.mPayRecordOrder.getMoney();
				PayTipUtils.showTip(money,PaySite.PREPARERECHARGE); //配置的提示方式
				dismiss();
				PayRecordActivity payRecordActivity = (PayRecordActivity)mContext;
				payRecordActivity.finishSelf();
				break;
			case R.id.pre_recharge_ingame_btn_cancel:
				dismiss();
			default:
				break;
		}

	}

}
