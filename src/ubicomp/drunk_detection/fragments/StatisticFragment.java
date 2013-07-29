package ubicomp.drunk_detection.fragments;

import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.ui.LoadingBox;
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
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
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
	private FrameLayout shadow;
	
	private ImageView showImage;
	private TextView showText;
	
	private ShowDismissHandler showDismissHandler;
	private ScrollDismissHandler scrollDismissHandler;
	
	private static final int[] DOT_ID={0xFF0,0xFF1,0xFF2};
	
	private AlphaAnimation questionAnimation;
	
	private QuestionMsgBox msgBox;
	
	private ImageView firstScroll;
	
	private Typeface wordTypefaceBold;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	view = inflater.inflate(R.layout.statistic_fragment, container,false);
        return view;
    }
    
    public void onResume(){
    	super.onResume();
		
    	statisticFragment = this;
    	
    	analysisViews = new StatisticPageView[4];
    	analysisViews[0] = new AnalysisProgressView(activity, statisticFragment);
    	analysisViews[1] = new AnalysisCounterView(activity, statisticFragment);
    	analysisViews[2] = new AnalysisSavingView(activity, statisticFragment);
		analysisViews[3] = new AnalysisRatingView(activity,statisticFragment);
    	
		statisticViewAdapter = new StatisticPagerAdapter(activity,statisticFragment);
		
		msgBox = new QuestionMsgBox(statisticFragment,(RelativeLayout) view);
		
		if (loadHandler==null)
			loadHandler = new LoadingHandler();
		
		if (showDismissHandler == null)
			showDismissHandler = new ShowDismissHandler();
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
			FragmentTabs.enableTab(false);
			
        	Point screen = FragmentTabs.getSize();
        	statistic_px = new Point(screen.x,screen.x*380/480);
        	
        	wordTypefaceBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/DFLiHeiStd-W5.otf");
        	statisticLayout = (RelativeLayout) view.findViewById(R.id.brac_statistics_layout);
        	statisticView = (ViewPager) view.findViewById(R.id.brac_statistics);
        	statisticView.setAdapter(statisticViewAdapter);
        	statisticView.setOnPageChangeListener(new StatisticOnPageChangeListener());
        	statisticView.setSelected(true);
        	analysisLayout  = (LinearLayout)  view.findViewById(R.id.brac_analysis_layout);
        	analysisLayout.removeAllViews();
        	analysisView = (ScrollView) view.findViewById(R.id.brac_analysis);
        	analysisView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction()== MotionEvent.ACTION_DOWN)
						ClickLoggerLog.Log(getActivity(), ClickLogId.STATISTIC_ANALYSIS_TOUCH);
					return false;
				}
			});
        	dots_layout = (RelativeLayout) view.findViewById(R.id.brac_statistics_dots);
        	
    		questionButton = (ImageView) view.findViewById(R.id.question_background);
        	questionButton.setOnClickListener(new QuestionOnClickListener());
    		
        	shadow = (FrameLayout) view.findViewById(R.id.statistic_shadow);
    		showImage = (ImageView) view.findViewById(R.id.statistic_show_picture);
    		showText = (TextView) view.findViewById(R.id.statistic_show_text);
    		showText.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 24/480);
    		showText.setTypeface(wordTypefaceBold);
    		
    		
    		firstScroll = (ImageView) view.findViewById(R.id.statistic_first_scroll);
    		
        	for (int i=0;i<analysisViews.length;++i)
    			analysisLayout.addView(analysisViews[i].getView());
    		
    		statisticViewAdapter.onPreTask();
    		for (int i=0;i<analysisViews.length;++i)
    			analysisViews[i].onPreTask();
    		
			LayoutParams statisticViewLayoutParam =  statisticLayout.getLayoutParams();
        	statisticViewLayoutParam.height = statistic_px.y;
			
        	LayoutParams analysisViewLayoutParam =  analysisView.getLayoutParams();
        	analysisViewLayoutParam.height = screen.y - statistic_px.y;
        	
	    	dot_on = getResources().getDrawable(R.drawable.statistic_dot_on);
	    	dot_off = getResources().getDrawable(R.drawable.statistic_dot_off);
			
	    	RelativeLayout.LayoutParams dotsLayoutParam = (android.widget.RelativeLayout.LayoutParams) dots_layout.getLayoutParams();
	    	dotsLayoutParam.topMargin = screen.x*350/480;
	    	
			statisticViewAdapter.onInBackground();
    		for (int i=0;i<analysisViews.length;++i)
    			analysisViews[i].onInBackground();
			
	    	int dot_gap =screen.x * 10/480;
			
	    	dots = new ImageView[3];
	    	for (int i=0;i<3;++i){
	    		dots[i] = new ImageView(dots_layout.getContext());
	    		dots_layout.addView(dots[i]);
	    		dots[i].setId(DOT_ID[i]);
	    	}
	    	
	    	RelativeLayout.LayoutParams dot1Param = (android.widget.RelativeLayout.LayoutParams) dots[1].getLayoutParams();
	    	dot1Param.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
	    	RelativeLayout.LayoutParams dot0Param = (android.widget.RelativeLayout.LayoutParams) dots[0].getLayoutParams();
	    	dot0Param.addRule(RelativeLayout.LEFT_OF,DOT_ID[1]);
	    	dot0Param.rightMargin = dot_gap;
	    	RelativeLayout.LayoutParams dot2Param = (android.widget.RelativeLayout.LayoutParams) dots[2].getLayoutParams();
	    	dot2Param.addRule(RelativeLayout.RIGHT_OF,DOT_ID[1]);
	    	dot2Param.leftMargin = dot_gap;
	    	
			statisticViewAdapter.onPostTask();
    		for (int i=0;i<analysisViews.length;++i)
    			analysisViews[i].onPostTask();
    		
	    	statisticView.setCurrentItem(0);
	    
    		for (int i=0;i<3;++i)
    			dots[i].setImageDrawable(dot_off);
			dots[0].setImageDrawable(dot_on);
			
			RelativeLayout.LayoutParams questionParam = (RelativeLayout.LayoutParams) questionButton.getLayoutParams();
			questionParam.topMargin =  screen.x * 23 / 480;
			questionParam.rightMargin =  screen.x * 23 / 480;
			
			RelativeLayout.LayoutParams fsParam = (RelativeLayout.LayoutParams) firstScroll.getLayoutParams();
			fsParam.topMargin = screen.x;
			
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(activity);
			boolean tested = sp.getBoolean("tested", false);
			int result = sp.getInt("latest_result", 0);
			
			if (tested){
				if (result <=1){
					showImage.setImageDrawable(getResources().getDrawable(R.drawable.statistic_show_pass));
					showText.setText(R.string.after_test_pass);
				}
				else{
					showImage.setImageDrawable(getResources().getDrawable(R.drawable.statistic_show_fail));
					showText.setText(R.string.after_test_fail);
				}
				
				shadow.setVisibility(View.VISIBLE);
				showImage.setVisibility(View.VISIBLE);
				showText.setVisibility(View.VISIBLE);
				SharedPreferences.Editor editor = sp.edit();
		    	editor.putBoolean("tested", false);
		    	editor.commit();
				Thread t = new Thread(new ShowTimer());
				t.start();
			}
			else{
				showImage.setVisibility(View.INVISIBLE);
				showText.setVisibility(View.INVISIBLE);
				shadow.setVisibility(View.INVISIBLE);
				FragmentTabs.enableTab(true);
				boolean fs = sp.getBoolean("first_scroll", true);
				if (fs){
					firstScroll.setVisibility(View.VISIBLE);
					Thread t = new Thread(new ScrollTimer());
					t.start();
				}
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
			
			LoadingBox.dismiss();
		}
	}
    
	@SuppressLint("HandlerLeak")
	private class ShowDismissHandler extends Handler{
		public void handleMessage(Message msg){
			showImage.setVisibility(View.INVISIBLE);
			showText.setVisibility(View.INVISIBLE);
			shadow.setVisibility(View.INVISIBLE);
			FragmentTabs.enableTab(true);
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(activity);
			boolean fs = sp.getBoolean("first_scroll", true);
			if (fs){
				firstScroll.setVisibility(View.VISIBLE);
				Thread t = new Thread(new ScrollTimer());
				t.start();
			}
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
	
	private class ShowTimer implements Runnable{

		@Override
		public void run() {
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
			}
			showDismissHandler.sendEmptyMessage(0);
		}
	}
	
	
	private class ScrollTimer implements Runnable{

		@Override
		public void run() {
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
			}
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
		FragmentTabs.enableTab(enable);
	}
	
	public void showEndOfQuestionnaire(){
		showImage.setImageResource(R.drawable.statistic_show_pass);
		shadow.setVisibility(View.VISIBLE);
		showImage.setVisibility(View.VISIBLE);
		showText.setVisibility(View.VISIBLE);
		showText.setText(R.string.after_questionnaire);
		Thread t = new Thread(new ShowTimer());
		t.start();
	}
	
	public void updateSelfHelpCounter(){
		try{
			AnalysisCounterView acv = (AnalysisCounterView) analysisViews[1];
			acv.updateCounter();
		}catch(Exception e){	}
	}
	
}
