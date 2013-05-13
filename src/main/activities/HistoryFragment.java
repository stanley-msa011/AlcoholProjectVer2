package main.activities;

import database.HistoryDB;
import history.GameHistory;
import history.pageEffect.PageAnimationTask;
import history.pageEffect.PageWidget;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class HistoryFragment extends Fragment {

	private View  view;
	
	private ImageView bgView;
	private ImageView playButton;
	private ImageView prevImage;
	private TextView playText;
	private TextView uidText;
	private RelativeLayout buttonLayout;
	private RelativeLayout main_layout;
	private HistoryDB db;
	
	private PageWidget pageWidget;
	private PageAnimationTask pageAnimationTask;
	
	private AlphaAnimation prevAnimation;
	private AlphaAnimationEndHandler aaEndHandler;
	
	private int width, height,top_margin,bg_x,bg_y,playWidth,playHeight;
	private static final int[] bgs= 
		{
		R.drawable.drunk_history_page1_0,R.drawable.drunk_history_page1_1,R.drawable.drunk_history_page1_2,R.drawable.drunk_history_page1_3,R.drawable.drunk_history_page1_4,R.drawable.drunk_history_page1_5,
		R.drawable.drunk_history_page2_0,R.drawable.drunk_history_page2_1,R.drawable.drunk_history_page2_2,R.drawable.drunk_history_page2_3,R.drawable.drunk_history_page2_4,R.drawable.drunk_history_page2_5,
		R.drawable.drunk_history_page3_0,R.drawable.drunk_history_page3_1,R.drawable.drunk_history_page3_2,R.drawable.drunk_history_page3_3,R.drawable.drunk_history_page3_4,R.drawable.drunk_history_page3_5,
		R.drawable.drunk_history_page4_0,R.drawable.drunk_history_page4_1,R.drawable.drunk_history_page4_2,R.drawable.drunk_history_page4_3,R.drawable.drunk_history_page4_4,R.drawable.drunk_history_page4_5,
		R.drawable.drunk_history_page5_0,R.drawable.drunk_history_page5_1,R.drawable.drunk_history_page5_2,R.drawable.drunk_history_page5_3,R.drawable.drunk_history_page5_4,R.drawable.drunk_history_page5_5
		};
	
	private static final int[] bgs_full= 
		{
		R.drawable.drunk_history_page1_5,
		R.drawable.drunk_history_page2_5,
		R.drawable.drunk_history_page3_5,
		R.drawable.drunk_history_page4_5,
		R.drawable.drunk_history_page5_5,
		 };
	private Bitmap background;
	private Bitmap cur_bg_bmp,next_bg_bmp;
	private Bitmap play_button_bmp;
	private Bitmap prev_bg_bmp;
	
	
	private PointF[] touchPoints;
	private PointF from,to;
	private HistoryFragment historyFragment;
	
	private int curPageIdx;
	private PointF curPageTouch;
	private LoadingHandler loadHandler;
	
	private int level;
	
	private boolean runAnimation;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	Log.d("History","createView start");
    	this.historyFragment = this;
    	view = inflater.inflate(R.layout.new_history_fragment, container,false);
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
    	if (background!=null && !background.isRecycled()){
    		background.recycle();
    		background = null;
    	}
    	if (play_button_bmp !=null && !play_button_bmp.isRecycled()){
    		play_button_bmp.recycle();
    		play_button_bmp = null;
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
    	
    	main_layout = (RelativeLayout) view.findViewById(R.id.history_main_layout);
    	
    	bg_x = screen.x;
    	bg_y = screen.y;
    	
    	
    	top_margin = (int) (bg_y*224.0/1280.0);
    	
    	bgView = (ImageView) view.findViewById(R.id.history_background);
    	bgView.setScaleType(ScaleType.FIT_XY);
    	
    	
    	width = (int)(bg_x*630.0/720.0);
    	height = (int)(bg_y*856.0/1280.0);
    	Log.d("PAGE",new Point(width,height).toString());
    	from = new PointF(width,height);
    	to = new PointF(-width*1.1F,height*0.95F);
    	touchPoints = new PointF[4];
    	
    	touchPoints[0] = new PointF(from.x,from.y);
    	
    	pageWidget= new PageWidget(main_layout.getContext(),width,height);
    	
    	curPageIdx = level;
    	curPageTouch = touchPoints[0];
    	Log.d("History","step1 end");
    }
    
    private void initView_step2(){
    	Log.d("History","step2 start");
    	
    	Bitmap tmp;
    	tmp = BitmapFactory.decodeResource(historyFragment.getResources(), R.drawable.drunk_history_bg);
    	background = Bitmap.createScaledBitmap(tmp, bg_x, bg_y, true);
    	tmp.recycle();
    	LayoutParams bgParam = (LayoutParams) bgView.getLayoutParams();
    	bgParam.width = bg_x;
    	bgParam.height = bg_y;
    	
		
		tmp = BitmapFactory.decodeResource(historyFragment.getResources(), bgs[curPageIdx]);
		cur_bg_bmp = Bitmap.createScaledBitmap(tmp, width, height, true);
		tmp.recycle();
		//if (curPageIdx == bgs.length-1)
		next_bg_bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
		//else{
		//	tmp = BitmapFactory.decodeResource(historyFragment.getResources(), bgs[curPageIdx+1]);
		//	next_bg_bmp = Bitmap.createScaledBitmap(tmp, width, height, true);
		//	tmp.recycle();
		//}
		if (curPageIdx > 0){
			tmp = BitmapFactory.decodeResource(historyFragment.getResources(), bgs[curPageIdx-1]);
			prev_bg_bmp = Bitmap.createScaledBitmap(tmp, width, height, true);
			tmp.recycle();
		}	
		
		
    	playWidth = (int) (bg_x*220.0/720.0);
    	playHeight = (int) (bg_y*220.0/1280.0);
    	if (playWidth > playHeight)
    		playHeight = playWidth;
    	else
    		playWidth = playHeight;
    	tmp = BitmapFactory.decodeResource(historyFragment.getResources(), R.drawable.drunk_history_play);
    	play_button_bmp = Bitmap.createScaledBitmap(tmp, playWidth, playHeight, true);
    	tmp.recycle();
    	Log.d("History","step2 end");
    }
    
    private void initView_step3(){
    	Log.d("History","step3 start");
    	if (background!=null&&!background.isRecycled())
    		bgView.setImageBitmap(background);
    	
    	main_layout.addView(pageWidget);
    	LayoutParams param = (LayoutParams) pageWidget.getLayoutParams();
    	param.width = width;
    	param.height = height;
    	param.topMargin = top_margin;
    	param.leftMargin = 0;

    	prevImage = new ImageView(main_layout.getContext());
    	main_layout.addView(prevImage);
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
    	buttonLayout = new RelativeLayout(main_layout.getContext());
    	main_layout.addView(buttonLayout);
    	LayoutParams playLayoutParam = (LayoutParams) buttonLayout.getLayoutParams();
    	playLayoutParam.width=playWidth;
    	playLayoutParam.height=playHeight;
    	playLayoutParam.leftMargin = (int) (bg_x*470.0/720.0);
    	playLayoutParam.topMargin = (int) (bg_y*940.0/1280.0);
    	
    	playButton = new ImageView(buttonLayout.getContext());
    	
    	//Bugs happened here
    	if (play_button_bmp==null)
    		Log.d("History","null button");
    	else if (play_button_bmp.isRecycled())
    		Log.d("History","recycled button");
    	else{
    		Log.d("History","exist button");
    	}
    	playButton.setImageBitmap(play_button_bmp);
    	playButton.setScaleType(ScaleType.FIT_XY);
    	playButton.setOnClickListener(new PageOnClickListener());
    	buttonLayout.addView(playButton);
    	
    	
    	LayoutParams playBgParam = (LayoutParams) playButton.getLayoutParams();
    	playBgParam.width=LayoutParams.MATCH_PARENT;
    	playBgParam.height=LayoutParams.MATCH_PARENT;
    	playBgParam.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
    	
    	playText = new TextView(main_layout.getContext());
    	if (Lang.eng)
    		playText.setText("Play ");
    	else
    		playText.setText("播放 ");
    	playText.setTextSize(TypedValue.COMPLEX_UNIT_PX,playWidth*0.25F);
    	playText.setGravity(Gravity.CENTER);
    	playText.setTextColor(0xFFF97306);
    	Typeface face=Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/helvetica-lt-std-bold.otf");
    	playText.setTypeface(face);
    	buttonLayout.addView(playText);
    	LayoutParams playTextParam = (LayoutParams) playText.getLayoutParams();
    	playTextParam.width = LayoutParams.WRAP_CONTENT;
    	playTextParam.height = LayoutParams.WRAP_CONTENT;
    	playTextParam.alignWithParent=true;
    	playTextParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
    	playTextParam.topMargin = (int)(playWidth*0.325F);
    	
    	uidText = new TextView(main_layout.getContext());
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(main_layout.getContext());
    	String uid = settings.getString("uid", "");
    	uidText.setText(uid);
    	Typeface face2=Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/helvetica-lt-std-bold.otf");
    	uidText.setTypeface(face2);
    	uidText.setTextColor(0xFFF97306);
    	uidText.setTextSize(TypedValue.COMPLEX_UNIT_PX,playText.getTextSize()*1.2F);
    	uidText.setGravity(Gravity.CENTER);
    	main_layout.addView(uidText);
    	LayoutParams uidTextParam = (LayoutParams) uidText.getLayoutParams();
    	uidTextParam.leftMargin = (int) (bg_x*515.0/720.0);
    	uidTextParam.topMargin = (int) (bg_y*148.0/1280.0);
    	Log.d("History","step3 end");
    }
    
    private class PageOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			if (!runAnimation){
				buttonLayout.setVisibility(View.INVISIBLE);
				runAnimation = true;
				int[] aBgs = generateAnimationBgs();
				int pageIdx = getMaxPageNum();
				pageAnimationTask = new PageAnimationTask(pageWidget,from,to,aBgs,historyFragment,curPageTouch,0,pageIdx);
				pageAnimationTask.execute();
				FragmentTabs.enableTab(false);
			}
		}
    }
    
    
    public void endAnimation(){
    	runAnimation = false;
    	buttonLayout.setVisibility(View.VISIBLE);
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
    	if (level < 6)
    		aBgs[0] = bgs[level];
    	else if (level < 12)
    		aBgs[1] = bgs[level];
    	else if (level < 18)
    		aBgs[2] = bgs[level];
    	else if (level < 24)
    		aBgs[3] = bgs[level];
    	else if (level < 30)
    		aBgs[4] = bgs[level];
    	
    	return aBgs;
	}
	
	private int getMaxPageNum(){
    	if (level < 6)
    		return 0;
    	else if (level < 12)
    		return 1;
    	else if (level < 18)
    		return 2;
    	else if (level < 24)
    		return 3;
    	return 4;
	}

	private boolean isChangePage(){
    	if (level == 0)
    		return true;
    	if (level == 6)
    		return true;
    	if (level == 12)
    		return true;
    	if (level == 18 )
    		return true;
    	if (level == 24)
    		return true;
    	return false;
	}
	
	private void startAnim(){
		
		boolean isChange = isChangePage();
		if (isChange){
			buttonLayout.setVisibility(View.INVISIBLE);
			runAnimation = true;
			int[] aBgs = generateAnimationBgs();
			int pageIdx = getMaxPageNum();
			int startIdx = pageIdx-1;
			if (startIdx <0)
				startIdx =0;
			prevImage.setVisibility(View.INVISIBLE);
			pageAnimationTask = new PageAnimationTask(pageWidget,from,to,aBgs,historyFragment,curPageTouch,startIdx,pageIdx);
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
	
}
