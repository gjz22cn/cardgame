package com.sdk.alipay.ui;

import com.zzyddz.shui.R;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.reflect.TypeToken;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.ImageUtil.ImageCallback;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.common.util.PatternUtils;
import com.lordcard.constant.CacheKey;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.GameUserGoods;
import com.lordcard.entity.PayPicType;
import com.lordcard.network.base.ThreadPool;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.network.http.HttpURL;
import com.lordcard.network.http.HttpUtils;
import com.lordcard.ui.base.BaseActivity;
import com.lordcard.ui.base.IPayView;
import com.sdk.alipay.AliConfig;

public class AliPayActivity extends BaseActivity implements IPayView,OnClickListener {

	private EditText editFree;
	private Button aliBackBtn, freeBtn, pay5Btn, pay10Btn, pay20Btn, pay100Btn, pay300Btn;
	// AlertDialog.Builder alertDialog = null;
	private MultiScreenTool mst = MultiScreenTool.singleTonVertical();
	private TextView zhidou;
	// private List<ImageView> extraImage = new ArrayList<ImageView>();
	private int[] extrapic_id = { R.id.extra_pic1, R.id.extra_pic2, R.id.extra_pic3, R.id.extra_pic4, R.id.extra_pic5 };
	private SharedPreferences sharedPrefrences;
	private Editor editor;
	RelativeLayout view;
	LinearLayout view2, view3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_layout);
		view = (RelativeLayout) findViewById(R.id.pay_layout);
		view2 = (LinearLayout) findViewById(R.id.redbackground);
		mst.adjustView(view);
		mst.adjustView(view2);
		ThreadPool.startWork(new Runnable() {

			@SuppressLint("WorldReadableFiles")
			public void run() {
				try {
					String result = HttpUtils.post(HttpURL.PAY_INFO_URL + "info", null, true);
					List<PayPicType> picMap = JsonHelper.fromJson(result, new TypeToken<List<PayPicType>>() {});
					int verson = picMap.get(0).getVerson();
					String picUrl = picMap.get(0).getPicUrl();
					sharedPrefrences = getSharedPreferences("pic_verson", MODE_WORLD_READABLE);
					int versonCode = sharedPrefrences.getInt("newverson", 0);
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
				} catch (Exception e) {}
			}
		});
		init();
	}

	@Override
	protected void onStart() {
		super.onStart();
		new Thread() {

			public void run() {
				final GameUserGoods gameUserGoods = HttpRequest.getGameUserGoods(false);
				if (gameUserGoods != null) {
					runOnUiThread(new Runnable() {

						public void run() {
							long point = gameUserGoods.getBean();
							if (point > 10000) {
								point = point / 10000;
								zhidou.setText(String.valueOf(point) + "W");
							} else {
								GameUser gameUser = (GameUser)GameCache.getObj(CacheKey.GAME_USER);
								zhidou.setText(String.valueOf(gameUser.getBean()));
							}
						}
					});
				} else {
					DialogUtils.toastTip("获取数据失败");
				}
			};
		}.start();
	}

	private void init() {
		editFree = (EditText) findViewById(R.id.ali_edit);
		freeBtn = (Button) findViewById(R.id.ali_free_btn);
		pay5Btn = (Button) findViewById(R.id.ali_5_btn);
		pay10Btn = (Button) findViewById(R.id.ali_10_btn);
		pay20Btn = (Button) findViewById(R.id.ali_20_btn);
		pay100Btn = (Button) findViewById(R.id.ali_100_btn);
		pay300Btn = (Button) findViewById(R.id.ali_300_btn);
		aliBackBtn = (Button) findViewById(R.id.ali_back);
		zhidou = (TextView) findViewById(R.id.ali_zhi_dou);
		freeBtn.setOnClickListener(this);
		pay5Btn.setOnClickListener(this);
		pay10Btn.setOnClickListener(this);
		pay20Btn.setOnClickListener(this);
		pay100Btn.setOnClickListener(this);
		pay300Btn.setOnClickListener(this);
		aliBackBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ali_free_btn:
				String payMoney = editFree.getText().toString().trim();
				if (checkInputMoney(payMoney)) {
					goPayActivity(payMoney);
				} else {
					editFree.setText("");
				}
				break;
			case R.id.ali_5_btn:
				goPayActivity("5");
				break;
			case R.id.ali_10_btn:
				goPayActivity("10");
				break;
			case R.id.ali_20_btn:
				goPayActivity("20");
				break;
			case R.id.ali_100_btn:
				goPayActivity("100");
				break;
			case R.id.ali_300_btn:
				goPayActivity("300");
				break;
			case R.id.ali_back:
				finishSelf();
				break;
			default:
				break;
		}
	}

	/**
	 * @param money
	 *            支付金额
	 */
	private void goPayActivity(String money) {
		AliConfig.PAY_MONEY = Integer.parseInt(money);
		Intent intent = getIntent();
		intent.setClass(this, AlixPayActivity.class);
		startActivity(intent);
	}

	private boolean checkInputMoney(String checkMoney) {
		// 不能为空
		if (TextUtils.isEmpty(checkMoney)) {
			DialogUtils.mesTip("请输入充值金额。", false);
			return false;
		}
		// 必须是数字
		if (!PatternUtils.isNumeric(checkMoney)) {
			DialogUtils.mesTip("输入的充值金额必须是数字。", false);
			return false;
		}
		// 必须符合充值规则（最少1元；最多1000元）
		try {
			int money = Integer.valueOf(checkMoney);
			if (money < 1 || money > 1000) {
				DialogUtils.mesTip("输入的充值金额必须符合充值规则。", false);
				return false;
			}
		} catch (NumberFormatException ex) {
			DialogUtils.mesTip("输入的充值金额必须符合充值规则。", false);
			return false;
		}
		return true;
	}
}
