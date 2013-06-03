package main.activities;

import java.util.ArrayList;
import java.util.Calendar;

import database.HistoryDB;
import history.DateBracGameHistory;
import history.pageEffect.PageAnimationTaskVertical;
import history.pageEffect.PageAnimationTaskVertical2;
import history.pageEffect.PageWidgetVertical;
import history.ui.UIMsgBox;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
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
import android.view.View.OnTouchListener;
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
	private HistoryDB db;
	private PageWidgetVertical pageWidget;
	private PageAnimationTaskVertical pageAnimationTask;
	private PageAnimationTaskVertical2 pageAnimationTask2;
	
	private HorizontalScrollView scrollView;
	
	private AlphaAnimation prevAnimation;
	private AlphaAnimationEndHandler aaEndHandler;
	
	private Point screen;
	
	private int NUM_OF_BARS;
	
	private ArrayList<DateValue> selected_dates ;
	private ArrayList<DateValue> dates ;
	private ArrayList<DateBracGameHistory> historys; ;
	
	private ChartView chart;
	
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
	private static final int[] bgs= 
		{
		R.drawable.history_page01,R.drawable.history_page02,R.drawable.history_page03,
		R.drawable.history_page01,R.drawable.history_page02,R.drawable.history_page03,
		R.drawable.history_page01,R.drawable.history_page02,R.drawable.history_page03,
		R.drawable.history_page01,R.drawable.history_page02,R.drawable.history_page03,
		R.drawable.history_page01,R.drawable.history_page02,R.drawable.history_page03,
		R.drawable.history_page01,R.drawable.history_page02,R.drawable.history_page03,
		R.drawable.history_page01,R.drawable.history_page02,R.drawable.history_page03,
		R.drawable.history_page01,R.drawable.history_page02,R.drawable.history_page03,
		R.drawable.history_page01,R.drawable.history_page02,R.drawable.history_page03,
		R.drawable.history_page01,R.drawable.history_page02,R.drawable.history_page03
		};
	
	private static final int[] bgs_full= 
		{
		R.drawable.history_page03,
		R.drawable.history_page03,
		R.drawable.history_page03,
		R.drawable.history_page03,
		R.drawable.history_page03,
		R.drawable.history_page03,
		R.drawable.history_page03,
		R.drawable.history_page03,
		R.drawable.history_page03,
		R.drawable.history_page03
		 };
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
	
	private UIMsgBox msgBox;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	Log.d("History","createView start");
    	this.historyFragment = this;
    	view = inflater.inflate(R.layout.history_fragment2, container,false);
    	db = new HistoryDB(this.getActivity());
    	Log.d("History","createView end");
    	return view;
    }
   
	public void onResume(){
		super.onResume();
		isAnimation = false;
		screen = FragmentTabs.getSize();
		 level = db.getLatestBracGameHistory().level;
		 achieve_level = level;
		 Log.d("PAGE_ANIMATION", "ach: "+achieve_level);
		historys = new ArrayList<DateBracGameHistory>();
		selected_dates = new ArrayList<DateValue>();
		dates = new ArrayList<DateValue>();
		
		gListener = new GestureListener();
		gDetector = new GestureDetector(getActivity(), gListener);
		gtListener = new TouchListener();
		
		DateBracGameHistory[] h = db.getAllHistory();
		
		if (h != null){
			Log.d("CHART","H_COUNT = "+h.length);
			for (int i=0;i<h.length;++i){
				historys.add(h[i]);
			}
		}
		Log.d("CHART","H_COUNT2 = "+historys.size());
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
			Log.d("CHART","NOB= "+NUM_OF_BARS);
		}
		
		Log.d("CHART", "NUM_OF_BARS = "+NUM_OF_BARS);
		
		RelativeLayout r = (RelativeLayout) view;
		msgBox = new UIMsgBox(this,r);
		if (loadHandler == null)
			loadHandler = new LoadingHandler();
		loadHandler.sendEmptyMessage(0);
		Log.d("History","end resume");
	}
    
    public void onPause(){
    	clear();
    	super.onPause();
    }
	
    private void clear(){
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
    
    private void initView_step1(){
    	Log.d("History","step1 start");
    	Point screen = FragmentTabs.getSize();
    	pageLayout = (RelativeLayout) view.findViewById(R.id.history_book_layout);
    	chartLayout = (RelativeLayout) view.findViewById(R.id.history_content_layout);
    	scrollView = (HorizontalScrollView) view.findViewById(R.id.history_scroll_view);
    	
    	bg_x = screen.x;
    	width = bg_x;
    	height = (int)(bg_x*365.0/355.0);
    	Log.d("PAGE",new Point(width,height).toString());
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
    	stageNum.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(bg_x*100.0/720.0));
    	stageNum.setTypeface(stageTypeface);
    	Log.d("History","step1 end");
    }
    
    private void initView_step2(){
    	Log.d("History","step2 start");
    	
    	Bitmap tmp;
		tmp = BitmapFactory.decodeResource(historyFragment.getResources(), bgs[level]);
		cur_bg_bmp = Bitmap.createScaledBitmap(tmp, width, height, true);
		tmp.recycle();
		next_bg_bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
			
		if (level > 0){
			tmp = BitmapFactory.decodeResource(historyFragment.getResources(), bgs[level-1]);
			prev_bg_bmp = Bitmap.createScaledBitmap(tmp, width, height, true);
			tmp.recycle();
		}	
		
		tmp = BitmapFactory.decodeResource(getResources(), R.drawable.history_label);
		labelBmp = Bitmap.createScaledBitmap(tmp, screen.x * 374/1080, screen.x *86/1080 , true);
		tmp.recycle();
		
    }
    
    private void initView_step3(){
    	Log.d("History","step3 start");
    	
    	LayoutParams sParam = (LayoutParams) stageLayout.getLayoutParams();
    	sParam.leftMargin = bg_x*795/1080;
    	sParam.topMargin = bg_x*135/1080;
    	
    	stageNum.setText(String.valueOf(level));
    	
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
    	if (prev_bg_bmp!=null && !isChangePage()){
    		prevImage.setImageBitmap(prev_bg_bmp);
    		prevAnimation = new AlphaAnimation(1.0F,0.0F);
    		prevAnimation.setDuration(2000);
    		prevImage.setAnimation(prevAnimation);
    		aaEndHandler = new AlphaAnimationEndHandler(); 
    	}
    	
    	stage.setText("  等級  ");
    	
    	
    	LinearLayout.LayoutParams scrollParam = (LinearLayout.LayoutParams)scrollView.getLayoutParams();
    	scrollParam.width = screen.x;
    	scrollParam.height = screen.x * 1709/1080 - height;
    	
    	FrameLayout.LayoutParams clParam = (FrameLayout.LayoutParams)chartLayout.getLayoutParams();
    	clParam.width = screen.x;
    	clParam.height = screen.x * 1709/1080 - height;
    	
    	chartHeight = screen.x * 1709/1080 - height;
    	
    	chart = new ChartView(this.getActivity());
    	chartLayout.addView(chart);
    	RelativeLayout.LayoutParams chartParam = (RelativeLayout.LayoutParams) chart.getLayoutParams();
		chart_width =  bar_left + (bar_width + bar_gap)* NUM_OF_BARS + bar_left;
		
		if (chart_width < screen.x)
			chart_width = screen.x;
		chartParam.width= chart_width;
		chartParam.height =  screen.x * 1709/1080 - height;
		
		pageWidget.setOnTouchListener(gtListener);
    }
    
    
    public void endAnimation(){
    	setStageVisible(true);
    	FragmentTabs.enableTab(true);
    	isAnimation = false;
    }
    
    
    public void endAnimation(int tt){
    	Log.d("PAGE_ANIMATION", "END ANIMATION level:" + level);
		stageNum.setText(String.valueOf(level));
    	setStageVisible(true);
    	pageWidget.setOnTouchListener(gtListener);
    	FragmentTabs.enableTab(true);
    	Log.d("PAGE_ANIMATION", "END ANIMATION");
    	isAnimation = false;
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
    		level = level + 3;
    		Log.d("PAGE_ANIMATION", "reset level(+): "+level);
    		if (level > achieve_level)
    			level = achieve_level;
    	}else{
    		level = level - level%3 - 1;
    		Log.d("PAGE_ANIMATION", "reset level(-): "+level);
    		if (level < 0)
    			level = 0;
    	}
    	
    	Log.d("PAGE_ANIMATION", "reset level fix: "+level);
    	Bitmap tmp = BitmapFactory.decodeResource(historyFragment.getResources(), bgs[level]);
		cur_bg_bmp = Bitmap.createScaledBitmap(tmp, width, height, true);
		tmp.recycle();
    	pageWidget.setBitmaps(cur_bg_bmp, next_bg_bmp);
    	pageWidget.setTouchPosition(curPageTouch);
    	Log.d("History","resetPage end");
    }
    
	@SuppressLint("HandlerLeak")
	private class LoadingHandler extends Handler{
		
		public void handleMessage(Message msg){
			Log.d("History","start loadHandler");
			initView_step1();
			initView_step2();
			setPage();
			initView_step3();
			endAnimation();
			LoadingBox.dismiss();
			startAnim();
			Log.d("History","end loadHandler");
			
		}
	}

	private int[] generateAnimationBgs(){
    	int[] aBgs = new int[bgs_full.length];
    	for (int i=0;i<aBgs.length;++i){
    		aBgs[i] = bgs_full[i];
    	}
    	aBgs[level/3] = bgs[level];
    	
    	return aBgs;
	}
	
	private int getMaxPageNum(){
		return level/3;
	}

	private boolean isChangePage(){
		return level%3==0;
	}
	
	private void startAnim(){
		if (level == 0)
			return;
		isAnimation = true;
		boolean isChange = isChangePage();
		if (isChange){
			
			FragmentTabs.enableTab(false);
			int[] aBgs = generateAnimationBgs();
			int pageIdx = getMaxPageNum();
			int startIdx = pageIdx-1;
			if (startIdx <0)
				startIdx =0;
			prevImage.setVisibility(View.INVISIBLE);
			setStageVisible(false);
			pageAnimationTask = new PageAnimationTaskVertical(pageWidget,from,to,aBgs,historyFragment,curPageTouch,startIdx,pageIdx);
			pageAnimationTask.execute();
			
			
		}else{
			if (prevAnimation!=null){
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
				int[] aBgs = generateAnimationBgs();
				int pageIdx = getMaxPageNum();
				int startIdx = pageIdx;
				int endIdx = startIdx+1;
				Log.d("PAGE_ANIMATION", "UP"+startIdx+"/"+endIdx);
				if (endIdx > achieve_level /3){
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
				int[] aBgs = generateAnimationBgs();
				int pageIdx = getMaxPageNum();
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
	
	
	
	
	
	
	private class ChartView extends View{

		private Paint[] paints = new Paint[2];
		private Paint annotation_paint = new Paint();
		private Paint annotation_paint_none = new Paint();
		private Paint button_paint = new Paint();
		private Paint button_paint2 = new Paint();
		private Paint text_paint = new Paint();
		private Paint text_paint2 = new Paint();
		private Paint shader_paint = new Paint();
		private Paint line_paint = new Paint();
		
	    private int RADIUS;
	    private int RADIUS_SQUARE;
	    private int BUTTON_RADIUS;
	    private int BUTTON_RADIUS_SQUARE;
	    private int BUTTON_GAPS;
	    
	    private int curX = -1,curY = -1;
	    
	    private ArrayList<Point>circle_centers;
	    private ArrayList<Point> selected_centers;
	    private ArrayList<Point> button_centers;
	    
	    private int chart_type = 0;
	    
		public ChartView(Context context) {
			super(context);
			for (int i=0;i<2;++i)
				paints[i] = new Paint();
			paints[0].setColor(0xFFf39800);
			paints[1].setColor(0xFFc9c9ca);
			
			line_paint.setColor(0xFF000000);
			line_paint.setStrokeWidth(screen.x * 7 / 1080);
			
			annotation_paint.setColor(0xFFf39800);
			annotation_paint_none.setColor(0xFFc9c9ca);
			
			text_paint.setColor(0xFFFFFFFF);
			text_paint.setTextSize(screen.x * 64F/1080F);
			text_paint.setTextAlign(Align.LEFT);
			
			text_paint2.setColor(0xFF000000);
			text_paint2.setTextAlign(Align.CENTER);
			float textSize = screen.x * 48F/1080F ;
			text_paint2.setTextSize(textSize);
			
			button_paint2.setColor(0xFFF39800);
			
			shader_paint.setColor(0x88FFFFFF);
			
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
	    	int action = event.getAction();
	    	if (action == MotionEvent.ACTION_DOWN){
	    		int x= (int)event.getX();
	    		int y = (int) event.getY();
	    		if (x < labelBmp.getWidth() && y < labelBmp.getHeight()){
	    			chart_type = (chart_type+1)%3;
	    			invalidate();
	    			return true;
	    		}
	    		
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
	    		
	    		if (onButton){
	    			msgBox.showMsgBox(selected_dates.get(buttonNum).toString());
	    		}
	    		else{
	    			curX = (int) event.getX();  
	    			curY = (int) event.getY();
	    		}
	    		invalidate();  
	    	}
	        return true;
	    }  
		
		@SuppressLint("DrawAllocation")
		@Override
		protected void onDraw(Canvas canvas){
			super.onDraw(canvas);
			canvas.drawColor(0xFF999999);
			
			drawChart(canvas);
			
		}
		
		static final long DAY_SEC = 60*60*24;
		
		private void drawChart(Canvas canvas){
			int max_height = (chartHeight - bar_bottom)*5/10;
			int left = bar_left;

			canvas.drawBitmap(labelBmp, 0, 0, null);
			
			if (chart_type == 0)
				canvas.drawText("心情量尺", screen.x * 20F/1080F, screen.x * 70F/1080F,text_paint);
			else if (chart_type == 1)
				canvas.drawText("渴癮指數", screen.x * 20F/1080F, screen.x * 70F/1080F,text_paint);
			else if (chart_type == 2)
				canvas.drawText("酒測結果", screen.x * 20F/1080F, screen.x * 70F/1080F,text_paint);
				
			if (NUM_OF_BARS == 0)
					return;

			long from_t = from_cal.getTimeInMillis()/1000;
			
			Calendar ccal = Calendar.getInstance();
			ccal.setTimeInMillis(from_cal.getTimeInMillis());
			
			circle_centers.clear();
			dates.clear();
			int pos = 0;
			for (int i=0;i<NUM_OF_BARS;++i){
				float height = 0;
				
				int count = 0;
				float e_sum = 0;
				float d_sum = 0;
				float b_sum = 0;
				
				for (int j=pos; j<historys.size();++j){
					DateBracGameHistory h = historys.get(j);
					if (h.timestamp >= from_t && h.timestamp < from_t + DAY_SEC){
						e_sum += h.emotion;
						d_sum += h.desire;
						b_sum += h.brac;
						++count;
					}else if(h.timestamp >= from_t + DAY_SEC){
						pos = j;
						break;
					}
				}
				
				boolean noCount = false;
				if (count == 0){
					height = 0;
					noCount = true;
				}
				else{
					if (chart_type == 0)
						height = (e_sum/count) /5 * max_height;
					else if (chart_type == 1)
						height = (d_sum/count) /10 * max_height;
					else if (chart_type == 2){
						height = (b_sum/count) /0.5F * max_height;
						if (height > max_height)
							height = max_height;
					}
				}
				
				int right = left+bar_width;
				int _bottom = chartHeight - bar_bottom;
				int _top = _bottom - (int)height;
				canvas.drawRect(left, _top, right, _bottom, paints[0]);
				
				Point center = new Point(left+circle_radius,_top - bar_gap - circle_radius);
				if (noCount)
					canvas.drawCircle(center.x,center.y, circle_radius, annotation_paint_none);
				else
					canvas.drawCircle(center.x,center.y, circle_radius, annotation_paint);
				
				circle_centers.add(center);
				int mYear = ccal.get(Calendar.YEAR);
				int mMonth = ccal.get(Calendar.MONTH)+1;
				int mDate = ccal.get(Calendar.DAY_OF_MONTH);
				//Log.d("CHART", "ccal: "+ccal.toString());
				dates.add(new DateValue(mYear,mMonth,mDate));
				
				canvas.drawLine(3*bar_width, _bottom, chart_width, _bottom, line_paint);
				
				canvas.drawLine(3*bar_width, _bottom, 3*bar_width, _bottom - max_height - bar_width, line_paint);
				canvas.drawText("0", bar_width, _bottom, text_paint2);
				String maxLabel = "MAX";
				if (chart_type == 0)
					maxLabel = "5";
				else if (chart_type == 1)
					maxLabel = "10";
				else if (chart_type == 2)
					maxLabel = "0.5";
				
				canvas.drawText(maxLabel, bar_width, _bottom - max_height, text_paint2);
				
				if (i%7 == 0){
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(from_t*1000);
					int m = cal.get(Calendar.MONTH)+1;
					int d = cal.get(Calendar.DAY_OF_MONTH);
					String str= m+"/"+d;
					canvas.drawText(str, left+circle_radius, _bottom + bar_width*2, text_paint2);
				}
				
				left += (bar_width+bar_gap);
				
				from_t+=DAY_SEC;
				ccal.add(Calendar.DATE, 1);
			}
			

			selected_centers.clear();
			button_centers.clear();
			selected_dates.clear();
			
			if (curX>0 && curY > 0){
				
				canvas.drawCircle(curX, curY, RADIUS, shader_paint);
				
				for (int i=0;i<circle_centers.size();++i){
					Point c = circle_centers.get(i);
					DateValue d = dates.get(i);
					int distance_square = (curX-c.x)* (curX-c.x)+(curY-c.y)* (curY-c.y);
					if (distance_square < RADIUS_SQUARE){
						selected_centers.add(c);
						selected_dates.add(d);
					}
				}
				
				int bound = BUTTON_RADIUS*3/2;
				int label_bound = labelBmp.getWidth() + bound;
				int leftBound = scrollView.getScrollX() + bound;
				int b_center_x = curX - (selected_centers.size()/2)*BUTTON_GAPS ;
				if (b_center_x <  leftBound)
					b_center_x =  leftBound;
				int out_of_range = b_center_x + selected_centers.size()*BUTTON_GAPS -BUTTON_RADIUS - (scrollView.getScrollX() + screen.x);
				if (out_of_range > 0){
					b_center_x -= out_of_range;
				}
				if (b_center_x < label_bound)
					b_center_x = label_bound;
				
				int b_center_y = BUTTON_RADIUS + bar_gap;
				
				int b_center_x_bak = b_center_x;
				
				for (int i=0;i<selected_centers.size();++i){
					Point from = selected_centers.get(i);
					Point to = new Point(b_center_x,b_center_y);
					button_centers.add(to);
					canvas.drawLine(from.x, from.y, to.x,to.y, text_paint);
					b_center_x+=BUTTON_GAPS;
				}
				b_center_x = b_center_x_bak;
				for (int i=0;i<selected_centers.size();++i){
					Point to = new Point(b_center_x,b_center_y);
					
					canvas.drawCircle(to.x, to.y, BUTTON_RADIUS, button_paint2);
					DateValue d = selected_dates.get(i);
					String str = d.month + "/" + d.date;
					canvas.drawText(str, to.x - BUTTON_RADIUS+bar_gap, to.y+bar_width, text_paint);
					b_center_x+=BUTTON_GAPS;
				}
			}
		}
	}


	private class DateValue{
		public int year,month,date;
		
		public DateValue(long ts){
			ts = ts * 1000L;
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(ts);
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH)+1;
			date = cal.get(Calendar.DAY_OF_MONTH);
		}
		public DateValue(int year, int month, int date){
			this.year = year;
			this.month = month;
			this.date = date;;
		}
		
		public String toString(){
			return year+"/"+month+"/"+date;
		}
	}

}
