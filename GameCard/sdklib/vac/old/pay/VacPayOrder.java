package com.sdk.vac.pay;

import com.google.gson.annotations.Expose;

/**
 * 联通沃商店支付
 * 
 * @author Administrator
 * 
 */
public class VacPayOrder {

	@Expose private String postURL; // 服务器接收支付回调结果的地址
	@Expose private String company; // 公司名字
	@Expose private String phone; // 电话号码
	@Expose private String oriderid; // 服务器产生的订单号
	@Expose private String productName;
	@Expose private String payNo;

	public String getPayNo() {
		return payNo;
	}

	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getPostURL() {
		return postURL;
	}

	public void setPostURL(String postURL) {
		this.postURL = postURL;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	// public String getDaoju() {
	// return daoju;
	// }
	// public void setDaoju(String daoju) {
	// this.daoju = daoju;
	// }
	// public String getMoney() {
	// return money;
	// }
	// public void setMoney(String money) {
	// this.money = money;
	// }
	// public String getConsumeCode() {
	// return consumeCode;
	// }
	// public void setConsumeCode(String consumeCode) {
	// this.consumeCode = consumeCode;
	// }
	public String getOriderid() {
		return oriderid;
	}

	public void setOriderid(String oriderid) {
		this.oriderid = oriderid;
	}
}
