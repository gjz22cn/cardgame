package com.sdk.weipai.pay;



import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
* @ClassName: WiipayOrder
* @Description:  微派订单
* @author shaohu
* @date 2013-7-16 上午11:56:40
* 
*/
public class WiipayOrder {
	//app编号
	@Expose
	@SerializedName("ac")
	private String appCode;
	//计费编号
	@Expose
	@SerializedName("pc")
	private String payCode;
	//订单编号
	@Expose
	@SerializedName("on")
	private String orderNo;
	//价格
	@Expose
	@SerializedName("pi")
	private Integer price;

	/**
	 * @return the appCode
	 */
	public String getAppCode() {
		return appCode;
	}

	/**
	 * @param appCode the appCode to set
	 */
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	/**
	 * @return the payCode
	 */
	public String getPayCode() {
		return payCode;
	}

	/**
	 * @param payCode the payCode to set
	 */
	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}

	/**
	 * @return the orderNo
	 */
	public String getOrderNo() {
		return orderNo;
	}

	/**
	 * @param orderNo the orderNo to set
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	/**
	 * @return the price
	 */
	public Integer getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(Integer price) {
		this.price = price;
	}

}
