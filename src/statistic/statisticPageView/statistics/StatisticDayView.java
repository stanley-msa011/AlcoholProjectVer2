package statistic.statisticPageView.statistics;

import java.text.DecimalFormat;
import java.util.Calendar;

import data.history.BracDetectionState;
import database.HistoryDB;
import database.TimeBlock;

import test.data.BracDataHandler;
import ubicomp.drunk_detection.activities.R;
import statistic.statisticPageView.StatisticPageView;
import ubicomp.drunk_detection.activities.StatisticFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class StatisticDayView extends StatisticPageView {
	
	private TextView bracValue,bracTime,bracHelp,bracTitle;
	private HistoryDB db;
	private ImageView valueCircle;
	private Drawable valueCircleDrawable;
	private ImageView[] circleImages;
	private Drawable[] circleDrawables;
	private TextView[] circleValues;
	private TextView[] circleTexts;
	private DecimalFormat format;
	private RelativeLayout valueLayout;
	private LinearLayout blockLayout;
	
	private ImageView emotion,desire;
	private Bitmap emotionBmp, desireBmp;
	
	private int[] blockHint = {R.string.morning,R.string.noon,R.string.night};
	
	private static final int nBlocks = 3;
	
	private Typeface digitTypeface;
	private Typeface digitTypefaceBold;
	private Typeface wordTypeface;
	private Typeface wordTypefaceBold;
	
	private final static int[] emotionId = {
		R.drawable.msg_emotion_1,
		R.drawable.msg_emotion_2,
		R.drawable.msg_emotion_3,
		R.drawable.msg_emotion_4,
		R.drawable.msg_emotion_5,};
	
	private final static int[] desireId = {
		R.drawable.msg_desire_1,
		R.drawable.msg_desire_2,
		R.drawable.msg_desire_3,
		R.drawable.msg_desire_4,
		R.drawable.msg_desire_5,
		R.drawable.msg_desire_6,
		R.drawable.msg_desire_7,
		R.drawable.msg_desire_8,
		R.drawable.msg_desire_9,
		R.drawable.msg_desire_10,
	};
	
	private int e_idx, d_idx;
	public StatisticDayView(Context context,StatisticFragment statisticFragment){
		super(context,R.layout.statistic_day_view,statisticFragment);
		db = new HistoryDB(context);
	}
	
		@Override
	public void clear() {
		if (emotion != null)
			emotion.setImageBitmap(null);
		if (desire != null)
			desire.setImageBitmap(null);
		
		
		if (emotionBmp != null && !emotionBmp.isRecycled()){
			emotionBmp.recycle();
			emotionBmp = null;
		}
		if (desireBmp != null && !desireBmp.isRecycled()){
			desireBmp.recycle();
			desireBmp = null;
		}
	}


		
	private float brac;
	private long brac_time;
    private String output;
	@Override
	public void onPreTask() {
		Point statistic_size = StatisticFragment.getStatisticPx();
		
		digitTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dinproregular.ttf");
		digitTypefaceBold = Typeface.createFromAsset(context.getAssets(), "fonts/dinpromedium.ttf");
		wordTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/DFLiHeiStd-W3.otf");
		wordTypefaceBold = Typeface.createFromAsset(context.getAssets(), "fonts/DFLiHeiStd-W5.otf");
		
		bracTitle = (TextView) view.findViewById(R.id.statistic_day_title);
		bracTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,  statistic_size.x * 32/480);
		bracTitle.setTypeface(wordTypefaceBold);
		
		bracValue = (TextView) view.findViewById(R.id.statistic_day_brac_value);
		bracValue.setTextSize(TypedValue.COMPLEX_UNIT_PX,  statistic_size.x * 64/480);
		bracValue.setTypeface(digitTypefaceBold);
		
		BracDetectionState history = db.getLatestBracDetection();

		brac = history.brac;
		brac_time = history.timestamp;
		e_idx = history.emotion-1;
		d_idx = history.desire - 1;
		
		bracTime = (TextView) view.findViewById(R.id.statistic_day_brac_time);
		bracTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, statistic_size.x * 29/480);
		bracTime.setTypeface(wordTypeface);
		
		circleImages = new ImageView[nBlocks];
		
		int textSize = statistic_size.x * 21/480;
		int textSize2 = statistic_size.x * 20/480;
		circleTexts =new TextView[nBlocks];
		circleValues = new TextView[nBlocks];
		for (int i=0;i<nBlocks;++i){
			circleImages[i] = new ImageView(context);
			circleTexts[i] = new TextView(context);
			circleValues[i] = new TextView(context); 
			
			circleTexts[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize2);
			circleTexts[i].setTextColor(0xFF727171);
			circleTexts[i].setText(blockHint[i]);
			circleTexts[i].setGravity(Gravity.CENTER);
			circleTexts[i].setTypeface(wordTypefaceBold);
			
			circleValues[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize2);
			circleValues[i].setTextColor(0xFFFFFFFF);
			circleValues[i].setTypeface(digitTypeface);
		}
		bracHelp = (TextView) view.findViewById(R.id.statistic_day_brac);
		bracHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		bracHelp.setTypeface(wordTypefaceBold);
		
		valueCircle = (ImageView) view.findViewById(R.id.statistic_day_value_circle);
		valueLayout = (RelativeLayout) view.findViewById(R.id.statistic_day_value_layout);
		blockLayout = (LinearLayout) view.findViewById(R.id.statistic_day_block_layout);
		
		emotion = (ImageView) view.findViewById(R.id.statistic_day_emotion);
		desire = (ImageView) view.findViewById(R.id.statistic_day_desire);
	}

	@Override
	public void onInBackground() {
		format = new DecimalFormat();
		format.setMaximumIntegerDigits(3);
		format.setMinimumIntegerDigits(1);
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);
		if (brac < BracDataHandler.THRESHOLD)
			output = "0.00";
		else
			output =format.format(brac); 
		
		Point screen = StatisticFragment.getStatisticPx();
		
		RelativeLayout.LayoutParams titleParam = (LayoutParams) bracTitle.getLayoutParams();
		titleParam.leftMargin = screen.x * 40/480;
		titleParam.topMargin = screen.x * 69/480;
		
		RelativeLayout.LayoutParams dateParam = (LayoutParams) bracTime.getLayoutParams();
		dateParam.leftMargin = screen.x * 40/480;
		dateParam.topMargin = screen.x * 23/480;

		RelativeLayout.LayoutParams vParam = (LayoutParams) valueLayout.getLayoutParams();
		vParam.rightMargin =screen.x * 43/480;
		vParam.topMargin = screen.x * 54/480;
		vParam.width = vParam.height = screen.x * 202/480;
		
		RelativeLayout.LayoutParams bParam = (LayoutParams) blockLayout.getLayoutParams();
		bParam.topMargin = screen.x * 18/480;
		
		RelativeLayout.LayoutParams hParam = (LayoutParams) bracHelp.getLayoutParams();
		hParam.topMargin = screen.x * 140/480;
		
		int icon_size = screen.x*60/480;
		RelativeLayout.LayoutParams emotionParam = (LayoutParams) emotion.getLayoutParams();
		emotionParam.topMargin = screen.x * 23/480;
		emotionParam.width = emotionParam.height = icon_size;
		emotionParam.leftMargin = screen.x * 40/480;
		
		RelativeLayout.LayoutParams desireParam = (LayoutParams) desire.getLayoutParams();
		desireParam.topMargin = screen.x * 23/480;
		desireParam.width = emotionParam.height = icon_size;
		desireParam.leftMargin = screen.x * 10/480;
		
		
		if (brac_time == 0)
			valueCircleDrawable = view.getResources().getDrawable(R.drawable.statistic_day_main_circle_none);
		else if (brac < BracDataHandler.THRESHOLD)
			valueCircleDrawable = view.getResources().getDrawable(R.drawable.statistic_day_main_circle_pass);
		else
			valueCircleDrawable = view.getResources().getDrawable(R.drawable.statistic_day_main_circle_fail);
		
		
		circleDrawables = new Drawable[3];
		circleDrawables[0] = view.getResources().getDrawable(R.drawable.statistic_day_circle_none);
		circleDrawables[1] = view.getResources().getDrawable(R.drawable.statistic_day_circle_fail);		
		circleDrawables[2] = view.getResources().getDrawable(R.drawable.statistic_day_circle_pass);		
		
		
		Bitmap tmp;
		if (e_idx >=0){
			tmp= BitmapFactory.decodeResource(view.getResources(), emotionId[e_idx]);
			emotionBmp = Bitmap.createScaledBitmap(tmp,  icon_size, icon_size, true);
			tmp.recycle();
		}
		if (d_idx >=0){
			tmp= BitmapFactory.decodeResource(view.getResources(), desireId[d_idx]);
			desireBmp = Bitmap.createScaledBitmap(tmp,  icon_size, icon_size, true);
			tmp.recycle();
		}
		
	}

	@Override
	public void onPostTask() {
		
		valueCircle.setImageDrawable(valueCircleDrawable);
		bracValue.setText(output);
		
		String time;
		if (brac_time == 0){
			bracTime.setText(R.string.today_test_none);
		}
		else{
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(brac_time);
			int month = cal.get(Calendar.MONTH)+1;
			int day = cal.get(Calendar.DATE);
			int hour = cal.get(Calendar.HOUR);
			int min = cal.get(Calendar.MINUTE);
			int am_pm = cal.get(Calendar.AM_PM);
			String min_str;
			if (min < 10)
				min_str = "0"+String.valueOf(min);
			else
				min_str = String.valueOf(min);
			
			String m_text = context.getResources().getString(R.string.month);
			String d_text = context.getResources().getString(R.string.day);
			
			if (am_pm == Calendar.AM)
				time = month+m_text+day+d_text+"\n"+hour+":"+min_str+" A.M";
			else
				time = month+m_text+day+d_text+"\n"+hour+":"+min_str+" P.M";
			bracTime.setText(time);
		}

		
		
		if (emotionBmp !=null && !emotionBmp.isRecycled())
			emotion.setImageBitmap(emotionBmp);
		
		if (desireBmp !=null && !desireBmp.isRecycled())
			desire.setImageBitmap(desireBmp);
		
		BracDetectionState[] historys = db.getTodayBracState();
		Point statistic_size = StatisticFragment.getStatisticPx();
		int blockMargin = statistic_size.x * 6/480;
		
		int cur_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		
		for (int i =0; i<nBlocks; ++i){
			RelativeLayout lLayout = new RelativeLayout(context);
			
			RelativeLayout sLayout = new RelativeLayout(context);
			sLayout.addView(circleImages[i]);
			sLayout.addView(circleValues[i]);
			sLayout.setId(0x999);
			RelativeLayout.LayoutParams circleParam =(RelativeLayout.LayoutParams ) circleImages[i].getLayoutParams();
			circleParam.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
			RelativeLayout.LayoutParams valueParam =(RelativeLayout.LayoutParams ) circleValues[i].getLayoutParams();
			valueParam.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
			
			lLayout.addView(sLayout);
			lLayout.addView(circleTexts[i]);
			
			RelativeLayout.LayoutParams tParam =(RelativeLayout.LayoutParams ) circleTexts[i].getLayoutParams();
			tParam.addRule(RelativeLayout.RIGHT_OF,0x999);
			tParam.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			
			if (historys[i]==null){
				circleValues[i].setText("");
				circleImages[i].setImageDrawable(circleDrawables[0]);
				if (TimeBlock.isEmpty(i, cur_hour))
					circleImages[i].setAlpha(0.5F);
			}
			else{
				String value =format.format(historys[i].brac);
				
				if (historys[i].brac < BracDataHandler.THRESHOLD){
					circleImages[i].setImageDrawable(circleDrawables[2]);
					circleValues[i].setText("0.00");
				}
				else{
					circleImages[i].setImageDrawable(circleDrawables[1]);
					circleValues[i].setText(value);
				}
			}
			
			
			blockLayout.addView(lLayout);
			
			LinearLayout.LayoutParams lParam =(LinearLayout.LayoutParams ) lLayout.getLayoutParams();
			lParam.leftMargin = lParam.rightMargin = blockMargin;
		}
	}

	@Override
	public void onCancel() {
		clear();
	}
}
