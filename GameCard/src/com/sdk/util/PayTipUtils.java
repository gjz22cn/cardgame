package com.sdk.util;

import java.util.HashMap;

import android.view.Gravity;
import android.widget.Toast;

import com.lordcard.common.util.DialogUtils;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameUser;
import com.lordcard.network.http.GameCache;
import com.sdk.util.vo.PayDialog;
import com.sdk.util.vo.PayInit;
import com.sdk.util.vo.PayPoint;
import com.sdk.util.vo.PaySiteConfigItem;

/**
 * 支付提示方式
 * @ClassName: PayTipUtils   
 * @Description: TODO 
 * @author yinhongbiao   
 * @date 2014-2-19 下午2:59:46
 */
public class PayTipUtils {

	public static final String MODEL_DIALOG = "dialog"; //自定义弹出框模式
	public static final String MODEL_TOAST = "toast"; //toast自动
	public static final String MODEL_SDK = "sdk"; //直接到支付sdk

	/**
	 * 计费位置提示方式展示
	 * @Title: showTip  
	 * @param money			当前需要充值的金额
	 * @param siteConfig	计费位置的配置
	 * @param paySiteTag	支付的计费位置标识
	 * @return void
	 * @throws
	 */
	public static boolean showTip(double money, String paySite) {
		//找到当前位置使用的计费配置
		PaySiteConfigItem siteConfigItem = PayUtils.getPaySiteUseConfig(paySite);
		if (siteConfigItem == null)
			return false;
		String showModel = siteConfigItem.getModel();
		//配置提示框弹出方式
		if(true)//目前只有基地计费
		{
			paySDK(money, siteConfigItem, paySite);
		}/*else
		{
			if (MODEL_DIALOG.equals(showModel)) {
				dialog(money, siteConfigItem, paySite);
			} else if (MODEL_TOAST.equals(showModel)) {
				toast(money, siteConfigItem, paySite);
			} else if (MODEL_SDK.equals(showModel)) {
				paySDK(money, siteConfigItem, paySite);
			}
		}*/

		return true;
	}

	@SuppressWarnings("unchecked")
	public static void dialog(double money, PaySiteConfigItem siteConfigItem, String paySite) {
		long limitBean = (long)(money * 10000);
		//最底充值金额限制
		if (siteConfigItem.getMin() > money) {
			money = siteConfigItem.getMin();
		}
		//充值 最高金额限制
		if (siteConfigItem.getMax() < money) {
			money = siteConfigItem.getMax();
		}
		//匹配合适的充值计费点
		String pno = siteConfigItem.getPno();
		String payCode = siteConfigItem.getPayCode();
		HashMap<String, PayInit> initMap = (HashMap<String, PayInit>) GameCache.getObj(CacheKey.PAY_INIT_MAP);
		PayInit payInit = initMap.get(payCode); //支付方式初始数据
		if (payInit == null)
			return;
		//匹配合适的充值计费点
		PayPoint payPoint = PayUtils.matchPayPoint(pno, money, payInit.getPointList());
		if (payPoint == null)
			return;
		//组织提示文字
		String msg = siteConfigItem.getMsg();
		if (msg != null) { //替换参数 
			GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
			msg = msg.replace("{mybean}", String.valueOf(gu.getBean()));
			msg = msg.replace("{addbean}", String.valueOf(limitBean));
			msg = msg.replace("{addmoney}", String.valueOf(payPoint.getMoney()));
		}
		final PayInit init = payInit;
		final PayPoint point = payPoint;
		final String tag = paySite;
		final String tipMsg = msg;
		Database.currentActivity.runOnUiThread(new Runnable() {

			public void run() {
				PayDialog payDialog = new PayDialog(Database.currentActivity, init, point, tag, tipMsg);
				payDialog.show();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public static void toast(double money, PaySiteConfigItem siteConfigItem, String paySite) {
		long limitBean = (long)(money * 10000);
		//最底充值金额限制
		if (siteConfigItem.getMin() > money) {
			money = siteConfigItem.getMin();
		}
		//充值 最高金额限制
		if (siteConfigItem.getMax() < money) {
			money = siteConfigItem.getMax();
		}
		//匹配合适的充值计费点
		String pno = siteConfigItem.getPno();
		String payCode = siteConfigItem.getPayCode();
		HashMap<String, PayInit> initMap = (HashMap<String, PayInit>) GameCache.getObj(CacheKey.PAY_INIT_MAP);
		PayInit payInit = initMap.get(payCode); //支付方式初始数据
		if (payInit == null)
			return;
		//匹配合适的充值计费点
		PayPoint payPoint = PayUtils.matchPayPoint(pno, money, payInit.getPointList());
		if (payPoint == null)
			return;
		//组织提示文字
		String msg = siteConfigItem.getMsg();
		msg = msg.replace("客电4000666899", " ");
		if (msg != null) { //替换参数 
			GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
			msg = msg.replace("{mybean}", String.valueOf(gu.getBean()));
			msg = msg.replace("{addbean}", String.valueOf(limitBean));
			msg = msg.replace("{addmoney}", String.valueOf(payPoint.getMoney()));
		}
		//提示消息
		DialogUtils.toastTip(msg, Toast.LENGTH_LONG, Gravity.CENTER);
		//去充值
		SDKFactory.goPay(payInit, payPoint, paySite);
	}

	/**
	 * 直接跳转到SDK支付
	 * @Title: paySDK  
	 * @param @param money
	 * @param @param siteConfigItem
	 * @param @param paySiteTag
	 * @return void
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public static void paySDK(double money, PaySiteConfigItem siteConfigItem, String paySite) {
		//最低充值金额限制
		if (siteConfigItem.getMin() > money) {
			money = siteConfigItem.getMin();
		}
		//充值 最高金额限制
		if (siteConfigItem.getMax() < money) {
			money = siteConfigItem.getMax();
		}
		//匹配合适的充值计费点
		String pno = siteConfigItem.getPno();
		String payCode = siteConfigItem.getPayCode();
		HashMap<String, PayInit> initMap = (HashMap<String, PayInit>) GameCache.getObj(CacheKey.PAY_INIT_MAP);
		PayInit payInit = initMap.get(payCode); //支付方式初始数据
		if (payInit == null)
			return;
		//匹配合适的充值计费点
		PayPoint payPoint = PayUtils.matchPayPoint(pno, money, payInit.getPointList());
		if (payPoint == null)
			return;
		//去充值
		SDKFactory.goPay(payInit, payPoint, paySite);
	}
}
