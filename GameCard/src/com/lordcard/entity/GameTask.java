package com.lordcard.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 游戏活动 model.GameTask
 * 
 * @author yinhb <br/>
 *         create at 2012 2012-11-6 下午4:18:43
 */
public class GameTask {

	/**
	 * 0:默认 1:邀请码，2：输入手机号码，3：完善个人资料，4：邀请好友下载，5：排行榜送金豆，6：其他活动,7:同步通讯录 8:应用下载,9:充值
	 */
	public static final int[] TASK_TYPE = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	@Expose @SerializedName("u") private String userId;
	@Expose @SerializedName("lt") private String loginToken;
	@Expose @SerializedName("m") private String mcId; // android/ios
	@Expose @SerializedName("vs") private String version; // apk版本
	@Expose @SerializedName("t") private int type; // 活动任务类型
	@Expose @SerializedName("c") private int child; // 子类
	@Expose @SerializedName("v") private String value; // 活动内容
	@Expose @SerializedName("ct") private int count; // 赠送的豆

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	// public String getTokenID() {
	// return tokenID;
	// }
	// public void setTokenID(String tokenID) {
	// this.tokenID = tokenID;
	// }
	public String getMcId() {
		return mcId;
	}

	public void setMcId(String mcId) {
		this.mcId = mcId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getChild() {
		return child;
	}

	public void setChild(int child) {
		this.child = child;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getLoginToken() {
		return loginToken;
	}

	public void setLoginToken(String loginToken) {
		this.loginToken = loginToken;
	}
}
