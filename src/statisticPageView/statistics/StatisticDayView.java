package statisticPageView.statistics;

import java.text.DecimalFormat;

import new_database.HistoryDB;
import statisticPageView.StatisticPageView;
import test.data.BracDataHandler;
import history.BracGameHistory;
import ioio.examples.hello.R;
import ioio.examples.hello.StatisticFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StatisticDayView extends StatisticPageView {
	
	private TextView bracValueTextView;
	private HistoryDB db;
	private Bitmap resultBitmap;
	private Bitmap bgBmp;
	private ImageView bg,bracResult;
	private LoadingTask task;
	
	public StatisticDayView(Context context,StatisticFragment statisticFragment){
		super(context,R.layout.statistic_day_view,statisticFragment);
		db = new HistoryDB(context);
		task = new LoadingTask();
		task.execute();
	}
	
		@Override
	public void clear() {
		Log.d("CLEAR","DAY");
		if (task!=null){
			task.cancel(true);
			task = null;
		}
		if ( resultBitmap!=null && ! resultBitmap.isRecycled()){
			 resultBitmap.recycle();
			 resultBitmap = null;
		}
		if ( bgBmp!=null && ! bgBmp.isRecycled()){
			bgBmp.recycle();
			bgBmp = null;
		}
	}

	
    private class LoadingTask extends AsyncTask<Void, Void, Void>{

    	private float brac;
    	private String output;
    	protected void onPreExecute(){
    		Point statistic_size = StatisticFragment.getStatisticPx();
    		int text_height = (int) (statistic_size.y * 90.0/467.0);
    		
    		bracValueTextView = (TextView) view.findViewById(R.id.statistic_day_brac_value);
    		bracValueTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, text_height);
    		Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/helvetica-lt-std-ultra-compressed.otf");
    		bracValueTextView.setTypeface(face);
    		bracValueTextView.setGravity(Gravity.CENTER_HORIZONTAL);
    		
    		BracGameHistory history = db.getLatestBracGameHistory();

    		brac = history.brac;
    		
    		bracResult = (ImageView) view.findViewById(R.id.statistic_day_brac_result);
    		bracResult.setScaleType(ScaleType.FIT_XY);
    		
    		bg = (ImageView) view.findViewById(R.id.statistic_day_brac_bg);
    	}
    	
		@Override
		protected Void doInBackground(Void... params) {
			DecimalFormat format = new DecimalFormat();
    		format.setMaximumIntegerDigits(1);
    		format.setMinimumIntegerDigits(1);
    		format.setMinimumFractionDigits(2);
    		format.setMaximumFractionDigits(2);
    		output =format.format(brac); 
			
    		Point statistic_size = StatisticFragment.getStatisticPx();
    		
    		int text_width = (int) (statistic_size.x * 144.0/720.0);
    		
    		bgBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_bg1);
    		
    		RelativeLayout.LayoutParams textParam = (RelativeLayout.LayoutParams)bracValueTextView.getLayoutParams();
    		textParam.width = text_width;
    		textParam.height =ViewGroup.LayoutParams.WRAP_CONTENT;
    		textParam.leftMargin = (int) ((statistic_size.x - text_width)/2+text_width*0.04);
    		textParam.topMargin = (int)(statistic_size.y*0.34);
    		
    		RelativeLayout.LayoutParams resultParam =(RelativeLayout.LayoutParams ) bracResult.getLayoutParams();
    		resultParam.width = (int)(statistic_size.x*130.0/720.0);
    		resultParam.height = (int)( statistic_size.y*51.0/467.0);
    		resultParam.leftMargin = (int) (statistic_size.x*529.0/720.0);
    		resultParam.topMargin = (int) (statistic_size.y*196.0/467.0);
    		
    		if (brac > BracDataHandler.THRESHOLD)
    			resultBitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.drunk_record_notpass);
    		else
    			resultBitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.drunk_record_pass);
			return null;
		}
		@Override
		 protected void onPostExecute(Void result) {
			bracValueTextView.setText(output);
			if (brac > BracDataHandler.THRESHOLD)
    			bracValueTextView.setTextColor(0xFFFF8613);
    		else
    			bracValueTextView.setTextColor(0xFFFFFFFF);
			bracResult.setImageBitmap(resultBitmap);
			bg.setImageBitmap(bgBmp);
		}
		
		protected void onCancelled(){
			clear();
		}
    }
}
