package com.lordcard.ui.base;

//public class ReadyJoinTask extends GenericTask {
//
//	private TaskFeedback feedback = TaskFeedback.getInstance(TaskFeedback.DIALOG_MODE);
//	private boolean isGamePlace;
//
//	public ReadyJoinTask(boolean isGamePlace) {
//		this.isGamePlace = isGamePlace;// 是否是比赛场
//	}
//
//	public ReadyJoinTask(boolean isGamePlace, Room joinRoom) {
//		this.isGamePlace = isGamePlace;// 是否是比赛场
//	}
//
//	protected TaskResult _doInBackground(TaskParams... params) {
//		try {
//			TaskParams param = null;
//			if (params.length <= 0) {
//				return TaskResult.FAILED;
//			}
//			param = params[0];
//			if (!isGamePlace) {
//				// 加入前判断 是否赠送金豆
//				long sendBean = HttpRequest.sentBean();
//				if (sendBean > 0) {// 送豆成功
//					DialogUtils.sentBeanTip();
//				}
//			}
//
//			Room joinRoom = (Room) param.get("joinRoom");
//
//			String result = HttpRequest.rjoin(joinRoom.getCode(), joinRoom.getPassword());
//			if (TextUtils.isEmpty(result) || HttpRequest.FAIL_STATE.equals(result)) {
//				return TaskResult.FAILED;
//			}
//			if (!feedback.hasCancel()) { //进度对话框未取消
//				CmdDetail detail = JsonHelper.fromJson(result, CmdDetail.class);
//
//				if (CmdUtils.CMD_ERR_RJOIN.equals(detail.getCmd())) { // 加入失败
//					String v = detail.getDetail();
//					if (HttpRequest.NO_LOGIN.equals(v) || HttpRequest.TOKEN_ILLEGAL.equals(v)) { // 未登录,用户登录token非法
//						DialogUtils.reLogin(Database.currentActivity);
//					} else if (HttpRequest.NO_HOME.equals(v)) { // 加入的房间不存在
//						DialogUtils.mesTip(Database.currentActivity.getString(R.string.no_join_home), true);
//					} else if (HttpRequest.NO_SERVER.equals(v)) { // 游戏服务器不存在
//						DialogUtils.mesTip(Database.currentActivity.getString(R.string.no_game_server), false);
//					}
//
//				} else if (CmdUtils.CMD_HDETAIL.equals(detail.getCmd())) { //金豆不足
//					if (Database.currentActivity.getClass().equals(LoginActivity.class)) {
////						DialogUtils.toastTip("您的金豆不足，请进入游戏充值界面进行充值。");
//						Database.currentActivity.runOnUiThread(new Runnable() {
//							public void run() {
//								Intent intent = new Intent();
//								intent.setClass(Database.currentActivity, DoudizhuRoomListActivity.class);
//								Database.currentActivity.startActivity(intent);
//							}
//						});
//					} else {
//						Room room = JsonHelper.fromJson(detail.getDetail(), Room.class);
//						DialogUtils.rechargeTip(room, false, null);
//					}
//
//				} else if (CmdUtils.CMD_RJOIN.equals(detail.getCmd())) { // 成功
//					// 返回游戏服务器IP地址
//					Room room = JsonHelper.fromJson(detail.getDetail(), Room.class);
//					if (null != joinRoom) {
//						room.setRoomDetail(joinRoom.getRoomDetail());
//					}
//					Database.JOIN_ROOM = room; // 加入的房间
//					Database.GAME_SERVER = room.getGameServer(); // 游戏服务器
//					Database.JOIN_ROOM_CODE = room.getCode();
//					Database.JOIN_ROOM_RATIO = room.getRatio();
//					Database.JOIN_ROOM_BASEPOINT = room.getBasePoint();
//
//					Intent in = new Intent();
//					Context context = Database.currentActivity;
//					if (Database.GAME_TYPE == Constant.GAME_TYPE_DIZHU) { // 斗地主
//						in.setClass(context, DoudizhuMainGameActivity.class);
//					}
//					//MobclickAgent.onEvent(context, room.getName(), 1);
//					Boolean isFinish = param.getBoolean("finish");
//					if (isFinish != null && isFinish) {
//						Database.currentActivity.finish();
//					}
//					Database.currentActivity.startActivity(in);
//					//是否回收当前页面，结束页面快速开始时使用
//				}
//			}
//
//			joinRoom = null;
//			if (param != null) {
//				param.clearParams();
//			}
//			param = null;
//		} catch (Exception e) {
//			return TaskResult.FAILED;
//		}
//		feedback.cancel(null);
//		return TaskResult.OK;
//	}
//}
