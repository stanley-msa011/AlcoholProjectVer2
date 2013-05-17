package statisticPageView.statistics;


import java.util.ArrayList;

import main.activities.FragmentTabs;
import main.activities.R;
import main.activities.StatisticFragment;

import statisticPageView.StatisticPageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

public class StatisticPagerAdapter extends PagerAdapter {

	private ArrayList<View> viewsList;
	private StatisticPageView[] statisticViews;
	
	public static Bitmap background;
	
	private StatisticFragment statisticFragment;
	
	public StatisticPagerAdapter(Context context,StatisticFragment statisticFragment){
		viewsList = new ArrayList<View>();
		statisticViews = new StatisticPageView[3];
		statisticViews[0] = new StatisticDayView(context,statisticFragment);
		statisticViews[1] = new StatisticWeekView(context,statisticFragment);
		statisticViews[2] = new StatisticMonthView(context,statisticFragment);
		
		viewsList.add(statisticViews[0].getView());
		viewsList.add(statisticViews[1].getView());
		viewsList.add(statisticViews[2].getView());
		
		this.statisticFragment = statisticFragment;
		
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
		for (int i=0;i<statisticViews.length;++i){
			statisticViews[i].onPreTask();
		}
	}
	public void onInBackground(){
		if (background == null || background.isRecycled()){
			Point screen = FragmentTabs.getSize();
			Bitmap tmp = BitmapFactory.decodeResource(statisticFragment.getResources(), R.drawable.statistic_background);
			background = Bitmap.createScaledBitmap(tmp, screen.x, (int) (screen.x*314.0/355.0), true);
			tmp.recycle();
		}
		for (int i=0;i<statisticViews.length;++i){
			statisticViews[i].onInBackground();
		}
	}
	
	public void onPostTask(){
		for (int i=0;i<statisticViews.length;++i){
			statisticViews[i].onPostTask();
		}
	}
	public void onCancel(){
		for (int i=0;i<statisticViews.length;++i){
			statisticViews[i].onCancel();
		}
	}
	
	public void clear(){
		Log.d("CLEAR","STATISTIC");
		for (int i=0;i<statisticViews.length;++i){
			statisticViews[i].clear();
		}
		if (background!=null && !background.isRecycled()){
			background.recycle();
			background = null;
		}
	}
	
}
