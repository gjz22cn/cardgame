package com.sdk.util.vo;

import com.google.gson.annotations.Expose;

/**
 * 计费位置支付信息配置
 *
 */
public class PaySiteConfigItem implements java.io.Serializable {

	private static final long serialVersionUID = 2272644722254384231L;
	
	@Expose private int node;			//0:主充值方式    其他数字为备用充值方式
	@Expose private String payCode;  	//支付方式编号
	@Expose private String pno;			//充值计费点编号    如果为auto  则根据情况自动计算
	@Expose private int min;			//最低充值的金额   为0时无效，当pno为auto时 与计算出的充值金额一起按最大值去匹配最合适的计费点
	@Expose private int max;			//一次最多的充值金额
	@Expose private String model;		//dialog:模式  ,toast:toast自动,sdk:直接到sdk
	@Expose private String smsType;		//短信发送方式  sys:使用系统短信发送 ,   auto:直接代码内部发送   没有配置则默认code
	@Expose	private String payTo;		//充值 到哪个账号， online:在线的账号, offline:单机账号  不配置默认为online
	@Expose private String msg;			//充值时候的提示文字
	
	public int getNode() {
		return node;
	}
	
	public void setNode(int node) {
		this.node = node;
	}
	
	
	public String getPayCode() {
		return payCode;
	}

	
	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}

	public String getModel() {
		return model;
	}
	
	public void setModel(String model) {
		this.model = model;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getPno() {
		return pno;
	}

	public void setPno(String pno) {
		this.pno = pno;
	}
	
	public int getMin() {
		return min;
	}
	
	public void setMin(int min) {
		this.min = min;
	}

	
	public int getMax() {
		return max;
	}

	
	public void setMax(int max) {
		this.max = max;
	}

	
	public String getSmsType() {
		return smsType;
	}

	
	public void setSmsType(String smsType) {
		this.smsType = smsType;
	}

	
	public String getPayTo() {
		return payTo;
	}

	
	public void setPayTo(String payTo) {
		this.payTo = payTo;
	}
}
