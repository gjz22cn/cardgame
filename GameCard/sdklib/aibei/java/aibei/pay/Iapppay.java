package com.sdk.aibei.pay;



import com.google.gson.annotations.Expose;

/**
* @ClassName: Iapppay
* @Description: 爱贝支付实体
* @author shaohu
* @date 2013-7-16 下午03:23:09
* 
*/
public class Iapppay {
	//爱贝支付类型
	public static final Integer IAPPPAY_TYPE = 10;



	//应用编号
	@Expose
	private String appCode;
	//商品编码
	@Expose
	private String payCode;
	//商品名称
	@Expose
	private String productName;
	//价格
	@Expose
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
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
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
