package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.PatternUtils;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameUser;
import com.lordcard.network.http.GameCache;
import com.lordcard.prerecharge.PrerechargeManager;

public class PreRecharggeInGameDialog extends BaseDialog {
	private View rechargeBtn_One = null;
	private View rechargeBtn_Two = null;
	
	private TextView text_charge_One = null;
	private TextView text_charge_Two = null;
	
	private TextView canWinBeans_One = null;
	private TextView canWinBeans_Two = null;
	
	private TextView rechargeNotice = null;
	private TextView rechargeDescription_One = null;
	private TextView rechargeDescription_Two = null;
	private long multiple = 0;
	private int[] price ;
	
	

	public PreRecharggeInGameDialog(Context context,long multiple) {
		super(context, R.style.process_dialog_black);
		this.multiple = multiple;
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
		setContentView(R.layout.pre_recharge_ingame);

		/** Get the view top layout **/
		mainLayout = (RelativeLayout) findViewById(R.id.pre_recharge_root_ingame);

		/** Initialize recharge buttons **/
		rechargeBtn_One = findViewById(R.id.pre_recharge_btn_one);
		rechargeBtn_One.setOnClickListener(this);
		rechargeBtn_Two = findViewById(R.id.pre_recharge_btn_two);
		rechargeBtn_Two.setOnClickListener(this);
		
		GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		price = PrerechargeManager.calculatePrerechargePrice(cacheUser.getBean(), multiple*Database.JOIN_ROOM_BASEPOINT);
		if(price[0]<=0){
			price[0]=2;
		}
		if(price[1]<=0){
			price[1]=2;
		}
		long totalWager = multiple*Database.JOIN_ROOM_BASEPOINT;
		
		text_charge_One = (TextView) findViewById(R.id.pre_recharge_text_one);
		text_charge_One.setText(getStringWithParams(R.string.text_charge_w, price[0]));
		int length = -1;
		int index = -1;
//		String paramString = PatternUtils.formatIqBeans(((totalWager > (cacheUser.getBean() + price[0]*100000))?(cacheUser.getBean() + price[0]*100000) : totalWager));
		String paramString = PatternUtils.formatIqBeans(cacheUser.getBean() + price[0]*10000);
		String tempText = getStringWithParams(R.string.text_can_win_beans, paramString);
		index = tempText.indexOf(paramString);
		length = paramString.length();
		SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(tempText);
		canWinBeans_One = (TextView) findViewById(R.id.textview_can_win_beans_one);
		spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), index, index + length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		canWinBeans_One.setText(spannableStringBuilder);
		
		
		
		text_charge_Two = (TextView) findViewById(R.id.pre_recharge_text_two);
		text_charge_Two.setText(getStringWithParams(R.string.text_charge_w, price[1]));
		
		canWinBeans_Two = (TextView) findViewById(R.id.textview_can_win_beans_two);
//		paramString = PatternUtils.formatIqBeans(((totalWager > (cacheUser.getBean() + price[1]*100000))?(cacheUser.getBean() + price[1]*100000) : totalWager));
		paramString = PatternUtils.formatIqBeans(cacheUser.getBean() + price[1]*10000);
		tempText = getStringWithParams(R.string.text_can_win_beans, paramString);
		spannableStringBuilder = new SpannableStringBuilder(tempText);
		index = tempText.indexOf(paramString);
		length = paramString.length();
		canWinBeans_Two.setText(spannableStringBuilder);
		
		
		rechargeDescription_One = (TextView) findViewById(R.id.text_description_one);
		String tempStringOne = PatternUtils.formatIqBeans(cacheUser.getBean());
		String tempStringTwo = PatternUtils.formatIqBeans(cacheUser.getBean());
		tempText = getStringWithParams(R.string.text_pre_recharge_notice_one, tempStringOne,tempStringTwo);
		spannableStringBuilder = new SpannableStringBuilder(tempText);
		length = tempStringOne.length();
		index = tempText.indexOf(tempStringOne);
		spannableStringBuilder.length();
		spannableStringBuilder.setSpan(new AbsoluteSizeSpan(20, true),index, index + length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#b71600")), index, index + length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), index, index + length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		length = tempStringTwo.length();
		index = tempText.indexOf(tempStringTwo, index + length);
		spannableStringBuilder.setSpan(new AbsoluteSizeSpan(20, true), index, index + length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#8c5209")),index, index + length,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), index, index + length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		rechargeDescription_One.setText(spannableStringBuilder);
		
		rechargeDescription_Two = (TextView) findViewById(R.id.text_description_two);
		tempText = getStringWithParams(getStringWithParams(R.string.text_pre_recharge_bureau_wager, PatternUtils.formatIqBeans(totalWager)));
		spannableStringBuilder = new SpannableStringBuilder(tempText);
		rechargeDescription_Two.setText(spannableStringBuilder);
		
		rechargeNotice = (TextView) findViewById(R.id.pre_recharge_notice);
		paramString = tempStringOne;
		tempText = getStringWithParams(R.string.text_pre_recharge_notice_two, paramString);
		spannableStringBuilder= new SpannableStringBuilder(tempText);
		index = tempText.indexOf(paramString);
		length = paramString.length();
		spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#b71600")), index, index + length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), index, index + length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		spannableStringBuilder.setSpan(new AbsoluteSizeSpan(19, true), index, index + length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		rechargeNotice.setText(spannableStringBuilder);
		
		/** Initialize buttons for dialog **/
		initButtons(null, null,
				(Button) mainLayout
						.findViewById(R.id.pre_recharge_ingame_btn_cancel));
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.pre_recharge_btn_one:
				if(price[0] > 0)
					PrerechargeManager.createPrerechargeOrder(price[0]);
				else
					DialogUtils.toastTip(mContext.getString(R.string.text_prerecharge_order_create_failed));
					
				dismiss();
				break;
			case R.id.pre_recharge_btn_two:
				if(price[1] > 0)
					PrerechargeManager.createPrerechargeOrder(price[1]);
				else
					DialogUtils.toastTip(mContext.getString(R.string.text_prerecharge_order_create_failed));
				dismiss();
				break;
			case R.id.pre_recharge_ingame_btn_cancel:
				dismiss();
				break;
			default:
				break;
		}
	}

}
