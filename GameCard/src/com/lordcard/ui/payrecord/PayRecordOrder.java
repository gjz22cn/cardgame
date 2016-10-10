package com.lordcard.ui.payrecord;

import com.google.gson.annotations.Expose;

public class PayRecordOrder {

	public void setPreOrderType(String preOrderType) {
		this.preOrderType = preOrderType;
	}

	@Expose private double money; // *充值金额
	@Expose private long baseBean; // 充值原本获得金豆数目
	@Expose private String payStatus; // 支付状态
	@Expose private String createTime; // 创建时间
	@Expose private String preOrderNo; //预充值订单号
	@Expose private long winBean; //赢得的金豆数目
	@Expose private String preOrderType; // 是否为预充值订单(0:否,1:是),默认0

	public String getPreOrderNo() {
		return preOrderNo;
	}

	public void setPreOrderNo(String preOrderNo) {
		this.preOrderNo = preOrderNo;
	}

	public long getWinBean() {
		return winBean;
	}

	public void setWinBean(long winBean) {
		this.winBean = winBean;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public long getBaseBean() {
		return baseBean;
	}

	public void setBaseBean(long baseBean) {
		this.baseBean = baseBean;
	}

	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getPreOrderType() {
		return preOrderType;
	}
}
