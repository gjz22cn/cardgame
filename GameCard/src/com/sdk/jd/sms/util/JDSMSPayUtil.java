package com.sdk.jd.sms.util;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import cn.cmgame.billing.api.BillingResult;
import cn.cmgame.billing.api.GameInterface;







import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Database;
import com.lordcard.entity.JsonResult;
import com.lordcard.network.base.ThreadPool;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.prerecharge.PrerechargeManager;
import com.lordcard.ui.view.Assistant;
import com.sdk.group.GroupPayDetailActivity;
import com.sdk.jdpay.JDOrder;
import com.sdk.jdpay.JDSMSConfig;
import com.sdk.mmpay.sms.util.MMOrder;
import com.sdk.util.PaySite;
import com.sdk.util.vo.PayPoint;

public class JDSMSPayUtil {
	private static Context context;
	public static void setContext(Context ctx)
	{
		context = ctx;
	}
	public static Context getContext()
	{
		return context;
	}
	public static int getOperators(){
		int SMS_Type = 0;
		TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		 System.out.println("---------------2"); 
		String imsi = telManager.getSubscriberId();
		 System.out.println("-------------------3"); 
		if(imsi!=null)
		{
			if(imsi.startsWith("46000") || imsi.startsWith("46002"))
			{//因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
		     //中国移动
				SMS_Type = 0;
	        }
			else if(imsi.startsWith("46001"))
			{
				//中国联通
				SMS_Type = 2;
	        }
			else if(imsi.startsWith("46003"))
	        {
				//中国电信
				SMS_Type = 1;
	        }
		}
		return SMS_Type;
	}
	/**
	 * 基地支付开始
	 * @param point
	 * @param paySiteTag
	 */
	 // 监听SDK付费请求结果，请合作方不要直接抄袭如下代码（仅用于告知调用方式），需要根据自身游戏逻辑处理监听结果
    static final GameInterface.IPayCallback payCallback = new GameInterface.IPayCallback() {
      @Override
      public void onResult(int resultCode, String billingIndex, Object obj) {
        String result = "";
        switch (resultCode) {
          case BillingResult.SUCCESS:
        	 JDSMSConfig.PAY_ORDER = null;
           // result = "购买道具：[" + billingIndex + "] 成功！";
			/*Database.chargingProcessDia = DialogUtils.getChargingProgressDialog(GroupPayDetailActivity.smsTxt, "充值中，请稍后...");
			Database.chargingProcessDia.setCancelable(false);
			Database.chargingProcessDia.show();*/
        	DialogUtils.mesTip("短信发送成功，由于网络延时金豆可能不会立即到账。请留意您的金豆变化", false,true);
            break;
          case BillingResult.FAILED:
        	JDSMSConfig.PAY_ORDER = null;
            result = "购买道具：[" + billingIndex + "] 失败！";
            break;
          default:
        	JDSMSConfig.PAY_ORDER = null;
            result = "购买道具：[" + billingIndex + "] 取消！";
            break;
        }
       // Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
      }
    };
    static String feeCode = "";
	public static void goPay(final PayPoint point,final String paySiteTag){
		feeCode = "";
		Log.e("smsSender", point.getName()+":"+point.getNo());
		if(point.getNo().equals("p1"))
		{
			feeCode = "001";
		}
		else if(point.getNo().equals("p2"))
		{
			feeCode = "002";
		}
		else if(point.getNo().equals("p3"))
		{
			feeCode = "003";
		}
		else if(point.getNo().equals("p4"))
		{
			feeCode = "004";
		}
		else if(point.getNo().equals("p5"))
		{
			feeCode = "005";
		}
		else if(point.getNo().equals("p6"))
		{
			feeCode = "006";
		}
		else if(point.getNo().equals("p7"))
		{
			feeCode = "007";
		}
		else if(point.getNo().equals("p8"))
		{
			feeCode = "008";
		}
		else if(point.getNo().equals("p9"))//记牌器
		{
			feeCode = "009";
		}
		else if(point.getNo().equals("p10"))
		{
			feeCode = "010";
		}
		else if(point.getNo().equals("p11"))
		{
			feeCode = "011";
		}
		try {
			if (JDSMSConfig.PAY_ORDER != null)
				return;
			
			ThreadPool.startWork(new Runnable() {

				public void run() {
					// 先提交充值订单
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("goodsName", point.getName()); 	//购买的物品名称
					paramMap.put("money",String.valueOf(point.getMoney()));
					paramMap.put("payFromType",paySiteTag);		//充值的标识位
					//判断是不是预充值
					if (PaySite.PREPARERECHARGE.equalsIgnoreCase(paySiteTag)  && null != PrerechargeManager.mPayRecordOrder.getPreOrderNo()) {
						paramMap.put(PrerechargeManager.PRERECHARGE_ORDER_PARAMS_PREORDERNO, PrerechargeManager.mPayRecordOrder.getPreOrderNo());
						paramMap.put(PrerechargeManager.PRERECHARGE_ORDER_PARAMS_PREORDERTYPE, "1");
					}
					if (Assistant.ASSID != null && Assistant.BTNCODE != null) {
						paramMap.put("asstId", Assistant.ASSID);
						paramMap.put("btnCode", Assistant.BTNCODE);
						Assistant.ASSID = null;
						Assistant.BTNCODE = null;
					}
					if (Database.JOIN_ROOM != null) {
						paramMap.put("payFromItem",Database.JOIN_ROOM.getCode());
					}
					
					String resultJson = HttpRequest.addPayOrder(JDSMSConfig.PAY_ORDER_URL, paramMap);
					JsonResult result = JsonHelper.fromJson(resultJson, JsonResult.class);
					if (JsonResult.SUCCESS.equals(result.getMethodCode())) {
						JDOrder mmorder = JsonHelper.fromJson(result.getMethodMessage(), JDOrder.class);
						JDSMSConfig.PAY_ORDER = mmorder.getOrderNo();
						for(int i=JDSMSConfig.PAY_ORDER.length();i<16;i++)
						{
							JDSMSConfig.PAY_ORDER += "#";
						}
						Database.currentActivity.runOnUiThread(new Runnable() {
							public void run() {
								GameInterface.doBilling(context,true, true, feeCode, JDSMSConfig.PAY_ORDER, payCallback);
								}
						});
					} else {
						DialogUtils.mesTip(result.getMethodMessage(), true);
					}
				}
			});
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
