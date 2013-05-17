package statisticPageView.statistics;

import java.text.DecimalFormat;
import java.util.Calendar;

import database.HistoryDB;
import database.TimeBlock;

import main.activities.FragmentTabs;
import main.activities.Lang;
import main.activities.R;
import main.activities.StatisticFragment;
import statisticPageView.StatisticPageView;
import test.data.BracDataHandler;
import history.BracGameHistory;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class StatisticDayView extends StatisticPageView {
	
	private TextView bracValue,bracTime,bracHelp,bracTitle;
	private HistoryDB db;
	private ImageView bg;
	private ImageView valueBg,valueCircle;
	private Bitmap valueBgBmp,valueCircleBmp;
	private ImageView[] circleImages;
	private Bitmap[] circleBmps;
	private TextView[] circleValues;
	private TextView[] circleTexts;
	private DecimalFormat format;
	private RelativeLayout valueLayout;
	private LinearLayout blockLayout;
	
	private String[] blockHint = {"早","中","下","晚"};
	
	private static final int nBlocks = 4;
	private int timeblock_type;
	
	public StatisticDayView(Context context,StatisticFragment statisticFragment){
		super(context,R.layout.statistic_day_view,statisticFragment);
		db = new HistoryDB(context);
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		timeblock_type = sp.getInt("timeblock_num", 2);
	}
	
		@Override
	public void clear() {
		if(valueBgBmp !=null && !valueBgBmp.isRecycled()){
			valueBgBmp.recycle();
			valueBgBmp = null;
		} 
		if (valueCircleBmp !=null && !valueCircleBmp.isRecycled()){
			valueCircleBmp.recycle();
			valueCircleBmp = null;
		}
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


		
	private float brac;
	private long brac_time;
    private String output;
	@Override
	public void onPreTask() {
		Point statistic_size = StatisticFragment.getStatisticPx();
		
		bracTitle = (TextView) view.findViewById(R.id.statistic_day_title);
		bracTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,  (int) (statistic_size.x * 56.0/720.0));
		
		bracValue = (TextView) view.findViewById(R.id.statistic_day_brac_value);
		bracValue.setTextSize(TypedValue.COMPLEX_UNIT_PX,  (int) (statistic_size.x * 90.0/720.0));
		
		BracGameHistory history = db.getLatestBracGameHistory();

		brac = history.brac;
		brac_time = history.timestamp;
		
		bracTime = (TextView) view.findViewById(R.id.statistic_day_brac_time);
		bracTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (statistic_size.x * 42.0/720.0));
		bg = (ImageView) view.findViewById(R.id.statistic_day_brac_bg);
		
		circleImages = new ImageView[nBlocks];
		
		int textSize = (int)(statistic_size.x * 36.0/720.0);
		int textSize2 = (int)(statistic_size.x * 32.0/720.0);
		circleTexts =new TextView[nBlocks];
		circleValues = new TextView[nBlocks];
		for (int i=0;i<nBlocks;++i){
			circleImages[i] = new ImageView(context);
			circleTexts[i] = new TextView(context);
			circleValues[i] = new TextView(context); 
			
			circleTexts[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			circleTexts[i].setTextColor(0xFF000000);
			circleTexts[i].setText(blockHint[i]);
			
			circleValues[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize2);
			circleValues[i].setTextColor(0xFFFFFFFF);
		}
		bracHelp = (TextView) view.findViewById(R.id.statistic_day_brac);
		bracHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		
		valueBg = (ImageView) view.findViewById(R.id.statistic_day_value_bg);
		valueCircle = (ImageView) view.findViewById(R.id.statistic_day_value_circle);
		valueLayout = (RelativeLayout) view.findViewById(R.id.statistic_day_value_layout);
		blockLayout = (LinearLayout) view.findViewById(R.id.statistic_day_block_layout);
		
	}

	@Override
	public void onInBackground() {
		format = new DecimalFormat();
		format.setMaximumIntegerDigits(1);
		format.setMinimumIntegerDigits(1);
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);
		output =format.format(brac); 
		
		Point statistic_size = StatisticFragment.getStatisticPx();
		
		RelativeLayout.LayoutParams titleParam = (LayoutParams) bracTitle.getLayoutParams();
		titleParam.leftMargin = (int) (statistic_size.x*100.0/720.0);
		titleParam.topMargin = (int) (statistic_size.x*100.0/720.0);
		
		RelativeLayout.LayoutParams dateParam = (LayoutParams) bracTime.getLayoutParams();
		dateParam.leftMargin = (int) (statistic_size.x*100.0/720.0);
		dateParam.topMargin = (int) (statistic_size.x*40.0/720.0);
		
		int valueSize= (int) (statistic_size.x*302.0/720.0);
		RelativeLayout.LayoutParams vParam = (LayoutParams) valueLayout.getLayoutParams();
		vParam.leftMargin = (int) (statistic_size.x*30.0/720.0);
		vParam.topMargin = (int) (statistic_size.x*80.0/720.0);
		vParam.width =valueSize;
		vParam.height = valueSize;
		
		RelativeLayout.LayoutParams bParam = (LayoutParams) blockLayout.getLayoutParams();
		bParam.topMargin = (int) (statistic_size.x*60.0/720.0);
		
		Bitmap tmp;
		
		if (brac_time == 0)
			tmp = BitmapFactory.decodeResource(view.getResources(), R.drawable.statistic_day_main_circle_none);
		else if (brac < BracDataHandler.THRESHOLD)
			tmp = BitmapFactory.decodeResource(view.getResources(), R.drawable.statistic_day_main_circle_pass);
		else
			tmp = BitmapFactory.decodeResource(view.getResources(), R.drawable.statistic_day_main_circle_fail);
		
		valueCircleBmp = Bitmap.createScaledBitmap(tmp, (int) (statistic_size.x*250.0/720.0), (int) (statistic_size.x*250.0/720.0), true);
		tmp.recycle();
		
		tmp = BitmapFactory.decodeResource(view.getResources(), R.drawable.statistic_day_main_circle);
		valueBgBmp = Bitmap.createScaledBitmap(tmp,valueSize, valueSize, true);
		tmp.recycle();
		
		circleBmps = new Bitmap[3];
		int circleSize = (int)(statistic_size.x * 70.0/720.0);
		tmp= BitmapFactory.decodeResource(view.getResources(), R.drawable.statistic_day_circle_none);
		circleBmps[0] = Bitmap.createScaledBitmap(tmp, circleSize, circleSize, true);
		tmp.recycle();
		
		tmp= BitmapFactory.decodeResource(view.getResources(), R.drawable.statistic_day_circle_fail);
		circleBmps[1] = Bitmap.createScaledBitmap(tmp, circleSize, circleSize, true);
		tmp.recycle();
		
		tmp = BitmapFactory.decodeResource(view.getResources(), R.drawable.statistic_day_circle_pass);
		circleBmps[2] = Bitmap.createScaledBitmap(tmp, circleSize, circleSize, true);
		tmp.recycle();
		
	}

	@Override
	public void onPostTask() {
		bracValue.setText(output);
		
		String time;
		if (brac_time == 0){
			time = "尚未測試";
		}
		else{
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(brac_time*1000L);
			int month = cal.get(Calendar.MONTH)+1;
			int day = cal.get(Calendar.DATE);
			int hour = cal.get(Calendar.HOUR);
			int min = cal.get(Calendar.MINUTE);
			int am_pm = cal.get(Calendar.AM_PM);
			
			if (am_pm == Calendar.AM)
				time = month+" / "+day+"\n"+hour+":"+min+" A.M.";
			else
				time = month+" / "+day+"\n"+hour+":"+min+" P.M.";
		}

		bracTime.setText(time);
		
		if (StatisticPagerAdapter.background!=null && !StatisticPagerAdapter.background.isRecycled())
			bg.setImageBitmap(StatisticPagerAdapter.background);
		
		if (valueBgBmp !=null && !valueBgBmp.isRecycled())
			valueBg.setImageBitmap(valueBgBmp);
		
		if (valueCircleBmp !=null && !valueCircleBmp.isRecycled())
			valueCircle.setImageBitmap(valueCircleBmp);
		
		BracGameHistory[] historys = db.getTodayBracGameHistory();
		Point statistic_size = StatisticFragment.getStatisticPx();
		int circleSize = (int)(statistic_size.x * 70.0/720.0);
		int blockWidth = (int)(statistic_size.x * 20.0/720.0);
		for (int i =0; i<nBlocks; ++i){
			RelativeLayout lLayout = new RelativeLayout(context);
			
			RelativeLayout sLayout = new RelativeLayout(context);
			sLayout.addView(circleImages[i]);
			sLayout.addView(circleValues[i]);
			sLayout.setId(0x999);
			RelativeLayout.LayoutParams circleParam =(RelativeLayout.LayoutParams ) circleImages[i].getLayoutParams();
			circleParam.width =circleSize;
			circleParam.height =circleSize;
			circleParam.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
			RelativeLayout.LayoutParams valueParam =(RelativeLayout.LayoutParams ) circleValues[i].getLayoutParams();
			valueParam.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
			
			lLayout.addView(sLayout);
			lLayout.addView(circleTexts[i]);
			RelativeLayout.LayoutParams sParam =(RelativeLayout.LayoutParams ) sLayout.getLayoutParams();
			sParam.width=circleSize;
			sParam.height=circleSize;
			RelativeLayout.LayoutParams tParam =(RelativeLayout.LayoutParams ) circleTexts[i].getLayoutParams();
			tParam.addRule(RelativeLayout.RIGHT_OF,0x999);
			tParam.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			
			if (historys[i]==null){
				circleValues[i].setText("");
				circleImages[i].setImageBitmap(circleBmps[0] );
			}
			else{
				String value =format.format(historys[i].brac);
				circleValues[i].setText(value);
				if (historys[i].brac < BracDataHandler.THRESHOLD)
					circleImages[i].setImageBitmap(circleBmps[2] );
				else
					circleImages[i].setImageBitmap(circleBmps[1] );
			}
			
			
			blockLayout.addView(lLayout);
			
			LinearLayout.LayoutParams lParam =(LinearLayout.LayoutParams ) lLayout.getLayoutParams();
			lParam.leftMargin = blockWidth;
			lParam.rightMargin = blockWidth;
			if (!TimeBlock.hasBlock(i, timeblock_type))
				lLayout.setAlpha(0.0F);
		}
	}

	@Override
	public void onCancel() {
		clear();
		
	}
}
