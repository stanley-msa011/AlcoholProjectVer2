package statistic.ui.statistic_page_view;

import data.database.HistoryDB;
import data.database.StartDateCheck;
import data.info.RankHistory;
import statistic.data.UserLevelCollector;
import ubicomp.drunk_detection.activities.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import ubicomp.drunk_detection.fragments.StatisticFragment;
import ubicomp.drunk_detection.ui.Typefaces;

public class AnalysisRatingView extends StatisticPageView {

	private TextView title;
	
	private TextView help, help2;
	private TextView low,high, low2 , high2;
	private HistoryDB db;
	
	private ImageView bar, bar2;
	private ImageView pointer, pointer2;
	private ImageView arrow, arrow2;
	
	
	private NetworkTask netTask;
	
	private RelativeLayout contentLayout;
	private RelativeLayout contentLayout2;
	
	private int minLeftPointer, maxLeftPointer;
	
	private Typeface wordTypeface, wordTypefaceBold;
	
	private String[] helpStr;

	private RelativeLayout titleLayout;
	
	
	public AnalysisRatingView(Context context,StatisticFragment statisticFragment) {
		super(context, R.layout.analysis_rating_view,statisticFragment);
		db = new HistoryDB(context);
		helpStr = context.getResources().getStringArray(R.array.analysis_ranking_help);
		wordTypeface = Typefaces.getWordTypeface(context);
		wordTypefaceBold = Typefaces.getWordTypefaceBold(context);
	}

	@Override
	public void clear() {
		if (netTask != null && !netTask.isCancelled()){
			netTask.cancel(true);
		}
	}

	@SuppressWarnings("deprecation")
	private void setPointer(int p1, int p2){
		
		if (!StartDateCheck.check(context)){
			pointer.setVisibility(View.INVISIBLE);
			pointer2.setVisibility(View.INVISIBLE);
			return;
		}
		pointer.setVisibility(View.VISIBLE);
		pointer2.setVisibility(View.VISIBLE);
		int nPeople ,rank;
		int nPeopleToday, rankToday;
		
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		String uid = sp.getString("uid", "");

		
		int margin = minLeftPointer;
		
		Rank _rank = getRank(uid);
		nPeople = _rank.nPeople;
		rank = _rank.rank;
		
		Rank _rank_today = getRankToday(uid);
		nPeopleToday = _rank_today.nPeople;
		rankToday = _rank_today.rank;
		
		if (nPeople == 0)
			margin = minLeftPointer + maxLeftPointer/2;
		else
			margin = (int)((maxLeftPointer - minLeftPointer)*(double)(nPeople - rank)/(double)nPeople ) + minLeftPointer;
		
		
		int margin2 = minLeftPointer;
		if (nPeopleToday == 0)
			margin2 = minLeftPointer + maxLeftPointer/2;
		else
			margin2 = (int)((maxLeftPointer - minLeftPointer)*(double)(nPeopleToday- rankToday)/(double)nPeopleToday ) + minLeftPointer;
		
		
		RelativeLayout.LayoutParams pointerParam = (RelativeLayout.LayoutParams)pointer.getLayoutParams();
		pointerParam.leftMargin = margin;
		RelativeLayout.LayoutParams pointerParam2 = (RelativeLayout.LayoutParams)pointer2.getLayoutParams();
		pointerParam2.leftMargin = margin2;
		
		
		
		contentLayout.updateViewLayout(pointer,pointerParam);
		contentLayout2.updateViewLayout(pointer2,pointerParam2);
		
		if (p1==0 && p2==0){
			arrow.setVisibility(View.INVISIBLE);
			arrow2.setVisibility(View.INVISIBLE);
			contentLayout.invalidate();
			contentLayout2.invalidate();
			return;
		}
		
		Point screen = StatisticFragment.getStatisticPx();
		
		RelativeLayout.LayoutParams aParam = (RelativeLayout.LayoutParams)arrow.getLayoutParams();
		if (p1 > 0){
			aParam.addRule(RelativeLayout.RIGHT_OF, R.id.analysis_rating_pointer);
			aParam.leftMargin = -screen.x*100/1080;
			arrow.setVisibility(View.VISIBLE);
			if (rank == 0){
				high.setTextColor(0xFFF39700);
				high.setTypeface(wordTypefaceBold);
				high.invalidate();
			}
		}else if (p1 <0){
			aParam.addRule(RelativeLayout.RIGHT_OF, R.id.analysis_rating_pointer);
			aParam.leftMargin = 0;
			arrow.setVisibility(View.VISIBLE);
			arrow.setRotationX(0.5f);
			arrow.setRotationY(0.5f);
			arrow.setRotation(180);
		}
		
		RelativeLayout.LayoutParams aParam2 = (RelativeLayout.LayoutParams)arrow2.getLayoutParams();
		if (p2 > 0){
			aParam2.addRule(RelativeLayout.RIGHT_OF, R.id.analysis_rating_pointer2);
			aParam2.leftMargin = -screen.x*100/1080;
			arrow2.setVisibility(View.VISIBLE);
			if (rankToday == 0){
				high2.setTextColor(0xFFF39700);
				high2.setTypeface(wordTypefaceBold);
				high2.invalidate();
			}
		}else if (p2 <0){
			aParam2.addRule(RelativeLayout.RIGHT_OF, R.id.analysis_rating_pointer2);
			aParam2.leftMargin = 0;
			arrow2.setVisibility(View.VISIBLE);
			arrow2.setRotationX(0.5f);
			arrow2.setRotationY(0.5f);
			arrow2.setRotation(180);
		}
		
		if (Build.VERSION.SDK_INT < 16)
			titleLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.analysis_title_bar_highlight));
		else
			titleLayout.setBackground(context.getResources().getDrawable(R.drawable.analysis_title_bar_highlight));
		
		titleLayout.invalidate();
		contentLayout.invalidate();
		contentLayout2.invalidate();
		contentLayout.updateViewLayout(arrow,aParam);
		contentLayout2.updateViewLayout(arrow2,aParam2);
	}
	
	private class Rank{
		public int nPeople, rank;
		public Rank(int nPeople, int rank){
			this.nPeople = nPeople;
			this.rank = rank;
		}
		public double getScore(){
			if (nPeople == 0)
				return Double.MAX_VALUE;
			return (double)rank/(double)nPeople;
		}
	}
	
	private Rank getRank(String uid){
		int nPeople, rank;
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
		return new Rank(nPeople,rank);
	}
	
	private Rank getRankToday(String uid){
		int nPeople, rank;
		RankHistory[] historys = db.getAllUsersHistoryToday();
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
		return new Rank(nPeople,rank);
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
		
		arrow = (ImageView) view.findViewById(R.id.analysis_rating_arrow);
		arrow.setVisibility(View.INVISIBLE);
		arrow2 = (ImageView) view.findViewById(R.id.analysis_rating_arrow2);
		arrow2.setVisibility(View.INVISIBLE);
		
		titleLayout = (RelativeLayout) view.findViewById(R.id.analysis_rating_title_layout);
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
		setPointer(0,0);
		netTask = new NetworkTask();
		netTask.execute();
	}

	@Override
	public void onCancel() {
		clear();
	}
	
	private RankHistory[] historys;
	private RankHistory[] historys_average;
	private UserLevelCollector levelCollector;
	
	private class NetworkTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			levelCollector = new UserLevelCollector(view.getContext());
			historys = levelCollector.update();
			historys_average = levelCollector.updateAverage();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			if (historys == null || historys_average == null)
				return;
			
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
			String uid = sp.getString("uid", "");
			double prev_score = getRank(uid).getScore();
			double prev_score_today = getRankToday(uid).getScore();
			
			db.cleanInteractionHistory();
			for (int i=0;i<historys.length;++i)
				db.insertInteractionHistory(historys[i]);
			for (int i=0;i<historys_average.length;++i)
				db.insertInteractionHistoryToday(historys_average[i]);
			
			double score = getRank(uid).getScore();
			double score_today = getRankToday(uid).getScore();
			
			Log.d("Score",score+" "+prev_score);
			Log.d("Score",score_today+" "+prev_score_today);
			int p1,p2;
			if (score == prev_score)
				p1= 0;
			else if (score > prev_score)
				p1 = -1;
			else
				p1 = 1;
			
			if (score_today == prev_score_today)
				p2= 0;
			else if (score_today > prev_score_today)
				p2 = -1;
			else
				p2 = 1;
			
			setPointer(p1,p2);
		}
		
	}
	

}
