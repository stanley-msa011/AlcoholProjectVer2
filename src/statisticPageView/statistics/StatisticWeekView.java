package statisticPageView.statistics;

import new_database.HistoryDB;
import history.BracGameHistory;
import ioio.examples.hello.R;
import ioio.examples.hello.StatisticFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import statisticPageView.StatisticPageView;
import test.data.BracDataHandler;

public class StatisticWeekView extends StatisticPageView {

	private HistoryDB db;
	private ImageView textImage, lineImage1, lineImage2;
	private Bitmap block_green, block_yellow, block_red;
	private Bitmap textBmp, lineBmp1, lineBmp2; 
	private RelativeLayout mainLayout;
	
	private ImageView[] blocks;
	
	public StatisticWeekView(Context context) {
		super(context, R.layout.statistic_week_view);
		db = new HistoryDB(context);
	}

	private void init(){
		Point screen = StatisticFragment.getStatisticPx();
		
		Bitmap tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_aweek_text);
		int textWidth = (int) (screen.x*120.0/720.0);
		int textHeight = (int) (textWidth*106.0/120.0);
		textBmp = Bitmap.createScaledBitmap(tmp, textWidth,textHeight , true);
		tmp.recycle();
		textImage = (ImageView) view.findViewById(R.id.statistic_week_text);
		textImage.setImageBitmap(textBmp);
		RelativeLayout.LayoutParams textParam = (RelativeLayout.LayoutParams) textImage.getLayoutParams();
		textParam.height = textHeight;
		textParam.width = textWidth;
		textParam.leftMargin = (int) (screen.x * 560.0/720.0);
		textParam.topMargin = (int)(screen.y*163.0/443.0);
		
		tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_line);
		int lineWidth1 = (int) (screen.x*557.0/720.0);
		int lineHeight1 = (int) (screen.y*5.0/443.0);
		lineBmp1 = Bitmap.createScaledBitmap(tmp, lineWidth1,lineHeight1 , true);
		tmp.recycle();
		lineImage1 = (ImageView) view.findViewById(R.id.statistic_week_line1);
		lineImage1.setImageBitmap(lineBmp1);
		RelativeLayout.LayoutParams lineParam1 = (RelativeLayout.LayoutParams) lineImage1.getLayoutParams();
		lineParam1.height = lineHeight1;
		lineParam1.width = lineWidth1;
		lineParam1.leftMargin = (int) (screen.x * 90.0/720.0);
		lineParam1.topMargin = (int)(screen.y*312.0/443.0);
		
		tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_line2);
		int lineWidth2 = (int) (screen.x*10.0/720.0);
		int lineHeight2 = (int) (screen.y*102.0/443.0);
		lineBmp2 = Bitmap.createScaledBitmap(tmp, lineWidth2,lineHeight2, true);
		tmp.recycle();
		lineImage2 = (ImageView) view.findViewById(R.id.statistic_week_line2);
		lineImage2.setImageBitmap(lineBmp2);
		RelativeLayout.LayoutParams lineParam2 = (RelativeLayout.LayoutParams) lineImage2.getLayoutParams();
		lineParam2.height = lineHeight2;
		lineParam2.width = lineWidth2;
		lineParam2.leftMargin = (int) (screen.x * 160.0/720.0);
		lineParam2.topMargin = (int)(screen.y*158.0/443.0);
	
		int blockWidth =  (int) (screen.x * 44.0/720.0);
		int blockHeight =  (int) (screen.y * 50.0/443.0);
		int blockGap = (int)(screen.x * 2.0/720.0);
		if (blockGap < 1)
			blockGap = 1;
		
		tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_aweek_green);
		block_green = Bitmap.createScaledBitmap(tmp, blockWidth, blockHeight, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_aweek_yellow);
		block_yellow = Bitmap.createScaledBitmap(tmp, blockWidth, blockHeight, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_aweek_red);
		block_red = Bitmap.createScaledBitmap(tmp, blockWidth, blockHeight, true);
		tmp.recycle();
		
		mainLayout = (RelativeLayout) view.findViewById(R.id.statistic_week_layout);
		
		blocks = new ImageView[28];
		int leftMargin = (int) (screen.x * 204.0/720.0);
		for (int i=0;i<7;++i){
			int topMargin = lineParam1.topMargin - blockHeight;
			for (int j=0;j<4;++j){
				int c = 4*i+j;
				blocks[c] = new ImageView(context);
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
	}
	
	private void setting(){
		init();
		BracGameHistory[] historys = db.getMultiDayInfo(7);
		for (int i=0;i<historys.length;++i){
			if (historys[i]==null){
				//MISS
				blocks[i].setImageBitmap(block_yellow);
			}
			else{
				if (historys[i].brac>BracDataHandler.THRESHOLD){
					//FAIL
					blocks[i].setImageBitmap(block_red);
				}
				else{
					//PASS
					blocks[i].setImageBitmap(block_green);
				}
			}
		}
	}

	@Override
	public void clear() {
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
	}

	@Override
	public void resume() {
		setting();
	}
	
}
