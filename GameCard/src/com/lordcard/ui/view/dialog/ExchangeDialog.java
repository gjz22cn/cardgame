package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.google.gson.reflect.TypeToken;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.common.util.PatternUtils;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.GameUserAddress;
import com.lordcard.entity.GoodsPart;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpCallback;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.network.http.HttpURL;

public class ExchangeDialog extends Dialog implements OnClickListener {

	private Context context;
	private RelativeLayout mainLayout;
	private String typeId;
	private EditText nameText, adressText, emaildress, phoneText;
	private Button okButton, closeBtn;
	private ArrayAdapter<String> adapter;
	private GameUserAddress userAddress;
	private Handler refresh;
	private TextView discrible;
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private RelativeLayout layout;

	protected ExchangeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.context = context;
	}

	public ExchangeDialog(Handler refresh, Context context, int theme, String type) {
		super(context, theme);
		this.context = context;
		this.typeId = type;
		this.refresh = refresh;
	}

	public ExchangeDialog(Context context, int dialog, String goodsName, String goodsid, Integer goodscount, List<GoodsPart> goods) {
		super(context, dialog);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exchange);
		layout(context);
		layout = (RelativeLayout) findViewById(R.id.exchange_dialog_layout);
		mst.adjustView(layout);
	}

	public void setDismiss() {

	}

	/**
	 * 布局
	 * 
	 * @param context
	 */
	private void layout(final Context context) {
		((TextView) findViewById(R.id.dialog_title_tv)).setText("物品使用");
		// ((TextView) findViewById(R.id.recharge_text_layout)).setText("物品使用");
		nameText = (EditText) findViewById(R.id.name_text);
		adressText = (EditText) findViewById(R.id.adress_text);
		emaildress = (EditText) findViewById(R.id.email_text);
		phoneText = (EditText) findViewById(R.id.phone_text);
		discrible = (TextView) findViewById(R.id.recharge_text_layout);

		GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("account",cacheUser.getAccount());
		HttpRequest.postCallback(HttpURL.USER_MESS_GET, paramMap, new HttpCallback() {
			@Override
			public void onSucceed(Object... obj) {
				if (obj == null) {

				} else {
					String result = (String) obj[0];
					if (result.trim().equals("1")) {
					} else {
						userAddress = JsonHelper.fromJson(result, new TypeToken<GameUserAddress>() {
						});
						if (userAddress != null) {
							Database.currentActivity.runOnUiThread(new Runnable() {
								public void run() {
									nameText.setText(userAddress.getAddressee());
									adressText.setText(userAddress.getAddress());
									emaildress.setText(userAddress.getZip());
									phoneText.setText(userAddress.getPhone());
								}
							});
						}
					}
				}
			}

			@Override
			public void onFailed(Object... obj) {

			}
		});

		closeBtn = (Button) findViewById(R.id.dialog_close_btn);
		okButton = (Button) findViewById(R.id.ok_btn);
		okButton.setOnClickListener(this);
		closeBtn.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok_btn:
			String name = nameText.getText().toString().trim();
			String adress = adressText.getText().toString().trim();
			String email = emaildress.getText().toString().trim();
			String phone = phoneText.getText().toString().trim();
			boolean isMobile = PatternUtils.validMobiles(phone);
			if (name.trim().equals("") || adress.trim().equals("") || email.trim().equals("") || phone.trim().equals("")) {
				DialogUtils.mesTip("请填写完整的信息！", false);
			} else if (!isMobile) {
				DialogUtils.mesTip("请输入正确的手机号码!", false);
			} else {
				MobclickAgent.onEvent(context, "领取物品填信息");
				GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("loginToken",cacheUser.getLoginToken());
				paramMap.put("addressee", name);
				paramMap.put("address", adress);
				paramMap.put("zip", email);
				paramMap.put("phone", phone);
				paramMap.put("typeId", typeId);
				HttpRequest.postCallback(HttpURL.USER_MESS_ADRESS, paramMap, new HttpCallback() {
					@Override
					public void onSucceed(Object... obj) {
						String result = (String) obj[0];
						if (result.trim().equals("0")) {
							Database.currentActivity.runOnUiThread(new Runnable() {
								public void run() {
									DialogUtils.mesTip("登记成功，将在五个工作日内将发货到您填写的地址上！", false);
									refresh.sendEmptyMessage(0);
									dismiss();
								}
							});
						} else {
							Database.currentActivity.runOnUiThread(new Runnable() {
								public void run() {
									DialogUtils.mesTip("提交信息失败，请重新提交！", false);
								}
							});
						}

					}

					@Override
					public void onFailed(Object... obj) {

					}
				});
			}
			break;
		case R.id.dialog_close_btn:
			dismiss();
			break;
		default:
			break;
		}
	}

	@Override
	public void dismiss() {
		mst.unRegisterView(layout);
		super.dismiss();
	}

}
