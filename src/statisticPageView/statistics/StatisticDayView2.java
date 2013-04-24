package statisticPageView.statistics;

import java.text.DecimalFormat;
import java.util.Calendar;

import database.HistoryDB;

import main.activities.Lang;
import main.activities.R;
import main.activities.StatisticFragment;
import statisticPageView.StatisticPageView;
import test.data.BracDataHandler;
import history.BracGameHistory;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class StatisticDayView2 extends StatisticPageView {
	
	private TextView bracValueTextView,bracResult2,bracTime;
	private HistoryDB db;
	private Bitmap resultBitmap;
	private Bitmap bgBmp,logoBmp;
	private ImageView bg,bracResult;
	private ImageView bracLogo;
	
	public StatisticDayView2(Context context,StatisticFragment statisticFragment){
		super(context,R.layout.statistic_day_view2,statisticFragment);
		db = new HistoryDB(context);
	}
	
		@Override
	public void clear() {
		Log.d("CLEAR","DAY");
		if ( resultBitmap!=null && ! resultBitmap.isRecycled()){
			 resultBitmap.recycle();
			 resultBitmap = null;
		}
		if ( bgBmp!=null && ! bgBmp.isRecycled()){
			bgBmp.recycle();
			bgBmp = null;
		}
		if ( logoBmp!=null && ! logoBmp.isRecycled()){
			logoBmp.recycle();
			logoBmp = null;
		}
	}


		
	private float brac;
	private long brac_time;
    private String output;
	@Override
	public void onPreTask() {
		Point statistic_size = StatisticFragment.getStatisticPx();
		int text_height = (int) (statistic_size.y * 90.0/467.0);
		
		bracValueTextView = (TextView) view.findViewById(R.id.statistic_day_brac_value);
		bracValueTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, text_height);
		Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/helvetica-lt-std-bold.otf");
		bracValueTextView.setTypeface(face);
		bracValueTextView.setGravity(Gravity.CENTER_HORIZONTAL);
		
		bracResult2 = (TextView) view.findViewById(R.id.statistic_day_brac_result2);
		int text_height2 = (int) (statistic_size.y * 42.0/467.0);
		bracResult2.setTextSize(TypedValue.COMPLEX_UNIT_PX, text_height2);
		
		BracGameHistory history = db.getLatestBracGameHistory();

		brac = history.brac;
		brac_time = history.timestamp;
		
		bracResult = (ImageView) view.findViewById(R.id.statistic_day_brac_result);
		bracResult.setScaleType(ScaleType.FIT_XY);
		
		bracLogo = (ImageView) view.findViewById(R.id.statistic_day_brac_logo);
		
		bracTime = (TextView) view.findViewById(R.id.statistic_day_brac_time);
		bracTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (statistic_size.y * 30.0/467.0));
		bg = (ImageView) view.findViewById(R.id.statistic_day_brac_bg);
		
	}

	@Override
	public void onInBackground() {
		DecimalFormat format = new DecimalFormat();
		format.setMaximumIntegerDigits(1);
		format.setMinimumIntegerDigits(1);
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);
		output =format.format(brac); 
		
		Point statistic_size = StatisticFragment.getStatisticPx();
		
		bgBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_bg2);
		
		RelativeLayout.LayoutParams textParam = (RelativeLayout.LayoutParams)bracValueTextView.getLayoutParams();
		textParam.leftMargin = (int)(statistic_size.x*80.0/720.0);
		textParam.topMargin = (int)(statistic_size.y*0.32);
		
		RelativeLayout.LayoutParams resultParam =(RelativeLayout.LayoutParams ) bracResult.getLayoutParams();
		resultParam.width = (int)(statistic_size.x*130.0/720.0);
		resultParam.height = (int)( statistic_size.y*51.0/467.0);
		resultParam.leftMargin = (int) (statistic_size.x*289.0/720.0);
		resultParam.topMargin = (int) (statistic_size.y*196.0/467.0);
		
		RelativeLayout.LayoutParams result2Param =(RelativeLayout.LayoutParams ) bracResult2.getLayoutParams();
		result2Param.leftMargin = (int) (statistic_size.x*309.0/720.0);
		
		RelativeLayout.LayoutParams dateParam = (LayoutParams) bracTime.getLayoutParams();
		dateParam.leftMargin = (int) (statistic_size.x*80.0/720.0);
		
		RelativeLayout.LayoutParams logoParam =(RelativeLayout.LayoutParams ) bracLogo.getLayoutParams();
		logoParam.width = (int)(statistic_size.x*50.0/720.0);
		logoParam.height =  (int)(statistic_size.x*50.0/720.0);
		logoParam.leftMargin = (int) (statistic_size.x*325.0/720.0);
		logoParam.topMargin = (int) (statistic_size.y*134.0/467.0);
		
		if (brac > BracDataHandler.THRESHOLD)
			resultBitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.drunk_record_notpass);
		else
			resultBitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.drunk_record_pass);
		
		if (brac > BracDataHandler.THRESHOLD2)
			logoBmp = BitmapFactory.decodeResource(view.getResources(), R.drawable.brac_fail);
		else if  (brac > BracDataHandler.THRESHOLD)
			logoBmp = BitmapFactory.decodeResource(view.getResources(), R.drawable.brac_warning);
		else
			logoBmp = BitmapFactory.decodeResource(view.getResources(), R.drawable.brac_success);
		
	}

	@Override
	public void onPostTask() {
		bracValueTextView.setText(output);
		if (brac > BracDataHandler.THRESHOLD){
			bracValueTextView.setTextColor(0xFFFF8613);
			if (brac > BracDataHandler.THRESHOLD2)
				bracResult2.setText("大發");
			else
				bracResult2.setText("小發");
			bracResult2.setTextColor(0xFFFF8613);
		}
		else{
			bracValueTextView.setTextColor(0xFFFFFFFF);
		}
		
		String time;
		if (brac_time == 0){
			time = "尚未測試";
		}
		else{
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(brac_time*1000L);
			int month = cal.get(Calendar.MONTH)+1;
			int day = cal.get(Calendar.DATE);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			time = "@ "+month+" 月 "+day+" 日\n"+hour+":"+min;
		}

		bracTime.setText(time);
		
		if (brac_time == 0){
			bracResult.setImageBitmap(null);
		}
		else{
			bracLogo.setImageBitmap(logoBmp);
			bracResult.setImageBitmap(resultBitmap);
		}
		bg.setImageBitmap(bgBmp);
		
	}

	@Override
	public void onCancel() {
		clear();
		
	}
}
