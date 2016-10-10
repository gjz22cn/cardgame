/**
 */
package com.lordcard.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoomSignup {
	/**
	 * [{"signUp":"1","hallCode":"1","roomCode":"1"},{"signUp":"0","hallCode":
	 * "1","roomCode":"2"}] signUp=0未报名，1已报名
	 * */
	@Expose @SerializedName("hc") private String	hallCode;
	@Expose @SerializedName("rc") private String	roomCode;
	@Expose @SerializedName("su") private String	signUp;
	@Expose @SerializedName("st") private Long		stopTime;
	private boolean isClick;//是否请求

	public String getSignUp() {
		return signUp;
	}

	public void setSignUp(String signUp) {
		this.signUp = signUp;
	}

	public String getHallCode() {
		return hallCode;
	}

	public void setHallCode(String hallCode) {
		this.hallCode = hallCode;
	}

	public String getRoomCode() {
		return roomCode;
	}

	public void setRoomCode(String roomCode) {
		this.roomCode = roomCode;
	}

	public final Long getStopTime() {
		return stopTime;
	}

	public final void setStopTime(Long stopTime) {
		this.stopTime = stopTime;
	}

	public final boolean isClick() {
		return isClick;
	}

	public final void setClick(boolean isClick) {
		this.isClick = isClick;
	}
	
}
