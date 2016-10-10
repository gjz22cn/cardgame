package com.lordcard.entity;

import com.google.gson.annotations.Expose;

public class ServerConfig {

	@Expose
	private String port; // 游戏端口号

	public ServerConfig(String port) {
		this.port = port;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

}
