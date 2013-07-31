package statistic.ui.statistic_page_view;

import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.fragments.StatisticFragment;
import android.annotation.SuppressLint;
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
import data.database.HistoryDB;

public class AnalysisSavingView extends StatisticPageView {

	private TextView title;
	
	private TextView help;
	private HistoryDB db;
	
	private ImageView goalBar, currentBar, start;
	private ImageView bar;
	
	private String goalGood;
	private int goalMoney;
	private int drinkCost;
	private int currentMoney;
	
	private RelativeLayout contentLayout;
	
	private Typeface wordTypeface;
	
	private String[] helpStr;
	private String money_sign;
	
	public AnalysisSavingView(Context context,StatisticFragment statisticFragment) {
		super(context, R.layout.analysis_saving_view,statisticFragment);
		db = new HistoryDB(context);
		wordTypeface = statisticFragment.wordTypeface;
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		goalGood = sp.getString("goal_good", "機車");
		goalMoney = sp.getInt("goal_money", 50000);
		drinkCost = sp.getInt("drink_cost", 1);
		helpStr = context.getResources().getStringArray(R.array.analysis_saving_help);
		money_sign = context.getResources().getString(R.string.money_sign);
	}

	@Override
	public void clear() {
	}	
	
	@SuppressLint("CutPasteId")
	@Override
	public void onPreTask() {
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
		bar = (ImageView) view.findViewById(R.id.analysis_saving_bar);
		
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

		RelativeLayout.LayoutParams bar0Param = (RelativeLayout.LayoutParams)bar.getLayoutParams();
		bar0Param.width =screen.x*400/480;
		
		int maxWidth = screen.x * 372/480;
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
		String text =  "<font color=#000000>"+helpStr[0]+" </font><font color=#f39700><strong>"+money_sign
								+currentMoney
								+"</strong></font><font color=#000000> "+helpStr[1]
								+goalGood
								+helpStr[2]+" </font><font color=#f39700><strong>"+money_sign
								+goalMoney
								+"</strong></font><font color=#000000></font><font color=#000000> "+helpStr[3]+"</font>";
		help.setText(Html.fromHtml(text));
		
	}

	@Override
	public void onCancel() {
		clear();
	}
	

}
