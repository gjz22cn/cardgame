package com.sdk.util.vo;

import java.util.List;

import com.google.gson.annotations.Expose;


/**
 * 支付初始化配置数据
 */
public class PayInit  implements java.io.Serializable {
	
	
	private static final long serialVersionUID = 4659677484714920291L;
	
	@Expose private String appId;					//初化的密钥对ID
	@Expose private String appkey;					//初化的密钥对Key
	@Expose private String appCode;					//初化时的Code(一般只需要前2个，有些可能有三个初数华)
	
	@Expose private String callBack;				//后台的回调地址
	@Expose private String model;					//支付的类型,多个用英文逗号分隔　sdk,sms
	
	@Expose private String factory;					//支付的执行类
	
	@Expose private List<PayPoint> pointList;		//计费点列表

	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}
	
	
	public String getModel() {
		return model;
	}

	
	public void setModel(String model) {
		this.model = model;
	}

	public String getCallBack() {
		return callBack;
	}
	
	public void setCallBack(String callBack) {
		this.callBack = callBack;
	}

	public List<PayPoint> getPointList() {
		return pointList;
	}
	
	public void setPointList(List<PayPoint> pointList) {
		this.pointList = pointList;
	}

	
	public String getFactory() {
		return factory;
	}

	
	public void setFactory(String factory) {
		this.factory = factory;
	}

	
	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	
	
//	public static void main(String[] args) {
//		PayInit payInit = new PayInit();
//		payInit.setAppId("300002834612");
//		payInit.setAppkey("766D3EB81AEF8566");
//		payInit.setCallBack("");
//		
//		List<PayPoint> pointList = new ArrayList<PayPoint>();
//		PayPoint po = new PayPoint();
//		po.setNo("p1");
//		po.setName("1万金豆");
//		po.setValue("30000283461201");
//		po.setMoney("1");
//		pointList.add(po);
//		payInit.setPointList(pointList);
//		System.out.println(JsonHelper.toJson(payInit));
//	}
}
