package com.lordcard.entity;

import com.google.gson.annotations.Expose;

/**
* @ClassName: GoodsHand
* @Description: 物品赠送
* @author shaohu
* @date 2013-7-30 下午03:46:19
* 
*/
public class GoodsGet {
	@Expose
	private String name;//物品名称
	@Expose
	private String goodCode;//物品编码
	@Expose
	private Integer count; //数量

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the goodCode
	 */
	public String getGoodCode() {
		return goodCode;
	}

	/**
	 * @param goodCode the goodCode to set
	 */
	public void setGoodCode(String goodCode) {
		this.goodCode = goodCode;
	}

	/**
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(Integer count) {
		this.count = count;
	}

}
