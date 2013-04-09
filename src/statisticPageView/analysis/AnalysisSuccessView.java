package statisticPageView.analysis;


import new_database.HistoryDB;
import history.BracGameHistory;
import ioio.examples.hello.R;
import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import statisticPageView.StatisticPageView;

public class AnalysisSuccessView extends StatisticPageView {

	private HistoryDB db;
	private TextView text;
	
	public AnalysisSuccessView(Context context,int type) {
		super(context,R.layout.analysis_success_view);
		db = new HistoryDB(context);
		setting();
	}
	
	private void setting(){
		int score = db.getAllBracGameScore();
		text = (TextView) view.findViewById(R.id.analysis_success_score);
		String score_str = String.valueOf(score);
		text.setText(score_str);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

}
