package com.lordcard.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 游戏玩家的物品
 */
public class GameUserGoods {

	/**
	 * 抽奖券
	 */
	@Expose
	@SerializedName("cpn")
	private int couponNum; // 抽奖卷数量
	@Expose
	@SerializedName("b")
	private long bean; // 我的金豆

	public GameUserGoods() {}

	public int getCouponNum() {
		return couponNum;
	}

	public void setCouponNum(int couponNum) {
		this.couponNum = couponNum;
	}

	public long getBean() {
		return bean;
	}

	public void setBean(long bean) {
		this.bean = bean;
	}

}
