package statisticPageView;

import ioio.examples.hello.StatisticFragment;
import android.content.Context;
import android.os.AsyncTask;
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
	
	abstract public void clear();
	
}
