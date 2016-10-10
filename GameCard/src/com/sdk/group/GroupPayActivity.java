package com.sdk.group;

import com.zzyddz.shui.R;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.ImageUtil.ImageCallback;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Constant;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.PayPicType;
import com.lordcard.network.base.ThreadPool;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpURL;
import com.lordcard.network.http.HttpUtils;
import com.lordcard.ui.base.BaseActivity;
import com.lordcard.ui.payrecord.PayRecordActivity;
import com.sdk.alipay.AliConfig;
import com.sdk.alipay.ui.AliPayActivity;
import com.sdk.alipay.ui.AlixPayActivity;
import com.sdk.jd.sms.util.JDSMSPayUtil;
import com.sdk.util.PaySite;
import com.sdk.util.PayTipUtils;
import com.sdk.util.PayUtils;
import com.sdk.util.vo.PayPoint;

/**
 * 综合充值界面
 * @author Administrator
 */
@SuppressLint("WorldReadableFiles")
public class GroupPayActivity extends BaseActivity implements OnClickListener {

	private Button genBackBtn, genRecordBtn, genTelBtn, genAliBtn, genYi2Btn, genYi10Btn, genYi30Btn, genZhi10Btn, genZhi20Btn, genZhi30Btn;
	private SharedPreferences sharedPrefrences;
	private Editor editor;
	private TextView zhidou;
	private String simKey;// 标志运营商
	private int[] extrapic_id = { R.id.groupali_1_btn, R.id.groupali_2_btn, R.id.groupali_3_btn, R.id.groupali_4_btn };
	private MultiScreenTool mst = MultiScreenTool.singleTonVertical();
	RelativeLayout view;
	private ListView vaclist, tianyilist;
    private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_group);
		context = this;
		ThreadPool.startWork(new Runnable() {

			public void run() {
				try {
					String result = HttpUtils.post(HttpURL.PAY_INFO_URL + "info", null, true);
					List<PayPicType> picMap = JsonHelper.fromJson(result, new TypeToken<List<PayPicType>>() {});
					final int verson = picMap.get(0).getVerson();
					final String picUrl = picMap.get(0).getPicUrl();
					sharedPrefrences = getSharedPreferences("pic_verson", MODE_WORLD_READABLE);
					final int versonCode = sharedPrefrences.getInt("newverson", 0);
					runOnUiThread(new Runnable() {

						public void run() {
							if (verson != versonCode) {
								editor = sharedPrefrences.edit();
								editor.putInt("newverson", verson);
								editor.commit();
								for (int i = 0; i < extrapic_id.length; i++) {
									ImageUtil.replaceImg(HttpURL.PAY_INFO_URL + picUrl, (ImageView) findViewById(extrapic_id[i]), new ImageCallback() {

										public void imageLoaded(Bitmap bitmap, ImageView view) {
											view.setScaleType(ScaleType.FIT_XY);
											view.setImageBitmap(bitmap);
										}
									});
								}
							} else {
								for (int i = 0; i < extrapic_id.length; i++) {
									ImageUtil.setImg(HttpURL.PAY_INFO_URL + picUrl, (ImageView) findViewById(extrapic_id[i]), new ImageCallback() {

										public void imageLoaded(Bitmap bitmap, ImageView view) {
											view.setScaleType(ScaleType.FIT_XY);
											view.setImageBitmap(bitmap);
										}
									});
								}
							}
						}
					});
				} catch (Exception e) {}
			}
		});
		init();
		view = (RelativeLayout) findViewById(R.id.mm_pay_layout);
		if (mst != null) {
			mst.adjustView(view);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		GameUser gameUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		long point = gameUser.getBean();
		if (point > 10000) {
			point = point / 10000;
			zhidou.setText(String.valueOf(point) + "W");
		} else {
			zhidou.setText(String.valueOf(point));
		}
	}

	/**
	 * 初始化View
	 */
	private void init() {
		genTelBtn = (Button) findViewById(R.id.gen_tel_btn);
		genAliBtn = (Button) findViewById(R.id.gen_ali_btn);
		genYi2Btn = (Button) findViewById(R.id.gen_yi_2_btn);
		genYi10Btn = (Button) findViewById(R.id.gen_yi_10_btn);
		genYi30Btn = (Button) findViewById(R.id.gen_yi_30_btn);
		genZhi10Btn = (Button) findViewById(R.id.gen_zhi_10_btn);
		genZhi20Btn = (Button) findViewById(R.id.gen_zhi_20_btn);
		genZhi30Btn = (Button) findViewById(R.id.gen_zhi_30_btn);
		genBackBtn = (Button) findViewById(R.id.gen_back);
		genRecordBtn = (Button) findViewById(R.id.gen_record);
		zhidou = (TextView) findViewById(R.id.gen_zhi_dou);
		genTelBtn.setOnClickListener(this);
		genAliBtn.setOnClickListener(this);
		genYi2Btn.setOnClickListener(this);
		genYi10Btn.setOnClickListener(this);
		genYi30Btn.setOnClickListener(this);
		genZhi10Btn.setOnClickListener(this);
		genZhi20Btn.setOnClickListener(this);
		genZhi30Btn.setOnClickListener(this);
		genBackBtn.setOnClickListener(this);
		genRecordBtn.setOnClickListener(this);
		vaclist = (ListView) findViewById(R.id.vac_listview);
		tianyilist = (ListView) findViewById(R.id.tianyi_listview);
		String simType = ActivityUtils.getSimType();
		if (Constant.SIM_MOBILE.equals(simType)) {
			simKey = Constant.SIM_MOBILE;
			genYi2Btn.setVisibility(View.VISIBLE);
			genYi10Btn.setVisibility(View.VISIBLE);
			genYi30Btn.setVisibility(View.GONE);
		} else if (Constant.SIM_UNICOM.equals(simType)) {
			simKey = Constant.SIM_UNICOM;
			List<PayPoint> pointList = PayUtils.getPayPoint(PaySite.RECHARGE_LIST);
			PayAdapter adapter = new PayAdapter(GroupPayActivity.this, pointList, PaySite.RECHARGE_LIST_FAST);
			vaclist.setAdapter(adapter);
			vaclist.setVisibility(View.VISIBLE);
		} else if (Constant.SIM_TELE.equals(simType)) {
			simKey = Constant.SIM_TELE;
			List<PayPoint> pointList = PayUtils.getPayPoint(PaySite.RECHARGE_LIST);
			PayAdapter adapter = new PayAdapter(GroupPayActivity.this, pointList, PaySite.RECHARGE_LIST_FAST);
			tianyilist.setAdapter(adapter);
			tianyilist.setVisibility(View.VISIBLE);
		} else {
			genYi2Btn.setVisibility(View.VISIBLE);
			genYi10Btn.setVisibility(View.VISIBLE);
			genYi30Btn.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.gen_tel_btn:
				if (ActivityUtils.simExist() && ActivityUtils.getSimType() != Constant.SIM_OTHER) {
					Intent detailIntent = new Intent();
					detailIntent.putExtra("key", String.valueOf(simKey));
					detailIntent.setClass(GroupPayActivity.this, GroupPayDetailActivity.class);
					startActivity(detailIntent);
				} else {
					Toast.makeText(GroupPayActivity.this, "请插入sim卡", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.gen_ali_btn:
				Intent aliIntent = new Intent();
				aliIntent.setClass(GroupPayActivity.this, AliPayActivity.class);
				startActivity(aliIntent);
				break;
			case R.id.gen_yi_2_btn:
				JDSMSPayUtil.setContext(context);
				PayTipUtils.showTip(2, PaySite.RECHARGE_LIST_FAST);
//				SDKFactory.smsPay(Integer.parseInt(GroupConstant.PAY_MONEY_2), SDKConstant.LIST);
				break;
			case R.id.gen_yi_10_btn:
				JDSMSPayUtil.setContext(context);
				PayTipUtils.showTip(10, PaySite.RECHARGE_LIST_FAST);
//				SDKFactory.smsPay(Integer.parseInt(GroupConstant.PAY_MONEY_10), SDKConstant.LIST);
				break;
			case R.id.gen_yi_30_btn:
				JDSMSPayUtil.setContext(context);
				PayTipUtils.showTip(30, PaySite.RECHARGE_LIST_FAST);
//				SDKFactory.smsPay(Integer.parseInt(GroupConstant.PAY_MONEY_30), SDKConstant.LIST);
				break;
			case R.id.gen_zhi_10_btn:
				goAliPay(10);
				break;
			case R.id.gen_zhi_20_btn:
				goAliPay(20);
				break;
			case R.id.gen_zhi_30_btn:
				goAliPay(100);
				break;
			case R.id.gen_back:
				finishSelf();
				break;
			case R.id.gen_record:
				Intent intent = new Intent();
				intent.setClass(GroupPayActivity.this, PayRecordActivity.class);
				startActivity(intent);
				break;
			default:
				break;
		}
	}

	/**
	 * @param money 支付金额
	 */
	private void goAliPay(int money) {
		JDSMSPayUtil.setContext(context);
		AliConfig.PAY_MONEY = money;
		Intent intent = getIntent();
		intent.setClass(this, AlixPayActivity.class);
		startActivity(intent);
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
}
