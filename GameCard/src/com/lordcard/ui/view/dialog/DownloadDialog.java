/**
 * BaseDialog.java [v 1.0.0]
 * classes : com.lordcard.ui.view.dialog.BaseDialog
 * auth : yinhongbiao
 * time : 2013 2013-3-25 下午5:32:42
 */
package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.network.http.HttpURL;

/**
 * com.lordcard.ui.view.dialog.AlertDialog
 * 
 * @author Administrator <br/>
 *         create at 2013 2013-3-25 下午5:32:42
 */
public class DownloadDialog extends Dialog implements OnClickListener {

	private Context context;
	private TextView showText;
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private LinearLayout layout;
	private String text;
	private List<String> list;
	public DownloadDialog(Context context,String text,List<String> list) {
		super(context, R.style.dialog);
		this.context = context;
		this.text=text;
		this.list=list;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_dialog);
		Button cancel = (Button) findViewById(R.id.single_common_cancel);
		Button ok = (Button) findViewById(R.id.single_common_ok);
		showText = (TextView) findViewById(R.id.single_common_text);
		cancel.setOnClickListener(this);
		ok.setOnClickListener(this);
		layout = (LinearLayout) findViewById(R.id.mm_layout);
		showText.setText(text);
		mst.adjustView(layout);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.single_common_cancel:
			DialogUtils.toastTip("你可以到物品囊启动物品的下载");
			dismiss();
			break;
		case R.id.single_common_ok:
			DialogUtils.toastTip("你物品正在下载到物品囊");
			for (int i = 0; i < list.size(); i++) {
				ImageUtil.downMMImg(HttpURL.URL_PIC_ALL + list.get(i),null);
			}
			dismiss();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 重写返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
				dismiss();

		}
		return true;
	}
}
