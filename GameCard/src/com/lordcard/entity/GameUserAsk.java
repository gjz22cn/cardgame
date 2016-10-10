package com.lordcard.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 用户反馈用解答
 * 
 * @ClassName: GameUserAsk
 * @Description: TODO
 * @author zhenggang
 * @date 2013-5-17 下午12:33:33
 */
public class GameUserAsk {

	public static final String ST_AN_NO = "0"; // 未解答
	public static final String ST_AN_YES = "1"; // 已名解答

	public static final String TP_DEFAULT = "0"; // 一般问题
	public static final String TP_COMMON = "1"; // 常见问题

	private String account; // 提问玩家账号
	private String link; // 联系方式
	@Expose
	@SerializedName("q")
	private String question; // 提问内容

	@Expose
	@SerializedName("ct")
	private String createTime; // 提问时间
	@Expose
	@SerializedName("t")
	private String type; // 数据类型　0:一般问题　1:常见问题　
	@Expose
	@SerializedName("as")
	private String answer; // 问题的回答
	@Expose
	@SerializedName("st")
	private String status; // 是否解答 0：未解答　1:解答
	@Expose
	@SerializedName("at")
	private String answerTime; // 解答时间

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAnswerTime() {
		return answerTime;
	}

	public void setAnswerTime(String answerTime) {
		this.answerTime = answerTime;
	}
}
