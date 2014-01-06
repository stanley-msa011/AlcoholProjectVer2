package statistic.ui.statistic_page_view;


import java.util.Calendar;

import data.calculate.TimeBlock;
import data.database.HistoryDB;
import data.info.BracDetectionState;
import test.data.BracDataHandler;
import ubicomp.drunk_detection.activities.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import ubicomp.drunk_detection.fragments.StatisticFragment;
import ubicomp.drunk_detection.ui.TextSize;
import ubicomp.drunk_detection.ui.Typefaces;

public class StatisticWeekView extends StatisticPageView {

	private HistoryDB db;
	private TextView[] time_labels;
	private TextView[] date_labels;
	private Drawable[] circleDrawables;
	private ImageView[] circles;

	private LinearLayout dateLayout;
	private LinearLayout timeLayout;
	private GridLayout blockLayout;
	private LinearLayout labelLayout;
	
	private TextView[] labels;
	private ImageView[] labelImgs;
	
	private TextView title;
	
	private static final int nBlocks = 3;
	private static final int nDate = 7;
	
	private static final int[] blockHint = {R.string.morning_short,R.string.noon_short,R.string.night_short};
	private static final int[] labelHint = {R.string.test_pass,R.string.test_fail,R.string.test_none}; 
	
	private Typeface digitTypefaceBold;
	private Typeface wordTypefaceBold;
	
	private Calendar startDate;
	
	private static final float ALPHA = 0.4F;
	
	public StatisticWeekView(Context context,StatisticFragment statisticFragment) {
		super(context, R.layout.statistic_week_view, statisticFragment);
		db = new HistoryDB(context);
		digitTypefaceBold = Typefaces.getDigitTypefaceBold(context);
		wordTypefaceBold = Typefaces.getWordTypefaceBold(context);
		startDate = Calendar.getInstance();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		int mYear = sp.getInt("sYear", startDate.get(Calendar.YEAR));
	    int mMonth = sp.getInt("sMonth", startDate.get(Calendar.MONTH));
	    int mDay = sp.getInt("sDate", startDate.get(Calendar.DATE));
	    startDate.set(mYear, mMonth, mDay, 0, 0, 0);
	}


	@Override
	public void clear() {
	}
	
	@Override
	public void onPreTask() {
		dateLayout = (LinearLayout) view.findViewById(R.id.statistic_week_date_label_layout);
		timeLayout = (LinearLayout) view.findViewById(R.id.statistic_week_timeblock_label_layout);
		blockLayout = (GridLayout) view.findViewById(R.id.statistic_week_block_layout);
		
		int titleSize = TextSize.titleSize(context);
		int textSize = TextSize.normalTextSize(context);
		title= (TextView) view.findViewById(R.id.statistic_week_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize);
		title.setTypeface(wordTypefaceBold);
		
		time_labels = new TextView[nBlocks];
		for (int i=0;i<nBlocks;++i){
			time_labels[i] = new TextView(context);
			time_labels[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			time_labels[i].setTextColor(0xFF727171);
			time_labels[i].setText(blockHint[i]);
			time_labels[i].setTypeface(wordTypefaceBold);
			time_labels[i].setGravity(Gravity.CENTER);
			timeLayout.addView(time_labels[i]);
		}

		
		date_labels = new TextView[nDate];
		for (int i=0;i<nDate;++i){
			date_labels[i] = new TextView(context);
			date_labels[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			date_labels[i].setTextColor(0xFF727171);
			date_labels[i].setGravity(Gravity.CENTER);
			date_labels[i].setTypeface(digitTypefaceBold);
			dateLayout.addView(date_labels[i]);
		}
		
		circles = new ImageView[nBlocks*nDate];
		
		for (int i=0;i<nBlocks*nDate;++i){
			circles[i] = new ImageView(context);
			blockLayout.addView(circles[i]);
			circles[i].setScaleType(ScaleType.CENTER);
		}
		
		labelLayout = (LinearLayout) view.findViewById(R.id.statistic_week_label_layout);
		labels = new TextView[3];
		labelImgs = new ImageView[3];
		for (int i=0;i<3;++i){
			labelImgs[i] = new ImageView(context);
			labelImgs[i].setScaleType(ScaleType.CENTER);
			labelLayout.addView(labelImgs[i]);
			labels[i] = new TextView(context);
			labels[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			labels[i].setTextColor(0xFF727171);
			labels[i].setGravity(Gravity.CENTER);
			labels[i].setTypeface(wordTypefaceBold);
			labels[i].setText(labelHint[i]);
			labelLayout.addView(labels[i]);
		}
	}


	@Override
	public void onInBackground() {
		
		circleDrawables = new Drawable[3];
		circleDrawables[0] = context.getResources().getDrawable(R.drawable.statistic_week_none);
		circleDrawables[1] = context.getResources().getDrawable(R.drawable.statistic_week_fail);
		circleDrawables[2] = context.getResources().getDrawable(R.drawable.statistic_week_pass);
		
		Point screen = StatisticFragment.getStatisticPx();
		
		RelativeLayout.LayoutParams titleParam = (RelativeLayout.LayoutParams) title.getLayoutParams();
		titleParam.topMargin =  screen.x * 54/480;
		titleParam.leftMargin =  screen.x * 89/480;
		
		RelativeLayout.LayoutParams tParam = (RelativeLayout.LayoutParams) timeLayout.getLayoutParams();
		tParam.leftMargin =  screen.x * 29/480;
		tParam.topMargin =  screen.x * 19/480;
		
		RelativeLayout.LayoutParams bParam = (RelativeLayout.LayoutParams) blockLayout.getLayoutParams();
		bParam.topMargin =  screen.x * 19/480;
		
		int c_width =  screen.x * 52/480;
		int c_height =  screen.x * 48/480;
		
		for (int i=0;i<nBlocks;++i){
			LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) time_labels[i].getLayoutParams();
			param.width = c_width;
			param.height = c_height;
		}
		
		for (int i=0;i<nDate;++i){
			LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) date_labels[i].getLayoutParams();
			param.width = c_width;
			param.height = c_height;
		}
		
		for (int i=0;i<nBlocks*nDate;++i){
			GridLayout.LayoutParams cParam = (GridLayout.LayoutParams) circles[i].getLayoutParams();
			cParam.width = c_width;
			cParam.height = c_height;
		}
		
		for (int i=0;i<3;++i){
			LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) labels[i].getLayoutParams();
			param.width = c_width*5/4;
			param.height = c_height;
			param = (LinearLayout.LayoutParams) labelImgs[i].getLayoutParams();
			param.width = c_width;
			param.height = c_height;
		}
	}


	@Override
	public void onPostTask() {
		
		BracDetectionState[] historys = db.getMultiDayInfo(nDate);
		
		int cur_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		
		for (int i=0;i<historys.length;++i){
			int idx = (i%nBlocks)*nDate + i/nBlocks;
			if (historys[i] == null){
				circles[idx].setImageDrawable(circleDrawables[0]);
				if (i >= historys.length - nBlocks && TimeBlock.isEmpty(i%nBlocks, cur_hour))
						circles[idx].setAlpha(ALPHA);
			}
			else if (historys[i].brac < BracDataHandler.THRESHOLD)
				circles[idx].setImageDrawable(circleDrawables[2]);
			else
				circles[idx].setImageDrawable(circleDrawables[1]);
		}
		
		Calendar cal = Calendar.getInstance();
		for (int i=6;i>0;--i){
			int date = cal.get(Calendar.DAY_OF_MONTH);
			String label = String.valueOf(date);
			date_labels[i].setText(label);
			if (cal.before(startDate)){
				int max = i+21;
				for (int j=i;j<max;j+=7){
					circles[j].setAlpha(ALPHA);
				}
			}
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		
		if (cal.before(startDate)){
			for (int j=0;j<21;j+=7){
				circles[j].setAlpha(ALPHA);
			}
		}
		
		int month = cal.get(Calendar.MONTH)+1;
		int date = cal.get(Calendar.DAY_OF_MONTH);
		String month_label = month+"/"+date;
		date_labels[0].setText(month_label);
		
		labelImgs[0].setImageDrawable(circleDrawables[2]);
		labelImgs[1].setImageDrawable(circleDrawables[1]);
		labelImgs[2].setImageDrawable(circleDrawables[0]);
		
	}


	@Override
	public void onCancel() {
		clear();
	}
	
	
}
