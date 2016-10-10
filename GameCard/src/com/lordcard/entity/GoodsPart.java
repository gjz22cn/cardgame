/**
 */
package com.lordcard.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @ClassName: GoodsPart
 * @Description: TODO
 * @author shaohu
 * @date 2013-4-10 下午03:39:50
 * 
 */
public class GoodsPart {

	@Expose
	@SerializedName("fn")
	private String fromName; // 名称
	@Expose
	@SerializedName("ft")
	private String fromTypeId; // 物品类型id
	@Expose
	@SerializedName("fc")
	private Integer fromCount=0; // 数量
	@Expose
	@SerializedName("ut")
	private String unit = ""; //单位  

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * @return the fromName
	 */
	public String getFromName() {
		return fromName;
	}

	/**
	 * @param fromName
	 *            the fromName to set
	 */
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	/**
	 * @return the fromTypeId
	 */
	public String getFromTypeId() {
		return fromTypeId;
	}

	/**
	 * @param fromTypeId
	 *            the fromTypeId to set
	 */
	public void setFromTypeId(String fromTypeId) {
		this.fromTypeId = fromTypeId;
	}

	/**
	 * @return the fromCount
	 */
	public Integer getFromCount() {
		return fromCount;
	}

	/**
	 * @param fromCount
	 *            the fromCount to set
	 */
	public void setFromCount(Integer fromCount) {
		this.fromCount = fromCount;
	}

}
