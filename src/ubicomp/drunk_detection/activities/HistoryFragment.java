package ubicomp.drunk_detection.activities;

import java.util.ArrayList;
import java.util.Calendar;

import ubicomp.drunk_detection.activities.R;

import database.AudioDB;
import database.HistoryDB;
import history.DateBracGameHistory;
import history.pageEffect.PageAnimationTaskVertical;
import history.pageEffect.PageAnimationTaskVertical2;
import history.pageEffect.PageWidgetVertical;
import history.ui.DateValue;
import history.ui.HistoryStorytelling;
import history.ui.AudioRecordBox;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	private ArrayList<DateBracGameHistory> historys; ;
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
	
	private int level;
	private PointF curPageTouch;
	private LoadingHandler loadHandler;
	
	private LinearLayout stageLayout;
	private TextView stage,stageNum;
	private Typeface stageTypeface;
	
	private Calendar from_cal;
	private Calendar to_cal;
	
	private Bitmap labelBmp;

	private int achieve_level;
	private boolean chartTouchable = true;
	
	private AudioRecordBox recordBox;
	
	private ProgressDialog dialog;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	Log.d("History","createView start");
    	this.historyFragment = this;
    	view = inflater.inflate(R.layout.history_fragment2, container,false);
    	hdb = new HistoryDB(this.getActivity());
    	adb = new AudioDB(this.getActivity());
    	Log.d("History","createView end");
    	return view;
    }
   
	public void onResume(){
		
		super.onResume();
		
		
		isAnimation = false;
		screen = FragmentTabs.getSize();
		
		 level = hdb.getLatestBracGameHistory().level;
		 achieve_level = level;
		 Log.d("PAGE_ANIMATION", "ach: "+achieve_level);
		
		historys = new ArrayList<DateBracGameHistory>();
		selected_dates = new ArrayList<DateValue>();
		selected_idx = new ArrayList<Integer>();
		bars = new ArrayList<BarInfo>();
		hasAudio = new ArrayList<Boolean>();
		
		gListener = new GestureListener();
		gDetector = new GestureDetector(getActivity(), gListener);
		gtListener = new TouchListener();
		
		DateBracGameHistory[] h = hdb.getAllHistory();
		if (h != null)
			for (int i=0;i<h.length;++i)
				historys.add(h[i]);
		
		if (historys.size() == 0){
			NUM_OF_BARS = 0;
		}else{
			
			from_cal = Calendar.getInstance();
			DateBracGameHistory h_from= historys.get(0);
			from_cal.set(h_from.year, h_from.month-1, h_from.date, 0, 0, 0);
			from_cal.set(Calendar.MILLISECOND, 0);
			
			to_cal = Calendar.getInstance();
			DateBracGameHistory h_to= historys.get(historys.size()-1);
			to_cal.set(h_to.year, h_to.month-1, h_to.date, 0, 0, 0);
			to_cal.set(Calendar.MILLISECOND, 0);
			
			long millis = to_cal.getTimeInMillis() - from_cal.getTimeInMillis();
			NUM_OF_BARS= (int)(millis/(1000*60*60*24)) + 1;
		}
		
		RelativeLayout r = (RelativeLayout) view;
		recordBox = new AudioRecordBox(this,r);
		if (loadHandler == null)
			loadHandler = new LoadingHandler();
		loadHandler.sendEmptyMessage(0);
	}
    
    public void onPause(){
    	if (recordBox!=null)
    		recordBox.OnPause();
    	clear();
    	super.onPause();
    }
	
    private void clear(){
    	pageWidget.setBitmaps(null, null);
    	prevImage.setImageBitmap(null);
    	
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
    	if (labelBmp!=null && !labelBmp.isRecycled()){
    		labelBmp.recycle();
    		labelBmp = null;
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
    	height = (int)(bg_x*365.0/355.0);
    	
    	from = new PointF(width,height);
    	to = new PointF(width*0.8F,-height);
    	touchPoint = new PointF(from.x,from.y);
    	
    	pageWidget= new PageWidgetVertical(pageLayout.getContext(),width,height);
    	
    	curPageTouch = touchPoint;
    	
    	stageLayout = (LinearLayout) view.findViewById(R.id.history_stage_layout);
    	stageTypeface = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/dinpromedium.ttf");
    	stage = (TextView) view.findViewById(R.id.history_stage);
    	stage.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(bg_x*40.0/720.0));
    	stage.setTypeface(stageTypeface);
    	stageNum = (TextView) view.findViewById(R.id.history_stage_num);
    	stageNum.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(bg_x*64.0/720.0));
    	stageNum.setTypeface(stageTypeface);
    	
    	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    	
    	Bitmap tmp;
		tmp = BitmapFactory.decodeResource(historyFragment.getResources(), HistoryStorytelling.getPage(level));
		cur_bg_bmp = Bitmap.createScaledBitmap(tmp, width, height, true);
		tmp.recycle();
		next_bg_bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
			
		prev_bg_bmp = null;
		if (level > 0){
			tmp = BitmapFactory.decodeResource(historyFragment.getResources(), HistoryStorytelling.getPage(level-1));
			prev_bg_bmp = Bitmap.createScaledBitmap(tmp, width, height, true);
			tmp.recycle();
		}	
		
		tmp = BitmapFactory.decodeResource(getResources(), R.drawable.history_label);
		labelBmp = Bitmap.createScaledBitmap(tmp, screen.x * 374/1080, screen.x *86/1080 , true);
		tmp.recycle();
    	
		 setPage();
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		
    	LayoutParams sParam = (LayoutParams) stageLayout.getLayoutParams();
    	sParam.rightMargin = bg_x*50/1080;
    	sParam.topMargin = bg_x*900/1080;

    	stageNum.setText(HistoryStorytelling.getPageNum(level)+"/"+HistoryStorytelling.MAX_PAGE);
    	
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
    	if (prev_bg_bmp!=null && !HistoryStorytelling.isChangePage(level)){
    		prevImage.setImageBitmap(prev_bg_bmp);
    		prevAnimation = new AlphaAnimation(1.0F,0.0F);
    		prevAnimation.setDuration(2000);
    		prevImage.setAnimation(prevAnimation);
    		aaEndHandler = new AlphaAnimationEndHandler(); 
    	}
    	
    	
    	//Set chart
    	RelativeLayout.LayoutParams scrollParam = (RelativeLayout.LayoutParams)scrollView.getLayoutParams();
    	scrollParam.width = screen.x;
    	scrollParam.height = screen.x * 1709/1080 - height;
    	
    	FrameLayout.LayoutParams clParam = (FrameLayout.LayoutParams)chartLayout.getLayoutParams();
    	clParam.width = screen.x;
    	clParam.height = screen.x * 1709/1080 - height;
    	
    	settingBars();
    	checkHasRecorder();
    	
    	chart = new ChartView(this.getActivity());
    	chartLayout.addView(chart);
    	

		
    	
    	chartHeight = screen.x * 1709/1080 - height;
    	chart_width =  bar_left * 3/2 + (bar_width + bar_gap)* NUM_OF_BARS;
    	if (chart_width < screen.x)
			chart_width = screen.x;
    	
    	RelativeLayout.LayoutParams chartParam = (RelativeLayout.LayoutParams) chart.getLayoutParams();
		chartParam.width= chart_width;
		chartParam.height =  screen.x * 1709/1080 - height;
		
		chartYAxis = new ChartYAxisView(this.getActivity());
		chartAreaLayout.addView(chartYAxis);
		RelativeLayout.LayoutParams chartYParam = (RelativeLayout.LayoutParams) chartYAxis.getLayoutParams();
		chartYParam.width = screen.x * 94/1080;
		chartYParam.height = chartParam.height;
		
    	chartTitle = new ChartTitleView(this.getActivity());
    	chartAreaLayout.addView(chartTitle);
    	RelativeLayout.LayoutParams chartTitleParam = (RelativeLayout.LayoutParams) chartTitle.getLayoutParams();
		chartTitleParam.width = labelBmp.getWidth();
		chartTitleParam.height = labelBmp.getHeight();
		
		chartLabel = new ChartLabelView(this.getActivity());
		chartAreaLayout.addView(chartLabel);
		RelativeLayout.LayoutParams chartLabelParam = (RelativeLayout.LayoutParams) chartLabel.getLayoutParams();
		chartLabelParam.width = screen.x - labelBmp.getWidth();
		chartLabelParam.height = screen.x * 120/1080;
		chartLabelParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		
		pageWidget.setOnTouchListener(gtListener);
		
    }
    
    
    public void endAnimation(){
    	setStageVisible(true);
    	FragmentTabs.enableTab(true);
    	isAnimation = false;
    	chart.invalidate();
    }
    
    
    public void endAnimation(int tt){
    	Log.d("PAGE_ANIMATION", "END ANIMATION level:" + level);
    	stageNum.setText(HistoryStorytelling.getPageNum(level)+"/"+HistoryStorytelling.MAX_PAGE);
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
    	Log.d("PAGE_ANIMATION", "reset level: "+level);
    	
    	if (change > 0){
    		level = HistoryStorytelling.getNextPageLevel(level);
    		Log.d("PAGE_ANIMATION", "reset level(+): "+level);
    		if (level > achieve_level)
    			level = achieve_level;
    	}else{
    		level = HistoryStorytelling.getPrevPageLevel(level);
    		Log.d("PAGE_ANIMATION", "reset level(-): "+level);
    	}
    	
    	Log.d("PAGE_ANIMATION", "reset level fix: "+level);
    	Bitmap tmp = BitmapFactory.decodeResource(historyFragment.getResources(), HistoryStorytelling.getPage(level));
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
		if (level == 0)
			return;
		
		if (HistoryStorytelling.isChangePage(level)){
			Log.d("PAGE_ANIMATION","START CHANGE PAGE ANIMATION");
			isAnimation = true;
			FragmentTabs.enableTab(false);
			int[] aBgs = HistoryStorytelling.getAnimationBgs(level);
			int pageIdx = HistoryStorytelling.getPageNum(level)-1;
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
		if (t)
			stageLayout.setVisibility(View.VISIBLE);
		else
			stageLayout.setVisibility(View.INVISIBLE);
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
			Log.d("PAGE_ANIMATION", "onDown");
			return true;
		}
		

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.d("PAGE_ANIMATION", "FLING");
			if (isAnimation)
				return true;
			isAnimation = true;
			
			float y1 = e1.getY();
			float y2 = e2.getY();
			
			if (y1 - y2 > 0){//UP
				Log.d("PAGE_ANIMATION", "UP");
				pageWidget.setOnTouchListener(null);
				FragmentTabs.enableTab(false);
				int[] aBgs = HistoryStorytelling.getAnimationBgs(level);
				int pageIdx = HistoryStorytelling.getPageNum(level)-1;
				int startIdx = pageIdx;
				int endIdx = startIdx+1;
				Log.d("PAGE_ANIMATION", "UP"+startIdx+"/"+endIdx);
				if (endIdx > (HistoryStorytelling.getPageNum(achieve_level)-1)){
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
				int[] aBgs = HistoryStorytelling.getAnimationBgs(level);
				int pageIdx = HistoryStorytelling.getPageNum(level)-1;
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
	
	private static final long DAY_SEC = 60*60*24;
	
	public void settingBars(){

		bars.clear();
		
		if (NUM_OF_BARS == 0)
			return;
		
		long from_t = from_cal.getTimeInMillis()/1000;
		Calendar ccal = Calendar.getInstance();
		ccal.setTimeInMillis(from_cal.getTimeInMillis());
		
		int prevLevel = 0;
		
		for (int i=0;i<NUM_OF_BARS;++i){
			
			int count = 0;
			float e_sum = 0;
			float d_sum = 0;
			float b_sum = 0;
			
			float emotion,desire,brac;
			int from_page = -1,to_page = -1;
			
			int pos = 0;
			
			for (int j=pos; j<historys.size();++j){
				DateBracGameHistory h = historys.get(j);
				if (h.timestamp >= from_t && h.timestamp < from_t + DAY_SEC){
					if (from_page == -1)
						from_page = HistoryStorytelling.getPageNum(h.level);
					
					e_sum += h.emotion;
					d_sum += h.desire;
					b_sum += h.brac;
					++count;
					prevLevel = h.level;
					
				}else if(h.timestamp >= from_t + DAY_SEC){
					pos = j;
					break;
				}
			}
			
			boolean hasData = true;
			if (count == 0){
				hasData = false;
				emotion = desire = brac = 0F;
				from_page = HistoryStorytelling.getPageNum(prevLevel);
			}else{
				emotion = e_sum/count;
				desire = d_sum/count;
				brac = b_sum/count;
			}
			to_page = HistoryStorytelling.getPageNum(prevLevel);
			
			int mYear = ccal.get(Calendar.YEAR);
			int mMonth = ccal.get(Calendar.MONTH)+1;
			int mDate = ccal.get(Calendar.DAY_OF_MONTH);
			
			DateValue dv = new DateValue(mYear,mMonth,mDate);
			
			BarInfo barInfo;
			barInfo = new BarInfo(emotion, desire, brac, from_page, to_page,hasData,dv);
			
			bars.add(barInfo);
			
			from_t+=DAY_SEC;
			ccal.add(Calendar.DATE, 1);
		}
		
	}
	
	private int chart_type = 0;
	
	private class ChartTitleView extends View{

		private Paint text_paint_large = new Paint();
		
		public ChartTitleView(Context context) {
			super(context);
			text_paint_large.setColor(0xFFFFFFFF);
			text_paint_large.setTextSize(screen.x * 64F/1080F);
			text_paint_large.setTextAlign(Align.LEFT);
		}
		
		@Override  
	    public boolean onTouchEvent(MotionEvent event) {
			if (!chartTouchable)
	    		return true;
			if (event.getAction() == MotionEvent.ACTION_DOWN){
				chart_type = (chart_type+1)%4;
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
			if (labelBmp !=null && !labelBmp.isRecycled())
				canvas.drawBitmap(labelBmp,0, 0, null);
			if (chart_type == 0)
				canvas.drawText("心情量尺", screen.x * 20F/1080F, screen.x * 70F/1080F,text_paint_large);
			else if (chart_type == 1)
				canvas.drawText("渴癮指數", screen.x * 20F/1080F, screen.x * 70F/1080F,text_paint_large);
			else if (chart_type == 2)
				canvas.drawText("酒測結果", screen.x * 20F/1080F, screen.x * 70F/1080F,text_paint_large);
			else
				canvas.drawText("綜合資料", screen.x * 20F/1080F, screen.x * 70F/1080F,text_paint_large);
		}
	}
	
	private class ChartYAxisView extends View{

		private Paint axis_paint = new Paint();
		private Paint text_paint_small = new Paint();
		
		public ChartYAxisView(Context context) {
			super(context);
			text_paint_small.setColor(0xFF000000);
			text_paint_small.setTextAlign(Align.CENTER);
			text_paint_small.setTextSize(screen.x * 48F/1080F);
			axis_paint.setColor(0xFF000000);
			axis_paint.setStrokeWidth(screen.x * 7 / 1080);
		}
		
		@Override
		protected void onDraw(Canvas canvas){
			super.onDraw(canvas);
			
			canvas.drawColor(0xFF999999);
			
			int max_height = (chartHeight - bar_bottom)*5/10;
			int _bottom = chartHeight - bar_bottom;
			
			canvas.drawLine(3*bar_width, _bottom, 3*bar_width, _bottom - max_height - bar_width, axis_paint);
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
				maxLabel = "最高";
			canvas.drawText(maxLabel, 3*bar_width/2, _bottom - max_height, text_paint_small);
		}
	}
	
	private class ChartLabelView extends View{

		private Paint emotion_paint = new Paint();
		private Paint desire_paint = new Paint();
		private Paint brac_paint = new Paint();
		private Paint text_paint_small = new Paint();
		
		
		public ChartLabelView(Context context) {
			super(context);
			text_paint_small.setColor(0xFF000000);
			text_paint_small.setTextAlign(Align.CENTER);
			text_paint_small.setTextSize(screen.x * 48F/1080F);
			
			emotion_paint.setColor(0xFFFF0000);
			emotion_paint.setStrokeWidth(screen.x * 10 / 1080);
			desire_paint.setColor(0xFF00FF00);
			desire_paint.setStrokeWidth(screen.x * 10 / 1080);
			brac_paint.setColor(0xFFFFFFFF);
			brac_paint.setStrokeWidth(screen.x * 10 / 1080);
		}
		
		@Override
		protected void onDraw(Canvas canvas){
			super.onDraw(canvas);
			
			//canvas.drawColor(0xFF999999);
			int base = screen.x * 30/1080;
			int from = base;
			int text_top = base * 3;
			int line_top = base * 4;
			int line_len = base * 6;
			if (chart_type > 2 ){
				canvas.drawText("心情量尺", from + line_len/2, text_top, text_paint_small);
				canvas.drawLine(from, line_top, from + line_len, line_top, emotion_paint);
				from += line_len + base + base;
				
				canvas.drawText("渴癮指數", from + line_len/2, text_top, text_paint_small);
				canvas.drawLine(from, line_top, from + line_len, line_top, desire_paint);
				from += line_len + base + base;
				
				canvas.drawText("酒測結果", from + line_len/2, text_top, text_paint_small);
				canvas.drawLine(from, line_top, from + line_len, line_top, brac_paint);
			}
		}
	}
	
	private class ChartView extends View{

		private Paint paint_pass = new Paint();
		private Paint paint_fail= new Paint();
		private Paint paint_none = new Paint();
		
		private Paint paint_highlight = new Paint();
		private Paint circle_paint_stroke = new Paint();
		private Paint button_paint = new Paint();
		private Paint button_paint_stroke = new Paint();
		private Paint text_paint_large = new Paint();
		private Paint text_paint_small = new Paint();
		private Paint text_paint_button = new Paint();
		private Paint focus_paint_len = new Paint();
		private Paint focus_paint_stroke = new Paint();
		private Paint line_paint = new Paint();
		private Paint axis_paint = new Paint();
		
		private Paint emotion_paint = new Paint();
		private Paint desire_paint = new Paint();
		private Paint brac_paint = new Paint();
		private Paint none_paint = new Paint();
		
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
			
			paint_pass.setColor(0xFFf39800);
			paint_fail.setColor(0xFF5bdfbf);
			paint_none.setColor(0xFFc9c9ca);
			
			paint_highlight .setColor(0xFFAADDFF);
			
			circle_paint_stroke.setColor(0xFFFF0000);
			circle_paint_stroke.setStyle(Style.STROKE);
			circle_paint_stroke.setStrokeWidth(screen.x * 7 / 1080);
			
			text_paint_large.setColor(0xFFFFFFFF);
			text_paint_large.setTextSize(screen.x * 64F/1080F);
			text_paint_large.setTextAlign(Align.LEFT);
			
			text_paint_button.setColor(0xFFFFFFFF);
			text_paint_button.setTextSize(screen.x * 64F/1080F);
			text_paint_button.setTextAlign(Align.CENTER);
			
			text_paint_small.setColor(0xFF000000);
			text_paint_small.setTextAlign(Align.CENTER);
			text_paint_small.setTextSize(screen.x * 48F/1080F);
			
			button_paint.setColor(0xFF009999);
			button_paint_stroke.setColor(0xFF000000);
			button_paint_stroke.setStyle(Style.STROKE);
			button_paint_stroke.setStrokeWidth(screen.x * 7 / 1080);
			
			focus_paint_len.setColor(0x88FFFFFF);
			focus_paint_stroke.setColor(0xFF000000);
			focus_paint_stroke.setStyle(Style.STROKE);
			focus_paint_stroke.setStrokeWidth(screen.x * 7 / 1080);
			
			axis_paint.setColor(0xFF000000);
			axis_paint.setStrokeWidth(screen.x * 7 / 1080);
			
			line_paint.setColor(0xFFFFFFFF);
			line_paint.setStrokeWidth(screen.x * 7 / 1080);
			
			emotion_paint.setColor(0xFFFF0000);
			emotion_paint.setStrokeWidth(screen.x * 8 / 1080);
			desire_paint.setColor(0xFF00FF00);
			desire_paint.setStrokeWidth(screen.x * 8 / 1080);
			brac_paint.setColor(0xFFFFFFFF);
			brac_paint.setStrokeWidth(screen.x * 8 / 1080);
			none_paint.setColor(0xFFFFFF00);
			none_paint.setStrokeWidth(screen.x * 8 / 1080);
			
			selected_centers = new ArrayList<Point>();
			circle_centers = new ArrayList<Point>();
			button_centers = new ArrayList<Point>();
			
			bar_width = screen.x * 30 / 1080;
		    bar_gap = screen.x * 7 / 1080;
		    chart_width = screen.x;
		    circle_radius = bar_width/2;
		    bar_bottom = screen.x*90/1080;
		    bar_left = screen.x*90/720;
		    
		    RADIUS = bar_width*9/5;
			RADIUS_SQUARE = RADIUS *RADIUS ;
			BUTTON_RADIUS = bar_width*3;
			BUTTON_RADIUS_SQUARE = BUTTON_RADIUS*BUTTON_RADIUS;
			BUTTON_GAPS = BUTTON_RADIUS * 5/2;
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
			canvas.drawColor(0xFF999999);
			
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
			
			
			drawBasis(canvas);
		}
		
		private void drawBasis(Canvas canvas){
			int _bottom = chartHeight - bar_bottom;
			//Draw Xaxis
			canvas.drawLine(3*bar_width, _bottom, chart_width, _bottom, axis_paint);
		}
		
		private void drawLineChart(Canvas canvas){
			int max_height = (chartHeight - bar_bottom)*5/10;
			int left = bar_left;
			int curPage = HistoryStorytelling.getPageNum(level);
			int small_radius = circle_radius/2;
			
			if (bars.size() == 0)
				return;
			
			Point prev_e_center = null;
			Point prev_d_center = null;
			Point prev_b_center = null;
			
			for (int i=0;i<bars.size();++i){
				BarInfo bar = bars.get(i);
				boolean inRange = false;
				if (curPage >= bar.from_page && curPage <= bar.to_page)
					inRange = true;
				
				float e_height,d_height,b_height;
				e_height = bar.emotion/5 * max_height;
				d_height = bar.desire/10 * max_height;
				b_height = bar.brac/0.5F * max_height;
				if (b_height > max_height)
					b_height = max_height;
				
				
				int right = left+bar_width;
				int _bottom = chartHeight - bar_bottom;
				int e_top = _bottom - (int)e_height;
				int d_top = _bottom - (int)d_height;
				int b_top = _bottom - (int)b_height;
				
				// draw highlights
				if (inRange)
					canvas.drawRect(left, _bottom-max_height - bar_width-circle_radius, right+bar_gap, _bottom, paint_highlight);
				
				//Draw bars & annotation_circles
				Point e_center = new Point(left+small_radius,e_top - bar_gap - small_radius);
				Point d_center = new Point(left+small_radius,d_top - bar_gap - small_radius);
				Point b_center = new Point(left+small_radius,b_top - bar_gap - small_radius);
				
				if (bar.hasData){
					if (prev_e_center!= null && prev_d_center!=null && prev_b_center!=null){
						canvas.drawCircle(e_center.x,e_center.y, small_radius, emotion_paint);
						canvas.drawCircle(d_center.x,d_center.y, small_radius, desire_paint);
						canvas.drawCircle(b_center.x,b_center.y, small_radius, brac_paint); 
						canvas.drawLine(prev_e_center.x, prev_e_center.y, e_center.x, e_center.y, emotion_paint);
						canvas.drawLine(prev_d_center.x, prev_d_center.y, d_center.x, d_center.y, desire_paint);
						canvas.drawLine(prev_b_center.x, prev_b_center.y, b_center.x, b_center.y, brac_paint);
					}
					prev_e_center = e_center;
					prev_d_center = d_center;
					prev_b_center = b_center;
				}
				else{
					canvas.drawCircle(e_center.x,e_center.y, small_radius*3/2, none_paint);
				}
				//Draw X axis Label
				if (i%7 == 0){
					String str= bar.dv.month+"/"+bar.dv.date;
					canvas.drawLine(left+circle_radius, _bottom, left+circle_radius, _bottom + circle_radius, axis_paint);
					canvas.drawText(str, left+small_radius, _bottom + bar_width*2, text_paint_small);
				}
				left += (bar_width+bar_gap);
			}
			
		}
		
		private void drawBarChart(Canvas canvas){
			int max_height = (chartHeight - bar_bottom)*5/10;
			int left = bar_left;
			int curPage = HistoryStorytelling.getPageNum(level);
			
			if (bars.size() == 0)
					return;

			for (int i=0;i<bars.size();++i){
				
				float height = 0;
				BarInfo bar = bars.get(i);
				
				boolean inRange = false;
				if (curPage >= bar.from_page && curPage <= bar.to_page)
					inRange = true;
				
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
				
				// draw highlights
				if (inRange)
					canvas.drawRect(left, _bottom-max_height - bar_width-circle_radius, right+bar_gap, _bottom, paint_highlight);
				
				//Draw bars & annotation_circles
				Point center = new Point(left+circle_radius,_top - bar_gap - circle_radius);
				
				if (!bar.hasData){
					canvas.drawRect(left, _top, right, _bottom, paint_none);
					canvas.drawCircle(center.x,center.y, circle_radius, paint_none);
				}
				else if (bar.brac > 0F){
					canvas.drawRect(left, _top, right, _bottom, paint_fail);
					canvas.drawCircle(center.x,center.y, circle_radius, paint_fail);
				}
				else{
					canvas.drawRect(left, _top, right, _bottom, paint_pass);
					canvas.drawCircle(center.x,center.y, circle_radius, paint_pass);
				}
				
				//draw circle for date with an audio record
				if (hasAudio.get(i))
					canvas.drawCircle(center.x,center.y, circle_radius, circle_paint_stroke);
				
				circle_centers.add(center);

				//Draw X axis Label
				if (i%7 == 0){
					String str= bar.dv.month+"/"+bar.dv.date;
					canvas.drawLine(left+circle_radius, _bottom, left+circle_radius, _bottom + circle_radius, axis_paint);
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
				canvas.drawCircle(curX, curY, RADIUS,focus_paint_stroke);
				
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
				
				int bound = BUTTON_RADIUS*3/2;
				int label_bound = scrollView.getScrollX() + labelBmp.getWidth() + bound;
				int b_center_x = curX - (selected_centers.size()/2)*BUTTON_GAPS ;
				if (b_center_x <  label_bound)
					b_center_x =  label_bound;
				int out_of_range = b_center_x + selected_centers.size()*BUTTON_GAPS -BUTTON_RADIUS - (scrollView.getScrollX() + screen.x);
				if (out_of_range > 0){
					b_center_x -= out_of_range;
				}
				
				int b_center_y = BUTTON_RADIUS + bar_gap;
				
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
					
					canvas.drawCircle(to.x, to.y, BUTTON_RADIUS, button_paint);
					DateValue d = selected_dates.get(i);
					canvas.drawCircle(to.x, to.y, BUTTON_RADIUS, button_paint_stroke);
					String str = d.month + "/" + d.date;
					canvas.drawText(str, to.x, to.y+bar_width, text_paint_button);
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
	
	private class BarInfo{
		public float emotion,desire,brac;
		public int from_page,to_page;
		public boolean hasData;
		public DateValue dv;
		
		BarInfo (float emotion, float desire, float brac, int from_page, int to_page,boolean hasData,DateValue dv){
			this.emotion = emotion;
			this.desire = desire;
			this.brac = brac;
			this.from_page = from_page;
			this.to_page = to_page;
			this.hasData = hasData;
			this.dv = dv;
		}
	}
	
}
