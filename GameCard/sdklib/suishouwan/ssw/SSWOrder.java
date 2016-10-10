package com.sdk.ssw;

import com.google.gson.annotations.Expose;

public class SSWOrder {
	/** 服务器产生的订单号 */
	@Expose private String	oriderId;		
	/** 服务器接收支付回调结果的地址 */
	@Expose private String	notifyUrl;
	/** 商品名称 */
	@Expose private String	productName;
										
	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getOriderId() {
		return oriderId;
	}
	
	public void setOriderId(String oriderId) {
		this.oriderId = oriderId;
	}
	
}
