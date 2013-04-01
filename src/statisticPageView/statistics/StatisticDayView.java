package statisticPageView.statistics;

import java.text.DecimalFormat;

import new_database.HistoryDB;
import statisticPageView.StatisticPageView;
import test.data.BracDataHandler;
import history.BracGameHistory;
import history.GameHistory;
import ioio.examples.hello.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class StatisticDayView extends StatisticPageView {
	
	private TextView bracValueTextView;
	private TextView bracResultTextView;
	private HistoryDB db;
	
	public StatisticDayView(Context context){
		super(context,R.layout.statistic_day_view);
		db = new HistoryDB(context);
		setting();
	}
	
	private void setting(){
		bracValueTextView = (TextView) view.findViewById(R.id.statistic_day_brac_value);
		bracResultTextView = (TextView) view.findViewById(R.id.statistic_day_brac_result);
		
		BracGameHistory history = db.getLatestBracGameHistory();
		
		float brac = history.brac;
		
		DecimalFormat format = new DecimalFormat();
		format.setMaximumIntegerDigits(1);
		format.setMinimumIntegerDigits(1);
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);
		String output =format.format(brac); 
		
		bracValueTextView.setText(output);
		bracResultTextView.setTextColor(0xFFFFFFFF);
		if (brac > BracDataHandler.THRESHOLD){
			bracResultTextView.setText("失敗");
		}
		else{
			bracResultTextView.setText("通過");
		}
	}
}
