package com.lordcard.ui.personal.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClientName {
	public static List<String> allClientNames = null;

	public static List<String> getallClientNames() {
		allClientNames = new ArrayList<String>();
		allClientNames.add("乔峰");
		allClientNames.add("段誉");
		allClientNames.add("虚竹");
		allClientNames.add("慕容复");
		allClientNames.add("郭靖");
		allClientNames.add("杨康");
		allClientNames.add("胡斐");
		allClientNames.add("令狐冲");
		allClientNames.add("独孤求败");
		allClientNames.add("岳不群");
		allClientNames.add("杨过");
		allClientNames.add("张无忌");
		allClientNames.add("韦小宝");
		allClientNames.add("张辽");
		allClientNames.add("郭嘉");
		allClientNames.add("荀彧");
		allClientNames.add("贾诩");
		allClientNames.add("司马懿");
		allClientNames.add("关羽");
		allClientNames.add("张飞");
		allClientNames.add("赵云");
		allClientNames.add("黄忠");
		allClientNames.add("魏延");
		allClientNames.add("诸葛亮");
		allClientNames.add("庞统");
		allClientNames.add("周瑜");
		allClientNames.add("陆逊");
		allClientNames.add("太史慈");
		allClientNames.add("曹操");
		allClientNames.add("孙权");
		allClientNames.add("刘备");
		allClientNames.add("吕布");
		allClientNames.add("武松");
		allClientNames.add("宋江");
		allClientNames.add("卢俊义");
		allClientNames.add("李逵");
		allClientNames.add("燕青");
		allClientNames.add("林冲");
		allClientNames.add("清风");
		allClientNames.add("明月");
		allClientNames.add("日游神");
		allClientNames.add("夜游神");
		allClientNames.add("九头虫");
		allClientNames.add("云里雾");
		allClientNames.add("雾里云");
		allClientNames.add("急如火");
		allClientNames.add("快如风");
		allClientNames.add("千里眼");
		allClientNames.add("顺风耳");
		List<String> ClientNames = new ArrayList<String>();

		for (int i = 0; i < 2; i++) {
			int nameSize = allClientNames.size();
			Random rd = new Random();
			int index = rd.nextInt(nameSize);
			ClientNames.add(allClientNames.get(index));
			allClientNames.remove(index);
		}

		return ClientNames;
	}
}
