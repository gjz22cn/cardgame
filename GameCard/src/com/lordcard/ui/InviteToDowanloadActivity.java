package com.lordcard.ui;

import com.zzyddz.shui.R;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.mydb.GameDBHelper;
import com.lordcard.common.mydb.PhoneDao;
import com.lordcard.common.task.GenericTask;
import com.lordcard.common.task.base.TaskParams;
import com.lordcard.common.task.base.TaskResult;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.ConfigUtil;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.EncodeUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.PatternUtils;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.entity.ContactPeople;
import com.lordcard.entity.GameTask;
import com.lordcard.entity.Room;
import com.lordcard.network.cmdmgr.CmdDetail;
import com.lordcard.network.cmdmgr.CmdUtils;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.network.http.HttpURL;
import com.lordcard.ui.base.BaseActivity;
import com.lordcard.ui.dizhu.DoudizhuMainGameActivity;
import com.lordcard.ui.view.MyLetterListView;
import com.lordcard.ui.view.MyLetterListView.OnTouchingLetterChangedListener;
import com.lordcard.ui.view.dialog.GameDialog;
import com.sdk.util.RechargeUtils;


@SuppressLint({ "HandlerLeak", "DefaultLocale" })
public class InviteToDowanloadActivity extends BaseActivity {

	private Button back = null;
	private Button yaoqing = null;
	private ProgressDialog progressDialog = null;
	private List<ContactPeople> contactPeopleList = null;
	private Intent in = null;
	private int type = 0; // 0:邀请下载游戏 1：邀请加入vip包房
	private BaseAdapter adapter;
	private ListView personList;
	private WindowManager mWindowManager;
	private TextView overlay;
	private MyLetterListView letterListView;
	private AsyncQueryHandler asyncQuery;
	private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
	private String[] sections;// 存放存在的汉语拼音首字母
	private Handler handler;
	private OverlayThread overlayThread;
	private boolean isInviteFriend = false; // 是否有邀请好友
	private Map<String, String> telphoneKeyMap = new HashMap<String, String>(); // 邀请的人
	/** 标签，ems版 **/
	private String emsSwitch;
	private RelativeLayout layout;
	private Map<String,String> allSettingMsgMap = null;
    private EditText obj_name_find;
    private Context context;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invited_download);
		this.context = this;
		setNameText();
		allSettingMsgMap = (HashMap<String, String>)GameCache.getObj(CacheKey.ALL_SETTING_KEY);
		layout = (RelativeLayout) findViewById(R.id.invited_download);
		mst.adjustView(layout);
		if (null != Database.ContactPeopleList) {
			Database.ContactPeopleList.clear();
		}
		emsSwitch = ConfigUtil.getCfg("ems_switch");
		in = getIntent();
		type = in.getIntExtra("type", 0);
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finishSelf();
			}
		});
		yaoqing = (Button) findViewById(R.id.sent);
		yaoqing.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				final List<ContactPeople> sendList = new ArrayList<ContactPeople>();
				if(peopleListFounded != null && peopleListFounded.size() > 0)
				{
					for (ContactPeople people : peopleListFounded) { // 找到需要发送邀请人
						if (people.isCheckdownload()) {
							sendList.add(people);
						}
					}
				}
				else if (contactPeopleList != null && contactPeopleList.size() > 0) {
					for (ContactPeople people : contactPeopleList) { // 找到需要发送邀请人
						if (people.isCheckdownload()) {
							sendList.add(people);
						}
					}
				}
				if (type == 0) { // 邀请下载游戏
					//MobclickAgent.onEvent(InviteToDowanloadActivity.this,ActivityUtils.getVersionName() +"邀请下载游戏");
					if (sendList.size() > 0) {
						GameDialog gameDialog = new GameDialog(Database.currentActivity) {

							public void okClick() {
								if (!ActivityUtils.simExist()) { // 判断SIM卡是否能使用
									return;
								}
								GenericTask inviteFriendTask = new InviteFriendTask();
								inviteFriendTask.setFeedback(feedback);
								TaskParams params = new TaskParams();
								params.put("sendList", sendList);
								inviteFriendTask.execute(params);
								taskManager.addTask(inviteFriendTask);
								// finish();
							}
						};
						gameDialog.show();
						gameDialog.setText("系统将以手机短信的方式邀请当前选择的好友参与游戏!");
					} else {
						GameDialog gameDialog = new GameDialog(Database.currentActivity) {

							public void cancelClick() {
								finish();
							};
						};
						gameDialog.show();
						gameDialog.setText("您当前没有选择邀请的好友。是否继续选择?");
					}
				} else if (type == 1) { // 邀请加入vip包房
					//MobclickAgent.onEvent(InviteToDowanloadActivity.this,ActivityUtils.getVersionName() +"邀请加入vip包房");
					if (sendList.size() > 0) {
						GameDialog gameDialog = new GameDialog(Database.currentActivity) {

							public void okClick() {
								isInviteFriend = true;
								createVipJoin();
								// finish();
							}
						};
						gameDialog.show();
						gameDialog.setText("系统将以手机短信的方式邀请当前选择的好友参与游戏!");
					} else {
						createVipJoin();
					}
				}
			}
		});
		progressDialog = DialogUtils.getWaitProgressDialog(InviteToDowanloadActivity.this, "数据加载中...");
		progressDialog.show();
		personList = (ListView) findViewById(R.id.InvitedlistView);
		letterListView = (MyLetterListView) findViewById(R.id.myLetterListView);
		letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
		alphaIndexer = new HashMap<String, Integer>();
		handler = new Handler();
		overlayThread = new OverlayThread();
		initOverlay();
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
	}
   private Hashtable<String,Integer> phoneTable;
   private ArrayList<ContactPeople> peopleListFounded;
   Handler myHandler_findPeople = new Handler() {   
	   public void handleMessage(Message msg) {  
		   switch (msg.what) {                  
		   case 100:     
			   if(obj_name_find != null)
			   {
				   if(peopleListFounded != null)
				   {
					   for (ContactPeople people : peopleListFounded) { // 找到需要发送邀请人
							if (people.isCheckdownload()) {
								people.setCheckdownload(false);
							}
						}
				   }
				   peopleListFounded.clear();
				   String str = obj_name_find.getText().toString();
				   if(str.equals(""))
				   {
					   setAdapter(contactPeopleList);
				   }else
				   {
					   for(int i=contactPeopleList.size()-1;i>=0;i--)
					   {
						   ContactPeople p = contactPeopleList.get(i);
						   String info = p.getSort_key()+p.getNumber();
						   String currentStr = p.getSort_Name(); 
						   boolean checkIndex = true;
						   str = str.toUpperCase(); 
						   if(str.length() ==1 && isAllUni(str))
						   {
							   checkIndex = false;
						   }
						   if(checkIndex &&(info.indexOf(str) != -1 || info.indexOf(currentStr) != -1))
						   {
							   peopleListFounded.add(p);
						   }else if(!checkIndex)
						   {
							   if(currentStr.startsWith(str))
							   {
								   peopleListFounded.add(p);
							   }
						   }
						   else
						   {
							  
								// 三个字符错误可以接受 //75%匹配可以接受
							    if (/*Match.match(2, str,currentStr)
									|| */Match.match(0.60,str,currentStr)) {
									peopleListFounded.add(p);
								}
						   }
					   }
					   if(peopleListFounded.size()>0)
					   {
						   setAdapter(peopleListFounded);
						   Toast.makeText(getApplicationContext(), "检索到"+peopleListFounded.size()+"个匹配项", Toast.LENGTH_SHORT).show();
					   }
				   }
			   }
			   break;                
			   }                  
		   super.handleMessage(msg);      
		   }    
	   }; 

   private void setNameText()
   {
	   peopleListFounded = new ArrayList<ContactPeople>();
	   phoneTable = new Hashtable<String, Integer>();
	   obj_name_find = (EditText) findViewById(R.id.tv_ObjName);
	   obj_name_find.addTextChangedListener(new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			//contactPeopleList
			myHandler_findPeople.sendEmptyMessage(100);
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
		}
	});
   }
	/**
	 * 进入创建的VIP
	 */
	private void createVipJoin() {
		// 加入游戏
		GenericTask vipCreateJoinTask = new VipRoomCreateJoinTask();
		vipCreateJoinTask.setFeedback(feedback);
		vipCreateJoinTask.execute();
		taskManager.addTask(vipCreateJoinTask);
	}

	public void onResume() {
		super.onResume();
		Uri uri = Uri.parse("content://com.android.contacts/data/phones");
		String[] projection = { Phone._ID, Phone.DISPLAY_NAME, Phone.DATA1, "sort_key", Phone.PHOTO_ID };
		asyncQuery.startQuery(0, null, uri, projection, null, null, "sort_key COLLATE LOCALIZED asc");
	}

	// 异步查询联系人
	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		ContentResolver contentResolver = null;

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
			contentResolver = cr;
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				Drawable defaultPhoto = ImageUtil.getResDrawable(R.drawable.default_contact, true);
				contactPeopleList = new ArrayList<ContactPeople>();
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					ContactPeople people = new ContactPeople();
					cursor.moveToPosition(i);
					String number = cursor.getString(2).replaceAll(" ", "");
					if (number.startsWith("+86")) {
						number = number.substring(3); // 去掉+86
					}
					if (PatternUtils.validMobiles(number)) {
						people.setNumber(number); // 去掉+86
						people.setMd5Number(EncodeUtils.MD5(number));
					} else {
						continue;
					}
					people.setName(cursor.getString(1));
					people.setSort_key(cursor.getString(3));
					people.setSort_Name(getAlpha2(people.getSort_key()));
					long photo_id = cursor.getLong(4);
					// 通讯录头像
					Drawable photo = null;
					try {
						if (photo_id > 0) {
							//							photo = ImageUtil.imageCacheMap.get(String.valueOf(photo_id));
							photo = ImageUtil.getDrawableResId((int) photo_id, true, true);
							if (photo == null) {
								Uri photoUri = ContactsContract.Data.CONTENT_URI;
								/**
								 * 1、Uri（实际上就是要查询的表）
								 * 2、需要返回的字段名（这里的字段"data15"存储的就是联系人的头像）
								 * 3、后面两个是where过滤
								 * ，传入先前查询到的photo_id，注意：其对应的是data表中"_id"字段）
								 **/
								Cursor photoCr = contentResolver.query(photoUri, new String[] { Phone.DATA15 }, "ContactsContract.Data._ID=?", new String[] { String.valueOf(photo_id) }, null);
								/** 对返回的Cursor进行遍历 */
								if (photoCr.moveToFirst()) {
									/* 对返回来的Cursor进行一系列的处理，最终转换成Bitmap对象 */
									ByteArrayInputStream inputStream = new ByteArrayInputStream(photoCr.getBlob(0));
									photo = new BitmapDrawable(BitmapFactory.decodeStream(inputStream));
									//									ImageUtil.imageCacheMap.put(String.valueOf(photo_id), photo);
									ImageUtil.addDrawable2Cache(String.valueOf(photo_id), photo);
									inputStream = null;
									photo = null;
								}
							}
						} else {
							photo = defaultPhoto;
						}
						
						people.setPhoto(photo);
						contactPeopleList.add(people);
						//phoneTable.put(people.getName()+people.getNumber(), contactPeopleList.size()-1);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				if (contactPeopleList.size() > 0) {
					setAdapter(contactPeopleList);
				}
				// 同步通讯录
				new Thread() {

					public void run() {
						if (type == 0) { // 邀请下载游戏
							SQLiteDatabase sqLite = GameDBHelper.openOrCreate();
							// 同步通讯录加密数据
							List<String> list = PhoneDao.queryAll(sqLite);
							if (null != list) {
								List<String> newPhoneList = new ArrayList<String>();
								for (ContactPeople people : contactPeopleList) {
									if (!list.contains(people.getMd5Number())) { // 不包含，则是新增加的
										newPhoneList.add(people.getMd5Number());
										PhoneDao.add(people, sqLite); // 更新本地
									}
								}
								if (newPhoneList.size() > 0) { // 同步
									HttpRequest.synchContack(newPhoneList);
								}
							}
							GameDBHelper.close();
							sqLite = null;
						}
					}
				}.start();
			}
			progressDialog.dismiss();
		}
	}

	private void setAdapter(List<ContactPeople> list) {
		Database.ContactPeopleList = list;
		adapter = new ContactListAdapter(this, list);
		personList.setAdapter(adapter);
	}

	private class ContactListAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private List<ContactPeople> peopleList;

		public ContactListAdapter(Context context, List<ContactPeople> list) {
			this.inflater = LayoutInflater.from(context);
			this.peopleList = list;
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				// 当前汉语拼音首字母
				String currentStr = getAlpha(list.get(i).getSort_key());
				// 上一个汉语拼音首字母，如果不存在为“ ”
				String previewStr = (i - 1) >= 0 ? getAlpha(list.get(i - 1).getSort_key()) : " ";
				if (!previewStr.equals(currentStr)) {
					String name = getAlpha(list.get(i).getSort_key());
					alphaIndexer.put(name, i);
					sections[i] = name;
				}
			}
		}

		@Override
		public int getCount() {
			return peopleList.size();
		}

		@Override
		public Object getItem(int position) {
			return peopleList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.contact_list_item, null);
				// if(position % 2 == 0){
				// convertView.setBackgroundResource(R.color.friend_item_color);
				// }
				holder = new ViewHolder();
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.number = (TextView) convertView.findViewById(R.id.number);
				holder.checkBox = (CheckBox) convertView.findViewById(R.id.people_check);
				holder.photoView = (ImageView) convertView.findViewById(R.id.photo_view);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ContactPeople cp = peopleList.get(position);
			holder.name.setText(cp.getName());
			holder.number.setText(cp.getNumber());
			holder.photoView.setBackgroundDrawable(cp.getPhoto());
			PeopleCheckLister peopleItemCheck = new PeopleCheckLister(holder.checkBox, position);
			holder.checkBox.setOnCheckedChangeListener(peopleItemCheck);
			if (cp.isCheckdownload()) {
				holder.checkBox.setChecked(true);
			} else {
				holder.checkBox.setChecked(false);
			}
			String currentStr = getAlpha(cp.getSort_key());// 当前字母
			String previewStr = (position - 1) >= 0 ? getAlpha(peopleList.get(position - 1).getSort_key()) : " ";
			if (!previewStr.equals(currentStr)) {
				holder.alpha.setVisibility(View.VISIBLE);
				holder.alpha.setText(currentStr);
			} else {
				holder.alpha.setVisibility(View.GONE);
			}
			return convertView;
		}

		private class ViewHolder {

			CheckBox checkBox;
			ImageView photoView;
			TextView alpha;
			TextView name;
			TextView number;
		}
	}

	public class PeopleCheckLister implements OnCheckedChangeListener {

		CheckBox check;
		int position;

		public PeopleCheckLister(CheckBox check, int position) {
			this.check = check;
			this.position = position;
		}

		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(peopleListFounded != null && peopleListFounded.size()>0)
			{
				if(peopleListFounded.size()>position)
				{
					if (isChecked) {
						peopleListFounded.get(position).setCheckdownload(true);
					} else {
						peopleListFounded.get(position).setCheckdownload(false);
					}
				}
			}else
			{
				if (isChecked) {
					contactPeopleList.get(position).setCheckdownload(true);
				} else {
					contactPeopleList.get(position).setCheckdownload(false);
				}
			}

		}
	}

	// 初始化汉语拼音首字母弹出提示框
	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.contact_overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
		/* james bug fix for leaked window start */
		/* old */
		//WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		//windowManager.addView(overlay, lp);
		/* new */
		mWindowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		mWindowManager.addView(overlay, lp);
		/* james bug fix for leaked window end  */
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//		mst.unRegisterView(layout);
		/* james bug fix for leaked window start */
		/* old */
		//WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		//windowManager.removeView(overlay);
		/* new */
		mWindowManager.removeView(overlay);
		/* james bug fix for leaked window end*/
	}

	private class LetterListViewListener implements OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(final String s) {
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				personList.setSelection(position);
				overlay.setText(sections[position]);
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);
				// 延迟一秒后执行，让overlay为不可见
				handler.postDelayed(overlayThread, 1500);
			}
		}
	}

	// 设置overlay不可见
	private class OverlayThread implements Runnable {

		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}
	}

	// 获得汉语拼音首字母
	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式，判断首字母是否是英文字母
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return "#";
		}
	}
	//检测字符串是否都是英文
	private boolean isAllUni(String str)
	{
		boolean res = true;
		if(str.length()>0)
		{
			Pattern pattern = Pattern.compile("^[A-Za-z]+$");
			for(int i=0;i<str.length();i++)
			{
				char c = str.charAt(i);
				if (!pattern.matcher(c + "").matches()) {
					res = false;
					break;
				}
			}
		}
		return res;
	}
	private String getAlpha2(String str) {
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		/*char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式，判断首字母是否是英文字母
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return "#";
		}*/
		boolean needAdd = true;
		str = str.trim();
		String res = "";
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		for(int i=0;i<str.length();i++)
		{
			char c = str.charAt(i);
			if (pattern.matcher(c + "").matches()) {
				if(needAdd)
				{
					res += (c + "").toUpperCase();
					needAdd = false;
				}

			}else
			{
				needAdd = true;
			}
		}
		if(res.equals(""))
		{
			res = "#";
		}
		return res;
	}
	class InviteFriendTask extends GenericTask {

		@SuppressWarnings("unchecked")
		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				TaskParams param = null;
				if (params.length <= 0) {
					return TaskResult.FAILED;
				}
				param = params[0];
				List<ContactPeople> sendList = (List<ContactPeople>) param.get("sendList");
				List<String> tempList = new ArrayList<String>();
				for (ContactPeople people : sendList) { // 邀请的人
					tempList.add(people.getMd5Number());
					telphoneKeyMap.put(people.getMd5Number(), people.getNumber());
				}
				// ------------------------------------------------------------------------------
				if ("close".equals(emsSwitch)) {
					String peopleNum = "";
					StringBuffer smsMsg = new StringBuffer();
					smsMsg.append("邀请你玩掌中游斗地主");
					if (null != allSettingMsgMap && allSettingMsgMap.containsKey("sms_send_apk") && !TextUtils.isEmpty(allSettingMsgMap.get("sms_send_apk"))) {
						smsMsg.append(" 。游戏下载地址  ：" + allSettingMsgMap.get("sms_send_apk"));
					}else{
						smsMsg.append(" 。游戏下载地址  ：" + HttpURL.CONFIG_SER + HttpURL.APK_NAME);
					}
					//smsMsg.append(" 。游戏下载地址  ：http://www.xnghm.com/");//xs_test
					for (String phoneKey : telphoneKeyMap.keySet()) {
						peopleNum += telphoneKeyMap.get(phoneKey) + ",";
					}
					sendSMS(smsMsg.toString(), peopleNum.substring(0, peopleNum.length() - 1));
				} else {
					String result = HttpRequest.inviteFriend(tempList);// 邀请好友接口
					if (HttpRequest.FAIL_STATE.equals(result)) { // 失败
						DialogUtils.mesTip(getString(R.string.invite_friend_fail), false);
					} else if (HttpRequest.TOKEN_ILLEGAL.equals(result)) { // 用户登录Token过期
						DialogUtils.reLogin(Database.currentActivity);
					} else {
						GameTask resultTask = JsonHelper.fromJson(result, GameTask.class);
						if (GameTask.TASK_TYPE[4] == resultTask.getType()) { // 邀请好友
							if (CmdUtils.FAIL_CODE.equals(resultTask.getValue())) { // 收到验证码失败
								DialogUtils.mesTip("无效验证码!", false);
							} else if (!CmdUtils.FAIL_CODE.equals(resultTask.getValue())) {
								Map<String, String> phoneMap = JsonHelper.fromJson(resultTask.getValue(), new TypeToken<Map<String, String>>() {});
								// 发送邀请码
								SmsManager smsManager = SmsManager.getDefault();
								Intent intent = new Intent(Constant.ACTION_SMS_SEND);
								PendingIntent sentIntent = PendingIntent.getBroadcast(InviteToDowanloadActivity.this, 0, intent, 0);
								ArrayList<PendingIntent> sentIntentList = new ArrayList<PendingIntent>();
								sentIntentList.add(sentIntent);
								for (String phoneKey : phoneMap.keySet()) {
									String code = phoneMap.get(phoneKey);
									StringBuffer smsMsg = new StringBuffer();
									smsMsg.append("邀请你玩掌中游斗地主");
									if (!TextUtils.isEmpty(code)) {
										smsMsg.append("，邀请码: " + code + "。凭此邀请码可以获系统赠送的金豆");
									}
									if (null != allSettingMsgMap && allSettingMsgMap.containsKey("sms_send_apk") && !TextUtils.isEmpty(allSettingMsgMap.get("sms_send_apk"))) {
										smsMsg.append(" 。游戏下载地址  ：" + allSettingMsgMap.get("sms_send_apk"));
									}else{
										smsMsg.append(" 。游戏下载地址  ：" + HttpURL.CONFIG_SER + HttpURL.APK_NAME);
									}
									//smsMsg.append(" 。游戏下载地址  ：http://www.xnghm.com/");//xs_test
									ArrayList<String> sendMsgList = smsManager.divideMessage(smsMsg.toString());
									smsManager.sendMultipartTextMessage(telphoneKeyMap.get(phoneKey), null, sendMsgList, sentIntentList, null);
								}
								if (resultTask.getCount() > 0) {
									DialogUtils.mesTip("恭喜您！通过邀请好友获取" + resultTask.getCount() + "金豆!", false);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				return TaskResult.FAILED;
			}
			return TaskResult.OK;
		}
	}

	/**
	 * 发送短信
	 * 
	 * @param smsBody
	 * @param smsContact
	 */
	private void sendSMS(String smsBody, String smsContact) {
		Uri smsToUri = Uri.parse("smsto:" + smsContact);
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		intent.putExtra("sms_body", smsBody);
		startActivity(intent);
	}

	private class VipRoomCreateJoinTask extends GenericTask {

		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				String result = HttpRequest.createRoom(Database.GAME_GROUP_NUM, Database.JOIN_ROOM_RATIO, false);
				// 创建失败
				if (result.equals(HttpRequest.FAIL_STATE)) {
					DialogUtils.mesTip(getString(R.string.room_create_fail), false);
					return TaskResult.FAILED;
				} else if (HttpRequest.TOKEN_ILLEGAL.equals(result)) { // 用户登录Token过期
					DialogUtils.reLogin(Database.currentActivity);
					return TaskResult.NO_LOGIN;
				} else {
					CmdDetail detail = JsonHelper.fromJson(result, CmdDetail.class);
					if (CmdUtils.CMD_ERR_CREATE.equals(detail.getCmd())) { // 创建失败
						String v = detail.getDetail();
						if (HttpRequest.NO_LOGIN.equals(v) || HttpRequest.TOKEN_ILLEGAL.equals(v)) { // 未登录,用户登录token非法
							DialogUtils.reLogin(Database.currentActivity);
						} else if (HttpRequest.NO_SERVER.equals(v)) { // 游戏服务器不存在
							DialogUtils.mesTip(getString(R.string.no_game_server), false);
						}
					} else if (CmdUtils.CMD_CREATE.equals(detail.getCmd())) { // 成功
						Room createRoom = JsonHelper.fromJson(detail.getDetail(), Room.class);
						Database.JOIN_ROOM = createRoom;
						Database.GAME_SERVER = Database.JOIN_ROOM.getGameServer(); // 游戏服务器
						Database.JOIN_ROOM_CODE = createRoom.getCode();
						Database.JOIN_ROOM_RATIO = createRoom.getRatio(); // vip包房倍数
						Database.JOIN_ROOM_BASEPOINT = createRoom.getBasePoint();
						Database.GAME_BG_DRAWABLEID = R.drawable.background_3;
						if (isInviteFriend) { // 给邀请的好友发邀请短信
							if (emsSwitch != null && !emsSwitch.equals("") && emsSwitch.equals("close")) {
								StringBuffer msgBuffer = new StringBuffer();
								msgBuffer.append("嘿！我在掌中游斗地主开了一个");// 嘿！我在千千游斗地主开了一个斗地主vip包房，房间号
								//								if (Database.VIP_GAME_TYPE == DoudizhuMainGameActivity.class) {
								//									msgBuffer.append("斗地主");
								//								}
								msgBuffer.append("斗地主");
								msgBuffer.append("vip包房, 房间号: ").append(Database.JOIN_ROOM_CODE).append(" 号。速来！");
								if (null != allSettingMsgMap && allSettingMsgMap.containsKey("sms_send_apk") && !TextUtils.isEmpty(allSettingMsgMap.get("sms_send_apk"))) {
									msgBuffer.append(" 。游戏下载地址  ：" + allSettingMsgMap.get("sms_send_apk"));
								}else{
									msgBuffer.append(" 。游戏下载地址  ：" + HttpURL.CONFIG_SER + HttpURL.APK_NAME);
								}
								String peopleNum = "";
								for (ContactPeople people : Database.ContactPeopleList) {
									if (people.isCheckdownload()) {
										peopleNum += people.getNumber() + ",";
									}
								}
								sendSMS(msgBuffer.toString(), peopleNum.substring(0, peopleNum.length() - 1));
							} else {
								SmsManager smsManager = SmsManager.getDefault();
								Intent intent = new Intent(Constant.ACTION_SMS_SEND);
								PendingIntent sentIntent = PendingIntent.getBroadcast(InviteToDowanloadActivity.this, 0, intent, 0);
								StringBuffer msgBuffer = new StringBuffer();
								msgBuffer.append("嘿！我在掌中游斗地主开了一个");// 嘿！我在千千游斗地主开了一个斗地主vip包房，房间号
																	// 100号。速来！
																	//								if (Database.VIP_GAME_TYPE == DoudizhuMainGameActivity.class) {
								//								}
								msgBuffer.append("斗地主");
								msgBuffer.append("vip包房, 房间号: ").append(Database.JOIN_ROOM_CODE).append(" 号。速来！");
								if (null != allSettingMsgMap && allSettingMsgMap.containsKey("sms_send_apk") && !TextUtils.isEmpty(allSettingMsgMap.get("sms_send_apk"))) {
									msgBuffer.append(" 。游戏下载地址  ：" + allSettingMsgMap.get("sms_send_apk"));
								}else{
									msgBuffer.append(" 。游戏下载地址  ：" + HttpURL.CONFIG_SER + HttpURL.APK_NAME);
								}
								ArrayList<PendingIntent> sentIntentList = new ArrayList<PendingIntent>();
								sentIntentList.add(sentIntent);
								for (ContactPeople people : Database.ContactPeopleList) {
									if (people.isCheckdownload()) {
										ArrayList<String> sendMsgList = smsManager.divideMessage(msgBuffer.toString());
										smsManager.sendMultipartTextMessage(people.getNumber(), null, sendMsgList, sentIntentList, null);
									}
								}
							}
						}
						Intent in = new Intent();
						in.putExtra("isviproom", true); // 标识是vip包房
						//						in.setClass(InviteToDowanloadActivity.this, Database.VIP_GAME_TYPE);
						in.setClass(InviteToDowanloadActivity.this, DoudizhuMainGameActivity.class);
						startActivity(in);
						finish();
					}
				}
			} catch (Exception e) {
				DialogUtils.mesTip(getString(R.string.vip_create_fail), false);
				return TaskResult.FAILED;
			}
			return TaskResult.OK;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finishSelf();
		}
		return super.onKeyDown(keyCode, event);
	}
}
