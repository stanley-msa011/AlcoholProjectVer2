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
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class HistoryFragment extends Fragment {

	private View  view;
	
	private ImageView bgView;
	private ImageView playButton;
	private TextView playText;
	private TextView uidText;
	private RelativeLayout buttonLayout;
	private RelativeLayout main_layout;
	private HistoryDB db;
	
	private PageWidget pageWidget;
	private PageAnimationTask pageAnimationTask;
	
	private int width, height,top_margin,bg_x,bg_y,playWidth,playHeight;
	private static final int[] bgs= {R.drawable.drunk_history_page1,R.drawable.drunk_history_page2,R.drawable.drunk_history_page3,R.drawable.drunk_history_page4,R.drawable.drunk_history_page5};
	private Bitmap background;
	private Bitmap cur_bg_bmp,next_bg_bmp;
	private Bitmap play_button_bmp;
	
	private PointF[] touchPoints;
	private PointF from,to;
	private HistoryFragment historyFragment;
	
	private int curPageIdx;
	private PointF curPageTouch;
	private LoadingHandler loadHandler;
	
	private boolean runAnimation;
	
	private ProgressDialog loadDialog;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	this.historyFragment = this;
    	view = inflater.inflate(R.layout.history_fragment, container,false);
    	db = new HistoryDB(this.getActivity());
    	runAnimation = false;
    	return view;
    }
   
	public void onResume(){
		super.onResume();
		System.gc();
		
		loadDialog = LoadingBox.loading(this.getActivity());
		loadDialog.show();
		if (loadHandler == null)
			loadHandler = new LoadingHandler();
		loadHandler.sendEmptyMessage(0);
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
    	if (cur_bg_bmp!=null && !cur_bg_bmp.isRecycled()){
    		cur_bg_bmp.recycle();
    		cur_bg_bmp=null;
    	}
    	if (next_bg_bmp!=null && !next_bg_bmp.isRecycled()){
    		next_bg_bmp.recycle();
    		next_bg_bmp=null;
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
    	
    	float gap_w = to.x - from.x;
    	float gap_h = to.y - from.y;
    	
    	touchPoints[0] = new PointF(from.x,from.y);
    	touchPoints[1] = new PointF(gap_w*0.2F+from.x,gap_h*0.2F+from.y);
    	touchPoints[2] = new PointF(gap_w*0.4F+from.x,gap_h*0.4F+from.y);
    	touchPoints[3] = new PointF(gap_w*0.6F+from.x,gap_h*0.6F+from.y);
    	
    	pageWidget= new PageWidget(main_layout.getContext(),width,height);
    	
    	GameHistory history = db.getLatestBracGameHistory();
    	int level = history.level;
    	
    	curPageIdx = level/4;
    	int curPageClip =level%4;
    	curPageTouch = touchPoints[curPageClip];
    }
    
    private void initView_step2(){
    	setPage();
    	
    	bgView.setImageBitmap(background);
    	
    	main_layout.addView(pageWidget);
    	LayoutParams param = (LayoutParams) pageWidget.getLayoutParams();
    	param.width = width;
    	param.height = height;
    	param.topMargin = top_margin;
    	param.leftMargin = 0;

    	buttonLayout = new RelativeLayout(main_layout.getContext());
    	main_layout.addView(buttonLayout);
    	LayoutParams playLayoutParam = (LayoutParams) buttonLayout.getLayoutParams();
    	playLayoutParam.width=playWidth;
    	playLayoutParam.height=playHeight;
    	playLayoutParam.leftMargin = (int) (bg_x*470.0/720.0);
    	playLayoutParam.topMargin = (int) (bg_y*940.0/1280.0);
    	
    	playButton = new ImageView(buttonLayout.getContext());
    	playButton.setImageBitmap(play_button_bmp );
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
    	
    }
    
    private class PageOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			if (!runAnimation){
				buttonLayout.setVisibility(View.INVISIBLE);
				runAnimation = true;
				pageAnimationTask = new PageAnimationTask(pageWidget,from,to,bgs,historyFragment,curPageTouch,curPageIdx);
				pageAnimationTask.execute();
			}
		}
    }
    
    
    public void endAnimation(){
    	runAnimation = false;
    	buttonLayout.setVisibility(View.VISIBLE);
    }
    
    public void setPage(){
    	pageWidget.setBitmaps(cur_bg_bmp, next_bg_bmp);
    	pageWidget.setTouchPosition(curPageTouch);
    	
    }
    
	@SuppressLint("HandlerLeak")
	private class LoadingHandler extends Handler{
		
		public void handleMessage(Message msg){
			initView_step1();
			
			background = BitmapFactory.decodeResource(historyFragment.getResources(), R.drawable.drunk_history_bg);
	    	LayoutParams bgParam = (LayoutParams) bgView.getLayoutParams();
	    	bgParam.width = bg_x;
	    	bgParam.height = bg_y;
	    	
			
			Bitmap tmp = BitmapFactory.decodeResource(historyFragment.getResources(), bgs[curPageIdx]);
			cur_bg_bmp = Bitmap.createScaledBitmap(tmp, width, height, true);
			tmp.recycle();
			if (curPageIdx == bgs.length-1)
				next_bg_bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			else{
				tmp = BitmapFactory.decodeResource(historyFragment.getResources(), bgs[curPageIdx+1]);
				next_bg_bmp = Bitmap.createScaledBitmap(tmp, width, height, true);
				tmp.recycle();
			}
	    	playWidth = (int) (bg_x*220.0/720.0);
	    	playHeight = (int) (bg_y*220.0/1280.0);
	    	if (playWidth > playHeight)
	    		playHeight = playWidth;
	    	else
	    		playWidth = playHeight;
	    	play_button_bmp =BitmapFactory.decodeResource(historyFragment.getResources(), R.drawable.drunk_history_play);
	    	
			initView_step2();
			endAnimation();
	    	
			loadDialog.dismiss();
		}
	}

}
