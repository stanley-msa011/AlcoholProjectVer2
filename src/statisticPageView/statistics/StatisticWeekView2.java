package statisticPageView.statistics;


import java.util.Calendar;

import database.HistoryDB;
import main.activities.R;
import main.activities.StatisticFragment;
import history.BracGameHistory;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import statisticPageView.StatisticPageView;
import test.data.BracDataHandler;

public class StatisticWeekView2 extends StatisticPageView {

	private HistoryDB db;
	private ImageView textImage, lineImage1;
	private Bitmap textBmp, lineBmp1; 
	private RelativeLayout mainLayout;
	private TextView help;
	private TextView[] labels;
	private TextView[] days;
	
	private ImageView bg;
	private Bitmap bgBmp;
	
	private ImageView[] blocks;
	
	private static final String[] WEEK_LABEL = {"日","一","二","三","四","五","六"};
	
	public StatisticWeekView2(Context context,StatisticFragment statisticFragment) {
		super(context, R.layout.statistic_week_view2, statisticFragment);
		db = new HistoryDB(context);
	}


	@Override
	public void clear() {
		Log.d("CLEAR","WEEK");
		
		if (textBmp!=null && !textBmp.isRecycled()){
			textBmp.recycle();
			textBmp = null;
		}
		if (lineBmp1!=null && !lineBmp1.isRecycled()){
			lineBmp1.recycle();
			lineBmp1 = null;
		}
		if (bgBmp!=null && !bgBmp.isRecycled()){
			bgBmp.recycle();
			bgBmp = null;
		}
	}
	
	private int blockTopMargin;
	@Override
	public void onPreTask() {
		Point screen = StatisticFragment.getStatisticPx();
		textImage = (ImageView) view.findViewById(R.id.statistic_week_text);
		textImage.setScaleType(ScaleType.FIT_XY);
		
		lineImage1 = (ImageView) view.findViewById(R.id.statistic_week_line1);
		lineImage1.setScaleType(ScaleType.FIT_XY);
		
		mainLayout = (RelativeLayout) view.findViewById(R.id.statistic_week_layout);
		
		int textSize = (int)(screen.x * 36.0/720.0);
		help = (TextView) view.findViewById(R.id.statistic_week_help);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		
		labels = new TextView[2];
		labels[0] =  (TextView) view.findViewById(R.id.statistic_week_label_1);
		labels[1] =  (TextView) view.findViewById(R.id.statistic_week_label_2);
		
		for (int i=0;i<2;++i){
			labels[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		}
			
		bg = (ImageView) view.findViewById(R.id.statistic_week_bg);
		days = new TextView[7];
		
	}


	@Override
	public void onInBackground() {
		
		Point screen = StatisticFragment.getStatisticPx();
		
		int textWidth = (int) (screen.x*120.0/720.0);
		int textHeight = (int) (textWidth*106.0/120.0);
		
		textBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_aweek_text);
		RelativeLayout.LayoutParams textParam = (RelativeLayout.LayoutParams) textImage.getLayoutParams();
		textParam.height = textHeight;
		textParam.width = textWidth;
		textParam.leftMargin = (int) (screen.x * 560.0/720.0);
		textParam.topMargin = (int)(screen.y*163.0/443.0);
		
		int lineWidth1 = screen.x;
		int lineHeight1 = (int) (screen.y*5.0/443.0);
		lineBmp1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_line3);
		
		RelativeLayout.LayoutParams lineParam1 = (RelativeLayout.LayoutParams) lineImage1.getLayoutParams();
		lineParam1.height = lineHeight1;
		lineParam1.width = lineWidth1;
		lineParam1.topMargin = blockTopMargin = (int)(screen.y*310.0/443.0);
		
		bgBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_bg2);
		
		RelativeLayout.LayoutParams helpParam = (RelativeLayout.LayoutParams) help.getLayoutParams();
		helpParam.leftMargin =  (int) (screen.x * 60.0/720.0);
		helpParam.topMargin = (int)(screen.y*61.0/443.0);
		
		for (int i=0;i<2;++i){
			RelativeLayout.LayoutParams labelParam = (RelativeLayout.LayoutParams) labels[i].getLayoutParams();
			labelParam.leftMargin = (int) (screen.x * 22.0/720.0);
		}
		RelativeLayout.LayoutParams labelParam = (RelativeLayout.LayoutParams) labels[0].getLayoutParams();
		labelParam.topMargin = (int)(screen.y*150.0/443.0);
		labelParam = (RelativeLayout.LayoutParams) labels[1].getLayoutParams();
		labelParam.topMargin = (int)(screen.y*40.0/443.0);
	}


	@Override
	public void onPostTask() {
		textImage.setImageBitmap(textBmp);
		lineImage1.setImageBitmap(lineBmp1);
		
		Point screen = StatisticFragment.getStatisticPx();
		
		int blockWidth =  (int) (screen.x * 44.0/720.0);
		int blockHeight =  (int) (screen.y * 90.0/443.0);
		int blockGapVer =  (int) (screen.y * 100.0/443.0);
		int blockGap = (int)(screen.x * 30.0/720.0);
		if (blockGap < 1)
			blockGap = 1;
		
		blocks = new ImageView[14];
		int leftMargin = (int) (screen.x * 70.0/720.0);
		for (int i=0;i<7;++i){
			int topMargin =blockTopMargin - blockHeight - blockGapVer;
			for (int j=0;j<2;++j){
				int c = 2*i+j;
				blocks[c] = new ImageView(context);
				mainLayout.addView(blocks[c]);
				RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) blocks[c].getLayoutParams();
				param.width = blockWidth;
				param.height = blockHeight;
				param.leftMargin = leftMargin;
				param.topMargin = topMargin;
				topMargin += blockGapVer;
			}
			leftMargin+=blockWidth+blockGap;
		}
		
		
		BracGameHistory[] historys = db.getMultiDayInfo(7);
		for (int i=0;i<historys.length;++i){
			if (historys[i]==null)//MISS
				blocks[i].setBackgroundColor(0xFFe4c626);
			else if (historys[i].brac>BracDataHandler.THRESHOLD)//FAIL
				blocks[i].setBackgroundColor(0xFFdd6325);
			else//PASS
				blocks[i].setBackgroundColor(0xFF5cb52f);
		}
		
		leftMargin-=blockWidth+blockGap;
		int textSize =(int) (screen.x * 36.0/720.0);
		Calendar cal = Calendar.getInstance();
		long dateMillis = 86400*1000L;
		for (int i=6;i>=0;--i){
			days[i] = new TextView(context);
			days[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
			String week_str = WEEK_LABEL[day_of_week-1];
			days[i].setText(week_str);
			days[i].setTextColor(0xFFFFFFFF);
			mainLayout.addView(days[i]);
			RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) days[i].getLayoutParams();
			param.leftMargin = leftMargin;
			param.topMargin = (int)(screen.y*308.0/443.0);
			leftMargin-=blockWidth+blockGap;
			cal.setTimeInMillis(cal.getTimeInMillis()-dateMillis);
		}
		
		bg.setImageBitmap(bgBmp);
		
		help.setText("一週統計測試");
	}


	@Override
	public void onCancel() {
		clear();
	}
	
	
}
