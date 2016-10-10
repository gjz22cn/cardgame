/**
 * BaseDialog.java [v 1.0.0]
 * classes : com.lordcard.ui.view.dialog.BaseDialog
 * auth : yinhongbiao
 * time : 2013 2013-3-25 下午5:32:42
 */
package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lordcard.common.exception.CrashApplication;
import com.lordcard.common.upgrade.UPVersion;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.Constant;

/**
 * com.lordcard.ui.view.dialog.AlertDialog
 * 
 * @author Administrator <br/>
 *         create at 2013 2013-3-25 下午5:32:42
 */
public class UpdateDialog extends Dialog implements OnClickListener {

	private Context context;
	private TextView showText;
	private CheckBox box;
	private ListView updateList;
	private TextView nullTv;//空布局，用来接收焦点响应点击事件
	private Button quickIstallationBtn;// 一键安装按钮
	private LinearLayout bottomLl;//底部布局
	private SharedPreferences prefrences;
	private List<String> listContent = new ArrayList<String>();
	private boolean canCancel = true; // 是否允许取消
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private LinearLayout layout;
	private Button ok;

	public UpdateDialog(List<String> listContent, Context context, boolean canCancel) {
		super(context, R.style.dialog);
		this.listContent = listContent;
		this.context = context;
		this.canCancel = canCancel;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.updete_dialog);
		intView();
	}

	/**
	 * 布局
	 */
	private void intView() {
		layout = (LinearLayout) findViewById(R.id.ud_layout);
		layout.setOnClickListener(this);
		mst.adjustView(layout);
		quickIstallationBtn = (Button) findViewById(R.id.ud_quick_installation_btn);
		quickIstallationBtn.setOnClickListener(this);
		nullTv = (TextView) findViewById(R.id.up_null_tv);
		nullTv.setOnClickListener(this);
		bottomLl = (LinearLayout) findViewById(R.id.ud_bottom_ll);
		box = (CheckBox) findViewById(R.id.check_box);
		Button cancel = (Button) findViewById(R.id.common_cancel);
		TextView tips = (TextView) findViewById(R.id.tips_view);
		ok = (Button) findViewById(R.id.common_ok);
		if (!canCancel) {
			bottomLl.setVisibility(View.GONE);
			quickIstallationBtn.setVisibility(View.VISIBLE);
			boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
			SharedPreferences sharedsaveData = CrashApplication.getInstance().getSharedPreferences(Constant.UPDATECODE, Context.MODE_PRIVATE);
			int saveCode = sharedsaveData.getInt(Constant.SAVECODE, 0);
			File file = null;
			boolean isQuick = false;
			if (saveCode == UPVersion.versionCode) {
				if (sdCardExist) {
					file = new File(Environment.getExternalStorageDirectory(), UPVersion.apkName);
					if (file.exists()) {
						// DialogUtils.mesToastTip("SD卡有包！");
						isQuick = true;
					}
				} else {
					file = context.getFileStreamPath(UPVersion.apkName);
					if (file.exists()) {
						isQuick = true;
					}
				}
			}
			if (isQuick) {
				cancel.setVisibility(View.INVISIBLE);
				box.setVisibility(View.GONE);
				tips.setVisibility(View.VISIBLE);
			} else {
				cancel.setVisibility(View.INVISIBLE);
				box.setVisibility(View.INVISIBLE);
			}
		} else {
			bottomLl.setVisibility(View.VISIBLE);
			quickIstallationBtn.setVisibility(View.GONE);
		}
		updateList = (ListView) findViewById(R.id.update_list);
		updateList.setFocusable(false);
		TextAdapter updateAdapter = new TextAdapter(listContent);
		updateList.setAdapter(updateAdapter);
		updateList.setClickable(false);
		showText = (TextView) findViewById(R.id.common_text);
		cancel.setOnClickListener(this);
		ok.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.common_cancel:// 取消
				mst.unRegisterView(layout);
				dismiss();
				// “下次不再提醒”复选框被勾上
				if (box.isChecked()) {
					prefrences = CrashApplication.getInstance().getSharedPreferences(Constant.UPDATECODE, Context.MODE_PRIVATE);
					Editor editor2 = prefrences.edit();
					// 把当前服务器的版本保存在本地(标志之后都不更新这个版本了)
					editor2.putInt(Constant.VERSIONCODE, UPVersion.versionCode);
					editor2.commit();
				}
				cancelClick();
				break;
			case R.id.common_ok:// 确定
			case R.id.ud_quick_installation_btn://一键安装
				if (canCancel) {
					mst.unRegisterView(layout);
					dismiss();
				}
				okClick();
				break;
			case R.id.ud_layout:// 一键安装的状态时点击对话框的任何一个位置都可以安装
			case R.id.up_null_tv:
				if (!canCancel) {
					okClick();
				}
				break;
			default:
				break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 重写返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (canCancel) {
				if (box.isChecked()) {
					prefrences = CrashApplication.getInstance().getSharedPreferences(Constant.UPDATECODE, Context.MODE_PRIVATE);
					Editor editor2 = prefrences.edit();
					editor2.putInt(Constant.VERSIONCODE, UPVersion.versionCode);
					editor2.commit();
					cancelClick();
				}
				dismiss();
			}
		}
		return true;
	}

	public void setokBtn(String content) {
		showText.setText(content);
	}

	public void setText(String content) {
		showText.setText(content);
	}

	public void setList(List<String> content) {
		this.listContent = content;
	}

	/** 确定 */
	public void okClick() {};

	/** 取消 */
	public void cancelClick() {};

	/** 按钮 */
	public void setButton(String content) {
		ok.setText(content);
	};

	private class TextAdapter extends BaseAdapter {

		private List<String> gifInt;
		private LayoutInflater mInflater;

		public TextAdapter(List<String> goodbagList) {
			this.gifInt = goodbagList;
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return gifInt.size();
		}

		public Object getItem(int position) {
			return gifInt.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder mViewHolder;
			if (null == convertView) {
				mViewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.text_list_item, null);
				mViewHolder.text = (TextView) convertView.findViewById(R.id.evalues_text);
				convertView.setTag(mViewHolder);
			} else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}
			mViewHolder.text.setText(gifInt.get(position));
			return convertView;
		}

		class ViewHolder {

			private TextView text;// 最高奖励(普通赛制)
		}
	}
}
