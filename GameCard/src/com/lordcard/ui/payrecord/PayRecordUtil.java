package com.lordcard.ui.payrecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lordcard.common.util.PatternUtils;

public class PayRecordUtil {
	public enum RecordStatus {
		Record_none,
		/**已充值*/
		Record_charge,
		/**冻结**/
		Record_freeze,
		/**等待充值**/
		Record_wait,
		/**充值失败**/
		Record_failed,
	}
	public enum OrderType{
		Order_none,
		/**普通充值订单**/
		Order_normal,
		/**预充值订单**/
		Order_prepay,
	}
	public static final String PAY_DATE = "pay_date";
	public static final String MONNEY = "monney";
	public static final String PAY_STATUS = "pay_status";
	public static final String BEANS = "beans";
	public static final String ORDER_TYPE = "order_type";
	public static final String PRE_ORDER_NO = "pre_order_no";
	public static List<Map<String, String>> listRecord(List<PayRecordOrder> payRecordOrders) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if(null == payRecordOrders)
			return list;
		Map<String, String> map = null;
		for (int i = 0; i < payRecordOrders.size(); i++) {
			PayRecordOrder payRecordOrder = payRecordOrders.get(i);
			String datePay = payRecordOrder.getCreateTime().substring(0, 10).replaceAll("-", "/");

			String money = String.valueOf((long) payRecordOrder.getMoney());
			String beans = PatternUtils.formatIqBeans(payRecordOrder.getBaseBean() + payRecordOrder.getWinBean());
			
			/** 0,200,400分别代表待充值，已充值，失败**/
			String payStatus = payRecordOrder.getPayStatus();
			if (payStatus.equals("400")) {
				payStatus = RecordStatus.Record_failed.toString();
			} else if(payStatus.equalsIgnoreCase("200")){
				payStatus = RecordStatus.Record_charge.toString();
			}else {
				payStatus = RecordStatus.Record_freeze.toString();
			}
			
			/**是否为预充值订单(0:否,1:是),默认0**/
			String orderType = String.valueOf(payRecordOrder.getPreOrderType());
			if(orderType.equalsIgnoreCase("1")){
				orderType = OrderType.Order_prepay.toString();
			}else {
				orderType = OrderType.Order_normal.toString();
			}
			
			String preOrderNo = payRecordOrder.getPreOrderNo();
			map = new HashMap<String, String>();
			map.put(PAY_DATE, datePay);
			map.put(MONNEY, money);
			map.put(PAY_STATUS, payStatus);
			map.put(BEANS, beans);
			map.put(ORDER_TYPE, orderType);
			map.put(PRE_ORDER_NO, preOrderNo);
			
			list.add(map);
		}
		return list;
	}
}
