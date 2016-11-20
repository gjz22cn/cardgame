package com.lordcard.adapter;

import com.zzyddz.shui.R;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.exception.CrashApplication;
import com.lordcard.common.task.GenericTask;
import com.lordcard.common.task.TaskManager;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.MySelector;
import com.lordcard.entity.MySelector.DrawCallback;
import com.lordcard.entity.Room;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpCallback;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.network.http.HttpURL;
import com.lordcard.ui.base.FastJoinTask;
import com.lordcard.ui.dizhu.DoudizhuRoomListActivity;
import com.lordcard.ui.view.dialog.SignupDialog;


public class RoomlistAdapter extends BaseAdapter {

	private Context context = null;
	private LayoutInflater layoutInflater = null;
	private TaskManager taskManager;
	private List<Room> roomlist = null;
	private List<ViewHolder> viewHolders;
	final String FAIL = "1";// 调比赛时间失败
	final String SIGNTIME = "17";// 报名时间
	final String PLAYTIME = "15";
	final String PLAYUNSIGN = "21";// 比赛时间 没有报名
	final String SIGNED = "10";// 报名时间，已经报名
	final String SIGNSUCCED = "12";// 报名成功
	final String SIGNFAIL = "11";// 报名失败
	final String LESSBEAN = "20";// 报名失败
	private Map<String, String> picMap;
	private String imageUrl;
	private String imgClickurl;
	private GenericTask rjoinTask;
	private SignupDialog hcsignDialog;
	private Set<SoftReference<Drawable>> drawCacheSet = new HashSet<SoftReference<Drawable>>();
	private Set<ImageView> imageViewSet = new HashSet<ImageView>();
	private static int mvPersonCount = 0;//当前虚拟人数
	public RoomlistAdapter(Context context, TaskManager taskManager) {
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.taskManager = taskManager;
		if (Database.HALL_CACHE != null && null != Database.HALL_CACHE.getGameRoomList()) {
			roomlist = Database.HALL_CACHE.getGameRoomList(); // 大厅房
		} else {
			roomlist = new ArrayList<Room>();
		}
		viewHolders = new ArrayList<ViewHolder>();
	}
	//设置虚拟人数
    public static void setMVPerson()
    {
    	Random r = new Random();
		int hour = ActivityUtils.getTime_Hour();
		//八点到10点
		if(hour>=20&&hour<22)
		{
			mvPersonCount = 15+r.nextInt(10)+r.nextInt(20);
		}
		//10点到12点
		else if(hour>=22 && hour<=24)
		{
			mvPersonCount = 15+r.nextInt(10);
		}
		//0点到早8点
		else if(hour>=0 && hour<=8)
		{
			mvPersonCount = 10+r.nextInt(10);
		}
		//8点到早12点
		else if(hour>=8 && hour<12)
		{
			mvPersonCount = 20+r.nextInt(20);
		}
		//8点到早12点
		else if(hour>=12 && hour<=13)
		{
			mvPersonCount = 30+r.nextInt(20);
		}
		//13点到早15点
		else if(hour>=13 && hour<=15)
		{
			mvPersonCount = 20+r.nextInt(30);
		}
		//15点到早20点
		else if(hour>=15 && hour<=20)
		{
			mvPersonCount = 30+r.nextInt(50);
		}else
		{
			mvPersonCount = 20+r.nextInt(30);
		}
    }
	/**
	 * 刷新大厅房间数据
	 */
	public void setRoomList() {
		if (Database.HALL_CACHE != null && null != Database.HALL_CACHE.getGameRoomList()) {
			this.roomlist = Database.HALL_CACHE.getGameRoomList(); // 大厅房
		} else {
			roomlist = new ArrayList<Room>();
		}
		viewHolders = new ArrayList<ViewHolder>();
	}
	/*
	 * 刷新大厅房间人数
	 */
	public static boolean isFirstSetCount = true;
    public void setRoomPersonCount()
    {
    	if(isFirstSetCount)
    	{
    		isFirstSetCount = false;
    		Random r = new Random();
        	for(int i=0;i<viewHolders.size();i++)
        	{
        		ViewHolder holder = viewHolders.get(i);
        		if(holder.roomItemBg == null || holder.roomItemPersonCount == null)
        		{
        			continue;
        		}
        		if(!holder.loading)
        		{
        			holder.setCount();
        		}else
        		{
        			holder.roomItemPersonCount.setText("");
        		}
        	}
    	}else
    	{
    		setMVPerson();
        	Random r = new Random();
        	for(int i=0;i<viewHolders.size();i++)
        	{
        		ViewHolder holder = viewHolders.get(i);
        		if(holder.roomItemBg == null || holder.roomItemPersonCount == null)
        		{
        			continue;
        		}
        		if(!holder.loading)
        		{
        			holder.isAdd = holder.isAddPerson();
        		}
        	}
        	for(int i=0;i<mvPersonCount;i++)
        	{
        		int roomIndex = r.nextInt(1000)%viewHolders.size();
        		ViewHolder holder = viewHolders.get(roomIndex);
        		if(holder.roomItemBg == null || holder.roomItemPersonCount == null)
        		{
        			continue;
        		}
        		if(!holder.loading)
        		{
        			holder.setCount(1,holder.isAdd);
        		}
        	}
    	}

    }
    public ViewHolder getholderByImageView(ImageView view)
    {
    	for(int i=0;i<viewHolders.size();i++)
    	{
    		ViewHolder holder = viewHolders.get(i);
    		if(holder.roomItemBg == view)
    		{
    			return holder;
    		}
    	}
    	return null;
    }
	@Override
	public int getCount() {
		return roomlist.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final Room room = roomlist.get(position);
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.room_item, null);
			holder = new ViewHolder();
			viewHolders.add(holder);
			holder.roomItemBg = (ImageView) convertView.findViewById(R.id.room_item_bg);
			holder.roomItemPersonCount = (TextView) convertView.findViewById(R.id.room_item_personCount);
			holder.roomItemPersonCount.setText("");
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			String url = room.getResHallUrl();
			boolean useDefaultImg = false;
			if(TextUtils.isEmpty(url)){
				useDefaultImg = true;
			}
			
			if(useDefaultImg){
				setDefault(holder.roomItemBg,position);
			}else{
				picMap = JsonHelper.fromJson(room.getResHall(), new TypeToken<Map<String, String>>() {});
				String itemName = picMap.get("roomItemH");
				String itemClickName = picMap.get("roomItemV");
				imgClickurl = url + itemClickName;
				imageUrl = url + itemName;
				//从缓存获取图片
				Drawable idNormal=ImageUtil.ImageHasLocal(imageUrl);
				Drawable idPressed=ImageUtil.ImageHasLocal(imgClickurl);
				if (idNormal != null && idPressed != null) {//根据图片链接，去本地找图片
					StateListDrawable bg = new StateListDrawable();
					bg.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, idPressed);
					bg.addState(new int[] { android.R.attr.state_enabled }, idNormal);
					if (imageViewSet.contains(holder.roomItemBg)) {
						ImageUtil.releaseDrawable(holder.roomItemBg.getBackground());
					}
					imageViewSet.add(holder.roomItemBg);
					SoftReference<Drawable> reference = new SoftReference<Drawable>(bg);
					bg = null;
					drawCacheSet.add(reference);
					holder.roomItemBg.setBackgroundDrawable(reference.get());
					holder.isLoading(false);
					holder.setDefaultPCount();
					holder.setCount();
				} else {
					final ViewHolder holder2 =  holder;
					MySelector.newSelector(imageUrl, imgClickurl, room.getCode(), holder.roomItemBg, new DrawCallback() {
						public void imageLoaded(StateListDrawable bitmap, ImageView view) {
							if(null !=bitmap){
								if (imageViewSet.contains(view)) {
									ImageUtil.releaseDrawable(view.getBackground());
								}
								imageViewSet.add(view);
								SoftReference<Drawable> reference = new SoftReference<Drawable>(bitmap);
								bitmap = null;
								drawCacheSet.add(reference);
								view.setBackgroundDrawable(reference.get());
								holder2.isLoading(false);
								holder2.setDefaultPCount();
								holder2.setCount();
							}
						}

						@Override
						public void imageLoadedDefault(ImageView view, String Code) {
							//先获取原始的图片
							String normalPath=CrashApplication.getInstance().getSharedPreferences("saveImgPath", Context.MODE_PRIVATE).getString(Code+"N", imageUrl);
							String pressedPath=CrashApplication.getInstance().getSharedPreferences("saveImgPath", Context.MODE_PRIVATE).getString(Code+"P", imgClickurl);
							Drawable idNormal=ImageUtil.ImageHasLocal(normalPath);
							Drawable idPressed=ImageUtil.ImageHasLocal(pressedPath);
							
							if (idNormal == null ) {
								idNormal = ImageUtil.getDrawableResId(R.drawable.room_loading,true,false);
								ViewHolder holder = getholderByImageView(view);
								if(holder != null)
								{
									holder.isLoading(true);
									holder.clearCount();
								}
								//holder.clearCount();
								
							}
							
							if (idPressed == null ) {
								idPressed = ImageUtil.getDrawableResId(R.drawable.room_loading,true,false);
								ViewHolder holder = getholderByImageView(view);
								if(holder != null)
								{
									holder.isLoading(true);
									holder.clearCount();
								}
							}
							
							//根据之前SharedPreferences保存的图片链接，到本地找之前的图片
							if (idNormal != null && idPressed != null) {
								StateListDrawable bg = new StateListDrawable();
								bg.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, idPressed);
								bg.addState(new int[] { android.R.attr.state_enabled }, idNormal);
								if (imageViewSet.contains(view)) {
									ImageUtil.releaseDrawable(view.getBackground());
								}
								imageViewSet.add(view);
								SoftReference<Drawable> reference = new SoftReference<Drawable>(bg);
								bg = null;
								drawCacheSet.add(reference);
								view.setBackgroundDrawable(reference.get());
							}
							setRoomPersonCount();
						}
					});
				}
			}
			
		} catch (Exception e) {}
		
//		JsonHelper.fromJson(room.getStartRace(), new TypeToken<Map<String, String>>() {});
		holder.roomItemBg.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (Math.abs(System.currentTimeMillis() - Constant.CLICK_TIME) >= Constant.SPACING_TIME) {
					Constant.CLICK_TIME=System.currentTimeMillis();
					
					long limitBean = room.getLimit();
					GameUser gu = (GameUser)GameCache.getObj(CacheKey.GAME_USER);
					double btBean = limitBean - gu.getBean();
					joinRoom(room);
					String roomName = room.getName();
					//MobclickAgent.onEvent(context,roomName);
				}
			}
		});
		return convertView;
	}

	public class ViewHolder {
		private boolean loading = false;
		private ImageView roomItemBg;// 背景图片
		private TextView roomItemPersonCount;//人数
		private int defaultPCount = 100;//默认人数
		public boolean isAdd = true;
		private void setDefaultPCount()
		{
			Random r = new Random();
			int hour = ActivityUtils.getTime_Hour();
			//八点到10点
			if(hour>=20&&hour<22)
			{
				defaultPCount = 1000+r.nextInt(10000)+r.nextInt(30000);
			}
			//10点到12点
			else if(hour>=22 && hour<=24)
			{
				defaultPCount = 1000+r.nextInt(10000);
			}
			//0点到早8点
			else if(hour>=0 && hour<=8)
			{
				defaultPCount = 100+r.nextInt(200);
			}
			//8点到早12点
			else if(hour>=8 && hour<12)
			{
				defaultPCount = 2000+r.nextInt(2000);
			}
			//8点到早12点
			else if(hour>=12 && hour<=13)
			{
				defaultPCount = 5000+r.nextInt(5000);
			}
			//13点到早15点
			else if(hour>=13 && hour<=15)
			{
				defaultPCount = 2000+r.nextInt(5000);
			}
			//15点到早20点
			else if(hour>=15 && hour<=20)
			{
				defaultPCount = 16000+r.nextInt(6000);
			}else
			{
				defaultPCount = 100+r.nextInt(1000);
			}
		}
		private boolean isAddPerson()
		{
			Random r = new Random();
			int hour = ActivityUtils.getTime_Hour();
			int addRate = 50;//增加人数的概率
			/*if(hour>=6&&hour<24)
			{
				addRate =90;
			}
			else if(hour>=0&&hour<3)
			{
				addRate =20;
			}
			else if(hour>=3&&hour<6)
			{
				addRate =10;
			}*/
			//八点到10点
			if(hour>=20&&hour<22)
			{
				addRate = 80;
			}
			//10点到12点
			else if(hour>=22 && hour<=24)
			{
				addRate = 60;
			}
			//0点到早8点
			else if(hour>=0 && hour<=8)
			{
				addRate = 20;
			}
			//8点到早12点
			else if(hour>=8 && hour<12)
			{
				addRate = 70;
			}
			//8点到早12点
			else if(hour>=12 && hour<=13)
			{
				addRate = 40;
			}
			//13点到早15点
			else if(hour>=13 && hour<=15)
			{
				addRate = 40;
			}
			//15点到早20点
			else if(hour>=15 && hour<=20)
			{
				addRate = 70;
			}else
			{
				addRate = 50;
			}
			return r.nextInt(100)<=addRate;
		}
		private void setCount()
		{
			int count =0;
			Random r = new Random();
			if(RoomlistAdapter.mvPersonCount>0)
			{
				count = r.nextInt(RoomlistAdapter.mvPersonCount);
				RoomlistAdapter.mvPersonCount -= count;
			}
			
			boolean isAddPerson = isAddPerson();
			
			if(!isAddPerson)
			{
				count = -count;
			}
			/*if(isAddPerson)
			{
				int baseCount = 2+r.nextInt(10);
				count = r.nextInt(baseCount);
			}else
			{
				count = -count;
			}*/
    		if(!loading)
    		{
    			int Num = defaultPCount+count;
    			int mincount = 20+r.nextInt(80);
    			if(Num<mincount)
    			{
    				Num = mincount;
    			}
    			roomItemPersonCount.setText("在线人数:"+Num);
    		}else
    		{
    			roomItemPersonCount.setText("");
    		}
		}
		private void setCount(int count,boolean isAdd)
		{
			if(!isAdd)
			{
				count = -count;
			}
    		if(!loading)
    		{
    			Random r = new Random();
    			defaultPCount +=count;
    			int mincount = 20+r.nextInt(80);
    			if(defaultPCount<mincount)
    			{
    				defaultPCount = mincount;
    			}
    			roomItemPersonCount.setText("在线人数:"+defaultPCount);
    		}else
    		{
    			roomItemPersonCount.setText("");
    		}
		}
		private void clearCount()
		{
			roomItemPersonCount.setText("");
		}
		private void isLoading(boolean loading)
		{
		    this.loading = loading;	
		    if(!loading)
		    {
		    	setDefaultPCount();
		    }
		}
	}

	public void onDestory() {
		if (roomlist != null) {
			roomlist = null;
		}
		layoutInflater = null;
		taskManager.cancelAll();
		taskManager = null;
		hcsignDialog = null;
		if (picMap != null) {
			picMap.clear();
			picMap = null;
		}
		if (rjoinTask != null) {
			rjoinTask.cancel(true);
			rjoinTask = null;
		}
		if (imageViewSet != null) {
			for (ImageView view : imageViewSet) {
				ImageUtil.releaseDrawable(view.getBackground());
			}
		}
		imageViewSet.clear();
		imageViewSet = null;
		if (drawCacheSet != null) {
			drawCacheSet.clear();
		}
		drawCacheSet = null;
	}

	/**
	 * 设置默认图片
	 * @param view
	 * @param Code
	 */
	private void setDefault(ImageView view, int position) {
		//1:新手房/17:中级场/19:高级场/109:乞丐场/44:钻石挖矿场/43:豪门争霸赛
 		try {
			Drawable normal = null;
			Drawable pressed = null;
			switch (position) {
				case 0:
					normal = context.getResources().getDrawable(R.drawable.grm01);
					pressed = context.getResources().getDrawable(R.drawable.grmv01);
					break;
				case 1:
					normal = context.getResources().getDrawable(R.drawable.grm02);
					pressed = context.getResources().getDrawable(R.drawable.grmv02);
					break;
				case 2:
					normal = context.getResources().getDrawable(R.drawable.grm03);
					pressed = context.getResources().getDrawable(R.drawable.grmv03);
					break;
				case 3:
					normal = context.getResources().getDrawable(R.drawable.grm04);
					pressed = context.getResources().getDrawable(R.drawable.grmv04);
					break;
				case 4:
					normal = context.getResources().getDrawable(R.drawable.drm5);
					pressed = context.getResources().getDrawable(R.drawable.drmv5);
					break;
				case 5:
					normal = context.getResources().getDrawable(R.drawable.drm6);
					pressed = context.getResources().getDrawable(R.drawable.drmv6);
					break;
				default:
					normal = context.getResources().getDrawable(R.drawable.room_loading);
					pressed = context.getResources().getDrawable(R.drawable.room_loading);
					break;
			}
			//设置Select图片
			StateListDrawable bg = new StateListDrawable();
			if (null == normal || null == pressed) {
				if (null == pressed) {
					pressed =normal;
				}else{
					normal = pressed;
				}
			}
			bg.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, pressed);
			bg.addState(new int[] { android.R.attr.state_enabled }, normal);
			bg.addState(new int[] {}, normal);
			//设置图片
			if (imageViewSet.contains(view)) {
				ImageUtil.releaseDrawable(view.getBackground());
			}
			imageViewSet.add(view);
			SoftReference<Drawable> reference = new SoftReference<Drawable>(bg);
			bg = null;
			drawCacheSet.add(reference);
			view.setBackgroundDrawable(reference.get());
		} catch (Exception e) {
			e.printStackTrace();
			view.setBackgroundResource(R.drawable.room_loading);
			ViewHolder holder = getholderByImageView(view);
			if(holder != null)
			{
				holder.isLoading(true);
				holder.clearCount();
			}
		}
	
	}

	/**
	 * 加入房间
	 * 
	 * @param room
	 */
	private synchronized void joinRoom(final Room room) {
		if (room.getHomeType() == 0) { // 普通房
			Database.GAME_BG_DRAWABLEID = R.drawable.gamebg;
		} else if (room.getHomeType() == 1) { // 高倍房
			Database.GAME_BG_DRAWABLEID = R.drawable.gamebg;
		} else if (room.getHomeType() == 2) { // VIP包房
			Database.GAME_BG_DRAWABLEID = R.drawable.background_3;
		}
		if (room.getHomeType() != 4 && room.getHomeType() != 5) {
			// 加入游戏前校验
			FastJoinTask.joinRoom(room);
		} else if (room.getHomeType() == 4) { // 钻石专场
			Database.GAME_BG_DRAWABLEID = R.drawable.gamebg;
			if (!Database.ZHIZHUANSIGN) {
				Database.ZHIZHUANSIGN = true;
				SignupDialog explainDialog = new SignupDialog(context, false, room, 1) {

					public void askJoin(Room rm) {
						// 加入游戏前校验
						if (rjoinTask != null) {
							rjoinTask.cancel(true);
							rjoinTask = null;
						}
						FastJoinTask.joinRoom(room);
						dismiss();
					}
				};
				explainDialog.show();
			} else {
				// 加入游戏前校验
				FastJoinTask.joinRoom(room);
			}
		} else if (room.getHomeType() == 5) {//合成剂专场
			Database.GAME_BG_DRAWABLEID = R.drawable.join_bj;
			Map<String, String> paramMap = new HashMap<String, String>();
			GameUser gameUser = (GameUser)GameCache.getObj(CacheKey.GAME_USER);
			paramMap.put("account",gameUser.getAccount());
			paramMap.put("code", room.getCode());
			paramMap.put("hallCode", String.valueOf(Database.GAME_TYPE));
			HttpRequest.postCallback(HttpURL.STOVE_ROOM_CHECK, paramMap, new HttpCallback() {

				@Override
				public void onSucceed(Object... obj) {
					String result = (String) obj[0];
					if (result.trim().equals(FAIL)) {
						return;
					}
					if (result.trim().equals(SIGNTIME)) {
						Database.currentActivity.runOnUiThread(new Runnable() {

							public void run() {
								hcsignDialog = new SignupDialog(context, false, room, 2) {

									public void askJoin(Room rm) {
										Map<String, String> paramMap = new HashMap<String, String>();
										GameUser gu = (GameUser)GameCache.getObj(CacheKey.GAME_USER);
										paramMap.put("account",gu.getAccount());
										paramMap.put("code", rm.getCode());
										paramMap.put("hallCode", String.valueOf(Database.GAME_TYPE));
										HttpRequest.postCallback(HttpURL.STOVE_ROOM_SIGN, paramMap, new HttpCallback() {

											@Override
											public void onSucceed(Object... obj) {
												String result = (String) obj[0];
												if (result.trim().equals(FAIL)) {
													dismiss();
												}
												if (result.trim().equals(SIGNED)) {
//													DialogUtils.mesTip("您已经报名过，请等候赛场开赛。", false);
													DialogUtils.toastTip("您已经报名过，请等候赛场开赛。", 1000, Gravity.CENTER);
													dismiss();
												}
												if (result.trim().equals(SIGNSUCCED)) {
//													DialogUtils.mesTip("恭喜您报名成功！", false);
													DialogUtils.toastTip("恭喜您报名成功！", 1000, Gravity.CENTER);
													dismiss();
												}
												if (result.trim().equals(SIGNFAIL)) {
													DialogUtils.toastTip("报名失败，请稍后再试！", 1000, Gravity.CENTER);
													dismiss();
												}
											}

											@Override
											public void onFailed(Object... obj) {
											}
										});
									}
								};
								if (Database.currentActivity.getClass().equals(DoudizhuRoomListActivity.class))
									hcsignDialog.show();
							}
						});
					}
					if (result.trim().equals(PLAYTIME)) {// 开赛已经报名
						// 加入游戏前校验
						Database.currentActivity.runOnUiThread(new Runnable() {

							public void run() {
								FastJoinTask.joinRoom(room);
							}
						});
					}
					if (result.equals(PLAYUNSIGN)) {// 开赛时间未报名
						Database.currentActivity.runOnUiThread(new Runnable() {

							public void run() {
								SignupDialog signDialog = new SignupDialog(context, true, room, 2) {

									public void askJoin(Room rm) {
										Map<String, String> paramMap = new HashMap<String, String>();
										GameUser gu = (GameUser)GameCache.getObj(CacheKey.GAME_USER);
										paramMap.put("account",gu.getAccount());
										paramMap.put("code", rm.getCode());
										paramMap.put("hallCode", String.valueOf(Database.GAME_TYPE));
										HttpRequest.postCallback(HttpURL.STOVE_ROOM_SIGN, paramMap, new HttpCallback() {

											@Override
											public void onSucceed(Object... obj) {
												String result = (String) obj[0];
												if (result.trim().equals(FAIL)) {
													dismiss();
												}
												if (result.trim().equals(SIGNSUCCED)) {
													dismiss();
													Database.currentActivity.runOnUiThread(new Runnable() {

														public void run() {
															FastJoinTask.joinRoom(room);
														}
													});
												}
												if (result.trim().equals(SIGNFAIL)) {
//													DialogUtils.mesTip("报名失败，请稍后再试！", false);
													DialogUtils.toastTip("报名失败，请稍后再试！", 1000, Gravity.CENTER);
													dismiss();
												}
											}

											@Override
											public void onFailed(Object... obj) {
											}
										});
									}
								};
								signDialog.show();
							}
						});
					}
				}

				@Override
				public void onFailed(Object... obj) {
				}
			});
		}
	}
}
