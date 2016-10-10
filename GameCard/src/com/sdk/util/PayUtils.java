package com.sdk.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.ConfigUtil;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.SortUtils;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Constant;
import com.lordcard.entity.JsonResult;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpURL;
import com.lordcard.network.http.HttpUtils;
import com.sdk.util.vo.PayInit;
import com.sdk.util.vo.PayPoint;
import com.sdk.util.vo.PaySiteConfig;
import com.sdk.util.vo.PaySiteConfigItem;

/**
 * 支付相关公共操作
 * @ClassName: PayUtils   
 * @Description: TODO 
 * @author yinhongbiao   
 * @date 2014-2-19 下午2:03:13
 */
public class PayUtils {

	/**
	 * 加载支付初始数据
	 * @Title: loadPayInitParam  
	 * @param 
	 * @return void
	 * @throws
	 */
	public static void loadPayInitParam() {
		try {
			String simType = ActivityUtils.getSimType();
			simType = "mobile";//xs_test所有计费提示都按照移动
			GameCache.putStr(Constant.SIM_KEY, simType); //保存sim类型
			String payCodes = ConfigUtil.getCfg("pay_codes");
			String url = HttpURL.HTTP_PATH + "game/pay/payInit.sc";
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("payCodes", payCodes);
			String result = HttpUtils.post(url, paramMap, true);
			JsonResult jsonResult = JsonHelper.fromJson(result, JsonResult.class);
			if (JsonResult.SUCCESS.equals(jsonResult.getMethodCode())) { //正确的返回数据
				String msg = jsonResult.getMethodMessage();
				//支付方式对应的初始配置文件
				TypeToken<Map<String, String>> typeToken = new TypeToken<Map<String, String>>() {};
				//key:支付方式的payCode  value:支付初始数据 init
				Map<String, String> payInitMap = JsonHelper.fromJson(msg, typeToken);
				//转成对象缓存
				HashMap<String, PayInit> initMap = new HashMap<String, PayInit>();
				if (payInitMap != null && !payInitMap.isEmpty()) {
					for (String key : payInitMap.keySet()) {
						try {
							PayInit init =  JsonHelper.fromJson(payInitMap.get(key), PayInit.class);
							initMap.put(key,init);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
					}
				}
				GameCache.putObj(CacheKey.PAY_INIT_MAP, initMap);
			}
		} catch (Exception e) {}
	}

	/**
	 * 加载计费点配置数据
	 * @Title: loadPaySiteConfig  
	 * @param 
	 * @return void
	 * @throws
	 */
	public static void loadPaySiteConfig() {
		try {
			String url = HttpURL.HTTP_PATH + "game/pay/paySite.sc";
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("game", Constant.GAME);
			paramMap.put("version", ActivityUtils.getVersionName());
			String result = HttpUtils.post(url, paramMap, true);
			JsonResult jsonResult = JsonHelper.fromJson(result, JsonResult.class);
			if (JsonResult.SUCCESS.equals(jsonResult.getMethodCode())) { //正确的返回数据
				String msg = jsonResult.getMethodMessage();
				//计费位置支付配置数据
				TypeToken<ArrayList<PaySiteConfig>> typeToken = new TypeToken<ArrayList<PaySiteConfig>>() {};
				ArrayList<PaySiteConfig> siteList = JsonHelper.fromJson(msg, typeToken);
				if (siteList != null) {
					TypeToken<HashMap<String, ArrayList<PaySiteConfigItem>>> siteToken = new TypeToken<HashMap<String, ArrayList<PaySiteConfigItem>>>() {};
					HashMap<String, PaySiteConfig> sitemap = new HashMap<String, PaySiteConfig>();
					for (PaySiteConfig paySiteConfig : siteList) {
						//将配置数据转成对象
						String payConf = paySiteConfig.getPayconf();
						HashMap<String, ArrayList<PaySiteConfigItem>> siteItemMap = JsonHelper.fromJson(payConf, siteToken);
						paySiteConfig.setSiteItemMap(siteItemMap);
						sitemap.put(paySiteConfig.getSite(), paySiteConfig);
					}
					GameCache.putObj(CacheKey.PAY_SITE_MAP, sitemap);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 获取计费位置当前有效的支付配置
	 * @Title: getPaySiteUseConfig  
	 * @param @param paySite
	 * @param @return
	 * @return PaySiteConfigItem
	 * @throws
	 */
	public static PaySiteConfigItem getPaySiteUseConfig(String paySite){
		PaySiteConfig siteConfig = PaySite.getSiteMap(paySite);
		if(siteConfig == null) return null;
		
		String simType = GameCache.getStr(Constant.SIM_KEY);
		HashMap<String, ArrayList<PaySiteConfigItem>> siteMap = siteConfig.getSiteItemMap();
		if(siteMap == null) return null;
		
		//指定类型sim卡的配置
		ArrayList<PaySiteConfigItem> siteItemList = siteMap.get(simType);	
		if(siteItemList == null) return null;
			
		//找到当前使用的配置 node为0是当前使用的，其他为备用
		PaySiteConfigItem siteConfigItem = null;		
		for (PaySiteConfigItem item : siteItemList) {
			if(item.getNode() == 0){
				siteConfigItem = item;
				break;
			}
		}
		return siteConfigItem;
	}
	
	/**
	 * 获取计费点
	 * @Title: getPayPoint  
	 * @param @return
	 * @return List<PayPoint>
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public static List<PayPoint> getPayPoint(String paySite){
		List<PayPoint> pointList = new ArrayList<PayPoint>();
		//找到当前位置使用的计费配置
		PaySiteConfigItem siteConfigItem = getPaySiteUseConfig(paySite);
		if(siteConfigItem == null) return null;
		
		String payCode = siteConfigItem.getPayCode();
		HashMap<String,PayInit> initMap = (HashMap<String,PayInit>)GameCache.getObj(CacheKey.PAY_INIT_MAP);
		PayInit payInit = initMap.get(payCode);	//支付方式初始数据
		if(payInit == null) return null;
		
		if(payInit.getPointList() != null){
			pointList = payInit.getPointList();
		}
		return pointList;
	}
	
	/**
	 * 匹配充值计费点
	 * @Title: matchPayPoint  
	 * @param @param pno
	 * @param @param money
	 * @param @param pointList
	 * @param @return
	 * @return PayPoint
	 * @throws
	 */
	public static PayPoint matchPayPoint(String pno, double money, List<PayPoint> pointList) {
		//计费点根据充值的金额升序排
		SortUtils<PayPoint> compator = new SortUtils<PayPoint>();
		compator.sort(pointList,"money",SortUtils.Sort.ASC);
		
		PayPoint point = null;
		//如果自动计算充值金额，则根据money找到最合试的充值金额
		if (PayPoint.AUTO.equals(pno)) {
			for (PayPoint payPoint : pointList) {
				if (payPoint.getMoney() >= money) { //大于等于当前金额的第一个计费点
					point = payPoint;
					break;
				}
			}
			//没有匹配的计费点，则默认最大的那个计费点
			if(point == null){
				point = pointList.get(pointList.size()-1);
			}
		} else { //指定了具体的充值计费点编号
			for (PayPoint payPoint : pointList) {
				if (payPoint.getNo().equals(pno)) { //找到指定的计费点
					point = payPoint;
					break;
				}
			}
		}
		return point;
	}
	
	/**
	 * 找到计费位置对应的计费点，没有则为空
	 * @Title: getPaySitePoint  
	 * @param @param paySite
	 * @param @return
	 * @return PayPoint
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public static PayPoint getPaySitePoint(String paySite){
		//找到当前位置使用的计费配置
		PaySiteConfigItem siteConfigItem = getPaySiteUseConfig(paySite);
		if(siteConfigItem == null) return null;
		
		//没有配置支付
		String pno = siteConfigItem.getPno();
		if(TextUtils.isEmpty(pno) || pno.equals(PayPoint.AUTO)){
			return null;
		}
		
		String payCode = siteConfigItem.getPayCode();
		
		HashMap<String,PayInit> initMap = (HashMap<String,PayInit>)GameCache.getObj(CacheKey.PAY_INIT_MAP);
		PayInit payInit = initMap.get(payCode);	//支付方式初始数据
		if(payInit == null) return null;

		List<PayPoint> pointList = payInit.getPointList();
		if(pointList == null) return null;
		
		for (PayPoint payPoint : pointList) {
			if(pno.equals(payPoint.getNo())){
				return payPoint;
			}
		}
		return null;
	}
	
}
