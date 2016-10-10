package com.sdk.jifeng.pay;


import com.google.gson.annotations.Expose;

/**
 * 机锋支付
 * @author Administrator
 *
 */
public class GFanPayOrder {

	@Expose private String oriderid;		//服务器产生的订单号
	

	public String getOriderid() {
		return oriderid;
	}
	public void setOriderid(String oriderid) {
		this.oriderid = oriderid;
	}
}
