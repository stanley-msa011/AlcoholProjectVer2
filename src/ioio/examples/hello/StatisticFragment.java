package ioio.examples.hello;

import statisticPageView.StatisticPageView;
import statisticPageView.analysis.AnalysisDrunkView;
import statisticPageView.analysis.AnalysisPerformanceView;
import statisticPageView.analysis.AnalysisRatingView;
import statisticPageView.analysis.AnalysisSuccessView;
import statisticPageView.statistics.StatisticDayView;
import statisticPageView.statistics.StatisticPagerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class StatisticFragment extends Fragment {
	private View view;
	private Activity activity;
	private ViewPager statisticView;
	private PagerAdapter statisticViewAdapter;
	private LinearLayout analysisLayout;
	private ScrollView analysisView;
	private StatisticPageView[] analysisViews;
	
	static final public int TYPE_DAY = 0;
	static final public int TYPE_WEEK = 1;
	static final public int TYPE_MONTH = 2;
	
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
    	statisticView = (ViewPager) view.findViewById(R.id.brac_statistics);
    	analysisView =  (ScrollView) view.findViewById(R.id.brac_analysis);
    	statisticViewAdapter = new StatisticPagerAdapter(activity);
    	statisticView.setAdapter(statisticViewAdapter);
		statisticView.setOnPageChangeListener( new StatisticOnPagerChangeListener());
    	analysisLayout  = (LinearLayout)  view.findViewById(R.id.brac_analysis_layout);
    	
    	statisticView.setCurrentItem(1);
    	statisticView.setCurrentItem(0);
        return view;
    }
    
    private class StatisticOnPagerChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int pos) {
			analysisLayout.removeAllViews();
			if (pos == TYPE_DAY){//DAY
				analysisViews = new StatisticPageView[4];
				analysisViews[0] = new AnalysisDrunkView(activity);
				analysisViews[1] = new AnalysisSuccessView(activity,TYPE_DAY);
				analysisViews[2] = new AnalysisRatingView(activity,TYPE_DAY);
				analysisViews[3] = new AnalysisPerformanceView(activity,TYPE_DAY);
			}else if (pos == 1){//WEEK
				analysisViews = new StatisticPageView[3];
				analysisViews[0] = new AnalysisSuccessView(activity,TYPE_WEEK);
				analysisViews[1] = new AnalysisRatingView(activity,TYPE_WEEK);
				analysisViews[2] = new AnalysisPerformanceView(activity,TYPE_WEEK);
			}else{ // MONTH
				analysisViews = new StatisticPageView[3];
				analysisViews[0] = new AnalysisSuccessView(activity,TYPE_MONTH);
				analysisViews[1] = new AnalysisRatingView(activity,TYPE_MONTH);
				analysisViews[2] = new AnalysisPerformanceView(activity,TYPE_MONTH);
			}
			
			for (int i=0;i<analysisViews.length;++i)
				analysisLayout.addView(analysisViews[i].getView());
			analysisView.scrollTo(0, 0);
		}
    	
    }
    
}
