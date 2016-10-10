package com.lordcard.ui.payrecord;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 预充值
 */
public class PreOrder {

	@Expose @SerializedName("on") private String orderNo; //预充值订单号
	@Expose @SerializedName("a") private String account; //帐号
	@Expose @SerializedName("gc") private String groupCode; //分组编号
	@Expose @SerializedName("pt") private Integer payType; //支付类型
	@Expose @SerializedName("mo") private Double money; //充值金额
	@Expose @SerializedName("bb") private Long baseBean; //充值原本获得金豆数目
	@Expose @SerializedName("wb") private Long winBean; //赢得的金豆数目

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Long getBaseBean() {
		return baseBean;
	}

	public void setBaseBean(Long baseBean) {
		this.baseBean = baseBean;
	}

	public Long getWinBean() {
		return winBean;
	}

	public void setWinBean(Long winBean) {
		this.winBean = winBean;
	}
}
