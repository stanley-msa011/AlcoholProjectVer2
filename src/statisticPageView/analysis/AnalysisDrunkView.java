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
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
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
	
	public AnalysisDrunkView(Context context){
		super(context, R.layout.analysis_drunk_view);
		db = new HistoryDB(context);
		format = new DecimalFormat();
		format.setMaximumIntegerDigits(1);
		format.setMinimumIntegerDigits(1);
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);
		
	}
	
	private void setting(){
		
		BracGameHistory[] historys = db.getTodayBracGameHistory();
		
		RelativeLayout content_layout = (RelativeLayout) view.findViewById(R.id.analysis_drunk_layout);
		
		Point screen = StatisticFragment.getStatisticPx();
		
		title = (TextView) view.findViewById(R.id.analysis_drunk_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(screen.x * 48.0/720.0));
		title.setTextColor(0xFFFFFFFF);
		RelativeLayout.LayoutParams titleParam = (RelativeLayout.LayoutParams)title.getLayoutParams();
		titleParam.width = (int)(screen.x * 162.0/720.0);
		titleParam.height = (int)(screen.x * 50.0/720.0);
		titleParam.leftMargin = (int)(screen.x * 120.0/720.0);
		titleParam.topMargin = 0;
		
		
		title_bg = (ImageView) view.findViewById(R.id.analysis_drunk_title_bg);
		RelativeLayout.LayoutParams titleBgParam = (RelativeLayout.LayoutParams)title.getLayoutParams();
		titleBgParam.width = screen.x;
		titleBgParam.height = (int)(screen.x * 69.0/720.0);
		
		Bitmap tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_titlebg);
		titleBmp = Bitmap.createScaledBitmap(tmp, screen.x, titleBgParam.height, true);
		tmp.recycle();
		title_bg.setImageBitmap(titleBmp);
		
		
		help = (TextView) view.findViewById(R.id.analysis_drunk_help);
		RelativeLayout.LayoutParams helpParam = (RelativeLayout.LayoutParams)help.getLayoutParams();
		helpParam.leftMargin = (int)(screen.x * 120.0/720.0);
		helpParam.topMargin = (int)(screen.x * 100.0/720.0);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 46.0/720.0));
		
		
		Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/helvetica-lt-std-ultra-compressed.otf");
		int bgSize = (int)(screen.x * 106.0/720.0);
		int bgGap = (int)(screen.x * 18.0/720.0);
		int textWidth = (int)(screen.x * 64.0/720.0);
		int textHeight = (int)(screen.x * 50.0/720.0);
		int textSize = (int)(screen.x * 48.0/720.0);
		int textGap = (int)(screen.x * 60.0/720.0);
		
		bgBmps = new Bitmap[3];
		tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_today_pass);
		bgBmps[0] = Bitmap.createScaledBitmap(tmp, bgSize, bgSize, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_today_notpass);
		bgBmps[1] = Bitmap.createScaledBitmap(tmp, bgSize, bgSize, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_today_notyet);
		bgBmps[2] = Bitmap.createScaledBitmap(tmp, bgSize, bgSize, true);
		tmp.recycle();
		
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

	@Override
	public void clear() {
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

	@Override
	public void resume() {
		setting();
	}
}
