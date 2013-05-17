package statisticPageView.analysis;

import main.activities.R;
import main.activities.StatisticFragment;
import statisticPageView.StatisticPageView;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import database.HistoryDB;

public class AnalysisSavingView extends StatisticPageView {

	private TextView title;
	private ImageView title_bg;
	private Bitmap titleBmp;
	
	private TextView help;
	private HistoryDB db;
	
	private ImageView goalBar, currentBar, start,end;
	private Bitmap goalBarBmp, currentBarBmp, barStartBmp, barEndBmp;
	
	private int goalMoney;
	private int drinkCost;
	private int currentMoney;
	
	private RelativeLayout contentLayout;
	
	public AnalysisSavingView(Context context,StatisticFragment statisticFragment) {
		super(context, R.layout.analysis_saving_view,statisticFragment);
		db = new HistoryDB(context);
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		goalMoney = sp.getInt("goal_money", 10000);
		drinkCost = sp.getInt("drink_cost", 200);
	}

	@Override
	public void clear() {
		if (titleBmp!=null && !titleBmp.isRecycled()){
			titleBmp.recycle();
			titleBmp = null;
		}
		if (goalBarBmp!=null && !goalBarBmp.isRecycled()){
			goalBarBmp.recycle();
			goalBarBmp = null;
		}
		if (currentBarBmp!=null && !currentBarBmp.isRecycled()){
			currentBarBmp.recycle();
			currentBarBmp = null;
		}
		if (barStartBmp !=null && !barStartBmp.isRecycled()){
			barStartBmp.recycle();
			barStartBmp = null;
		}
		if (barEndBmp !=null && !barEndBmp.isRecycled()){
			barEndBmp.recycle();
			barEndBmp = null;
		}
	}	
	
	@Override
	public void onPreTask() {
		Point screen = StatisticFragment.getStatisticPx();
		
		title = (TextView) view.findViewById(R.id.analysis_saving_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(screen.x * 36.0/720.0));
		
		title_bg = (ImageView) view.findViewById(R.id.analysis_saving_title_bg);
		
		help = (TextView) view.findViewById(R.id.analysis_saving_help);
		help.setTextColor(0xFF545454);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 36.0/720.0));

		goalBar = (ImageView) view.findViewById(R.id.analysis_saving_bar);
		currentBar = (ImageView) view.findViewById(R.id.analysis_saving_cur_bar);
		start = (ImageView) view.findViewById(R.id.analysis_saving_cur_bar_start);
		end = (ImageView) view.findViewById(R.id.analysis_saving_cur_bar_end);
		
		contentLayout = (RelativeLayout) view.findViewById(R.id.analysis_saving_content_layout);
	}

	
	
	@Override
	public void onInBackground() {
		
		int curDrink = db.getAllBracGameScore();
		currentMoney = curDrink*drinkCost;
		
		Point screen = StatisticFragment.getStatisticPx();
		RelativeLayout.LayoutParams titleParam = (RelativeLayout.LayoutParams)title.getLayoutParams();
		titleParam.leftMargin = (int)(screen.x * 90.0/720.0);
		
		RelativeLayout.LayoutParams titleBgParam = (RelativeLayout.LayoutParams)title_bg.getLayoutParams();
		titleBgParam.width = screen.x;
		titleBgParam.height = (int)(screen.x * 47.0/720.0);
		
		titleBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.analysis_title_bar);
		
		LinearLayout.LayoutParams helpParam = (LinearLayout.LayoutParams)help.getLayoutParams();
		helpParam.topMargin = helpParam.bottomMargin =  (int)(screen.x * 16.0/720.0);
		
		RelativeLayout.LayoutParams barParam = (RelativeLayout.LayoutParams)goalBar.getLayoutParams();
		barParam.width = (int)(screen.x * 542.0/720.0);
		barParam.height = (int)(screen.x * 38.0/720.0);
		barParam.leftMargin = (int)(screen.x * 89.0/720.0);
		goalBarBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.analysis_money_bar);	
		
		RelativeLayout.LayoutParams startParam = (RelativeLayout.LayoutParams)start.getLayoutParams();
		startParam.width = (int)(screen.x * 16.0/720.0);
		startParam.height = (int)(screen.x * 38.0/720.0);
		startParam.leftMargin = (int)(screen.x * 89.0/720.0);
		barStartBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.analysis_money_cur_bar_start);	
		
		RelativeLayout.LayoutParams endParam = (RelativeLayout.LayoutParams)end.getLayoutParams();
		endParam.width = (int)(screen.x * 18.0/720.0);
		endParam.height = (int)(screen.x * 38.0/720.0);
		barEndBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.analysis_money_cur_bar_end);	
		
		int maxWidth = barParam.width - startParam.width -endParam.width;
		int width;
		if (currentMoney> goalMoney)
			width = maxWidth;
		else{
			width = maxWidth *currentMoney/goalMoney;
		}
		
		RelativeLayout.LayoutParams currentBarParam = (RelativeLayout.LayoutParams)currentBar.getLayoutParams();
		currentBarParam.width = width;
		currentBarParam.height =(int)(screen.x * 38.0/720.0);
		currentBarBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.analysis_money_cur_bar_internal);	
		
		LinearLayout.LayoutParams contentParam = (LinearLayout.LayoutParams)contentLayout.getLayoutParams();
		contentParam.bottomMargin =  (int)(screen.x * 20.0/720.0);
	}

	@Override
	public void onPostTask() {
		title_bg.setImageBitmap(titleBmp);
		goalBar.setImageBitmap(goalBarBmp);
		currentBar.setImageBitmap(currentBarBmp);
		start.setImageBitmap(barStartBmp);
		end.setImageBitmap(barEndBmp);
		String text =  "<font color=#000000>您已節省 </font><font color=#f39700>$"
								+currentMoney
								+"</font><font color=#000000> 元，目標為 </font><font color=#f39700>$"
								+goalMoney
								+"</font><font color=#000000></font><font color=#000000> 元</font>";
		help.setText(Html.fromHtml(text));
		
	}

	@Override
	public void onCancel() {
		clear();
	}
	

}
