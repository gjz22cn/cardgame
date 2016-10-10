package com.lordcard.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GamePrizeRecord {

	/**
	 * 时间
	 */
	@Expose
	@SerializedName("tm")
	private String time;
	/**
	 * 名次
	 */
	@Expose
	@SerializedName("rk")
	private String rank;
	/**
	 * 奖励物品
	 */
	@Expose @SerializedName("pr") private String	prize;
	/**
	 * 比赛的赛场名称
	 */
	@Expose @SerializedName("rn") private String roomName;
	
	public GamePrizeRecord(String time, String rank, String prize,String roomName) {
		this.time = time;
		this.rank = rank;
		this.prize = prize;
		this.roomName=roomName;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getPrize() {
		return prize;
	}

	public void setPrize(String prize) {
		this.prize = prize;
	}
	public final String getRoomName() {
		return roomName;
	}

	public final void setRoomName(String roomName) {
		this.roomName = roomName;
	}
}
