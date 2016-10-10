/**
 */
package com.lordcard.entity;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @ClassName: GoodsExchangeVo
 * @Description: 物品兑换vo
 * @author shaohu
 * @date 2013-4-10 下午03:36:03
 * 
 */
public class GoodsExchange {
	@Expose
	@SerializedName("ti")
	private String typeId;
	@Expose
	@SerializedName("tn")
	private String typeName;
	@Expose
	@SerializedName("tc")
	private Integer count;
	@Expose
	@SerializedName("tp")
	private String picPath;
	@Expose
	@SerializedName("gp")
	private List<GoodsPart> goodsParts;
	@Expose
	@SerializedName("ab")
	private String about; // 约钻石数
	@Expose
	@SerializedName("ct")
	private Integer type; // 1表示金豆合成,2表示十二生肖合成,3表示数码合成

	// @Expose @SerializedName("dp") private Integer display; //控制物品栏中物品展示方式
	// 1:文本展示,2:图片展示

	/**
	 * @return the typeId
	 */
	public String getTypeId() {
		return typeId;
	}

	/**
	 * @param typeId
	 *            the typeId to set
	 */
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	/**
	 * @return the typeName
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @param typeName
	 *            the typeName to set
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
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
	 * @return the goodsParts
	 */
	public List<GoodsPart> getGoodsParts() {
		return goodsParts;
	}

	/**
	 * @param goodsParts
	 *            the goodsParts to set
	 */
	public void setGoodsParts(List<GoodsPart> goodsParts) {
		this.goodsParts = goodsParts;
	}

	/**
	 * @return the picPath
	 */
	public String getPicPath() {
		return picPath;
	}

	/**
	 * @param picPath
	 *            the picPath to set
	 */
	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	/**
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * @return the about
	 */
	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}
	// public Integer getDisplay() {
	// return display;
	// }
	/**
	 * @param display
	 *            the display to set
	 */
	// public void setDisplay(Integer display) {
	// this.display = display;
	// }

}
