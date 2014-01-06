package statistic.ui.statistic_page_view;


import java.util.Calendar;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.ImageView.ScaleType;
import android.widget.TableLayout;
import android.widget.TextView;
import ubicomp.drunk_detection.fragments.StatisticFragment;
import ubicomp.drunk_detection.ui.TextSize;
import ubicomp.drunk_detection.ui.Typefaces;

public class StatisticMonthViewOld extends StatisticPageView {

	private HistoryDB db;
	private TextView[] time_labels;
	private Drawable[] circleDrawables;
	private ImageView[] circles;

	private LinearLayout timeLayout;
	private TableLayout blockLayout;
	private LinearLayout labelLayout;
	
	private TextView[] labels;
	private ImageView[] labelImgs;
	
	private TextView month0, month1, month2, month3;
	
	private TextView title;
	
	private static final int nBlocks = 3;
	private static final int nDate = 28;
	
	private static final int[] blockHint = {R.string.morning_short,R.string.noon_short,R.string.night_short};
	private static final int[] labelHint = {R.string.test_pass,R.string.test_fail,R.string.test_none}; 
	
	private Typeface digitTypefaceBold;
	private Typeface wordTypefaceBold;
	
	private Calendar startDate;
	
	public StatisticMonthViewOld(Context context) {
		super(context, R.layout.statistic_month_view_old);
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
		timeLayout = (LinearLayout) view.findViewById(R.id.statistic_month_timeblock_label_layout);
		blockLayout = (TableLayout) view.findViewById(R.id.statistic_month_block_layout);
		
		int titleSize = TextSize.titleSize(context);
		int textSize = TextSize.normalTextSize(context);
		title= (TextView) view.findViewById(R.id.statistic_month_title);
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

		month0 = (TextView) view.findViewById(R.id.statistic_month_0);;
		month0.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		month0.setTypeface(digitTypefaceBold);
		month1 = (TextView) view.findViewById(R.id.statistic_month_1);;
		month1.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		month1.setTypeface(digitTypefaceBold);
		month2 = (TextView) view.findViewById(R.id.statistic_month_2);;
		month2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		month2.setTypeface(digitTypefaceBold);
		month3 = (TextView) view.findViewById(R.id.statistic_month_3);;
		month3.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		month3.setTypeface(digitTypefaceBold);
		
		circles = new ImageView[nBlocks*nDate];
		
		for (int i=0;i<nBlocks*nDate;++i){
			circles[i] = new ImageView(context);
			circles[i].setScaleType(ScaleType.CENTER);
		}
		
		for (int i=0;i<nBlocks;++i){
			TableRow tr = new TableRow(context);
			blockLayout.addView(tr);
			for (int j=0;j<nDate;++j){
				tr.addView(circles[i*nDate+j]);
			}
		}
		
		labelLayout = (LinearLayout) view.findViewById(R.id.statistic_month_label_layout);
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
			labels[i].setText(labelHint[i]);
			labels[i].setTypeface(wordTypefaceBold);
			labelLayout.addView(labels[i]);
		}
	}


	@Override
	public void onInBackground() {

		circleDrawables = new Drawable[3];
		circleDrawables[0] = context.getResources().getDrawable(R.drawable.statistic_month_none);
		circleDrawables[1] = context.getResources().getDrawable(R.drawable.statistic_month_fail);
		circleDrawables[2] = context.getResources().getDrawable(R.drawable.statistic_month_pass);
		
		Point screen = StatisticFragment.getStatisticPx();
		
		RelativeLayout.LayoutParams titleParam = (RelativeLayout.LayoutParams) title.getLayoutParams();
		titleParam.topMargin =  screen.x * 54/480;
		titleParam.leftMargin =  screen.x * 89/480;
		
		RelativeLayout.LayoutParams tParam = (RelativeLayout.LayoutParams) timeLayout.getLayoutParams();
		tParam.leftMargin =  screen.x * 29/480;
		tParam.topMargin =  screen.x * 19/480;
		
		RelativeLayout.LayoutParams bParam = (RelativeLayout.LayoutParams) blockLayout.getLayoutParams();
		bParam.topMargin =  screen.x * 19/480;
		bParam.leftMargin = screen.x * 20/480;
		
		int c_width =  screen.x * 52/480;
		int c_height =  screen.x * 48/480;
		
		int m_gap = screen.x * 32/480;
		
		RelativeLayout.LayoutParams mParam =  (RelativeLayout.LayoutParams)month0.getLayoutParams();
		mParam.width = c_width;
		mParam.height = c_height;
		mParam.rightMargin = m_gap;
		
		mParam =  (RelativeLayout.LayoutParams)month1.getLayoutParams();
		mParam.width = c_width;
		mParam.height = c_height;
		mParam.rightMargin = m_gap;
		
		mParam =  (RelativeLayout.LayoutParams)month2.getLayoutParams();
		mParam.width = c_width;
		mParam.height = c_height;
		mParam.rightMargin = m_gap;
		
		mParam =  (RelativeLayout.LayoutParams)month3.getLayoutParams();
		mParam.width = c_width;
		mParam.height =c_height;
		
		
		for (int i=0;i<nBlocks;++i){
			LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) time_labels[i].getLayoutParams();
			param.width = c_width;
			param.height = c_height;
		}
		
		int b_width =  screen.x*11/480;
		int b_gap =  screen.x*7/480;
		for (int i=0;i<nBlocks*nDate;++i){
			TableRow.LayoutParams cParam = (TableRow.LayoutParams) circles[i].getLayoutParams();
			cParam.width = b_width;
			cParam.height = c_height;
			if (i%7==6)
				cParam.rightMargin = b_gap;
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
		for (int i=0;i<historys.length;++i){
			int idx = (i%nBlocks)*nDate + i/nBlocks;
			if (historys[i] == null)
				circles[idx].setImageDrawable(circleDrawables[0]);
			else if (historys[i].brac < BracDataHandler.THRESHOLD)
				circles[idx].setImageDrawable(circleDrawables[2]);
			else
				circles[idx].setImageDrawable(circleDrawables[1]);
			
		}
		
		Calendar cal = Calendar.getInstance();
		
		int month, date;
		String month_label = "";

		cal.add(Calendar.DAY_OF_MONTH, -6);
		month = cal.get(Calendar.MONTH)+1;
		date = cal.get(Calendar.DATE);
		month_label = month+"/"+date;
		month3.setText(month_label);
		cal.add(Calendar.DAY_OF_MONTH, -7);
		month = cal.get(Calendar.MONTH)+1;
		date = cal.get(Calendar.DATE);
		month_label = month+"/"+date;
		month2.setText(month_label);
		cal.add(Calendar.DAY_OF_MONTH, -7);
		month = cal.get(Calendar.MONTH)+1;
		date = cal.get(Calendar.DATE);
		month_label = month+"/"+date;
		month1.setText(month_label);
		cal.add(Calendar.DAY_OF_MONTH, -7);
		month = cal.get(Calendar.MONTH)+1;
		date = cal.get(Calendar.DATE);
		month_label = month+"/"+date;
		month0.setText(month_label);
		
		labelImgs[0].setImageDrawable(circleDrawables[2]);
		labelImgs[1].setImageDrawable(circleDrawables[1]);
		labelImgs[2].setImageDrawable(circleDrawables[0]);
		
	}


	@Override
	public void onCancel() {
		clear();
	}
	
	
}
