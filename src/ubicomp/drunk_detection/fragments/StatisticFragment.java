package ubicomp.drunk_detection.fragments;

import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.ui.CustomToast;
import ubicomp.drunk_detection.ui.LoadingDialogControl;
import ubicomp.drunk_detection.ui.ScaleOnTouchListener;
import statistic.ui.QuestionMsgBox;
import statistic.ui.statistic_page_view.AnalysisCounterView;
import statistic.ui.statistic_page_view.AnalysisProgressView;
import statistic.ui.statistic_page_view.AnalysisRatingView;
import statistic.ui.statistic_page_view.AnalysisSavingView;
import statistic.ui.statistic_page_view.StatisticPageView;
import statistic.ui.statistic_page_view.StatisticPagerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLoggerLog;

public class StatisticFragment extends Fragment {
	private View view;
	private Activity activity;
	private ViewPager statisticView;
	private StatisticPagerAdapter statisticViewAdapter;
	private RelativeLayout statisticLayout,dots_layout;
	private ImageView[] dots;
	private Drawable dot_on,dot_off;
	private LinearLayout analysisLayout;
	private StatisticPageView[] analysisViews;
	private static Point statistic_px,analysis_px;
	private ScrollView analysisView;
	private LoadingHandler loadHandler;
	private StatisticFragment statisticFragment;
	
	private ImageView questionButton;
	private ScrollDismissHandler scrollDismissHandler;
	
	private static final int[] DOT_ID={0xFF0,0xFF1,0xFF2};
	
	private AlphaAnimation questionAnimation;
	
	private QuestionMsgBox msgBox;
	
	private ImageView firstScroll;
	
	private Point screen;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this.getActivity();
		dot_on = getResources().getDrawable(R.drawable.statistic_dot_on);
    	dot_off = getResources().getDrawable(R.drawable.statistic_dot_off);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	view = inflater.inflate(R.layout.statistic_fragment, container,false);
    	screen = FragmentTabs.getSize();
    	if (screen == null){
    		if (activity!=null)
    			activity.finish();
    		else
    			return view;
    	}
		statistic_px = new Point(screen.x,screen.x*380/480);
    	statisticLayout = (RelativeLayout) view.findViewById(R.id.brac_statistics_layout);
    	analysisLayout  = (LinearLayout)  view.findViewById(R.id.brac_analysis_layout);
    	analysisView = (ScrollView) view.findViewById(R.id.brac_analysis);
    	statisticView = (ViewPager) view.findViewById(R.id.brac_statistics);
    	dots_layout = (RelativeLayout) view.findViewById(R.id.brac_statistics_dots);
		questionButton = (ImageView) view.findViewById(R.id.question_button);
		firstScroll = (ImageView) view.findViewById(R.id.statistic_first_scroll);
		LayoutParams statisticViewLayoutParam =  statisticLayout.getLayoutParams();
    	statisticViewLayoutParam.height = statistic_px.y;
    	LayoutParams analysisViewLayoutParam =  analysisView.getLayoutParams();
    	analysisViewLayoutParam.height = screen.y - statistic_px.y;
    	RelativeLayout.LayoutParams dotsLayoutParam = (RelativeLayout.LayoutParams) dots_layout.getLayoutParams();
    	dotsLayoutParam.topMargin = screen.x*350/480;
    	dots = new ImageView[3];
    	for (int i=0;i<3;++i){
    		dots[i] = new ImageView(activity);
    		dots_layout.addView(dots[i]);
    		dots[i].setId(DOT_ID[i]);
    	}
    	int dot_gap =screen.x * 10/480;
    	RelativeLayout.LayoutParams dot1Param = (RelativeLayout.LayoutParams) dots[1].getLayoutParams();
    	dot1Param.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
    	RelativeLayout.LayoutParams dot0Param = (RelativeLayout.LayoutParams) dots[0].getLayoutParams();
    	dot0Param.addRule(RelativeLayout.LEFT_OF,DOT_ID[1]);
    	dot0Param.rightMargin = dot_gap;
    	RelativeLayout.LayoutParams dot2Param = (RelativeLayout.LayoutParams) dots[2].getLayoutParams();
    	dot2Param.addRule(RelativeLayout.RIGHT_OF,DOT_ID[1]);
    	dot2Param.leftMargin = dot_gap;
    	RelativeLayout.LayoutParams questionParam = (RelativeLayout.LayoutParams) questionButton.getLayoutParams();
		questionParam.topMargin =  screen.x * 3 / 480;
		questionParam.rightMargin =  screen.x * 3 / 480;
		int padding = screen.x * 20/480;
		questionButton.setPadding(padding, padding, padding, padding);
		questionButton.setOnTouchListener(new ScaleOnTouchListener());
		
		RelativeLayout.LayoutParams fsParam = (RelativeLayout.LayoutParams) firstScroll.getLayoutParams();
		fsParam.topMargin = screen.x;
        return view;
    }
    
    public void onResume(){
    	super.onResume();
    	enablePage(true);
    	statisticFragment = this;
    	analysisViews = new StatisticPageView[4];
    	analysisViews[0] = new AnalysisProgressView(activity, statisticFragment);
    	analysisViews[1] = new AnalysisCounterView(activity, statisticFragment);
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
    	if (sp.getBoolean("show_saving", true))
    		analysisViews[2] = new AnalysisSavingView(activity, statisticFragment);
		analysisViews[3] = new AnalysisRatingView(activity,statisticFragment);
		statisticViewAdapter = new StatisticPagerAdapter(activity,statisticFragment);
		msgBox = new QuestionMsgBox(statisticFragment,(RelativeLayout) view);
		if (loadHandler==null)
			loadHandler = new LoadingHandler();
		
		if (scrollDismissHandler == null)
			scrollDismissHandler = new ScrollDismissHandler();
		loadHandler.sendEmptyMessage(0);
    }
    
    public void onPause(){
    	if (loadHandler!=null)
    		loadHandler.removeMessages(0);
    	clear();
    	super.onPause();
    }
    
	private void clear(){
    	
    	statisticViewAdapter.clear();
    	for (int i=0;i<analysisViews.length;++i){
    		if (analysisViews[i]!=null)
    			analysisViews[i].clear();
    	}
    	if (analysisLayout!=null)
    		analysisLayout.removeAllViews();
    	if (msgBox!=null)
    		msgBox.clear();
    	System.gc();
    }
    
    public static Point getStatisticPx(){
    	if (statistic_px!=null)
    		return statistic_px;
    	else
    		return null;
    }
    public static Point getAnalysisPx(){
    	if (analysis_px!=null)
    		return analysis_px;
    	else
    		return null;
    }
    
    private class StatisticOnPageChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			
			long page = 0;
			switch (arg0){
			case 0:
				page = ClickLogId.STATISTIC_TODAY_VIEW;
				break;
			case 1:
				page = ClickLogId.STATISTIC_WEEKLY_VIEW;
				break;
			case 2:
				page = ClickLogId.STATISTIC_MONTHLY_VIEW;
				break;
			}
			ClickLoggerLog.Log(getActivity(), page);
			for (int i=0;i<3;++i)
				dots[i].setImageDrawable(dot_off);
			dots[arg0].setImageDrawable(dot_on);
		}
    	
    }
    
	@SuppressLint("HandlerLeak")
	private class LoadingHandler extends Handler{
		public void handleMessage(Message msg){
			FragmentTabs.enableTabAndClick(false);
        	statisticView.setAdapter(statisticViewAdapter);
        	statisticView.setOnPageChangeListener(new StatisticOnPageChangeListener());
        	statisticView.setSelected(true);
        	analysisLayout.removeAllViews();
        	analysisView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction()== MotionEvent.ACTION_DOWN)
						ClickLoggerLog.Log(getActivity(), ClickLogId.STATISTIC_ANALYSIS_TOUCH);
					return false;
				}
			});
        	
        	questionButton.setOnClickListener(new QuestionOnClickListener());
        	for (int i=0;i<analysisViews.length;++i)
        		if (analysisViews[i]!=null)
        			analysisLayout.addView(analysisViews[i].getView());
        	
    		statisticViewAdapter.onPreTask();
    		for (int i=0;i<analysisViews.length;++i)
    			if (analysisViews[i]!=null)
    				analysisViews[i].onPreTask();
    		
			statisticViewAdapter.onInBackground();
    		for (int i=0;i<analysisViews.length;++i)
    			if (analysisViews[i]!=null)
    				analysisViews[i].onInBackground();
			
	    	
			statisticViewAdapter.onPostTask();
    		for (int i=0;i<analysisViews.length;++i)
    			if (analysisViews[i]!=null)
    				analysisViews[i].onPostTask();
    		
	    	statisticView.setCurrentItem(0);
	    
    		for (int i=0;i<3;++i)
    			dots[i].setImageDrawable(dot_off);
			dots[0].setImageDrawable(dot_on);
			
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(activity);
			boolean tested = sp.getBoolean("tested", false);
			int result = sp.getInt("latest_result", 0);
			boolean isAdd = sp.getBoolean("latest_result_add", false);
			
			if (tested){
				int add_self_help_counter = 0;
				int show_text = 0;
				int default_counter = 0;
				if (result <=1){
					show_text  = R.string.after_test_pass;
					add_self_help_counter = 2;
				}
				else{
					show_text  = R.string.after_test_fail;
					add_self_help_counter = 1;
					default_counter = -1;
				}
				if (isAdd)
					CustomToast.generateToast(activity, show_text, add_self_help_counter, screen);
				else
					CustomToast.generateToast(activity, show_text, default_counter, screen);
				
				SharedPreferences.Editor editor = sp.edit();
		    	editor.putBoolean("tested", false);
		    	editor.putBoolean("latest_result_pass", false);
		    	editor.commit();
			}
			boolean fs = sp.getBoolean("first_scroll", true);
			if (fs){
				firstScroll.setVisibility(View.VISIBLE);
				Thread t = new Thread(new ScrollTimer());
				t.start();
			}
			
			if (msgBox!=null){
				msgBox.settingPreTask();
				msgBox.settingInBackground();
				msgBox.settingPostTask();
			}
			
			questionAnimation = new AlphaAnimation(1.0F,0.0F);
			questionAnimation.setDuration(200);
			questionAnimation.setRepeatCount(Animation.INFINITE);
			questionAnimation.setRepeatMode(Animation.REVERSE);
			
			setQuestionAnimation();
			
			FragmentTabs.enableTabAndClick(true);
			LoadingDialogControl.dismiss();
		}
	}
	
	@SuppressLint("HandlerLeak")
	private class ScrollDismissHandler extends Handler{
		public void handleMessage(Message msg){
			firstScroll.setVisibility(View.INVISIBLE);
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(activity);
			SharedPreferences.Editor edit = sp.edit();
			edit.putBoolean("first_scroll", false);
			edit.commit();
		}
	}
	
	
	private class ScrollTimer implements Runnable{

		@Override
		public void run() {
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {}
			scrollDismissHandler.sendEmptyMessage(0);
		}
	}
	
	
	private class QuestionOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			ClickLoggerLog.Log(getActivity(), ClickLogId.STATISTIC_QUESTION_BUTTON);
			
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(activity);
			int result = sp.getInt("latest_result", -1);
			if (msgBox == null)
				return;
			if (result == 0)
				msgBox.generateType0Box();
			else if (result == 1)
				msgBox.generateType1Box();
			else if (result == 2)
				msgBox.generateType2Box();
			else if (result == 3)
				msgBox.generateType3Box();
			else
				msgBox.generateNormalBox();
		}
	} 
	
	public void setQuestionAnimation(){
		questionButton.setVisibility(View.VISIBLE);
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(activity);
		int result = sp.getInt("latest_result", -1);
		if (result  == -1){
			questionAnimation.cancel();
			questionButton.setAnimation(null);
			questionButton.setAlpha(1.0F);
		}
		else{
			questionButton.setAnimation(questionAnimation);
			questionAnimation.start();
		}
	}
	
	public void enablePage(boolean enable){
		statisticView.setEnabled(enable);
		analysisView.setEnabled(enable);
		questionButton.setEnabled(enable);
		FragmentTabs.enableTabAndClick(enable);
	}
	
	public void showEndOfQuestionnaire(boolean addAcc){
		if (addAcc)
			CustomToast.generateToast(activity, R.string.after_questionnaire, 1, screen);
		else
			CustomToast.generateToast(activity, R.string.after_questionnaire, 0, screen);
	}
	
	public void updateSelfHelpCounter(){
		try{
			AnalysisCounterView acv = (AnalysisCounterView) analysisViews[1];
			acv.updateCounter();
		}catch(Exception e){	}
	}
	
}
