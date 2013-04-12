package statisticPageView.analysis;

import java.text.DecimalFormat;

import new_database.HistoryDB;
import history.BracGameHistory;
import ioio.examples.hello.R;
import ioio.examples.hello.StatisticFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import statisticPageView.StatisticPageView;
import test.data.BracDataHandler;

public class AnalysisDrunkView extends StatisticPageView {

	private TextView title;
	private ImageView title_bg;
	private Bitmap titleBmp;
	private TextView help;
	private TextView[] texts;
	private ImageView[] bgs;
	private Bitmap[] bgBmps;
	private HistoryDB db;
	private DecimalFormat format ;
	private LoadingTask task;
	
	
	public AnalysisDrunkView(Context context,StatisticFragment statisticFragment){
		super(context, R.layout.analysis_drunk_view,statisticFragment);
		db = new HistoryDB(context);
		format = new DecimalFormat();
		format.setMaximumIntegerDigits(1);
		format.setMinimumIntegerDigits(1);
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);
		//setting();
		task = new LoadingTask();
		task.execute();
	}
	
	@Override
	public void clear() {
		Log.d("CLEAR","DRUNK");
		if (task !=null){
			task.cancel(true);
			task = null;
		}
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
	
	
	private class LoadingTask extends AsyncTask<Void, Void, Void>{

		private BracGameHistory[] historys;
		private RelativeLayout content_layout;
		
    	protected void onPreExecute(){
    		
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
		protected Void doInBackground(Void... params) {
			
			historys = db.getTodayBracGameHistory();
			
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
    		
			return null;
		}
		@Override
		 protected void onPostExecute(Void result) {
			title_bg.setImageBitmap(titleBmp);
			help.setText("您今天的酒測次數與結果" );
			
			Point screen = StatisticFragment.getStatisticPx();
			
    		Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/helvetica-lt-std-ultra-compressed.otf");
    		int bgSize = (int)(screen.x * 106.0/720.0);
    		int bgGap = (int)(screen.x * 18.0/720.0);
    		int textWidth = (int)(screen.x * 64.0/720.0);
    		int textHeight = (int)(screen.x * 50.0/720.0);
    		int textSize = (int)(screen.x * 48.0/720.0);
    		int textGap = (int)(screen.x * 60.0/720.0);

    		texts = new TextView[4];
    		bgs = new ImageView[4];
    		
    		for (int i=0;i<4;++i){
    			
    			bgs[i] = new ImageView(context);
    			content_layout.addView(bgs[i]);
    			RelativeLayout.LayoutParams bgParam = (LayoutParams) bgs[i].getLayoutParams();
    			bgParam.width = bgSize;
    			bgParam.height = bgSize;
    			bgParam.leftMargin = (int)(screen.x * 120.0/720.0) +  (bgSize + bgGap)*i;
    			bgParam.topMargin = (int)(screen.x * 179.0/720.0);
    			
    			texts[i] = new TextView(context);
    			texts[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    			texts[i].setTypeface(face);
    			texts[i].setGravity(Gravity.CENTER);
    			texts[i].setTextColor(0xFFFFFFFF);
    			content_layout.addView(texts[i]);
    			
    			RelativeLayout.LayoutParams textParam = (LayoutParams) texts[i].getLayoutParams();
    			textParam.width = textWidth;
    			textParam.height = textHeight;
    			textParam.leftMargin = (int)(screen.x * 141.0/720.0) + (textWidth + textGap)*i;
    			textParam.topMargin = (int)(screen.x * 205.0/720.0);
    			
    			if (historys[i]==null){
    				texts[i].setText("");
    				bgs[i].setImageBitmap(bgBmps[2]);
    			}
    			else{
    				String out = format.format(historys[i].brac);
    				texts[i].setText(out);
    				if (historys[i].brac>BracDataHandler.THRESHOLD)
    					bgs[i].setImageBitmap(bgBmps[1]);
    				else
    					bgs[i].setImageBitmap(bgBmps[0]);
    			}
    		}
		}
		
		protected void onCancelled(){
			clear();
		}
	}	

}
