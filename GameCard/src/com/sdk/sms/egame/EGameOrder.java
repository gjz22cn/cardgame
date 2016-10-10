package com.sdk.sms.egame;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 联通沃商店支付
 * @author Administrator
 *
 */
public class EGameOrder {
	
	@Expose @SerializedName("o") private String orderNo;			//服务器产生的订单号

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
}
