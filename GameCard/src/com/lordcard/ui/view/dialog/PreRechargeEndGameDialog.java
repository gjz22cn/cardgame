package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lordcard.common.util.PatternUtils;
import com.lordcard.constant.Database;
import com.lordcard.prerecharge.PrerechargeManager;
import com.lordcard.ui.payrecord.PayRecordOrder;
import com.sdk.constant.SDKConstant;
import com.sdk.jd.sms.util.JDSMSPayUtil;
import com.sdk.util.PaySite;
import com.sdk.util.PayTipUtils;
import com.sdk.util.RechargeUtils;
import com.sdk.util.SDKFactory;

public class PreRechargeEndGameDialog extends BaseDialog {

	protected Handler mHandler = null;
	private PayRecordOrder mPayRecordOrder = null;

	private TextView textViewPrerechargeDescriptionOne = null;
	private TextView textViewPrerechargeDescriptionTwo = null;
	private TextView textViewPrerechargeDescriptionThree = null;
	
	private TextView textPrerechargeMount = null;
    private Context context;
	public PreRechargeEndGameDialog(Context context, PayRecordOrder payRecordOrder,Handler handler) {
		super(context, R.style.process_dialog_black);
		mHandler = handler;
		mPayRecordOrder = payRecordOrder;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void initContentView(Context context) {
         this.context = context;
		 setCancelable(false);

		/** Set dialog animation when dialog show() or dismiss() **/
		setDialogAnimation(R.style.pre_recharge_dialog_anim_style);

		/** Set dialog gravity of the window **/
		setGravity(Gravity.CENTER);

		/** Set view to the dialog from xml **/
		setContentView(R.layout.pre_recharge_endgame);
		
		/**Get the view top layout**/
		mainLayout = (View) findViewById(R.id.pre_recharge_root_end);
		long iqBeansNum = mPayRecordOrder.getBaseBean() + mPayRecordOrder.getWinBean();
		textViewPrerechargeDescriptionOne = (TextView) mainLayout.findViewById(R.id.beans_notice);
		textViewPrerechargeDescriptionOne.setText(getStringWithParams(R.string.text_prerecharge_description_one, PatternUtils.formatIqBeans(iqBeansNum)));
		
		textViewPrerechargeDescriptionTwo = (TextView) mainLayout.findViewById(R.id.pre_recharge_beans);
		iqBeansNum = mPayRecordOrder.getBaseBean();
		textViewPrerechargeDescriptionTwo.setText(getStringWithParams(R.string.text_prerecharge_description_two,PatternUtils.formatIqBeans(iqBeansNum)));
		
		textViewPrerechargeDescriptionThree = (TextView) mainLayout.findViewById(R.id.win_beans);
		iqBeansNum = mPayRecordOrder.getWinBean();
		textViewPrerechargeDescriptionThree.setText(getStringWithParams(R.string.text_prerecharge_description_three,PatternUtils.formatIqBeans(iqBeansNum)));
		
		textPrerechargeMount = (TextView) mainLayout.findViewById(R.id.pre_recharge_text_price);
		textPrerechargeMount.setText(getStringWithParams(R.string.text_charge_yuan, (int)mPayRecordOrder.getMoney()));
		/**Initialize buttons for dialog**/
		initButtons(null, (Button)mainLayout.findViewById(R.id.charge), (Button) findViewById(R.id.pre_recharge_ingame_btn_cancel));
		

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.charge:
				mHandler.sendEmptyMessage(GameEndDialog.ENABLE_DIALOG);	
//				SDKFactory.fastPay((int) PrerechargeManager.mPayRecordOrder.getMoney(), SDKConstant.PRRECHARGE);
				JDSMSPayUtil.setContext(context);
				double money = PrerechargeManager.mPayRecordOrder.getMoney();
				PayTipUtils.showTip(money,PaySite.PREPARERECHARGE); //配置的提示方式
				
				dismiss();
				break;
			case R.id.pre_recharge_ingame_btn_cancel:
				mHandler.sendEmptyMessage(GameEndDialog.ENABLE_DIALOG);	
				dismiss();
				break;
			default:
				break;
		}

	}

	@SuppressLint("HandlerLeak")
	public Handler messageHandler = new Handler() {
		public void handleMessage(Message msg) {

		};
	};

	/**
	 * Give mHandler a instance from Depend On Activity
	 * 
	 * @param handler
	 */
	public void setDependOnActivityHandler(Handler handler) {
		mHandler = handler;
	}


}
