package com.sdk.dianjin.pay;

import com.google.gson.annotations.Expose;

/**
 * @ClassName: DpayOrder
 * @Description: 点金订单
 * @author shaohu
 * @date 2013-5-11 上午11:57:23
 * 
 */
public class DpayOrder {

	@Expose
	private String orderNo; // 订单号码

	/**
	 * @return the orderNo
	 */
	public String getOrderNo() {
		return orderNo;
	}

	/**
	 * @param orderNo
	 *            the orderNo to set
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

}
