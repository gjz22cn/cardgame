package com.sdk.ssw;

import com.google.gson.annotations.Expose;

/**
 * 随手玩
 */
public class SSW {
	
	//orderDesc
	
	/** 订单描述 */
	@Expose private String					orderDesc;
	/** 价格 */
	@Expose private Integer					price;
	
	
	public String getOrderDesc() {
		return orderDesc;
	}
	
	public void setOrderDesc(String orderDesc) {
		this.orderDesc = orderDesc;
	}
	
	
	public Integer getPrice() {
		return price;
	}
	
	public void setPrice(Integer price) {
		this.price = price;
	}
	
}
