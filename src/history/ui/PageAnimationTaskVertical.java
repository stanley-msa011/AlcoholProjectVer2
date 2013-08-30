package history.ui;

import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.fragments.HistoryFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;

public class PageAnimationTaskVertical extends AsyncTask<Void, Void, Void> {

	private PageWidgetVertical pageWidget;
	private PointF from;
	private float width_gap_1;
	private float height_gap_1;
	public static final int gaps = 25;
	private static final int clip_time = 400;
	private static final int sleep_time = clip_time/gaps;
	private int[] bgs;
	private HistoryFragment historyFragment;
	
	private PointF endTouch;
	private int startImageIdx;
	private int endImageIdx;
	
	private Bitmap cur=null,next=null,tmp=null;
	private Bitmap prev_cur=null,prev_next=null;
	
	private int end_type=-1; 
	private Point screen;
	private int y_axis;
	
	
	public PageAnimationTaskVertical(PageWidgetVertical pageWidget, PointF from, PointF to,int[] bgs,HistoryFragment historyFragment,PointF endTouch,int startImageIdx,int endImageIdx){
		this.pageWidget = pageWidget;
		this.from = from;
		this.endTouch = endTouch;
		this.startImageIdx = startImageIdx;
		this.endImageIdx = endImageIdx;
		this.historyFragment = historyFragment;
		screen = FragmentTabs.getSize();
		width_gap_1 = (to.x - from.x)/(float)gaps;
		height_gap_1 = (to.y - from.y)/(float)gaps;
		this.bgs = bgs;
		y_axis = screen.y - screen.x * 574/1080;
	}
	
	public PageAnimationTaskVertical(PageWidgetVertical pageWidget, PointF from, PointF to,int[] bgs,HistoryFragment historyFragment,PointF endTouch,int startImageIdx,int endImageIdx,int type){
		this.pageWidget = pageWidget;
		this.from = from;
		this.endTouch = endTouch;
		this.startImageIdx = startImageIdx;
		this.endImageIdx = endImageIdx;
		this.historyFragment = historyFragment;
		screen = FragmentTabs.getSize();
		width_gap_1 = (to.x - from.x)/(float)gaps;
		height_gap_1 = (to.y - from.y)/(float)gaps;
		this.bgs = bgs;	
		end_type = type;
		if (FragmentTabs.isWideScreen())
			y_axis = screen.x*1137/1080;
		else
			y_axis = screen.x * 993/1080;
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		Log.d("PAGE_ANIMATION","START ANIMATION");
		
		cur=null;next=null;tmp=null;
		prev_cur=null;prev_next=null;
		
		Bitmap tmp2;
		
		for (int c=startImageIdx;c<bgs.length-1;++c){
			if (c > endImageIdx)
				break;
			
			if (prev_next!=null){
				cur = prev_next;
			}
			else{
				tmp2 = BitmapFactory.decodeResource(pageWidget.getResources(), bgs[c]);
				cur = Bitmap.createScaledBitmap(tmp2, screen.x, y_axis, true);
				tmp2.recycle();
			}
			tmp2 = BitmapFactory.decodeResource(pageWidget.getResources(), bgs[c+1]);
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
				if (touch.x+touch.y < endTouch.x + endTouch.y && c == endImageIdx)
					break;
				pageWidget.setTouchPosition(touch);
			}
			try {
				Thread.sleep(sleep_time);
			} catch (InterruptedException e) {}

			if (prev_cur!=null&&!prev_cur.isRecycled()){
				prev_cur.recycle();
				prev_cur = null;
			}
			
			prev_cur = cur;
			prev_next= next;
		}
		if (prev_next!=null&&!prev_next.isRecycled()){
			prev_next.recycle();
			prev_next = null;
		}
		try {
			Thread.sleep(sleep_time);
		} catch (InterruptedException e) {}
		return null;
	}
	@Override
	 protected void onPostExecute(Void result) {
		historyFragment.resetPage(0);
		if (end_type == -1)
			historyFragment.endAnimation();
		else
			historyFragment.endAnimation(end_type);
    }
	@Override
	protected void onCancelled(){
		FragmentTabs.enableTabAndClick(true);
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
