package statisticPageView.analysis;

import java.text.DecimalFormat;

import new_database.HistoryDB;
import history.BracGameHistory;
import ioio.examples.hello.R;
import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import statisticPageView.StatisticPageView;

public class AnalysisDrunkView extends StatisticPageView {

	private RelativeLayout[] values;
	private TextView[] texts;
	private HistoryDB db;
	private DecimalFormat format ;
	
	public AnalysisDrunkView(Context context){
		super(context, R.layout.analysis_drunk_view);
		db = new HistoryDB(context);
		format = new DecimalFormat();
		format.setMaximumIntegerDigits(1);
		format.setMinimumIntegerDigits(1);
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);
		setting();
	}
	
	private void setting(){
		values = new RelativeLayout[4];
		texts = new TextView[4];
		BracGameHistory[] historys = db.getTodayBracGameHistory();
		LinearLayout content_layout = (LinearLayout) view.findViewById(R.id.analysis_drunk_view_content);
		for (int i=0;i<4;++i){
			values[i] = new RelativeLayout(context);
			texts[i] = new TextView(context);
			texts[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			content_layout.addView(texts[i]);
			LayoutParams param = (LayoutParams) texts[i].getLayoutParams();
			param.gravity = Gravity.CENTER_HORIZONTAL;
			param.gravity = Gravity.CENTER_VERTICAL;
			param.weight = 0.25F;
			if (historys[i]==null)
				texts[i].setText("X");
			else{
				String out = format.format(historys[i].brac);
				texts[i].setText(out);
			}
			texts[i].setGravity(Gravity.CENTER);
			
		}
		
	}
}
