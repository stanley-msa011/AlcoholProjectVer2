package main.activities;

import statistic.statisticPageView.StatisticPageView;
import statistic.statisticPageView.analysis.AnalysisProgressView;
import statistic.statisticPageView.analysis.AnalysisRatingView;
import statistic.statisticPageView.analysis.AnalysisSavingView;
import statistic.statisticPageView.statistics.StatisticPagerAdapter;
import statistic.ui.QuestionMsgBox;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import clicklog.ClickLogger;

public class StatisticFragment extends Fragment {
	private View view;
	private Activity activity;
	private ViewPager statisticView;
	private StatisticPagerAdapter statisticViewAdapter;
	private RelativeLayout statisticLayout,dots_layout;
	private ImageView[] dots;
	private LinearLayout analysisLayout;
	private StatisticPageView[] analysisViews;
	private static Point statistic_px,analysis_px;
	private ScrollView analysisView;
	private Bitmap dot_on, dot_off;
	private LoadingHandler loadHandler;
	private StatisticFragment statisticFragment;
	
	private RelativeLayout questionLayout;
	private ImageView questionButton;
	private Bitmap questionButtonBmp;
	
	private ImageView showImage;
	private Bitmap showImageBmp;
	private TextView showText;
	
	private Typeface wordTypeface;
	
	private ShowDismissHandler showDismissHandler;
	
	private static final int[] DOT_ID={0xFF0,0xFF1,0xFF2};
	
	private AlphaAnimation questionAnimation;
	
	private QuestionMsgBox msgBox;
	
	// For Click Sequence Logging
	private ClickLogger clickLogger;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Statistic Fragment","onCreate");
        activity = this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	view = inflater.inflate(R.layout.statistic_fragment, container,false);
    	if (view == null){
    		Log.d("STATISTIC FRAGMENT","VIEW NULL");
    	}
        return view;
    }
    
    public void onResume(){
    	super.onResume();
    	
    	clickLogger = new ClickLogger();
    	
    	statisticFragment = this;
    	
    	analysisViews = new StatisticPageView[3];
    	analysisViews[0] = new AnalysisProgressView(activity, statisticFragment);
    	analysisViews[1] = new AnalysisSavingView(activity, statisticFragment);
		analysisViews[2] = new AnalysisRatingView(activity,statisticFragment);
    	
		statisticViewAdapter = new StatisticPagerAdapter(activity,statisticFragment);
		
		msgBox = new QuestionMsgBox(statisticFragment,(RelativeLayout) view);
		
		if (loadHandler==null)
			loadHandler = new LoadingHandler();
		
		if (showDismissHandler == null)
			showDismissHandler = new ShowDismissHandler();
		
		loadHandler.sendEmptyMessage(0);
    }
    
    public void onPause(){
    	if (loadHandler!=null)
    		loadHandler.removeMessages(0);
    	clear();
    	super.onPause();
    }
    
    private void clear(){
    	if (dot_on!=null && !dot_on.isRecycled()){
    		dot_on.recycle();
    		dot_on=null;
    	}
    	if (dot_off!=null && !dot_off.isRecycled()){
    		dot_off.recycle();
    		dot_off=null;
    	}
    	if (questionButtonBmp !=null && !questionButtonBmp.isRecycled()){
    		questionButtonBmp.recycle();
    		questionButtonBmp = null;
    	}
    	if (showImageBmp !=null && !showImageBmp.isRecycled()){
    		showImageBmp.recycle();
    		showImageBmp = null;
    	}
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
			//Log.d("Eric", "Scrolled:" + arg0 + "," + arg1 + "," + arg2);
		}

		@Override
		public void onPageSelected(int arg0) {
			
			clickLogger.click_logging(System.currentTimeMillis(), "RecordStatisticPage" + (arg0+1) + "_scrolled");
			
			for (int i=0;i<3;++i)
				dots[i].setImageBitmap(dot_off);
			dots[arg0].setImageBitmap(dot_on);
		}
    	
    }
    
	@SuppressLint("HandlerLeak")
	private class LoadingHandler extends Handler{
		public void handleMessage(Message msg){
			FragmentTabs.enableTab(false);
			
        	Point screen = FragmentTabs.getSize();
        	statistic_px = new Point(screen.x,(int) (screen.x*314.0/355.0));
        	
        	statisticLayout = (RelativeLayout) view.findViewById(R.id.brac_statistics_layout);
        	statisticView = (ViewPager) view.findViewById(R.id.brac_statistics);
        	statisticView.setAdapter(statisticViewAdapter);
        	statisticView.setOnPageChangeListener(new StatisticOnPageChangeListener());
        	statisticView.setSelected(true);
        	analysisLayout  = (LinearLayout)  view.findViewById(R.id.brac_analysis_layout);
        	analysisLayout.removeAllViews();
        	analysisView = (ScrollView) view.findViewById(R.id.brac_analysis);
        	dots_layout = (RelativeLayout) view.findViewById(R.id.brac_statistics_dots);
        	
        	wordTypeface = Typeface.createFromAsset(activity.getAssets(), "fonts/dfheistd-w3.otf");
        	
        	questionLayout = (RelativeLayout) view.findViewById(R.id.question_layout);
    		questionButton = (ImageView) view.findViewById(R.id.question_background);
        	questionLayout.setOnClickListener(new QuestionOnClickListener());
    		
    		
    		showImage = (ImageView) view.findViewById(R.id.statistic_picture);
    		showText = (TextView) view.findViewById(R.id.statistic_text);
    		showText.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 42.0/720.0));
    		showText.setTypeface(wordTypeface);
    		
        	for (int i=0;i<analysisViews.length;++i)
    			analysisLayout.addView(analysisViews[i].getView());
    		
    		statisticViewAdapter.onPreTask();
    		for (int i=0;i<analysisViews.length;++i)
    			analysisViews[i].onPreTask();
    		
			LayoutParams statisticViewLayoutParam =  statisticLayout.getLayoutParams();
        	statisticViewLayoutParam.height = statistic_px.y;
			
        	LayoutParams analysisViewLayoutParam =  analysisView.getLayoutParams();
        	analysisViewLayoutParam.height = (int)(screen.x*555.0/355.0) - statistic_px.y;
        	
	    	dot_on = BitmapFactory.decodeResource(activity.getResources(), R.drawable.statistic_dot_on);
	    	dot_off = BitmapFactory.decodeResource(activity.getResources(), R.drawable.statistic_dot_off);
			
	    	RelativeLayout.LayoutParams dotsLayoutParam = (android.widget.RelativeLayout.LayoutParams) dots_layout.getLayoutParams();
	    	dotsLayoutParam.topMargin = (int)(statistic_px.y*0.9); 
	    	
			statisticViewAdapter.onInBackground();
    		for (int i=0;i<analysisViews.length;++i)
    			analysisViews[i].onInBackground();
			
			int dot_size = (int) (screen.x*18.0/720.0);
	    	int dot_gap = (int) (dot_size);
			
	    	dots = new ImageView[3];
	    	for (int i=0;i<3;++i){
	    		dots[i] = new ImageView(dots_layout.getContext());
	    		dots[i].setScaleType(ScaleType.FIT_XY);
	    		dots_layout.addView(dots[i]);
	    		RelativeLayout.LayoutParams dotParam = (android.widget.RelativeLayout.LayoutParams) dots[i].getLayoutParams();
	    		dotParam.width = dot_size;
	    		dotParam.height = dot_size;
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
	    	
	    	//Bugs happened here
	    	for (int i=0;i<3;++i)
				dots[i].setImageBitmap(dot_off);
			dots[0].setImageBitmap(dot_on);
			
			Bitmap tmp;
			if (questionButtonBmp==null ||questionButtonBmp.isRecycled()){
				tmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.test_tutorial_button);
				questionButtonBmp = Bitmap.createScaledBitmap(tmp, (int)(screen.x * 62.0/720.0), (int)(screen.x * 62.0/720.0), true);
				tmp.recycle();
			}
			RelativeLayout.LayoutParams questionLayoutParam = (RelativeLayout.LayoutParams) questionLayout.getLayoutParams();
			questionLayoutParam.width = (int)(screen.x * 62.0/720.0);
			questionLayoutParam.height = (int)(screen.x * 62.0/720.0);
			questionLayoutParam.topMargin = (int)(screen.x * 40.0/720.0);
			questionLayoutParam.rightMargin = (int)(screen.x * 40.0/720.0);
			
			if (questionButtonBmp!=null && !questionButtonBmp.isRecycled())
				questionButton.setImageBitmap(questionButtonBmp);
			
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(activity);
			boolean tested = sp.getBoolean("tested", false);
			int result = sp.getInt("latest_result", 0);
			
			RelativeLayout.LayoutParams showParam = (RelativeLayout.LayoutParams) showImage.getLayoutParams();
			showParam.width = (int)(screen.x * 240.0/720.0);
			showParam.height = (int)(screen.x * 240.0/720.0);
			
			if (tested){
				if (result <=1){
					//tmp= BitmapFactory.decodeResource(activity.getResources(), R.drawable.statistic_show_pass2);
					//showText.setText("做得好，請繼續加油");
					showImage.setImageResource(R.drawable.statistic_show_pass2);
				}
				else{
					//tmp= BitmapFactory.decodeResource(activity.getResources(), R.drawable.statistic_show_fail2);
					//showText.setText("請繼續加油");
					showImage.setImageResource(R.drawable.statistic_show_fail2);
				}
				//showImageBmp = Bitmap.createScaledBitmap(tmp, (int)(screen.x * 240.0/720.0), (int)(screen.x * 240.0/720.0), true);
				//tmp.recycle();
				//showImage.setImageBitmap(showImageBmp);
				
				showImage.setVisibility(View.VISIBLE);
				//showText.setVisibility(View.VISIBLE);
				SharedPreferences.Editor editor = sp.edit();
		    	editor.putBoolean("tested", false);
		    	editor.commit();
				Thread t = new Thread(new ShowTimer());
				t.start();
			}
			else{
				showImage.setVisibility(View.INVISIBLE);
				showText.setVisibility(View.INVISIBLE);
				FragmentTabs.enableTab(true);
				FragmentTabs.changeTab(1);
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
			FragmentTabs.enableTab(true);
			//FragmentTabs.changeTab(1);
		}
	}
	
	private class ShowTimer implements Runnable{

		@Override
		public void run() {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			showDismissHandler.sendEmptyMessage(0);
		}
	}
	
	private class QuestionOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(activity);
			int result = sp.getInt("latest_result", 0);
			if (msgBox == null)
				return;
			if (result == 0){
				msgBox.generateType0Box();
			}
			else if (result == 1){
				msgBox.generateType1Box();
			}else if (result == 2){
				msgBox.generateType2Box();
			}else if (result == 3){
				msgBox.generateType3Box();
			}
		}
		
	} 
	
	public void setQuestionAnimation(){
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(activity);
		int result = sp.getInt("latest_result", 0);
		if (result <=1){
			questionAnimation.cancel();
			questionLayout.setAnimation(null);
			questionLayout.setAlpha(1.0F);
		}
		else{
			questionLayout.setAnimation(questionAnimation);
			questionAnimation.start();
		}
	}
}
