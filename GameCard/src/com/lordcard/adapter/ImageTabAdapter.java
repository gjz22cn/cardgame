/**
 * ImageTabAdapter.java [v 1.0.0]
 * classes : adapter.ImageTabAdapter
 * auth : yinhongbiao
 * time : 2012 2012-11-6 下午3:16:15
 */
package com.lordcard.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * adapter.ImageTabAdapter
 * 
 * @author yinhb <br/>
 *         create at 2012 2012-11-6 下午3:16:15
 */
public class ImageTabAdapter extends BaseAdapter {

	@SuppressWarnings("unused")
	private Context context;
	private int width;
	private int height;
	private ImageView[] imageViews;
	private List<Drawable> imageList;
	private List<Drawable> selectImageList;

	private int selectTabId = 0;

	public ImageTabAdapter(Context context, List<Drawable> imageList, List<Drawable> selectImageList, int width, int height) {
		this.context = context;
		this.imageList = imageList;
		this.selectImageList = selectImageList;
		this.width = width;
		this.height = height;
		imageViews = new ImageView[imageList.size()];
		for (int i = 0; i < imageViews.length; i++) {
			imageViews[i] = new ImageView(context);
		}
	}

	@Override
	public int getCount() {
		return imageList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 点击设置
	 * 
	 * @param selectedID
	 */
	public void setFocus(int selectedID) {
		for (int i = 0; i < imageViews.length; i++) {
			imageViews[i].setBackgroundColor(Color.TRANSPARENT);
			imageViews[i].setImageDrawable(imageList.get(i));
		}
		selectTabId = selectedID;
		imageViews[selectedID].setImageDrawable(selectImageList.get(selectedID));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == selectTabId) {
			imageViews[position].setImageDrawable(selectImageList.get(position));
		} else {
			imageViews[position].setImageDrawable(imageList.get(position));
		}
		imageViews[position].setLayoutParams(new GridView.LayoutParams(width, height));
		imageViews[position].setBackgroundColor(Color.TRANSPARENT);
		return imageViews[position];
	}
}
