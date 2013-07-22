package ubicomp.drunk_detection.activities;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ubicomp.drunk_detection.activities.R;

import data.history.AccumulatedHistoryState;
import data.history.BarInfo;
import data.history.DateBracDetectionState;
import database.AudioDB;
import database.HistoryDB;
import database.WeekNum;
import history.pageEffect.PageAnimationTaskVertical;
import history.pageEffect.PageAnimationTaskVertical2;
import history.pageEffect.PageWidgetVertical;
import history.ui.DateValue;
import history.ui.HistoryStorytelling;
import history.ui.AudioRecordBox;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class HistoryFragment extends Fragment {

	private View  view;
	
	private RelativeLayout pageLayout;
	private RelativeLayout chartLayout;
	private RelativeLayout chartAreaLayout;
	private RelativeLayout stageLayout;
	private HistoryDB hdb;
	private AudioDB adb;
	private PageWidgetVertical pageWidget;
	private PageAnimationTaskVertical pageAnimationTask;
	private PageAnimationTaskVertical2 pageAnimationTask2;
	
	private HorizontalScrollView scrollView;
	
	private Point screen;
	
	private int NUM_OF_BARS;
	
	private ArrayList<DateValue> selected_dates ;
	private ArrayList<Integer> selected_idx;
	private ArrayList<DateBracDetectionState> historys; ;
	private ArrayList<BarInfo> bars;
	private ArrayList<Boolean> hasAudio;
	
	private ChartView chart;
	private ChartTitleView  chartTitle;
	private ChartYAxisView chartYAxis;
	private ChartLabelView chartLabel;
	
    private int bar_width;
    private int bar_gap;
    private int chart_width;
    private int chartHeight;
    private int bar_bottom;
    private int bar_left;
    private int circle_radius;
	
    private GestureDetector gDetector;
    private  GestureListener gListener;
    private TouchListener gtListener;
    
    private boolean isAnimation = false;
    
	private int width, height,top_margin,bg_x;
	
	private Bitmap cur_bg_bmp,next_bg_bmp;
	//private Drawable prev_bgDrawable;
	
	private PointF touchPoint;
	private PointF from,to;
	private HistoryFragment historyFragment;
	
	private int page_week;
	private PointF curPageTouch;
	private LoadingHandler loadHandler;
	
	private Typeface wordTypefaceBold, digitTypeface,digitTypefaceBold;
	
	private DecimalFormat format;
	private TextView quoteText;
	private TextView stageMessageText,stageMessage,stageRateText;
	
	private Calendar from_cal;
	private Calendar to_cal;
	
	private Bitmap chartPlay;
	
	private int max_week;
	private boolean chartTouchable = true;
	
	private AudioRecordBox recordBox;
	
	private Drawable chartBg1Drawable, chartBg2Drawable, chartBg3Drawable, chartBg4Drawable;
	private Bitmap chartCircleBmp;
	
	
	private AccumulatedHistoryState[] page_states;
	
	private static final String[] QUOTE_STR = {
		"酒精是一位可畏的敵人，但你相信不會被\n敵人擺布，也決不低頭認輸。",
		"想踏上戒酒這條路已經很久了，你正在這\n路上。珍惜每一天，會感覺愈來愈好。",
		"你發現不能空等你的船來到，而是你得自\n己游向它。",
		"你清楚你可以從過去的失敗中，要如何讓\n自己現在更好，更珍惜戒酒的日子。",
		"你開始懂得好好善待自己，並且”自愛”\n是一生最重要的一場戀愛。",
		"對過去憤怒，或對未來恐懼，只是讓你的\n生活變得更無力，更容易錯過現在。",
		"我們的價值是由我們是怎樣的人來決定，\n而不是有了什麼來決定。",
		"我們的價值是由我們是怎樣的人來決定，\n而不是有了什麼來決定。",
		"你發現當你越認真努力戒酒，生活變得更\n順，運氣也會跟著變好。",
		"你相信自己一步一步在走你自己所創造的\n不一樣中。",
		"就像一次一次的身體訓練，每一次都讓我\n們變得更強壯或更堅定。",
		"你發現別人的鼓勵讓我們更堅持下去，但\n如果也可以鼓勵他人，更能讓生活更有意義。"
	};
	
	private static final int MAX_PAGE_WEEK = 11;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	this.historyFragment = this;
    	view = inflater.inflate(R.layout.history_fragment, container,false);
    	hdb = new HistoryDB(this.getActivity());
    	adb = new AudioDB(this.getActivity());
    	from_cal = Calendar.getInstance();
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
	    int mYear = sp.getInt("sYear", from_cal.get(Calendar.YEAR));
	    int mMonth = sp.getInt("sMonth", from_cal.get(Calendar.MONTH));
	    int mDay = sp.getInt("sDate", from_cal.get(Calendar.DATE));
    	from_cal.set(mYear, mMonth, mDay, 0, 0, 0);
    	from_cal.set(Calendar.MILLISECOND, 0);
    	return view;
    }
   
	public void onResume(){
		
		super.onResume();
		
		isAnimation = false;
		screen = FragmentTabs.getSize();
		
		page_states =hdb.getAccumulatedHistoryStateByWeek(); 
		page_week = page_states.length - 1;
		if(page_week > MAX_PAGE_WEEK)
			page_week = MAX_PAGE_WEEK;
		max_week = page_week;
		
		historys = new ArrayList<DateBracDetectionState>();
		selected_dates = new ArrayList<DateValue>();
		selected_idx = new ArrayList<Integer>();
		bars = new ArrayList<BarInfo>();
		hasAudio = new ArrayList<Boolean>();
		
		gListener = new GestureListener();
		gDetector = new GestureDetector(getActivity(), gListener);
		gtListener = new TouchListener();
		
		DateBracDetectionState[] h = hdb.getAllHistory();
		if (h != null)
			for (int i=0;i<h.length;++i)
				historys.add(h[i]);
		
		to_cal = Calendar.getInstance();
		if (from_cal.before(to_cal)){
			long millis = to_cal.getTimeInMillis() - from_cal.getTimeInMillis();
			NUM_OF_BARS= (int)(millis/AlarmManager.INTERVAL_DAY) + 1;
		}else
			NUM_OF_BARS = 0;
		
		RelativeLayout r = (RelativeLayout) view;
		recordBox = new AudioRecordBox(this,r);
		if (loadHandler == null)
			loadHandler = new LoadingHandler();
		loadHandler.sendEmptyMessage(0);
	}
    
    public void onPause(){
    	if (recordBox!=null){
    		recordBox.OnPause();
    		recordBox.clear();
    	}
    	selected_dates.clear();
    	selected_idx.clear();
    	historys.clear();
    	bars.clear();
    	hasAudio.clear();
    	clear();
    	super.onPause();
    }
	
    private void clear(){
    	pageLayout.removeView(pageWidget);
    	chartLayout.removeView(chart);
    	chartAreaLayout.removeView(chartYAxis);
    	chartAreaLayout.removeView(chartTitle);
    	chartAreaLayout.removeView(chartLabel);
    	
    	pageWidget.setBitmaps(null, null);
    	
    	if (loadHandler !=null)
    		loadHandler.removeMessages(0);
    	
    	if (pageAnimationTask!=null){
    		pageAnimationTask.cancel(true);
    		pageAnimationTask = null;
    	}
    	if (pageAnimationTask2!=null){
    		pageAnimationTask2.cancel(true);
    		pageAnimationTask2 = null;
    	}
    	
    	if (cur_bg_bmp!=null && !cur_bg_bmp.isRecycled()){
    		cur_bg_bmp.recycle();
    		cur_bg_bmp=null;
    	}
    	if (next_bg_bmp!=null && !next_bg_bmp.isRecycled()){
    		next_bg_bmp.recycle();
    		next_bg_bmp=null;
    	}
    	if (chartCircleBmp!=null && !chartCircleBmp.isRecycled()){
    		chartCircleBmp.recycle();
    		chartCircleBmp=null;
    	}
    	if (pageWidget!=null){
    		pageWidget.destroyDrawingCache();
    		pageWidget.clear();
    		pageWidget=null;
    	}
    	if (chartPlay !=null && !chartPlay.isRecycled()){
    		chartPlay.recycle();
    		chartPlay = null;
    	}
    	System.gc();
    }
    
    
    private void initView(){
    	pageLayout = (RelativeLayout) view.findViewById(R.id.history_book_layout);
    	chartLayout = (RelativeLayout) view.findViewById(R.id.history_content_layout);
    	scrollView = (HorizontalScrollView) view.findViewById(R.id.history_scroll_view);
    	scrollView.setSmoothScrollingEnabled(true);
    	chartAreaLayout = (RelativeLayout) view.findViewById(R.id.history_chart_area_layout);
    	
    	wordTypefaceBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/DFLiHeiStd-W5.otf");
    	digitTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/dinproregular.ttf");
    	digitTypefaceBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/dinpromedium.ttf");
    	/*Setting the play*/
    	bg_x = screen.x;
    	width = bg_x;
    	height = bg_x* 1137/1080;
    	
    	from = new PointF(width,height);
    	to = new PointF(width*0.8F,-height);
    	touchPoint = new PointF(from.x,from.y);
    	
    	pageWidget= new PageWidgetVertical(pageLayout.getContext(),width,height);
    	
    	curPageTouch = touchPoint;

    	int textSize = bg_x*21/480;
    	
    	stageLayout = (RelativeLayout) view.findViewById(R.id.history_stage_message_layout);
    	
    	stageMessage = (TextView) view.findViewById(R.id.history_stage);
    	stageMessage.setTypeface(wordTypefaceBold);
    	
    	stageMessageText = (TextView) view.findViewById(R.id.history_stage_message);
    	stageMessageText.setTextSize(TypedValue.COMPLEX_UNIT_PX, bg_x*58/480);
    	stageMessageText.setTypeface(digitTypefaceBold);
    	
    	stageRateText = (TextView) view.findViewById(R.id.history_stage_rate);
    	stageRateText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    	stageRateText.setTypeface(wordTypefaceBold);
    	
    	
    	quoteText = (TextView) view.findViewById(R.id.history_quote);
    	quoteText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    	quoteText.setTypeface(wordTypefaceBold);
    	
    	format = new DecimalFormat();
		format.setMaximumIntegerDigits(3);
		format.setMinimumIntegerDigits(1);
		format.setMinimumFractionDigits(0);
		format.setMaximumFractionDigits(0);
    	
    	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    	
    	Resources r = historyFragment.getResources();
		
    	//AccumulatedHistoryState curAH = page_states[page_week];
    	/*Bitmap tmp;
    	tmp = BitmapFactory.decodeResource(r, HistoryStorytelling.getPage(curAH.getScore(), curAH.week));
    	cur_bg_bmp = Bitmap.createScaledBitmap(tmp, screen.x, screen.x * 1137/1080, true);
		cur_bg_bmp = BitmapFactory.decodeResource(r, HistoryStorytelling.getPage(curAH.getScore(), curAH.week));*/
		
	
		 int chart_height = screen.x * 564/1080;
		chartBg1Drawable = r.getDrawable(R.drawable.chart_bg1);
		chartBg2Drawable = r.getDrawable(R.drawable.chart_bg2);
		chartBg3Drawable = r.getDrawable(R.drawable.chart_bg3);
		chartBg4Drawable = r.getDrawable(R.drawable.chart_bg4);		

		chartCircleBmp = BitmapFactory.decodeResource(r, R.drawable.chart_circle);
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		
    	LayoutParams sParam = (LayoutParams) stageLayout.getLayoutParams();
    	sParam.leftMargin = bg_x*10/480;
    	sParam.topMargin = bg_x * 60/480;
    	sParam.width = bg_x*70/480;
    	
    	LayoutParams rParam = (LayoutParams) stageRateText.getLayoutParams();
    	rParam.leftMargin = bg_x*20/480;
    	rParam.topMargin = bg_x * 430/480;
    	rParam.width = bg_x*70/480;
    	
    	LayoutParams qParam = (LayoutParams) quoteText.getLayoutParams();
    	qParam.leftMargin = bg_x*5/480;
    	qParam.topMargin = bg_x * 430/480;
    	
    	pageLayout.addView(pageWidget);
    	LayoutParams param = (LayoutParams) pageWidget.getLayoutParams();
    	param.width = width;
    	param.height = height;

    	//Set chart
    	RelativeLayout.LayoutParams scrollParam = (RelativeLayout.LayoutParams)scrollView.getLayoutParams();
    	scrollParam.width = screen.x;
    	scrollParam.height = chart_height;
    	
    	FrameLayout.LayoutParams clParam = (FrameLayout.LayoutParams)chartLayout.getLayoutParams();
    	clParam.width = screen.x;
    	clParam.height = chart_height;
    	
    	settingBars();
    	checkHasRecorder();
    	
    	chartPlay = BitmapFactory.decodeResource(r, R.drawable.chart_play);
    	
    	chart = new ChartView(this.getActivity());
    	
    	
    	chartHeight = chart_height;
    	chart_width =  bar_left * 3/2 + (bar_width + bar_gap)* NUM_OF_BARS;
    	if (chart_width < screen.x)
			chart_width = screen.x;
    	
    	chartLayout.addView(chart);
    	RelativeLayout.LayoutParams chartParam = (RelativeLayout.LayoutParams) chart.getLayoutParams();
		chartParam.width= chart_width;
		chartParam.height = chart_height;
		
		chartYAxis = new ChartYAxisView(this.getActivity());
		chartAreaLayout.addView(chartYAxis);
		RelativeLayout.LayoutParams chartYParam = (RelativeLayout.LayoutParams) chartYAxis.getLayoutParams();
		chartYParam.width = screen.x * 94/1080;
		chartYParam.height = chartParam.height;
		
    	chartTitle = new ChartTitleView(this.getActivity());
    	chartAreaLayout.addView(chartTitle);
    	RelativeLayout.LayoutParams chartTitleParam = (RelativeLayout.LayoutParams) chartTitle.getLayoutParams();
		chartTitleParam.width = screen.x;
		chartTitleParam.height = screen.x * 100 / 1080;
		chartTitleParam.topMargin = screen.x * 35/1080;
		
		chartLabel = new ChartLabelView(this.getActivity());
		chartAreaLayout.addView(chartLabel,0);
		RelativeLayout.LayoutParams chartLabelParam = (RelativeLayout.LayoutParams) chartLabel.getLayoutParams();
		chartLabelParam.width = screen.x * 540/1080;
		chartLabelParam.height = screen.x * 90 / 1080;
		chartLabelParam.topMargin = screen.x * 130/1080;
		chartLabelParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		
		if (chart_type == 0)
			chartAreaLayout.setBackground(chartBg1Drawable);
		else if (chart_type == 1)
			chartAreaLayout.setBackground(chartBg2Drawable);
		else if (chart_type == 2)
			chartAreaLayout.setBackground(chartBg3Drawable);
		else
			chartAreaLayout.setBackground(chartBg4Drawable);
		
		pageWidget.setOnTouchListener(gtListener);
		
    }
    
    private void setStorytellingTexts(){
    	
    	AccumulatedHistoryState curAH = page_states[page_week];
    	float progress = (float)curAH.getScore()*100F/(float)curAH.MAX_SCORE;
    	
    	String stageText = String.valueOf(page_week+1);
    	
    	stageMessageText.setText(stageText);
    	String progress_str =  "<font color=#e79100>"+ format.format(progress)+"%"+"</font><font color=#717071><br/>完成</font>";
    	stageRateText.setText(Html.fromHtml(progress_str));
    	quoteText.setText(QUOTE_STR[page_week]);
    	
    }
    
    
    public void endAnimation(){
    	setStorytellingTexts();
    	setStageVisible(true);
    	FragmentTabs.enableTab(true);
    	isAnimation = false;
    	chart.invalidate();
    }
    
    
    public void endAnimation(int tt){
    	Log.d("PAGE_ANIMATION", "END ANIMATION level:" + page_week);
    	setStorytellingTexts();
    	setStageVisible(true);
    	pageWidget.setOnTouchListener(gtListener);
    	FragmentTabs.enableTab(true);
    	isAnimation = false;
    	chart.invalidate();
    }
    
    public void resetPage(int change){
    	Log.d("History","resetPage start");
    	if (cur_bg_bmp!=null && !cur_bg_bmp.isRecycled()){
    		cur_bg_bmp.recycle();
    		cur_bg_bmp = null;
    	}
    	if (next_bg_bmp!=null && !next_bg_bmp.isRecycled()){
    		next_bg_bmp.recycle();
    		next_bg_bmp = null;
    	}
    	Log.d("PAGE_ANIMATION", "reset level: "+page_week);
    	
    	if (change > 0){
    		++page_week;
    		if (page_week > max_week)
    			page_week = max_week;
    	}else if (change <0){
    		--page_week;
    		if (page_week < 0)
    			page_week =0;
    	}
    	
    	Log.d("PAGE_ANIMATION", "reset level fix: "+page_week);
    	AccumulatedHistoryState AH = page_states[page_week];
    	Bitmap tmp = BitmapFactory.decodeResource(historyFragment.getResources(), HistoryStorytelling.getPage(AH.getScore(),AH.week));
    	cur_bg_bmp = Bitmap.createScaledBitmap(tmp, screen.x, screen.x*1137/1080, true);
    	tmp.recycle();
    	next_bg_bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    	pageWidget.setBitmaps(cur_bg_bmp, next_bg_bmp);
    	pageWidget.setTouchPosition(curPageTouch);
    	
    	int scroll_value = bar_left+( screen.x * 48 / 1080)*7*(page_week-1);
    	if (scroll_value <0)
    		scroll_value = 0;
    	scrollView.smoothScrollTo(scroll_value, 0);
    	//scrollView.setScrollX(scroll_value);
    	Log.d("History","resetPage end");
    }
    
	@SuppressLint("HandlerLeak")
	private class LoadingHandler extends Handler{
		
		public void handleMessage(Message msg){
			initView();
			recordBox.setImage();
			endAnimation();
			
			LoadingBox.dismiss();
			Log.d("PAGE_ANIMATION","END LOADING BOX");
			startAnim();
		}
	}

	private void startAnim(){
		
		Log.d("PAGE_ANIMATION","START ANIMATION");
		if (page_week == 0){
			resetPage(0);
			return;
		}
			Log.d("PAGE_ANIMATION","START CHANGE PAGE ANIMATION");
			isAnimation = true;
			FragmentTabs.enableTab(false);
			
			int[] aBgs = HistoryStorytelling.getAnimationBgs(page_states);
			int pageIdx = page_week;
			int startIdx = pageIdx-1;
			if (startIdx <0)
				startIdx =0;
			setStageVisible(false);
			pageAnimationTask = new PageAnimationTaskVertical(pageWidget,from,to,aBgs,historyFragment,curPageTouch,startIdx,pageIdx);
			Log.d("PAGE_ANIMATION","START CHANGE PAGE ANIMATION EXECUTE");
			pageAnimationTask.execute();
	}
	
	public void setStageVisible(boolean t){
		if (t){
			stageMessageText.setVisibility(View.VISIBLE);
			stageMessage.setVisibility(View.VISIBLE);
			stageRateText.setVisibility(View.VISIBLE);
			quoteText.setVisibility(View.VISIBLE);
		}
		else{
			stageMessageText.setVisibility(View.INVISIBLE);
			stageMessage.setVisibility(View.INVISIBLE);
			stageRateText.setVisibility(View.INVISIBLE);
			quoteText.setVisibility(View.INVISIBLE);
		}
	}
	
	private class TouchListener implements View.OnTouchListener{
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return gDetector.onTouchEvent(event);
		}
	}
	
	private class GestureListener extends GestureDetector.SimpleOnGestureListener{

		@Override
		public boolean onDown(MotionEvent e1){
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (isAnimation)
				return true;
			isAnimation = true;
			
			float y1 = e1.getY();
			float y2 = e2.getY();
			if (y1 - y2 > 0){//UP
				Log.d("PAGE_ANIMATION", "UP");
				pageWidget.setOnTouchListener(null);
				FragmentTabs.enableTab(false);
				int[] aBgs = HistoryStorytelling.getAnimationBgs(page_states);
				int pageIdx = page_week;
				int startIdx = pageIdx;
				int endIdx = startIdx+1;
				Log.d("PAGE_ANIMATION", "UP"+startIdx+"/"+endIdx);
				if (endIdx > max_week){
					Log.d("PAGE_ANIMATION", "UP fail");
					isAnimation = false;
					pageWidget.setOnTouchListener(gtListener);
					FragmentTabs.enableTab(true);
					return true;
				}
				Log.d("PAGE_ANIMATION", "UP2");
				//prevImage.setVisibility(View.INVISIBLE);
				setStageVisible(false);
				pageAnimationTask2 = new PageAnimationTaskVertical2(pageWidget,from,to,aBgs,historyFragment,curPageTouch,startIdx,pageIdx,1);
				pageAnimationTask2.execute();
			}else if (y2 - y1 >  0){//DOWN
				Log.d("PAGE_ANIMATION", "DOWN");
				pageWidget.setOnTouchListener(null);
				FragmentTabs.enableTab(false);
				int[] aBgs = HistoryStorytelling.getAnimationBgs(page_states);
				int pageIdx = page_week;
				int startIdx = pageIdx;
				int endIdx = startIdx-1;
				if (endIdx < 0){
					isAnimation = false;
					pageWidget.setOnTouchListener(gtListener);
					FragmentTabs.enableTab(true);
					return true;
				}
				Log.d("PAGE_ANIMATION", "DOWN2");
				//prevImage.setVisibility(View.INVISIBLE);
				setStageVisible(false);
				
				pageAnimationTask2 = new PageAnimationTaskVertical2(pageWidget,from,to,aBgs,historyFragment,curPageTouch,startIdx,endIdx,0);
				pageAnimationTask2.execute();
			}
			return true;
		}
	}
	
	private static final long DAY_MILLIS = AlarmManager.INTERVAL_DAY;
	
	public void settingBars(){

		bars.clear();
		
		if (NUM_OF_BARS == 0)
			return;
		
		long from_t = from_cal.getTimeInMillis();
		Calendar ccal = Calendar.getInstance();
		ccal.setTimeInMillis(from_cal.getTimeInMillis());
		
		for (int i=0;i<NUM_OF_BARS;++i){
			
			int count = 0;
			float e_sum = 0;
			float d_sum = 0;
			float b_sum = 0;
			
			float emotion,desire,brac;
			
			int pos = 0;
			
			int bar_week = -1;
			
			for (int j=pos; j<historys.size();++j){
				DateBracDetectionState h = historys.get(j);
				if (h.timestamp >= from_t && h.timestamp < from_t + DAY_MILLIS){
					e_sum += (h.emotion > 0)?h.emotion:0;
					d_sum += (h.desire >0)?h.desire:0;
					b_sum += h.brac;
					++count;
					bar_week = h.week;
				}else if(h.timestamp >= from_t + DAY_MILLIS){
					pos = j;
					break;
				}
			}
			
			boolean hasData = true;
			if (count == 0){
				hasData = false;
				emotion = desire = brac = 0F;
				bar_week = WeekNum.getWeek(this.getActivity(),from_t);
			}else{
				emotion = e_sum/count;
				desire = d_sum/count;
				brac = b_sum/count;
			}
			
			int mYear = ccal.get(Calendar.YEAR);
			int mMonth = ccal.get(Calendar.MONTH);
			int mDate = ccal.get(Calendar.DAY_OF_MONTH);
			
			DateValue dv = new DateValue(mYear,mMonth,mDate);
			
			BarInfo barInfo;
			barInfo = new BarInfo(emotion, desire, brac, bar_week,hasData,dv);
			
			bars.add(barInfo);
			
			from_t+=DAY_MILLIS;
			ccal.add(Calendar.DATE, 1);
		}
		
	}
	
	private int chart_type = 0;
	
	private class ChartTitleView extends View{

		private Paint text_paint_large = new Paint();
		private Paint text_paint_large_2 = new Paint();
		
		public ChartTitleView(Context context) {
			super(context);
			text_paint_large.setColor(0xFFAAAAAA);
			text_paint_large.setTextSize(screen.x * 48F/1080F);
			text_paint_large.setTextAlign(Align.LEFT);
			text_paint_large.setTypeface(wordTypefaceBold);
			text_paint_large_2.setColor(0xFFf09600);
			text_paint_large_2.setTextSize(screen.x * 48F/1080F);
			text_paint_large_2.setTextAlign(Align.LEFT);
			text_paint_large_2.setTypeface(wordTypefaceBold);
		}
		
		@Override  
	    public boolean onTouchEvent(MotionEvent event) {
			if (!chartTouchable)
	    		return true;
			int x = (int) event.getX();

			if (event.getAction() == MotionEvent.ACTION_DOWN){
				if (x < screen.x * 310/1080){
					chart_type = 0;
					chartAreaLayout.setBackground(chartBg1Drawable);
				}
				else if (x < screen.x * 590/1080){
					chart_type = 1;
					chartAreaLayout.setBackground(chartBg2Drawable);
				}
				else if (x < screen.x * 870/1080){
					chart_type = 2;
					chartAreaLayout.setBackground(chartBg3Drawable);
				}else{
					chart_type = 3;
					chartAreaLayout.setBackground(chartBg4Drawable);
				}
				
				invalidate();
				chart.invalidate();
				chartYAxis.invalidate();
				chartLabel.invalidate();
			}
			return true;
		}
		
		@Override
		protected void onDraw(Canvas canvas){
			super.onDraw(canvas);
			canvas.drawText("心情量尺", screen.x * 30F/1080F, screen.x * 75F/1080F,text_paint_large);	
			canvas.drawText("渴癮指數", screen.x * 310F/1080F, screen.x * 75F/1080F,text_paint_large);
			canvas.drawText("酒測結果", screen.x * 590F/1080F, screen.x * 75F/1080F,text_paint_large);
			canvas.drawText("綜合資料", screen.x * 870F/1080F, screen.x * 75F/1080F,text_paint_large);
			switch(chart_type){
				case 0:
					canvas.drawText("心情量尺", screen.x * 30F/1080F, screen.x * 75F/1080F,text_paint_large_2);
					break;
				case 1:
					canvas.drawText("渴癮指數", screen.x * 310F/1080F, screen.x * 75F/1080F,text_paint_large_2);
					break;
				case 2:
					canvas.drawText("酒測結果", screen.x * 590F/1080F, screen.x * 75F/1080F,text_paint_large_2);
					break;
				case 3:
					canvas.drawText("綜合資料", screen.x * 870F/1080F, screen.x * 75F/1080F,text_paint_large_2);
					break;
			}
		}
	}
	
	private class ChartYAxisView extends View{

		private Paint axis_paint = new Paint();
		private Paint text_paint_small = new Paint();
		
		public ChartYAxisView(Context context) {
			super(context);
			text_paint_small.setColor(0xFF3c3b3b);
			text_paint_small.setTextAlign(Align.CENTER);
			text_paint_small.setTextSize(screen.x * 40F/1080F);
			text_paint_small.setTypeface(digitTypeface);
			axis_paint.setColor(0xFF000000);
			axis_paint.setStrokeWidth(screen.x * 7 / 1080);
		}
		
		@Override
		protected void onDraw(Canvas canvas){
			super.onDraw(canvas);
			
			int max_height = (chartHeight - bar_bottom)*4/10;
			int _bottom = chartHeight - bar_bottom;
			
			//Draw Y axis label
			canvas.drawText("0", 3*bar_width/2, _bottom, text_paint_small);
			String maxLabel;
			if (chart_type == 0)
				maxLabel = "5";
			else if (chart_type == 1)
				maxLabel = "10";
			else if (chart_type == 2)
				maxLabel = "0.5";
			else
				maxLabel = "高";
			canvas.drawText(maxLabel, 3*bar_width/2, _bottom - max_height, text_paint_small);
		}
	}
	
	private class ChartLabelView extends View{

		private Paint emotion_paint = new Paint();
		private Paint desire_paint = new Paint();
		private Paint brac_paint = new Paint();
		private Paint text_paint = new Paint();
		private Paint paint_pass = new Paint();
		private Paint paint_fail= new Paint();
		
		public ChartLabelView(Context context) {
			super(context);
			text_paint.setColor(0xFFAAAAAA);
			text_paint.setTextAlign(Align.CENTER);
			text_paint.setTextSize(screen.x * 18F/480F);
			text_paint.setTypeface(wordTypefaceBold);
			
			emotion_paint.setColor(0xFF2dc7b3);
			desire_paint.setColor(0xFFf19700);
			brac_paint.setColor(0xFFFFFFFF);
			
			paint_pass.setColor(0xFF5bdebe);
			paint_fail.setColor(0xFFf09600);
		}
		
		@Override
		protected void onDraw(Canvas canvas){
			super.onDraw(canvas);
			int base = screen.x * 44/1080;
			int gap = screen.x * 10/1080;
			int top = screen.x * 20/1080;
			int top2 = screen.x * 60/1080;
			int from = 0;
			int line_len = base * 5/2;
			if (chart_type == 3 ){
				canvas.drawRect(from, top, from+base, base+top, emotion_paint);
				from += base + gap;
				canvas.drawText("心情", from + line_len/2, top2 , text_paint);
				from += line_len + gap;
				
				canvas.drawRect(from, top, from+base, base+top, desire_paint);
				from += base + gap;
				canvas.drawText("渴癮", from + line_len/2, top2, text_paint);
				from += line_len + gap;
				
				canvas.drawRect(from, top, from+base, base+top, brac_paint);
				from += base + gap;
				canvas.drawText("酒測值", from + line_len*3/4, top2, text_paint);
			}else{
				//only two labels
				from += base + gap;
				from += line_len + gap;
				
				canvas.drawRect(from, top, from+base, base+top, paint_pass);
				from += base + gap;
				canvas.drawText("通過", from + line_len/2, top2, text_paint);
				from += line_len + gap;
				
				canvas.drawRect(from, top, from+base, base+top, paint_fail);
				from += base + gap;
				canvas.drawText("不通過", from + line_len*3/4, top2, text_paint);
			}
		}
	}
	
	
	private class ChartView extends View{

		private Paint paint_pass = new Paint();
		private Paint paint_fail= new Paint();
		private Paint paint_none = new Paint();
		
		private Paint paint_highlight = new Paint();
		private Paint circle_paint_stroke = new Paint();
		private Paint text_paint_large = new Paint();
		private Paint text_paint_small = new Paint();
		private Paint text_paint_button = new Paint();
		private Paint focus_paint_len = new Paint();
		private Paint line_paint = new Paint();
		private Paint axis_paint = new Paint();
		private Paint record_paint = new Paint();
		private Paint no_record_paint = new Paint();
		
		private Paint emotion_paint = new Paint();
		private Paint desire_paint = new Paint();
		private Paint brac_paint = new Paint();
		
		private Paint emotion_paint_bg = new Paint();
		private Paint desire_paint_bg = new Paint();
		private Paint brac_paint_bg = new Paint();
		
	    private int RADIUS;
	    private int RADIUS_SQUARE;
	    private int BUTTON_RADIUS;
	    private int BUTTON_RADIUS_SQUARE;
	    private int BUTTON_GAPS;
	    
	    private int curX = -1,curY = -1;
	    
	    private ArrayList<Point>circle_centers;
	    private ArrayList<Point> selected_centers;
	    private ArrayList<Point> button_centers;
	    
	    private float top_touch;
	    
		public ChartView(Context context) {
			super(context);
			
			paint_pass.setColor(0xFF5bdebe);
			paint_fail.setColor(0xFFf09600);
			paint_none.setColor(0xFFc9c9ca);
			
			record_paint.setColor(0xFFff6f61);
			no_record_paint.setColor(0xFF858585);
			
			paint_highlight .setColor(0x33FFFFFF);
			
			circle_paint_stroke.setColor(0xFFFF0000);
			circle_paint_stroke.setStyle(Style.STROKE);
			circle_paint_stroke.setStrokeWidth(screen.x * 7 / 1080);
			
			text_paint_large.setColor(0xFFFFFFFF);
			text_paint_large.setTextSize(screen.x * 16F/480F);
			text_paint_large.setTextAlign(Align.LEFT);
			
			text_paint_button.setColor(0xFFFFFFFF);
			text_paint_button.setTextSize(screen.x * 16F/480F);
			text_paint_button.setTextAlign(Align.CENTER);
			
			text_paint_small.setColor(0xFF908f90);
			text_paint_small.setTextAlign(Align.CENTER);
			text_paint_small.setTextSize(screen.x * 20F/480F);
			
			focus_paint_len.setColor(0x44FFFFFF);
			
			axis_paint.setColor(0xFF5f5f5f);
			axis_paint.setStrokeWidth(screen.x * 8 / 1080);
			
			line_paint.setColor(0xFFFFFFFF);
			line_paint.setStrokeWidth(screen.x * 3 / 1080);
			
			
			emotion_paint.setColor(0xFF2dc7b3);
			emotion_paint.setStrokeWidth(screen.x * 4 / 480);
			desire_paint.setColor(0xFFf19700);
			desire_paint.setStrokeWidth(screen.x * 4 / 480);
			brac_paint.setColor(0xFFFFFFFF);
			brac_paint.setStrokeWidth(screen.x * 4 / 480);
			
			emotion_paint_bg.setColor(0x772dc7b3);
			desire_paint_bg.setColor(0x77f19700);
			brac_paint_bg.setColor(0x77FFFFFF);
			
			selected_centers = new ArrayList<Point>();
			circle_centers = new ArrayList<Point>();
			button_centers = new ArrayList<Point>();
			
			bar_width = screen.x * 36 / 1080;
		    bar_gap = screen.x * 12 / 1080;
		    chart_width = screen.x;
		    circle_radius = bar_width/3;
		    bar_bottom = screen.x*90/1080;
		    bar_left = screen.x * 94/1080;
		    
		    RADIUS = bar_width*9/5;
			RADIUS_SQUARE = RADIUS *RADIUS ;
			BUTTON_RADIUS = chartCircleBmp.getWidth()/2;
			BUTTON_RADIUS_SQUARE = BUTTON_RADIUS*BUTTON_RADIUS;
			BUTTON_GAPS = BUTTON_RADIUS * 4;
			
			top_touch = screen.x * 180/1080;
		}
		
	    @Override  
	    public boolean onTouchEvent(MotionEvent event) {  
	    	
	    	if (!chartTouchable)
	    		return true;
	    	
	    	int action = event.getAction();
	    	if (action == MotionEvent.ACTION_DOWN){
	    		int x= (int)event.getX();
	    		int y = (int) event.getY();
	    	
	    		boolean onButton = false;
	    		int buttonNum = 0;
	    		for (int i=0;i<button_centers.size();++i){
	    			Point c = button_centers.get(i);
	    			int distance_square = (c.x - x)*(c.x - x) + (c.y - y)*(c.y - y);
	    			if (distance_square < BUTTON_RADIUS_SQUARE){
	    				onButton = true;
	    				buttonNum = i;
	    				break;
	    			}
	    		}
	    		
	    		if (onButton)
	    			recordBox.showMsgBox(selected_dates.get(buttonNum),selected_idx.get(buttonNum));
	    		else{
	    			float ty = event.getY();
	    			if (ty>= top_touch){
	    				curX = (int) event.getX();  
	    				curY = (int) event.getY();
	    			}
	    		}
	    	}
	    	invalidate();
	        return true;
	    }  
		
		@SuppressLint("DrawAllocation")
		@Override
		protected void onDraw(Canvas canvas){
			super.onDraw(canvas);
			
			circle_centers.clear();
			selected_centers.clear();
			selected_idx.clear();
			button_centers.clear();
			selected_dates.clear();
			
			if (chart_type <3){
				drawBarChart(canvas);
				drawButtons(canvas);
			}
			else
				drawLineChart(canvas);
			
		}
		
		private void drawLineChart(Canvas canvas){
			int max_height = (chartHeight - bar_bottom)*4/10;
			int left = bar_left;
			int small_radius = circle_radius/2;
			
			int _bottom = chartHeight - bar_bottom;
			
			canvas.drawLine(left, _bottom, chart_width, _bottom, emotion_paint);
			
			
			if (bars.size() == 0)
				return;
			
			Point prev_e_center = null;
			Point prev_d_center = null;
			Point prev_b_center = null;
			
			for (int i=0;i<bars.size();++i){
				BarInfo bar = bars.get(i);
				
				float e_height,d_height,b_height;
				e_height = bar.emotion/5 * max_height;
				d_height = bar.desire/10 * max_height;
				b_height = bar.brac/0.3F * max_height;
				if (b_height > max_height)
					b_height = max_height;
				
				int e_top = _bottom - (int)e_height;
				int d_top = _bottom - (int)d_height;
				int b_top = _bottom - (int)b_height;
				if (!bar.hasData)
					e_top = d_top = b_top = _bottom;
				
				
				//Draw X axis Label
				if (i%7 == 0){
					String str= (bar.dv.month+1)+"/"+bar.dv.date;
					canvas.drawLine(left, _bottom, left, _bottom-max_height, axis_paint);
					canvas.drawText(str, left+small_radius, _bottom + bar_width*2, text_paint_small);
				}
				
				//Draw bars & annotation_circles
				Point e_center = new Point(left+small_radius,e_top - bar_gap - small_radius);
				Point d_center = new Point(left+small_radius,d_top - bar_gap - small_radius);
				Point b_center = new Point(left+small_radius,b_top - bar_gap - small_radius);
				
					if (prev_e_center!= null && prev_d_center!=null && prev_b_center!=null){
						
						Path path_e = new Path();
						path_e.moveTo(prev_e_center.x, _bottom);
						path_e.lineTo(prev_e_center.x, prev_e_center.y);
						path_e.lineTo(e_center.x, e_center.y);
						path_e.lineTo(e_center.x, _bottom);
						
						Path path_d = new Path();
						path_d.moveTo(prev_d_center.x, _bottom);
						path_d.lineTo(prev_d_center.x, prev_d_center.y);
						path_d.lineTo(d_center.x, d_center.y);
						path_d.lineTo(d_center.x, _bottom);
						path_d.lineTo(prev_d_center.x, _bottom);
						
						Path path_b = new Path();
						path_b.moveTo(prev_d_center.x, _bottom);
						path_b.lineTo(prev_b_center.x, prev_b_center.y);
						path_b.lineTo(b_center.x, b_center.y);
						path_b.lineTo(b_center.x, _bottom);
						path_b.lineTo(prev_b_center.x, _bottom);
						
						canvas.drawPath(path_e, emotion_paint_bg);
						canvas.drawPath(path_d, desire_paint_bg);
						canvas.drawPath(path_b, brac_paint_bg);
						
						canvas.drawLine(prev_e_center.x, prev_e_center.y, e_center.x, e_center.y, emotion_paint);
						canvas.drawLine(prev_d_center.x, prev_d_center.y, d_center.x, d_center.y, desire_paint);
						canvas.drawLine(prev_b_center.x, prev_b_center.y, b_center.x, b_center.y, brac_paint);
						
					}else{
						canvas.drawLine(e_center.x, _bottom, e_center.x, e_center.y, emotion_paint);
						canvas.drawLine(d_center.x, _bottom, d_center.x, d_center.y, desire_paint);
						canvas.drawLine(b_center.x, _bottom, b_center.x, b_center.y, brac_paint);
					}
					prev_e_center = e_center;
					prev_d_center = d_center;
					prev_b_center = b_center;
				
				// draw highlights
				if (bar.week == page_week)
					canvas.drawRect(left, _bottom-max_height - bar_width-circle_radius,left + bar_width+bar_gap, _bottom, paint_highlight);
				
				if (i == bars.size()-1){
					canvas.drawLine(e_center.x, _bottom, e_center.x, e_center.y, emotion_paint);
					canvas.drawLine(d_center.x, _bottom, d_center.x, d_center.y, desire_paint);
					canvas.drawLine(b_center.x, _bottom, b_center.x, b_center.y, brac_paint);
				}
				left += (bar_width+bar_gap);
			}
			
			if (curX>0 && curY > 0)
				canvas.drawCircle(curX, curY, RADIUS, focus_paint_len);
			
		}
		
		private void drawBarChart(Canvas canvas){
			int max_height = (chartHeight - bar_bottom)*4/10;
			int left = bar_left;
			
			if (bars.size() == 0)
					return;

			int playW = chartPlay.getWidth()/2;
			int playH = chartPlay.getHeight()/2;
			
			int bar_half = bar_width/2;
			for (int i=0;i<bars.size();++i){
				
				float height = 0;
				BarInfo bar = bars.get(i);
				
				if (chart_type == 0)
					height =bar.emotion/5 * max_height;
				else if (chart_type == 1)
					height =bar.desire/10 * max_height;
				else if (chart_type == 2){
					height = bar.brac / 0.3F * max_height;
					if (height > max_height)
						height = max_height;
				}
				
				//Draw bars & annotation_circles & highlights
				int right = left+bar_width;
				int _bottom = chartHeight - bar_bottom;
				int _top = _bottom - (int)height;
				
				
				//Draw bars & annotation_circles
				Point center = new Point(left+bar_half,_top - bar_gap - circle_radius);
				
				boolean hasAudioData = hasAudio.get(i);;
				if (!hasAudioData)
					canvas.drawCircle(center.x,center.y, circle_radius, no_record_paint);
				else
					canvas.drawBitmap(chartPlay, center.x - playW, center.y - playH, null);
				
				if (!bar.hasData);
				else if (bar.brac > 0F)
					canvas.drawRect(left, _top, right, _bottom, paint_fail);
				else
					canvas.drawRect(left, _top, right, _bottom, paint_pass);
				
				circle_centers.add(center);

				// draw highlights
				if (bar.week == page_week)
					canvas.drawRect(left, _bottom-max_height - bar_width-circle_radius, right+bar_gap, _bottom, paint_highlight);
				
				
				//Draw X axis Label
				if (i%7 == 0){
					String str= (bar.dv.month+1)+"/"+bar.dv.date;
					canvas.drawText(str, left+circle_radius, _bottom + bar_width*2, text_paint_small);
				}
				left += (bar_width+bar_gap);
			}
		}
		
		private void drawButtons(Canvas canvas){
			//Draw buttons
			if (curX>0 && curY > 0){
				
				//Draw focus area
				canvas.drawCircle(curX, curY, RADIUS, focus_paint_len);
				
				for (int i=0;i<circle_centers.size();++i){
					Point c = circle_centers.get(i);
					int distance_square = (curX-c.x)* (curX-c.x)+(curY-c.y)* (curY-c.y);
					if (distance_square < RADIUS_SQUARE){
						DateValue d = bars.get(i).dv;
						selected_centers.add(c);
						selected_dates.add(d);
						selected_idx.add(i);
					}
				}
				
				int b_center_x = screen.x * 180/1080 + scrollView.getScrollX(); 
				
				int b_center_y = screen.x * 170/1080;
				
				int b_center_x_bak = b_center_x;
				//Draw lines
				for (int i=0;i<selected_centers.size();++i){
					Point from = selected_centers.get(i);
					Point to = new Point(b_center_x,b_center_y);
					button_centers.add(to);
					canvas.drawLine(from.x, from.y, to.x,to.y, line_paint);
					b_center_x+=BUTTON_GAPS;
				}
				//Draw buttons
				b_center_x = b_center_x_bak;
				for (int i=0;i<selected_centers.size();++i){
					Point to = new Point(b_center_x,b_center_y);
					
					canvas.drawBitmap(chartCircleBmp, to.x - BUTTON_RADIUS, to.y - BUTTON_RADIUS, null);
					DateValue d = selected_dates.get(i);
					String str = (d.month+1) + "/" + d.date;
					canvas.drawText(str, to.x, to.y+BUTTON_RADIUS/3, text_paint_button);
					b_center_x+=BUTTON_GAPS;
				}
			}
		}
	}

	public void enablePage(boolean enable){
		chartTouchable = enable;
		pageWidget.setEnabled(enable);
		scrollView.setEnabled(enable);
		chart.setEnabled(enable);
		FragmentTabs.enableTab(enable);
	}
	
	private void checkHasRecorder(){
		hasAudio.clear();
		for (int i=0;i<bars.size();++i){
			if (adb.hasAudio(bars.get(i).dv))
				hasAudio.add(true);
			else
				hasAudio.add(false);
		}
		if (chart!=null)
			chart.invalidate();
	}
	
	public void updateHasRecorder(int idx){
		if (idx >=0 && idx < bars.size())
			hasAudio.set(idx, adb.hasAudio(bars.get(idx).dv));
	}
	

	
}
