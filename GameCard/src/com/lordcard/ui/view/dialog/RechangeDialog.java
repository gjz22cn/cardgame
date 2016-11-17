package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.common.util.PatternUtils;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.GoodsPart;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpCallback;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.network.http.HttpURL;
import com.lordcard.ui.StoveActivity;


public class RechangeDialog extends Dialog implements OnClickListener {
	private Spinner spiEdu = null;
	private ArrayAdapter<CharSequence> adapteEdu = null;
	private List<CharSequence> dataEdu = null;// 定义一个集合数据
	private Context context;
	private RelativeLayout mainLayout;
	private Spinner mySpinner;
	private TextView myTextView;
	private Button changezdBtn, changehfBtn;
	private ArrayAdapter<String> adapter;
	private EditText phoneText;
	private EditText selectText;
	private String typeId;
	private Button downButton;
	private Handler refresh;
	private String value;
	private Button beanButton, chargeButton, closeButton;
	final String account[] = { "移动", "联通", "电信" };
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private RelativeLayout layout;

	protected RechangeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.context = context;
	}

	public RechangeDialog(String value, Handler refresh, Context context, int theme, String type) {
		super(context, theme);
		this.context = context;
		this.typeId = type;
		this.refresh = refresh;
		this.value = value;
	}

	public RechangeDialog(Context context, int dialog, String goodsName, String goodsid, Integer goodscount, List<GoodsPart> goods) {
		super(context, dialog);
		this.context = context;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rechange);
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
		((TextView) findViewById(R.id.dialog_title_tv)).setText("话费卡使用");
		myTextView = (TextView) findViewById(R.id.hf_text_layout);
		String discrible = "您将使用" + value + "元话费卡充值进行充值，请填写你的手机信息，您也可以将话费卡转换成金豆。";
		myTextView.setText(discrible);
		selectText = (EditText) findViewById(R.id.select_text);
		selectText.setText("移动");
		phoneText = (EditText) findViewById(R.id.phoneid);
		downButton = (Button) findViewById(R.id.spindown_btn);
		downButton.setOnClickListener(this);

		closeButton = (Button) findViewById(R.id.dialog_close_btn);
		beanButton = (Button) findViewById(R.id.stovebean_btn);
		chargeButton = (Button) findViewById(R.id.recharge_btn);
		selectText.setOnClickListener(this);
		closeButton.setOnClickListener(this);
		beanButton.setOnClickListener(this);
		chargeButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.recharge_btn:
			String phoneNum = phoneText.getText().toString().trim();
			String card = selectText.getText().toString().trim();
			// 手机号验资
			boolean isMobile = PatternUtils.validMobiles(phoneNum);
			if (phoneNum.trim().equals("") || card.trim().equals("")) {
				DialogUtils.mesTip("请填写完整的信息！", false);
			} else if (!isMobile) {
				DialogUtils.mesTip("请输入正确的手机号码!", false);
				phoneText.setText("");
			} else {
				//MobclickAgent.onEvent(context, "话费券充值信息完成");
				//MobclickAgent.onEvent(context, "");
				Map<String, String> paramMap = new HashMap<String, String>();
				String operatorsid = "";
				if (card.equals("移动")) {
					operatorsid = "1";
				} else if (card.equals("联通")) {
					operatorsid = "2";
				} else if (card.equals("电信")) {
					operatorsid = "3";
				}
				GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
				paramMap.put("loginToken",cacheUser.getLoginToken());
				paramMap.put("operators", operatorsid);
				paramMap.put("phone", phoneNum);
				paramMap.put("typeId", typeId);
				HttpRequest.postCallback(HttpURL.USER_PHONE_MESS, paramMap, new HttpCallback() {
					@Override
					public void onSucceed(Object... obj) {
						String result = (String) obj[0];
						if (result.trim().equals("0")) {
							Database.currentActivity.runOnUiThread(new Runnable() {
								public void run() {
									DialogUtils.mesTip("登记成功，将在两个工作日内将话费充值到您手机上！", false);
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

		case R.id.stovebean_btn:
			//MobclickAgent.onEvent(context, "话费券充值合成金豆");
			Bundle bundle = new Bundle();
			bundle.putInt("page", 0);
			Intent stoveIntent = new Intent();
			stoveIntent.setClass(context, StoveActivity.class);
			stoveIntent.putExtras(bundle);
			context.startActivity(stoveIntent);
			dismiss();
			break;
		case R.id.select_text:
		case R.id.spindown_btn:
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("请选择运营商");
			builder.setItems(account, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					String accountStr = account[which];
					selectText.setText(accountStr);

				}
			});
			builder.create().show();
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
		mst.adjustView(layout);
		super.dismiss();
	}

}
