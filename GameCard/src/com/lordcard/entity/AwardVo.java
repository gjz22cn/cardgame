package com.lordcard.entity;

/**
 * 奖励方案Vo
 * 
 * @author Administrator
 */
public class AwardVo {
	private int no;// 排名
	private String name;// 昵称
	private String integral;// 积分
	private String prize;// 奖品

	public AwardVo(int no, String name, String integral, String prize) {
		this.no = no;
		this.name = name;
		this.integral = integral;
		this.prize = prize;
	}

	public final int getNo() {
		return no;
	}

	public final void setNo(int no) {
		this.no = no;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getIntegral() {
		return integral;
	}

	public final void setIntegral(String integral) {
		this.integral = integral;
	}

	public final String getPrize() {
		return prize;
	}

	public final void setPrize(String prize) {
		this.prize = prize;
	}

}
