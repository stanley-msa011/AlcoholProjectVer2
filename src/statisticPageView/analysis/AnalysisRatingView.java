package statisticPageView.analysis;

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
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import statisticPageView.StatisticPageView;

public class AnalysisRatingView extends StatisticPageView {

	private TextView title;
	private ImageView title_bg;
	private Bitmap titleBmp;
	
	private TextView help;
	private HistoryDB db;
	private TextView high,low;
	
	private ImageView bar, pointer;
	private Bitmap barBmp, pointerBmp;
	
	//private NetworkLoadingTask nTask;
	
	private NetworkHandler netHandler;
	
	private int minLeftPointer, maxLeftPointer;
	
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
		
		RelativeLayout.LayoutParams pointerParam = (RelativeLayout.LayoutParams)pointer.getLayoutParams();
		pointerParam.leftMargin = margin;
	}
	
	
	@Override
	public void onPreTask() {
		Point screen = StatisticFragment.getStatisticPx();
		
		title = (TextView) view.findViewById(R.id.analysis_rating_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(screen.x * 44.0/720.0));
		title.setTextColor(0xFFFFFFFF);
		
		title_bg = (ImageView) view.findViewById(R.id.analysis_rating_title_bg);
		
		help = (TextView) view.findViewById(R.id.analysis_rating_help);
		help.setTextColor(0xFF545454);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 46.0/720.0));

		bar = (ImageView) view.findViewById(R.id.analysis_rating_bar);
		pointer  = (ImageView) view.findViewById(R.id.analysis_rating_pointer);
		
		high = (TextView) view.findViewById(R.id.analysis_rating_high);
		high.setTextColor(0xFF545454);
		high.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 44.0/720.0));
		low = (TextView) view.findViewById(R.id.analysis_rating_low);
		low.setTextColor(0xFF545454);
		low.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 44.0/720.0));
		
	}

	@Override
	public void onInBackground() {
		Point screen = StatisticFragment.getStatisticPx();
		RelativeLayout.LayoutParams titleParam = (RelativeLayout.LayoutParams)title.getLayoutParams();
		titleParam.height = (int)(screen.x * 50.0/720.0);
		titleParam.leftMargin = (int)(screen.x * 120.0/720.0);
		titleParam.topMargin = 0;
		
		RelativeLayout.LayoutParams titleBgParam = (RelativeLayout.LayoutParams)title_bg.getLayoutParams();
		titleBgParam.width = screen.x;
		titleBgParam.height = (int)(screen.x * 69.0/720.0);
		
		titleBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_titlebg);
		
		RelativeLayout.LayoutParams helpParam = (RelativeLayout.LayoutParams)help.getLayoutParams();
		helpParam.leftMargin = (int)(screen.x * 120.0/720.0);
		helpParam.topMargin = (int)(screen.x * 100.0/720.0);
		
		
		RelativeLayout.LayoutParams barParam = (RelativeLayout.LayoutParams)bar.getLayoutParams();
		barParam.width = (int)(screen.x * 480.0/720.0);
		barParam.height = (int)(screen.x * 58.0/720.0);
		barParam.leftMargin = minLeftPointer = (int)(screen.x * 121.0/720.0);
		barParam.topMargin = (int)(screen.x * 231.0/720.0);
		barBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_group_bg);
		maxLeftPointer = minLeftPointer +  (int)(screen.x * 420.0/720.0);
		
		RelativeLayout.LayoutParams pointerParam = (RelativeLayout.LayoutParams)pointer.getLayoutParams();
		pointerParam.width = (int)(screen.x * 75.0/720.0);
		pointerParam.height = (int)(screen.x * 120.0/720.0);
		
		pointerParam.topMargin = (int)(screen.x * 233.0/720.0);
		pointerBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_group);
		
		
		RelativeLayout.LayoutParams highParam = (RelativeLayout.LayoutParams)high.getLayoutParams();
		highParam.topMargin = (int)(screen.x * 291.0/720.0);
		highParam.rightMargin = (int)(screen.x * 120.0/720.0);
		RelativeLayout.LayoutParams lowParam = (RelativeLayout.LayoutParams)low.getLayoutParams();
		lowParam.topMargin = (int)(screen.x * 291.0/720.0);
		lowParam.leftMargin = (int)(screen.x * 120.0/720.0);
		
	}

	@Override
	public void onPostTask() {
		title_bg.setImageBitmap(titleBmp);
		bar.setImageBitmap(barBmp);
		pointer.setImageBitmap(pointerBmp);
		
		help.setText("與其他戒酒同伴的表現相比，您的表現排名" );
		high.setText("高" );
		low.setText("低" );
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
