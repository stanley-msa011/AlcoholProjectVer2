package ioio.examples.hello;

import history.GameHistory;
import history.pageEffect.PageAnimationHandler;
import history.pageEffect.PageAnimationTask;
import history.pageEffect.PageWidget;
import new_database.HistoryDB;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class HistoryFragment extends Fragment {

	private View  view;
	
	private ImageView bgView;
	private ImageView playButton;
	private TextView playText;
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
	private LoadingTask loadTask;
	
	private boolean runAnimation;
	
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
		initView_step1();
		loadTask = new LoadingTask();
		loadTask.execute();
	}
    
    public void onPause(){
    	if (loadTask!=null)
    		loadTask.cancel(true);
    	if (pageAnimationTask!=null)
    		pageAnimationTask.cancel(true);
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
    	main_layout.removeAllViews();
    	main_layout.removeAllViewsInLayout();
    	pageWidget.destroyDrawingCache();
    	pageWidget.clear();
    	pageWidget=null;
    	System.gc();
    	super.onPause();
    }
	
    private void initView_step1(){
    	
    	
    	Point screen = FragmentTabs.getSize();
    	
    	main_layout = (RelativeLayout) view.findViewById(R.id.history_main_layout);
    	
    	bg_x = screen.x;
    	bg_y = screen.y;
    	
    	bgView = (ImageView) view.findViewById(R.id.history_bg);
    	LayoutParams bgParam = (LayoutParams) bgView.getLayoutParams();
    	bgParam.width = bg_x;
    	bgParam.height = bg_y;
    	
    	top_margin = (int) (bg_y*224.0/1280.0);
    	
    	Bitmap tmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.drunk_history_bg);
    	background = Bitmap.createScaledBitmap(tmp, bg_x,bg_y , true);
    	bgView.setImageBitmap(background);
    	tmp.recycle();
    	
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
    
    
    private class LoadingTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
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
			tmp = BitmapFactory.decodeResource(historyFragment.getResources(), R.drawable.drunk_history_play);
	    	playWidth = (int) (bg_x*220.0/720.0);
	    	playHeight = (int) (bg_y*220.0/1280.0);
	    	if (playWidth > playHeight)
	    		playHeight = playWidth;
	    	else
	    		playWidth = playHeight;
	    	play_button_bmp =Bitmap.createScaledBitmap(tmp, playWidth, playHeight, true);
	    	
			return null;
		}
		@Override
		 protected void onPostExecute(Void result) {
			initView_step2();
			endAnimation();
		}
    }

}
