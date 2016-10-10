package com.sdk.jifeng.pay.ui.dialog;

import com.zzyddz.shui.R;
import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lordcard.common.pay.PayDialog;
import com.lordcard.common.pay.SDKFactory;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.entity.Room;
import com.lordcard.ui.TaskMenuActivity;
import com.sdk.constant.SdkConstant;
import com.sdk.constant.SdkDatabase;
import com.sdk.jifeng.uti.JFPayUtil;

public class JFDialog extends PayDialog {

	private Context context;
	private Room room; // 进入房间
	long rechargeMoney = 0; // 充值金额
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	long rechargeBean = 0;
	private String  orderName, orderDesc, payNo;
	private int money;
	public JFDialog(Context context, Room room) {
		super(context, R.style.dialog);
		this.context = context;
		this.room = room;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_dialog);
		try {
			long limit = room.getLimit();

			int limit2 = (int) limit;
			if (SdkConstant.PAY_ROOM == 0) {
				String money2=SdkDatabase.ROMM_PAY.get(room.getCode());
				for (int i = 0; i < SdkDatabase.JFPAY_LIST.size(); i++) {
					if(Integer.valueOf(money2).equals(SdkDatabase.JFPAY_LIST.get(i).getPrice())){
						money = SdkDatabase.JFPAY_LIST.get(i).getPrice();
						orderName = SdkDatabase.JFPAY_LIST.get(i)
								.getOrderName();
						orderDesc = SdkDatabase.JFPAY_LIST.get(i)
								.getOrderDesc();
						payNo = SdkDatabase.JFPAY_LIST.get(i).getPayNo();
						break;
					}
				}

			} else {
				for (int i = 0; i < SdkDatabase.JFPAY_LIST.size(); i++) {
					if (SdkDatabase.JFPAY_LIST.get(i).getPrice() * 10000 < limit2
							|| SdkDatabase.JFPAY_LIST.get(i).getPrice() * 10000 == limit2) {
						money = SdkDatabase.JFPAY_LIST.get(i).getPrice();
						orderName = SdkDatabase.JFPAY_LIST.get(i)
								.getOrderName();
						orderDesc = SdkDatabase.JFPAY_LIST.get(i)
								.getOrderDesc();
						payNo = SdkDatabase.JFPAY_LIST.get(i).getPayNo();
						break;
					} else if (i == SdkDatabase.JFPAY_LIST.size() - 1) {
						money = SdkDatabase.JFPAY_LIST.get(i).getPrice();
						orderName = SdkDatabase.JFPAY_LIST.get(i)
								.getOrderName();
						orderDesc = SdkDatabase.JFPAY_LIST.get(i)
								.getOrderDesc();
						payNo = SdkDatabase.JFPAY_LIST.get(i).getPayNo();
					}
				}
			}

			rechargeBean = (long) (money);
			String msgTip = context.getString(R.string.pay_dialog_msg);
			msgTip = msgTip.replace("{limit}", String.valueOf(room.getLimit()));
			msgTip = msgTip.replace("{buybean}", String.valueOf(rechargeBean));
			TextView tv = ((TextView) findViewById(R.id.dialog_tip_text));
			SpannableString msp = new SpannableString(msgTip);
			int star = msgTip.indexOf("确认购买");
			int end = star + "确认购买".length();
			int star3 = msgTip.indexOf(String.valueOf(rechargeBean)+"万金豆等同于"+String.valueOf(rechargeBean)+"元");
			int end3 = star3 + (String.valueOf(rechargeBean)+"万金豆等同于"+String.valueOf(rechargeBean)+"元").length();
			msp.setSpan(new ForegroundColorSpan(Color.YELLOW), star, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置前景色为洋红色
			msp.setSpan(new ForegroundColorSpan(Color.YELLOW), star3, end3,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置前景色为洋红色
			tv.setText(msp);
			layout(context);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		mst.adjustView(findViewById(R.id.pay_dialog_layout));
	}

	/**
	 * 布局
	 */
	private void layout(final Context context) {
		Button otherBtn = (Button) findViewById(R.id.pay_other);
		Button freeBtn = (Button) findViewById(R.id.pay_free);
		Button okBtn = (Button) findViewById(R.id.pay_ok);
		Button cancelBtn = (Button) findViewById(R.id.pay_cancel);

		otherBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent in = new Intent();
				in.setClass(context, SDKFactory.getPayView(context));
				context.startActivity(in);
				dismiss();
			}
		});
		freeBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent in = new Intent();
				in.setClass(context, TaskMenuActivity.class);
				context.startActivity(in);
				dismiss();
			}
		});

		cancelBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
				finishGameAcitivity();
			}
		});

		okBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				new JFPayUtil().JFPay(context, money, orderName, orderDesc, payNo);
				dismiss();

			}
		});
	}


}
