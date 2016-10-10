/**
 * HttpURL.java [v 1.0.0]
 * classes : common.http.HttpURL
 * auth : yinhongbiao
 * time : 2013 2013-2-5 下午5:09:54
 */
package com.lordcard.network.http;

import com.lordcard.common.util.ChannelUtils;

/**
 * common.http.HttpURL
 * 
 * @author yinhb <br/>
 *         create at 2013 2013-2-5 下午5:09:54
 */
public class HttpURL {

	// ///////////////////////////////////第三方接口参数
	public static final String CHANNEL_NAME = ChannelUtils.getSerDir(); //渠道名称
	/**游戏登录服务器 */
	public static final String HTTP_PATH = "http://121.42.54.146:8080/gameweb/";
//	public static final String HTTP_PATH = "http://121.41.104.75:8080/gameweb/";
//	public static final String HTTP_PATH = "http://192.168.2.110:8080/gameweb/";
	

	/**游戏文件服务器地址： 更新包，资源文件下载等 */
//	public static final String GAME_FILE_SER = "http://file.qianqian360.com/";
	public static final String GAME_FILE_SER = "http://121.42.54.146:8080/";
	public static final String UG_URL = "http://www.ug123.com.cn:81/tilm/";
	/** 游戏APK配置信息 */
	public static final String CONFIG_SER = GAME_FILE_SER + "pic/v/" + CHANNEL_NAME + "/dz/"; // 地主游戏apk配置地址
	public static final String APK_INFO = "v.json"; // 游戏apk版本配置信息
	public static final String APK_NAME = "dz.apk"; // 游戏apk升级包
	/** 图片资源路径 **/
	public final static String URL_PIC_ALL = GAME_FILE_SER + "pic/";
	/** 支付宝图片资源路径 **/
	public static String PAY_INFO_URL = URL_PIC_ALL + "img/pay/";
	/** 复合比赛报名状态路径 **/
	public static String FH_ROOM_SIGNUP = HTTP_PATH + "game/playtype/checkusersignup.sc";
	/** 游戏兑奖区 **/
	public final static String URL_AD_PRIZE = HttpURL.UG_URL + "advertisingByArea.do?advertisingArea=4&mcId=android&version=1";
	/** 第三方推广APK下载地址 */
	public static final String THIRD_APK_URL = GAME_FILE_SER + "download/apk/downsoft.json";
	/** 表情资源包下载路径 */
	public static final String CHAT_RES_HTTP = GAME_FILE_SER + "download/gres/chat.zip";
	/** 消息推送 */
	public static final String NOTICE_PUSH_URL = HTTP_PATH + "/game/common/getServiceNotice.sc";
	/** 获取消息通知 */
	public static final String GAME_NOTICE_URL = HTTP_PATH + "game/common/getGameNotice.sc";
	/** 赠送抽奖券 */
	public static String COUPON_GIVE_URL = HTTP_PATH + "game/gtask/giveBoxGoods.sc";// 生肖开宝箱
	/**金豆购买抽奖券 */
	public static String COUPON_BUY_URL = HTTP_PATH + "game/gtask/buyCoupon.sc";
	/** 抽奖券抽奖 */
	public static String COUPON_LUCKY_URL = HTTP_PATH + "/game/gtask/luckyMod.sc";// 生肖版抽奖
	public static String COUPON_ZHIZUAN_URL = HTTP_PATH + "/game/gtask/luckyDrawSpec.sc";
	// 点击头像获取个人信息
	public static String COUPON_INFO_URL = HTTP_PATH + "/game/gmback/getPartUserGoods.sc";
	/** 物品宝鉴所有图片和所有物品的详情 */
	public static String EVALUES_IMG_URL = HTTP_PATH + "game/gmback/goodsType.sc";
	/** 物品栏所有物品信息和默认第一个物品详细信息 */
	public static String GOODS_BAG_URL = HTTP_PATH + "game/gmback/getUserGoods.sc";
	/** 物品栏点击对应物品获取后台返回数据，proposMap.get("oper").equals("5") */
	public static String GOODS_BAG_CALLBACK = HTTP_PATH + "/game/gmback/goodsUse.sc";
	/** 物品栏对应物品信息 */
	public static String GOODS_BAG_MSG = HTTP_PATH + "game/gmback/getGoodsTypeByType.sc";
	/** 游戏指南主页面 */
	public static String GAME_GUIDE_URL = HTTP_PATH + "game/gmback/getGameContentTitle.sc";
	/** 游戏指南详细内容 */
	public static String GUIDE_DETAIL_URL = HTTP_PATH + "game/gmback/getGameContentDetail.sc";
	/** 数码产品详情 */
	public static String STOVE_DIGIDETAIL = HTTP_PATH + "/game/gmback/getGoodsTypeDetail.sc";
	/** 游戏金豆合成 */
	public static String STOVE_BEAN_URL = HTTP_PATH + "game/gmback/exchange.sc";
	/** 游戏合成数据校验 */
	public static String STOVE_CHECK_URL = HTTP_PATH + "game/gmback/goodsCompositePart.sc";
	/** 游戏合成房间 */
	public static String STOVE_ROOM_CHECK = HTTP_PATH + "game/joinuser/getSpecTime.sc";
	/** 提交手机号充值 */
	public static String USER_PHONE_MESS = HTTP_PATH + "game/phone/addGamePhoneBill.sc";
	/** 获取个人信息获取数码物品 */
	public static String USER_MESS_GET = HTTP_PATH + "game/userAddress/getUserAddress.sc";
	/** 提交个人信息获取数码物品 */
	public static String USER_MESS_ADRESS = HTTP_PATH + "game/userAddress/addGameUserAddress.sc";
	/** 游戏合成房间 报名 */
	public static String STOVE_ROOM_SIGN = HTTP_PATH + "game/joinuser/join.sc";
	/** 游戏合成结果 */
	public static String STOVE_RESULT_URL = HTTP_PATH + "game/gmback/goodscomposite.sc";
	/** 后台订单纪录　 */
	public static String PAY_LOG_URL = HTTP_PATH + "game/payorder/getPayOrderData.sc";
	/** 后台控制mm联通电信短信支付　 */
	public static String PAY_CHANGE_URL = HTTP_PATH + "game/tele/getBillPoint.sc";
	/** 获取包裹道具（美女图鉴）信息 */
	public static String DAOJU_DETAIL_URL = HTTP_PATH + "/game/props/useprops.sc";
	/** 获取游戏界面道具（美女图鉴）信息 */
	public static String MAIN_DAOJU_URL = HTTP_PATH + "/game/props/getallprops.sc";
	/** 预充值倍数信息**/
	public static String PRE_RECHARGE_MULTIPLE_URL = HttpURL.HTTP_PATH + "game/cnofig/preMultiple.sc";
}
