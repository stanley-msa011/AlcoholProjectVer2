package main.activities;

import database.HistoryDB;
import history.pageEffect.PageAnimationTaskVertical;
import history.pageEffect.PageWidgetVertical;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class HistoryFragment2 extends Fragment {

	private View  view;
	
	private ImageView prevImage;
	private RelativeLayout page_layout;
	private HistoryDB db;
	private PageWidgetVertical pageWidget;
	private PageAnimationTaskVertical pageAnimationTask;
	
	private AlphaAnimation prevAnimation;
	private AlphaAnimationEndHandler aaEndHandler;
	
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
	private PointF from,to,middle1,middle2,middle3;
	private HistoryFragment2 historyFragment;
	
	private int curPageIdx;
	private PointF curPageTouch;
	private LoadingHandler loadHandler;
	
	private int level;
	
	private boolean runAnimation;
	
	private LinearLayout stageLayout;
	private TextView stage,stageNum;
	
	private Typeface stageTypeface;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	Log.d("History","createView start");
    	this.historyFragment = this;
    	view = inflater.inflate(R.layout.history_fragment2, container,false);
    	db = new HistoryDB(this.getActivity());
    	runAnimation = false;
    	Log.d("History","createView end");
    	return view;
    }
   
	public void onResume(){
		super.onResume();
		
		level = db.getLatestBracGameHistory().level;
		
		LoadingBox.show(this.getActivity());
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
    	System.gc();
    }
    
    private void initView_step1(){
    	Log.d("History","step1 start");
    	Point screen = FragmentTabs.getSize();
    	page_layout = (RelativeLayout) view.findViewById(R.id.history_book_layout);
    	
    	bg_x = screen.x;
    	width = bg_x;
    	height = (int)(bg_x*365.0/355.0);
    	Log.d("PAGE",new Point(width,height).toString());
    	from = new PointF(width,height);
    	to = new PointF(width*0.8F,-height);
    	middle1 = new PointF(width*0.75F,height*0.7F);
    	middle2 = new PointF(width*0.7F,height*0.3F);
    	middle3 = new PointF(width*0.75F,-height*0.2F);
    	touchPoint = new PointF(from.x,from.y);
    	
    	pageWidget= new PageWidgetVertical(page_layout.getContext(),width,height);
    	
    	curPageIdx = level;
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
		tmp = BitmapFactory.decodeResource(historyFragment.getResources(), bgs[curPageIdx]);
		cur_bg_bmp = Bitmap.createScaledBitmap(tmp, width, height, true);
		tmp.recycle();
		next_bg_bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
			
		if (curPageIdx > 0){
			tmp = BitmapFactory.decodeResource(historyFragment.getResources(), bgs[curPageIdx-1]);
			prev_bg_bmp = Bitmap.createScaledBitmap(tmp, width, height, true);
			tmp.recycle();
		}	
		
    }
    
    private void initView_step3(){
    	Log.d("History","step3 start");
    	
    	LayoutParams sParam = (LayoutParams) stageLayout.getLayoutParams();
    	sParam.leftMargin = (int)(bg_x*530.0/720.0);
    	sParam.topMargin = (int)(bg_x*90.0/720.0);
    	
    	stageNum.setText(String.valueOf(curPageIdx));
    	
    	page_layout.addView(pageWidget);
    	LayoutParams param = (LayoutParams) pageWidget.getLayoutParams();
    	param.width = width;
    	param.height = height;

    	prevImage = new ImageView(page_layout.getContext());
    	page_layout.addView(prevImage);
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
    	pageWidget.setOnClickListener(new PageOnClickListener());
    }
    
    private class PageOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			if (level/3 == 0)
				return;
			if (!runAnimation){
				runAnimation = true;
				int[] aBgs = generateAnimationBgs();
				int pageIdx = getMaxPageNum();
				setStageVisible(false);
				pageAnimationTask = new PageAnimationTaskVertical(pageWidget,from,to,middle1,middle2,middle3,aBgs,historyFragment,curPageTouch,0,pageIdx);
				pageAnimationTask.execute();
				FragmentTabs.enableTab(false);
			}
		}
    }
    
    
    public void endAnimation(){
    	runAnimation = false;
    	setStageVisible(true);
    	FragmentTabs.enableTab(true);
    }
    
    public void setPage(){
    	Log.d("History","setPage start");
    	pageWidget.setBitmaps(cur_bg_bmp, next_bg_bmp);
    	pageWidget.setTouchPosition(curPageTouch);
    	Log.d("History","setPage end");
    	
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
		boolean isChange = isChangePage();
		if (isChange){
			runAnimation = true;
			int[] aBgs = generateAnimationBgs();
			int pageIdx = getMaxPageNum();
			int startIdx = pageIdx-1;
			if (startIdx <0)
				startIdx =0;
			prevImage.setVisibility(View.INVISIBLE);
			setStageVisible(false);
			pageAnimationTask = new PageAnimationTaskVertical(pageWidget,from,to,middle1,middle2,middle3,aBgs,historyFragment,curPageTouch,startIdx,pageIdx);
			pageAnimationTask.execute();
			FragmentTabs.enableTab(false);
			
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
	
}
