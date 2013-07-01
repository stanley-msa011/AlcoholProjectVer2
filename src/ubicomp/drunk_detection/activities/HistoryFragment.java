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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class HistoryFragment extends Fragment {

	private View  view;
	
	private ImageView prevImage;
	private RelativeLayout pageLayout;
	private RelativeLayout chartLayout;
	private RelativeLayout chartAreaLayout;
	private HistoryDB hdb;
	private AudioDB adb;
	private PageWidgetVertical pageWidget;
	private PageAnimationTaskVertical pageAnimationTask;
	private PageAnimationTaskVertical2 pageAnimationTask2;
	
	private HorizontalScrollView scrollView;
	
	private AlphaAnimation prevAnimation;
	private AlphaAnimationEndHandler aaEndHandler;
	
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
	private Bitmap prev_bg_bmp;
	
	
	private PointF touchPoint;
	private PointF from,to;
	private HistoryFragment historyFragment;
	
	private int page_week;
	private PointF curPageTouch;
	private LoadingHandler loadHandler;
	
	private LinearLayout stageLayout;
	private TextView stage,stageNum;
	private Typeface stageTypeface;
	
	private DecimalFormat format;
	private TextView progressText, dateText, quoteText;
	
	private Calendar from_cal;
	private Calendar to_cal;
	
	private int max_week;
	private boolean chartTouchable = true;
	
	private AudioRecordBox recordBox;
	
	private ProgressDialog dialog;
	
	private Bitmap chartBg1Bmp, chartBg2Bmp, chartBg3Bmp, chartBg4Bmp, chartCircleBmp;
	private BitmapDrawable chartBg1, chartBg2, chartBg3, chartBg4;
	
	private AccumulatedHistoryState[] page_states;
	
	private static final String[] QUOTE_STR = {
		"酒精是一位可畏的敵人，\n但你相信不會被敵人擺布。\n你不會低頭，也不會認輸。",
		"你告訴自己，想踏上這條戒酒的路已經想很久了，\n現在就正在這路上, 你會珍惜。\n一天過一天，你可以感覺狀況愈來愈好。",
		"你發現不能空等你的船來到，\n而是你得自己游向它。",
		"你清楚你可以從過去的失敗中，\n要如何讓自己現在更好，\n更珍惜戒酒的日子。",
		"你開始懂得好好善待自己，\n並且”自愛”是一生最重要的一場戀愛。",
		"對過去憤怒，或對未來恐懼，\n只是讓你的生活變得更無力，\n更容易錯過現在。",
		"我們的價值是由我們是怎樣的人來決定，\n而不是我們有了什麼來決定。\n你更確認自己要保持清醒。",
		"你正勇於面對每一個戒酒在生活的挑戰，\n特別是接受自己的不足或缺點，\n但你珍惜自己的任何改變。",
		"你發現當你越認真努力戒酒，\n生活變得更順，運氣也會跟著變好。",
		"你相信自己一步一步在走\n你自己所創造的不一樣中。",
		"就像一次一次的身體訓練，\n每一次都讓我們變得更強壯或更堅定。",
		"你發現別人的鼓勵可以讓我們更堅持下去，\n但如果也可以鼓勵他人，\n更能讓我們生活更有意義。"
	};
	
	private static final int MAX_PAGE_WEEK = 11;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	this.historyFragment = this;
    	view = inflater.inflate(R.layout.history_fragment2, container,false);
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
    	pageLayout.removeView(prevImage);
    	chartLayout.removeView(chart);
    	chartAreaLayout.removeView(chartYAxis);
    	chartAreaLayout.removeView(chartTitle);
    	chartAreaLayout.removeView(chartLabel);
    	
    	pageWidget.setBitmaps(null, null);
    	prevImage.setImageBitmap(null);
    	scrollView.setBackgroundColor(0x00000000);
    	chartAreaLayout.setBackgroundColor(0x00000000);
    	
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
    	if (aaEndHandler!=null){
    		aaEndHandler.removeMessages(0);
    		aaEndHandler = null;
    	}
    	
    	if (cur_bg_bmp!=null && !cur_bg_bmp.isRecycled()){
    		cur_bg_bmp.recycle();
    		cur_bg_bmp=null;
    	}
    	if (next_bg_bmp!=null && !next_bg_bmp.isRecycled()){
    		next_bg_bmp.recycle();
    		next_bg_bmp=null;
    	}
    	if (prev_bg_bmp!=null && !prev_bg_bmp.isRecycled()){
    		prev_bg_bmp.recycle();
    		prev_bg_bmp=null;
    	}
    	if (pageWidget!=null){
    		pageWidget.destroyDrawingCache();
    		pageWidget.clear();
    		pageWidget=null;
    	}
    	if (chartBg1Bmp!=null && !chartBg1Bmp.isRecycled()){
    		chartBg1Bmp.recycle();
    		chartBg1Bmp = null;
    	}
    	if (chartBg2Bmp!=null && !chartBg2Bmp.isRecycled()){
    		chartBg2Bmp.recycle();
    		chartBg2Bmp = null;
    	}
    	if (chartBg3Bmp!=null && !chartBg3Bmp.isRecycled()){
    		chartBg3Bmp.recycle();
    		chartBg3Bmp = null;
    	}
    	if (chartBg4Bmp!=null && !chartBg4Bmp.isRecycled()){
    		chartBg4Bmp.recycle();
    		chartBg4Bmp = null;
    	}
    	if (chartCircleBmp!=null && !chartCircleBmp.isRecycled()){
    		chartCircleBmp.recycle();
    		chartCircleBmp = null;
    	}
    	System.gc();
    }
    
    
    private void initView(){
    	pageLayout = (RelativeLayout) view.findViewById(R.id.history_book_layout);
    	chartLayout = (RelativeLayout) view.findViewById(R.id.history_content_layout);
    	scrollView = (HorizontalScrollView) view.findViewById(R.id.history_scroll_view);
    	chartAreaLayout = (RelativeLayout) view.findViewById(R.id.history_chart_area_layout);
    	
    	/*Setting the play*/
    	bg_x = screen.x;
    	width = bg_x;
    	height = bg_x* 1055/1080;
    	
    	from = new PointF(width,height);
    	to = new PointF(width*0.8F,-height);
    	touchPoint = new PointF(from.x,from.y);
    	
    	pageWidget= new PageWidgetVertical(pageLayout.getContext(),width,height);
    	
    	curPageTouch = touchPoint;
    	
    	stageLayout = (LinearLayout) view.findViewById(R.id.history_stage_layout);
    	stageTypeface = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/dinpromedium.ttf");
    	stage = (TextView) view.findViewById(R.id.history_stage);
    	stage.setTextSize(TypedValue.COMPLEX_UNIT_PX, bg_x*64/1080);
    	stage.setTypeface(stageTypeface);
    	stageNum = (TextView) view.findViewById(R.id.history_stage_num);
    	stageNum.setTextSize(TypedValue.COMPLEX_UNIT_PX, bg_x*80/1080);
    	stageNum.setTypeface(stageTypeface);
    	
    	
    	progressText = (TextView) view.findViewById(R.id.history_progress);
    	progressText.setTextSize(TypedValue.COMPLEX_UNIT_PX, bg_x*64/1080);
    	progressText.setTypeface(stageTypeface);
    	dateText = (TextView) view.findViewById(R.id.history_date);
    	dateText.setTextSize(TypedValue.COMPLEX_UNIT_PX, bg_x*64/1080);
    	dateText.setTypeface(stageTypeface);
    	quoteText = (TextView) view.findViewById(R.id.history_quote);
    	quoteText.setTextSize(TypedValue.COMPLEX_UNIT_PX, bg_x*48/1080);
    	quoteText.setTypeface(stageTypeface);
    	
    	format = new DecimalFormat();
		format.setMaximumIntegerDigits(2);
		format.setMinimumIntegerDigits(2);
		format.setMinimumFractionDigits(1);
		format.setMaximumFractionDigits(1);
    	
    	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    	
    	Bitmap tmp;
    	BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inSampleSize = 2;
    	Resources r = historyFragment.getResources();
		
    	AccumulatedHistoryState curAH = page_states[page_week];
    	
		//tmp = BitmapFactory.decodeResource(historyFragment.getResources(), HistoryStorytelling.getPage(page_week),opt);\
		tmp = BitmapFactory.decodeResource(r, HistoryStorytelling.getPage(curAH.getScore(), curAH.week), opt);
		cur_bg_bmp = Bitmap.createScaledBitmap(tmp, width, height, true);
		tmp.recycle();
		next_bg_bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
			
		prev_bg_bmp = null;
		AccumulatedHistoryState prevAH = null;
		if ( page_week> 0){
			prevAH = page_states[page_week - 1];
			tmp = BitmapFactory.decodeResource(r,HistoryStorytelling.getPage(prevAH.getScore(), prevAH.week) ,opt);
			prev_bg_bmp = Bitmap.createScaledBitmap(tmp, width, height, true);
			tmp.recycle();
		}	
		 setPage();
		 
		 int chart_height = screen.x * 548/1080;
		tmp = BitmapFactory.decodeResource(historyFragment.getResources(), R.drawable.chart_bg1,opt);
		chartBg1Bmp = Bitmap.createScaledBitmap(tmp, screen.x,chart_height, true);
		tmp.recycle();
		
		tmp = BitmapFactory.decodeResource(historyFragment.getResources(), R.drawable.chart_bg2,opt);
		chartBg2Bmp = Bitmap.createScaledBitmap(tmp, screen.x, chart_height, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(historyFragment.getResources(), R.drawable.chart_bg3,opt);
		chartBg3Bmp = Bitmap.createScaledBitmap(tmp, screen.x, chart_height, true);
		tmp.recycle();
		
		tmp = BitmapFactory.decodeResource(historyFragment.getResources(), R.drawable.chart_bg4,opt);
		chartBg4Bmp = Bitmap.createScaledBitmap(tmp, screen.x,chart_height, true);
		tmp.recycle();
		
		tmp = BitmapFactory.decodeResource(historyFragment.getResources(), R.drawable.chart_circle);
		chartCircleBmp = Bitmap.createScaledBitmap(tmp, screen.x * 122/1080, screen.x * 122/1080, true);
		tmp.recycle();
		
		chartBg1 = new BitmapDrawable(historyFragment.getResources(),chartBg1Bmp);
		chartBg2 = new BitmapDrawable(historyFragment.getResources(),chartBg2Bmp);
		chartBg3 = new BitmapDrawable(historyFragment.getResources(),chartBg3Bmp);
		chartBg4 = new BitmapDrawable(historyFragment.getResources(),chartBg4Bmp);
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		
    	LayoutParams sParam = (LayoutParams) stageLayout.getLayoutParams();
    	sParam.leftMargin = bg_x*30/1080;
    	sParam.topMargin = bg_x * 190/1080;
    	
    	LayoutParams prParam = (LayoutParams) progressText.getLayoutParams();
    	prParam.leftMargin = bg_x*30/1080;
    	
    	LayoutParams dParam = (LayoutParams) dateText.getLayoutParams();
    	dParam.leftMargin = bg_x*10/1080;
    	dParam.topMargin = bg_x * 135/1080;
    	
    	LayoutParams qParam = (LayoutParams) quoteText.getLayoutParams();
    	qParam.leftMargin = bg_x*30/1080;
    	qParam.topMargin = bg_x * 135/1080;
    	
    	LayoutParams plParam = (LayoutParams) pageLayout.getLayoutParams();
    	plParam.topMargin = screen.x * 110/1080;
    	
    	pageLayout.addView(pageWidget);
    	LayoutParams param = (LayoutParams) pageWidget.getLayoutParams();
    	param.width = width;
    	param.height = height;

    	prevImage = new ImageView(pageLayout.getContext());
    	pageLayout.addView(prevImage);
    	LayoutParams pParam = (LayoutParams) prevImage.getLayoutParams();
    	pParam.width = width;
    	pParam.height = height;
    	pParam.topMargin = top_margin;
    	pParam.leftMargin = 0;
    	prevAnimation = null;
    	if (prev_bg_bmp!=null && !HistoryStorytelling.isChangePage(prevAH,curAH)){
    		prevImage.setImageBitmap(prev_bg_bmp);
    		prevAnimation = new AlphaAnimation(1.0F,0.0F);
    		prevAnimation.setDuration(2000);
    		prevImage.setAnimation(prevAnimation);
    		aaEndHandler = new AlphaAnimationEndHandler(); 
    	}
    	
    	setStorytellingTexts();
    	
    	//Set chart
    	RelativeLayout.LayoutParams scrollParam = (RelativeLayout.LayoutParams)scrollView.getLayoutParams();
    	scrollParam.width = screen.x;
    	scrollParam.height = chart_height;
    	
    	FrameLayout.LayoutParams clParam = (FrameLayout.LayoutParams)chartLayout.getLayoutParams();
    	clParam.width = screen.x;
    	clParam.height = chart_height;
    	
    	settingBars();
    	checkHasRecorder();
    	
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
		chartTitleParam.height = screen.x * 90 / 1080;
		
		chartLabel = new ChartLabelView(this.getActivity());
		chartAreaLayout.addView(chartLabel,0);
		RelativeLayout.LayoutParams chartLabelParam = (RelativeLayout.LayoutParams) chartLabel.getLayoutParams();
		chartLabelParam.width = screen.x * 540/1080;
		chartLabelParam.height = screen.x * 90 / 1080;
		chartLabelParam.topMargin = screen.x * 110/1080;
		chartLabelParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		
		if (chart_type == 0)
			chartAreaLayout.setBackground(chartBg1);
		else if (chart_type == 1)
			chartAreaLayout.setBackground(chartBg2);
		else if (chart_type == 2)
			chartAreaLayout.setBackground(chartBg3);
		else
			chartAreaLayout.setBackground(chartBg4);
		
		pageWidget.setOnTouchListener(gtListener);
		
    }
    
    private void setStorytellingTexts(){
    	AccumulatedHistoryState curAH = page_states[page_week];
    	stageNum.setText((page_week+1)+"/"+(HistoryStorytelling.MAX_PAGE+1));
    	float progress = curAH.getScore()/curAH.MAX_SCORE*100;
    	String progress_str = format.format(progress)+" %";
    	progressText.setText(progress_str);

    	Calendar date_cal = Calendar.getInstance();
    	date_cal.setTime(from_cal.getTime());
    	date_cal.add(Calendar.DATE, page_week * 7);
    	String from_date_str = (date_cal.get(Calendar.MONTH) +1)+ " / " +date_cal.get(Calendar.DAY_OF_MONTH);
    	date_cal.add(Calendar.DATE, 6);
    	String to_date_str = (date_cal.get(Calendar.MONTH) +1)+ " / " +date_cal.get(Calendar.DAY_OF_MONTH);
    	String date_str = from_date_str + "\n|\n" + to_date_str;
    	dateText.setText(date_str);
    	
    	quoteText.setText(QUOTE_STR[page_week]);
    }
    
    
    public void endAnimation(){
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
    	Log.d("PAGE_ANIMATION", "END ANIMATION");
    	isAnimation = false;
    	chart.invalidate();
    }
    
    public void setPage(){
    	Log.d("History","setPage start");
    	pageWidget.setBitmaps(cur_bg_bmp, next_bg_bmp);
    	pageWidget.setTouchPosition(curPageTouch);
    	Log.d("History","setPage end");
    }
    
    public void resetPage(int change){
    	Log.d("History","resetPage start");
    	if (cur_bg_bmp!=null && !cur_bg_bmp.isRecycled()){
    		cur_bg_bmp.recycle();
    		cur_bg_bmp = null;
    	}
    	Log.d("PAGE_ANIMATION", "reset level: "+page_week);
    	
    	if (change > 0){
    		++page_week;
    		if (page_week > max_week)
    			page_week = max_week;
    	}else{
    		--page_week;
    		if (page_week < 0)
    			page_week =0;
    	}
    	
    	Log.d("PAGE_ANIMATION", "reset level fix: "+page_week);
    	BitmapFactory.Options opt = new BitmapFactory.Options();
    	opt.inSampleSize = 2;
    	AccumulatedHistoryState AH = page_states[page_week];
    	Bitmap tmp = BitmapFactory.decodeResource(historyFragment.getResources(), HistoryStorytelling.getPage(AH.getScore(),AH.week),opt);
		cur_bg_bmp = Bitmap.createScaledBitmap(tmp, width, height, true);
		tmp.recycle();
    	pageWidget.setBitmaps(cur_bg_bmp, next_bg_bmp);
    	pageWidget.setTouchPosition(curPageTouch);
    	Log.d("History","resetPage end");
    }
    
    public void onStart(){
    	
    	dialog = new ProgressDialog(this.getActivity());
		dialog.setMessage("載入中");
		dialog.setCancelable(true);
		if (!dialog.isShowing()){
			dialog.show();
		}
		super.onStart();
    }
    
	@SuppressLint("HandlerLeak")
	private class LoadingHandler extends Handler{
		
		public void handleMessage(Message msg){
			initView();
			recordBox.setImage();
			endAnimation();
			
			if (dialog!=null && dialog.isShowing())
				dialog.dismiss();
			LoadingBox.dismiss();
			Log.d("PAGE_ANIMATION","END LOADING BOX");
			startAnim();
		}
	}

	private void startAnim(){
		
		Log.d("PAGE_ANIMATION","START ANIMATION");
		if (page_week == 0)
			return;
		
		AccumulatedHistoryState prevAH = null;
		AccumulatedHistoryState curAH = null;
		curAH = page_states[page_week];
		if (page_week > 0)
			prevAH = page_states[page_week-1];
		
		if (HistoryStorytelling.isChangePage(prevAH,curAH)){
			Log.d("PAGE_ANIMATION","START CHANGE PAGE ANIMATION");
			isAnimation = true;
			FragmentTabs.enableTab(false);
			
			int[] aBgs = HistoryStorytelling.getAnimationBgs(page_states);
			int pageIdx = page_week;
			int startIdx = pageIdx-1;
			if (startIdx <0)
				startIdx =0;
			prevImage.setVisibility(View.INVISIBLE);
			setStageVisible(false);
			pageAnimationTask = new PageAnimationTaskVertical(pageWidget,from,to,aBgs,historyFragment,curPageTouch,startIdx,pageIdx);
			pageAnimationTask.execute();
			
		}else{
			isAnimation = true;
			Log.d("PAGE_ANIMATION","START ALPHA ANIMATION=0");
			if (prevAnimation!=null){
				Log.d("PAGE_ANIMATION","START ALPHA ANIMATION");
				prevImage.setVisibility(View.VISIBLE);
				Runnable r = new alphaAnimationTimer();
				Thread t = new Thread(r);
				prevAnimation.start();
				t.start();
			}
		}
	}
	
	private class alphaAnimationTimer implements Runnable{
		@Override
		public void run() {
			try {
				Thread.sleep(2000);
				if (aaEndHandler!=null)
					aaEndHandler.sendEmptyMessage(0);
				isAnimation = false; 
			} catch (InterruptedException e) {}
		}
	}
	@SuppressLint("HandlerLeak")
	private class AlphaAnimationEndHandler extends Handler{
		public void handleMessage(Message msg){
			if (prevAnimation!=null)
				prevAnimation.cancel();
			prevImage.setAnimation(null);
			prevImage.setImageBitmap(null);
			prevImage.setVisibility(View.INVISIBLE);
			isAnimation = false;
		}
	}
	
	public void setStageVisible(boolean t){
		if (t){
			stageLayout.setVisibility(View.VISIBLE);
			progressText.setVisibility(View.VISIBLE);
			dateText.setVisibility(View.VISIBLE);
			quoteText.setVisibility(View.VISIBLE);
		}
		else{
			stageLayout.setVisibility(View.INVISIBLE);
			progressText.setVisibility(View.INVISIBLE);
			dateText.setVisibility(View.INVISIBLE);
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
			float x1 = e1.getX();
			float x2 = e2.getX();
			float y_change = Math.abs(y2- y1);
			float x_change = Math.abs(x2 - x1);
			if (x_change >= y_change)
				return true;
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
				prevImage.setVisibility(View.INVISIBLE);
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
				prevImage.setVisibility(View.INVISIBLE);
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
					e_sum += h.emotion;
					d_sum += h.desire;
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
		
		public ChartTitleView(Context context) {
			super(context);
			text_paint_large.setColor(0xFFAAAAAA);
			text_paint_large.setTextSize(screen.x * 48F/1080F);
			text_paint_large.setTextAlign(Align.LEFT);
		}
		
		@Override  
	    public boolean onTouchEvent(MotionEvent event) {
			if (!chartTouchable)
	    		return true;
			int x = (int) event.getX();

			if (event.getAction() == MotionEvent.ACTION_DOWN){
				if (x < screen.x * 310/1080){
					chart_type = 0;
					chartAreaLayout.setBackground(chartBg1);
				}
				else if (x < screen.x * 590/1080){
					chart_type = 1;
					chartAreaLayout.setBackground(chartBg2);
				}
				else if (x < screen.x * 870/1080){
					chart_type = 2;
					chartAreaLayout.setBackground(chartBg3);
				}else{
					chart_type = 3;
					chartAreaLayout.setBackground(chartBg4);
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
			text_paint.setTextSize(screen.x * 44F/1080F);
			
			emotion_paint.setColor(0xFFf29700);
			desire_paint.setColor(0xFFe84902);
			brac_paint.setColor(0xFFFFFFFF);
			
			paint_pass.setColor(0xFF5bdebe);
			paint_fail.setColor(0xFFe64802);
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
	    
	    
	    
		public ChartView(Context context) {
			super(context);
			
			paint_pass.setColor(0xFF5bdebe);
			paint_fail.setColor(0xFFe64802);
			paint_none.setColor(0xFFc9c9ca);
			
			record_paint.setColor(0xFFff6f61);
			no_record_paint.setColor(0xFF858585);
			
			paint_highlight .setColor(0x44AAAAFF);
			
			circle_paint_stroke.setColor(0xFFFF0000);
			circle_paint_stroke.setStyle(Style.STROKE);
			circle_paint_stroke.setStrokeWidth(screen.x * 7 / 1080);
			
			text_paint_large.setColor(0xFFFFFFFF);
			text_paint_large.setTextSize(screen.x * 64F/1080F);
			text_paint_large.setTextAlign(Align.LEFT);
			
			text_paint_button.setColor(0xFFFFFFFF);
			text_paint_button.setTextSize(screen.x * 40F/1080F);
			text_paint_button.setTextAlign(Align.CENTER);
			
			text_paint_small.setColor(0xFF908f90);
			text_paint_small.setTextAlign(Align.CENTER);
			text_paint_small.setTextSize(screen.x * 40F/1080F);
			
			focus_paint_len.setColor(0x44FFFFFF);
			
			axis_paint.setColor(0xFF5f5f5f);
			axis_paint.setStrokeWidth(screen.x * 8 / 1080);
			
			line_paint.setColor(0xFFFFFFFF);
			line_paint.setStrokeWidth(screen.x * 3 / 1080);
			
			emotion_paint.setColor(0xFFf29700);
			emotion_paint.setStrokeWidth(screen.x * 6 / 1080);
			desire_paint.setColor(0xFFe84902);
			desire_paint.setStrokeWidth(screen.x * 6 / 1080);
			brac_paint.setColor(0xFFFFFFFF);
			brac_paint.setStrokeWidth(screen.x * 6 / 1080);
			
			emotion_paint_bg.setColor(0x77f29700);
			desire_paint_bg.setColor(0x77e84902);
			brac_paint_bg.setColor(0x77FFFFFF);
			
			selected_centers = new ArrayList<Point>();
			circle_centers = new ArrayList<Point>();
			button_centers = new ArrayList<Point>();
			
			bar_width = screen.x * 24 / 1080;
		    bar_gap = screen.x * 8 / 1080;
		    chart_width = screen.x;
		    circle_radius = bar_width/3;
		    bar_bottom = screen.x*90/1080;
		    bar_left = screen.x * 94/1080;
		    
		    RADIUS = bar_width*9/5;
			RADIUS_SQUARE = RADIUS *RADIUS ;
			BUTTON_RADIUS = screen.x * 61/1080;
			BUTTON_RADIUS_SQUARE = BUTTON_RADIUS*BUTTON_RADIUS;
			BUTTON_GAPS = BUTTON_RADIUS * 8/3;
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
	    			curX = (int) event.getX();  
	    			curY = (int) event.getY();
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
				b_height = bar.brac/0.5F * max_height;
				if (b_height > max_height)
					b_height = max_height;
				
				int e_top = _bottom - (int)e_height;
				int d_top = _bottom - (int)d_height;
				int b_top = _bottom - (int)b_height;
				
				
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
				
				if (bar.hasData){
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
				}
				
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

			int bar_half = bar_width/2;
			for (int i=0;i<bars.size();++i){
				
				float height = 0;
				BarInfo bar = bars.get(i);
				
				if (chart_type == 0)
					height =bar.emotion/5 * max_height;
				else if (chart_type == 1)
					height =bar.desire/10 * max_height;
				else if (chart_type == 2){
					height = bar.brac / 0.5F * max_height;
					if (height > max_height)
						height = max_height;
				}
				
				//Draw bars & annotation_circles & highlights
				int right = left+bar_width;
				int _bottom = chartHeight - bar_bottom;
				int _top = _bottom - (int)height;
				
				
				//Draw bars & annotation_circles
				Point center = new Point(left+bar_half,_top - bar_gap - circle_radius);
				
				Paint dot_paint;
				if (hasAudio.get(i))
					dot_paint = record_paint;
				else
					dot_paint = no_record_paint;
				if (!bar.hasData){
					canvas.drawRect(left, _top, right, _bottom, paint_none);
					canvas.drawCircle(center.x,center.y, circle_radius, dot_paint);
				}
				else if (bar.brac > 0F){
					canvas.drawRect(left, _top, right, _bottom, paint_fail);
					canvas.drawCircle(center.x,center.y, circle_radius, dot_paint);
				}
				else{
					canvas.drawRect(left, _top, right, _bottom, paint_pass);
					canvas.drawCircle(center.x,center.y, circle_radius, dot_paint);
				}
				
				circle_centers.add(center);

				// draw highlights
				if (bar.week == page_week)
					canvas.drawRect(left, _bottom-max_height - bar_width-circle_radius, right+bar_gap, _bottom, paint_highlight);
				
				
				//Draw X axis Label
				if (i%7 == 0){
					String str= (bar.dv.month+1)+"/"+bar.dv.date;
					//canvas.drawLine(left+circle_radius, _bottom, left+circle_radius, _bottom + circle_radius, axis_paint);
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
				
				int b_center_x = screen.x * 290/1080 + scrollView.getScrollX(); 
				
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
					String str = d.month + "/" + d.date;
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
