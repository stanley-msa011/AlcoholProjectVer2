package statisticPageView.analysis;

import java.util.Calendar;

import main.activities.Lang;
import main.activities.R;
import main.activities.StatisticFragment;
import statisticPageView.StatisticPageView;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import database.HistoryDB;

public class AnalysisProgressView extends StatisticPageView {

	private TextView title;
	private ImageView title_bg;
	private Bitmap titleBmp;
	private TextView help;
	private HistoryDB db;
	private RelativeLayout contentLayout;
	private Calendar fromCal;
	
	private static final int totalWeek = 12;
	private int currentWeek;
	private int restWeek;
	
	private Typeface wordTypeface;
	
	public AnalysisProgressView(Context context,StatisticFragment statisticFragment){
		super(context, R.layout.analysis_progress_view,statisticFragment);
		db = new HistoryDB(context);
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		
		fromCal = db.getFirstTestDate();
		if (fromCal == null)
			fromCal = Calendar.getInstance();
		int year = sp.getInt("sYear", fromCal.get(Calendar.YEAR));
		int month = sp.getInt("sMonth", fromCal.get(Calendar.MONTH));
		int date = sp.getInt("sDate", fromCal.get(Calendar.DATE));
		fromCal.set(Calendar.YEAR, year);
		fromCal.set(Calendar.MONTH, month);
		fromCal.set(Calendar.DATE, date);
	}
	
	@Override
	public void clear() {
		Log.d("CLEAR","DRUNK");
		if (titleBmp!=null && !titleBmp.isRecycled()){
			titleBmp.recycle();
			titleBmp = null;
		}
	}
	
	
	
	@Override
	public void onPreTask() {
		
		wordTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dfheistd-w3.otf");
		
		Point screen = StatisticFragment.getStatisticPx();
		
		title = (TextView) view.findViewById(R.id.analysis_progress_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(screen.x * 36.0/720.0));
		title.setTypeface(wordTypeface);
		
		title_bg = (ImageView) view.findViewById(R.id.analysis_progress_title_bg);
		title_bg.setScaleType(ScaleType.FIT_XY);
		
		help = (TextView) view.findViewById(R.id.analysis_progress_help);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 36.0/720.0));
		help.setTypeface(wordTypeface);
		
		contentLayout = (RelativeLayout) view.findViewById(R.id.analysis_progress_content_layout);
		
	}

	@Override
	public void onInBackground() {
		
		Calendar now = Calendar.getInstance();
		if (fromCal == null)
			currentWeek = 0;
		else{
			long diff_millis = now.getTimeInMillis() - fromCal.getTimeInMillis();
			currentWeek = (int)(diff_millis / (1000 * 60 * 60 * 24 * 7));
		}
		Log.d("analysis progress",fromCal.toString());
		restWeek = totalWeek - currentWeek;
		
		Point screen = StatisticFragment.getStatisticPx();
		RelativeLayout.LayoutParams titleParam = (RelativeLayout.LayoutParams)title.getLayoutParams();
		titleParam.leftMargin = (int)(screen.x * 90.0/720.0);
		
		RelativeLayout.LayoutParams titleBgParam = (RelativeLayout.LayoutParams)title_bg.getLayoutParams();
		titleBgParam.width = screen.x;
		titleBgParam.height = (int)(screen.x * 47.0/720.0);
		
		titleBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.analysis_title_bar);
		
		LinearLayout.LayoutParams helpParam = (LinearLayout.LayoutParams)help.getLayoutParams();
		helpParam.topMargin = helpParam.bottomMargin =  (int)(screen.x *16.0/720.0);
		
		//LinearLayout.LayoutParams contentParam = (LinearLayout.LayoutParams)contentLayout.getLayoutParams();
		//contentParam.bottomMargin =  (int)(screen.x * 30.0/720.0);
		
	}

	@Override
	public void onPostTask() {
		title_bg.setImageBitmap(titleBmp);
		String text =  "<font color=#000000>已戒酒 </font><font color=#f39700>"
				+currentWeek
				+"</font><font color=#000000> 周，完成此療程尚餘 </font><font color=#f39700>"
				+restWeek
				+"</font><font color=#000000></font><font color=#000000> 周</font>";
		help.setText(Html.fromHtml(text));
		
	}

	@Override
	public void onCancel() {
		clear();
	}	

}
