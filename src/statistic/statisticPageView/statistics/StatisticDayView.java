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
	private Bitmap valueCircleBmp;
	private ImageView[] circleImages;
	private Bitmap[] circleBmps;
	private TextView[] circleValues;
	private TextView[] circleTexts;
	private DecimalFormat format;
	private RelativeLayout valueLayout;
	private LinearLayout blockLayout;
	
	private ImageView emotion,desire;
	private Bitmap emotionBmp, desireBmp;
	
	private String[] blockHint = {"早上\n0~12","中午\n12~18","晚上\n18~24"};
	
	private static final int nBlocks = 3;
	
	private Typeface digitTypeface;
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
		valueCircle.setImageBitmap(null);
		emotion.setImageBitmap(null);
		desire.setImageBitmap(null);
		
		if (circleImages!=null)
			for (int i=0;i<circleImages.length;++i)
				circleImages[i].setImageBitmap(null);
		
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
		wordTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dfheistd-w3.otf");
		wordTypefaceBold = Typeface.createFromAsset(context.getAssets(), "fonts/dfheistd-w5.otf");
		
		bracTitle = (TextView) view.findViewById(R.id.statistic_day_title);
		bracTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,  statistic_size.x * 80/1080);
		bracTitle.setTypeface(wordTypefaceBold);
		
		bracValue = (TextView) view.findViewById(R.id.statistic_day_brac_value);
		bracValue.setTextSize(TypedValue.COMPLEX_UNIT_PX,  statistic_size.x * 135/1080);
		bracValue.setTypeface(digitTypeface);
		
		BracDetectionState history = db.getLatestBracDetection();

		brac = history.brac;
		brac_time = history.timestamp;
		e_idx = history.emotion-1;
		d_idx = history.desire - 1;
		
		bracTime = (TextView) view.findViewById(R.id.statistic_day_brac_time);
		bracTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, statistic_size.x * 72/1080);
		bracTime.setTypeface(wordTypeface);
		
		circleImages = new ImageView[nBlocks];
		
		int textSize = statistic_size.x * 54/1080;
		int textSize2 = statistic_size.x * 48/1080;
		circleTexts =new TextView[nBlocks];
		circleValues = new TextView[nBlocks];
		for (int i=0;i<nBlocks;++i){
			circleImages[i] = new ImageView(context);
			circleTexts[i] = new TextView(context);
			circleValues[i] = new TextView(context); 
			
			circleTexts[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			circleTexts[i].setTextColor(0xFF000000);
			circleTexts[i].setText(blockHint[i]);
			circleTexts[i].setGravity(Gravity.CENTER);
			circleTexts[i].setTypeface(wordTypeface);
			
			circleValues[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize2);
			circleValues[i].setTextColor(0xFFFFFFFF);
			circleValues[i].setTypeface(digitTypeface);
		}
		bracHelp = (TextView) view.findViewById(R.id.statistic_day_brac);
		bracHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		bracHelp.setTypeface(digitTypeface);
		
		valueCircle = (ImageView) view.findViewById(R.id.statistic_day_value_circle);
		valueLayout = (RelativeLayout) view.findViewById(R.id.statistic_day_value_layout);
		blockLayout = (LinearLayout) view.findViewById(R.id.statistic_day_block_layout);
		
		emotion = (ImageView) view.findViewById(R.id.statistic_day_emotion);
		desire = (ImageView) view.findViewById(R.id.statistic_day_desire);
	}

	@Override
	public void onInBackground() {
		format = new DecimalFormat();
		format.setMaximumIntegerDigits(1);
		format.setMinimumIntegerDigits(1);
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);
		output =format.format(brac); 
		
		Point screen = StatisticFragment.getStatisticPx();
		
		RelativeLayout.LayoutParams titleParam = (LayoutParams) bracTitle.getLayoutParams();
		titleParam.leftMargin = screen.x * 80/1080;
		titleParam.topMargin = screen.x * 300/1080;
		
		RelativeLayout.LayoutParams dateParam = (LayoutParams) bracTime.getLayoutParams();
		dateParam.leftMargin = screen.x * 80/1080;
		dateParam.topMargin = screen.x * 20/1080;

		RelativeLayout.LayoutParams vParam = (LayoutParams) valueLayout.getLayoutParams();
		vParam.leftMargin =screen.x * 100/1080;
		vParam.topMargin = screen.x * 240/1080;
		vParam.width =screen.x * 454/1080;
		vParam.height = screen.x * 454/1080;
		
		RelativeLayout.LayoutParams bParam = (LayoutParams) blockLayout.getLayoutParams();
		bParam.topMargin = screen.x * 40/1080;
		
		int icon_size = screen.x*120/1080;
		RelativeLayout.LayoutParams emotionParam = (LayoutParams) emotion.getLayoutParams();
		emotionParam.topMargin = screen.x * 20/1080;
		emotionParam.width = emotionParam.height = icon_size;
		emotionParam.leftMargin = screen.x * 80/1080;
		
		RelativeLayout.LayoutParams desireParam = (LayoutParams) desire.getLayoutParams();
		desireParam.topMargin = screen.x * 20/1080;
		desireParam.width = emotionParam.height = icon_size;
		desireParam.leftMargin = screen.x * 20/1080;
		
		Bitmap tmp;
		BitmapFactory.Options opt = new BitmapFactory.Options();
    	opt.inSampleSize = 2;
		
		if (brac_time == 0)
			tmp = BitmapFactory.decodeResource(view.getResources(), R.drawable.statistic_day_main_circle_none,opt);
		else if (brac < BracDataHandler.THRESHOLD)
			tmp = BitmapFactory.decodeResource(view.getResources(), R.drawable.statistic_day_main_circle_pass,opt);
		else
			tmp = BitmapFactory.decodeResource(view.getResources(), R.drawable.statistic_day_main_circle_fail,opt);
		
		valueCircleBmp = Bitmap.createScaledBitmap(tmp, screen.x * 454/1080, screen.x * 454/1080, true);
		tmp.recycle();
		
		circleBmps = new Bitmap[3];
		int circleWidth = screen.x * 141/1080;
		int circleHeight = screen.x * 140/1080;
		tmp= BitmapFactory.decodeResource(view.getResources(), R.drawable.statistic_day_circle_none,opt);
		circleBmps[0] = Bitmap.createScaledBitmap(tmp, circleWidth, circleHeight, true);
		tmp.recycle();
		
		tmp= BitmapFactory.decodeResource(view.getResources(), R.drawable.statistic_day_circle_fail,opt);
		circleBmps[1] = Bitmap.createScaledBitmap(tmp,  circleWidth, circleHeight, true);
		tmp.recycle();
		
		tmp = BitmapFactory.decodeResource(view.getResources(), R.drawable.statistic_day_circle_pass,opt);
		circleBmps[2] = Bitmap.createScaledBitmap(tmp,  circleWidth, circleHeight, true);
		tmp.recycle();
		
		
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
			String min_str;
			if (min < 10)
				min_str = "0"+String.valueOf(min);
			else
				min_str = String.valueOf(min);
			
			if (am_pm == Calendar.AM)
				time = month+"月"+day+"日\n"+hour+":"+min_str+" A.M";
			else
				time = month+"月"+day+"日\n"+hour+":"+min_str+" P.M";
		}

		bracTime.setText(time);
		
		if (valueCircleBmp !=null && !valueCircleBmp.isRecycled())
			valueCircle.setImageBitmap(valueCircleBmp);
		
		if (emotionBmp !=null && !emotionBmp.isRecycled())
			emotion.setImageBitmap(emotionBmp);
		
		if (desireBmp !=null && !desireBmp.isRecycled())
			desire.setImageBitmap(desireBmp);
		
		BracDetectionState[] historys = db.getTodayBracState();
		Point statistic_size = StatisticFragment.getStatisticPx();
		int circleWidth = statistic_size.x * 141/1080;
		int circleHeight = statistic_size.x * 140/1080;
		int blockWidth = statistic_size.x * 30/1080;
		
		int cur_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		
		for (int i =0; i<nBlocks; ++i){
			RelativeLayout lLayout = new RelativeLayout(context);
			
			RelativeLayout sLayout = new RelativeLayout(context);
			sLayout.addView(circleImages[i]);
			sLayout.addView(circleValues[i]);
			sLayout.setId(0x999);
			RelativeLayout.LayoutParams circleParam =(RelativeLayout.LayoutParams ) circleImages[i].getLayoutParams();
			circleParam.width =circleWidth;
			circleParam.height =circleHeight;
			
			circleParam.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
			RelativeLayout.LayoutParams valueParam =(RelativeLayout.LayoutParams ) circleValues[i].getLayoutParams();
			valueParam.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
			
			lLayout.addView(sLayout);
			lLayout.addView(circleTexts[i]);
			
			RelativeLayout.LayoutParams sParam =(RelativeLayout.LayoutParams ) sLayout.getLayoutParams();
			sParam.width=circleWidth;
			sParam.height=circleHeight;
			
			RelativeLayout.LayoutParams tParam =(RelativeLayout.LayoutParams ) circleTexts[i].getLayoutParams();
			tParam.addRule(RelativeLayout.RIGHT_OF,0x999);
			tParam.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			
			if (historys[i]==null){
				circleValues[i].setText("");
				if (TimeBlock.isEmpty(i, cur_hour)){
					circleImages[i].setImageBitmap(circleBmps[0] );
					circleImages[i].setAlpha(0.5F);
				}
				else
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
		}
	}

	@Override
	public void onCancel() {
		clear();
	}
}
