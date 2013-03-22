package statisticPageView.statistics;

import statisticPageView.StatisticPageView;
import ioio.examples.hello.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class StatisticDayView extends StatisticPageView {
	
	private TextView bracValueTextView;
	private TextView bracPassTextView;
	private TextView bracFailTextView;
	
	
	public StatisticDayView(Context context){
		super(context,R.layout.statistic_day_view);
		setting();
	}
	
	private void setting(){
		bracValueTextView = (TextView) view.findViewById(R.id.statistic_day_brac_value);
		bracPassTextView = (TextView) view.findViewById(R.id.statistic_day_brac_pass);
		bracFailTextView = (TextView) view.findViewById(R.id.statistic_day_brac_fail);
		
		//dummy setting
		bracValueTextView.setText("0.00");
		bracPassTextView.setTextColor(0xFFFFFFFF);
		bracFailTextView.setTextColor(0xFF999999);
	}
}
