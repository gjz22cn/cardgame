/**
 */
package com.lordcard.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @ClassName: Goods
 * @Description: 物品
 * @author shaohu
 * @date 2013-4-11 下午03:02:24
 * 
 */
public class Goods {

	@Expose
	@SerializedName("ti")
	private String typeId; // 物品id
	@Expose
	@SerializedName("tn")
	private String name; // 物品名称
	@Expose
	@SerializedName("tc")
	private int couponNum=0; // 物品数量
	@Expose
	@SerializedName("tp")
	private String picPath;

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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the couponNum
	 */
	public int getCouponNum() {
		return couponNum;
	}

	/**
	 * @param couponNum
	 *            the couponNum to set
	 */
	public void setCouponNum(int couponNum) {
		this.couponNum = couponNum;
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

}
