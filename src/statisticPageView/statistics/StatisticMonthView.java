package statisticPageView.statistics;

import java.util.Calendar;

import database.HistoryDB;
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

public class StatisticMonthView extends StatisticPageView {

	private HistoryDB db;
	private ImageView  lineImage1, lineImage2;
	private Bitmap block_green, block_yellow, block_red;
	private Bitmap textBmp, lineBmp1, lineBmp2; 
	private RelativeLayout mainLayout;
	private TextView help;
	private TextView[] labels;
	private TextView[] days;
	
	private ImageView[] blocks;
	
	private ImageView bg;
	private Bitmap bgBmp;
	
	public StatisticMonthView(Context context,StatisticFragment statisticFragment) {
		super(context, R.layout.statistic_month_view,statisticFragment);
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
		if (lineBmp2!=null && !lineBmp2.isRecycled()){
			lineBmp2.recycle();
			lineBmp2 = null;
		}
		if (block_red!=null && !block_red.isRecycled()){
			block_red.recycle();
			block_red = null;
		}
		if (block_yellow!=null && !block_yellow.isRecycled()){
			block_yellow.recycle();
			block_yellow = null;
		}
		if (block_green!=null && !block_green.isRecycled()){
			block_green.recycle();
			block_green = null;
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
		lineBmp1 =BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_line3);
		lineImage1 = (ImageView) view.findViewById(R.id.statistic_month_line1);
		lineImage1.setImageBitmap(lineBmp1);
		lineImage1.setScaleType(ScaleType.FIT_XY);
		
		lineBmp2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_line2);
		lineImage2 = (ImageView) view.findViewById(R.id.statistic_month_line2);
		lineImage2.setImageBitmap(lineBmp2);
		lineImage2.setScaleType(ScaleType.FIT_XY);
		
		mainLayout = (RelativeLayout) view.findViewById(R.id.statistic_month_layout);
		
		bg = (ImageView) view.findViewById(R.id.statistic_month_bg);
		
		int textSize = (int)(screen.x * 36.0/720.0);
		help = (TextView) view.findViewById(R.id.statistic_month_help);
		help.setTextColor(0xFFFFFFFF);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		
		labels = new TextView[4];
		labels[0] =  (TextView) view.findViewById(R.id.statistic_month_label_1);
		labels[1] =  (TextView) view.findViewById(R.id.statistic_month_label_2);
		labels[2] =  (TextView) view.findViewById(R.id.statistic_month_label_3);
		labels[3] =  (TextView) view.findViewById(R.id.statistic_month_label_4);
		
		for (int i=0;i<4;++i){
			labels[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			labels[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		}
		
	}

	@Override
	public void onInBackground() {
		Point screen = StatisticFragment.getStatisticPx();
		
		int lineWidth1 =screen.x;
		int lineHeight1 = (int) (screen.y*5.0/443.0);
		
		RelativeLayout.LayoutParams lineParam1 = (RelativeLayout.LayoutParams) lineImage1.getLayoutParams();
		lineParam1.height = lineHeight1;
		lineParam1.width = lineWidth1;
		lineParam1.leftMargin = 0;
		lineParam1.topMargin = blockTopMargin = (int)(screen.y*310.0/443.0);
		
		int lineWidth2 = (int) (screen.x*10.0/720.0);
		int lineHeight2 = (int) (screen.y*102.0/443.0);
		RelativeLayout.LayoutParams lineParam2 = (RelativeLayout.LayoutParams) lineImage2.getLayoutParams();
		lineParam2.height = lineHeight2;
		lineParam2.width = lineWidth2;
		lineParam2.leftMargin = (int) (screen.x * 34.0/720.0);
		lineParam2.topMargin = (int)(screen.y*158.0/443.0);
		
		block_green = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_4week_green);
		block_yellow = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_4week_yellow);
		block_red = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_4week_red);
		
		bgBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_bg2);
		
		RelativeLayout.LayoutParams helpParam = (RelativeLayout.LayoutParams) help.getLayoutParams();
		helpParam.leftMargin =  (int) (screen.x * 60.0/720.0);
		helpParam.topMargin = (int)(screen.y*61.0/443.0);
		
		for (int i=0;i<4;++i){
			RelativeLayout.LayoutParams labelParam = (RelativeLayout.LayoutParams) labels[i].getLayoutParams();
			labelParam.leftMargin = (int) (screen.x *22.0/720.0);
		}
		RelativeLayout.LayoutParams labelParam = (RelativeLayout.LayoutParams) labels[0].getLayoutParams();
		labelParam.topMargin = (int)(screen.y*115.0/443.0);
		
	}

	@Override
	public void onPostTask() {
		Point screen = StatisticFragment.getStatisticPx();
		
		int blockWidth =  (int) (screen.x * 20.0/720.0);
		int blockHeight =  (int) (screen.y * 50.0/443.0);
		int blockGap = (int)(screen.x * 2.0/720.0);
		if (blockGap < 1)
			blockGap = 1;
		
		blocks = new ImageView[28*4];
		int leftMargin = (int) (screen.x * 70.0/720.0);
		for (int i=0;i<28;++i){
			int topMargin = blockTopMargin - blockHeight - 3*blockHeight;
			for (int j=0;j<4;++j){
				int c = 4*i+j;
				blocks[c] = new ImageView(context);
				blocks[c].setScaleType(ScaleType.FIT_XY);
				mainLayout.addView(blocks[c]);
				RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) blocks[c].getLayoutParams();
				param.width = blockWidth;
				param.height = blockHeight;
				param.leftMargin = leftMargin;
				param.topMargin = topMargin;
				topMargin += blockHeight;
				if (i %7 ==6 && i != 27){
					blocks[c].setPadding(0, 0, blockGap, 0);
					blocks[c].setBackgroundColor(0xFFF97306);
				}
				
			}
			leftMargin+=blockWidth+blockGap;
		}
		
		BracGameHistory[] historys = db.getMultiDayInfo(28);
		for (int i=0;i<historys.length;++i){
			if (historys[i]==null)//MISS
				blocks[i].setImageBitmap(block_yellow);
			else if (historys[i].brac>BracDataHandler.THRESHOLD)//FAIL
				blocks[i].setImageBitmap(block_red);
			else//PASS
				blocks[i].setImageBitmap(block_green);
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
