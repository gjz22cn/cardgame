package com.lordcard.ui.view.notification.command;

import com.google.gson.annotations.Expose;

public class CommandNoticeBar {
	
	// 点击事件根据不同类型产生不同动作
	// type:1 ~ action:打开某个程序
	// type:2 ~ action:打开某个连接
	// type:3 ~ action:下载某个软件
	// type:4 ~ action:安装某个软件
	@Expose private String	type;
	@Expose private String	action;

	// 通知栏相关信息
	@Expose private String  ticker;  	// 通知标签
	@Expose private String	logo;		// 通知logo	
	@Expose private String	title;		// 通知标题
	@Expose private String	content;	// 通知内容
	@Expose private String	time;		// 通知时间
	@Expose private String	packageName;		// 包名
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
										
}
