package ioio.examples.hello;

import statisticPageView.StatisticPageView;
import statisticPageView.analysis.AnalysisDrunkView;
import statisticPageView.analysis.AnalysisRatingView;
import statisticPageView.analysis.AnalysisSuccessView;
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
import android.widget.LinearLayout;
import android.widget.TextView;

public class StatisticFragment extends Fragment {
	private View view;
	private Activity activity;
	private ViewPager statisticView;
	private PagerAdapter statisticViewAdapter;
	private LinearLayout analysisLayout;
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
    	statisticViewAdapter = new StatisticPagerAdapter(activity);
    	statisticView.setAdapter(statisticViewAdapter);
    	statisticView.setOnPageChangeListener(new StatisticOnPageChangeListener());
    	
    	statisticView.setSelected(true);
    	
    	analysisLayout  = (LinearLayout)  view.findViewById(R.id.brac_analysis_layout);
    	
    	analysisViews = new StatisticPageView[3];
		analysisViews[0] = new AnalysisDrunkView(activity);
		analysisViews[1] = new AnalysisSuccessView(activity,TYPE_DAY);
		analysisViews[2] = new AnalysisRatingView(activity,TYPE_DAY);
    	
		for (int i=0;i<analysisViews.length;++i)
			analysisLayout.addView(analysisViews[i].getView());
		
    	statisticView.setCurrentItem(1);
    	statisticView.setCurrentItem(0);
        return view;
    }
    
    
    private class StatisticOnPageChangeListener implements OnPageChangeListener{

    	TextView[] dots;
    	public StatisticOnPageChangeListener(){
    		dots = new TextView[3];
    		dots[0]=(TextView) view.findViewById(R.id.statistics_page_day);
    		dots[1]=(TextView) view.findViewById(R.id.statistics_page_week);
    		dots[2]=(TextView) view.findViewById(R.id.statistics_page_month);
    	}
    	
    	
		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			for (int i=0;i<3;++i){
				dots[i].setTextColor(0xFF9999FF);
			}
			dots[arg0].setTextColor(0xFFFFFFFF);
		}
    	
    }
}
