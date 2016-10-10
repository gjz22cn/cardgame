package com.lordcard.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GameScoreTradeRank {

	/**
	 * 用户帐号
	 */
	@Expose
	@SerializedName("ac")
	private String account;
	/**
	 * 用户帐号
	 */
	@Expose
	@SerializedName("na")
	private String nickName;
	/**
	 * 名次
	 */
	@Expose
	@SerializedName("ra")
	private String rank;
	/**
	 * 积分
	 */
	@Expose
	@SerializedName("ce")
	private String score;
	/**
	 * 是否是自己
	 */
	@Expose
	@SerializedName("bl")
	private boolean isSelf;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public final boolean isSelf() {
		return isSelf;
	}

	public final void setSelf(boolean isSelf) {
		this.isSelf = isSelf;
	}
}
