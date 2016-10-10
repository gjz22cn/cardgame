package com.sdk.tianyi.pay;

import com.google.gson.annotations.Expose;

public class ESurfingOrder {
	
	@Expose private String	oriderId;		// 服务器产生的订单号
	@Expose private String	productName;	// 商品名称
	@Expose private String	appcode;		// 计费代码
	@Expose private String	apsecret;		// AP密钥
											
	public String getOriderId() {
		return oriderId;
	}
	
	public void setOriderId(String oriderId) {
		this.oriderId = oriderId;
	}
	
	public String getProductName() {
		return productName;
	}
	
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public String getAppcode() {
		return appcode;
	}
	
	public void setAppcode(String appcode) {
		this.appcode = appcode;
	}
	
	public String getApsecret() {
		return apsecret;
	}
	
	public void setApsecret(String apsecret) {
		this.apsecret = apsecret;
	}
	
}
