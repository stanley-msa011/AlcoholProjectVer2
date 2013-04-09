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
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StatisticDayView extends StatisticPageView {
	
	private TextView bracValueTextView;
	private HistoryDB db;
	private Bitmap resultBitmap;
	private ImageView bracResult;
	
	public StatisticDayView(Context context){
		super(context,R.layout.statistic_day_view);
		db = new HistoryDB(context);
	}
	
	private void setting(){
		bracValueTextView = (TextView) view.findViewById(R.id.statistic_day_brac_value);
		
		BracGameHistory history = db.getLatestBracGameHistory();
		
		
		 
		float brac = history.brac;
		
		DecimalFormat format = new DecimalFormat();
		format.setMaximumIntegerDigits(1);
		format.setMinimumIntegerDigits(1);
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);
		String output =format.format(brac); 
		
		Point statistic_size = StatisticFragment.getStatisticPx();
		
		bracValueTextView.setText(output);
		int text_width = (int) (statistic_size.x * 144.0/720.0);
		int text_height = (int) (statistic_size.y * 90.0/467.0);
		bracValueTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, text_height);
		Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/helvetica-lt-std-ultra-compressed.otf");
		bracValueTextView.setTypeface(face);
		bracValueTextView.setGravity(Gravity.CENTER_HORIZONTAL);
		RelativeLayout.LayoutParams textParam = (RelativeLayout.LayoutParams)bracValueTextView.getLayoutParams();
		textParam.width = text_width;
		textParam.height =ViewGroup.LayoutParams.WRAP_CONTENT;
		textParam.leftMargin = (int) ((statistic_size.x - text_width)/2+text_width*0.04);
		textParam.topMargin = (int)(statistic_size.y*0.34);
		
		Log.d("BRAC",String.valueOf(brac));
		Bitmap tmp;
		if (brac > BracDataHandler.THRESHOLD){
			tmp = BitmapFactory.decodeResource(view.getResources(), R.drawable.drunk_record_notpass);
			bracValueTextView.setTextColor(0xFFFF8613);
		}
		else{
			tmp = BitmapFactory.decodeResource(view.getResources(), R.drawable.drunk_record_pass);
			bracValueTextView.setTextColor(0xFFFFFFFF);
		}
		
		resultBitmap = Bitmap.createScaledBitmap(tmp,(int)( statistic_size.x*130.0/720.0),(int)( statistic_size.y*51.0/467.0), true);
		tmp.recycle();
		bracResult = (ImageView) view.findViewById(R.id.statistic_day_brac_result);
		bracResult.setImageBitmap(resultBitmap);
		RelativeLayout.LayoutParams resultParam =(RelativeLayout.LayoutParams ) bracResult.getLayoutParams();
		resultParam.leftMargin = (int) (statistic_size.x*529.0/720.0);
		resultParam.topMargin = (int) (statistic_size.y*196.0/467.0);
	}

	@Override
	public void clear() {
		if ( resultBitmap!=null && ! resultBitmap.isRecycled()){
			 resultBitmap.recycle();
			 resultBitmap = null;
		}
	}

	@Override
	public void resume() {
		setting();
	}
}
