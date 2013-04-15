package statisticPageView.statistics;


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

public class StatisticWeekView extends StatisticPageView {

	private HistoryDB db;
	private ImageView textImage, lineImage1, lineImage2;
	private Bitmap block_green, block_yellow, block_red;
	private Bitmap textBmp, lineBmp1, lineBmp2; 
	private RelativeLayout mainLayout;
	private TextView help;
	
	private ImageView bg;
	private Bitmap bgBmp;
	
	private ImageView[] blocks;
	
	
	public StatisticWeekView(Context context,StatisticFragment statisticFragment) {
		super(context, R.layout.statistic_week_view, statisticFragment);
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
		textImage = (ImageView) view.findViewById(R.id.statistic_week_text);
		textImage.setScaleType(ScaleType.FIT_XY);
		
		lineImage1 = (ImageView) view.findViewById(R.id.statistic_week_line1);
		lineImage1.setScaleType(ScaleType.FIT_XY);
		
		lineImage2 = (ImageView) view.findViewById(R.id.statistic_week_line2);
		lineImage2.setScaleType(ScaleType.FIT_XY);
		
		mainLayout = (RelativeLayout) view.findViewById(R.id.statistic_week_layout);
		
		help = (TextView) view.findViewById(R.id.statistic_week_help);
		help.setTextColor(0xFFFFFFFF);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 36.0/720.0));
		
		bg = (ImageView) view.findViewById(R.id.statistic_week_bg);
		
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
		
		int lineWidth1 = (int) (screen.x*557.0/720.0);
		int lineHeight1 = (int) (screen.y*5.0/443.0);
		lineBmp1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_line);
		
		RelativeLayout.LayoutParams lineParam1 = (RelativeLayout.LayoutParams) lineImage1.getLayoutParams();
		lineParam1.height = lineHeight1;
		lineParam1.width = lineWidth1;
		lineParam1.leftMargin = (int) (screen.x * 90.0/720.0);
		lineParam1.topMargin = blockTopMargin = (int)(screen.y*312.0/443.0);
		
		int lineWidth2 = (int) (screen.x*10.0/720.0);
		int lineHeight2 = (int) (screen.y*102.0/443.0);
		lineBmp2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_line2);
		
		RelativeLayout.LayoutParams lineParam2 = (RelativeLayout.LayoutParams) lineImage2.getLayoutParams();
		lineParam2.height = lineHeight2;
		lineParam2.width = lineWidth2;
		lineParam2.leftMargin = (int) (screen.x * 160.0/720.0);
		lineParam2.topMargin = (int)(screen.y*158.0/443.0);
		
		block_green = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_aweek_green);
		block_yellow = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_aweek_yellow);
		block_red = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_aweek_red);
		
		bgBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_bg2);
		
		RelativeLayout.LayoutParams helpParam = (RelativeLayout.LayoutParams) help.getLayoutParams();
		helpParam.leftMargin =  (int) (screen.x * 130.0/720.0);
		helpParam.topMargin = (int)(screen.y*310.0/443.0);
		
		
	}


	@Override
	public void onPostTask() {
		textImage.setImageBitmap(textBmp);
		lineImage1.setImageBitmap(lineBmp1);
		lineImage2.setImageBitmap(lineBmp2);
		
		Point screen = StatisticFragment.getStatisticPx();
		
		int blockWidth =  (int) (screen.x * 44.0/720.0);
		int blockHeight =  (int) (screen.y * 50.0/443.0);
		int blockGap = (int)(screen.x * 2.0/720.0);
		if (blockGap < 1)
			blockGap = 1;
		
		blocks = new ImageView[28];
		int leftMargin = (int) (screen.x * 204.0/720.0);
		for (int i=0;i<7;++i){
			int topMargin =blockTopMargin - blockHeight;
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
				topMargin -= blockHeight;
			}
			leftMargin+=blockWidth+blockGap;
		}
		
		
		BracGameHistory[] historys = db.getMultiDayInfo(7);
		for (int i=0;i<historys.length;++i){
			if (historys[i]==null)//MISS
				blocks[i].setImageBitmap(block_yellow);
			else if (historys[i].brac>BracDataHandler.THRESHOLD)//FAIL
				blocks[i].setImageBitmap(block_red);
			else//PASS
				blocks[i].setImageBitmap(block_green);
		}
		
		bg.setImageBitmap(bgBmp);
		
		help.setText("一週統計測試");
	}


	@Override
	public void onCancel() {
		clear();
	}
	
	
}
