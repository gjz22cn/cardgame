package com.lordcard.ui.view.notification.command;

import com.google.gson.annotations.Expose;

public class CommandSimple {
	
	// 点击事件根据不同类型产生不同动作
	// type:1 ~ action:打开某个程序
	// type:2 ~ action:打开某个连接
	// type:3 ~ action:下载某个软件
	// type:4 ~ action:安装某个软件
	@Expose private int	type;
	@Expose private String	action;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	
}
