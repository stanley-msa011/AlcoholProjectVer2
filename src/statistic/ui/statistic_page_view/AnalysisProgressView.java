package statistic.ui.statistic_page_view;

import java.util.Calendar;

import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.fragments.StatisticFragment;
import ubicomp.drunk_detection.ui.CustomTypefaceSpan;
import ubicomp.drunk_detection.ui.TextSize;
import ubicomp.drunk_detection.ui.Typefaces;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
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
	
	private Typeface wordTypeface,digitTypefaceBold;
	
	private String[] helpStr;
	
	public AnalysisProgressView(Context context,StatisticFragment statisticFragment){
		super(context, R.layout.analysis_progress_view,statisticFragment);
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		wordTypeface = Typefaces.getWordTypeface(context);
		digitTypefaceBold = Typefaces.getDigitTypefaceBold(context);
		helpStr = context.getResources().getStringArray(R.array.analysis_progress_help);
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
		
		title = (TextView) view.findViewById(R.id.analysis_progress_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,TextSize.normalTextSize(context));
		title.setTypeface(wordTypeface);
		
		help = (TextView) view.findViewById(R.id.analysis_progress_help);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX,TextSize.normalTextSize(context));
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
		restWeek = totalWeek - currentWeek;
		if (restWeek < 0 )
			restWeek = 0;
		if (restWeek > totalWeek)
			restWeek = totalWeek;
		
		Point screen = StatisticFragment.getStatisticPx();
		RelativeLayout.LayoutParams titleParam = (RelativeLayout.LayoutParams)title.getLayoutParams();
		titleParam.leftMargin = screen.x * 40/480;
		
		LinearLayout.LayoutParams helpParam = (LinearLayout.LayoutParams)help.getLayoutParams();
		helpParam.topMargin = helpParam.bottomMargin =  screen.x *11/480;
		helpParam.leftMargin =  screen.x * 40/480;
		
	}

	@Override
	public void onPostTask() {
		
		String cur_week = " "+String.valueOf(currentWeek)+" ";
		String rest_week = " "+String.valueOf(restWeek)+" ";
		
		Spannable s = new SpannableString(helpStr[0]+cur_week+helpStr[1]+rest_week+helpStr[2]);
		int start = 0;
		int end = helpStr[0].length();
		s.setSpan(new CustomTypefaceSpan("custom1",wordTypeface,0xFF000000), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end =start+cur_week.length();
		s.setSpan(new CustomTypefaceSpan("custom2",digitTypefaceBold,0xFFF39700), start, end,Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end =start+helpStr[1].length();
		s.setSpan(new CustomTypefaceSpan("custom1",wordTypeface,0xFF000000), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end =start+rest_week.length();
		s.setSpan(new CustomTypefaceSpan("custom2",digitTypefaceBold,0xFFF39700), start, end,Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end =start+helpStr[2].length();
		s.setSpan(new CustomTypefaceSpan("custom1",wordTypeface,0xFF000000), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		help.setText(s);
	}

	@Override
	public void onCancel() {
		clear();
	}	

}
