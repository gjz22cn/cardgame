package com.sdk.util;

import java.util.HashMap;

import com.lordcard.constant.CacheKey;
import com.lordcard.network.http.GameCache;
import com.sdk.util.vo.PaySiteConfig;

/**
 * 计费位置配置数据
 * @ClassName: PaySite   
 * @Description: TODO 
 * @author yinhongbiao   
 * @date 2014-2-19 下午2:46:08
 */
public class PaySite {
	
	public static final String OFF_LINE = "offline"; 		//单机账号
	public static final String ON_LINE = "online"; 		//连网账号

	public static final String SIGN_IN = "signin"; //登录后签到页面
	public static final String ROOM_ITEM_CLICK = "roomitemclick";//	房间列表点击金豆不足充值位置
	public static final String SINGLE_GAME_CLICK = "singlegameclick";//	单机游戏房间内充值
	public static final String PLAYING_CLICK = "playingclick";//	房间里面充值点击
	public static final String VIP_CREATE = "vipcreate";//	创建vip包房金豆不足时充值
	public static final String GAME_HELP_CLICK = "gamehelpclick";//	游戏助手提示的充值
	public static final String PREPARERECHARGE = "preparerecharge";//	预充值弹出时充值
	public static final String RECORED_CARD = "recoredcard";//	记牌器使用时充值
	public static final String GAME_END_CLICK = "gameendclick";//	结束界面充值按扭
	public static final String GAME_END_AUTO = "gameendauto";//	结束界面自动弹出充值
	public static final String RECHARGE_LIST_FAST = "rechargelistfast";//	充值页面快速充值
	public static final String RECHARGE_LIST = "rechargelist";//	充值页面充值
	public static final String GAME_SIGN_UP = "gamesignup";//	比赛报名
	public static final String ROOM_RECEIVE_BEEN = "roomreceivebeen";//大厅领取金豆
	
	
	/**
	 * 根据计费位置获取位置的配置
	 * @Title: getSiteMap  
	 * @param @param site
	 * @param @return
	 * @return PaySiteConfig
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public static PaySiteConfig getSiteMap(String site){
		HashMap<String, PaySiteConfig> map = (HashMap<String, PaySiteConfig>)GameCache.getObj(CacheKey.PAY_SITE_MAP);
		if(map != null){
			return map.get(site);
		}
		return null;
	}	
}
