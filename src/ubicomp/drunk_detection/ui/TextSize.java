package ubicomp.drunk_detection.ui;

import android.content.Context;
import android.graphics.Point;

public class TextSize {

	private static Point screen = null;
	
	private static int getScreenX(Context context){
		if (screen == null)
			screen = ScreenSize.getScreenSize(context);
		return screen.x;
	}

	private static int normalTextSize = 0;
	static public int normalTextSize(Context context){
		normalTextSize =  normalTextSize > 0 ?normalTextSize: getScreenX(context) * 21/480;
		return normalTextSize;
	}
	
	private static int smallTitleTextSize = 0;
	static public int smallTitleTextSize(Context context){
		smallTitleTextSize = smallTitleTextSize > 0 ? smallTitleTextSize: getScreenX(context)* 24/480;
		return smallTitleTextSize;
	}
	
	private static int smallTextSize = 0;
	static public int smallTextSize(Context context){
		smallTextSize =  smallTextSize > 0 ? smallTextSize:getScreenX(context) * 16/480;
		return smallTextSize;
	}
	
	private static int mTextSize = 0;
	static public int mTextSize(Context context){
		mTextSize =  mTextSize > 0 ? mTextSize:getScreenX(context) * 18/480;
		return mTextSize;
	}
	
	private static int titleSize = 0;
	static public int titleSize(Context context){
		titleSize = titleSize > 0 ? titleSize:getScreenX(context) * 27/480;
		return titleSize;
	}
	
	private static int largeTitleSize = 0;
	static public int largeTitleSize(Context context){
		largeTitleSize = largeTitleSize > 0 ? largeTitleSize:getScreenX(context) * 32/480;
		return largeTitleSize;
	}
	
	private static int slargeTitleSize = 0;
	static public int slargeTitleSize(Context context){
		slargeTitleSize = slargeTitleSize > 0 ? slargeTitleSize:getScreenX(context) * 48/480;
		return slargeTitleSize;
	}
	
	private static int mlargeTitleSize = 0;
	static public int mlargeTitleSize(Context context){
		mlargeTitleSize = mlargeTitleSize > 0 ? mlargeTitleSize:getScreenX(context) * 58/480;
		return mlargeTitleSize;
	}
	
	private static int xlargeTextSize = 0;
	static public int xlargeTextSize(Context context){
		xlargeTextSize = xlargeTextSize > 0 ? xlargeTextSize:getScreenX(context) * 64/480;
		return xlargeTextSize;
	}
	
	private static int xxlargeTextSize = 0;
	static public int xxlargeTextSize(Context context){
		xxlargeTextSize = xxlargeTextSize > 0 ? xxlargeTextSize:getScreenX(context) * 75/480;
		return xxlargeTextSize;
	}
	
	private static int largeTextSize = 0;
	static public int largeTextSize(Context context){
		largeTextSize = largeTextSize > 0 ? largeTextSize:getScreenX(context) * 42/480;
		return largeTextSize;
	}
}
