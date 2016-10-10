/**  
 
 * @Title: PageResult.java 
 
 * @Package com.game.common.page 
 
 * @Description: TODO 
 
 * @author yinhongbiao  
 
 * @date 2013-5-17 下午03:25:06 
 
 * @version V1.0  
 
 */

package com.lordcard.entity;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @ClassName: PageResult
 * @Description: TODO
 * @author yinhongbiao
 * @date 2013-5-17 下午03:25:06
 */
public class PageQueryResult {
	/** 当前页中存放的数据 */
	@Expose
	@SerializedName("list")
	private List<GameUserAsk> dataList;
	/** 每页的记录数 */
	@Expose
	@SerializedName("ps")
	private int pageSize;
	/** 跳转页数 */
	@Expose
	@SerializedName("no")
	private int pageNo;
	/** 总页数 */
	@Expose
	@SerializedName("pc")
	private int pageCount;

	public List<GameUserAsk> getDataList() {
		return dataList;
	}

	public void setDataList(List<GameUserAsk> dataList) {
		this.dataList = dataList;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

}
