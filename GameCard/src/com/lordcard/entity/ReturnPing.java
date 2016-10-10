package com.lordcard.entity;

import com.google.gson.annotations.Expose;

public class ReturnPing {

	@Expose
	private String account; // 账号
	@Expose
	private Integer networkType; // 网络类型(1=wifi,2=2g,3=3g)
	@Expose
	private String loginTime; // 登录时间
	@Expose
	private Integer pingStatus; // ping状态(1=成功,0=失败)
	@Expose
	private Integer pingMinTime; // 最小时间(ms)
	@Expose
	private Integer pingMaxTime; // 最大时间(ms)
	@Expose
	private Integer pingAvgTime; // 平均时间(ms)
	@Expose
	private Integer commandCode; // 指令编号

	public final String getAccount() {
		return account;
	}

	public final void setAccount(String account) {
		this.account = account;
	}

	public final Integer getNetworkType() {
		return networkType;
	}

	public final void setNetworkType(Integer networkType) {
		this.networkType = networkType;
	}

	public final String getLoginTime() {
		return loginTime;
	}

	public final void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

	public final Integer getPingStatus() {
		return pingStatus;
	}

	public final void setPingStatus(Integer pingStatus) {
		this.pingStatus = pingStatus;
	}

	public final Integer getPingMinTime() {
		return pingMinTime;
	}

	public final void setPingMinTime(Integer pingMinTime) {
		this.pingMinTime = pingMinTime;
	}

	public final Integer getPingMaxTime() {
		return pingMaxTime;
	}

	public final void setPingMaxTime(Integer pingMaxTime) {
		this.pingMaxTime = pingMaxTime;
	}

	public final Integer getPingAvgTime() {
		return pingAvgTime;
	}

	public final void setPingAvgTime(Integer pingAvgTime) {
		this.pingAvgTime = pingAvgTime;
	}

	public final Integer getCommandCode() {
		return commandCode;
	}

	public final void setCommandCode(Integer commandCode) {
		this.commandCode = commandCode;
	}

}
