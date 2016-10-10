/**
 */
package com.lordcard.entity;

import com.google.gson.annotations.Expose;

/**
 * @ClassName: PrizeProgram
 * @Description: 奖励
 * @author shaohu
 * @date 2013-6-14 下午03:40:42
 * 
 */
public class PrizeGoods  {
	/**
	 * 物品编码
	 */
	@Expose
	private String goodsCode;
	/**
	 * 物品数量
	 */
	@Expose
	private Integer count;

	/**
	 * 物品名称
	 */
	@Expose
	private String goodsName;

	/**
	 * 物品单位
	 */
	@Expose
	private String unit;

	/**
	 * @return the goodsCode
	 */
	public String getGoodsCode() {
		return goodsCode;
	}

	/**
	 * @param goodsCode
	 *            the goodsCode to set
	 */
	public void setGoodsCode(String goodsCode) {
		this.goodsCode = goodsCode;
	}

	/**
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(Integer count) {
		this.count = count;
	}

	/**
	 * @return the goodsName
	 */
	public String getGoodsName() {
		return goodsName;
	}

	/**
	 * @param goodsName
	 *            the goodsName to set
	 */
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @param unit
	 *            the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}
}
