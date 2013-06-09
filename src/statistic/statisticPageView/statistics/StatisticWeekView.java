package statistic.statisticPageView.statistics;


import java.util.Calendar;

import database.HistoryDB;
import database.TimeBlock;
import ubicomp.drunk_detection.activities.R;
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
import ubicomp.drunk_detection.activities.StatisticFragment;

public class StatisticWeekView extends StatisticPageView {

	private HistoryDB db;
	private TextView[] time_labels;
	private TextView[] date_labels;
	private Bitmap[] circleBmps;
	private ImageView[] circles;

	private LinearLayout dateLayout;
	private LinearLayout timeLayout;
	private GridLayout blockLayout;
	private LinearLayout labelLayout;
	
	private TextView[] labels;
	private ImageView[] labelImgs;
	
	private TextView monthText;
	
	private TextView title;
	
	private static final int nBlocks = 4;
	private static final int nDate = 7;
	
	private static final String[] blockHint = {"早","中","下","晚"};
	private static final String[] labelHint = {"通過","不通過","未測試"}; 
	
	private int timeblock_type;
	
	private Typeface digitTypeface;
	private Typeface wordTypeface;
	private Typeface wordTypefaceBold;
	
	public StatisticWeekView(Context context,StatisticFragment statisticFragment) {
		super(context, R.layout.statistic_week_view, statisticFragment);
		db = new HistoryDB(context);
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		timeblock_type = sp.getInt("timeblock_num", 3);
	}


	@Override
	public void clear() {
		Log.d("CLEAR","WEEK");
		if (circles!=null)
			for (int i=0;i<circles.length;++i)
				circles[i].setImageBitmap(null);
		if (labelImgs !=null)
			for (int i=0;i<labelImgs.length;++i)
				labelImgs[i].setImageBitmap(null);
		
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
		
		dateLayout = (LinearLayout) view.findViewById(R.id.statistic_week_date_label_layout);
		timeLayout = (LinearLayout) view.findViewById(R.id.statistic_week_timeblock_label_layout);
		blockLayout = (GridLayout) view.findViewById(R.id.statistic_week_block_layout);
		
		int textSize = screen.x * 72/1080;
		int textSize2 = screen.x * 54/1080;
		title= (TextView) view.findViewById(R.id.statistic_week_title);
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

		monthText = (TextView) view.findViewById(R.id.statistic_week_month);;
		monthText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize2);
		monthText.setTypeface(digitTypeface);
		
		date_labels = new TextView[nDate];
		for (int i=0;i<nDate;++i){
			date_labels[i] = new TextView(context);
			date_labels[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize2);
			date_labels[i].setTextColor(0xFF000000);
			date_labels[i].setGravity(Gravity.CENTER);
			date_labels[i].setTypeface(digitTypeface);
			if (Build.VERSION.SDK_INT>=17)
				date_labels[i].setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
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
			labels[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize2);
			labels[i].setTextColor(0xFF000000);
			labels[i].setGravity(Gravity.CENTER);
			labels[i].setTypeface(wordTypeface);
			if (Build.VERSION.SDK_INT>=17)
				labels[i].setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
			labels[i].setText(labelHint[i]);
			labelLayout.addView(labels[i]);
		}
	}


	@Override
	public void onInBackground() {
		
		Point screen = StatisticFragment.getStatisticPx();
		
		BitmapFactory.Options opt = new BitmapFactory.Options();
    	opt.inSampleSize = 2;
		
		circleBmps = new Bitmap[3];
		int circleSize = screen.x * 49/1080;
		Bitmap[] tmp = new Bitmap[3];
		tmp[0] = BitmapFactory.decodeResource(context.getResources(),R.drawable.statistic_week_none,opt);
		circleBmps[0] = Bitmap.createScaledBitmap(tmp[0], circleSize, circleSize, true);
		tmp[1] = BitmapFactory.decodeResource(context.getResources(),R.drawable.statistic_week_fail,opt);
		circleBmps[1] = Bitmap.createScaledBitmap(tmp[1], circleSize, circleSize, true);
		tmp[2] = BitmapFactory.decodeResource(context.getResources(),R.drawable.statistic_week_pass,opt);
		circleBmps[2] = Bitmap.createScaledBitmap(tmp[2], circleSize, circleSize, true);
		
		for (int i=0;i<tmp.length;++i)
			if (circleBmps[i]!=tmp[i])
				tmp[i].recycle();
		
		
		RelativeLayout.LayoutParams dParam = (RelativeLayout.LayoutParams) dateLayout.getLayoutParams();
		dParam.topMargin =  screen.x * 30/1080;
		
		RelativeLayout.LayoutParams tParam = (RelativeLayout.LayoutParams) timeLayout.getLayoutParams();
		tParam.leftMargin =  screen.x * 60/1080;
		tParam.topMargin =  screen.x * 30/1080;
		
		RelativeLayout.LayoutParams bParam = (RelativeLayout.LayoutParams) blockLayout.getLayoutParams();
		bParam.topMargin =  screen.x * 30/1080;
		
		RelativeLayout.LayoutParams titleParam = (RelativeLayout.LayoutParams) title.getLayoutParams();
		titleParam.topMargin =  screen.x * 240/1080;
		titleParam.leftMargin =  screen.x * 190/1080;
		
		int c_width =  (int) (screen.x * 75.0/720.0);
		int c_height =  (int) (screen.x * 50.0/720.0);
		
		RelativeLayout.LayoutParams mParam =  (RelativeLayout.LayoutParams)monthText.getLayoutParams();
		mParam.width = c_width;
		mParam.height = c_height;
		mParam.topMargin =  screen.x * 30/1080;
		mParam.leftMargin =  screen.x * 60/1080;
		
		for (int i=0;i<nDate;++i){
			LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) date_labels[i].getLayoutParams();
			param.width = c_width;
			param.height = c_height;
		}
		
		int height = (c_height*4)/timeblock_type;
		
		for (int i=0;i<nBlocks;++i){
			LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) time_labels[i].getLayoutParams();
			param.width = c_width;
			param.height = height;
			if (!TimeBlock.hasBlock(i, timeblock_type))
				param.width = param.height = 0;
		}
		
		for (int i=0;i<nBlocks*nDate;++i){
			GridLayout.LayoutParams cParam = (GridLayout.LayoutParams) circles[i].getLayoutParams();
			cParam.width = c_width;
			cParam.height = height;
			if (!TimeBlock.hasBlock(i/nDate, timeblock_type))
				cParam.width = cParam.height = 0;
		}
		
		RelativeLayout.LayoutParams lParam = (RelativeLayout.LayoutParams) labelLayout.getLayoutParams();
		lParam.topMargin =  screen.x * 30/1080;
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
		
		BracGameHistory[] historys = db.getMultiDayInfo(nDate);
		
		int cur_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		
		for (int i=0;i<historys.length;++i){
			int idx = (i%nBlocks)*nDate + i/nBlocks;
			if (!TimeBlock.hasBlock(i%nBlocks, timeblock_type)){
				circles[idx].setImageBitmap(circleBmps[0]);
				circles[idx].setAlpha(0.3F);
				continue;
			}
			if (historys[i] == null){
				if (i < historys.length - nBlocks)
					circles[idx].setImageBitmap(circleBmps[0]);
				else
					if (TimeBlock.isEmpty(i%nBlocks, cur_hour)){
						circles[idx].setImageBitmap(circleBmps[0]);
						circles[idx].setAlpha(0.3F);
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
		long dateMillis = 86400*1000L;
		for (int i=6;i>=0;--i){
			int date = cal.get(Calendar.DAY_OF_MONTH);
			String label = String.valueOf(date);
			date_labels[i].setText(label);
			Long t = cal.getTimeInMillis();
			t -= dateMillis;
			cal.setTimeInMillis(t);
		}
		
		int month = cal.get(Calendar.MONTH)+1;
		String month_label = month+"/";
		monthText.setText(month_label);
		
		labelImgs[0].setImageBitmap(circleBmps[2]);
		labelImgs[1].setImageBitmap(circleBmps[1]);
		labelImgs[2].setImageBitmap(circleBmps[0]);
		
	}


	@Override
	public void onCancel() {
		clear();
	}
	
	
}
