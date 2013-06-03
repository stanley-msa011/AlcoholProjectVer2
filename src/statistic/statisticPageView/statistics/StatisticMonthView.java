package statistic.statisticPageView.statistics;


import java.util.Calendar;

import database.HistoryDB;
import database.TimeBlock;
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
import statistic.statisticPageView.StatisticPageView;
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
	
	private TextView month0, month1, month2, month3;
	
	private TextView title;
	
	private static final int nBlocks = 4;
	private static final int nDate = 28;
	
	private static final String[] blockHint = {"早","中","下","晚"};
	private static final String[] labelHint = {"通過","不通過","未測試"}; 
	
	private int timeblock_type;
	
	private Typeface digitTypeface;
	private Typeface wordTypeface;
	private Typeface wordTypefaceBold;
	
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
		wordTypefaceBold = Typeface.createFromAsset(context.getAssets(), "fonts/dfheistd-w5.otf");
		
		timeLayout = (LinearLayout) view.findViewById(R.id.statistic_month_timeblock_label_layout);
		blockLayout = (GridLayout) view.findViewById(R.id.statistic_month_block_layout);
		
		int textSize = screen.x * 72/1080;
		int textSize2 = screen.x * 54/1080;
		title= (TextView) view.findViewById(R.id.statistic_month_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		title.setTypeface(wordTypefaceBold);
		
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
		
		BitmapFactory.Options opt = new BitmapFactory.Options();
    	opt.inSampleSize = 2;
		
		circleBmps = new Bitmap[4];
		int circleWidth = screen.x *16/1080;
		int circleHeight = screen.x *62/1080;
		Bitmap[] tmp = new Bitmap[4];
		tmp[0] = BitmapFactory.decodeResource(context.getResources(),R.drawable.statistic_month_none,opt);
		circleBmps[0] = Bitmap.createScaledBitmap(tmp[0], circleWidth, circleHeight, true);
		tmp[1] = BitmapFactory.decodeResource(context.getResources(),R.drawable.statistic_month_fail,opt);
		circleBmps[1] = Bitmap.createScaledBitmap(tmp[1], circleWidth, circleHeight, true);
		tmp[2] = BitmapFactory.decodeResource(context.getResources(),R.drawable.statistic_month_pass,opt);
		circleBmps[2] = Bitmap.createScaledBitmap(tmp[2], circleWidth, circleHeight, true);
		for (int i=0;i<tmp.length;++i)
			if (circleBmps[i]!=tmp[i])
				tmp[i].recycle();
		
		
		
		RelativeLayout.LayoutParams tParam = (RelativeLayout.LayoutParams) timeLayout.getLayoutParams();
		tParam.leftMargin =  screen.x * 60/1080;
		tParam.topMargin =  screen.x * 30/1080;
		
		RelativeLayout.LayoutParams bParam = (RelativeLayout.LayoutParams) blockLayout.getLayoutParams();
		bParam.topMargin =  screen.x * 30/1080;
		
		RelativeLayout.LayoutParams titleParam = (RelativeLayout.LayoutParams) title.getLayoutParams();
		titleParam.topMargin =  screen.x * 240/1080;
		titleParam.leftMargin =  screen.x * 190/1080;
		
		int c_width =  screen.x * 113/1080;
		int c_height = screen.x * 75/1080;
		
		int height = (c_height*4)/timeblock_type;
		
		int label_topMargin = screen.x * 30/1080;
		int label_width =  screen.x*204/1080;
		
		RelativeLayout.LayoutParams mParam =  (RelativeLayout.LayoutParams)month0.getLayoutParams();
		mParam.width = label_width;
		mParam.height = c_height;
		mParam.topMargin = label_topMargin;
		mParam.leftMargin = screen.x*165/1080;
		
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
		
		
		for (int i=0;i<nBlocks;++i){
			LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) time_labels[i].getLayoutParams();
			param.width = c_width;
			param.height = height;
			if (!TimeBlock.hasBlock(i, timeblock_type))
				param.width = param.height = 0;
		}
		
		int b_width =  screen.x*27/1080;
		int b_gap =  screen.x*15/1080;
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
		lParam.topMargin = screen.x*30/1080;
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
						circles[idx].setImageBitmap(circleBmps[0]);
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
		
		
	}


	@Override
	public void onCancel() {
		clear();
	}
	
	
}
