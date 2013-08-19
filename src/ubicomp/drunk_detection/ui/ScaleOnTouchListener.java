package ubicomp.drunk_detection.ui;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class ScaleOnTouchListener implements View.OnTouchListener{

	private Rect rect;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int e = event.getActionMasked();
		switch(e){
			case MotionEvent.ACTION_MOVE:
				if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())){
					v.setScaleX(1F);
					v.setScaleY(1F);
		        }
				break;
			case MotionEvent.ACTION_UP:
				v.setScaleX(1F);
				v.setScaleY(1F);
				break;
			case MotionEvent.ACTION_DOWN:
				v.setScaleX(1.5F);
				v.setScaleY(1.5F);
				rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
				break;
		}
		return false;
	}
}