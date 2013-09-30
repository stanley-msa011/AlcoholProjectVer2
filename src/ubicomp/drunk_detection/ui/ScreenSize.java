package ubicomp.drunk_detection.ui;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class ScreenSize {

	private static Point screen = null;
	
	public static int getScreenX(Context context){
		if (screen != null)
			return screen.x;
		getSize(context);
		return screen.x;
	}
	
	public static Point getScreenSize(Context context){
		if (screen != null)
			return screen;
		getSize(context);
		return screen;
	}
	
	@SuppressWarnings("deprecation")
	private static void getSize(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		screen = new Point();
		
		if (Build.VERSION.SDK_INT < 13){
			screen.x = display.getWidth();
			screen.y = display.getHeight();
		}else{
			display.getSize(screen);
		}
		
		if (screen.x > screen.y){
			int tmp = screen.x;
			screen.x = screen.y;
			screen.y = tmp;
		}
		screen.y = screen.y - screen.x * 209/1080;
		Log.d("ScreenSize",screen.x+"-"+screen.y);
	}
}
