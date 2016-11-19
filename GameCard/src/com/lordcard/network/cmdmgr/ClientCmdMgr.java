package com.lordcard.network.cmdmgr;

import android.text.TextUtils;
import android.util.Log;

import com.lordcard.constant.Database;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.network.socket.ICallback;

public class ClientCmdMgr {

	private static long seq = 1; //消息序列号  一直递增

	private static Client client = null;

	/**
	 * 建立连接
	 * @Title: start  
	 * @param @param ip
	 * @param @param port
	 * @return void
	 * @throws
	 */
	public static boolean startClient(ICallback callback) {
		try {
//			HttpSynch.synchGameServer();
			HttpRequest.getCacheServer(true);
			
			String server = Database.GAME_SERVER;
			if (TextUtils.isEmpty(server))
				return false;

			String[] ipPort = server.split(":");
			if (ipPort.length != 2)
				return false;

			String ip = ipPort[0];
			int port = Integer.parseInt(ipPort[1]);

			boolean isNewClient = false;
			if (client == null) {
				isNewClient = true;
			} else if (client != null && !client.isConnected()) {
				client.destory();
				isNewClient = true;
			}

			if (isNewClient) {
				client = new Client(ip, port);
			}
			if (client.isConnected()) {
				client.setCallback(callback);
				client.startGame();
				return true;
			}
		} catch (Exception e) {
			closeClient();
		}
		return false;
	}

	/**
	 * Client 结束(退出游戏界面)
	 * @Title: destory  
	 * @param 
	 * @return void
	 * @throws
	 */
	public static void closeClient() {
		if (client != null) {
			client.destory();
		}
		client = null;
	}

	/**
	 * 设置Client状态
	 * @Title: setClientStatus  
	 * @param 
	 * @return void
	 * @throws
	 */
	public static void setClientStatus(int status) {
		if (client != null) {
			client.setStatus(status);
		}
	}

	/**
	 * 重连次数归零
	 * @Title: resetRelinkCount  
	 * @param 
	 * @return void
	 * @throws
	 */
	public static void resetRelinkCount() {
		client.setRelinkCount(0);
	}

	/**
	 * 发送消息
	 * @Title: sendCmd  
	 * @param @param detail
	 * @return void
	 * @throws
	 */
	public static void sendCmd(CmdDetail detail) {
		if (client != null) {
			client.sendCmd(detail);
		}
	}

	/**
	 * 一局结束
	 * @Title: gameOver  
	 * @param 
	 * @return void
	 * @throws
	 */
	public static void gameOver() {
		if (client != null) {
			client.gameOver();
		}
	}

	public synchronized static long getCmdSeq() {
		return seq++;
	}
}
