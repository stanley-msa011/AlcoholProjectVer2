package history.pageEffect;

import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class PageAnimationHandler extends Handler {

	private PageWidget pageWidget;
	private PointF from;
	private PointF to;
	private float width_gap;
	private float height_gap;
	private static final int gaps = 100;
	
	public PageAnimationHandler(PageWidget pageWidget, PointF from, PointF to){
		this.pageWidget = pageWidget;
		this.from = from;
		this.to = to;
		width_gap = (to.x - from.x)/(float)gaps;
		height_gap = (to.y - from.y)/(float)gaps;
	}
	
	public void handleMessage(Message msg){
		int what = msg.what;
		
		if (what ==0){
			Log.d("PageAnimation","Start animation");
			PointF touch = new PointF(from.x,from.y);
			for (int i=0;i<gaps;++i){
				touch.x += width_gap;
				touch.y += height_gap;
				pageWidget.setTouchPosition(touch);
			}
		}
	}
}
