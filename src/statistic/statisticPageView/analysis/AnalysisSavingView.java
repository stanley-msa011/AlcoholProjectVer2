package statistic.statisticPageView.analysis;

import ubicomp.drunk_detection.activities.R;
import statistic.statisticPageView.StatisticPageView;
import statistic.statisticSetting.TargetSetting;
import ubicomp.drunk_detection.activities.StatisticFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	
	private Typeface wordTypeface;
	
	public AnalysisSavingView(Context context,StatisticFragment statisticFragment) {
		super(context, R.layout.analysis_saving_view,statisticFragment);
		db = new HistoryDB(context);
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		goalMoney = sp.getInt("goal_money", 10000);
		drinkCost = sp.getInt("drink_cost", 1);
	}

	@Override
	public void clear() {
		if (title_bg!=null)
			title_bg.setImageBitmap(null);
		if (goalBar!=null)
			goalBar.setImageBitmap(null);
		if (currentBar !=null)
			currentBar.setImageBitmap(null);
		if (start !=null)
			start.setImageBitmap(null);
		if (end !=null)
			end.setImageBitmap(null);
		
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
		
		wordTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dfheistd-w3.otf");
		
		Point screen = StatisticFragment.getStatisticPx();
		
		title = (TextView) view.findViewById(R.id.analysis_saving_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,screen.x * 54/1080);
		title.setTypeface(wordTypeface);
		
		title_bg = (ImageView) view.findViewById(R.id.analysis_saving_title_bg);
		
		help = (TextView) view.findViewById(R.id.analysis_saving_help);
		help.setTextColor(0xFF545454);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 54/1080);
		help.setTypeface(wordTypeface);
		
		goalBar = (ImageView) view.findViewById(R.id.analysis_saving_bar);
		currentBar = (ImageView) view.findViewById(R.id.analysis_saving_cur_bar);
		start = (ImageView) view.findViewById(R.id.analysis_saving_cur_bar_start);
		end = (ImageView) view.findViewById(R.id.analysis_saving_cur_bar_end);
		
		contentLayout = (RelativeLayout) view.findViewById(R.id.analysis_saving_content_layout);
	}

	
	
	@Override
	public void onInBackground() {
		
		int curDrink = db.getAllBracDetectionScore();
		currentMoney = curDrink*drinkCost;
		
		BitmapFactory.Options opt = new BitmapFactory.Options();
    	opt.inSampleSize = 2;
		
		Point screen = StatisticFragment.getStatisticPx();
		RelativeLayout.LayoutParams titleParam = (RelativeLayout.LayoutParams)title.getLayoutParams();
		titleParam.leftMargin =screen.x * 135/1080;
		
		RelativeLayout.LayoutParams titleBgParam = (RelativeLayout.LayoutParams)title_bg.getLayoutParams();
		titleBgParam.width = screen.x;
		titleBgParam.height =screen.x * 59/1080;
		
		Bitmap tmp;
    	
		tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.analysis_title_bar,opt);
		titleBmp = Bitmap.createScaledBitmap(tmp, screen.x,screen.x * 59/1080,true);
		tmp.recycle();
		
		LinearLayout.LayoutParams helpParam = (LinearLayout.LayoutParams)help.getLayoutParams();
		helpParam.topMargin = helpParam.bottomMargin = screen.x * 24/1080;
		
		RelativeLayout.LayoutParams barParam = (RelativeLayout.LayoutParams)goalBar.getLayoutParams();
		barParam.width = screen.x * 901/1080;
		barParam.height = screen.x * 65/1080;
		barParam.leftMargin = screen.x * 90/1080;
		tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.analysis_money_bar,opt);	
		
		goalBarBmp = Bitmap.createScaledBitmap(tmp, barParam.width, barParam.height, true);
		tmp.recycle();
		
		RelativeLayout.LayoutParams startParam = (RelativeLayout.LayoutParams)start.getLayoutParams();
		startParam.width = screen.x * 27/1080;
		startParam.height = screen.x * 65/1080;
		startParam.leftMargin = screen.x * 90/1080;
		barStartBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.analysis_money_cur_bar_start,opt);	
		
		RelativeLayout.LayoutParams endParam = (RelativeLayout.LayoutParams)end.getLayoutParams();
		endParam.width = screen.x * 29/1080;
		endParam.height =screen.x * 65/1080;
		barEndBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.analysis_money_cur_bar_end,opt);	
		
		int maxWidth = barParam.width - startParam.width -endParam.width;
		int width;
		if (currentMoney> TargetSetting.getTargetValue(goalMoney))
			width = maxWidth;
		else{
			width = maxWidth *currentMoney/TargetSetting.getTargetValue(goalMoney);
		}
		
		RelativeLayout.LayoutParams currentBarParam = (RelativeLayout.LayoutParams)currentBar.getLayoutParams();
		currentBarParam.width = width;
		currentBarParam.height =screen.x * 65/1080;
		currentBarBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.analysis_money_cur_bar_internal,opt);	
		
		LinearLayout.LayoutParams contentParam = (LinearLayout.LayoutParams)contentLayout.getLayoutParams();
		contentParam.bottomMargin =  screen.x * 30/1080;
	}

	@Override
	public void onPostTask() {
		title_bg.setImageBitmap(titleBmp);
		goalBar.setImageBitmap(goalBarBmp);
		currentBar.setImageBitmap(currentBarBmp);
		start.setImageBitmap(barStartBmp);
		end.setImageBitmap(barEndBmp);
		String text =  "<font color=#000000>您已節省 </font><font color=#f39700><strong>$"
								+currentMoney
								+"</strong></font><font color=#000000> 元，"
								+TargetSetting.getTargetName(goalMoney)
								+"為 </font><font color=#f39700><strong>$"
								+TargetSetting.getTargetValue(goalMoney)
								+"</strong></font><font color=#000000></font><font color=#000000> 元</font>";
		help.setText(Html.fromHtml(text));
		
	}

	@Override
	public void onCancel() {
		clear();
	}
	

}
