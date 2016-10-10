package com.sdk.jifeng;


import com.google.gson.annotations.Expose;

/**
 * 机锋支付
 * 
 */
public class GFen {
	
	/** 机锋支付类型 */
	public static final Integer	GFEN_TYPE	= 3;
	
	/** 应用编号 */
	@Expose private String		appCode;
	/** 订单编码 */
	@Expose private String		payNo;
	/** 订单名称 */
	@Expose private String		orderName;
	/** 订单描述 */
	@Expose private String		orderDesc;
	/** 商品描述 */
	@Expose private String		productName;
	/** 价格 */
	@Expose private Integer		price;
	
	public String getAppCode() {
		return appCode;
	}
	
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	
	public String getPayNo() {
		return payNo;
	}
	
	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}
	
	public String getOrderName() {
		return orderName;
	}
	
	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}
	
	public String getOrderDesc() {
		return orderDesc;
	}
	
	public void setOrderDesc(String orderDesc) {
		this.orderDesc = orderDesc;
	}
	
	public String getProductName() {
		return productName;
	}
	
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public Integer getPrice() {
		return price;
	}
	
	public void setPrice(Integer price) {
		this.price = price;
	}
	
}
