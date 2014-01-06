package history.ui;

import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.ui.ScreenSize;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;

public class PageAnimationTaskVerticalFling extends AsyncTask<Void, Void, Void> {

	private PageWidgetVertical pageWidget;
	private PointF from,to;
	private float width_gap;
	private float height_gap;
	public static final int gaps = 25;
	private static final int clip_time = 400;
	private static final int sleep_time = clip_time/gaps;
	private int[] bgs;
	private PageAnimationCaller endPageAnimation;
	
	private int startImageIdx;
	
	private Bitmap cur=null,next=null,tmp=null;
	private Point screen;
	private int type=-1; 
	private int y_axis;
	
	public PageAnimationTaskVerticalFling(PageWidgetVertical pageWidget, PointF from, PointF to, int[] bgs,PageAnimationCaller endPageAnimation,int startImageIdx, int type){
		this.pageWidget = pageWidget;
		this.from = from;
		this.to = to;
		this.startImageIdx = startImageIdx;
		
		this.endPageAnimation = endPageAnimation;
		screen = ScreenSize.getScreenSize(pageWidget.getContext());
		width_gap = (to.x - from.x)/(float)gaps;
		height_gap = (to.y - from.y)/(float)gaps;
		this.bgs = bgs;
		this.type = type;
		y_axis = screen.y - screen.x * 574/1080;
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		
		cur=null;next=null;tmp=null;
		
		int curC = startImageIdx;
		
		if  (type == 1){// cur to next ()
			Log.d("PAGE_ANIMATION", "UP ANIMATION");
			cur = pageWidget.curPageBmp;
			tmp = BitmapFactory.decodeResource(pageWidget.getResources(), bgs[curC+1]);
			next = Bitmap.createScaledBitmap(tmp, screen.x, y_axis, true);
			tmp.recycle();
			
			pageWidget.setBitmaps(cur, next);
			pageWidget.setTouchPosition(from);
			
			PointF touch = new PointF(from.x,from.y);
			
			for (int i=0;i<gaps;++i){
				touch.x += width_gap;
				touch.y += height_gap;
				try {
					Thread.sleep(sleep_time);
				} catch (InterruptedException e) {}
				pageWidget.setTouchPosition(touch);
			}
			
			endPageAnimation.resetPage(+1);
			
			
		}else{ //next to cur (DOWN)
			Log.d("PAGE_ANIMATION", "DOWN ANIMATION");
			
			next=pageWidget.curPageBmp;
			tmp = BitmapFactory.decodeResource(pageWidget.getResources(), bgs[curC-1]);
			cur = Bitmap.createScaledBitmap(tmp, screen.x, y_axis, true);
			tmp.recycle();
			
			pageWidget.setBitmaps(cur, next);
			pageWidget.setTouchPosition(to);
			
			PointF touch = new PointF(to.x,to.y);
			
			for (int i=0;i<gaps;++i){
				touch.x -= width_gap;
				touch.y -= height_gap;
				try {
					Thread.sleep(sleep_time);
				} catch (InterruptedException e) {}
				pageWidget.setTouchPosition(touch);
			}
			
			endPageAnimation.resetPage(-1);
		}
		
		return null;
	}
	@Override
	 protected void onPostExecute(Void result) {
		endPageAnimation.endAnimation(0);
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
	}
	
}
