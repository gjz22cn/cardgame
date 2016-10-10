package com.lordcard.entity;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 重连数据
 * @ClassName: ReLink   
 * @Description: TODO 
 * @author yinhongbiao   
 * @date 2013-6-26 下午07:46:01
 */
public class ReLink {
	
	public static final int TARGET_MYSELF = 0;
	public static final int TARGET_OTHER = 1;
	
	@Expose @SerializedName("t")  private int target;						//重连玩家 0:自己 1:其他玩家
	@Expose @SerializedName("o")  private int order;						//重连玩家的位置编号
	
	@Expose @SerializedName("rm") private Room room;							//房间
	@Expose @SerializedName("cr") private int callRatio;	 				//玩家叫的倍数
	@Expose @SerializedName("r")  private int ratio;						//游戏当前倍数
	@Expose @SerializedName("gs")  private String gameServer;				//玩家上局所在的游戏服务器
	
	@Expose @SerializedName("st") private int status;						//当前状态
	@Expose @SerializedName("mo") private int masterOrder=0;				    //庄家位置
	@Expose @SerializedName("no") private int nextPlayOrder;				//操作人的order
	
	@Expose @SerializedName("mc") private String masterCard;				//地主底牌
	
	@Expose @SerializedName("do") private int isMyDo; 						//是否轮到我操作,0:不是本人操作，1是本人操作
	@Expose @SerializedName("mcl") private List<Integer> myCardList;			//我自己的剩余的牌

	@Expose @SerializedName("pcl") private List<String> playCardList;					//玩家出牌的记录，最多取2条返回
	@Expose @SerializedName("u") private Map<Integer,ReLinkUser> userInfo;	//其他玩家剩余的牌  key:玩家顺序 value:玩家信息
	@Expose @SerializedName("acm") private Map<String, List<Integer>> allPlayCardMap;	//所有玩家出牌记录，key:玩家帐号,value:牌

	public ReLink() {
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getNextPlayOrder() {
		return nextPlayOrder;
	}

	public void setNextPlayOrder(int nextPlayOrder) {
		this.nextPlayOrder = nextPlayOrder;
	}

	public Map<String, List<Integer>> getAllPlayCardMap() {
		return allPlayCardMap;
	}

	public void setAllPlayCardMap(Map<String, List<Integer>> allPlayCardMap) {
		this.allPlayCardMap = allPlayCardMap;
	}

	public int getIsMyDo() {
		return isMyDo;
	}

	public void setIsMyDo(int isMyDo) {
		this.isMyDo = isMyDo;
	}

	public int getMasterOrder() {
		return masterOrder;
	}

	public void setMasterOrder(int masterOrder) {
		this.masterOrder = masterOrder;
	}

	public int getCallRatio() {
		return callRatio;
	}

	public void setCallRatio(int callRatio) {
		this.callRatio = callRatio;
	}

	public int getRatio() {
		return ratio;
	}

	public void setRatio(int ratio) {
		this.ratio = ratio;
	}

	public List<Integer> getMyCardList() {
		return myCardList;
	}

	public void setMyCardList(List<Integer> myCardList) {
		this.myCardList = myCardList;
	}

	public Map<Integer, ReLinkUser> getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(Map<Integer, ReLinkUser> userInfo) {
		this.userInfo = userInfo;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getMasterCard() {
		return masterCard;
	}

	public void setMasterCard(String masterCard) {
		this.masterCard = masterCard;
	}

	public List<String> getPlayCardList() {
		return playCardList;
	}

	public void setPlayCardList(List<String> playCardList) {
		this.playCardList = playCardList;
	}

	public String getGameServer() {
		return gameServer;
	}

	public void setGameServer(String gameServer) {
		this.gameServer = gameServer;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
}
