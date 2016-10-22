package com.lordcard.network.http;

import android.text.TextUtils;

import com.lordcard.constant.CacheKey;
import com.lordcard.entity.ChannelCfg;
import com.lordcard.network.base.ThreadPool;
import com.sdk.util.PayUtils;


public class GameHttpTask {
	
	/**
	 * 校验默认的初始配置是否有效
	 * @Title: checkInitCfg  
	 * @param 
	 * @return void
	 * @throws
	 */
	public static void checkInitCfg(){

		ThreadPool.startWork(new Runnable() {
			public void run() {
				//获取渠道相关配置
				String channelId = GameCache.getStr(CacheKey.CHANNEL_MM_ID);
				//对应的渠道配置ID存在
				if(!TextUtils.isEmpty(channelId)){
					ChannelCfg cfg = (ChannelCfg)GameCache.getObj(channelId);
					if(cfg == null){	//不存在渠道配置，需要去后台加载
						cfg = HttpRequest.loadChannelCfg(channelId);
						GameCache.putObj(channelId,cfg);
					}
				}
				/* james removed
				//初始化支付配置
				if (GameCache.getObj(CacheKey.PAY_INIT_MAP) == null) {
					PayUtils.loadPayInitParam(); //加载支付初始数据
				}
				if (GameCache.getObj(CacheKey.PAY_SITE_MAP) == null) {
					PayUtils.loadPaySiteConfig(); //加载计费点配置数据
				} */
				//获取所有的共用配置
				if (GameCache.getObj(CacheKey.ALL_SETTING_KEY) == null) {
					HttpRequest.getComSettingDate();
				}
			}
		});
	}
	
}
