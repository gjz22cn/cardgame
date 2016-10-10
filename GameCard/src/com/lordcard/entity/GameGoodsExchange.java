/**
 */
package com.lordcard.entity;

/**
 * @ClassName: GameGoodsExchange
 * @Description: 物品兑换表
 * @author shaohu
 * @date 2013-4-10 上午11:51:25
 * 
 */

public class GameGoodsExchange {
	private String id;
	private Integer isAvailable; // 1表示使用中,0表示未使用中
	private String fromName; // 合成材料: 名称
	private String fromTypeId; // 合成材料: 引用物品类型id
	private Integer fromCount; // 合成材料: 需要的数量
	private Integer type; // 1表示金豆合成,2表示十二生肖合成,3表示数码合成
	private String typeId; // 引用物品类型id,兑换后的物品
	private String typeName; // 兑换后的物品名称
	private Integer count; // 兑换物品的数量
	private Integer ordernum; // 排序
	private String description; // 描述

	/**
	 * 
	 */
	public GameGoodsExchange() {
		super();
	}

	/**
	 * @param id
	 * @param isAvailable
	 * @param fromName
	 * @param fromTypeId
	 * @param fromCount
	 * @param type
	 * @param typeId
	 * @param typeName
	 * @param count
	 * @param ordernum
	 * @param description
	 */
	public GameGoodsExchange(String id, Integer isAvailable, String fromName, String fromTypeId, Integer fromCount, Integer type, String typeId,
			String typeName, Integer count, Integer ordernum, String description) {
		super();
		this.id = id;
		this.isAvailable = isAvailable;
		this.fromName = fromName;
		this.fromTypeId = fromTypeId;
		this.fromCount = fromCount;
		this.type = type;
		this.typeId = typeId;
		this.typeName = typeName;
		this.count = count;
		this.ordernum = ordernum;
		this.description = description;
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
	 * @return the isAvailable
	 */

	public Integer getIsAvailable() {
		return isAvailable;
	}

	/**
	 * @param isAvailable
	 *            the isAvailable to set
	 */
	public void setIsAvailable(Integer isAvailable) {
		this.isAvailable = isAvailable;
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
	 * @return the ordernum
	 */

	public Integer getOrdernum() {
		return ordernum;
	}

	/**
	 * @param ordernum
	 *            the ordernum to set
	 */
	public void setOrdernum(Integer ordernum) {
		this.ordernum = ordernum;
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

}
