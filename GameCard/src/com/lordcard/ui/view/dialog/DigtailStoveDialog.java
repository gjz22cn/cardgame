package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.entity.GoodsTypeDetail;
import com.lordcard.network.http.HttpURL;

public class DigtailStoveDialog extends Dialog implements OnClickListener {

	private Context context;
	private LinearLayout mainLayout;
	private GoodsTypeDetail digiDetail;
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private ImageView stoveTip;
	private Bitmap tempBitmap;//图片资源
	private Handler handler;

	//	protected DigtailStoveDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
	//		super(context, cancelable, cancelListener);
	//		this.context = context;
	//	}

	public DigtailStoveDialog(Context context, int theme, GoodsTypeDetail digiDetail) {
		super(context, theme);
		this.context = context;
		this.digiDetail = digiDetail;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.good_stove_dialog);
		mainLayout = (LinearLayout) findViewById(R.id.stovedigital_layout);
		mainLayout.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.liaotian_bj_1,true));
		mainLayout.setOnClickListener(null);
		mst.adjustView(mainLayout);
		layout(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		dismiss();
		return super.onTouchEvent(event);
	}

	/**
	 * 布局
	 * 
	 * @param context
	 */
	private void layout(final Context context) {
		TextView diggitalText = (TextView) findViewById(R.id.digtail_detail_text);
		TextView stovenameText = (TextView) findViewById(R.id.digital_name_layout);
		stoveTip = (ImageView) findViewById(R.id.stove_digtail_img);
		stovenameText.setText(digiDetail.getName());
		diggitalText.setText("      " + digiDetail.getRemark());
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1001:
					if (null != stoveTip && null != tempBitmap) {
						stoveTip.setImageBitmap(tempBitmap);
					}
					break;
				default:
					break;
				}
			}
		};

		final String path = HttpURL.URL_PIC_ALL + digiDetail.getLargePicPath();
		tempBitmap = ImageUtil.getBitmap(path, false);
		if (tempBitmap != null) {
			handler.sendEmptyMessage(1001);
		} else {
			new Thread() {
				public void run() {
					tempBitmap = ImageUtil.getBitMapFromNetWork(path);
					handler.sendEmptyMessage(1001);
				};
			}.start();
		}
		//		setImgNoCache(path);
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void dismiss() {
		super.dismiss();
		mst.unRegisterView(mainLayout);
		//		Database.StoveDialogShow=false;
		ImageUtil.releaseDrawable(mainLayout.getBackground());
		if (null != stoveTip && !stoveTip.isShown()) {
			stoveTip.destroyDrawingCache();
		}
		stoveTip = null;
		//		if(null !=tempBitmap && !tempBitmap.isRecycled()){
		//			tempBitmap.recycle();
		//		}
		tempBitmap = null;
	}

	/**
	 * 设置图片
	 * @param path
	 */
	//	private  synchronized void setImgNoCache(final String path) {
	//		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
	//		if (sdCardExist) {
	//			if (null != ImageUtil.getImageFromSdCard(path)) {
	//				Log.i("prizeImage", "advImageCacheMap从SD卡取："+path);
	//				tempBitmap=ImageUtil.getImageFromSdCard(path);
	//				handler.sendEmptyMessage(1001);
	//				return;
	//			}
	//		} else {
	//			if (null != ImageUtil.getImageFromData(path)) {
	//				Log.i("prizeImage", "advImageCacheMap从内存取："+path);
	//				tempBitmap=ImageUtil.getImageFromData(path);
	//				handler.sendEmptyMessage(1001);
	//				return;
	//			}
	//		}
	//
	//		new Thread() {
	//			@Override
	//			public void run() {
	//				byte[] data = null;
	//				try {
	//					data = DownloadUtils.getImage(path);
	//				} catch (Exception e) {
	//				}
	//				if (data != null && data.length != 0) {
	//					BitmapFactory.Options options = new BitmapFactory.Options();
	//					options.inPreferredConfig = Bitmap.Config.RGB_565;
	//					options.inSampleSize = 2;
	//					if(null !=tempBitmap && !tempBitmap.isRecycled()){
	//						tempBitmap.recycle();
	//						tempBitmap=null;
	//					}
	//					tempBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
	//					boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
	//					if (sdCardExist) {
	//						ImageUtil.saveImageToSdCard(path, tempBitmap);
	//					} else {
	//						ImageUtil.saveImageToData(path, tempBitmap);
	//						Log.i("joinRoom", "------------------------保存图片："+path+"----------------------------------");
	//					}
	//					handler.sendEmptyMessage(1001);
	//				}
	//			}
	//		}.start();
	//	}
}
