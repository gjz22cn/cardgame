package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lordcard.common.schedule.AutoTask;
import com.lordcard.common.schedule.ScheduledTask;
import com.lordcard.common.util.AudioPlayUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.ui.interfaces.PrizeInterface;

/**
 * 抽将项组件 model.Lottery
 * 
 * @author Administrator <br/>
 *         create at 2013 2013-3-19 下午2:43:03
 */
@SuppressLint("HandlerLeak")
public class Lottery extends RelativeLayout {

	private ImageView medalImage, coverImage;
	private RelativeLayout innerLayout;

	public RelativeLayout.LayoutParams params = null;
	public RelativeLayout.LayoutParams imageParams = null;
	public RelativeLayout.LayoutParams innerParams = null;
	public RelativeLayout.LayoutParams coverParams = null;
	private Lottery successor;
	private String name;
	private String index = "middle";
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private AutoTask autoTask;
	//	private Timer timer;
	//	private Context context;
	private String imageName;
	private Vector<PrizeInterface> obs;
	private static final String TAG = "Medal";
	private String value;
	private String detail;
	private boolean isCJ;

	public Lottery(Context context) {
		super(context);
		//		this.context = context;
		successor = null;
		obs = new Vector<PrizeInterface>();
		medalImage = new ImageView(context);
		innerLayout = new RelativeLayout(context);
		innerLayout.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.medal,true));
		imageParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
		imageParams.setMargins(mst.adjustXIgnoreDensity(2), mst.adjustXIgnoreDensity(5), mst.adjustXIgnoreDensity(2), mst.adjustXIgnoreDensity(2));
		//		imageParams.setMargins(2, 5, 2,2);
		innerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);

		coverImage = new ImageView(context);
		coverImage.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.lot_cover,true));
		coverParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
		coverParams.setMargins(mst.adjustXIgnoreDensity(5), mst.adjustXIgnoreDensity(5), mst.adjustXIgnoreDensity(5), mst.adjustXIgnoreDensity(2));
		//		coverParams.setMargins(5, 5, 5, 2);

		addView(medalImage, imageParams);
		addView(coverImage, coverParams);
		coverImage.setVisibility(View.INVISIBLE);
		innerLayout.setVisibility(View.INVISIBLE);
		addView(innerLayout, innerParams);
	}

	/**
	 * 处理本次抽奖单元的过程
	 */
	public void handle(boolean isChouJiang) {
		isCJ = isChouJiang;
		sendName();
		AudioPlayUtils.getInstance().playSoundMusic(true);
		getInnerLayout().setVisibility(View.VISIBLE);
		//		if (timer != null) {
		//			timer.cancel();
		//			timer.purge();
		//			timer = null;
		//		}
		if (autoTask != null) {
			autoTask.stop(true);
		}
		if (LotteryDialog.stop) {
			LotteryDialog.lucking = false;
			if (isChouJiang) {
				LotteryDialog.setCoverAllInvisible();
			} else {
				LotteryDialog.setCoverZhizuanInvisible();
			}
			if (LotteryDialog.error) {
				if (isChouJiang) {
					LotteryDialog.setAllInnerInvisuble();
				} else {
					LotteryDialog.setAllZhizuanInvisuble();
				}
				LotteryDialog.error = false;
			}
		} else {
			getCoverImage().setVisibility(View.INVISIBLE);
			next();
		}
	}

	public ImageView getMedalImage() {
		return medalImage;
	}

	public void setMedalImage(ImageView medalImage) {
		this.medalImage = medalImage;
	}

	public RelativeLayout getInnerLayout() {
		return innerLayout;
	}

	public void setInnerLayout(RelativeLayout innerLayout) {
		this.innerLayout = innerLayout;
	}

	public void setSuccessor(Lottery aSuccessor) {
		successor = aSuccessor;
	}

	/**
	 * 调用下一次抽奖单元
	 */
	public void next() {
		if (successor != null) {
			startMedal(LotteryDialog.speed);
		} else {
			Log.i(TAG, name + " successor null!");
		}
	}

	public void startMedal(int stopInSeconds) {
		if (autoTask != null) {
			autoTask.stop(true);
			autoTask = null;
		}
		autoTask = new AutoTask() {
			public void run() {
				autoTask.stop(false);
				handler.sendEmptyMessage(0);
			}
		};
		ScheduledTask.addDelayTask(autoTask, stopInSeconds);
		//		timer = new Timer();
		//		timer.schedule(new StopBeatingReminder(), stopInSeconds);
	}

	//	class StopBeatingReminder extends TimerTask {
	//		public void run() {
	//			if (timer != null) {
	//				timer.cancel();
	//				timer.purge();
	//				timer = null;
	//			}
	//			handler.sendEmptyMessage(0);
	//		}
	//	}

	/**
	 * 关闭定时器
	 */
	public void stopTimer() {
		//		if (timer != null) {
		//			timer.cancel();
		//			timer.purge();
		//			timer = null;
		//		}
		if (autoTask != null) {
			autoTask.stop(true);
			autoTask = null;
		}
	}

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				getInnerLayout().setVisibility(View.INVISIBLE);
				getCoverImage().setVisibility(View.VISIBLE);

				successor.handle(isCJ);
				break;
			default:
				break;
			}
		}
	};

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addPrizeListener(PrizeInterface o) {
		if (o == null)
			throw new NullPointerException();
		if (!obs.contains(o)) {
			obs.addElement(o);
		}
	}

	public void deletePrizeListener(PrizeInterface o) {
		obs.removeElement(o);
	}

	public void notifyPrizeListeners(Object arg) {
		Object[] arrLocal;
		arrLocal = obs.toArray();
		for (int i = arrLocal.length - 1; i >= 0; i--)
			((PrizeInterface) arrLocal[i]).prizeName(this);
	}

	public void deleteNameListenerrs() {
		obs.removeAllElements();
	}

	public void sendName() {
		notifyPrizeListeners(getName());
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public ImageView getCoverImage() {
		return coverImage;
	}

	public void setCoverImage(ImageView coverImage) {
		this.coverImage = coverImage;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

}
