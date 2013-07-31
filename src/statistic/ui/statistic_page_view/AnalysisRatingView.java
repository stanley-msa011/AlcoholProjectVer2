package statistic.ui.statistic_page_view;

import data.database.HistoryDB;
import data.info.RankHistory;
import statistic.data.UserLevelCollector;
import ubicomp.drunk_detection.activities.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import ubicomp.drunk_detection.fragments.StatisticFragment;

public class AnalysisRatingView extends StatisticPageView {

	private TextView title;
	
	private TextView help, help2;
	private TextView low,high, low2 , high2;
	private HistoryDB db;
	
	private ImageView bar, bar2;
	private ImageView pointer, pointer2;
	
	private NetworkTask netTask;
	
	private RelativeLayout contentLayout;
	private RelativeLayout contentLayout2;
	
	private int minLeftPointer, maxLeftPointer;
	
	private Typeface wordTypeface;
	
	private String[] helpStr;
	
	public AnalysisRatingView(Context context,StatisticFragment statisticFragment) {
		super(context, R.layout.analysis_rating_view,statisticFragment);
		db = new HistoryDB(context);
		helpStr = context.getResources().getStringArray(R.array.analysis_ranking_help);
		wordTypeface = statisticFragment.wordTypeface;
	}

	@Override
	public void clear() {
		if (netTask != null && !netTask.isCancelled()){
			netTask.cancel(true);
		}
	}

	private void setPointer(){
		
		int nPeople ,rank;
		
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		String uid = sp.getString("uid", "");
		
		RankHistory[] historys = db.getAllUsersHistory();
		if (historys == null){
			nPeople =0;
			rank = 0;
		}
		else{
			rank = historys.length-1;
			nPeople = historys.length-1;
			int tmp_rank = 0, count = 0;
			int prev_score = historys[0].score;
			
			for (int i=0;i<historys.length;++i){
				if (historys[i].score < prev_score){
					tmp_rank = count;
				}
				if (historys[i].uid.equals(uid)){
					rank = tmp_rank;
					break;
				}
				++count;
				prev_score = historys[i].score;
			}
		}
		int margin = minLeftPointer;
		
		if (nPeople == 0)
			margin = minLeftPointer + maxLeftPointer/2;
		else
			margin = (int)((maxLeftPointer - minLeftPointer)*(double)(nPeople - rank)/(double)nPeople ) + minLeftPointer;
		
		int margin2 = minLeftPointer;
		if (nPeople == 0)
			margin2 = minLeftPointer + maxLeftPointer/2;
		else{
			if (nPeople/2 < rank)
				margin2 = minLeftPointer;
			else
				margin2 = (int)((maxLeftPointer - minLeftPointer)*(double)(nPeople/2- rank)/(double)(nPeople/2) ) + minLeftPointer;
		}
		RelativeLayout.LayoutParams pointerParam = (RelativeLayout.LayoutParams)pointer.getLayoutParams();
		pointerParam.leftMargin = margin;
		RelativeLayout.LayoutParams pointerParam2 = (RelativeLayout.LayoutParams)pointer2.getLayoutParams();
		pointerParam2.leftMargin = margin2;
	}
	
	
	@Override
	public void onPreTask() {
		
		Point screen = StatisticFragment.getStatisticPx();
		
		int textSize = screen.x * 21/480;
		
		title = (TextView) view.findViewById(R.id.analysis_rating_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		title.setTypeface(wordTypeface);
		
		help = (TextView) view.findViewById(R.id.analysis_rating_help);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		help.setTypeface(wordTypeface);
		help2 = (TextView) view.findViewById(R.id.analysis_rating_help2);
		help2.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		help2.setTypeface(wordTypeface);
		
		pointer  = (ImageView) view.findViewById(R.id.analysis_rating_pointer);
		pointer2  = (ImageView) view.findViewById(R.id.analysis_rating_pointer2);
		
		low = (TextView) view.findViewById(R.id.analysis_rating_low);
		low.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		low.setTypeface(wordTypeface);
		low2 = (TextView) view.findViewById(R.id.analysis_rating_low2);
		low2.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		low2.setTypeface(wordTypeface);
		
		high = (TextView) view.findViewById(R.id.analysis_rating_high);
		high.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		high.setTypeface(wordTypeface);
		high2 = (TextView) view.findViewById(R.id.analysis_rating_high2);
		high2.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		high2.setTypeface(wordTypeface);
		
		bar = (ImageView) view.findViewById(R.id.analysis_rating_bar);
		bar2 = (ImageView) view.findViewById(R.id.analysis_rating_bar2);
		
		contentLayout = (RelativeLayout) view.findViewById(R.id.analysis_rating_content_layout);
		contentLayout2 = (RelativeLayout) view.findViewById(R.id.analysis_rating_content_layout2);
		
	}

	@Override
	public void onInBackground() {
		Point screen = StatisticFragment.getStatisticPx();
		RelativeLayout.LayoutParams titleParam = (RelativeLayout.LayoutParams)title.getLayoutParams();
		titleParam.leftMargin = screen.x * 40/480;
		
		
		LinearLayout.LayoutParams helpParam = (LinearLayout.LayoutParams)help.getLayoutParams();
		helpParam.topMargin = helpParam.bottomMargin =  screen.x * 11/480;
		helpParam.leftMargin = screen.x * 40/480;
		LinearLayout.LayoutParams helpParam2 = (LinearLayout.LayoutParams)help2.getLayoutParams();
		helpParam2.topMargin = helpParam2.bottomMargin =  screen.x * 11/480;
		helpParam2.leftMargin = screen.x * 40/480;
		
		minLeftPointer  = screen.x * 80/480;
		maxLeftPointer = minLeftPointer +  screen.x * 315/480;
		
		LinearLayout.LayoutParams contentParam = (LinearLayout.LayoutParams)contentLayout.getLayoutParams();
		contentParam.bottomMargin = screen.x * 11/480;
		LinearLayout.LayoutParams contentParam2 = (LinearLayout.LayoutParams)contentLayout2.getLayoutParams();
		contentParam2.bottomMargin =  screen.x * 11/480;
		
		RelativeLayout.LayoutParams barParam = (LayoutParams) bar.getLayoutParams();
		barParam.width = screen.x * 346/480;
		RelativeLayout.LayoutParams bar2Param = (LayoutParams) bar2.getLayoutParams();
		bar2Param.width = screen.x * 346/480;
	}

	@Override
	public void onPostTask() {
		help.setText(helpStr[0] );
		help2.setText(helpStr[1]);
		setPointer();
		netTask = new NetworkTask();
		netTask.execute();
	}

	@Override
	public void onCancel() {
		clear();
	}
	
	private RankHistory[] historys;
	private UserLevelCollector levelCollector;
	
	private class NetworkTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			levelCollector = new UserLevelCollector(view.getContext());
			historys = levelCollector.update();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			if (historys == null)
				return;
			db.cleanInteractionHistory();
			for (int i=0;i<historys.length;++i){
				db.insertInteractionHistory(historys[i]);
			}
			setPointer();
		}
		
	}
	

}
