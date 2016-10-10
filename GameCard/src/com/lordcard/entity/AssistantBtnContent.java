/**
*/
package com.lordcard.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.lordcard.common.util.JsonHelper;

/**
* @ClassName: AsstContentBtn
* @Description: 游戏助手内容按钮
* @author shaohu
* @date 2013-7-30 下午12:31:46
* 
*/
public class AssistantBtnContent {

	/**
	 * 横排
	 */
	public final static Integer ARR_HOR = 1;

	/**
	 * 竖排
	 */
	public final static Integer ARR_VER = 2;

	/**
	 * 随机按钮顺序
	 */
	public final static Integer IS_RAND = 1;

	/**
	 * 非随机按钮顺序
	 */
	public final static Integer NON_RAND = 0;

	@Expose
	private Integer display; //1:横排,2:竖排
	@Expose
	private Integer isRand; //按钮是否随机排序 1:随机排序,0:非随机排序
	@Expose
	private List<AssistantBtn> asstBtns; //助手按钮

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
	 * @return the isRand
	 */
	public Integer getIsRand() {
		return isRand;
	}

	/**
	 * @param isRand the isRand to set
	 */
	public void setIsRand(Integer isRand) {
		this.isRand = isRand;
	}

	/**
	 * @return the asstBtns
	 */
	public List<AssistantBtn> getAsstBtns() {
		return asstBtns;
	}

	/**
	 * @param asstBtns the asstBtns to set
	 */
	public void setAsstBtns(List<AssistantBtn> asstBtns) {
		this.asstBtns = asstBtns;
	}

	public static void main(String[] args) {
		AssistantBtnContent asstContent = new AssistantBtnContent();
		asstContent.setDisplay(AssistantBtnContent.ARR_VER);
		asstContent.setIsRand(AssistantBtnContent.IS_RAND);

		List<AssistantBtn> asstBtns = new ArrayList<AssistantBtn>();
		AssistantBtn asstBtn1 = new AssistantBtn();
		asstBtn1.setCode("1");
		asstBtn1.setBtnText("按钮测试1");
		//行为
		List<AssistantAction> actions1 = new ArrayList<AssistantAction>();
		AssistantAction action1 = new AssistantAction();
		action1.setAc(AssistantAction.AC_ONCLICK);
		action1.setRemark("点击按钮统计");

		actions1.add(action1);

		AssistantAction action2 = new AssistantAction();
		action2.setAc(AssistantAction.AC_GIVE);
		action2.setRemark("按钮测试赠送");

		List<GoodsGet> goodsHands1 = new ArrayList<GoodsGet>();
		GoodsGet goodsHand1 = new GoodsGet();
		goodsHand1.setGoodCode("2");//金豆
		goodsHand1.setName("金豆");
		goodsHand1.setCount(5000);
		goodsHands1.add(goodsHand1);

		GoodsGet goodsHand2 = new GoodsGet();
		goodsHand2.setGoodCode("3");//钻石
		goodsHand2.setName("钻石");
		goodsHand2.setCount(5);
		goodsHands1.add(goodsHand2);

		action2.setGoodsHands(goodsHands1);
		actions1.add(action2);
		asstBtn1.setActions(actions1);
		asstBtns.add(asstBtn1);
		asstContent.setAsstBtns(asstBtns);

		System.out.println(JsonHelper.toJson(asstContent));
	}
}
