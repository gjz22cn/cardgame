package com.sdk.util;

import com.lordcard.entity.Room;


public class RechargeUtils {
	
	/**
	 * 计算当前房间需默认需要充值的金额
	 * @Title: calculateMoney  
	 * @param @param room
	 * @param @return
	 * @return long
	 * @throws
	 */
	public static double calRoomJoinMoney(Room room){
		double limitBean = room.getLimit();
		double money = limitBean/10000;
		return money;
	}
	
}
