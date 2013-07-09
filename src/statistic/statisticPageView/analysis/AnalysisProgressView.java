package statistic.statisticPageView.analysis;

import java.util.Calendar;

import statistic.statisticPageView.StatisticPageView;
import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.activities.StatisticFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AnalysisProgressView extends StatisticPageView {

	private TextView title;
	private TextView help;
	private Calendar fromCal;
	
	private static final int totalWeek = 12;
	private int currentWeek;
	private int restWeek;
	
	private Typeface wordTypeface;
	
	public AnalysisProgressView(Context context,StatisticFragment statisticFragment){
		super(context, R.layout.analysis_progress_view,statisticFragment);
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		
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
	}
	
	
	
	@Override
	public void onPreTask() {
		
		wordTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/DFLiHeiStd-W3.otf");
		
		Point screen = StatisticFragment.getStatisticPx();
		
		title = (TextView) view.findViewById(R.id.analysis_progress_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,screen.x * 21/480);
		title.setTypeface(wordTypeface);
		
		help = (TextView) view.findViewById(R.id.analysis_progress_help);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 21/480);
		help.setTypeface(wordTypeface);
		
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
		titleParam.leftMargin = screen.x * 40/480;
		
		LinearLayout.LayoutParams helpParam = (LinearLayout.LayoutParams)help.getLayoutParams();
		helpParam.topMargin = helpParam.bottomMargin =  screen.x *11/480;
		helpParam.leftMargin =  screen.x * 40/480;
		
	}

	@Override
	public void onPostTask() {
		String text =  "<font color=#000000>已戒酒 </font><font color=#f39700><strong>"
				+currentWeek
				+"</strong></font><font color=#000000> 周，完成此療程尚餘 </font><font color=#f39700><strong>"
				+restWeek
				+"</strong></font><font color=#000000></font><font color=#000000> 周</font>";
		help.setText(Html.fromHtml(text));
	}

	@Override
	public void onCancel() {
		clear();
	}	

}
