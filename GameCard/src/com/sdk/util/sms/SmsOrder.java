package com.sdk.util.sms;

import com.google.gson.annotations.Expose;

public class SmsOrder {

	@Expose private String orderNo; // 订单号码
	@Expose private String smsTxt; 	// 发送指令

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getSmsTxt() {
		return smsTxt;
	}
	
	public void setSmsTxt(String smsTxt) {
		this.smsTxt = smsTxt;
	}
	
}
