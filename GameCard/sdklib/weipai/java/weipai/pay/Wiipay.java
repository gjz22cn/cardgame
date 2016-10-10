package com.sdk.weipai.pay;



import com.google.gson.annotations.Expose;

/**
* @ClassName: Wiipay
* @Description:  微派实体
* @author shaohu
* @date 2013-7-16 上午11:56:25
* 
*/
public class Wiipay {
	
	//微派支付类型
	public static final Integer WIIPAY_TYPE = 9;


	//app编号
	@Expose
	private String appCode;
	//计费编码
	@Expose
	private String payCode;
	//计费项名
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
