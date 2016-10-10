package com.lordcard.prerecharge;

import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.network.base.ThreadPool;
import com.lordcard.network.cmdmgr.ClientCmdMgr;
import com.lordcard.network.cmdmgr.CmdDetail;
import com.lordcard.network.cmdmgr.CmdUtils;
import com.lordcard.network.http.HttpURL;
import com.lordcard.network.http.HttpUtils;
import com.lordcard.ui.payrecord.PayRecordOrder;
import com.lordcard.ui.payrecord.PreOrder;
import com.lordcard.ui.view.dialog.BaseDialog;
import com.lordcard.ui.view.dialog.PreRechargeEndGameDialog;
import com.lordcard.ui.view.dialog.PreRechargeRecordDialog;
import com.lordcard.ui.view.dialog.PreRecharggeInGameDialog;
import com.sdk.util.PaySite;
import com.sdk.util.PayUtils;
import com.sdk.util.vo.PayPoint;

public class PrerechargeManager {

	private static BaseDialog prerechargeDialogs = null;
	private static boolean isPrePay = false;

	public enum PrerechargeDialogType {
		Dialog_none, Dialog_ingame, Dialog_endgame, Dialog_record,
	}

	public static float percent = 0.3f;
	/** 预充值显示参数-倍数 **/
	public static double showPrerechargePercent = 1.0;
	/** 预充值显示参数-剩余牌数 **/
	public static int showPrerechargelastCardCount = 20;
	/** 生成预充值订单成功返回参数-status **/
	public static final String PRERECHARGE_ORDER_PARAMS_STATUS = "status";
	/** 生成预充值订单成功返回参数-detail **/
	public static final String PRERECHARGE_ORDER_PARAMS_DETAIL = "detail";
	/** 生成预充值订单成功返回参数-preOrderNo **/
	public static final String PRERECHARGE_ORDER_PARAMS_PREORDERNO = "preOrderNo";
	/** 生成预充值订单成功返回参数-preOrderType **/
	public static final String PRERECHARGE_ORDER_PARAMS_PREORDERTYPE = "preOrderType";
	/** 预充值类型key **/
	public static final String PRERECHARGE_ORDER_PLACE = "PRRECHARGE";
	/** 预充值 参数key **/
	public static final String PRERECHARGE_PREMULTIPLE = "preMultiple";
	public static final String PRERECHARGE_LASTCARDCOUNT = "lastCardCount";
	public static PayRecordOrder mPayRecordOrder = new PayRecordOrder();

	public static void setPrePay(boolean prePay) {
		isPrePay = prePay;
	}

	public static boolean isPrePay() {
		return isPrePay;
	}

	public static void clearPrerechargeInfo() {
		mPayRecordOrder = new PayRecordOrder();
		isPrePay = false;
		prerechargeDialogs = null;
	}

	public static boolean isShowPrerechargeDialog() {
		if (null == prerechargeDialogs)
			return false;
		return prerechargeDialogs.isShowing();
	}

	public static void dismissPrerechargeDialog() {
		if (null == prerechargeDialogs)
			return;
		prerechargeDialogs.dismiss();
	}

	/**
	 * 生成预支付Dialog
	 * 
	 * @param type
	 *            Dialog类型
	 * @param context
	 *            上下文
	 * @param payRecordOrder
	 *            订单记录 如果type = Dialog_ingame,传入null,即可
	 * @param handler
	 *            Handler 如果type = Dialog_ingame,传入null,即可
	 * @param multiple
	 *            游戏倍数
	 * @return 预支付Dialog实例
	 */
	public static BaseDialog createPrerechargeDialog(PrerechargeDialogType type, Context context, PayRecordOrder payRecordOrder, Handler handler, long multiple) {
		BaseDialog baseDialog = null;
		switch (type) {
			case Dialog_ingame:
				baseDialog = new PreRecharggeInGameDialog(context, multiple);
				prerechargeDialogs = baseDialog;
				break;
			case Dialog_endgame:
				baseDialog = new PreRechargeEndGameDialog(context, payRecordOrder, handler);
				break;
			case Dialog_record:
				baseDialog = new PreRechargeRecordDialog(context, payRecordOrder, handler);
				break;
			default:
				break;
		}
		return baseDialog;
	}

	/**
	 * 根据金豆赌注所计算出来的预充值价格列表
	 * 
	 * @param currentIqBeans
	 * @param wagerIqBeans
	 * @param simCardType
	 * @return金豆赌注所计算出来的预充值价格列表
	 */
	public static int[] calculatePrerechargePrice(long currentIqBeans, long wagerIqBeans) {
		long differIqBeans = wagerIqBeans - currentIqBeans;
		int price = -1;
		if (differIqBeans < 10000) {
			price = 1;
		} else {
			long tempIntegerPart = differIqBeans / 10000;
			price = (int) tempIntegerPart;
			double tempDecimalParet = (differIqBeans - tempIntegerPart * 10000) / 10000f;
			price = (tempDecimalParet >= percent) ? (price + 1) : price;
		}
		/**清除一块钱计费点**/
		if (price <= 1) {
			price = 2;
		} else if (price > 30) {
			price = 30;
		}
		return changePriceToAdjustThirdPartyPurchase(price);
	}

	/**
	 * 把金豆赌注所计算出来的预充值价格,调整为第三方支付相应计费点列表
	 * 
	 * @param price金豆赌注所计算出来的预充值价格
	 * @param simCardType 当前手机simCard类型
	 * @return 调整后的第三方相应支付计费点列表
	 */
	private static int[] changePriceToAdjustThirdPartyPurchase(int price) {
		int[] thirdPartPriceArray = new int[2];
		
		List<PayPoint> pointList = PayUtils.getPayPoint(PaySite.PREPARERECHARGE); //获取预充值对应支付方式的计费点
		if(pointList == null){
			/** 无法识别为默认支付宝 **/
			thirdPartPriceArray[0] = price;
			thirdPartPriceArray[1] = price + 1;
		}else{
			int mmPrice[] = new int[pointList.size()];
			for (int i = 0; i < pointList.size(); i++) {
				mmPrice[i]  = pointList.get(i).getMoney();
			}
		}
		
//		int mmPrice[] = null;
//		switch (simCardType) {
//			case Constant.SIM_MOBILE:
//				/** 移动MM支付 **/
//				mmPrice = new int[] { 1, 2, 4, 5, 8, 10, 15, 20, 25, 30 };
//				thirdPartPriceArray = getTwoSimilarPriceWithTarget(mmPrice, price);
//				break;
//			case Constant.SIM_TELE:
//				/** 电信支付 **/
//				List<ESurfing> telecomProductsList = TYConfig.ESURFING_LIST;
//				if (null == telecomProductsList || telecomProductsList.size() == 0)
//					break;
//				mmPrice = new int[telecomProductsList.size()];
//				int index = 0;
//				for (ESurfing eSurfing : telecomProductsList) {
//					mmPrice[index] = eSurfing.getPrice();
//					index++;
//				}
//				thirdPartPriceArray = getTwoSimilarPriceWithTarget(mmPrice, price);
//				break;
//			case Constant.SIM_UNICOM:
//				/** 联通支付 **/
//				List<VACBillPoint> cuProductsList = VACConfig.BILLPOINT_LIST;
//				if (null == cuProductsList || cuProductsList.size() == 0)
//					break;
//				mmPrice = new int[cuProductsList.size()];
//				int position = 0;
//				for (VACBillPoint vac : cuProductsList) {
//					mmPrice[position] = vac.getPrice();
//					position++;
//				}
//				thirdPartPriceArray = getTwoSimilarPriceWithTarget(mmPrice, price);
//				break;
//			case Constant.SIM_OTHER:
//				/** 默认支付宝 **/
//				thirdPartPriceArray[0] = price;
//				thirdPartPriceArray[1] = price + 1;
//				break;
//			default:
//				/** 无法识别为默认支付宝 **/
//				thirdPartPriceArray[0] = price;
//				thirdPartPriceArray[1] = price + 1;
//				break;
//		}
		return thirdPartPriceArray;
	}

	/**
	 * 从计费点列表查找两个最接近的计费点
	 * 
	 * @param priceArray
	 *            计费点列表
	 * @param targetPrice
	 *            目标计费点
	 * @return resultPrice 查询结果
	 */
	public static int[] getTwoSimilarPriceWithTarget(int[] priceArray, int targetPrice) {
		int[] resultPrice = new int[2];
		Arrays.sort(priceArray);
		for (int price : priceArray) {
			if (resultPrice[0] == 0) {
				/** 第一个值为0时，给第一个值赋初值 **/
				resultPrice[0] = price;
			} else if (resultPrice[1] == 0) {
				/** 第一个值为0时，给第一个值赋初值 **/
				resultPrice[1] = price;
			} else if (resultPrice[0] != price && resultPrice[1] != price) {
				/** 结果数组中的计费点都不等于当前价格price 执行操作 **/
				if (price <= targetPrice) {
					/**
					 * 当前价格price小于等于目标价格targetPrice时,用第二个计费点替换第一个计费点,
					 * 再把当前计费点price赋给第二个计费点
					 **/
					resultPrice[0] = resultPrice[1];
					resultPrice[1] = price;
				} else if (price > targetPrice) {
					/** 当前价格price大于目标价格targetPrice 执行操作 **/
					if (resultPrice[0] < targetPrice) {
						/** 第一个结果数组中第一个计费点小于目标价格targetPrice **/
						resultPrice[0] = resultPrice[1];
						resultPrice[1] = price;
					}
				}
			}
		}
		resultPrice[0] = (resultPrice[0] == 0) ? 1 : resultPrice[0];
		resultPrice[1] = (resultPrice[1] == 0) ? (resultPrice[0] + 1) : resultPrice[1];
		Arrays.sort(resultPrice);
		return resultPrice;
	}

	/**
	 * 生成预充值订单
	 * 
	 * @param money
	 *            价格
	 * @param payType
	 *            支付类型
	 */
	public synchronized static void createPrerechargeOrder(final double money) {
		ThreadPool.startWork(new Runnable() {

			@Override
			public void run() {
				
				PreOrder order = new PreOrder();
//				String payType = SDKFactory.getPayType();
//				if (TextUtils.isEmpty(payType)) {
//					payType = "-1";
//				}
//				order.setPayType(Integer.parseInt(payType));
				
//				PaySiteConfigItem paySiteConfigItem = PayUtils.getPaySiteUseConfig(PaySite.PREPARERECHARGE);
//				String payCode = paySiteConfigItem.getPayCode();
				order.setPayType(-1);
				order.setMoney(money);
				CmdDetail cmdDetail = new CmdDetail();
				cmdDetail.setCmd(CmdUtils.CMD_PPC);
				cmdDetail.setDetail(JsonHelper.toJson(order));
				ClientCmdMgr.sendCmd(cmdDetail);
				/** 记录订单价格 **/
				mPayRecordOrder.setMoney(money);
			}
		});
	}

	/**
	 * 获取显示预充值UI的参数
	 * 
	 * @param url
	 *            访问地址
	 */
	public static void getPrerechargeParams() {
		String result = HttpUtils.post(HttpURL.PRE_RECHARGE_MULTIPLE_URL, null, true);
		boolean isGetParamsCorrect = false;
		if (null != result && !TextUtils.isEmpty(result)) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(result);
				String multiple = jsonObject.getString(PRERECHARGE_PREMULTIPLE);
				String lastCardCount = jsonObject.getString(PRERECHARGE_LASTCARDCOUNT);
				if (null != multiple) {
					showPrerechargePercent = Double.valueOf(multiple);
					ActivityUtils.addSharedValue(PRERECHARGE_PREMULTIPLE, String.valueOf(showPrerechargePercent));
					isGetParamsCorrect = true;
				}
				if (null != lastCardCount) {
					showPrerechargelastCardCount = Integer.valueOf(lastCardCount);
					ActivityUtils.addSharedValue(PRERECHARGE_LASTCARDCOUNT, String.valueOf(showPrerechargelastCardCount));
				} else {
					isGetParamsCorrect = false;
				}
			} catch (JSONException e) {
				isGetParamsCorrect = false;
				e.printStackTrace();
			} finally {
				if (!isGetParamsCorrect) {
					if (null != ActivityUtils.getSharedValue(PRERECHARGE_PREMULTIPLE)) {
						showPrerechargePercent = Double.valueOf(ActivityUtils.getSharedValue(PRERECHARGE_PREMULTIPLE));
					}
					if (null != ActivityUtils.getSharedValue(PRERECHARGE_LASTCARDCOUNT)) {
						showPrerechargelastCardCount = Integer.valueOf(ActivityUtils.getSharedValue(PRERECHARGE_LASTCARDCOUNT));
					}
				}
			}
		}
	}
}
