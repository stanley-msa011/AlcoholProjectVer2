package statisticPageView.analysis;

import java.util.Calendar;

import database.HistoryDB;
import main.activities.R;
import main.activities.StatisticFragment;
import history.InteractionHistory;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import statisticPageView.StatisticPageView;

public class AnalysisRestView extends StatisticPageView {

	private TextView title;
	private ImageView title_bg;
	private Bitmap titleBmp;
	
	private TextView help;
	private HistoryDB db;
	private TextView from,to;
	
	private ImageView[] circles;
	
	public AnalysisRestView(Context context,StatisticFragment statisticFragment) {
		super(context, R.layout.analysis_rest_view,statisticFragment);
		db = new HistoryDB(context);
	}

	@Override
	public void clear() {
		if (titleBmp!=null && !titleBmp.isRecycled()){
			titleBmp.recycle();
			titleBmp = null;
		}
		
	}

	private void setPointer(){

		Calendar from_cal = db.getFirstTestDate();
		
		if (from_cal == null){
			help.setText("請完成第一次測試才能計算完成度" );
			from.setText("?" );
			to.setText("?" );
			return;
		}
		Calendar to_cal = Calendar.getInstance();
		long ts_from = from_cal.getTimeInMillis();
		long ts_to = ts_from + 86400*7*12*1000L;
		to_cal.setTimeInMillis(ts_to);
		
		Calendar cal = Calendar.getInstance();
		long ts = cal.getTimeInMillis();
		int done = (int)((ts - ts_from)/(86400*7*1000L));
		int rest = 12 - done;
		
		for (int i=0;i<done;++i)
			circles[i].setImageResource(R.drawable.complete_circle);
		for (int i=done; i<circles.length;++i)
			circles[i].setImageResource(R.drawable.complete_ring);
		
		String readme = "已戒酒 "  + done + " 周,療程尚餘 "+ rest +" 周"; 
		help.setText(readme);
		String from_str = (from_cal.get(Calendar.MONTH)+1) + "/" + from_cal.get(Calendar.DATE);
		String to_str = (to_cal.get(Calendar.MONTH)+1) + "/" + to_cal.get(Calendar.DATE);
		from.setText(from_str );
		to.setText(to_str);
	}
	
	
	@Override
	public void onPreTask() {
		Point screen = StatisticFragment.getStatisticPx();
		
		title = (TextView) view.findViewById(R.id.analysis_rest_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(screen.x * 44.0/720.0));
		title.setTextColor(0xFFFFFFFF);
		
		title_bg = (ImageView) view.findViewById(R.id.analysis_rest_title_bg);
		
		help = (TextView) view.findViewById(R.id.analysis_rest_help);
		help.setTextColor(0xFF545454);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 46.0/720.0));

		circles = new ImageView[12];
		for (int i=0;i<circles.length;++i){
			circles[i] = new ImageView(context);
			RelativeLayout v = (RelativeLayout) view.findViewById(R.id.analysis_rest_layout);
			v.addView(circles[i]);
		}
		from = (TextView) view.findViewById(R.id.analysis_rest_from);
		from.setTextColor(0xFF545454);
		from.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 44.0/720.0));
		to = (TextView) view.findViewById(R.id.analysis_rest_to);
		to.setTextColor(0xFF545454);
		to.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 44.0/720.0));
		
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
		helpParam.topMargin = (int)(screen.x * 100.0/720.0);
		
		RelativeLayout.LayoutParams highParam = (RelativeLayout.LayoutParams)from.getLayoutParams();
		highParam.topMargin = (int)(screen.x * 250.0/720.0);
		highParam.leftMargin = (int)(screen.x * 30.0/720.0);
		RelativeLayout.LayoutParams lowParam = (RelativeLayout.LayoutParams)to.getLayoutParams();
		lowParam.topMargin = (int)(screen.x * 250.0/720.0);
		lowParam.rightMargin = (int)(screen.x * 30.0/720.0);
		
		int topMargin =  (int)(screen.x * 200.0/720.0);
		int leftMargin =  (int)(screen.x * 38.0/720.0);
		int width = (int)(screen.x * 40.0/720.0);
		int gap = (int)(screen.x * 55.0/720.0);
		
		for (int i=0;i<circles.length;++i){
			RelativeLayout.LayoutParams cParam = (RelativeLayout.LayoutParams) circles[i].getLayoutParams();
			cParam.topMargin = topMargin;
			cParam.leftMargin = leftMargin;
			cParam.width = width;
			cParam.height = width;
			leftMargin += gap;
		}
		
	}

	@Override
	public void onPostTask() {
		title_bg.setImageBitmap(titleBmp);
		
		
		setPointer();
		
	}

	@Override
	public void onCancel() {
		clear();
	}

}
