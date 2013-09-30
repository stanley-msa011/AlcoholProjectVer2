package statistic.ui.statistic_page_view;


import java.util.ArrayList;

import ubicomp.drunk_detection.fragments.StatisticFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class StatisticPagerAdapter extends PagerAdapter {

	private ArrayList<View> viewsList;
	private StatisticPageView[] statisticViews;
	
	
	public StatisticPagerAdapter(Context context,StatisticFragment statisticFragment){
		viewsList = new ArrayList<View>();
		
		if (Build.VERSION.SDK_INT<14){
			statisticViews = new StatisticPageView[3];
			statisticViews[0] = new StatisticDayView(context,statisticFragment);
			statisticViews[1] = new StatisticWeekViewOld(context,statisticFragment);
			statisticViews[2] = new StatisticMonthViewOld(context,statisticFragment);
			viewsList.add(statisticViews[0].getView());
			viewsList.add(statisticViews[1].getView());
			viewsList.add(statisticViews[2].getView());
			return;
		}
		
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		Boolean debug = sp.getBoolean("debug", false);
		Boolean debug_type = sp.getBoolean("debug_type", false);
		if (debug){
			if (!debug_type){
				statisticViews = new StatisticPageView[2];
				statisticViews[0] = new DevelopView(context,statisticFragment);
				statisticViews[1] = new StatisticDayView(context,statisticFragment);
				viewsList.add(statisticViews[0].getView());
				viewsList.add(statisticViews[1].getView());
				return;
			}else{
				statisticViews = new StatisticPageView[2];
				statisticViews[0] = new DevelopNormalView(context,statisticFragment);
				statisticViews[1] = new StatisticDayView(context,statisticFragment);
				viewsList.add(statisticViews[0].getView());
				viewsList.add(statisticViews[1].getView());
				return;
			}
		}
		
		statisticViews = new StatisticPageView[3];
		statisticViews[0] = new StatisticDayView(context,statisticFragment);
		statisticViews[1] = new StatisticWeekView(context,statisticFragment);
		statisticViews[2] = new StatisticMonthView(context,statisticFragment);
		
		viewsList.add(statisticViews[0].getView());
		viewsList.add(statisticViews[1].getView());
		viewsList.add(statisticViews[2].getView());
		
	}
	
	
	@Override
	public int getCount() {
		return viewsList.size();
	}

	@Override  
    public Object instantiateItem(View collection, int position) {  
          
        ((ViewPager) collection).addView(viewsList.get(position),0);  
          
        return viewsList.get(position);  
    }  
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0==(View)arg1;  
	}
	@Override  
    public void destroyItem(View collection, int position, Object view) {  
        ((ViewPager) collection).removeView(viewsList.get(position));  
    } 
	
	public void onPreTask(){
		for (int i=0;i<statisticViews.length;++i)
			statisticViews[i].onPreTask();
	}
	public void onInBackground(){
		for (int i=0;i<statisticViews.length;++i)
			statisticViews[i].onInBackground();
	}
	
	public void onPostTask(){
		for (int i=0;i<statisticViews.length;++i)
			statisticViews[i].onPostTask();
	}
	public void onCancel(){
		for (int i=0;i<statisticViews.length;++i)
			statisticViews[i].onCancel();
	}
	
	public void clear(){
		for (int i=0;i<statisticViews.length;++i)
			statisticViews[i].clear();
	}
	
}
