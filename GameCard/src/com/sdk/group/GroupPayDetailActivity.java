package com.sdk.group;

import com.zzyddz.shui.R;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameUser;
import com.lordcard.network.http.GameCache;
import com.lordcard.ui.base.BaseActivity;
import com.sdk.jd.sms.util.JDSMSPayUtil;
import com.sdk.util.PaySite;
import com.sdk.util.PayTipUtils;
import com.sdk.util.PayUtils;
import com.sdk.util.vo.PayPoint;

/**
 * 三合一(移动，联通，电信)充值界面
 * @author Administrator
 */
public class GroupPayDetailActivity extends BaseActivity implements OnClickListener {

	private Button genBackBtn;
	private RadioGroup genRadio;
	private RadioButton yiButton, ltButton, dxButton;
	private LinearLayout layoutYi;
	private TextView zhidou;
	private MultiScreenTool mst = MultiScreenTool.singleTonVertical();
	private ListView vaclist, tianyilist;
	LinearLayout view;
	LinearLayout view2;
	public static Context smsTxt;
//	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_group_detail);
		view = (LinearLayout) findViewById(R.id.mm_pay_layout2);
		view2 = (LinearLayout) findViewById(R.id.redbackground);
		init();
		mst.adjustView(view);
		mst.adjustView(view2);
		smsTxt = this;
	}

	@Override
	protected void onStart() {
		super.onStart();
		GameUser gameUser = (GameUser)GameCache.getObj(CacheKey.GAME_USER);
		long point = gameUser.getBean();
		if (point > 10000) {
			point = point / 10000;
			zhidou.setText(String.valueOf(point) + "W");
		} else {
			zhidou.setText(String.valueOf(point));
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
//		if (progressDialog != null) {
//			progressDialog.dismiss();
//			progressDialog = null;
//		}
	}

	private void init() {
		genBackBtn = (Button) findViewById(R.id.gen_detail_back);
		zhidou = (TextView) findViewById(R.id.gen_detail_zhi_dou);
		genBackBtn.setOnClickListener(this);
		genRadio = (RadioGroup) findViewById(R.id.gen_radio);
		layoutYi = (LinearLayout) findViewById(R.id.gen_layout_yi);
		yiButton = (RadioButton) findViewById(R.id.gen_detail_yi);
		ltButton = (RadioButton) findViewById(R.id.gen_detail_lian);
		dxButton = (RadioButton) findViewById(R.id.gen_detail_dian);
		vaclist = (ListView) findViewById(R.id.vac_listview);
		tianyilist = (ListView) findViewById(R.id.tianyi_listview);
		findViewById(R.id.gen_detail_yi_2_btn).setOnClickListener(this);
		findViewById(R.id.gen_detail_yi_5_btn).setOnClickListener(this);
		findViewById(R.id.gen_detail_yi_8_btn).setOnClickListener(this);
		findViewById(R.id.gen_detail_yi_10_btn).setOnClickListener(this);
		findViewById(R.id.gen_detail_yi_15_btn).setOnClickListener(this);
		findViewById(R.id.gen_detail_yi_20_btn).setOnClickListener(this);
		findViewById(R.id.gen_detail_yi_25_btn).setOnClickListener(this);
		findViewById(R.id.gen_detail_yi_30_btn).setOnClickListener(this);
		Intent intent = getIntent();
		String simKey = intent.getStringExtra("key"); // 获取运营商标志
		if (Constant.SIM_MOBILE.equals(simKey)) { // 根据key锁定运营商
			genRadio.check(R.id.gen_detail_yi);
			yiButton.setClickable(true);
			ltButton.setClickable(false);
			dxButton.setClickable(false);
			layoutYi.setVisibility(View.VISIBLE);
		} else if (Constant.SIM_UNICOM.equals(simKey)) {
			genRadio.check(R.id.gen_detail_lian);
			yiButton.setClickable(false);
			ltButton.setClickable(true);
			dxButton.setClickable(false);
			layoutYi.setVisibility(View.GONE);
//			VACPayAdapter adapter = new VACPayAdapter(GroupPayDetailActivity.this, VACConfig.BILLPOINT_LIST);
			List<PayPoint> pointList = PayUtils.getPayPoint(PaySite.RECHARGE_LIST);
			PayAdapter adapter = new PayAdapter(GroupPayDetailActivity.this,pointList,PaySite.RECHARGE_LIST);
			vaclist.setAdapter(adapter);
			vaclist.setVisibility(View.VISIBLE);
		} else if (Constant.SIM_TELE.equals(simKey)) {
			genRadio.check(R.id.gen_detail_dian);
			yiButton.setClickable(false);
			ltButton.setClickable(false);
			dxButton.setClickable(true);
			layoutYi.setVisibility(View.GONE);
//			TYPayAdapter adapter = new TYPayAdapter(GroupPayDetailActivity.this, TYConfig.ESURFING_LIST);
			List<PayPoint> pointList = PayUtils.getPayPoint(PaySite.RECHARGE_LIST);
			PayAdapter adapter = new PayAdapter(GroupPayDetailActivity.this,pointList,PaySite.RECHARGE_LIST);
			tianyilist.setAdapter(adapter);
			tianyilist.setVisibility(View.VISIBLE);
		} else {
			genRadio.check(R.id.gen_detail_yi);
			yiButton.setClickable(true);
			ltButton.setClickable(false);
			dxButton.setClickable(false);
			layoutYi.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.gen_detail_back:
				finishSelf();
				break;
			default:
				break;
		}
		Database.chargingProcessDia = null;
//		progressDialog = DialogUtils.getWaitProgressDialog(this, "请稍后...");
//		progressDialog.setCanceledOnTouchOutside(true);
//		progressDialog.show();
		
		int payMoney = 0;
		switch (v.getId()) {
		case R.id.gen_detail_back:
			finishSelf();
			break;
		case R.id.gen_detail_yi_2_btn:
			payMoney = 2;
//			MMPayUtil.goPay(MMConfig.PAY_CODE_2, SDKConstant.LIST, null);
			break;
		case R.id.gen_detail_yi_5_btn:
			payMoney = 5;
//			MMPayUtil.goPay(MMConfig.PAY_CODE_5, SDKConstant.LIST, null);
			break;
		case R.id.gen_detail_yi_8_btn:
			payMoney = 8;
//			MMPayUtil.goPay(MMConfig.PAY_CODE_8, SDKConstant.LIST, null);
			break;
		case R.id.gen_detail_yi_10_btn:
			payMoney = 10;
//			MMPayUtil.goPay(MMConfig.PAY_CODE_10, SDKConstant.LIST, null);
			break;
		case R.id.gen_detail_yi_15_btn:
			payMoney = 15;
//			MMPayUtil.goPay(MMConfig.PAY_CODE_15, SDKConstant.LIST, null);
			break;
		case R.id.gen_detail_yi_20_btn:
			payMoney = 20;
//			MMPayUtil.goPay(MMConfig.PAY_CODE_20, SDKConstant.LIST, null);
			break;
		case R.id.gen_detail_yi_25_btn:
			payMoney = 25;
//			MMPayUtil.goPay(MMConfig.PAY_CODE_25, SDKConstant.LIST, null);
			break;
		case R.id.gen_detail_yi_30_btn:
			payMoney = 30;
//			MMPayUtil.goPay(MMConfig.PAY_CODE_30, SDKConstant.LIST, null);
			break;
		default:
			break;
	}
	
	if(payMoney > 0){
		JDSMSPayUtil.setContext(this);
		//Toast.makeText(this, "暂不提供充值", Toast.LENGTH_LONG).show();
		PayTipUtils.showTip(payMoney,PaySite.RECHARGE_LIST);
	}
		/*if ((ActivityUtils.getSimType() == Constant.SIM_MOBILE) || (ActivityUtils.simExist() == false)) {
			switch (v.getId()) {
				case R.id.gen_detail_back:
					finishSelf();
					break;
				case R.id.gen_detail_yi_2_btn:
					payMoney = 2;
//					MMPayUtil.goPay(MMConfig.PAY_CODE_2, SDKConstant.LIST, null);
					break;
				case R.id.gen_detail_yi_5_btn:
					payMoney = 5;
//					MMPayUtil.goPay(MMConfig.PAY_CODE_5, SDKConstant.LIST, null);
					break;
				case R.id.gen_detail_yi_8_btn:
					payMoney = 8;
//					MMPayUtil.goPay(MMConfig.PAY_CODE_8, SDKConstant.LIST, null);
					break;
				case R.id.gen_detail_yi_10_btn:
					payMoney = 10;
//					MMPayUtil.goPay(MMConfig.PAY_CODE_10, SDKConstant.LIST, null);
					break;
				case R.id.gen_detail_yi_15_btn:
					payMoney = 15;
//					MMPayUtil.goPay(MMConfig.PAY_CODE_15, SDKConstant.LIST, null);
					break;
				case R.id.gen_detail_yi_20_btn:
					payMoney = 20;
//					MMPayUtil.goPay(MMConfig.PAY_CODE_20, SDKConstant.LIST, null);
					break;
				case R.id.gen_detail_yi_25_btn:
					payMoney = 25;
//					MMPayUtil.goPay(MMConfig.PAY_CODE_25, SDKConstant.LIST, null);
					break;
				case R.id.gen_detail_yi_30_btn:
					payMoney = 30;
//					MMPayUtil.goPay(MMConfig.PAY_CODE_30, SDKConstant.LIST, null);
					break;
				default:
					break;
			}
			
			if(payMoney > 0){
				JDSMSPayUtil.setContext(this);
				//Toast.makeText(this, "暂不提供充值", Toast.LENGTH_LONG).show();
				PayTipUtils.showTip(payMoney,PaySite.RECHARGE_LIST);
			}
		} else if (ActivityUtils.getSimType() == Constant.SIM_UNICOM) {}*/
	}


	public class ViewHolder {
		public Button guideItem;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 重写返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			try {
				finishSelf();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		if (progressDialog != null) {
//			progressDialog.dismiss();
//			progressDialog = null;
//		}
	}
}
