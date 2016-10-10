/**
 * HttpUtils.java [v 1.0.0]
 * classes : com.game.module.common.util.HttpUtils
 * auth : yinhongbiao
 * time : 2013 2013-1-25 下午03:50:20
 */
package com.lordcard.network.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.text.TextUtils;

import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.ChannelUtils;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.StringUtils;
import com.lordcard.constant.Database;
import com.lordcard.entity.JsonResult;
import com.lordcard.network.base.ThreadPool;

/**
 * com.game.module.common.util.HttpUtils
 * 
 * @author yinhb <br/>
 *         create at 2013 2013-1-25 下午03:50:20
 */
public class HttpUtils {

	/**
	 * 提交业务指令
	 * @param uri 请求的相对地址
	 * @param postCmd CmdDetail 的json数据
	 * @return
	 */
	public static String postCmd(String uri, String postCmd) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("postCmd", postCmd);
		String resultJson = post(HttpURL.HTTP_PATH + uri, param);
		return resultJson;
	}

	/**
	 * post请求 默认结果不缓存
	 * @param url post url
	 * @param paramMap post参数
	 * @return
	 */
	public static String post(String url, Map<String, String> paramMap) {
		return post(url, paramMap, false);
	}

	/**
	 * 获取请求缓存的键值
	 * @param url
	 * @param paramMap
	 * @return
	 */
	public static String getCacheKey(String url, Map<String, String> paramMap) {
		// 获取Cache的缓存键
		StringBuffer cacheKey = new StringBuffer(url);
		if (paramMap != null) {
			for (String key : paramMap.keySet()) {
				cacheKey.append("_").append(paramMap.get(key));
			}
		}
		return cacheKey.toString();
	}

	/**
	 * post请求
	 * @param url		post url
	 * @param paramMap  post参数
	 * @param isCache   结果是否缓存
	 *  (缓存后下次请求将直接取缓存数据,继续的请求获取的数据用来更新缓存)
	 *   true:缓存 false:不缓存
	 * @return
	 */
	public static String post(final String url, final Map<String, String> paramMap, boolean isCache) {
		String result = null;
		final String cacheKey = getCacheKey(url, paramMap);
		//如果为空 则是url 和param都是空, 无效的请求
		if (TextUtils.isEmpty(cacheKey)) {
			return null;
		}
		if (isCache) { //是否获取缓存数据
			result = GameCache.getStr(cacheKey);
		}
		boolean isNowRequest = false; //是否需要立即请求数据
		//缓存数据不存在
		if (result == null || result.length() == 0) {
			isNowRequest = true;
		}
		if (isNowRequest) { //没有缓存数据  即时获取请求数据
			result = doPost(url, paramMap);
			if (result != null && !"null".equals(result) && result.length() > 0) { //缓存请求的结果数据
				boolean isTrueResult = true;
				try {
					JsonResult jsonResult = JsonHelper.fromJson(result, JsonResult.class);
					if (!JsonResult.SUCCESS.equals(jsonResult.getMethodCode())) { //正确的返回数据 可以缓存
						isTrueResult = false;
					}
				} catch (Exception e) {}
				if (isTrueResult) {
					if (isCache) {
						GameCache.putStr(cacheKey, result);
					} else {
						//当前请求存在相应的缓存值,则更新,没有不增加
						String tempCache = GameCache.getStr(cacheKey);
						if (tempCache != null && tempCache.length() > 0) {
							GameCache.putStr(cacheKey, result);
						}
					}
				}
			}
		} else {
			//已有缓存数据 开启异步请求同步缓存
			ThreadPool.startWork(new Runnable() {

				public void run() {
					try {
						//将请求的结果同步到缓存
						String synResult = doPost(url, paramMap);
						if (synResult != null && !"null".equals(synResult) && synResult.length() > 0) { //缓存请求的结果数据
							boolean isTrueResult = true;
							try {
								JsonResult jsonResult = JsonHelper.fromJson(synResult, JsonResult.class);
								if (!JsonResult.SUCCESS.equals(jsonResult.getMethodCode())) { //正确的返回数据 可以缓存
									isTrueResult = false;
								}
							} catch (Exception e) {}
							if (isTrueResult) {
								GameCache.putStr(cacheKey, synResult);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		return result;
	}

	private static String doPost(String url, Map<String, String> paramMap) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpost = new HttpPost(url);
		try {
			//httpost.setHeader("Connection", "Keep-Alive");
			httpost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			// post参数
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			if (paramMap == null) {
				paramMap = new HashMap<String, String>();
			}
			paramMap.put("channel",ChannelUtils.getSerCfgName()); // 渠道号
			paramMap.put("version", ActivityUtils.getVersionName()); // 版本号
			paramMap.put("batchId", ChannelUtils.getBatchId()); //批次号
			paramMap.put("macIp", ActivityUtils.getAndroidId()); //手机唯一ID
			paramMap.put("rt", String.valueOf(System.currentTimeMillis())); //时间撮
			for (String key : paramMap.keySet()) {
				nvps.add(new BasicNameValuePair(key, paramMap.get(key)));
			}
			// 请求编码
			httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			HttpResponse response = httpclient.execute(httpost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String result = EntityUtils.toString(entity, "UTF-8");
				result = StringUtils.uncompress(result);
				return doWithResult(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return null;
	}

	/**
	 * 返回的结果处理 
	 * @param result
	 * @return
	 */
	private static String doWithResult(String result) {
		if (result.endsWith(";")) {
			result = result.substring(0, result.length() - 1);
		}
		if (HttpRequest.LOGIN_TOKEN_ILLEGAL.equals(result)) {
			Database.currentActivity.runOnUiThread(new Runnable() {

				public void run() {
					DialogUtils.reLogin(Database.currentActivity);
				}
			});
			return null;
		} else if (HttpRequest.REQUEST_ILLEGAL.equals(result)) {
			Database.currentActivity.runOnUiThread(new Runnable() {

				public void run() {
					DialogUtils.mesTip("非法的请求", false);
				}
			});
			return null;
		}
		return result;
	}

	public static String get(String url) {
		if (url.indexOf("?") > 0) {
			url += "&";
		} else {
			url += "?";
		}
		url += "rt=" + (new Random().nextInt());
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		try {
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String result = EntityUtils.toString(entity, "UTF-8");
				result = StringUtils.uncompress(result);
				if (result.endsWith(";")) {
					result = result.substring(0, result.length() - 1);
				}
				return result;
			}
		} catch (Exception e) {
			httpget.abort();
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return null;
	}
}
