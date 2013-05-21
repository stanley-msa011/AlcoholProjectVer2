package statistic.statisticPageView;

import main.activities.StatisticFragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public abstract class StatisticPageView {
	protected Context context;
	protected View view;
	private LayoutInflater inflater;
	protected StatisticFragment statisticFragment;
	
	public StatisticPageView(Context context,int layout_id,StatisticFragment statisticFragment){
		this.context = context;
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(layout_id,null);
		this.statisticFragment = statisticFragment;
	}
	
	public View getView(){
		return view;
	}
	
	abstract public void onPreTask();
	abstract public void onInBackground();
	abstract public void onPostTask();
	abstract public void onCancel();
	
	abstract public void clear();
	
}
