//package com.lordcard.net.socket;
//
//import com.zzyddz.shui.R;
//
//import java.net.InetSocketAddress;
//import java.util.Date;
//import java.util.Locale;
//import java.util.concurrent.ConcurrentLinkedQueue;
//
//import org.jboss.netty.bootstrap.ClientBootstrap;
//import org.jboss.netty.buffer.ChannelBuffer;
//import org.jboss.netty.buffer.ChannelBuffers;
//import org.jboss.netty.channel.Channel;
//import org.jboss.netty.channel.ChannelFuture;
//import org.jboss.netty.channel.ChannelFutureListener;
//import org.jboss.netty.channel.ChannelHandlerContext;
//import org.jboss.netty.channel.ChannelPipeline;
//import org.jboss.netty.channel.ChannelPipelineFactory;
//import org.jboss.netty.channel.ChannelStateEvent;
//import org.jboss.netty.channel.Channels;
//import org.jboss.netty.channel.ExceptionEvent;
//import org.jboss.netty.channel.MessageEvent;
//import org.jboss.netty.channel.SimpleChannelHandler;
//import org.jboss.netty.handler.codec.string.StringDecoder;
//import org.jboss.netty.handler.codec.string.StringEncoder;
//
//import android.text.TextUtils;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.lordcard.common.exception.CrashApplication;
//import com.lordcard.common.exception.LogUtil;
//import com.lordcard.common.exception.NetException;
//import com.lordcard.common.mydb.ExceptionDao;
//import com.lordcard.common.schedule.AutoTask;
//import com.lordcard.common.schedule.ScheduledTask;
//import com.lordcard.common.util.ActivityUtils;
//import com.lordcard.common.util.DateUtil;
//import com.lordcard.common.util.DialogUtils;
//import com.lordcard.common.util.JsonHelper;
//import com.lordcard.constant.Constant;
//import com.lordcard.constant.Database;
//import com.lordcard.entity.CmdDetail;
//import com.lordcard.net.socket.Client.ServerCmdMsgHandler;
//
//public class Client_Bak {
//
//	private StringBuffer RECEIVE_MSG = new StringBuffer();
//
//	//	private ChannelFuture channelFuture;
//	private Channel channel;
//
//	private int TIME_OUT = 5000; //连接超时时间
//	boolean isTimeOut = false;
//	boolean isActiveClose = false; // 主动关闭
//
//	private long lastHbTime = 0; //最后接收心跳时间,0未开始接收心跳
//
//	private boolean canWrite = false; //是否允许发送
//
//	private AutoTask livingTask, sendCmdTask = null;
//
//	/** 发送的指令 */
//	private ConcurrentLinkedQueue<CmdDetail> cmdQuery = new ConcurrentLinkedQueue<CmdDetail>();
//	private ICallback callback;
//
//	/**
//	 * 创建连接
//	 */
//	public Client() {
//
//		if (TextUtils.isEmpty(Database.GAME_SERVER))
//			return;
//
//		String[] ipPort = Database.GAME_SERVER.split(":");
//		if (ipPort.length != 2)
//			return;
//
//		ClientBootstrap bootstrap = new ClientBootstrap(Constant.channelFactory);
//
//		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
//			public ChannelPipeline getPipeline() throws Exception {
//				ChannelPipeline pipeline = Channels.pipeline();
//				pipeline.addLast("encode", new StringEncoder());
//				pipeline.addLast("decode", new StringDecoder());
//				pipeline.addLast("handler", new ServerCmdMsgHandler());
//				return pipeline;
//			}
//		});
//
//		bootstrap.setOption("soLinger", 2000);
//		bootstrap.setOption("tcpNoDelay", true);
//		bootstrap.setOption("keepAlive", true);
//		bootstrap.setOption("reuseAddress", true);
//		bootstrap.setOption("connectTimeoutMillis", TIME_OUT); // 超时时间
//		String ip = ipPort[0];
//		int port = Integer.parseInt(ipPort[1]);
//		ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(ip, port));
//		channelFuture = channelFuture.awaitUninterruptibly(); // 等待返回的ChannelFuture
//		if (channelFuture.isSuccess()) { // 以确定建立连接的尝试是否成功。
//			channel = channelFuture.getChannel();
//
//			sendCmdTask = new AutoTask() {
//				public void run() {
//					autoSendCmd();
//				}
//			};
//
//			ScheduledTask.addRateTask(sendCmdTask, 500);
//			Constant.lastHbTime = System.currentTimeMillis();//初始化心跳值
//			this.starLivingTimer();
//			CmdDetail linkDetail = new CmdDetail();
//			linkDetail.setCmd(CmdUtils.CMD_LINK);
//			linkDetail.setMac(ActivityUtils.getAndroidId());
//			sentMessage(linkDetail);
//		}
//	}
//
//	/**
//	 * 关闭连接
//	 */
//	public void close() {
//		if (sendCmdTask != null) {
//			sendCmdTask.stop(true);
//		}
//
//		lastHbTime = 0;
//		this.setCallback(null);
//		new Thread() {
//			public void run() {
//				isActiveClose = true;
//				if (channel != null) {
//					channel.close();
//					channel.unbind();
//					channel.getCloseFuture().addListener(ChannelFutureListener.CLOSE);
//					channel.getCloseFuture().awaitUninterruptibly();
//					channel = null;
//				}
//			};
//		}.start();
//		//this.stopLivingListener();
//	}
//
//	/**
//	 * 消息处理
//	 * 
//	 * @author yinhb
//	 */
//	class ServerCmdMsgHandler extends SimpleChannelHandler {
//
//		@Override
//		public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
//			super.channelClosed(ctx, e);
//			if (isActiveClose)
//				return;
//
//			if (isTimeOut) { // 连接超时
//				DialogUtils.netFailTip(R.string.net_timeout);
//			}
//		}
//
//		/**
//		 * 网络异常
//		 */
//		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
//			e.getCause().printStackTrace();
//
//			NetException exception = new NetException();
//			exception.setCause(Database.GAME_SERVER + " ||" + e.getCause().getMessage());
//
//			String url = "autoExit:" + isActiveClose;
//			int size = Database.hasSendCmdList.size();
//			if (size > 15) {
//				Database.hasSendCmdList = Database.hasSendCmdList.subList(size - 15, size);
//			}
//			url += " ,cmd:{" + Database.hasSendCmdList.toString() + "}";
//			url += ",account=" + Database.USER.getAccount() + ",deviceId:" + ActivityUtils.getAndroidId();
//			exception.setUrl(url);
//			exception.setType("socket");
//			ExceptionDao.add(exception);
//
//			LogUtil.err("socket 网络中断异常",e.getCause());
//			
//			if (e.getCause() != null) {
//				String msg = e.getCause().getMessage().replaceAll(" ", "").toLowerCase(Locale.CHINA);
//				if (msg.equals("connectiontimedout")) { // 根据异常信息判断是否是连接超时
//					isTimeOut = true;
//				}
//			}
//
//			if (isConnected()) {
//				close();
//			}
//		}
//
//		/**
//		 * 接收消息
//		 */
//		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
//			// 非游戏界面时　socket消息全部忽略
//			if (!ActivityUtils.isGameView()) { // 不是游戏页面或结束，关闭socket 连接
//				stopLivingTimer();
//				close();
//				return;
//			}
//			setLastHbTime();
//			String msg = String.valueOf(e.getMessage());
//			if (RECEIVE_MSG.length() > 0) {
//				msg = RECEIVE_MSG.toString() + msg;
//				RECEIVE_MSG = new StringBuffer();
//			}
//
//			//socket已中断，后续发过来的指令不在接受处理 
//			if (!isConnected()) {
//				return;
//			}
//
//			String msgList[] = getCmdDetails(msg);
//
//			for (int i = 0; i < msgList.length; i++) {
//				doWithCmdMsg(msgList[i]);
//			}
//		}
//	}
//
//	private void autoSendCmd() {
//		if ((cmdQuery.size() > 0) & canWrite) {
//			if (channel != null && channel.isConnected()) {
//				CmdDetail detail = cmdQuery.poll();
//				if (detail != null & !TextUtils.isEmpty(detail.getCmd())) {
//					Database.hasSendCmdList.add(DateUtil.formatTimesDate(new Date()) + ":out." + detail.getCmd());
//					String sendMsg = detail.toJson() + ";";
//					Log.d(Constant.LOG_TAG, "===== >" + sendMsg);
//					ChannelBuffer buffer = ChannelBuffers.buffer(sendMsg.length() * 2);
//					buffer.writeBytes(sendMsg.getBytes());
//					channel.write(buffer);
//					//					channel.write(sendMsg);
//				}
//			}
//		}
//	}
//
//	/**
//	 * 判断是否连接
//	 * @return
//	 */
//	public boolean isConnected() {
//		if (channel != null && channel.isConnected()) {
//			return true;
//		}
//		return false;
//	}
//
//	public void sendHb() {
//		if (isConnected()) {
//			ChannelBuffer buffer = ChannelBuffers.buffer(4);
//			buffer.writeBytes("h;".getBytes());
//			channel.write(buffer);
//		}
//	}
//
//	/**
//	 * 发送信息
//	 * 
//	 * @param message
//	 */
//	public void sentMessage(CmdDetail detail) {
//		String cmd = detail.getCmd();
//		if (CmdUtils.CMD_HB.equals(cmd) || CmdUtils.CMD_LINK.equals(cmd) || CmdUtils.CMD_OUT.equals(cmd) || CmdUtils.CMD_CHAT.equals(cmd)
//				|| CmdUtils.CMD_RANK.equals(cmd)) {
//			if (isConnected()) {
//				String sendMsg = detail.toJson() + ";";
//				Log.d(Constant.LOG_TAG, "===== >" + sendMsg);
//				Database.hasSendCmdList.add(DateUtil.formatTimesDate(new Date()) + ": out." + detail.getCmd());
//				channel.write(sendMsg);
//			}
//		} else {
//			cmdQuery.add(detail);
//		}
//	}
//
//	/**
//	 * 发送信息
//	 * 
//	 * @param message
//	 */
//	public void sentMessage(String cmd, String message) {
//		CmdDetail detail = new CmdDetail();
//		detail.setCmd(cmd);
//		detail.setDetail(message);
//		sentMessage(detail);
//	}
//
//	/**
//	 * 服务器指令处理
//	 * 
//	 * @param msg
//	 */
//	private void doWithCmdMsg(String msg) {
//		if (TextUtils.isEmpty(msg))
//			return;
//
//		CmdDetail bf = JsonHelper.fromJson(msg, CmdDetail.class);
//		String cmd = bf.getCmd();
//
//		Database.hasSendCmdList.add(DateUtil.formatTimesDate(new Date()) + ":in." + cmd);
//		if (CmdUtils.CMD_LINK.equals(cmd)) { // 连接成功
//			canWrite = true;
//			return;
//		}
//
//		if (CmdUtils.CMD_HB.equals(cmd)) { // 心跳纪录时间点
//			lastHbTime = System.currentTimeMillis();
//		}
//
//		if (CmdUtils.CMD_ERR_RJOIN.equals(cmd)) { //玩家加入时，服务器暂停维护提示
//			if (CmdUtils.CMD_SER_STOP.equals(bf.getDetail())) {
//				String tipMsg = CrashApplication.getInstance().getResources().getString(R.string.gs_update);
//				DialogUtils.mesToastTip(tipMsg);
//				ActivityUtils.finishAcitivity();
//				return;
//			}
//		}
//
//		Log.d(Constant.LOG_TAG, "<=====" + msg);
//		if (callback != null && isConnected() && !isActiveClose) {
//			callback.getMessage(msg);
//		}
//	}
//
//	/**
//	 * 解析服务器返回的指令列表
//	 * 
//	 * @param str
//	 * @return
//	 */
//	public String[] getCmdDetails(String str) {
//		str = str.replaceAll(" ", "");
//		if (!str.endsWith(";")) { // 不是}; 号结尾 则表示收到的信息不完整
//			if (str.lastIndexOf(";") == -1) { // 单条指令不完整
//				RECEIVE_MSG.append(str);
//				str = "";
//			} else { // 多条指令 最后一条不完整
//				String tempStr = str.substring(0, str.lastIndexOf(";"));
//				RECEIVE_MSG.append(str.substring(str.lastIndexOf(";") + 1));
//				str = tempStr;
//			}
//		}
//		String list[] = str.split(";");
//		return list;
//	}
//
//	/**
//	 * 设置接收到最后一条命令的时间
//	 */
//	public void setLastHbTime() {
//		Constant.lastHbTime = System.currentTimeMillis();
//	}
//
//	/**
//	 * 自己网络情况监控
//	 * @throws
//	 */
//	public void starLivingTimer() {
//		if (livingTask != null) {
//			livingTask.stop(true);
//			livingTask = null;
//		}
//
//		livingTask = new AutoTask() {
//			public void run() {
//				sendHb();
//
//				//游戏时退出当前界面
//				if (Constant.lastHbTime == 0) {
//					return;
//				}
//				long now = System.currentTimeMillis();
//				Log.d("hjr", "client.isConnect ==" + isConnected());
//				Log.d("hjr", (now - Constant.lastHbTime) + " 时间");
//
//				long btime = now - Constant.lastHbTime;
//				int closeTime = Constant.WAIT_TIME + 15;
//
//				if (btime > closeTime * 1000) {
//					if (ActivityUtils.isGameView()) {
//						DialogUtils.toastTip("请在良好的网络环境下游戏！", Toast.LENGTH_LONG);
//						HeartBeatManager.closeClient();
//						ActivityUtils.finishAcitivity();
//
//						NetException exception = new NetException();
//						exception.setCause(Database.GAME_SERVER + " || " + "请在良好的网络环境下游戏");
//						int size = Database.hasSendCmdList.size();
//						if (size > 15) {
//							Database.hasSendCmdList = Database.hasSendCmdList.subList(size - 15, size);
//						}
//						String url = "cmd:{" + Database.hasSendCmdList.toString() + "}";
//						url += ",account=" + Database.USER.getAccount() + ",deviceId:" + ActivityUtils.getAndroidId();
//						exception.setUrl(url);
//						exception.setType("socket");
//						ExceptionDao.add(exception);
//					}
//					stopLivingTimer();
//				}
//			}
//		};
//		ScheduledTask.addRateTask(livingTask, 5000);
//	}
//
//	public void stopLivingTimer() {
//		if (livingTask != null) {
//			livingTask.stop(true);
//			livingTask = null;
//		}
//	}
//
//	public long getLastHbTime() {
//		return lastHbTime;
//	}
//
//	public ICallback getCallback() {
//		return callback;
//	}
//
//	public void setCallback(ICallback cbl) {// 2.2
//		this.callback = cbl;
//	}
//}
