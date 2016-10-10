/**
*/
package com.lordcard.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
* @ClassName: GameAsistantContent
* @Description: 游戏助理消息
* @author 
* @date 2013-8-1 下午03:52:39
* 
*/
public class GameAsistantContent {

	@Expose
	@SerializedName("id")
	private Long id;
	//助手图标
	@Expose
	@SerializedName("ai")
	private String asstIcon;
	//助手小图标
	@Expose
	@SerializedName("si")
	private String smallIcon;

	//显示方式 2：图片,1:文本,3消息
	@Expose
	@SerializedName("di")
	private Integer display;
	//消息内容
	@Expose
	@SerializedName("ct")
	private String content;
	//消息按钮json
	@Expose
	@SerializedName("ba")
	private String btnAc;
	//消息优先级
	@Expose
	@SerializedName("lv")
	private Integer level;

	@Expose
	@SerializedName("vt")
	private String validTime; //有效期

	@Expose
	@SerializedName("bi")
	private String bind;
	
	@Expose
	@SerializedName("ti")
	private String title;//标题
	
	@Expose
	@SerializedName("pt")
	private String pushTime;//推送时间间隔
	
	/**
	 * 类型
	 */
	@Expose
	@SerializedName("tp")
	private Integer type;
	/**
	 * 顺序
	 */
	@Expose
	@SerializedName("od")
	private Integer order;
	
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}


	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getPushTime() {
		return pushTime;
	}

	public void setPushTime(String pushTime) {
		this.pushTime = pushTime;
	}

	/**
	 * 报名的房间编号
	 */
	@Expose
	@SerializedName("jc")
	private String joinCode;

	public String getJoinCode() {
		return joinCode;
	}

	public void setJoinCode(String joinCode) {
		this.joinCode = joinCode;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBind() {
		return bind;
	}

	public void setBind(String bind) {
		this.bind = bind;
	}

	public String getValidTime() {
		return validTime;
	}

	public void setValidTime(String validTime) {
		this.validTime = validTime;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the asstIcon
	 */
	public String getAsstIcon() {
		return asstIcon;
	}

	/**
	 * @param asstIcon the asstIcon to set
	 */
	public void setAsstIcon(String asstIcon) {
		this.asstIcon = asstIcon;
	}

	/**
	 * @return the display
	 */
	public Integer getDisplay() {
		return display;
	}

	/**
	 * @param display the display to set
	 */
	public void setDisplay(Integer display) {
		this.display = display;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the btnAc
	 */
	public String getBtnAc() {
		return btnAc;
	}

	/**
	 * @param btnAc the btnAc to set
	 */
	public void setBtnAc(String btnAc) {
		this.btnAc = btnAc;
	}

	public String getSmallIcon() {
		return smallIcon;
	}

	public void setSmallIcon(String smallIcon) {
		this.smallIcon = smallIcon;
	}

}
