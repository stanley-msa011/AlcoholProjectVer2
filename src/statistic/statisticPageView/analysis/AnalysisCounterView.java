package statistic.statisticPageView.analysis;

import database.HistoryDB;
import database.QuestionDB;

import statistic.statisticPageView.StatisticPageView;
import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.activities.StatisticFragment;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.Html;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AnalysisCounterView extends StatisticPageView {

	private TextView title;
	private TextView help;
	private HistoryDB hdb;
	private QuestionDB qdb;
	
	private Typeface wordTypeface;
	private String[] helpStr;
	
	public AnalysisCounterView(Context context,StatisticFragment statisticFragment){
		super(context, R.layout.analysis_counter_view,statisticFragment);
		hdb = new HistoryDB(context);
    	qdb = new QuestionDB(context);
		helpStr = context.getResources().getStringArray(R.array.analysis_counter_help);
	}
	
	@Override
	public void clear() {
	}
	
	
	
	@Override
	public void onPreTask() {
		
		wordTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/DFLiHeiStd-W3.otf");
		
		Point screen = StatisticFragment.getStatisticPx();
		
		title = (TextView) view.findViewById(R.id.analysis_counter_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,screen.x * 21/480);
		title.setTypeface(wordTypeface);
		
		help = (TextView) view.findViewById(R.id.analysis_counter_help);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 21/480);
		help.setTypeface(wordTypeface);
		
	}

	
	private static final int COUPON_COUNTER = 40;
	private String sc_message;
	
	@Override
	public void onInBackground() {
		
		
		int total_counter = 
    			hdb.getLatestAccumulatedHistoryState().getSelfHelpCounter()
    			- hdb.getLatestUsedState().getSelfHelpCounter() 
    			+ qdb.getLatestEmotion().getSelfHelpCounter() 
    			+ qdb.getLatestEmotionManage().getSelfHelpCounter() 
    			+ qdb.getLatestQuestionnaire().getSelfHelpCounter();
    	int coupon = total_counter/COUPON_COUNTER;
    	int counter = total_counter - coupon*COUPON_COUNTER;
		
    	sc_message = "<font color=#000000>"+helpStr[0]+" </font><font color=#f39700><strong>"+
    	counter+
    	"</strong></font><font color=#000000> "+helpStr[1]+" </font><font color=#f39700><strong>"+
    	coupon+
    	"</strong></font><font color=#000000></font><font color=#000000> "+helpStr[2]+"</font>";
    	
		Point screen = StatisticFragment.getStatisticPx();
		RelativeLayout.LayoutParams titleParam = (RelativeLayout.LayoutParams)title.getLayoutParams();
		titleParam.leftMargin = screen.x * 40/480;
		
		LinearLayout.LayoutParams helpParam = (LinearLayout.LayoutParams)help.getLayoutParams();
		helpParam.topMargin = helpParam.bottomMargin =  screen.x *11/480;
		helpParam.leftMargin =  screen.x * 40/480;
		
	}

	public void updateCounter(){
		int total_counter = 
    			hdb.getLatestAccumulatedHistoryState().getSelfHelpCounter()
    			- hdb.getLatestUsedState().getSelfHelpCounter() 
    			+ qdb.getLatestEmotion().getSelfHelpCounter() 
    			+ qdb.getLatestEmotionManage().getSelfHelpCounter() 
    			+ qdb.getLatestQuestionnaire().getSelfHelpCounter();
    	int coupon = total_counter/COUPON_COUNTER;
    	int counter = total_counter - coupon*COUPON_COUNTER;
		
    	sc_message = "<font color=#000000>"+helpStr[0]+" </font><font color=#f39700><strong>"+
    	counter+
    	"</strong></font><font color=#000000> "+helpStr[1]+" </font><font color=#f39700><strong>"+
    	coupon+
    	"</strong></font><font color=#000000></font><font color=#000000> "+helpStr[2]+"</font>";
    	help.setText(Html.fromHtml(sc_message));
	}
	
	@Override
	public void onPostTask() {
		help.setText(Html.fromHtml(sc_message));
	}

	@Override
	public void onCancel() {
		clear();
	}	

}
