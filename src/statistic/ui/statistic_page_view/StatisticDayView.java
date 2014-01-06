package statistic.ui.statistic_page_view;

import java.text.DecimalFormat;
import java.util.Calendar;

import data.calculate.TimeBlock;
import data.database.HistoryDB;
import data.info.BracDetectionState;

import test.data.BracDataHandler;
import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.fragments.StatisticFragment;
import ubicomp.drunk_detection.ui.CustomTypefaceSpan;
import ubicomp.drunk_detection.ui.TextSize;
import ubicomp.drunk_detection.ui.Typefaces;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
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
	private Drawable emotionDrawable, desireDrawable;
	
	private static final int[] blockHint = {R.string.morning,R.string.noon,R.string.night,R.string.morning_time,R.string.noon_time,R.string.night_time};
	private String[] blockHintStr = new String[3];
	private String[] blockHintTime = new String[3];
	
	
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
		digitTypeface = Typefaces.getDigitTypeface(context);
		digitTypefaceBold = Typefaces.getDigitTypefaceBold(context);
		wordTypeface = Typefaces.getWordTypeface(context);
		wordTypefaceBold = Typefaces.getWordTypefaceBold(context);
		for (int i=0;i<3;++i){
			blockHintStr[i] = context.getString(blockHint[i]); 
			blockHintTime[i] = context.getString(blockHint[i+3]); 
		}
	}
	
		@Override
	public void clear() {
		if (emotion != null)
			emotion.setImageDrawable(null);
		if (desire != null)
			desire.setImageDrawable(null);
	}


		
	private float brac;
	private long brac_time;
    private String output;
	@Override
	public void onPreTask() {
		Point statistic_size = StatisticFragment.getStatisticPx();
		
		bracTitle = (TextView) view.findViewById(R.id.statistic_day_title);
		bracTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,  TextSize.largeTitleSize(context));
		bracTitle.setTypeface(wordTypefaceBold);
		
		bracValue = (TextView) view.findViewById(R.id.statistic_day_brac_value);
		bracValue.setTextSize(TypedValue.COMPLEX_UNIT_PX,  TextSize.xlargeTextSize(context));
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
		
		int textSize = TextSize.normalTextSize(context);
		circleTexts =new TextView[nBlocks];
		circleValues = new TextView[nBlocks];
		for (int i=0;i<nBlocks;++i){
			circleImages[i] = new ImageView(context);
			circleTexts[i] = new TextView(context);
			circleValues[i] = new TextView(context); 
			
			circleTexts[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			Spannable s = new SpannableString(blockHintStr[i]+"\n"+blockHintTime[i]);
			int start = 0;
			int end = blockHintStr[i].length()+1;
			s.setSpan(new CustomTypefaceSpan("custom1",wordTypefaceBold,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			start = end;
			end = start + blockHintTime[i].length();
			s.setSpan(new CustomTypefaceSpan("custom2",digitTypefaceBold,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			circleTexts[i].setText(s);
			circleTexts[i].setGravity(Gravity.CENTER);
			circleTexts[i].setTypeface(wordTypefaceBold);
			
			circleValues[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
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
		emotionParam.topMargin = screen.x * 10/480;
		emotionParam.width = emotionParam.height = icon_size;
		emotionParam.leftMargin = screen.x * 40/480;
		
		RelativeLayout.LayoutParams desireParam = (LayoutParams) desire.getLayoutParams();
		desireParam.topMargin = screen.x * 10/480;
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
		
		
		if (e_idx >=0)
			emotionDrawable = view.getResources().getDrawable(emotionId[e_idx]);
		if (d_idx >=0)
			desireDrawable = view.getResources().getDrawable(desireId[d_idx]);
		
	}

	@Override
	public void onPostTask() {
		
		valueCircle.setImageDrawable(valueCircleDrawable);
		bracValue.setText(output);
		
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
			
			String m_text = context.getString(R.string.month);
			String d_text = context.getString(R.string.day);
			
			String month_str = String.valueOf(month);
			String day_str = String.valueOf(day);
			
			String ampm = null;
			
			if (am_pm == Calendar.AM)
				ampm= " A.M.";
			else{
				ampm = "P.M.";
				if (hour == 0)
					hour = 12;
			}
			String time_str = hour+":"+min_str+ampm;
			
			Spannable s = new SpannableString(month_str+m_text+day_str+d_text+"\n"+time_str);
			int start = 0;
			int end = month_str.length();
			s.setSpan(new CustomTypefaceSpan("c1",digitTypeface,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			start = end;
			end = start+m_text.length();
			s.setSpan(new CustomTypefaceSpan("c2",wordTypeface,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			start = end;
			end = start+day_str.length();
			s.setSpan(new CustomTypefaceSpan("c1",digitTypeface,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			start = end;
			end = start+d_text.length()+1;
			s.setSpan(new CustomTypefaceSpan("c2",wordTypeface,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			start = end;
			end = start+time_str.length();
			s.setSpan(new CustomTypefaceSpan("c1",digitTypeface,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			bracTime.setText(s);
		}

		
		
		if (emotionDrawable !=null)
			emotion.setImageDrawable(emotionDrawable);
		
		if (desireDrawable !=null)
			desire.setImageDrawable(desireDrawable);
		
		BracDetectionState[] historys = db.getTodayBracState();
		Point statistic_size = StatisticFragment.getStatisticPx();
		int blockMargin = statistic_size.x * 6/480;
		int circle_size = statistic_size.x * 63/480;
		
		int cur_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		
		for (int i =0; i<nBlocks; ++i){
			RelativeLayout lLayout = new RelativeLayout(context);
			
			RelativeLayout sLayout = new RelativeLayout(context);
			sLayout.addView(circleImages[i]);
			RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) circleImages[i].getLayoutParams();
			param.width = param.height = circle_size;
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
				if (TimeBlock.isEmpty(i, cur_hour) && Build.VERSION.SDK_INT>=11)
						circleImages[i].setAlpha(0.4F);
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
