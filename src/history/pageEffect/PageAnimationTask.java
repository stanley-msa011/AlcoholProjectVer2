package history.pageEffect;

import main.activities.HistoryFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;

public class PageAnimationTask extends AsyncTask<Void, Void, Void> {

	private PageWidget pageWidget;
	private PointF from;
	private float width_gap;
	private float height_gap;
	public static final int gaps = 90;
	private static final int clip_time = 2700;
	private static final int sleep_time = clip_time/gaps;
	private int[] bgs;
	private HistoryFragment historyFragment;
	
	private PointF endTouch;
	private int endImageIdx;
	
	public PageAnimationTask(PageWidget pageWidget, PointF from, PointF to, int[] bgs,HistoryFragment historyFragment,PointF endTouch,int endImageIdx){
		this.pageWidget = pageWidget;
		this.from = from;
		this.endTouch = endTouch;
		this.endImageIdx = endImageIdx;
		
		this.historyFragment = historyFragment;
		width_gap = (to.x - from.x)/(float)gaps;
		height_gap = (to.y - from.y)/(float)gaps;
		this.bgs = bgs;	
}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		Log.d("PageAnimation","Start animation");
		int width = pageWidget.getWidth();
		int height = pageWidget.getHeight();
		
		Bitmap cur=null,next=null,tmp=null;
		Bitmap prev_cur=null,prev_next=null;
		for (int c=0;c<bgs.length-1;++c){
			if (c > endImageIdx)
				break;
			
			if (prev_next!=null){
				cur = prev_next;
			}
			else{
				tmp = BitmapFactory.decodeResource(pageWidget.getResources(), bgs[c]);
				cur = Bitmap.createScaledBitmap(tmp, width,height , true);
				tmp.recycle();
			}
			tmp = BitmapFactory.decodeResource(pageWidget.getResources(), bgs[c+1]);
			next = Bitmap.createScaledBitmap(tmp, width,height , true);
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
		historyFragment.setPage();
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
		//
		historyFragment.endAnimation();
    }
	
}
