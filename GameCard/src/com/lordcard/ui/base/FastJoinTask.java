package com.lordcard.ui.base;

import android.content.Intent;
import android.os.Bundle;

import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.entity.Room;
import com.lordcard.ui.dizhu.DoudizhuMainGameActivity;

public class FastJoinTask {

//	private static ProgressDialog waitDialog;
	public static void fastJoin() {
		try {
			Room room = new Room();
			room.setCode("-1");
			Database.JOIN_ROOM = room;
			Bundle bundle = new Bundle();
			bundle.putInt("type", Constant.FASTJOIN_TYPE);
			Intent intent = new Intent();
			intent.setClass(Database.currentActivity, DoudizhuMainGameActivity.class);
			intent.putExtras(bundle);
			Database.currentActivity.startActivity(intent);
		} catch (Exception e) {}
	}

	public static void joinRoom(Room room) {
		try {
			Database.JOIN_ROOM = room; // 加入的房间
			Database.JOIN_ROOM_CODE = room.getCode();
			Database.JOIN_ROOM_RATIO = room.getRatio();
			Database.JOIN_ROOM_BASEPOINT = room.getBasePoint();
			Intent intent = new Intent();
			intent.setClass(Database.currentActivity, DoudizhuMainGameActivity.class);
			Database.currentActivity.startActivity(intent);
		} catch (Exception e) {} finally {
		}
	}
}
