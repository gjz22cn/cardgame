/**
 */
package com.lordcard.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @ClassName: GoodsType
 * @Description: 物品类型数据
 * @author shaohu
 * @date 2013-4-11 上午10:46:50
 * 
 */
public class GoodsDetails {

	@Expose
	@SerializedName("text")
	private String text; //

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the id
	 */

}
