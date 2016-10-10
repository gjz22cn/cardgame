package com.sdk.dianjin.pay.ui.dialog;

import com.zzyddz.shui.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lordcard.common.pay.PayDialog;
import com.lordcard.common.pay.SDKFactory;
import com.lordcard.entity.Room;
import com.lordcard.ui.TaskMenuActivity;
import com.sdk.dianjin.pay.DJConstant;
import com.sdk.dianjin.pay.ui.DJPayActivity;

public class DJDialog extends PayDialog {

	private Context context;
	private String msg;
	private Room room; // 进入房间
	long rechargeMoney = 0; // 充值金额

	public DJDialog(Context context, Room room, String msg) {
		super(context, R.style.dialog);
		this.context = context;
		this.room = room;
		this.msg = msg;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_dialog);

		long limit = room.getLimit();

		long rechargeBean = 0;

		if (limit >= 0 && limit <= 20000) {
			rechargeMoney = 2;
		} else if (limit > 20000 && limit <= 50000) {
			rechargeMoney = 5;
		} else if (limit > 50000 && limit <= 100000) {
			rechargeMoney = 10;
		} else if (limit > 100000 && limit <= 200000) {
			rechargeMoney = 20;
		} else if (limit > 200000 && limit <= 300000) {
			rechargeMoney = 30;
		} else if (limit > 300000 && limit <= 400000) {
			rechargeMoney = 40;
		} else if (limit > 400000 && limit <= 500000) {
			rechargeMoney = 50;
		} else {
			rechargeMoney = 50;
		}

		if (this.msg != null) {
			((TextView) findViewById(R.id.dialog_tip_text)).setText(msg);
		} else {
			rechargeBean = rechargeMoney * 10000;
			String msgTip = context.getString(R.string.pay_dialog_msg);
			msgTip = msgTip.replace("{limit}", String.valueOf(room.getLimit()));
			msgTip = msgTip.replace("{buybean}", String.valueOf(rechargeBean / 10000));
			((TextView) findViewById(R.id.dialog_tip_text)).setText(msgTip);
		}

		// rechargeBean = rechargeMoney * 10000;
		// String msgTip = context.getString(R.string.pay_dialog_msg);
		// msgTip = msgTip.replace("{limit}", String.valueOf(room.getLimit()));
		// msgTip = msgTip.replace("{buybean}", String.valueOf(rechargeBean /
		// 10000));
		// ((TextView) findViewById(R.id.dialog_tip_text)).setText(msgTip);
		layout(context);
	}

	private void layout(final Context context) {
		Button otherBtn = (Button) findViewById(R.id.pay_other);
		Button freeBtn = (Button) findViewById(R.id.pay_free);
		Button okBtn = (Button) findViewById(R.id.pay_ok);
		Button cancelBtn = (Button) findViewById(R.id.pay_cancel);
		otherBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent in = new Intent();
				in.setClass(context, DJPayActivity.class);
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
				try {
					String djPay = "";
					if (rechargeMoney == 10) {
						djPay = DJConstant.DJPAY_10;
					} else if (rechargeMoney == 20) {
						djPay = DJConstant.DJPAY_20;
					} else if (rechargeMoney == 30) {
						djPay = DJConstant.DJPAY_30;
					} else if (rechargeMoney == 40) {
						djPay = DJConstant.DJPAY_40;
					} else if (rechargeMoney == 50) {
						djPay = DJConstant.DJPAY_50;
					} else if (rechargeMoney == 5) {
						djPay = DJConstant.DJPAY_5;
					} else {
						djPay = DJConstant.DJPAY_2;
					}
					SDKFactory.fastPay(context,djPay);
					dismiss();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}
}
