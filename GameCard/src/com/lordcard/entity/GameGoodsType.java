/**
 */
package com.lordcard.entity;

/**
 * @ClassName: GameGoodsType
 * @Description: 物品类型
 * @author shaohu
 * @date 2013-4-10 上午11:22:16
 * 
 */
public class GameGoodsType {

	private String id; //
	private String name; // 物品名称
	private Integer type; // 物品类型1表示虚拟物品 2表示实物物品
	private String picPath; // 物品图片
	private String title; // 标题
	private Integer orderNum; // 排序
	private String description; // 描述
	private Integer display; // 控制物品栏中物品展示方式 1:文本展示,2:图片展示

	public GameGoodsType() {
		super();
	}

	/**
	 * @param id
	 * @param name
	 * @param type
	 * @param picPath
	 * @param title
	 * @param orderNum
	 * @param description
	 * @param display
	 */
	public GameGoodsType(String id, String name, Integer type, String picPath, String title, Integer orderNum, String description, Integer display) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.picPath = picPath;
		this.title = title;
		this.orderNum = orderNum;
		this.description = description;
		this.display = display;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	public String getDescription() {
		return description;
	}

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

}
