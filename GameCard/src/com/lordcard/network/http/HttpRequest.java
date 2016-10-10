/**
 * HttpRequest.java [v 1.0.0]
 * classes : common.http.HttpRequest
 * auth : yinhongbiao
 * time : 2013 2013-2-21 下午3:35:12
 */
package com.lordcard.network.http;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.exception.ExceptionVO;
import com.lordcard.common.exception.NetException;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.ConfigUtil;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.EncodeUtils;
import com.lordcard.common.util.Encrypt;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.entity.ChannelCfg;
import com.lordcard.entity.GameIQ;
import com.lordcard.entity.GameTask;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.GameUserGoods;
import com.lordcard.entity.JsonParam;
import com.lordcard.entity.JsonResult;
import com.lordcard.entity.NoticesVo;
import com.lordcard.entity.PageQueryResult;
import com.lordcard.entity.Room;
import com.lordcard.network.base.ThreadPool;
import com.lordcard.network.cmdmgr.CmdDetail;
import com.lordcard.network.cmdmgr.CmdUtils;

/**
 * http请求 common.http.HttpRequest
 * 
 * @author yinhb <br/>
 *         create at 2013 2013-2-21 下午3:35:12
 */
public class HttpRequest {

	public static final String SUCCESS_STATE = "0"; // 成功状态
	public static final String SENSITIVE_WORDS = "-10";// 敏感字符
	public static final String FAIL_STATE = "1"; // 失败状态
	public static final String NO_LOGIN = "2"; // 未登录
	public static final String TOKEN_ILLEGAL = "3"; // 用户Token非法
	public static final String FAIL_PASSWD = "4"; // vip包房密码错误
	public static final String NO_FULL_BEAN = "5"; // 没有足够的金豆
	public static final String NO_HOME = "6"; // 房间不存在
	public static final String NO_SERVER = "7"; // 没有可用的游戏服务器
	public static final String NO_FULL_GOODS = "8"; // 没有足够的物品
	public static final String NULL_PRIZE = "10"; // 没有中奖
	public static final String LOGIN_TOKEN_ILLEGAL = "token_illegal"; // 用户Token非法
	public static final String REQUEST_ILLEGAL = "req_illegal"; // 非法的用户请求

	/**
	 * 登录游戏主页
	 */
	public static String login(String account, String userPwd) {
		String url = HttpURL.HTTP_PATH + "game/user/login.sc";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("account", account);
		paramMap.put("userPwd", userPwd);
		paramMap.put("gameType", String.valueOf(Database.GAME_TYPE));
		paramMap.put("pinfo", ActivityUtils.getPhoneInfo());
		return HttpUtils.post(url, paramMap);
	}

	/**
	 * 获取服务器IP
	 * @param getCache 是否直接获取缓存
	 * @return
	 */
	public static void getCacheServer(boolean getCache) {
		final Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("gameType", String.valueOf(Database.GAME_TYPE));
		final String url = HttpURL.HTTP_PATH + "/game/server/getServer.sc";
		if (getCache) {
			try {
				String cacheKey = HttpUtils.getCacheKey(url, paramMap);
				JsonResult jsonResult = JsonHelper.fromJson(GameCache.getStr(cacheKey), JsonResult.class);
				if (JsonResult.SUCCESS.equals(jsonResult.getMethodCode())) {
					String gameServer = jsonResult.getMethodMessage();
					Database.GAME_SERVER = gameServer;
				}
			} catch (Exception e) {
			}
		}
		
		if(TextUtils.isEmpty(Database.GAME_SERVER)){
			Database.GAME_SERVER = ConfigUtil.getCfg("fast.game.ip");
		}
		
		ThreadPool.startWork(new Runnable() {

			public void run() {
				try {
					String result = HttpUtils.post(url, paramMap, true);
					JsonResult jsonResult = JsonHelper.fromJson(result, JsonResult.class);
					if (JsonResult.SUCCESS.equals(jsonResult.getMethodCode())) {
						String gameServer = jsonResult.getMethodMessage();
						Database.GAME_SERVER = gameServer;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 登录游戏主页
	 */
	public static String thirdlogin(Map<String, String> paramMap) {
		/** 第三方SDK登录 */
		String url = HttpURL.HTTP_PATH + "game/user/loginThird.sc";
		paramMap.put("pinfo", ActivityUtils.getPhoneInfo());
		return HttpUtils.post(url, paramMap, false);
	}

	/**
	 * 注册账号
	 * 
	 * @return
	 */
	public static String register() {
		String url = HttpURL.HTTP_PATH + "game/user/register.sc";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("pinfo", ActivityUtils.getPhoneInfo());
		paramMap.put("gameType", String.valueOf(Database.GAME_TYPE));
		return HttpUtils.post(url, paramMap);
	}

	/**
	 * 获取用户信息
	 * 
	 * @param loginToken
	 * @return
	 */
	public static GameUser getGameUserDetail(String loginToken) {
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		try {
			String url = HttpURL.HTTP_PATH + "game/user/getUserByGameToken.sc";
			Map<String, String> param = new HashMap<String, String>();
			param.put("loginToken", loginToken);
			String result = HttpUtils.post(url, param, false);
			GameUser gameUser = JsonHelper.fromJson(result, GameUser.class);
			if (null == gameUser) {
				Log.e("debugs", "HttpRequest--获取用户信息---Database.USER  为空");
			}else{
				gameUser.setAuthKey(gu.getAuthKey());
				gu = gameUser;
				GameCache.putObj(CacheKey.GAME_USER,gu);
			}
		} catch (Exception e) {}
		return gu;
	}

	/**
	 * 登录具体游戏
	 * 
	 * @param loadRoom  是否加载房间信息
	 * @return 成功:返回大厅房间信息 失败：1
	 */
	public static String loginGame(boolean loadRoom) {
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		String url = HttpURL.HTTP_PATH + "game/user/loginGame.sc";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("loginToken",gu.getLoginToken());
		paramMap.put("gameType", String.valueOf(Database.GAME_TYPE));
		paramMap.put("loadRoom", String.valueOf(loadRoom));
		return HttpUtils.post(url, paramMap,false);
	}

	/**
	 * 修改用户的信息
	 */
	public static String updateCustomer(GameUser gameUser) {
		String resultJson = null;
		try {
			GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
			String url = HttpURL.HTTP_PATH + "game/user/updateCustomer.sc"; // 修改资料的url
			Map<String, String> param = new HashMap<String, String>();
			param.put("gameUserJson", JsonHelper.toJson(gameUser));
			param.put("loginToken",gu.getLoginToken());
//			param.put("signKey", Database.SIGN_KEY);
			param.put("signKey", gu.getAuthKey());
			resultJson = HttpUtils.post(url, param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultJson;
	}

	/**
	 * 房间信息是否有更新
	 * map.put("ut", updateTime); //更新时间 map.put("ty", updateRoomType); //更新房间类型
	 * 游戏中房间房间更新类型 all:更新所有房间,gen:普通房间,rk:复合赛房间,fast:快速赛房间[多个逗号分隔]
	 * 
	 * @return
	 */
	public static Map<String, String> roomHasChanged() {
		String resultJson = null;
		Map<String, String> map = null;
		try {
			String url = HttpURL.HTTP_PATH + "game/cnofig/getUdConfig.sc"; // 获取房间列表更新信息的url
			resultJson = HttpUtils.post(url, null);
			if (!TextUtils.isEmpty(resultJson.trim()) && !HttpRequest.FAIL_STATE.equals(resultJson.trim())) {
				map = JsonHelper.fromJson(resultJson.trim(), new TypeToken<Map<String, String>>() {});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 获取房间信息
	 * 
	 * @return
	 */
	public static List<Room> getRoomInfo(String roomType) {
		String resultJson = null;
		List<Room> roomList = null;
		try {
			String url = HttpURL.HTTP_PATH + "/game/room/list.sc"; // 获取房间信息
			Map<String, String> param = new HashMap<String, String>();
			param.put("roomType", roomType);
			param.put("gameType", String.valueOf(Database.GAME_TYPE));
			resultJson = HttpUtils.post(url, param);
			Log.i("hallResult", roomType + ":::::::::::: " + resultJson);
			if (!TextUtils.isEmpty(resultJson.trim()) && !HttpRequest.FAIL_STATE.equals(resultJson.trim())) {
				roomList = JsonHelper.fromJson(resultJson.trim(), new TypeToken<List<Room>>() {});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return roomList;
	}

	/**
	 * 获取玩家用户物品
	 * 
	 * @return
	 */
	public static GameUserGoods getGameUserGoods(boolean isCache) {
		GameUserGoods gameUserGoods = null;
		try {
			GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("loginToken", gu.getLoginToken());
			String url = HttpURL.HTTP_PATH + "game/user/getGameUserGoods.sc";
			String result = HttpUtils.post(url, paramMap, isCache);
			gameUserGoods = JsonHelper.fromJson(result, GameUserGoods.class);
			if (gameUserGoods != null && gu != null) {
				gu.setBean(gameUserGoods.getBean());
				GameCache.putObj(CacheKey.GAME_USER,gu);
			}
			return gameUserGoods;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gameUserGoods;
	}

	/**
	 * 修改用户密码
	 * 
	 * @param callback
	 */
	public static String updatePassWord(String account, String newPwd, String oldPwd) {
		String url = HttpURL.HTTP_PATH + "game/user/updateCustemerPwd.sc";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("oldPwd", EncodeUtils.MD5(oldPwd));
		paramMap.put("newPwd", EncodeUtils.MD5(newPwd));
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		paramMap.put("loginToken",gu.getLoginToken());
//		paramMap.put("signKey", Database.SIGN_KEY);
		paramMap.put("signKey", gu.getAuthKey());
		return HttpUtils.post(url, paramMap);
	}

	/**
	 * 密码找回
	 * 
	 * @param token
	 * @param callback
	 */
	public static String fetrievePwd(String account, String email) {
		String url = HttpURL.HTTP_PATH + "game/user/fetrievePwd.sc";
		Map<String, String> paramMap = new HashMap<String, String>();
		// paramMap.put("account", Database.USER.getAccount());
		paramMap.put("account", account);
		paramMap.put("email", email);
		return HttpUtils.post(url, paramMap);
	}

	/**
	 * 加入游戏前请求
	 * 
	 * @param homeCode
	 * @param passwd
	 * @return 满足加入条件，则返回服务器地址指令 失败返回 err_rjoin 指令金豆不足 homeDetail 指令 返回房间详情
	 */
	public static String rjoin(String homeCode, String passwd) {
		String url = HttpURL.HTTP_PATH + "game/join/rjoin.sc";
		JsonParam param = new JsonParam();
		param.setHomeCode(homeCode);
		param.setPasswd(passwd);
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		param.setLoginToken(gu.getLoginToken());
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("rjoin", JsonHelper.toJson(param));
		return HttpUtils.post(url, paramMap);
	}

	/**
	 * 创建房间
	 * 
	 * @param limitGroupNum
	 * @param ratio
	 * @return
	 */
	public static String createRoom(int limitGroupNum, int ratio, boolean isBeforeCheck) {
		Room room = new Room();
		room.setRatio(ratio);
		room.setLimitGroupNum(limitGroupNum);
		room.setHallCode(String.valueOf(Database.GAME_TYPE));
		if (Database.GAME_TYPE == Constant.GAME_TYPE_DIZHU) {
			room.setName("斗地主vip包房");
		}
		CmdDetail createDetail = new CmdDetail();
		createDetail.setCmd(CmdUtils.CMD_CREATE);
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		createDetail.setToken(gu.getLoginToken());
		createDetail.setDetail(JsonHelper.toJson(room));
		String uri = "game/room/createRoom.sc";
		Map<String, String> param = new HashMap<String, String>();
		param.put("postCmd", createDetail.toJson());
		param.put("isBeforeCheck", String.valueOf(isBeforeCheck));
		param.put("loginToken", gu.getLoginToken());
//		param.put("signKey", Database.SIGN_KEY);
		param.put("signKey", gu.getAuthKey());
		String result = HttpUtils.post(HttpURL.HTTP_PATH + uri, param);
		return result;
	}

	/**
	 * 邀请好友
	 * 
	 * @param friendList
	 *            朋友列表
	 */
	public static String inviteFriend(List<String> friendList) {
		String url = HttpURL.HTTP_PATH + "game/task/inviteFriend.sc";
		GameTask gameTask = new GameTask();
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		gameTask.setUserId(gu.getAccount());
		gameTask.setMcId(Constant.MCID);
		gameTask.setLoginToken(gu.getLoginToken());
		gameTask.setType(GameTask.TASK_TYPE[4]); // 邀请好友
		gameTask.setValue(JsonHelper.toJson(friendList));
		gameTask.setVersion(ActivityUtils.getVersionName());
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("ctosTask", JsonHelper.toJson(gameTask));
		return HttpUtils.post(url, paramMap);
	}

	/**
	 * 完善资料送金豆
	 */
	public static String completePersonalInfo() {
		String url = HttpURL.HTTP_PATH + "game/task/completePersonalInfo.sc";
		GameTask gameTask = new GameTask();
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		gameTask.setUserId(gu.getAccount());
		gameTask.setMcId(Constant.MCID);
		gameTask.setLoginToken(gu.getLoginToken());
		gameTask.setType(GameTask.TASK_TYPE[3]); // 完善资料
		gameTask.setValue(Constant.SUCCESS);
		gameTask.setVersion(ActivityUtils.getVersionName());
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("ctosTask", JsonHelper.toJson(gameTask));
		return HttpUtils.post(url, paramMap);
	}

	/**
	 * 提交邀请码
	 */
	public static String submitInviteCode(String code) {
		String url = HttpURL.HTTP_PATH + "game/task/submitInviteCode.sc";
		GameTask gameTask = new GameTask();
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		gameTask.setUserId(gu.getAccount());
		gameTask.setMcId(Constant.MCID);
		gameTask.setLoginToken(gu.getLoginToken());
		gameTask.setType(GameTask.TASK_TYPE[1]); // 邀请码
		gameTask.setValue(code);
		gameTask.setVersion(ActivityUtils.getVersionName());
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("ctosTask", JsonHelper.toJson(gameTask));
		return HttpUtils.post(url, paramMap);
	}

	/**
	 * 提交手机号码
	 */
	public static String submitPhone(int child, String value) {
		String url = HttpURL.HTTP_PATH + "game/task/submitPhone.sc";
		GameTask gameTask = new GameTask();
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		gameTask.setUserId(gu.getAccount());
		gameTask.setMcId(Constant.MCID);
		gameTask.setLoginToken(gu.getLoginToken());
		gameTask.setType(GameTask.TASK_TYPE[2]);
		gameTask.setChild(child);
		gameTask.setValue(value);
		gameTask.setVersion(ActivityUtils.getVersionName());
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("ctosTask", JsonHelper.toJson(gameTask));
		return HttpUtils.post(url, paramMap);
	}

	/**
	 * 同步通讯录
	 */
	public static String synchContack(List<String> phoneList) {
		String url = HttpURL.HTTP_PATH + "game/task/syncFriends.sc";
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		GameTask gameTask = new GameTask();
		gameTask.setUserId(gu.getAccount());
		gameTask.setMcId(Constant.MCID);
		gameTask.setLoginToken(gu.getLoginToken());
		gameTask.setType(GameTask.TASK_TYPE[7]); // 同步通讯录
		gameTask.setValue(JsonHelper.toJson(phoneList));
		gameTask.setVersion(ActivityUtils.getVersionName());
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("ctosTask", JsonHelper.toJson(gameTask));
		return HttpUtils.post(url, paramMap);
	}

	/**
	 * APK下载
	 */
	public static String downSoftTask(String softId) {
		String url = HttpURL.HTTP_PATH + "game/task/downloadSoft.sc";
		GameTask gameTask = new GameTask();
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		gameTask.setUserId(gu.getAccount());
		gameTask.setMcId(Constant.MCID);
		gameTask.setLoginToken(gu.getLoginToken());
		gameTask.setType(GameTask.TASK_TYPE[8]);
		gameTask.setValue(softId);
		gameTask.setVersion(ActivityUtils.getVersionName());
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("ctosTask", JsonHelper.toJson(gameTask));
		return HttpUtils.post(url, paramMap);
	}

	/**
	 * 获取消息中心推送公告
	 * 
	 * @return
	 */
	public static String getGameData() {
		String url = HttpURL.HTTP_PATH + "game/common/getGameNotices.sc";
		Map<String, String> paramMap = new HashMap<String, String>();
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		paramMap.put("loginToken", gu.getLoginToken());
		return HttpUtils.post(url, paramMap, true);
	}
	
	/**
	 * 加载游戏公告
	 * @Title: loadGameNotice  
	 * @param 
	 * @return void
	 * @throws
	 */
	public static void loadGameNotice(){
		String result = HttpUtils.post(HttpURL.GAME_NOTICE_URL, null, true);
		if (TextUtils.isEmpty(result)) {
			return;
		}
		NoticesVo notices = JsonHelper.fromJson(result, NoticesVo.class);
		GameCache.putObj(CacheKey.GAME_NOTICE, notices);
	}

	/**
	 * 获取游戏助理消息　
	 * 
	 * @return
	 */
	public static String getAsstContent() {
		String url = HttpURL.HTTP_PATH + "game/gmasst/getAsstContent.sc";
		Map<String, String> paramMap = new HashMap<String, String>();
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		paramMap.put("loginToken", gu.getLoginToken());
		return HttpUtils.post(url, paramMap);
	}

	/**
	 * 点击按钮处理消息　
	 * 
	 * @return
	 */
	public static String clickBtn(Map<String, String> paramMap) {
		String url = HttpURL.HTTP_PATH + "game/gmasst/getAsstAction.sc";
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		paramMap.put("loginToken", gu.getLoginToken());
//		paramMap.put("signKey", Database.SIGN_KEY);
		paramMap.put("signKey", gu.getAuthKey());
		return HttpUtils.post(url, paramMap);
	}

//	/**
//	 * 后台控制支付配置
//	 * 
//	 * @return
//	 */
//	public static void getConfig(String payType) {
//		Map<String, String> paramMap = null;
//		if (payType != null) {
//			paramMap = new HashMap<String, String>();
//			paramMap.put("payType", payType);
//		}
//		String url = HttpURL.HTTP_PATH + "game/cnofig/getConfig.sc";
//		String callBack = HttpUtils.post(url, paramMap, true);
//		Database.CONFIG_MAP = JsonHelper.fromJson(callBack, new TypeToken<Map<String, String>>() {});
//		if (Database.CONFIG_MAP != null) {
//			String socketTime = Database.CONFIG_MAP.get("socket_relink_time");
//			try {
//				if(!TextUtils.isEmpty(socketTime)){
//					int socketWait = Integer.parseInt(socketTime);
//					if (socketWait > 1000) {
//						SocketConfig.WAIT_TIME_OUT = socketWait;
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}

	/**
	 * 获取发短信后，获取金豆是否成功
	 * 
	 * @param money
	 * @return
	 */
	public static String getdou(Map<String, String> paramMap) {
		String url = HttpURL.HTTP_PATH + "/game/tele/sendSmsSuccess.sc";
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
//		paramMap.put("signKey", Database.SIGN_KEY);
		paramMap.put("signKey", gu.getAuthKey());
		return HttpUtils.post(url, paramMap);
	}

	/**
	 * 获取支付金额
	 * @param money
	 * @return
	 */
	public static String getPayMoney(String payUrl, Map<String, String> paramMap) {
		return HttpUtils.post(payUrl, paramMap, true);
	}

	/**
	 * 充值接口
	 * 
	 * @param money
	 * @return
	 */
	public static String addPayOrder(String payUrl, Map<String, String> paramMap) {
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		paramMap.put("loginToken", gu.getLoginToken()); //金豆
//		paramMap.put("signKey", Database.SIGN_KEY);
		paramMap.put("signKey", gu.getAuthKey());
		return HttpUtils.post(payUrl, paramMap);
	}

	/**
	 * 充值返回
	 * 
	 * @return
	 */
	public static String payCallBack(String callBackUrl, Map<String, String> paramMap) {
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		paramMap.put("loginToken", gu.getLoginToken()); //金豆
//		paramMap.put("signKey", Database.SIGN_KEY);
		paramMap.put("signKey", gu.getAuthKey());
		return HttpUtils.post(callBackUrl, paramMap);
	}

	/**
	 * 送豆子
	 * 
	 * @param presentType
	 *            用户类型 1 新注册用户游戏赠送 2 老用户每天游戏赠送
	 * @return
	 */
	public static long sentBean() {
		try {
			GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
			String presentType = "2";
			long createDate = Long.parseLong(gu.getCreateDate());
			long nowDate = new Date().getTime();
			if ((nowDate - createDate) < 86400000) { // 时间相差 大于一天的毫秒数 老用户
				presentType = "1";
			}
			String url = HttpURL.HTTP_PATH + "game/gtask/addAssetPresent.sc";
			Map<String, String> param = new HashMap<String, String>();
			param.put("loginToken", gu.getLoginToken());
			param.put("presentType", String.valueOf(presentType));
			String result = HttpUtils.post(url, param);
			JsonResult jsonResult = JsonHelper.fromJson(result, JsonResult.class);
			if (JsonResult.SUCCESS.equals(jsonResult.getMethodCode())) { // 成功
				long sendBean = Long.parseLong(jsonResult.getMethodMessage());
				Database.SEND_BEAN = sendBean;
				gu.setBean(gu.getBean() + sendBean);
				GameCache.putObj(CacheKey.GAME_USER,gu);
				return sendBean;
			}
		} catch (Exception e) {}
		return 0;
	}

	/**
	 * 游戏排行
	 * 
	 * @param tokenId
	 *            用户tokenID
	 * @return
	 */
	public static String gameSort() {
		String url = HttpURL.HTTP_PATH + "game/common/gameSort.sc";
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("loginToken", gu.getLoginToken());
		return HttpUtils.post(url, paramMap, true);
	}

	/**
	 * 添加签到信息
	 * 
	 * @return String 1 失败 ,0 成功
	 */
	public static String setSign() {
		String url = HttpURL.HTTP_PATH + "/game/sign/addSignInfo.sc";
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("loginToken", gu.getLoginToken());
		return HttpUtils.post(url, paramMap);
	}

	/**
	 * 检查用户是否签到和签到次数
	 * 
	 * @return String 1 失败 {"sign":0,"signCount":0} sign:0表示未签到 ,1:已经签到
	 *         signCount:已经签到次数
	 */
	public static String checkSign() {
		String url = HttpURL.HTTP_PATH + "/game/sign/getSignInfoByAccount.sc";
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("loginToken", gu.getLoginToken());
		return HttpUtils.post(url, paramMap);
	}

	/**
	 * 前台功能开关配置
	 * 
	 * @param type
	 *            首页商城开关 1 ,apk下载类型 2
	 * @param callback
	 */
	public static void openApiSwith(final String type, final HttpCallback callback) {
		new Thread() {

			public void run() {
				try {
					String result = getApiSwitch(type);
					callback.onSucceed(result);
				} catch (Exception e) {
					callback.onFailed();
				}
			}
		}.start();
	}

	public static String getApiSwitch(String type) {
		String result = "0";
		try {
			String url = HttpURL.HTTP_PATH + "game/common/apiSwitch.sc";
			result = HttpUtils.get(url + "?type=" + type);
		} catch (Exception e) {}
		return result;
	}

	/**
	 * 上传异常信息到服务器
	 */
	public static void uploadException(ExceptionVO exceptionVo) {
		try {
			String url = HttpURL.HTTP_PATH + "game/common/uploadException.sc";
			Map<String, String> param = new HashMap<String, String>();
			param.put("ei", JsonHelper.toJson(exceptionVo));
			HttpUtils.post(url, param);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 上传异常信息到服务器
	 */
	public static boolean uploadNetError(List<NetException> errList) {
		try {
			String url = HttpURL.HTTP_PATH + "game/common/uploadException.sc";
			Map<String, String> param = new HashMap<String, String>();
			// 只上传最后的10条命令，全部上传太多
			if (errList != null && errList.size() > 15) {
				errList = errList.subList(errList.size() - 15, errList.size());
			}
			param.put("ei", JsonHelper.toJson(errList));
			param.put("type", "net");
			HttpUtils.post(url, param);
			return true;
		} catch (Exception e) {}
		return false;
	}

	/**
	 * 获得广告的信息
	 */
	public static String getAppsInfo() {
		try {
			String result = HttpUtils.post(HttpURL.THIRD_APK_URL, null, true);
			return new String(result.getBytes("ISO-8859-1"), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取我的提交问题
	 * 
	 * @throws
	 */
	public static PageQueryResult getMyAsk(int pageNo) {
		try {
			String url = HttpURL.HTTP_PATH + "gm/help/getMyAsk.sc";
			Map<String, String> paramMap = new HashMap<String, String>();
			GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
			paramMap.put("loginToken", gu.getLoginToken());
			pageNo = (pageNo <= 0) ? 1 : pageNo;
			paramMap.put("pageNo", String.valueOf(pageNo));
			String result = HttpUtils.post(url, paramMap);
			JsonResult jsonResult = JsonHelper.fromJson(result, JsonResult.class);
			if (HttpRequest.SUCCESS_STATE.equals(jsonResult.getMethodCode())) {
				PageQueryResult queryResult = JsonHelper.fromJson(jsonResult.getMethodMessage(), PageQueryResult.class);
				return queryResult;
			} else {
				DialogUtils.mesTip(jsonResult.getMethodMessage(), false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取我的提交问题
	 * 
	 * @throws
	 */
	public static boolean submitMyAsk(String question) {
		try {
			String url = HttpURL.HTTP_PATH + "gm/help/submitAsk.sc";
			Map<String, String> paramMap = new HashMap<String, String>();
			GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
//			paramMap.put("authKey", Database.SIGN_KEY);
			paramMap.put("authKey", gu.getAuthKey());
			paramMap.put("question", question);
			paramMap.put("account", gu.getAccount());
			String result = HttpUtils.post(url, paramMap);
			JsonResult jsonResult = JsonHelper.fromJson(result, JsonResult.class);
			if (HttpRequest.SUCCESS_STATE.equals(jsonResult.getMethodCode())) {// 成功
				return true;
			} else {
				DialogUtils.mesTip(jsonResult.getMethodMessage(), false);
			}
		} catch (Exception e) {}
		return false;
	}

	/**
	 * 回调请求
	 */
	public static void postCallback(final String url, final Map<String, String> paramMap, final HttpCallback callback) {
		new Thread() {

			public void run() {
				try {
					String result = HttpUtils.post(url, paramMap);
					callback.onSucceed(result);
				} catch (Exception e) {
					callback.onFailed();
				}
			}
		}.start();
	}

	/**
	 * 游戏加入等待页面消息
	 */
	public static void loadJoinRoomTip() {
		if (Database.JOIN_NOTICE_LIST != null && Database.JOIN_NOTICE_LIST.size() > 0)
			return;
		ThreadPool.startWork(new Runnable() {

			public void run() {
				try {
					String url = HttpURL.HTTP_PATH + "game/common/getGameTipMes.sc";
					String result = HttpUtils.post(url, null, true);
					Database.JOIN_NOTICE_LIST = JsonHelper.fromJson(result, new TypeToken<List<NoticesVo>>() {});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 应用激活接口
	 * 
	 * @param batchId
	 *            批次号
	 */
	public static String activateFromFront(String batchId) {
		try {
			String mac = ActivityUtils.getNetWorkMac();
			String sysbId = Database.GAME_SYSBID;
			String activatePwd = Encrypt.encrypt(batchId, mac, sysbId);
			String url = HttpURL.UG_URL + "activateFromFront.do"; // 应用激活接口
			Map<String, String> param = new HashMap<String, String>();
			param.put("sysbId", sysbId); // 应用ID
			param.put("batchId", batchId); // 批次ID
			param.put("mac", mac); // MAC
			param.put("activatePwd", activatePwd); // 加密码
			param.put("mcId", "android");
			String resultJson = HttpUtils.post(url, param);
			JSONObject result = new JSONObject(resultJson);
			return result.getString("methodCode");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取激活参数
	 * 
	 * @return
	 */
	public static Map<String, String> getInterByProNames() {
		String url = HttpURL.UG_URL + "getInterByProNames.do"; // 应用激活条件
		Map<String, String> param = new HashMap<String, String>();
		param.put("proNames", "activate_effective_count,activate_effective_time"); // 应用ID
		String resultJson = HttpUtils.post(url, param);
		try {
			JSONObject result = new JSONObject(resultJson);
			String methodCode = result.getString("methodCode"); // 方法处理结果状态码
			if ("0".equals(methodCode)) { // 成功
				Map<String, String> resultMap = new HashMap<String, String>();
				JSONArray array = new JSONArray(result.getString("jsonArray"));
				for (int i = 0; i < array.length(); i++) {
					Map<String, String> map = JsonHelper.fromJson(array.getString(i), new TypeToken<Map<String, String>>() {});
					resultMap.putAll(map);
				}
				return resultMap;
			}
		} catch (Exception e) {}
		return null;
	}

	/**
	 * 获取资金池数据
	 * 
	 * @param roomCode
	 * @param hallCode
	 * @return
	 */
	public static String getPrizePool(String roomCode) {
		try {
			String url = HttpURL.HTTP_PATH + "game/prizerule/prizePool.sc";
			Map<String, String> param = new HashMap<String, String>();
			param.put("roomCode", roomCode);
			param.put("hallCode", String.valueOf(Database.GAME_TYPE));
			return HttpUtils.post(url, param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取比赛规则说明
	 * 
	 * @param roomCode
	 * @param hallCode
	 * @return
	 */
	public static String getGameRule(String roomCode) {
		try {
			String url = HttpURL.HTTP_PATH + "game/playtype/getruledesc.sc";
			Map<String, String> param = new HashMap<String, String>();
			param.put("roomCode", roomCode);
			param.put("hallCode", String.valueOf(Database.GAME_TYPE));
			return HttpUtils.post(url, param, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 报名比赛场
	 * 
	 * @param hallCode
	 * @param roomCode
	 * @param account
	 * @return
	 */
	public static String signUp(String code) {
		try {
			String url = HttpURL.HTTP_PATH + "/game/playtype/join.sc";
			Map<String, String> param = new HashMap<String, String>();
			GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
			param.put("account", gu.getAccount());
			param.put("code", code);
			param.put("hallCode", String.valueOf(Database.GAME_TYPE));
			param.put("payType", Database.PAYTYPE);
			return HttpUtils.post(url, param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 用户是否报名报名比赛("0"未报名，"1"已报名)
	 * 
	 * @param roomCode
	 * @param hallCode
	 * @return
	 */
	public static String isSignUp(String roomCode) {
		try {
			String url = HttpURL.HTTP_PATH + "/game/playtype/checkusersignup.sc";
			Map<String, String> param = new HashMap<String, String>();
			GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
			param.put("roomCode", roomCode);
			param.put("hallCode", String.valueOf(Database.GAME_TYPE));
			param.put("account", gu.getAccount());
			return HttpUtils.post(url, param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取排名(复合赛制)
	 * 
	 * @param playType
	 * @return
	 */
	public static String getFuheRank(String roomCode) {
		try {
			String url = HttpURL.HTTP_PATH + "/game/scoretrade/getlatestscoretraderank.sc";
			GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
			Map<String, String> param = new HashMap<String, String>();
			param.put("roomCode", roomCode);
			param.put("hallCode", String.valueOf(Database.GAME_TYPE));
			param.put("account", gu.getAccount());
			return HttpUtils.post(url, param, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 提交执行命令结果 game/command/postData.sc 参数： "code", code "data", data
	 */
	public static String postCommandResult(int code, Object data, String account) {
		String url = HttpURL.HTTP_PATH + "game/command/postData.sc";
		Map<String, String> rMap = new HashMap<String, String>();
		rMap.put("code", String.valueOf(code));
		rMap.put("account", String.valueOf(account));
		rMap.put("data", JsonHelper.toJson(data));
		Log.d("result", "code:" + rMap.get("code") + "data:" + rMap.get("data"));
		return HttpUtils.post(url, rMap);
	}

	/**
	 * 获取某场比赛的排名结果
	 * 
	 * @param playNo
	 * @return
	 */
	public static String getRank(String playNo) {
		String url = HttpURL.HTTP_PATH + "game/prizerecord/getPrizeRecord.sc";
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		Map<String, String> rMap = new HashMap<String, String>();
		rMap.put("playNo", playNo);
		rMap.put("loginToken", gu.getLoginToken());
		return HttpUtils.post(url, rMap, true);
	}
	/**
	 * 获取配置数据
	 * @param playNo
	 * @return
	 */
	public static void getComSettingDate() {
		try {
			HashMap<String,String> settingMap = new HashMap<String,String>();
			Object allKey = GameCache.getObj(CacheKey.ALL_SETTING_KEY);
			if(allKey == null){
				GameCache.putObj(CacheKey.ALL_SETTING_KEY,settingMap);
			}
		
			String url = HttpURL.HTTP_PATH + "game/common/getComConf.d";
			Object obj = GameCache.getObj(CacheKey.GAME_USER);
			if(obj == null) return;
			
			GameUser gu = (GameUser)obj;
			Map<String, String> rMap = new HashMap<String, String>();
			rMap.put("loginToken", gu.getLoginToken());
			String result = HttpUtils.post(url, rMap,true);
			if(!TextUtils.isEmpty(result)){
				result = new String(result.getBytes("ISO-8859-1"), Constant.CHAR);
				JsonResult jsonresult = JsonHelper.fromJson(result, JsonResult.class);
				if(JsonResult.SUCCESS.equals(jsonresult.getMethodCode())){
					String value = jsonresult.getMethodMessage();
					try {
						if (!TextUtils.isEmpty(value)) {
							TypeToken<HashMap<String,String>> typeToken = new TypeToken<HashMap<String,String>>() {};
							settingMap = JsonHelper.fromJson(value, typeToken);
							GameCache.putObj(CacheKey.ALL_SETTING_KEY,settingMap);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
		}
	}
	
	/**
	 * 获取各界面提示内容信息
	 * @return
	 */
	public static String getTextViewMessageDate() {
		String url = HttpURL.GAME_FILE_SER + "pic/res/string.json";
		return HttpUtils.post(url, null, true);
	}

	/**
	 * 获取IQ列表
	 * 
	 * @return
	 */
	public static List<GameIQ> getGameIq() {
		String url = HttpURL.HTTP_PATH + "game/gameiq/getAllTitle.sc";
		String result = HttpUtils.post(url, null, true);
		try {
			return JsonHelper.fromJson(result, new TypeToken<List<GameIQ>>() {});
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 *退赛
	 * （参数 ： code:房间编号, hallCode：房间大厅编号,account:账号，signKey:安全校验码）
	 * @param roomCode
	 * @return
	 */
	public static String returnGamePlace(String roomCode) {
		String url = HttpURL.HTTP_PATH + "game/playtype/quit.sc";
		GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		Map<String, String> rMap = new HashMap<String, String>();
		rMap.put("code", roomCode);
		rMap.put("hallCode", String.valueOf(Database.GAME_TYPE));
		rMap.put("account", gu.getAccount());
//		rMap.put("signKey", Database.GAME_USER_AUTH_KEY);
		rMap.put("signKey", gu.getAuthKey());
		return HttpUtils.post(url, rMap, true);
	}
	
	/**
	 * 载渠道对应的配置
	 * @Title: loadChannelCfg  
	 * @param @param channelId
	 * @param @return
	 * @return ChannelCfg
	 * @throws
	 */
	public static ChannelCfg loadChannelCfg(String channelId){
		try {
			String url = HttpURL.HTTP_PATH + "game/cnofig/loadChannelCfg.d";
			Map<String, String> rMap = new HashMap<String, String>();
			rMap.put("channelid",channelId);
			rMap.put("game", Constant.GAME);
			String result = HttpUtils.post(url, rMap,false);
			JsonResult jsonResult = JsonHelper.fromJson(result, JsonResult.class);
			if (jsonResult != null && JsonResult.SUCCESS.equals(jsonResult.getMethodCode())) { //正确的返回数据
				ChannelCfg cfg = JsonHelper.fromJson(jsonResult.getMethodMessage(),ChannelCfg.class);
				return cfg;
			}
		} catch (Exception e) {}
		
		return null;
	}
}
