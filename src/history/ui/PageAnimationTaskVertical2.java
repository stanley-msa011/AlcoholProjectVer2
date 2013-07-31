package history.ui;

import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.fragments.HistoryFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;

public class PageAnimationTaskVertical2 extends AsyncTask<Void, Void, Void> {

	private PageWidgetVertical pageWidget;
	private PointF from,to;
	private float width_gap_1;
	private float height_gap_1;
	public static final int gaps = 25;
	private static final int clip_time = 400;
	private static final int sleep_time = clip_time/gaps;
	private int[] bgs;
	private HistoryFragment historyFragment;
	
	private int startImageIdx;
	
	private Bitmap cur=null,next=null,tmp=null;
	private Bitmap prev_cur=null,prev_next=null;
	private Point screen;
	private int type=-1; 
	private int y_axis;
	
	public PageAnimationTaskVertical2(PageWidgetVertical pageWidget, PointF from, PointF to, int[] bgs,HistoryFragment historyFragment,PointF endTouch,int startImageIdx,int endImageIdx, int type){
		this.pageWidget = pageWidget;
		this.from = from;
		this.to = to;
		this.startImageIdx = startImageIdx;
		
		this.historyFragment = historyFragment;
		screen = FragmentTabs.getSize();
		width_gap_1 = (to.x - from.x)/(float)gaps;
		height_gap_1 = (to.y - from.y)/(float)gaps;
		this.bgs = bgs;
		this.type = type;
		y_axis = screen.y - screen.x * 574/1080;
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		
		cur=null;next=null;tmp=null;
		prev_cur=null;prev_next=null;
		
		int curC = startImageIdx;
		
		Bitmap tmp2;
		
		if  (type == 1){// cur to next ()
			Log.d("PAGE_ANIMATION", "UP ANIMATION");
			tmp2 = BitmapFactory.decodeResource(pageWidget.getResources(), bgs[curC]);
			cur = Bitmap.createScaledBitmap(tmp2, screen.x, y_axis, true);
			tmp2.recycle();
			tmp2 = BitmapFactory.decodeResource(pageWidget.getResources(), bgs[curC+1]);
			next = Bitmap.createScaledBitmap(tmp2, screen.x, y_axis, true);
			tmp2.recycle();
			
			pageWidget.setBitmaps(cur, next);
			pageWidget.setTouchPosition(from);
			
			PointF touch = new PointF(from.x,from.y);
			
			for (int i=0;i<gaps;++i){
				touch.x += width_gap_1;
				touch.y += height_gap_1;
				try {
					Thread.sleep(sleep_time);
				} catch (InterruptedException e) {}
				pageWidget.setTouchPosition(touch);
			}
			try {
				Thread.sleep(sleep_time);
			} catch (InterruptedException e) {}
			
			historyFragment.resetPage(+1);
			
			
		}else{ //next to cur (DOWN)
			Log.d("PAGE_ANIMATION", "DOWN ANIMATION");
			
			tmp2 = BitmapFactory.decodeResource(pageWidget.getResources(), bgs[curC]);
			next = Bitmap.createScaledBitmap(tmp2, screen.x, y_axis, true);
			tmp2.recycle();
			//next = BitmapFactory.decodeResource(pageWidget.getResources(), bgs[curC]);
			tmp2 = BitmapFactory.decodeResource(pageWidget.getResources(), bgs[curC-1]);
			cur = Bitmap.createScaledBitmap(tmp2, screen.x, y_axis, true);
			tmp2.recycle();
			//cur = BitmapFactory.decodeResource(pageWidget.getResources(), bgs[curC-1]);
			
			pageWidget.setBitmaps(cur, next);
			pageWidget.setTouchPosition(to);
			
			PointF touch = new PointF(to.x,to.y);
			
			for (int i=0;i<gaps;++i){
				touch.x -= width_gap_1;
				touch.y -= height_gap_1;
				try {
					Thread.sleep(sleep_time);
				} catch (InterruptedException e) {}
				pageWidget.setTouchPosition(touch);
			}
			try {
				Thread.sleep(sleep_time);
			} catch (InterruptedException e) {}
			
			historyFragment.resetPage(-1);
		}
		
		return null;
	}
	@Override
	 protected void onPostExecute(Void result) {
		historyFragment.endAnimation(0);
    }
	@Override
	protected void onCancelled(){
		FragmentTabs.enableTab(true);
		if (cur!=null && !cur.isRecycled()){
			cur.recycle();
			cur = null;
		}
		
		if (next!=null && !next.isRecycled()){
			next.recycle();
			next = null;
		}
		if (tmp!=null && !tmp.isRecycled()){
			tmp.recycle();
			tmp = null;
		}
		if (prev_cur!=null && !prev_cur.isRecycled()){
			prev_cur.recycle();
			prev_cur = null;
		}
		if (prev_next!=null && !prev_next.isRecycled()){
			prev_next.recycle();
			prev_next = null;
		}
	}
	
}
