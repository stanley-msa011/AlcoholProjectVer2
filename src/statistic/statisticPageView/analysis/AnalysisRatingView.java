package statistic.statisticPageView.analysis;

import data.rank.RankHistory;
import database.HistoryDB;
import ubicomp.drunk_detection.activities.R;
import interaction.UserLevelCollector;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import statistic.statisticPageView.StatisticPageView;
import ubicomp.drunk_detection.activities.StatisticFragment;

public class AnalysisRatingView extends StatisticPageView {

	private TextView title;
	private ImageView title_bg;
	private Bitmap titleBmp;
	
	private TextView help, help2;
	private TextView low,high, low2 , high2;
	private HistoryDB db;
	
	private ImageView bar, pointer, bar2 , pointer2;
	private Bitmap barBmp, pointerBmp;
	
	private NetworkHandler netHandler;
	private NetworkTask netTask;
	
	private RelativeLayout contentLayout;
	private RelativeLayout contentLayout2;
	
	private int minLeftPointer, maxLeftPointer;
	
	private Typeface wordTypeface;
	
	public AnalysisRatingView(Context context,StatisticFragment statisticFragment) {
		super(context, R.layout.analysis_rating_view,statisticFragment);
		db = new HistoryDB(context);
	}

	@Override
	public void clear() {
		if (title_bg!=null)
			title_bg.setImageBitmap(null);
		
		if (bar !=null)
			bar.setImageBitmap(null);
		if (pointer !=null)
			pointer.setImageBitmap(null);
		if (bar2!=null)
			bar2.setImageBitmap(null);
		if (pointer !=null)
			pointer.setImageBitmap(null);
		
		if (netHandler!=null)
			netHandler.removeMessages(0);
		
		if (netTask != null && !netTask.isCancelled()){
			netTask.cancel(true);
		}
		if (titleBmp!=null && !titleBmp.isRecycled()){
			titleBmp.recycle();
			titleBmp = null;
		}
		if (barBmp!=null && !barBmp.isRecycled()){
			barBmp.recycle();
			barBmp = null;
		}
		if (pointerBmp!=null && !pointerBmp.isRecycled()){
			pointerBmp.recycle();
			pointerBmp = null;
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
		Log.d("rating",String.valueOf(rank)+"/"+String.valueOf(nPeople));
		
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
		
		wordTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dfheistd-w3.otf");
		
		Point screen = StatisticFragment.getStatisticPx();
		
		int textSize = screen.x * 54/1080;
		
		title = (TextView) view.findViewById(R.id.analysis_rating_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		title.setTypeface(wordTypeface);
		
		title_bg = (ImageView) view.findViewById(R.id.analysis_rating_title_bg);
		
		help = (TextView) view.findViewById(R.id.analysis_rating_help);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		help.setTypeface(wordTypeface);
		help2 = (TextView) view.findViewById(R.id.analysis_rating_help2);
		help2.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		help2.setTypeface(wordTypeface);
		
		bar = (ImageView) view.findViewById(R.id.analysis_rating_bar);
		pointer  = (ImageView) view.findViewById(R.id.analysis_rating_pointer);
		bar2 = (ImageView) view.findViewById(R.id.analysis_rating_bar2);
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
		
		contentLayout = (RelativeLayout) view.findViewById(R.id.analysis_rating_content_layout);
		contentLayout2 = (RelativeLayout) view.findViewById(R.id.analysis_rating_content_layout2);
		
	}

	@Override
	public void onInBackground() {
		Point screen = StatisticFragment.getStatisticPx();
		RelativeLayout.LayoutParams titleParam = (RelativeLayout.LayoutParams)title.getLayoutParams();
		titleParam.leftMargin = screen.x * 135/1080;
		
		RelativeLayout.LayoutParams titleBgParam = (RelativeLayout.LayoutParams)title_bg.getLayoutParams();
		titleBgParam.width = screen.x;
		titleBgParam.height = screen.x * 59/1080;
		
		BitmapFactory.Options opt = new BitmapFactory.Options();
    	opt.inSampleSize = 2;
		Bitmap tmp;
    	
		tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.analysis_title_bar,opt);
		titleBmp = Bitmap.createScaledBitmap(tmp, screen.x,screen.x * 59/1080,true);
		tmp.recycle();
		
		LinearLayout.LayoutParams helpParam = (LinearLayout.LayoutParams)help.getLayoutParams();
		helpParam.topMargin = helpParam.bottomMargin =  screen.x * 24/1080;
		LinearLayout.LayoutParams helpParam2 = (LinearLayout.LayoutParams)help2.getLayoutParams();
		helpParam2.topMargin = helpParam2.bottomMargin =  screen.x * 24/1080;
		
		RelativeLayout.LayoutParams barParam = (RelativeLayout.LayoutParams)bar.getLayoutParams();
		barParam.width = screen.x * 901/1080;
		barParam.height = screen.x * 65/1080;
		RelativeLayout.LayoutParams barParam2 = (RelativeLayout.LayoutParams)bar2.getLayoutParams();
		barParam2.width =screen.x * 901/1080;
		barParam2.height = screen.x * 65/1080;
		
		minLeftPointer  = screen.x * 117/1080;
		tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.analysis_rating_bar,opt);
		barBmp = Bitmap.createScaledBitmap(tmp, barParam.width,barParam.height,true);
		maxLeftPointer = minLeftPointer +  screen.x * 850/1080;
		
		RelativeLayout.LayoutParams pointerParam = (RelativeLayout.LayoutParams)pointer.getLayoutParams();
		pointerParam.width =screen.x * 6/1080;
		pointerParam.height = screen.x * 65/1080;
		RelativeLayout.LayoutParams pointerParam2 = (RelativeLayout.LayoutParams)pointer2.getLayoutParams();
		pointerParam2.width = screen.x * 6/1080;
		pointerParam2.height = screen.x * 65/1080;
		
		pointerBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.analysis_rating_pointer);
		
		
		LinearLayout.LayoutParams contentParam = (LinearLayout.LayoutParams)contentLayout.getLayoutParams();
		contentParam.bottomMargin = screen.x * 30/1080;
		LinearLayout.LayoutParams contentParam2 = (LinearLayout.LayoutParams)contentLayout2.getLayoutParams();
		contentParam2.bottomMargin =  screen.x * 30/1080;
	}

	@Override
	public void onPostTask() {
		title_bg.setImageBitmap(titleBmp);
		bar.setImageBitmap(barBmp);
		pointer.setImageBitmap(pointerBmp);
		bar2.setImageBitmap(barBmp);
		pointer2.setImageBitmap(pointerBmp);
		
		help.setText("與其他戒酒朋友相比，您的排名為" );
		help2.setText("與AA成員相比，您的排名為" );
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
	
	@SuppressLint("HandlerLeak")
	private class NetworkHandler extends Handler{
		public void handleMessage(Message msg){
			Log.d("NetworkLoadingTask","StartLoading");
			levelCollector = new UserLevelCollector(view.getContext());
			historys = levelCollector.update();
			
			if (historys == null)
				return;
			for (int i=0;i<historys.length;++i)
				db.insertInteractionHistory(historys[i]);
			setPointer();
		}
	}
	
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
			for (int i=0;i<historys.length;++i)
				db.insertInteractionHistory(historys[i]);
			setPointer();
		}
		
	}
	

}
