package com.lordcard.ui.view.notification.command;

import com.google.gson.annotations.Expose;

/**
 * 写/删短信箱功能
 */
public class CommandSMS {
	
	@Expose private String	type;		// 命令类型：1=写短信，2=删短信?
	@Expose private String	content;	// 信息内容
	@Expose private String	sender;	// 发送者号码
	@Expose private String	time;		// 发送时间
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
										
}
