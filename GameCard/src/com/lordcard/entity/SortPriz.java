package com.lordcard.entity;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 排名大奖 model.SortPriz
 * 
 * @author Administrator <br/>
 *         create at 2013 2013-3-19 下午2:09:16
 */
public class SortPriz {
	@Expose
	@SerializedName("ks")
	private String kaiSaiTime;
	@Expose
	@SerializedName("kj")
	private String kaiJiangTime;
	@Expose
	@SerializedName("zj")
	private String zhongJiang;
	@Expose
	@SerializedName("ot")
	private int offTime;
	@Expose
	@SerializedName("mj")
	private String myJiang;

	@Expose
	@SerializedName("s")
	private List<GameUser> userList;

	public String getKaiSaiTime() {
		return kaiSaiTime;
	}

	public void setKaiSaiTime(String kaiSaiTime) {
		this.kaiSaiTime = kaiSaiTime;
	}

	public String getKaiJiangTime() {
		return kaiJiangTime;
	}

	public void setKaiJiangTime(String kaiJiangTime) {
		this.kaiJiangTime = kaiJiangTime;
	}

	public String getZhongJiang() {
		return zhongJiang;
	}

	public void setZhongJiang(String zhongJiang) {
		this.zhongJiang = zhongJiang;
	}

	public int getOffTime() {
		return offTime;
	}

	public void setOffTime(int offTime) {
		this.offTime = offTime;
	}

	public String getMyJiang() {
		return myJiang;
	}

	public void setMyJiang(String myJiang) {
		this.myJiang = myJiang;
	}

	public List<GameUser> getUserList() {
		return userList;
	}

	public void setUserList(List<GameUser> userList) {
		this.userList = userList;
	}

}
