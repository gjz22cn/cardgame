package com.sdk.util.vo;

import com.zzyddz.shui.R;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.Database;
import com.sdk.group.GroupPayActivity;
import com.sdk.util.PaySite;
import com.sdk.util.PayUtils;
import com.sdk.util.SDKFactory;

public class PayDialog extends Dialog {

	private Context context;
	private String tipMsg; 		//提示消息
	private PayInit payInit;
	private PayPoint payPoint;
	private String paySiteTag;
	
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();

	public PayDialog(Context context,PayInit payInit,PayPoint payPoint,String paySiteTag,String tipMsg) {
		super(context, R.style.dialog);
		this.context = context;
		this.payInit = payInit;
		this.payPoint = payPoint;
		this.paySiteTag = paySiteTag;
		this.tipMsg = tipMsg;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_dialog);
		
		TextView tv = ((TextView) findViewById(R.id.dialog_tip_text));
		tv.setText(Html.fromHtml(tipMsg));
		
		layout(context);
		mst.adjustView(findViewById(R.id.pay_dialog_layout));
	}

	/**
	 * 布局
	 */
	private void layout(final Context context) {
		PaySiteConfigItem configItem = PayUtils.getPaySiteUseConfig(paySiteTag);
		
		Button otherBtn = (Button) findViewById(R.id.pay_other);
		if(PaySite.OFF_LINE.equals(configItem.getPayTo())){//单机账号充值
//			otherBtn.setText(R.string.dialog_cancell);
			otherBtn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					dismiss();
					finishGameAcitivity();
				}
			});
			
		}else{
			otherBtn.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					Intent in = new Intent();
					in.setClass(context, GroupPayActivity.class);
					context.startActivity(in);
					mst.unRegisterView(findViewById(R.id.pay_dialog_layout));
					dismiss();
					finishGameAcitivity();
				}
			});
		}
		

		Button cancelBtn = (Button) findViewById(R.id.pay_cancel);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
				finishGameAcitivity();
			}
		});

		Button okBtn = (Button) findViewById(R.id.pay_ok);
		okBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					DialogUtils.mesToastTip("支付组件加载中，请稍候...");
					//去充值
					SDKFactory.goPay(payInit,payPoint,paySiteTag);
					mst.unRegisterView(findViewById(R.id.pay_dialog_layout));
					dismiss();
					finishGameAcitivity();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	public void finishGameAcitivity() {
		if (Database.currentActivity != null) {
			// 游戏页面
			if (ActivityUtils.isGameView() && !PaySite.SINGLE_GAME_CLICK.equals(paySiteTag)) {
				Database.currentActivity.finish();
				return;
			}
		}
	}
}
