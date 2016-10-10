package com.lordcard.entity;

public class PingVo {
	/**报文顺序号*/
	private int icmp_seq;
	/**生存时间*/
	private int ttl;
	/**往返时间*/
	private float time;
	/**IP地址*/
	private String ip;

	public final int getIcmp_seq() {
		return icmp_seq;
	}

	public final void setIcmp_seq(int icmp_seq) {
		this.icmp_seq = icmp_seq;
	}

	public final int getTtl() {
		return ttl;
	}

	public final void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public final float getTime() {
		return time;
	}

	public final void setTime(float time) {
		this.time = time;
	}

	public final String getIp() {
		return ip;
	}

	public final void setIp(String ip) {
		this.ip = ip;
	}

}
