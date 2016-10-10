package com.sdk.alipay.ui;

import com.zzyddz.shui.R;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lordcard.common.task.GenericTask;
import com.lordcard.common.task.base.TaskParams;
import com.lordcard.common.task.base.TaskResult;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.entity.JsonResult;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.prerecharge.PrerechargeManager;
import com.lordcard.ui.base.BaseActivity;
import com.lordcard.ui.base.IPayView;
import com.lordcard.ui.view.dialog.GameDialog;
import com.sdk.alipay.AliConfig;
import com.sdk.alipay.broadcast.AlipayBroadcastReceiver;
import com.sdk.alipay.util.AliPayOrder;
import com.sdk.alipay.util.AlixId;
import com.sdk.alipay.util.BaseHelper;
import com.sdk.alipay.util.MobileSecurePayHelper;
import com.sdk.alipay.util.MobileSecurePayer;
import com.sdk.alipay.util.ResultChecker;
import com.sdk.alipay.util.Rsa;
import com.sdk.util.PaySite;

/**
 * 支付时的操作页面 Author:kenethso 2012-11-15 下午5:11:46 Email:kennethso@126.com
 */
@SuppressLint("HandlerLeak")
public class AlixPayActivity extends BaseActivity implements IPayView{

	static String TAG = "AlixPayActivity";
	private Button payAgainBtn;
	private Bundle mBundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pay_alipay_remote_service_binding);
		/**预充值place标识**/
		mBundle = getIntent().getExtras();
		TextView mTitleName = (TextView) findViewById(R.id.AlipayTitleItemName);
		mTitleName.setText(getString(R.string.app_name));
		initRequestData();
		
	}

	private void initRequestData() {

		MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(AlixPayActivity.this);
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(this);
		mspHelper.setNewestVersion(mPerferences.getBoolean(AlipayBroadcastReceiver.NEWEST_SERVICE, false));
		// 判断是否已经安装支付宝安全支付
		boolean isMobile_spExist = mspHelper.detectMobile_sp();
		if (!isMobile_spExist) {
			payAgainBtn = (Button) this.findViewById(R.id.pay_again);
			payAgainBtn.setVisibility(View.VISIBLE);
			payAgainBtn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					initRequestData();
				}
			});
			return;

		}

		GenericTask aliPayTask = new AliPayTask();
		aliPayTask.setFeedback(feedback);
		aliPayTask.execute();
		taskManager.addTask(aliPayTask);
	}

	String sign(String signType, String content) {
		return Rsa.sign(content, AliConfig.RSA_PRIVATE);
	}

	public static class AlixOnCancelListener implements DialogInterface.OnCancelListener {
		Activity mcontext;

		public AlixOnCancelListener(Activity context) {
			mcontext = context;
		}

		public void onCancel(DialogInterface dialog) {
			mcontext.onKeyDown(KeyEvent.KEYCODE_BACK, null);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return false;
	}

	/**
	 * BaseHelper.showDialog通用对话框的确定键单击事件
	 */
	DialogInterface.OnClickListener onClickListener_Dialog_PositiveButton = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {

		}
	};

	private String getPayErrorMessage(int statusToCode) {
		String returnString;

		switch (statusToCode) {
		case 4003:
			returnString = getResources().getString(R.string.code_4003);
			break;
		case 4004:
			returnString = getResources().getString(R.string.code_4004);
			break;
		case 4005:
			returnString = getResources().getString(R.string.code_4005);
			break;
		case 4006:
			returnString = getResources().getString(R.string.code_4006);
			break;
		case 4010:
			returnString = getResources().getString(R.string.code_4010);
			break;
		case 6000:
			returnString = getResources().getString(R.string.code_6000);
			break;
		case 6001:
			returnString = getResources().getString(R.string.code_6001);
			break;
		case 6002:
			returnString = getResources().getString(R.string.code_6002);
			break;
		default:
			returnString = getResources().getString(R.string.code_4006);
			break;
		}
		return returnString;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String strRet = (String) msg.obj;

			switch (msg.what) {
			case AlixId.RQF_PAY:
				try {
					// 获取交易状态码，具体状态代码请参看文档
					String tradeStatus = "resultStatus={";
					int imemoStart = strRet.indexOf("resultStatus=");
					imemoStart += tradeStatus.length();
					int imemoEnd = strRet.indexOf("};memo=");
					tradeStatus = strRet.substring(imemoStart, imemoEnd);

					// 先验签通知
					ResultChecker resultChecker = new ResultChecker(strRet);
					int retVal = resultChecker.checkSign();
					// retVal = ResultChecker.RESULT_CHECK_SIGN_SUCCEED;
					// 验签失败
					if (retVal == ResultChecker.RESULT_CHECK_SIGN_FAILED) {
						BaseHelper.showDialog(AlixPayActivity.this, "提示", getResources().getString(R.string.check_sign_failed),
								android.R.drawable.ic_dialog_alert, onClickListener_Dialog_PositiveButton);
					} else {// 验签成功。验签成功后再判断交易状态码
						if (tradeStatus.equals("9000")) {// 判断交易状态码，只有9000表示交易成功
							// 支付成功
							AlixPayActivity.this.setResult(RESULT_OK, new Intent());
							AlixPayActivity.this.finish();
							Toast.makeText(getApplicationContext(), "支付成功", Toast.LENGTH_LONG).show();
						} else if (tradeStatus.equals("4000")) {
							// DialogUtils.mesTip("未完成操作", true);
						} else {
							String errMsg = getPayErrorMessage(Integer.parseInt("tradeStatus"));
							GameDialog gameDialog = new GameDialog(AlixPayActivity.this, false) {
								public void okClick() {
									finish();
								};
							};
							gameDialog.show();
							gameDialog.setText(errMsg);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 支付宝充值 com.alipay.ui.AliPayTask
	 * 
	 * @author Administrator <br/>
	 *         create at 2013 2013-3-22 下午3:31:41
	 */
	private class AliPayTask extends GenericTask {

		protected TaskResult _doInBackground(TaskParams... params) {
			try {

				String money = String.valueOf(AliConfig.PAY_MONEY);
				// 先提交充值订单
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("money", money);
				
				/**判断是不是预充值**/
				if(mBundle != null){
					String place = mBundle.getString(PrerechargeManager.PRERECHARGE_ORDER_PLACE);
					if(PaySite.PREPARERECHARGE.equalsIgnoreCase(place) && null != PrerechargeManager.mPayRecordOrder.getPreOrderNo()){
						paramMap.put(PrerechargeManager.PRERECHARGE_ORDER_PARAMS_PREORDERNO, PrerechargeManager.mPayRecordOrder.getPreOrderNo());
						paramMap.put(PrerechargeManager.PRERECHARGE_ORDER_PARAMS_PREORDERTYPE, "1");
					}
				}
				String resultJson = HttpRequest.addPayOrder(AliConfig.ALIPAY_URL, paramMap);
				JsonResult result = JsonHelper.fromJson(resultJson, JsonResult.class);
				if (JsonResult.SUCCESS.equals(result.getMethodCode())) {

					AliPayOrder payOrder = JsonHelper.fromJson(result.getMethodMessage(), AliPayOrder.class);

					String outTradeNo = payOrder.getOutTradeNo();
					String payNotifyUrl = payOrder.getNotifyUrl();
					String payPartner = payOrder.getPartner();
					String paySeller = payOrder.getSeller();
					AliConfig.RSA_PRIVATE = payOrder.getRsaShopPrivate();
					AliConfig.RSA_ALIPAY_PUBLIC = payOrder.getRsaPublic();
					String goodsName = money + "元金豆充值";
					String goodsMemo = "掌中游斗地主金豆充值";
					
					StringBuilder sb = new StringBuilder();

					sb.append("partner=\"").append(payPartner).append("\"");
					sb.append("&");
					sb.append("seller=\"").append(paySeller).append("\"");
					sb.append("&");
					sb.append("out_trade_no=\"").append(outTradeNo).append("\"");
					sb.append("&");
					sb.append("subject=\"").append(goodsName).append("\"");
					sb.append("&");
					sb.append("body=\"").append(goodsMemo).append("\"");
					sb.append("&");
					sb.append("total_fee=\"").append(money).append("\"");
					sb.append("&");
					sb.append("notify_url=\"").append(payNotifyUrl).append("\"");
					String orderInfo = sb.toString();

					String signType = BaseHelper.getSignType();
					String strsign = sign(signType, orderInfo);
					strsign = URLEncoder.encode(strsign);
					String info = orderInfo + "&" + "sign=\"" + strsign + "\"" + "&" + signType;
					MobileSecurePayer msp = new MobileSecurePayer();

					boolean bRet = msp.pay(info, mHandler, AlixId.RQF_PAY, AlixPayActivity.this);

					if (!bRet) {
						// closeProgress();
						// mProgress =
						// BaseHelper.showProgress(AlixPayActivity.this, null,
						// "正在支付",false, true);
						// BaseHelper.showProgress(AlixPayActivity.this, null,
						// "正在支付",false, true);
						throw new Exception();
					}

				} else {
					DialogUtils.mesTip(result.getMethodMessage(), true);
				}
			} catch (Exception e) {
				// DialogUtils.alipayFail("充值失败,请稍候在试");
				DialogUtils.mesTip("充值失败,请稍候在试", true);
				return TaskResult.FAILED;
			}
			return TaskResult.OK;
		}
	}
}