package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.common.util.PatternUtils;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Constant;
import com.lordcard.entity.GameUser;
import com.lordcard.network.http.GameCache;

/**
 * 聊天对话框
 * @author xu
 */
public class ChatDialog extends Dialog implements OnClickListener {
	private List<RelativeLayout> taskLayoutList;
	private final int[] gifInt = { R.drawable.f01, R.drawable.f02, R.drawable.f03, R.drawable.f04, R.drawable.f05, R.drawable.f06, R.drawable.f07, R.drawable.f08 };
//	private final int[] girlsDrawable = { R.drawable.girl1, R.drawable.girl2, R.drawable.girl3, R.drawable.girl4, R.drawable.girl5, R.drawable.girl6, R.drawable.girl7, R.drawable.girl8 };
	private final String[] gifStr = { "g01.gif", "g02.gif", "g03.gif", "g04.gif", "g05.gif", "g06.gif", "g07.gif", "g08.gif" };
//	private final String[] girlStr = { "girl1.png", "girl2.png", "girl3.png", "girl4.png", "girl5.png", "girl6.png", "girl7.png", "girl8.png" };
	private Button thinkMessBtn, moRenBtn, usualMessBtn, sendMessBtn, girlMessBtn, sendMessBtn1, exitBtn = null;
	private RelativeLayout thinkMessLayout, morenImageLayout, usualMessLayout, girlMessLayout;
	private EditText messEditText, thinkText;
	private GridView girlGrid, boyGrid; // 美女和小孩gif
	private MyAdapter girlGridAdapter, boyGridAdapter;
	private ListView messList, thinkList;
	private Context mContext;
	private Handler mHandler;
	private int clickType = Constant.MESSAGE_TYPE_TWO;
	// 适应多屏幕的工具
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();

	public ChatDialog(Context context,Handler mHandler) {
		super(context, R.style.dialog);
		this.mContext = context;
		this.mHandler=mHandler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liaotian_menu);
		try {
			clickType = Constant.MESSAGE_TYPE_TWO;// 默认美女
			taskLayoutList = new ArrayList<RelativeLayout>();
			mst.adjustView(findViewById(R.id.liaotian_layout));
			// layout.findViewById(R.id.liao_bg).setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.liaotian_bj_1));
			moRenBtn = (Button) findViewById(R.id.quite_image_btn);
			usualMessBtn = (Button) findViewById(R.id.usual_mess_btn);
			thinkMessBtn = (Button) findViewById(R.id.think_mess_btn);
			sendMessBtn = (Button) findViewById(R.id.send_mess_btn);
			girlMessBtn = (Button) findViewById(R.id.girl_mess_btn);
			exitBtn = (Button) findViewById(R.id.guanbi_btn);
			exitBtn.setOnClickListener(this);
			sendMessBtn1 = (Button) findViewById(R.id.send_mess_btn1);
			messEditText = (EditText) findViewById(R.id.mess_text);
			thinkText = (EditText) findViewById(R.id.think_text);
			sendMessBtn.setOnClickListener(this);
			sendMessBtn1.setOnClickListener(this);
			// messText1 = (EditText)
			// layout.findViewById(R.id.think_text);
			morenImageLayout = (RelativeLayout) findViewById(R.id.moren_image_layout);
			usualMessLayout = (RelativeLayout) findViewById(R.id.usual_mess_layout);
			thinkMessLayout = (RelativeLayout) findViewById(R.id.think_mess_layout);
			girlMessLayout = (RelativeLayout) findViewById(R.id.girl_image_layout);
			taskLayoutList.add(morenImageLayout);
			taskLayoutList.add(usualMessLayout);
			taskLayoutList.add(thinkMessLayout);
			taskLayoutList.add(girlMessLayout);
			moRenBtn.setOnClickListener(this);
			usualMessBtn.setOnClickListener(this);
			thinkMessBtn.setOnClickListener(this);
			girlMessBtn.setOnClickListener(this);
			boyGrid = (GridView) findViewById(R.id.gridviews);
			boyGrid.setPadding(1, 1, 1, 1);
			boyGridAdapter = new MyAdapter(gifInt,mContext);
			boyGrid.setAdapter(boyGridAdapter);
			final GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
			boyGrid.setOnItemClickListener(new Gallery.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
					Message msg=new Message();
					Bundle b=new Bundle();
					msg.what=Constant.HANDLER_WHAT_GAME_VIEW_SEND_MESS_GIF;
					b.putString(Constant.GAME_VIEW_SEND_MESS_GIF, gifStr[position]);
					b.putInt(Constant.GAME_VIEW_SEND_MESS_CLICK_TYPE,clickType);
					msg.setData(b);
					mHandler.sendMessage(msg);
					dismiss();
				}
			});
//			// 美女 start
//			girlGrid = (GridView) layout.findViewById(R.id.girl_grid);
//			girlGrid.setPadding(1, 1, 1, 1);
//			girlGridAdapter = new MyAdapter(girlsDrawable);
//			girlGrid.setAdapter(girlGridAdapter);
//			girlGrid.setOnItemClickListener(new Gallery.OnItemClickListener() {
//
//				@Override
//				public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
//					MobclickAgent.onEvent(DoudizhuMainGameActivity.this, "发送美女");
//					String imageName = girlStr[position];
//					CmdDetail chat = new CmdDetail();
//					chat.setCmd(CmdUtils.CMD_CHAT);
//					CmdDetail chatDetail = new CmdDetail();
//					chatDetail.setType(clickType);
//					chatDetail.setValue(imageName);
//					chatDetail.setFromUserId(cacheUser.getAccount());
//					String dj = JsonHelper.toJson(chatDetail);
//					chat.setDetail(dj);
//					myFrame.removeAllViews();
//					myFrame.setVisibility(View.GONE);
//					startTask(girlLeftFrame, selfTask);
//					messageFrame(girlLeftFrame, imageName, clickType, null);
//					ClientCmdMgr.sendCmd(chat);
//					reNameDialog.dismiss();
//				}
//			});
//			// 美女 end
			messList = (ListView) findViewById(R.id.message_list);
			messList.setAdapter(new ArrayAdapter<String>(mContext, R.layout.mess_list_item, getMessData()));
			messList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					messEditText.setText(getMessData().get(arg2));
					
					Message msg=new Message();
					Bundle b=new Bundle();
					msg.what=Constant.HANDLER_WHAT_GAME_VIEW_SEND_MESS_TEXT;
					b.putString(Constant.GAME_VIEW_SEND_MESS_TEXT, getMessData().get(arg2));
					b.putInt(Constant.GAME_VIEW_SEND_MESS_CLICK_TYPE,clickType);
					msg.setData(b);
					mHandler.sendMessage(msg);
					dismiss();
				}
			});
			thinkList = (ListView) findViewById(R.id.think_list);
			thinkList.setAdapter(new ArrayAdapter<String>(mContext, R.layout.mess_list_item, getThinkData()));
			thinkList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					thinkText.setText(getThinkData().get(arg2));
					
					Message msg=new Message();
					Bundle b=new Bundle();
					msg.what=Constant.HANDLER_WHAT_GAME_VIEW_SEND_MESS_TEXT;
					b.putString(Constant.GAME_VIEW_SEND_MESS_TEXT, getThinkData().get(arg2));
					b.putInt(Constant.GAME_VIEW_SEND_MESS_CLICK_TYPE,clickType);
					msg.setData(b);
					mHandler.sendMessage(msg);
					dismiss();
				}
			});
		} catch (Exception e) {}
	}

	@Override
	public void onClick(View v) {
		Message msg=new Message();
		Bundle b=new Bundle();
		switch (v.getId()) {
			case R.id.guanbi_btn:
				dismiss();
				break;
			case R.id.send_mess_btn:
				if(PatternUtils.hasSensitivword(messEditText.getText().toString().trim()))
				{
					Toast.makeText(mContext, "聊天内容不能包含敏感信息", Toast.LENGTH_SHORT).show();
				}else
				{
					msg.what=Constant.HANDLER_WHAT_GAME_VIEW_SEND_MESS_TEXT;
					b.putString(Constant.GAME_VIEW_SEND_MESS_TEXT, messEditText.getText().toString());
					b.putInt(Constant.GAME_VIEW_SEND_MESS_CLICK_TYPE,clickType);
					msg.setData(b);
					mHandler.sendMessage(msg);
					MobclickAgent.onEvent(mContext, "聊天发送");
					dismiss();
				}
				
				break;
			case R.id.send_mess_btn1:
				if(PatternUtils.hasSensitivword(thinkText.getText().toString().trim()))
				{
					Toast.makeText(mContext, "聊天内容不能包含敏感信息", Toast.LENGTH_SHORT).show();
				}else
				{
					msg.what=Constant.HANDLER_WHAT_GAME_VIEW_SEND_MESS_TEXT;
					b.putString(Constant.GAME_VIEW_SEND_MESS_TEXT, thinkText.getText().toString());
					b.putInt(Constant.GAME_VIEW_SEND_MESS_CLICK_TYPE,clickType);
					msg.setData(b);
					mHandler.sendMessage(msg);
					MobclickAgent.onEvent(mContext, "聊天发送");
					dismiss();
				}
				break;
			case R.id.gridviews://GIF动画
				break;
			case R.id.girl_grid://美女
				break;
			case R.id.quite_image_btn://
				MobclickAgent.onEvent(mContext, "表情");
				clickType = Constant.MESSAGE_TYPE_TWO;
				moRenBtn.setBackgroundResource(R.drawable.images_btn_2);
				usualMessBtn.setBackgroundResource(R.drawable.images_btn_1);
				thinkMessBtn.setBackgroundResource(R.drawable.images_btn_1);
				girlMessBtn.setBackgroundResource(R.drawable.images_btn_1);
				getPageView(0);
				break;
			case R.id.usual_mess_btn://
				MobclickAgent.onEvent(mContext, "短语");
				clickType = Constant.MESSAGE_TYPE_ZERO;
				usualMessBtn.setBackgroundResource(R.drawable.images_btn_2);
				moRenBtn.setBackgroundResource(R.drawable.images_btn_1);
				thinkMessBtn.setBackgroundResource(R.drawable.images_btn_1);
				girlMessBtn.setBackgroundResource(R.drawable.images_btn_1);
				getPageView(1);
				break;
			case R.id.think_mess_btn://
				MobclickAgent.onEvent(mContext, "思考");
				clickType = Constant.MESSAGE_TYPE_ONE;
				thinkMessBtn.setBackgroundResource(R.drawable.images_btn_2);
				moRenBtn.setBackgroundResource(R.drawable.images_btn_1);
				usualMessBtn.setBackgroundResource(R.drawable.images_btn_1);
				girlMessBtn.setBackgroundResource(R.drawable.images_btn_1);
				getPageView(2);
				break;
//				case R.id.girl_mess_btn:
//				MobclickAgent.onEvent(PersonnalDoudizhuActivity.this, "美女");
//				clickType = Constant.MESSAGE_TYPE_THREE;
//				girlMessBtn.setBackgroundResource(R.drawable.images_btn_2);
//				moRenBtn.setBackgroundResource(R.drawable.images_btn_1);
//				usualMessBtn.setBackgroundResource(R.drawable.images_btn_1);
//				thinkMessBtn.setBackgroundResource(R.drawable.images_btn_1);
//				getPageView(3);
//				break;
			default:
				break;
		}
	}
	
	// 短语，思考
	private List<String> getMessData() {
		String[] items = mContext.getResources().getStringArray(R.array.mes_language);
		List<String> list = Arrays.asList(items);
		return list;
	}

	private List<String> getThinkData() {
		String[] items = mContext.getResources().getStringArray(R.array.think_language);
		List<String> list = Arrays.asList(items);
		return list;
	}
	private void getPageView(int pageID) {
		for (int i = 0; i < taskLayoutList.size(); i++) {
			if (pageID == i) {
				taskLayoutList.get(i).setVisibility(View.VISIBLE);
			} else {
				taskLayoutList.get(i).setVisibility(View.GONE);
			}
		}
	}
	
	@Override
	public void dismiss() {
		thinkText.setText("");
		messEditText.setText("");
		
//		girlGridAdapter = null;
//		boyGridAdapter = null;
//		if (thinkMessLayout != null) {
//			thinkMessLayout.removeAllViews();
//			thinkMessLayout = null;
//		}
//		if (morenImageLayout != null) {
//			morenImageLayout.removeAllViews();
//			morenImageLayout = null;
//		}
//		if (usualMessLayout != null) {
//			usualMessLayout.removeAllViews();
//			usualMessLayout = null;
//		}
//		if (girlMessLayout != null) {
//			girlMessLayout.removeAllViews();
//			girlMessLayout = null;
//		}
		
//		if (boyGrid != null && boyGrid.getChildCount() > 0) {
//			boyGrid.destroyDrawingCache();
//			boyGrid.clearAnimation();
//			boyGrid = null;
//		}
//		if (girlGrid != null && girlGrid.getChildCount() > 0) {
//			girlGrid.destroyDrawingCache();
//			girlGrid.clearAnimation();
//			girlGrid = null;
//		}
//		if (null != taskLayoutList) {
//			taskLayoutList.clear();
//		}
//		taskLayoutList = null;
		super.dismiss();
	}
}

// 显示表情
class MyAdapter extends BaseAdapter {
	
	private int[] gifInt;
	private LayoutInflater mInflater;
	private Context context;
	
	public MyAdapter(int[] gifs,Context context) {
		this.gifInt = gifs;
		this.context=context;
		this.mInflater = LayoutInflater.from(context);
	}
	
	public int getCount() {
		return gifInt.length;
	}
	
	public Object getItem(int position) {
		return gifInt[position];
	}
	
	public long getItemId(int position) {
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.gif_item, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.gif1);
			holder.iv.setBackgroundDrawable(ImageUtil.getDrawableResId(gifInt[position], true, true));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		return convertView;
	}
	
	private class ViewHolder {
		
		private ImageView iv;
	}
}
