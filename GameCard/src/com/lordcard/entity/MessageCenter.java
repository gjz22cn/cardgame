package com.lordcard.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageCenter {
	
	@Expose @SerializedName("i") private long		id;		//ID
	@Expose @SerializedName("t") private String		title;		//标题
	@Expose @SerializedName("c") private String		content;	//内容
	@Expose @SerializedName("tm") private String	ctime;		//创建时间
	@Expose @SerializedName("ty") private int		type;		//1:公告，2：推送															
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
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
	
	public String getCtime() {
		return ctime;
	}
	
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
	
}
