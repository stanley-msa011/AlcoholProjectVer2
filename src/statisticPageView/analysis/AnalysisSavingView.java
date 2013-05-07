package statisticPageView.analysis;

import history.InteractionHistory;
import interaction.UserLevelCollector;
import main.activities.R;
import main.activities.StatisticFragment;
import statisticPageView.StatisticPageView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import database.HistoryDB;

public class AnalysisSavingView extends StatisticPageView {

	private TextView title;
	private ImageView title_bg;
	private Bitmap titleBmp;
	
	private TextView help;
	private HistoryDB db;
	private TextView high,low;
	
	private ImageView goalBar, currentBar;
	private Bitmap goalBarBmp, currentBarBmp;
	
	//private NetworkLoadingTask nTask;
	
	private NetworkHandler netHandler;
	
	public AnalysisSavingView(Context context,StatisticFragment statisticFragment) {
		super(context, R.layout.analysis_saving_view,statisticFragment);
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
		if (goalBarBmp!=null && !goalBarBmp.isRecycled()){
			goalBarBmp.recycle();
			goalBarBmp = null;
		}
		if (currentBarBmp!=null && !currentBarBmp.isRecycled()){
			currentBarBmp.recycle();
			currentBarBmp = null;
		}
		
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

		goalBar = (ImageView) view.findViewById(R.id.analysis_goal_bar);
		currentBar = (ImageView) view.findViewById(R.id.analysis_current_bar);
		
		high = (TextView) view.findViewById(R.id.analysis_rating_high);
		high.setTextColor(0xFF545454);
		high.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 44.0/720.0));
		low = (TextView) view.findViewById(R.id.analysis_rating_low);
		low.setTextColor(0xFF545454);
		low.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 44.0/720.0));
		
	}

	private int goalMoney;
	private int currentMoney;
	
	@Override
	public void onInBackground() {
		
		goalMoney = 10000;   // TODO get real numbers
		currentMoney = 3000; //
		
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
		
		RelativeLayout.LayoutParams currentBarParam = (RelativeLayout.LayoutParams)currentBar.getLayoutParams();
		currentBarParam.width = (int)(screen.x * 480.0/720.0 * currentMoney/goalMoney);
		currentBarParam.height = (int)(screen.x * 58.0/720.0);
		currentBarParam.leftMargin = (int)(screen.x * 121.0/720.0);
		currentBarParam.topMargin = (int)(screen.x * 200.0/720.0);
		currentBarBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_group_bg);	
		
		RelativeLayout.LayoutParams goalBarParam = (RelativeLayout.LayoutParams)goalBar.getLayoutParams();
		goalBarParam.width = (int)(screen.x * 480.0/720.0);
		goalBarParam.height = (int)(screen.x * 58.0/720.0);
		goalBarParam.leftMargin = (int)(screen.x * 121.0/720.0);
		goalBarParam.topMargin = (int)(screen.x * 320.0/720.0);
		goalBarBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_group_bg);	
		
		RelativeLayout.LayoutParams highParam = (RelativeLayout.LayoutParams)high.getLayoutParams();
		highParam.topMargin = (int)(screen.x * 200.0/720.0);
		highParam.rightMargin = (int)(screen.x * 120.0/720.0);
		RelativeLayout.LayoutParams lowParam = (RelativeLayout.LayoutParams)low.getLayoutParams();
		lowParam.topMargin = (int)(screen.x * 320.0/720.0);
		lowParam.rightMargin = (int)(screen.x * 120.0/720.0);
		
	}

	@Override
	public void onPostTask() {
		title_bg.setImageBitmap(titleBmp);
		goalBar.setImageBitmap(goalBarBmp);
		currentBar.setImageBitmap(currentBarBmp);
		
		help.setText("因戒酒所節省的酒錢");
		high.setText("您已節省 NTD$" + String.valueOf(currentMoney));
		low.setText("您的目標 NTD$" + String.valueOf(goalMoney));
		//setPointer();
		
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
		}
	}
	

}
