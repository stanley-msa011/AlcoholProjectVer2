package statistic.ui.statistic_page_view;

import data.database.AdditionalDB;
import data.database.AudioDB;
import data.database.HistoryDB;
import data.database.QuestionDB;

import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.fragments.StatisticFragment;
import ubicomp.drunk_detection.ui.CustomTypefaceSpan;
import ubicomp.drunk_detection.ui.TextSize;
import ubicomp.drunk_detection.ui.Typefaces;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AnalysisCounterView extends StatisticPageView {

	private TextView title;
	private TextView help;
	private HistoryDB hdb;
	private QuestionDB qdb;
	private AudioDB adb;
	private AdditionalDB addDb;
	private RelativeLayout titleLayout;
	
	private Typeface wordTypeface,digitTypefaceBold;
	private String[] helpStr;
	private AlphaAnimation animation;
	private Spannable helpSpannable;
	
	public AnalysisCounterView(Context context,StatisticFragment statisticFragment){
		super(context, R.layout.analysis_counter_view,statisticFragment);
		hdb = new HistoryDB(context);
    	qdb = new QuestionDB(context);
    	adb = new AudioDB(context);
    	addDb = new AdditionalDB(context);
		helpStr = context.getResources().getStringArray(R.array.analysis_counter_help);
		wordTypeface = Typefaces.getWordTypeface(context);
		digitTypefaceBold = Typefaces.getDigitTypefaceBold(context);
		animation = new AlphaAnimation(1.F,0.F);
		animation.setDuration(1000);
		animation.setRepeatMode(Animation.REVERSE);
		animation.setRepeatCount(5);
	}
	
	@Override
	public void clear() {
	}
	
	
	
	@Override
	public void onPreTask() {
		
		title = (TextView) view.findViewById(R.id.analysis_counter_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,TextSize.normalTextSize(context));
		title.setTypeface(wordTypeface);
		titleLayout = (RelativeLayout) view.findViewById(R.id.analysis_counter_title_layout);
		help = (TextView) view.findViewById(R.id.analysis_counter_help);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX,TextSize.normalTextSize(context));
		help.setTypeface(wordTypeface);
		help.setText(" ");
	}

	
	private static final int COUPON_COUNTER = 40;
	
	@Override
	public void onInBackground() {
		Point screen = StatisticFragment.getStatisticPx();
		RelativeLayout.LayoutParams titleParam = (RelativeLayout.LayoutParams)title.getLayoutParams();
		titleParam.leftMargin = screen.x * 40/480;
		
		LinearLayout.LayoutParams helpParam = (LinearLayout.LayoutParams)help.getLayoutParams();
		helpParam.topMargin = helpParam.bottomMargin =  screen.x *11/480;
		helpParam.leftMargin =  screen.x * 40/480;
		updateCounter();
	}

	@SuppressWarnings("deprecation")
	public void updateCounter(){
		
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = sp.edit();
		int prev_coupon = sp.getInt("prev_coupon", 0);
		
		int total_counter = 
    			hdb.getLatestAccumulatedHistoryState().getSelfHelpCounter()
    			- hdb.getLatestUsedState().getSelfHelpCounter() 
    			+ qdb.getLatestEmotion().getSelfHelpCounter() 
    			+ qdb.getLatestEmotionManage().getSelfHelpCounter() 
    			+ qdb.getLatestQuestionnaire().getSelfHelpCounter()
				+ adb.getLatestAudioData().getSelfHelpCounter()
				+ qdb.getLatestStorytellingUsage().getSelfHelpCounter()
				+ addDb.getLatestStorytellingFling().getSelfHelpCounter();
    	int coupon = total_counter/COUPON_COUNTER;
    	int counter = total_counter;
    	
    	edit.putInt("prev_coupon", coupon);
    	edit.commit();
    	if (prev_coupon <coupon){
    		animation.setAnimationListener(new CouponAnimationListener(prev_coupon,coupon,counter));
    		setHelp(counter,prev_coupon);
    		if (Build.VERSION.SDK_INT < 16)
    			titleLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.analysis_title_bar_highlight));
    		else
    			titleLayout.setBackground(context.getResources().getDrawable(R.drawable.analysis_title_bar_highlight));
    		help.setAnimation(animation);
    		return;
    	}else{
    		if (Build.VERSION.SDK_INT < 16)
    			titleLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.analysis_title_bar));
    		else
    			titleLayout.setBackground(context.getResources().getDrawable(R.drawable.analysis_title_bar));
    	}
    	setHelp(counter,coupon);
	}
	
	@Override
	public void onPostTask() {
		help.setText(helpSpannable);
	}

	@Override
	public void onCancel() {
		clear();
	}	

	
	private class CouponAnimationListener implements AnimationListener{
		
		private int prev,cur,counter;
		
		public CouponAnimationListener(int prev_coupon, int cur_coupon,int counter){
			prev = prev_coupon;
			cur = cur_coupon;
			this.counter = counter;
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			setHelp(counter,cur);
		}
		@Override
		public void onAnimationRepeat(Animation animation) {
			setHelp(counter,cur);
		}
		@Override
		public void onAnimationStart(Animation animation) {
			setHelp(counter,prev);
		}
	}
	
	
	private void setHelp(int counter,int coupon){
		String counter_str = " "+String.valueOf(counter)+" ";
		String coupon_str = " "+String.valueOf(coupon)+" ";
		
		helpSpannable = new SpannableString(helpStr[0]+counter_str+helpStr[1]+coupon_str+helpStr[2]);
		int start = 0;
		int end = helpStr[0].length();
		helpSpannable.setSpan(new CustomTypefaceSpan("custom1",wordTypeface,0xFF000000), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end =start+counter_str.length();
		helpSpannable.setSpan(new CustomTypefaceSpan("custom2",digitTypefaceBold,0xFFF39700), start, end,Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end =start+helpStr[1].length();
		helpSpannable.setSpan(new CustomTypefaceSpan("custom1",wordTypeface,0xFF000000), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end =start+coupon_str.length();
		helpSpannable.setSpan(new CustomTypefaceSpan("custom2",digitTypefaceBold,0xFFF39700), start, end,Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end =start+helpStr[2].length();
		helpSpannable.setSpan(new CustomTypefaceSpan("custom1",wordTypeface,0xFF000000), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    	help.setText(helpSpannable);
	}
}
