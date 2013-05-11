package statisticPageView.statistics;

import java.util.Calendar;

import database.HistoryDB;
import main.activities.FragmentTabs;
import main.activities.Lang;
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
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import statisticPageView.StatisticPageView;
import test.data.BracDataHandler;

public class StatisticMonthView2 extends StatisticPageView {

	private HistoryDB db;
	private ImageView  lineImage1;
	private Bitmap textBmp, lineBmp1; 
	private RelativeLayout mainLayout;
	private TextView help;
	private TextView[] labels;
	private TextView[] days;
	private ImageView[] lines;
	
	private ImageView[] blocks;
	
	private ImageView bg;
	private Bitmap bgBmp;
	
	public StatisticMonthView2(Context context,StatisticFragment statisticFragment) {
		super(context, R.layout.statistic_month_view2,statisticFragment);
		db = new HistoryDB(context);
	}

	@Override
	public void clear() {
		Log.d("CLEAR","MONTH");
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
		
		lineImage1 = (ImageView) view.findViewById(R.id.statistic_month_line1);
		
		lines = new ImageView[3];
		
		mainLayout = (RelativeLayout) view.findViewById(R.id.statistic_month_layout);
		
		bg = (ImageView) view.findViewById(R.id.statistic_month_bg);
		
		int textSize = (int)(screen.x * 36.0/720.0);
		help = (TextView) view.findViewById(R.id.statistic_month_help);
		help.setTextColor(0xFFFFFFFF);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		
		labels = new TextView[2];
		labels[0] =  (TextView) view.findViewById(R.id.statistic_month_label_1);
		labels[1] =  (TextView) view.findViewById(R.id.statistic_month_label_2);
		
		for (int i=0;i<2;++i){
			labels[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		}
		for (int i=0;i<3;++i){
			lines[i] = new ImageView(context);
			mainLayout.addView(lines[i]);
		}
		
	}

	@Override
	public void onInBackground() {

		lineBmp1 =BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_line3);
		
		Point screen = StatisticFragment.getStatisticPx();
		
		int lineWidth1 =screen.x;
		int lineHeight1 = (int) (screen.y*5.0/443.0);
		
		RelativeLayout.LayoutParams lineParam1 = (RelativeLayout.LayoutParams) lineImage1.getLayoutParams();
		lineParam1.height = lineHeight1;
		lineParam1.width = lineWidth1;
		lineParam1.leftMargin = 0;
		lineParam1.topMargin = blockTopMargin = (int)(screen.y*310.0/443.0);
		
		bgBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_bg2);
		
		RelativeLayout.LayoutParams helpParam = (RelativeLayout.LayoutParams) help.getLayoutParams();
		helpParam.leftMargin =  (int) (screen.x * 60.0/720.0);
		helpParam.topMargin = (int)(screen.y*61.0/443.0);
		
		for (int i=0;i<2;++i){
			RelativeLayout.LayoutParams labelParam = (RelativeLayout.LayoutParams) labels[i].getLayoutParams();
			labelParam.leftMargin = (int) (screen.x *22.0/720.0);
		}
		RelativeLayout.LayoutParams labelParam = (RelativeLayout.LayoutParams) labels[0].getLayoutParams();
		labelParam.topMargin = (int)(screen.y*150.0/443.0);
		labelParam = (RelativeLayout.LayoutParams) labels[1].getLayoutParams();
		labelParam.topMargin = (int)(screen.y*40.0/443.0);
		
	}

	@Override
	public void onPostTask() {
		Point screen = StatisticFragment.getStatisticPx();
		
		lineImage1.setImageBitmap(lineBmp1);
		lineImage1.setScaleType(ScaleType.FIT_XY);
		
		int blockWidth =  (int) (screen.x * 20.0/720.0);
		int blockHeight =  (int) (screen.y * 90.0/443.0);
		int blockGapVer =   (int) (screen.y * 100.0/443.0);
		int blockGap = (int)(screen.x * 2.0/720.0);
		if (blockGap < 1)
			blockGap = 1;
		
		blocks = new ImageView[28*2];
		int leftMargin = (int) (screen.x * 70.0/720.0);
		for (int i=0;i<28;++i){
			int topMargin = blockTopMargin - blockHeight - blockGapVer;
			for (int j=0;j<2;++j){
				int c = 2*i+j;
				blocks[c] = new ImageView(context);
				blocks[c].setScaleType(ScaleType.FIT_XY);
				mainLayout.addView(blocks[c]);
				RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) blocks[c].getLayoutParams();
				param.width = (int)(blockWidth*0.8);
				param.height = blockHeight;
				param.leftMargin = leftMargin;
				param.topMargin = topMargin;
				topMargin += blockGapVer;
				if (i %7 ==6 && i != 27){
					blocks[c].setPadding(0, 0, 2*blockGap, 0);
				}
				
			}
			leftMargin+=blockWidth+blockGap;
		}
		
		int leftMargin2 = (int) (screen.x * 70.0/720.0);
		
		int lineWidth3 = (int) (screen.x*5.0/720.0);
		int lineHeight3 =blockGapVer*2;
		for (int i=0;i<3;++i){
			leftMargin2+=7*(blockWidth+blockGap) - 2*blockGap;
			if (i>0)
				leftMargin2 += blockGap;
			lines[i].setBackgroundColor(0xFFFF0000);
			RelativeLayout.LayoutParams lineParam3 = (RelativeLayout.LayoutParams) lines[i].getLayoutParams();
			lineParam3.height = lineHeight3;
			lineParam3.width = lineWidth3;
			lineParam3.leftMargin = leftMargin2;;
			lineParam3.topMargin = blockTopMargin - 2*blockGapVer;
		}
		
		BracGameHistory[] historys = db.getMultiDayInfo(28);
		for (int i=0;i<historys.length;++i){
			if (historys[i]==null)//MISS
				blocks[i].setBackgroundColor(0xFFe4c626);
			else if (historys[i].brac>BracDataHandler.THRESHOLD)//FAIL
				blocks[i].setBackgroundColor(0xFFdd6325);
			else//PASS
				blocks[i].setBackgroundColor(0xFF5cb52f);
		}
		
		
		days = new TextView[2];
		leftMargin-=blockWidth+blockGap;
		int textSize =(int) (screen.x * 24.0/720.0);
		Calendar cal = Calendar.getInstance();
		long monthMillis = 86400*1000L*27L;
		for (int i=1;i>=0;--i){
			days[i] = new TextView(context);
			days[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			int date = cal.get(Calendar.DATE);
			int month = cal.get(Calendar.MONTH)+1;
			String date_str= month+"\n/"+date;
			if (month<10)
				date_str = "0"+date_str;
			days[i].setText(date_str);
			days[i].setLineSpacing(-textSize/2, 1.2F);
			days[i].setTextColor(0xFFFFFFFF);
			mainLayout.addView(days[i]);
			RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) days[i].getLayoutParams();
			param.leftMargin = leftMargin;
			param.topMargin = (int)(screen.y*312.0/443.0);
			leftMargin-=(blockWidth+blockGap)*27;
			cal.setTimeInMillis(cal.getTimeInMillis()-monthMillis);
		}
		
		bg.setImageBitmap(bgBmp);
		
		
		
		if (Lang.eng)
			help.setText("Monthly Statistic");
		else
			help.setText("四週統計測試");
		
	}

	@Override
	public void onCancel() {
		clear();
		
	}	
}
