/**
 */
package com.lordcard.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @ClassName: GoodsType
 * @Description: 物品类型数据
 * @author shaohu
 * @date 2013-4-11 上午10:46:50
 * 
 */
public class GoodsType {

	@Expose
	@SerializedName("i")
	private String id; //
	@Expose
	@SerializedName("n")
	private String name; // 物品名称
	@Expose
	@SerializedName("p")
	private String picPath; // 物品图片
	@Expose
	@SerializedName("t")
	private String title; // 标题
	@Expose
	@SerializedName("va")
	private String value; // 值 话费面值
	@Expose
	@SerializedName("pr")
	private String props; // 属性
	@Expose
	@SerializedName("ct")
	private Integer compositeType; // 合成类型

	private Integer orderNum; // 排序
	@Expose
	@SerializedName("dp")
	private String description; // 描述
	private Integer display; // 控制物品栏中物品展示方式 1:文本展示,2:图片展示

	@Expose
	@SerializedName("ps")
	private Integer propsSign; //是否为道具(0=非道具，1=道具)

	public Integer getPropsSign() {
		return propsSign;
	}

	public void setPropsSign(Integer propsSign) {
		this.propsSign = propsSign;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the id
	 */
	public String getProps() {
		return props;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setProps(String props) {
		this.props = props;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the orderNum
	 */
	public Integer getOrderNum() {
		return orderNum;
	}

	/**
	 * @param orderNum
	 *            the orderNum to set
	 */
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

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

	public Integer getCompositeType() {
		return compositeType;
	}

	public void setCompositeType(Integer compositeType) {
		this.compositeType = compositeType;
	}
}
