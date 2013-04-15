package statisticPageView.analysis;


import database.HistoryDB;
import main.activities.FragmentTabs;
import main.activities.Lang;
import main.activities.R;
import main.activities.StatisticFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import statisticPageView.StatisticPageView;

public class AnalysisSuccessView extends StatisticPageView {

	private TextView title;
	private ImageView title_bg;
	private Bitmap titleBmp;
	
	private TextView help;
	private HistoryDB db;
	private TextView text;
	private TextView success,fail;
	
	private ImageView all, niddle;
	private Bitmap allBmp, niddleBmp;
	
	
	public AnalysisSuccessView(Context context,StatisticFragment statisticFragment) {
		super(context,R.layout.analysis_success_view,statisticFragment);
		db = new HistoryDB(context);
	}
	

	@Override
	public void clear() {
		if (titleBmp!=null && !titleBmp.isRecycled()){
			titleBmp.recycle();
			titleBmp = null;
		}
		if (allBmp!=null && !allBmp.isRecycled()){
			allBmp.recycle();
			allBmp = null;
		}
		if (niddleBmp!=null && !niddleBmp.isRecycled()){
			niddleBmp.recycle();
			niddleBmp = null;
		}
	}
	
	private int score;
	private float niddlePivotY,niddlePivotX;
	private float niddleRotate;
	
	@Override
	public void onPreTask() {
		Point screen = StatisticFragment.getStatisticPx();
		
		score = db.getAllBracGameScore();
		
		text = (TextView) view.findViewById(R.id.analysis_success_score);
		text.setTextColor(0xFFF0843C);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 60.0/720.0));
		Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/helvetica-lt-std-ultra-compressed.otf");
		text.setTypeface(face);
		
		
		
		title = (TextView) view.findViewById(R.id.analysis_success_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(screen.x * 44.0/720.0));
		title.setTextColor(0xFFFFFFFF);
		
		title_bg = (ImageView) view.findViewById(R.id.analysis_success_title_bg);
		
		help = (TextView) view.findViewById(R.id.analysis_success_help);
		help.setTextColor(0xFF545454);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 46.0/720.0));

		all = (ImageView) view.findViewById(R.id.analysis_success_all);
		niddle  = (ImageView) view.findViewById(R.id.analysis_success_niddle);
		
		success = (TextView) view.findViewById(R.id.analysis_success_success);
		success.setTextColor(0xFF545454);
		success.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 46.0/720.0));
		fail = (TextView) view.findViewById(R.id.analysis_success_fail);
		fail.setTextColor(0xFF545454);
		fail.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 46.0/720.0));
		
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
		helpParam.leftMargin = (int)(screen.x * 120.0/720.0);
		helpParam.topMargin = (int)(screen.x * 100.0/720.0);
		
		
		RelativeLayout.LayoutParams allParam = (RelativeLayout.LayoutParams)all.getLayoutParams();
		allParam.width = (int)(screen.x * 486.0/720.0);
		allParam.height = (int)(screen.x * 214.0/720.0);
		allParam.leftMargin = (int)(screen.x * 121.0/720.0);
		allParam.topMargin = (int)(screen.x * 189.0/720.0);
		allBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_all);
		
		
		
		RelativeLayout.LayoutParams niddleParam = (RelativeLayout.LayoutParams)niddle.getLayoutParams();
		niddleParam.width = (int)(screen.x * 63.0/720.0);
		niddleParam.height = (int)(screen.x * 174.0/720.0);
		
		
		niddleRotate = (score - 5)*65/4;
		niddleParam.topMargin = (int)(screen.x * 189.0/720.0);
		niddleParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		niddleBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.drunk_record_all_line3);
		niddlePivotX =niddleParam.width/2;
		niddlePivotY =niddleParam.height*1.35F;
		
		RelativeLayout.LayoutParams textParam = (RelativeLayout.LayoutParams)text.getLayoutParams();
		textParam.topMargin = (int)(screen.x * 380.0/720.0);
		
		RelativeLayout.LayoutParams successParam = (RelativeLayout.LayoutParams)success.getLayoutParams();
		successParam.topMargin = (int)(screen.x * 380.0/720.0);
		successParam.rightMargin = (int)(screen.x * 120.0/720.0);
		RelativeLayout.LayoutParams failParam = (RelativeLayout.LayoutParams)fail.getLayoutParams();
		failParam.topMargin = (int)(screen.x * 380.0/720.0);
		failParam.leftMargin = (int)(screen.x * 120.0/720.0);
		
	}


	@Override
	public void onPostTask() {
		title_bg.setImageBitmap(titleBmp);
		all.setImageBitmap(allBmp);
		niddle.setImageBitmap(niddleBmp);
		
		niddle.setPivotX(niddlePivotX);
		niddle.setPivotY(niddlePivotY);
		niddle.setRotation(niddleRotate);
		text.setText(String.valueOf(score));
		if (Lang.eng){
			help.setText("Depends on the tests, your overall result");
			Point screen = FragmentTabs.getSize();
			RelativeLayout.LayoutParams helpParam = (RelativeLayout.LayoutParams)help.getLayoutParams();
    		helpParam.topMargin = (int)(screen.x * 80.0/720.0);
		}
		else
			help.setText("依據您的酒測，整體戒酒表現" );
		success.setText("成功" );
		fail.setText("失敗" );
		
	}

	@Override
	public void onCancel() {
		clear();
		
	}	

}
