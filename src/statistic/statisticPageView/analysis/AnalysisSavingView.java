package statistic.statisticPageView.analysis;

import ubicomp.drunk_detection.activities.R;
import statistic.statisticPageView.StatisticPageView;
import ubicomp.drunk_detection.activities.StatisticFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import database.HistoryDB;

public class AnalysisSavingView extends StatisticPageView {

	private TextView title;
	
	private TextView help;
	private HistoryDB db;
	
	private ImageView goalBar, currentBar, start;
	
	private String goalGood;
	private int goalMoney;
	private int drinkCost;
	private int currentMoney;
	
	private RelativeLayout contentLayout;
	
	private Typeface wordTypeface;
	
	public AnalysisSavingView(Context context,StatisticFragment statisticFragment) {
		super(context, R.layout.analysis_saving_view,statisticFragment);
		db = new HistoryDB(context);
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		goalGood = sp.getString("goal_good", "機車");
		goalMoney = sp.getInt("goal_money", 50000);
		drinkCost = sp.getInt("drink_cost", 1);
	}

	@Override
	public void clear() {
	}	
	
	@Override
	public void onPreTask() {
		
		wordTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/DFLiHeiStd-W3.otf");
		
		Point screen = StatisticFragment.getStatisticPx();
		
		title = (TextView) view.findViewById(R.id.analysis_saving_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,screen.x * 21/480);
		title.setTypeface(wordTypeface);
		
		help = (TextView) view.findViewById(R.id.analysis_saving_help);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 21/480);
		help.setTypeface(wordTypeface);
		
		goalBar = (ImageView) view.findViewById(R.id.analysis_saving_bar);
		currentBar = (ImageView) view.findViewById(R.id.analysis_saving_cur_bar);
		start = (ImageView) view.findViewById(R.id.analysis_saving_cur_bar_start);
		
		contentLayout = (RelativeLayout) view.findViewById(R.id.analysis_saving_content_layout);
	}

	
	
	@Override
	public void onInBackground() {
		
		int curDrink = db.getAllBracDetectionScore();
		currentMoney = curDrink*drinkCost;
		
		Point screen = StatisticFragment.getStatisticPx();
		RelativeLayout.LayoutParams titleParam = (RelativeLayout.LayoutParams)title.getLayoutParams();
		titleParam.leftMargin =screen.x * 40/480;
		
		LinearLayout.LayoutParams helpParam = (LinearLayout.LayoutParams)help.getLayoutParams();
		helpParam.topMargin = helpParam.bottomMargin = screen.x * 11/480;
		helpParam.leftMargin = screen.x * 40/480;
		
		RelativeLayout.LayoutParams barParam = (RelativeLayout.LayoutParams)goalBar.getLayoutParams();
		barParam.leftMargin = screen.x * 90/1080;
		
		
		RelativeLayout.LayoutParams startParam = (RelativeLayout.LayoutParams)start.getLayoutParams();
		startParam.leftMargin = screen.x * 90/1080;
		
		int maxWidth = screen.x * 839/1080;
		int width;
		if (currentMoney > goalMoney)
			width = maxWidth;
		else{
			width = maxWidth *currentMoney/goalMoney;
		}
		
		RelativeLayout.LayoutParams currentBarParam = (RelativeLayout.LayoutParams)currentBar.getLayoutParams();
		currentBarParam.width = width;
		
		LinearLayout.LayoutParams contentParam = (LinearLayout.LayoutParams)contentLayout.getLayoutParams();
		contentParam.bottomMargin =  screen.x * 11/480;
	}

	@Override
	public void onPostTask() {
		String text =  "<font color=#000000>您已節省 </font><font color=#f39700><strong>$"
								+currentMoney
								+"</strong></font><font color=#000000> 元，"
								+goalGood
								+"為 </font><font color=#f39700><strong>$"
								+goalMoney
								+"</strong></font><font color=#000000></font><font color=#000000> 元</font>";
		help.setText(Html.fromHtml(text));
		
	}

	@Override
	public void onCancel() {
		clear();
	}
	

}
