package statisticPageView.analysis;

import java.text.DecimalFormat;

import database.HistoryDB;

import main.activities.Lang;
import main.activities.R;
import main.activities.StatisticFragment;
import history.BracGameHistory;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
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
	private TextView[] labels;
	private ImageView[] bgs;
	private Bitmap[] bgBmps;
	private HistoryDB db;
	private DecimalFormat format ;
	
	
	public AnalysisDrunkView(Context context,StatisticFragment statisticFragment){
		super(context, R.layout.analysis_drunk_view,statisticFragment);
		db = new HistoryDB(context);
		format = new DecimalFormat();
		format.setMaximumIntegerDigits(1);
		format.setMinimumIntegerDigits(1);
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);
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
	

	private BracGameHistory[] historys;
	private RelativeLayout content_layout;
	
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
		
	}

	@Override
	public void onPostTask() {
		title_bg.setImageBitmap(titleBmp);
		if (Lang.eng)
			help.setText("Your test results today");
		else
		help.setText("今天的酒測次數與結果" );
		
		Point screen = StatisticFragment.getStatisticPx();
		
		Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/helvetica-lt-std-bold.otf");
		int bgSize = (int)(screen.x * 106.0/720.0);
		int bgGap = (int)(screen.x * 18.0/720.0);
		int textWidth = (int)(screen.x * 64.0/720.0);
		int textSize = (int)(screen.x * 46.0/720.0);
		int textGap = (int)(screen.x * 61.0/720.0);

		int labelGap = (int)(screen.x * 125.0/720.0);
		
		texts = new TextView[4];
		labels = new TextView[4];
		bgs = new ImageView[4];
		
		for (int i=0;i<4;++i){
			
			bgs[i] = new ImageView(context);
			content_layout.addView(bgs[i]);
			RelativeLayout.LayoutParams bgParam = (LayoutParams) bgs[i].getLayoutParams();
			bgParam.width = bgSize;
			bgParam.height = bgSize;
			bgParam.leftMargin = (int)(screen.x * 120.0/720.0) +  (bgSize + bgGap)*i;
			bgParam.topMargin = (int)(screen.x * 179.0/720.0);
			
			labels[i]  = new TextView(context);
			labels[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			labels[i].setTypeface(face);
			labels[i].setTextColor(0xFF545454);
			labels[i].setGravity(Gravity.CENTER);
			content_layout.addView(labels[i]);
			RelativeLayout.LayoutParams labelParam = (LayoutParams) labels[i].getLayoutParams();
			labelParam.leftMargin = (int)(screen.x * 146.0/720.0) +labelGap*i;
			labelParam.topMargin = (int)(screen.x * 283.0/720.0);
			
			texts[i] = new TextView(context);
			texts[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			texts[i].setTypeface(face);
			texts[i].setGravity(Gravity.CENTER);
			texts[i].setTextColor(0xFFFFFFFF);
			content_layout.addView(texts[i]);
			
			RelativeLayout.LayoutParams textParam = (LayoutParams) texts[i].getLayoutParams();
			textParam.leftMargin = (int)(screen.x * 127.0/720.0) + (textWidth + textGap)*i;
			textParam.topMargin = (int)(screen.x * 205.0/720.0);
			
			
			if (historys[i]==null){
				texts[i].setText("");
				if (bgBmps!=null)
					bgs[i].setImageBitmap(bgBmps[2]);
				labels[i].setVisibility(View.VISIBLE);
			}
			else{
				String out = format.format(historys[i].brac);
				texts[i].setText(out);
				if (historys[i].brac>BracDataHandler.THRESHOLD&&bgBmps!=null)
					bgs[i].setImageBitmap(bgBmps[1]);
				else if (bgBmps!=null)
					bgs[i].setImageBitmap(bgBmps[0]);
				labels[i].setVisibility(View.INVISIBLE);
			}
		}
		for (int i=3;i>=0;--i){
			if (historys[i]!=null){
				bgs[i].setBackgroundResource(R.drawable.ring);
				break;
			}
		}
		
		if (Lang.eng){
			labels[0].setText("am");
			labels[0].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize/2);
			labels[1].setText("noon");
			labels[1].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize/2);
			labels[2].setText("pm");
			labels[2].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize/2);
			labels[3].setText("night");
			labels[3].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize/2);
		}else{
			labels[0].setText("早");
			labels[1].setText("中");
			labels[2].setText("下");
			labels[3].setText("晚");
		}
	}

	@Override
	public void onCancel() {
		clear();
	}	

}
