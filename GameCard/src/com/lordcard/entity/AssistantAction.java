/**
*/
package com.lordcard.entity;

import java.util.List;

import com.google.gson.annotations.Expose;

/**
* @ClassName: AsstAction
* @Description: 助手动作
* @author shaohu
* @date 2013-7-30 下午03:30:50
* 
*/
public class AssistantAction {
	/**
	 * 按钮点击行为
	 */
	public final static String AC_ONCLICK = "oc";
	/**
	 * 赠送
	 */
	public final static String AC_GIVE = "gv";

	/**
	 * 购买
	 */
	public final static String AC_BUY = "by";

	/**
	 * 跳转
	 */
	public final static String AC_FORWARD = "fw";

	/**
	 * 报名
	 */
	public final static String AC_SIGN = "sg";

	/**
	 * 加入
	 */
	public final static String AC_JOIN = "ji";

	/**
	 * 退出
	 */
	public final static String AC_EXIT = "ex";
	/**
	 * 下载
	 */
	public final static String AC_DOWN = "dw";
	/**
	 * 跳转
	 */
	public final static String AC_SKIP = "aw";
	
	/**
	 * 充值赠送
	 */
	public final static String AC_PAY = "py";

	public static String getAcPay() {
		return AC_PAY;
	}

	/**
	 * ac值   oc:表示点击行为,gv:表示赠送,by:表示购买,fw:跳转网页,ex:退出,dw下载,aw跳转

	 */
	@Expose
	private String ac;
	@Expose
	private String remark;
	@Expose
	private List<GoodsGet> goodsHands;

	//请求url
	@Expose
	private String requestUrl;

	/**
	 * @return the ac
	 */
	public String getAc() {
		return ac;
	}

	/**
	 * @param ac the ac to set
	 */
	public void setAc(String ac) {
		this.ac = ac;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * @return the goodsHands
	 */
	public List<GoodsGet> getGoodsHands() {
		return goodsHands;
	}

	/**
	 * @param goodsHands the goodsHands to set
	 */
	public void setGoodsHands(List<GoodsGet> goodsHands) {
		this.goodsHands = goodsHands;
	}

	/**
	 * @return the requestUrl
	 */
	public String getRequestUrl() {
		return requestUrl;
	}

	/**
	 * @param requestUrl the requestUrl to set
	 */
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

}
