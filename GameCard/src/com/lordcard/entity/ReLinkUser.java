package com.lordcard.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 重连的用户信息
 * @ClassName: ReLinkUser   
 * @Description: TODO 
 * @author yinhongbiao   
 * @date 2013-6-26 下午08:03:50
 */
public class ReLinkUser {

	@Expose
	@SerializedName("o")
	private int order; //顺序
	@Expose
	@SerializedName("cc")
	private int cardCount; //剩余的牌数量
	@Expose
	@SerializedName("u")
	private GameUser gameUser; //玩家信息
	@Expose
	@SerializedName("a")
	private int isAuto; //是否拖管 0否 1:是
	@Expose
	@SerializedName("r")
	private int ratio = 1; // 加倍的倍数 (1:不加倍,2:加2倍,4:加4倍)

	public ReLinkUser() {}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getCardCount() {
		return cardCount;
	}

	public void setCardCount(int cardCount) {
		this.cardCount = cardCount;
	}

	public GameUser getGameUser() {
		return gameUser;
	}

	public void setGameUser(GameUser gameUser) {
		this.gameUser = gameUser;
	}

	public int getRatio() {
		return ratio;
	}

	public void setRatio(int ratio) {
		this.ratio = ratio;
	}

	public int getIsAuto() {
		return isAuto;
	}

	public void setIsAuto(int isAuto) {
		this.isAuto = isAuto;
	}
}
