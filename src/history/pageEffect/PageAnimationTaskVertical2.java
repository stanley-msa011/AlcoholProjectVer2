package history.pageEffect;

import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.activities.HistoryFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;

public class PageAnimationTaskVertical2 extends AsyncTask<Void, Void, Void> {

	private PageWidgetVertical pageWidget;
	private PointF from,to;
	private float width_gap_1;
	private float height_gap_1;
	public static final int gaps = 60;
	private static final int clip_time = 300;
	private static final int sleep_time = clip_time/gaps;
	private int[] bgs;
	private HistoryFragment historyFragment;
	
	private int startImageIdx;
	
	private Bitmap cur=null,next=null,tmp=null;
	private Bitmap prev_cur=null,prev_next=null;
	
	private int type=-1; 
	
	public PageAnimationTaskVertical2(PageWidgetVertical pageWidget, PointF from, PointF to, int[] bgs,HistoryFragment historyFragment,PointF endTouch,int startImageIdx,int endImageIdx, int type){
		this.pageWidget = pageWidget;
		this.from = from;
		this.to = to;
		this.startImageIdx = startImageIdx;
		
		this.historyFragment = historyFragment;
		
		width_gap_1 = (to.x - from.x)/(float)gaps;
		height_gap_1 = (to.y - from.y)/(float)gaps;
		this.bgs = bgs;
		this.type = type;
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		Log.d("PageAnimation","Start animation");
		
		cur=null;next=null;tmp=null;
		prev_cur=null;prev_next=null;
		
		//for (int c=startImageIdx;c<bgs.length-1;++c){
		
		int curC = startImageIdx;
		
		if  (type == 1){// cur to next ()
			Log.d("PAGE_ANIMATION", "in UP");
			
			cur = BitmapFactory.decodeResource(pageWidget.getResources(), bgs[curC]);
			
			next = BitmapFactory.decodeResource(pageWidget.getResources(), bgs[curC+1]);
			
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
			Log.d("PAGE_ANIMATION", "in DOWN");
			
			next = BitmapFactory.decodeResource(pageWidget.getResources(), bgs[curC]);
			
			cur = BitmapFactory.decodeResource(pageWidget.getResources(), bgs[curC-1]);
			
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
