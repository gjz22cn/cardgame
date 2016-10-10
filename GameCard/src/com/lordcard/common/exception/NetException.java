package com.lordcard.common.exception;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NetException {
	
	private int id;
	@Expose @SerializedName("c") private String cause;			//问题
	@Expose @SerializedName("u") private String url;				//请求地址
	@Expose @SerializedName("ni") private String netinfo;			//网络情况
	@Expose @SerializedName("tm") private String time;			//异常时间
	@Expose @SerializedName("t") private String type;			//异常类型
	@Expose @SerializedName("a") private String apk;				//异常apk版本
	@Expose @SerializedName("n") private String netok;			//网络是否畅通
	
	public NetException() {
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCause() {
		return cause;
	}
	public void setCause(String cause) {
		this.cause = cause;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getNetinfo() {
		return netinfo;
	}
	public void setNetinfo(String netinfo) {
		this.netinfo = netinfo;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getApk() {
		return apk;
	}
	public void setApk(String apk) {
		this.apk = apk;
	}
	public String getNetok() {
		return netok;
	}
	public void setNetok(String netok) {
		this.netok = netok;
	}
}
