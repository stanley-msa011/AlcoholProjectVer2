package history.pageEffect;

import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.activities.HistoryFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;

public class PageAnimationTaskVertical extends AsyncTask<Void, Void, Void> {

	private PageWidgetVertical pageWidget;
	private PointF from;
	private float width_gap_1,width_gap_2,width_gap_3,width_gap_4;
	private float height_gap_1,height_gap_2,height_gap_3,height_gap_4;
	public static final int gaps = 100;
	private static final int clip_time = 1000;
	private static final int sleep_time = clip_time/gaps;
	private int[] bgs;
	private HistoryFragment historyFragment;
	
	private PointF endTouch;
	private int startImageIdx;
	private int endImageIdx;
	
	private Bitmap cur=null,next=null,tmp=null;
	private Bitmap prev_cur=null,prev_next=null;
	
	private int end_type=-1; 
	
	public PageAnimationTaskVertical(PageWidgetVertical pageWidget, PointF from, PointF to,int[] bgs,HistoryFragment historyFragment,PointF endTouch,int startImageIdx,int endImageIdx){
		this.pageWidget = pageWidget;
		this.from = from;
		this.endTouch = endTouch;
		this.startImageIdx = startImageIdx;
		this.endImageIdx = endImageIdx;
		
		this.historyFragment = historyFragment;
		
		width_gap_1 = (to.x - from.x)/(float)gaps;
		height_gap_1 = (to.y - from.y)/(float)gaps;
		this.bgs = bgs;	
	}
	
	public PageAnimationTaskVertical(PageWidgetVertical pageWidget, PointF from, PointF to,int[] bgs,HistoryFragment historyFragment,PointF endTouch,int startImageIdx,int endImageIdx,int type){
		this.pageWidget = pageWidget;
		this.from = from;
		this.endTouch = endTouch;
		this.startImageIdx = startImageIdx;
		this.endImageIdx = endImageIdx;
		
		this.historyFragment = historyFragment;
		
		width_gap_1 = (to.x - from.x)/(float)gaps;
		height_gap_1 = (to.y - from.y)/(float)gaps;
		this.bgs = bgs;	
		end_type = type;
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		Log.d("PageAnimation","Start animation");
		int width = pageWidget.getWidth2();
		int height = pageWidget.getHeight2();
		
		
		cur=null;next=null;tmp=null;
		prev_cur=null;prev_next=null;
		
		for (int c=startImageIdx;c<bgs.length-1;++c){
			if (c > endImageIdx)
				break;
			
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inSampleSize = 3;
			if (prev_next!=null){
				cur = prev_next;
			}
			else{
				tmp = BitmapFactory.decodeResource(pageWidget.getResources(), bgs[c],opt);
				cur = Bitmap.createScaledBitmap(tmp, width,height , true);
				tmp.recycle();
			}
			tmp = BitmapFactory.decodeResource(pageWidget.getResources(), bgs[c+1],opt);
			next = Bitmap.createScaledBitmap(tmp, width,height , true);
			tmp.recycle();
			
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
		if (end_type == -1)
			historyFragment.endAnimation();
		else
			historyFragment.endAnimation(end_type);
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
