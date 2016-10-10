package com.lordcard.ui.interfaces;

import java.util.List;

import com.lordcard.entity.GameScoreTradeRank;

public interface ChangeProInterface {
	/**
	 * 改变加入比赛场等待人数
	 */
	void setPro(int n);

	/**
	 * 设置比赛排名
	 * @param gstList
	 */
	void setRank(List<GameScoreTradeRank> gstList);
}
