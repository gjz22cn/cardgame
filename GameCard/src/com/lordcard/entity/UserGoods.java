/**
 */
package com.lordcard.entity;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @ClassName: UserGoods
 * @Description: 用户物品
 * @author shaohu
 * @date 2013-4-11 下午03:01:09
 * 
 */
public class UserGoods {

	@Expose @SerializedName("di") private Integer display; // 1表示文本显示,2表示图片显示区
	@Expose @SerializedName("dg") private List<Goods> goods; // 用户物品数量表
	@Expose @SerializedName("dt") private GoodsType goodsType; // 物品类型信息

	/**
	 * @return the display
	 */
	public Integer getDisplay() {
		return display;
	}

	/**
	 * @param display
	 *            the display to set
	 */
	public void setDisplay(Integer display) {
		this.display = display;
	}

	/**
	 * @return the goods
	 */
	public List<Goods> getGoods() {
		return goods;
	}

	/**
	 * @param goods
	 *            the goods to set
	 */
	public void setGoods(List<Goods> goods) {
		this.goods = goods;
	}

	/**
	 * @return the goodsType
	 */
	public GoodsType getGoodsType() {
		return goodsType;
	}

	/**
	 * @param goodsType
	 *            the goodsType to set
	 */
	public void setGoodsType(GoodsType goodsType) {
		this.goodsType = goodsType;
	}
}
