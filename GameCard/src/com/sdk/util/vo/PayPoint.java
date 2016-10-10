package com.sdk.util.vo;

import com.google.gson.annotations.Expose;

/**
 * 计费点
 * @author Administrator
 */
public class PayPoint implements java.io.Serializable {

	
	private static final long serialVersionUID = 7634857459826327579L;
	
	public static final String AUTO = "auto"; //自动计算计费标识
	@Expose private String no; //编号
	@Expose private String name; //名称
	@Expose private String smsCall;	//短信支付时候对应的短信发送号码
	@Expose private String value; //SDK计费值
	@Expose private int money; //对应金额

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public String getSmsCall() {
		return smsCall;
	}
	
	public void setSmsCall(String smsCall) {
		this.smsCall = smsCall;
	}
	
}
