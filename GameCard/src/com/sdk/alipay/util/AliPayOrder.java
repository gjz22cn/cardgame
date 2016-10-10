package com.sdk.alipay.util;

import com.google.gson.annotations.Expose;

public class AliPayOrder {

	@Expose
	private String partner; // 支付宝合作商家ID
	@Expose
	private String seller; // 账户ID
	@Expose
	private String outTradeNo; // 系统订单号 对应PayOrder orderNo
	// private String subject; //商品名称
	// private String body; //商品描述
	// private String totalFee; //支付金额
	@Expose
	private String notifyUrl; // 异步回调接口 需要外网能访问
	@Expose
	private String rsaShopPrivate; // 商户rsa私钥
	@Expose
	private String rsaPublic; // 支付宝rsa公钥

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	// public String getSubject() {
	// return subject;
	// }
	// public void setSubject(String subject) {
	// this.subject = subject;
	// }
	// public String getBody() {
	// return body;
	// }
	// public void setBody(String body) {
	// this.body = body;
	// }
	// public String getTotalFee() {
	// return totalFee;
	// }
	// public void setTotalFee(String totalFee) {
	// this.totalFee = totalFee;
	// }
	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getRsaShopPrivate() {
		return rsaShopPrivate;
	}

	public void setRsaShopPrivate(String rsaShopPrivate) {
		this.rsaShopPrivate = rsaShopPrivate;
	}

	public String getRsaPublic() {
		return rsaPublic;
	}

	public void setRsaPublic(String rsaPublic) {
		this.rsaPublic = rsaPublic;
	}

}
