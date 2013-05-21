package statisticPageView.statistics;


import java.util.Calendar;

import database.HistoryDB;
import database.TimeBlock;
import main.activities.FragmentTabs;
import main.activities.R;
import main.activities.StatisticFragment;
import history.BracGameHistory;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import statisticPageView.StatisticPageView;
import test.data.BracDataHandler;

public class StatisticMonthView extends StatisticPageView {

	private HistoryDB db;
	private TextView[] time_labels;
	private Bitmap[] circleBmps;
	private ImageView[] circles;

	private LinearLayout timeLayout;
	private GridLayout blockLayout;
	private LinearLayout labelLayout;
	
	private TextView[] labels;
	private ImageView[] labelImgs;
	
	private TextView month0, month1, month2, month3, month4;
	
	private TextView title;
	
	private ImageView bg;
	
	private static final int nBlocks = 4;
	private static final int nDate = 28;
	
	private static final String[] blockHint = {"早","中","下","晚"};
	private static final String[] labelHint = {"通過","不通過","未測試"}; 
	
	private int timeblock_type;
	
	private Typeface digitTypeface;
	private Typeface wordTypeface;
	
	public StatisticMonthView(Context context,StatisticFragment statisticFragment) {
		super(context, R.layout.statistic_month_view, statisticFragment);
		db = new HistoryDB(context);
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		timeblock_type = sp.getInt("timeblock_num", 2);
	}


	@Override
	public void clear() {
		Log.d("CLEAR","WEEK");
		
		if (circleBmps!=null){
			for (int i=0;i<circleBmps.length;++i){
				if (circleBmps[i]!=null && !circleBmps[i].isRecycled()){
					circleBmps[i].recycle();
					circleBmps[i] = null;
				}
			}
			circleBmps = null;
		}
	}
	
	@Override
	public void onPreTask() {
		Point screen = StatisticFragment.getStatisticPx();
		
		digitTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dinproregular.ttf");
		wordTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dfheistd-w3.otf");
		
		timeLayout = (LinearLayout) view.findViewById(R.id.statistic_month_timeblock_label_layout);
		blockLayout = (GridLayout) view.findViewById(R.id.statistic_month_block_layout);
		
		int textSize =  (int) (screen.x * 42.0/720.0);
		int textSize2 =  (int) (screen.x * 36.0/720.0);
		title= (TextView) view.findViewById(R.id.statistic_month_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		title.setTypeface(wordTypeface);
		
		time_labels = new TextView[nBlocks];
		for (int i=0;i<nBlocks;++i){
			time_labels[i] = new TextView(context);
			time_labels[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize2);
			time_labels[i].setTextColor(0xFF000000);
			time_labels[i].setText(blockHint[i]);
			time_labels[i].setTypeface(wordTypeface);
			if (Build.VERSION.SDK_INT>=17)
				time_labels[i].setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
			time_labels[i].setGravity(Gravity.CENTER);
			timeLayout.addView(time_labels[i]);
			if (!TimeBlock.hasBlock(i, timeblock_type))
				time_labels[i].setAlpha(0.0F);
		}

		month0 = (TextView) view.findViewById(R.id.statistic_month_0);;
		month0.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize2);
		month0.setTypeface(digitTypeface);
		month1 = (TextView) view.findViewById(R.id.statistic_month_1);;
		month1.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize2);
		month1.setTypeface(digitTypeface);
		month2 = (TextView) view.findViewById(R.id.statistic_month_2);;
		month2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize2);
		month2.setTypeface(digitTypeface);
		month3 = (TextView) view.findViewById(R.id.statistic_month_3);;
		month3.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize2);
		month3.setTypeface(digitTypeface);
		month4 = (TextView) view.findViewById(R.id.statistic_month_4);;
		month4.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize2);
		month4.setTypeface(digitTypeface);
		
		bg = (ImageView) view.findViewById(R.id.statistic_month_bg);
		
		circles = new ImageView[nBlocks*nDate];
		
		for (int i=0;i<nBlocks*nDate;++i){
			circles[i] = new ImageView(context);
			blockLayout.addView(circles[i]);
			circles[i].setScaleType(ScaleType.CENTER);
		}
		
		labelLayout = (LinearLayout) view.findViewById(R.id.statistic_month_label_layout);
		labels = new TextView[3];
		labelImgs = new ImageView[3];
		for (int i=0;i<3;++i){
			labelImgs[i] = new ImageView(context);
			labelImgs[i].setScaleType(ScaleType.CENTER);
			labelLayout.addView(labelImgs[i]);
			labels[i] = new TextView(context);
			labels[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize2);
			labels[i].setTextColor(0xFF000000);
			labels[i].setGravity(Gravity.CENTER);
			if (Build.VERSION.SDK_INT>=17)
				labels[i].setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
			labels[i].setText(labelHint[i]);
			labels[i].setTypeface(wordTypeface);
			labelLayout.addView(labels[i]);
		}
	}


	@Override
	public void onInBackground() {
		
		Point screen = StatisticFragment.getStatisticPx();
		
		circleBmps = new Bitmap[4];
		int circleWidth = (int) (screen.x * 10.0/720.0);
		int circleHeight = (int) (screen.x *38.0/720.0);
		Bitmap[] tmp = new Bitmap[4];
		tmp[0] = BitmapFactory.decodeResource(context.getResources(),R.drawable.statistic_month_none);
		circleBmps[0] = Bitmap.createScaledBitmap(tmp[0], circleWidth, circleHeight, true);
		tmp[1] = BitmapFactory.decodeResource(context.getResources(),R.drawable.statistic_month_fail);
		circleBmps[1] = Bitmap.createScaledBitmap(tmp[1], circleWidth, circleHeight, true);
		tmp[2] = BitmapFactory.decodeResource(context.getResources(),R.drawable.statistic_month_pass);
		circleBmps[2] = Bitmap.createScaledBitmap(tmp[2], circleWidth, circleHeight, true);
		tmp[3] = BitmapFactory.decodeResource(context.getResources(),R.drawable.statistic_month_empty);
		circleBmps[3] = Bitmap.createScaledBitmap(tmp[3], circleWidth, circleHeight, true);
		for (int i=0;i<tmp.length;++i)
			if (circleBmps[i]!=tmp[i])
				tmp[0].recycle();
		
		
		
		RelativeLayout.LayoutParams tParam = (RelativeLayout.LayoutParams) timeLayout.getLayoutParams();
		tParam.leftMargin = (int)(screen.x*40.0/720.0);
		tParam.topMargin = (int)(screen.x*20.0/720.0);
		
		RelativeLayout.LayoutParams bParam = (RelativeLayout.LayoutParams) blockLayout.getLayoutParams();
		bParam.leftMargin = (int)(screen.x*00.0/720.0);
		bParam.topMargin = (int)(screen.x*20.0/720.0);
		
		RelativeLayout.LayoutParams titleParam = (RelativeLayout.LayoutParams) title.getLayoutParams();
		titleParam.topMargin = (int)(screen.x*80.0/720.0);
		
		int c_width =  (int) (screen.x * 75.0/720.0);
		int c_height =  (int) (screen.x * 50.0/720.0);
		
		int height = (c_height*4)/timeblock_type;
		
		int label_topMargin = (int)(screen.x*30.0/720.0);
		int label_width =  (int)(screen.x*136.0/720.0);
		
		RelativeLayout.LayoutParams mParam =  (RelativeLayout.LayoutParams)month0.getLayoutParams();
		mParam.width = label_width;
		mParam.height = c_height;
		mParam.topMargin = label_topMargin;
		mParam.leftMargin = (int)(screen.x*110.0/720.0);
		
		mParam =  (RelativeLayout.LayoutParams)month1.getLayoutParams();
		mParam.width = label_width;
		mParam.height = c_height;
		mParam.topMargin = label_topMargin;
		
		mParam =  (RelativeLayout.LayoutParams)month2.getLayoutParams();
		mParam.width = label_width;
		mParam.height = c_height;
		mParam.topMargin = label_topMargin;
		
		mParam =  (RelativeLayout.LayoutParams)month3.getLayoutParams();
		mParam.width = label_width*3/4;
		mParam.height = c_height;
		mParam.topMargin = label_topMargin;
		
		mParam =  (RelativeLayout.LayoutParams)month4.getLayoutParams();
		mParam.height = c_height;
		mParam.topMargin = label_topMargin;
		
		for (int i=0;i<nBlocks;++i){
			LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) time_labels[i].getLayoutParams();
			param.width = c_width;
			param.height = height;
			if (!TimeBlock.hasBlock(i, timeblock_type))
				param.width = param.height = 0;
		}
		
		int b_width =  (int) (screen.x * 18.0/720.0);
		int b_gap =  (int) (screen.x * 10.0/720.0);
		for (int i=0;i<nBlocks*nDate;++i){
			GridLayout.LayoutParams cParam = (GridLayout.LayoutParams) circles[i].getLayoutParams();
			cParam.width = b_width;
			cParam.height = height;
			if (!TimeBlock.hasBlock(i/nDate, timeblock_type))
				cParam.width = cParam.height = 0;
			if (i%7==6)
				cParam.rightMargin = b_gap;
		}
		
		RelativeLayout.LayoutParams lParam = (RelativeLayout.LayoutParams) labelLayout.getLayoutParams();
		lParam.topMargin = (int)(screen.x*20.0/720.0);
		for (int i=0;i<3;++i){
			LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) labels[i].getLayoutParams();
			param.width = (int)(c_width*1.5);
			param.height = c_height;
			param = (LinearLayout.LayoutParams) labelImgs[i].getLayoutParams();
			param.width = c_width;
			param.height = c_height;
		}
	}


	@Override
	public void onPostTask() {
		
		int cur_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		
		BracGameHistory[] historys = db.getMultiDayInfo(nDate);
		for (int i=0;i<historys.length;++i){
			int idx = (i%nBlocks)*nDate + i/nBlocks;
			if (!TimeBlock.hasBlock(i%4, timeblock_type)){
				circles[idx].setImageBitmap(circleBmps[0]);
				circles[idx].setAlpha(0.0F);
				continue;
			}
			if (historys[i] == null){
				if (i < historys.length - nBlocks)
					circles[idx].setImageBitmap(circleBmps[0]);
				else
					if (TimeBlock.isEmpty(i%nBlocks, cur_hour)){
						circles[idx].setImageBitmap(circleBmps[3]);
						circles[idx].setAlpha(0.1F);
					}
					else
						circles[idx].setImageBitmap(circleBmps[0]);
			}
			else if (historys[i].brac < BracDataHandler.THRESHOLD)
				circles[idx].setImageBitmap(circleBmps[2]);
			else
				circles[idx].setImageBitmap(circleBmps[1]);
			
		}
		
		Calendar cal = Calendar.getInstance();
		final long Millis6 = 86400*1000L*6;
		final long Millis7 = 86400*1000L*7;
		
		int month = cal.get(Calendar.MONTH)+1;
		int date = cal.get(Calendar.DATE);
		String month_label = month+"/"+date;
		month4.setText("");

		cal.setTimeInMillis(cal.getTimeInMillis()-Millis6);
		month = cal.get(Calendar.MONTH)+1;
		date = cal.get(Calendar.DATE);
		month_label = month+"/"+date;
		month3.setText(month_label);
		cal.setTimeInMillis(cal.getTimeInMillis()-Millis7);
		month = cal.get(Calendar.MONTH)+1;
		date = cal.get(Calendar.DATE);
		month_label = month+"/"+date;
		month2.setText(month_label);
		cal.setTimeInMillis(cal.getTimeInMillis()-Millis7);
		month = cal.get(Calendar.MONTH)+1;
		date = cal.get(Calendar.DATE);
		month_label = month+"/"+date;
		month1.setText(month_label);
		cal.setTimeInMillis(cal.getTimeInMillis()-Millis7);
		month = cal.get(Calendar.MONTH)+1;
		date = cal.get(Calendar.DATE);
		month_label = month+"/"+date;
		month0.setText(month_label);
		
		labelImgs[0].setImageBitmap(circleBmps[2]);
		labelImgs[1].setImageBitmap(circleBmps[1]);
		labelImgs[2].setImageBitmap(circleBmps[0]);
		
		if (StatisticPagerAdapter.background!=null && !StatisticPagerAdapter.background.isRecycled())
			bg.setImageBitmap(StatisticPagerAdapter.background);
		
	}


	@Override
	public void onCancel() {
		clear();
	}
	
	
}
