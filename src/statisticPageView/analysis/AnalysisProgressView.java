package statisticPageView.analysis;

import java.text.DecimalFormat;

import main.activities.Lang;
import main.activities.R;
import main.activities.StatisticFragment;
import statisticPageView.StatisticPageView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import database.HistoryDB;

public class AnalysisProgressView extends StatisticPageView {

	private TextView title;
	private ImageView title_bg;
	private Bitmap titleBmp;
	private TextView help;
	private ImageView[] bgs;
	private Bitmap[] bgBmps;
	private HistoryDB db;
	
	
	public AnalysisProgressView(Context context,StatisticFragment statisticFragment){
		super(context, R.layout.analysis_progress_view,statisticFragment);
		db = new HistoryDB(context);
	}
	
	@Override
	public void clear() {
		Log.d("CLEAR","DRUNK");
		if (titleBmp!=null && !titleBmp.isRecycled()){
			titleBmp.recycle();
			titleBmp = null;
		}
		if (bgBmps!=null){
			for (int i=0;i<3;++i){
				if (bgBmps[i]!=null && !bgBmps[i].isRecycled()){
					bgBmps[i].recycle();
					bgBmps[i] = null;
				}
			}
			bgBmps = null;
		}
	}
	
	private RelativeLayout content_layout;
	private int totalWeek = 7;
	private int currentWeek = 1;
	
	@Override
	public void onPreTask() {
		content_layout = (RelativeLayout) view.findViewById(R.id.analysis_drunk_layout);
		
		Point screen = StatisticFragment.getStatisticPx();
		
		title = (TextView) view.findViewById(R.id.analysis_drunk_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(screen.x * 44.0/720.0));
		title.setTextColor(0xFFFFFFFF);
		
		title_bg = (ImageView) view.findViewById(R.id.analysis_drunk_title_bg);
		title_bg.setScaleType(ScaleType.FIT_XY);
		
		help = (TextView) view.findViewById(R.id.analysis_drunk_help);
		help.setTextColor(0xFF545454);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 46.0/720.0));
		
	}

	@Override
	public void onInBackground() {
		currentWeek = 5; // TODO get actual progress from DB
		
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
		
		bgBmps = new Bitmap[3];
		bgBmps[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_today_pass);
		bgBmps[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_today_notpass);
		bgBmps[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_today_notyet);
		
	}

	@Override
	public void onPostTask() {
		title_bg.setImageBitmap(titleBmp);
		if (Lang.eng)
			help.setText("You have completed " + String.valueOf(currentWeek) + " weeks of treatment, "
					+ String.valueOf(totalWeek - currentWeek) + " weeks are remaining.");
		else
		help.setText("已戒酒" + String.valueOf(currentWeek) + "周，完成此療程尚餘" + String.valueOf(totalWeek - currentWeek) + "周" );
		
		Point screen = StatisticFragment.getStatisticPx();
		
		int bgMargin = (int)(screen.x * 100.0/720.0);
		int bgGap = (int)(screen.x * 18.0/720.0);
		int bgSize = (int)((screen.x - 2 * bgMargin - (totalWeek - 1) * bgGap) / totalWeek);

		bgs = new ImageView[totalWeek];
		
		for (int i=0;i<totalWeek;++i){
			
			bgs[i] = new ImageView(context);
			content_layout.addView(bgs[i]);
			RelativeLayout.LayoutParams bgParam = (LayoutParams) bgs[i].getLayoutParams();
			bgParam.width = bgSize;
			bgParam.height = bgSize;
			bgParam.leftMargin = (int)(bgMargin + (bgSize + bgGap)*i);
			bgParam.topMargin = (int)(screen.x * 231.0/720.0);
			
			if(i < currentWeek)
				bgs[i].setImageBitmap(bgBmps[0]);
			else
				bgs[i].setImageBitmap(bgBmps[2]);
			
		}
		
	}

	@Override
	public void onCancel() {
		clear();
	}	

}
