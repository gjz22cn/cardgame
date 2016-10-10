package com.sdk.dianjin.pay.ui;

import com.zzyddz.shui.R;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bodong.dpaysdk.DPayConfig;
import com.bodong.dpaysdk.DPayManager;
import com.bodong.dpaysdk.entity.DPayRechargeOrder;
import com.bodong.dpaysdk.listener.DPayLoginListener;
import com.bodong.dpaysdk.listener.DPayLogoutListener;
import com.bodong.dpaysdk.listener.DPayRechargeListener;
import com.bodong.dpaysdk.listener.DPaySDKExitListener;
import com.lordcard.common.task.GenericTask;
import com.lordcard.common.task.base.TaskParams;
import com.lordcard.common.task.base.TaskResult;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameUserGoods;
import com.lordcard.entity.JsonResult;
import com.lordcard.net.http.HttpRequest;
import com.lordcard.ui.base.BaseActivity;
import com.lordcard.ui.payrecord.PayRecordActivity;
import com.sdk.dianjin.pay.DJConstant;
import com.sdk.dianjin.pay.DpayOrder;

public class DJPayActivity extends BaseActivity implements OnClickListener {

	private Button BackBtn, pay2Btn, pay5Btn, pay10Btn, pay20Btn, pay50Btn, pay40Btn, pay30Btn, recordBtn;
	private static final String TAG = DJPayActivity.class.getSimpleName();
	public String orderid;

	public static String _payType = "1";
	private TextView zhidou;
	static final int REQUEST_CODE_RECHARGE = 12;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_dj);
		// findViewById(R.id.mm_pay_layout).setBackgroundDrawable(
		// ImageUtil.getResDrawable(R.drawable.mm_bg));
		mst.adjustView(findViewById(R.id.mm_pay_layout));
		init();
		context = this;

	}

	@Override
	protected void onStart() {
		super.onStart();
		new Thread() {
			public void run() {
				final GameUserGoods gameUserGoods = HttpRequest.getGameUserGoods();
				if (gameUserGoods != null) {
					runOnUiThread(new Runnable() {
						public void run() {
							long point = gameUserGoods.getBean();
							if (point > 10000) {
								point = point / 10000;
								zhidou.setText(String.valueOf(point) + "W");
							} else {
								zhidou.setText(String.valueOf(Database.USER.getBean()));
							}
						}
					});
				} else {
					DialogUtils.toastTip("获取数据失败");
				}
			};
		}.start();
	}

	// 充值监听
	private DPayRechargeListener mDPayRechargeListener = new DPayRechargeListener() {

		/**
		 * @Title: onRechargeSubmitted
		 * @Description: 订单已经提交到平台服务器处理，若开发者使用代币模式，可在此将订单信息保存到游戏服务端，
		 *               后续平台服务器通知游戏服务器时，游戏服务器可根据此单据进行比对处理。
		 * 
		 * @param rechargeOrder
		 *            : 订单详情
		 */
		@Override
		public void onRechargeSubmitted(DPayRechargeOrder order) {
			Log.i(TAG, "订单已经提交平台服务器：" + order.rechargeId + " 开发者传入订单号：" + order.uRechargeId + " extra：" + order.extra + " 用户充值金额（人民币）：" + order.money
					+ " 用户充值金额（游戏币）：" + order.amount);
		}

		/**
		 * @Title: onRechargeFinished
		 * @Description: 用户完成充值流程，开发者可在此根据订单的状态，进行自身逻辑处理。推荐处理方式为：
		 *               1、若订单为到帐（用户充值成功），开发者处理游戏客户端后续逻辑，例如：继续购买操作。
		 *               2、若订单为失败（用户充值失败），开发者可提示用户充值失败等。
		 *               3、若订单为未到帐（短信/充值卡等充值方式会有一定时间的延迟），推荐的处理方式为：
		 *               游戏客户端查询游戏服务器此订单状态 若订单状态还是未到帐，则弹出提示窗口，提醒用户充值还未到帐，是否重新查询
		 * 
		 *               订单状态可参见：
		 * @see DPayConfig.RechargeStatus
		 * @param rechargeOrder
		 *            : 订单详情，若订单还未提交到平台服务器（用户还未进行充值），则为空
		 */
		@Override
		public void onRechargeFinished(DPayRechargeOrder order) {
			if (order != null) {
				// 充值订单状态
				int rechargeResult = order.status;
				// 平台充值订单号
				String rechargeId = order.rechargeId;
				// 开发者传入订单号
				String uRechargeId = order.uRechargeId;
				// 开发者传入附带值
				String extra = order.extra;
				// 用户充值人民币，以元为单位
				float money = order.money;
				// 用户充值游戏币个数
				int amount = order.amount;
				if (rechargeResult == DPayConfig.RechargeStatus.TRANSFERED) {
					// 处理充值成功逻辑
					if(Database.chargingProcessDia != null && Database.chargingProcessDia.isShowing())
					{
						Database.chargingProcessDia.dismiss();
					}
					DialogUtils.toastTip("用户充值成功");
					
				} else if (rechargeResult == DPayConfig.RechargeStatus.UNTRANSFERED) {
					// 处理充值未到帐，开发者可利用充值订单到游戏服务器查询此订单状态。

				} else if (rechargeResult == DPayConfig.RechargeStatus.TRANSFER_FAILED) {
					// 处理充值失败
					DialogUtils.toastTip("用户充值失败");
				}
				Log.i(TAG, "充值订单号：" + rechargeId + " 开发者传入订单号：" + uRechargeId + " extra：" + extra + " 充值订单状态：" + rechargeResult + " 用户充值金额（人民币）："
						+ money + " 用户充值金额（游戏币）：" + amount);
			} else {
				// 用户取消，订单还未提交到平台服务器处理
				Log.i(TAG, "感谢使用点金游戏平台充值，欢迎下次回来～");
			}
		}
	};

	private void init() {

		BackBtn = (Button) findViewById(R.id.dj_back);
		zhidou = (TextView) findViewById(R.id.zhi_dou);
		pay2Btn = (Button) findViewById(R.id.dj_2_btn);
		pay5Btn = (Button) findViewById(R.id.dj_5_btn);
		pay10Btn = (Button) findViewById(R.id.dj_10_btn);
		pay20Btn = (Button) findViewById(R.id.dj_20_btn);
		pay30Btn = (Button) findViewById(R.id.dj_30_btn);
		pay40Btn = (Button) findViewById(R.id.dj_40_btn);
		pay50Btn = (Button) findViewById(R.id.dj_50_btn);
		recordBtn = (Button) findViewById(R.id.dj_record);
		pay2Btn.setOnClickListener(this);
		pay5Btn.setOnClickListener(this);
		pay10Btn.setOnClickListener(this);
		pay20Btn.setOnClickListener(this);
		pay30Btn.setOnClickListener(this);
		pay40Btn.setOnClickListener(this);
		pay50Btn.setOnClickListener(this);
		BackBtn.setOnClickListener(this);
		recordBtn.setOnClickListener(this);

		// DJPayUtil.djInit();

		// 初始化sdk
		DPayManager.init(this);
		DPayManager.setSDKExitListener(new DPaySDKExitListener() {
			@Override
			public void onExit() {
				Log.i(TAG, "客户端知道你退出了哦～");
			}
		});
		// 设置充值监听
		DPayManager.setRechargeListener(mDPayRechargeListener);
		// 设置登录监听
		DPayManager.setLoginListener(new DPayLoginListener() {
			@Override
			public void onLogin() {
				if (DPayManager.isUserLoggedIn()) {
					Log.i(TAG, "客户端知道你登录了哦～");
					Log.i(TAG, "登录信息为：" + DPayManager.getUserId() + " -- " + DPayManager.getSessionId());
				}
			}
		});
		// 设置注销监听
		DPayManager.setLogoutListener(new DPayLogoutListener() {
			@Override
			public void onLogout() {
				Log.i(TAG, "客户端知道你注销了哦～");
			}
		});

		Intent intent = getIntent();
		String djPay = intent.getStringExtra("djPay"); // 获取人民币
		if (djPay != null) {

			goPay(String.valueOf(djPay));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dj_2_btn:
			goPay(DJConstant.DJPAY_2);
			break;
		case R.id.dj_5_btn:
			goPay(DJConstant.DJPAY_5);
			break;
		case R.id.dj_10_btn:
			goPay(DJConstant.DJPAY_10);
			break;
		case R.id.dj_20_btn:
			goPay(DJConstant.DJPAY_20);
			break;
		case R.id.dj_30_btn:
			goPay(DJConstant.DJPAY_30);
			break;
		case R.id.dj_40_btn:
			goPay(DJConstant.DJPAY_40);
			break;
		case R.id.dj_50_btn:
			goPay(DJConstant.DJPAY_50);
			break;
		case R.id.dj_back:
			finish();
			break;
		case R.id.dj_record:
			Intent intent = new Intent();
			intent.setClass(DJPayActivity.this, PayRecordActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	/**
	 * 根据人民币去充值
	 * 
	 * @param consumeCode
	 */
	public void goPay(String money) {
		TaskParams params = new TaskParams();
		params.put("money", money); // 人民币
		params.put("loginToken", Database.USER.getLoginToken());
		GenericTask DJPayTask = new DJPayTask();
		DJPayTask.setFeedback(feedback);
		DJPayTask.execute(params);
		taskManager.addTask(DJPayTask);
	}

	private class DJPayTask extends GenericTask {

		protected TaskResult _doInBackground(TaskParams... params) {
			try {

				TaskParams param = params[0];
				final String money = param.getString("money");
				final String loginToken = param.getString("loginToken");

				// 先提交充值订单
				Map<String, String> paramMap = new HashMap<String, String>();

				paramMap.put("money", money);
				paramMap.put("loginToken", loginToken);
				final int moneyPay = Integer.parseInt(money);// 上传的是金豆，
				// 后台生成订单
				String resultJson = HttpRequest.addPayOrder(DJConstant.DJPAY_URL, paramMap);
				JsonResult result = JsonHelper.fromJson(resultJson, JsonResult.class);
				if (JsonResult.SUCCESS.equals(result.getMethodCode())) {
					final DpayOrder payOrder = JsonHelper.fromJson(result.getMethodMessage(), DpayOrder.class);
					orderid = payOrder.getOrderNo();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							DPayManager.startRechargeActivity(context, orderid, "", moneyPay);
						}
					});
					//
				} else {
					DialogUtils.mesTip(result.getMethodMessage(), true);
				}

			} catch (Exception e) {
				DialogUtils.mesTip("充值失败", true);
				return TaskResult.FAILED;
			}
			return TaskResult.OK;
		}
	}

	@Override
	protected void onDestroy() {
		// 移除平台监听
		DPayManager.setRechargeListener(null);
		DPayManager.setSDKExitListener(null);
		DPayManager.setLoginListener(null);
		DPayManager.setLogoutListener(null);
		super.onDestroy();
	}
}
