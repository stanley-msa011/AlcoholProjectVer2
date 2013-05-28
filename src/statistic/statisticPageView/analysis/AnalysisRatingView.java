package statistic.statisticPageView.analysis;

import database.HistoryDB;
import main.activities.R;
import main.activities.StatisticFragment;
import history.InteractionHistory;
import interaction.UserLevelCollector;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
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
	
	private RelativeLayout contentLayout;
	private RelativeLayout contentLayout2;
	
	private int minLeftPointer, maxLeftPointer;
	
	private Typeface wordTypeface;
	
	public AnalysisRatingView(Context context,StatisticFragment statisticFragment) {
		super(context, R.layout.analysis_rating_view,statisticFragment);
		db = new HistoryDB(context);
		if (netHandler==null)
			netHandler = new NetworkHandler();
		netHandler.sendEmptyMessage(0);
	}

	@Override
	public void clear() {
		if (netHandler!=null)
			netHandler.removeMessages(0);
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
		
		InteractionHistory[] historys = db.getAllUsersHistory();
		if (historys == null){
			nPeople =0;
			rank = 0;
		}
		else{
			rank = historys.length-1;
			nPeople = historys.length-1;
			int tmp_rank = 0, count = 0;
			int prev_level = historys[0].level;
			
			for (int i=0;i<historys.length;++i){
				if (historys[i].level < prev_level){
					tmp_rank = count;
				}
				if (historys[i].uid.equals(uid)){
					rank = tmp_rank;
					break;
				}
				++count;
				prev_level = historys[i].level;
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
		
		title = (TextView) view.findViewById(R.id.analysis_rating_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(screen.x * 36.0/720.0));
		title.setTypeface(wordTypeface);
		
		title_bg = (ImageView) view.findViewById(R.id.analysis_rating_title_bg);
		
		help = (TextView) view.findViewById(R.id.analysis_rating_help);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 36.0/720.0));
		help.setTypeface(wordTypeface);
		help2 = (TextView) view.findViewById(R.id.analysis_rating_help2);
		help2.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 36.0/720.0));
		help2.setTypeface(wordTypeface);
		
		bar = (ImageView) view.findViewById(R.id.analysis_rating_bar);
		pointer  = (ImageView) view.findViewById(R.id.analysis_rating_pointer);
		bar2 = (ImageView) view.findViewById(R.id.analysis_rating_bar2);
		pointer2  = (ImageView) view.findViewById(R.id.analysis_rating_pointer2);
		
		low = (TextView) view.findViewById(R.id.analysis_rating_low);
		low.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 36.0/720.0));
		low.setTypeface(wordTypeface);
		low2 = (TextView) view.findViewById(R.id.analysis_rating_low2);
		low2.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 36.0/720.0));
		low2.setTypeface(wordTypeface);
		
		high = (TextView) view.findViewById(R.id.analysis_rating_high);
		high.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 36.0/720.0));
		high.setTypeface(wordTypeface);
		high2 = (TextView) view.findViewById(R.id.analysis_rating_high2);
		high2.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 36.0/720.0));
		high2.setTypeface(wordTypeface);
		
		contentLayout = (RelativeLayout) view.findViewById(R.id.analysis_rating_content_layout);
		contentLayout2 = (RelativeLayout) view.findViewById(R.id.analysis_rating_content_layout2);
		
	}

	@Override
	public void onInBackground() {
		Point screen = StatisticFragment.getStatisticPx();
		RelativeLayout.LayoutParams titleParam = (RelativeLayout.LayoutParams)title.getLayoutParams();
		titleParam.leftMargin = (int)(screen.x * 90.0/720.0);
		
		RelativeLayout.LayoutParams titleBgParam = (RelativeLayout.LayoutParams)title_bg.getLayoutParams();
		titleBgParam.width = screen.x;
		titleBgParam.height = (int)(screen.x * 47.0/720.0);
		
		titleBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.analysis_title_bar);
		
		LinearLayout.LayoutParams helpParam = (LinearLayout.LayoutParams)help.getLayoutParams();
		helpParam.topMargin = helpParam.bottomMargin =  (int)(screen.x * 16.0/720.0);
		LinearLayout.LayoutParams helpParam2 = (LinearLayout.LayoutParams)help2.getLayoutParams();
		helpParam2.topMargin = helpParam2.bottomMargin =  (int)(screen.x * 16.0/720.0);
		
		RelativeLayout.LayoutParams barParam = (RelativeLayout.LayoutParams)bar.getLayoutParams();
		barParam.width = (int)(screen.x * 542.0/720.0);
		barParam.height = (int)(screen.x * 38.0/720.0);
		RelativeLayout.LayoutParams barParam2 = (RelativeLayout.LayoutParams)bar2.getLayoutParams();
		barParam2.width = (int)(screen.x * 542.0/720.0);
		barParam2.height = (int)(screen.x * 38.0/720.0);
		
		minLeftPointer  = (int)(screen.x * 120.0/720.0);
		barBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.analysis_rating_bar);
		maxLeftPointer = minLeftPointer +   (int)(screen.x * 465.0/720.0);
		
		RelativeLayout.LayoutParams pointerParam = (RelativeLayout.LayoutParams)pointer.getLayoutParams();
		pointerParam.width = (int)(screen.x * 4.0/720.0);
		pointerParam.height = (int)(screen.x * 38.0/720.0);
		RelativeLayout.LayoutParams pointerParam2 = (RelativeLayout.LayoutParams)pointer2.getLayoutParams();
		pointerParam2.width = (int)(screen.x * 4.0/720.0);
		pointerParam2.height = (int)(screen.x * 38.0/720.0);
		
		pointerBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.analysis_rating_pointer);
		
		
		LinearLayout.LayoutParams contentParam = (LinearLayout.LayoutParams)contentLayout.getLayoutParams();
		contentParam.bottomMargin =  (int)(screen.x * 20.0/720.0);
		LinearLayout.LayoutParams contentParam2 = (LinearLayout.LayoutParams)contentLayout2.getLayoutParams();
		contentParam2.bottomMargin =  (int)(screen.x * 20.0/720.0);
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
		
	}

	@Override
	public void onCancel() {
		clear();
	}
	
	private InteractionHistory[] historys;
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
	
	
	

}
