package history.ui;

import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.fragments.HistoryFragment;
import ubicomp.drunk_detection.ui.ScreenSize;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.AsyncTask;

public class PageAnimationTaskVertical extends AsyncTask<Void, Void, Void> {

	private PageWidgetVertical pageWidget;
	private PointF from;
	private float width_gap;
	private float height_gap;
	public static final int gaps = 30;
	private static final int clip_time = 500;
	private static final int sleep_time = clip_time/gaps;
	private HistoryFragment historyFragment;
	
	private Bitmap cur=null,next=null;
	
	private Point screen;
	private int y_axis;
	
	public PageAnimationTaskVertical(PageWidgetVertical pageWidget, PointF from, PointF to,int[] bgs,HistoryFragment historyFragment,int startImageIdx){
		this.pageWidget = pageWidget;
		this.from = from;
		this.historyFragment = historyFragment;
		width_gap = (to.x - from.x)/(float)gaps;
		height_gap = (to.y - from.y)/(float)gaps;
		screen =ScreenSize.getScreenSize(historyFragment.getActivity());
		y_axis = screen.y - screen.x * 574/1080;
		Bitmap tmp;
		tmp = BitmapFactory.decodeResource(pageWidget.getResources(), bgs[startImageIdx]);
		cur = Bitmap.createScaledBitmap(tmp, screen.x, y_axis, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(pageWidget.getResources(), bgs[startImageIdx+1]);
		next = Bitmap.createScaledBitmap(tmp, screen.x, y_axis, true);
		tmp.recycle();
		pageWidget.setBitmaps(cur, next);
		pageWidget.setTouchPosition(from);
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		try {
			Thread.sleep(sleep_time);
		} catch (InterruptedException e) {}
		PointF touch = new PointF(from.x,from.y);
		for (int i=0;i<gaps;++i){
			touch.x += width_gap;
			touch.y += height_gap;
			try {
				Thread.sleep(sleep_time);
			} catch (InterruptedException e) {}
			pageWidget.setTouchPosition(touch);
		}
		return null;
	}
	@Override
	 protected void onPostExecute(Void result) {
		clean();
		historyFragment.resetPage(0);
		historyFragment.endAnimation();
    }
	@Override
	protected void onCancelled(){
		clean();
		FragmentTabs.enableTabAndClick(true);
	}

	private void clean(){
		if (cur!=null && !cur.isRecycled()){
			cur.recycle();
			cur = null;
		}
		if (next!=null && !next.isRecycled()){
			next.recycle();
			next = null;
		}
		System.gc();
	}
	
}
