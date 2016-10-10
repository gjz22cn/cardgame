package com.lordcard.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.lordcard.common.exception.CrashApplication;
import com.lordcard.common.util.ImageUtil;

public class MySelector {
	/** 设置Selector。 */
	public interface DrawCallback {
		/**设置加载的图片
		 * @param bitmap
		 * @param view
		 */
		public void imageLoaded(StateListDrawable bitmap, ImageView view);
		/**设置默认图片
		 * @param view
		 * @param homeType
		 */
		public void imageLoadedDefault(ImageView view,String Code);
	}

	public static void newSelector(Drawable idNormal, Drawable idPressed,String Code, final ImageView imageview, final DrawCallback drawCallback) {
		drawCallback.imageLoadedDefault(imageview,Code);
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				drawCallback.imageLoaded((StateListDrawable) msg.obj, imageview);
			}
		};
		StateListDrawable bg = new StateListDrawable();
		bg.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, idPressed);
		bg.addState(new int[] { android.R.attr.state_enabled }, idNormal);
		bg.addState(new int[] {}, idNormal);
		Message message = handler.obtainMessage(0, bg);
		handler.sendMessage(message);
		return;
	}

	public static void newSelector(final String idNormal, final String idPressed,final String Code, final ImageView imageview, final DrawCallback drawCallback) {
		drawCallback.imageLoadedDefault(imageview,Code);
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				drawCallback.imageLoaded((StateListDrawable) msg.obj, imageview);
			}
		};
		new Thread() {
			@Override
			public void run() {
				StateListDrawable bg = new StateListDrawable();
				Bitmap bitmapN=ImageUtil.getBitmap(idNormal, true);
				Bitmap bitmapP=ImageUtil.getBitmap(idPressed, true);
				if(null ==bitmapN && null ==bitmapP){
					Message message = handler.obtainMessage(0, null);
					handler.sendMessage(message);
				}else{
					Drawable normal=null;
					Drawable pressed=null;
					if(null ==bitmapN || null ==bitmapP){
						if(null ==bitmapN){
							 normal = new BitmapDrawable(bitmapP);
							 pressed = new BitmapDrawable(bitmapP);
						}else{
							 normal = new BitmapDrawable(bitmapN);
							 pressed = new BitmapDrawable(bitmapN);
						}
					}else{
						 normal = new BitmapDrawable(bitmapN);
						 pressed = new BitmapDrawable(bitmapP);
							//将图片路径存入SharedPreferences
							CrashApplication.getInstance().getSharedPreferences("saveImgPath", Context.MODE_PRIVATE).edit().putString(Code+"N", idNormal).commit();
							CrashApplication.getInstance().getSharedPreferences("saveImgPath", Context.MODE_PRIVATE).edit().putString(Code+"P", idPressed).commit();
					}
					bg.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, pressed);
					bg.addState(new int[] { android.R.attr.state_enabled }, normal);
					bg.addState(new int[] {}, normal);
					Message message = handler.obtainMessage(0, bg);
					handler.sendMessage(message);
				}
				return;
			}
		}.start();

	}
}
