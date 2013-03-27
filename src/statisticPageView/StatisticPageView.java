package statisticPageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class StatisticPageView {
	protected Context context;
	protected View view;
	private LayoutInflater inflater;
	
	public StatisticPageView(Context context,int layout_id){
		this.context = context;
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(layout_id,null);
	}
	
	public View getView(){
		return view;
	}
}
