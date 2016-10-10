/**
*/
package com.lordcard.entity;

import java.util.List;

import com.google.gson.annotations.Expose;

/**
* @ClassName: AsstBtn
* @Description: 游戏助手按钮
* @author shaohu
* @date 2013-7-30 下午03:25:32
* 
*/
public class AssistantBtn {
	//按钮唯一编码
	@Expose
	private String code;
	//按钮显示文本
	@Expose
	private String btnText;
	//按钮动作
	@Expose
	private List<AssistantAction> actions;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the btnText
	 */
	public String getBtnText() {
		return btnText;
	}

	/**
	 * @param btnText the btnText to set
	 */
	public void setBtnText(String btnText) {
		this.btnText = btnText;
	}

	/**
	 * @return the actions
	 */
	public List<AssistantAction> getActions() {
		return actions;
	}

	/**
	 * @param actions the actions to set
	 */
	public void setActions(List<AssistantAction> actions) {
		this.actions = actions;
	}

}
