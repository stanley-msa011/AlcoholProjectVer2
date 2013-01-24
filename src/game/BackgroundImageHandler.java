package game;

import android.content.Context;
import android.graphics.drawable.Drawable;
import ioio.examples.hello.R;

public class BackgroundImageHandler {
	
	private static final int[] Bg_pics = {
		R.drawable.background_all_0,R.drawable.background_all_1,
		R.drawable.background_all_2,R.drawable.background_all_3
	};
	
	private static int BG_STAGE = GameState.MAX_STAGE+1;
	private static Drawable[] Bg_drawables = null;
	
	public static int getBackgroundImageDrawableId(int stage){
		int idx = stage;
		if (idx<0 || idx >= Bg_pics.length)
			return Bg_pics [0];
		return Bg_pics [idx];
	}
	
	public static Drawable getBackgroundImageDrawable(int stage,Context context){
		if (Bg_drawables == null){
			Bg_drawables = new Drawable[BG_STAGE];
			Bg_drawables[0] = context.getResources().getDrawable(R.drawable.background_all_0);
			Bg_drawables[1] = context.getResources().getDrawable(R.drawable.background_all_1);
			Bg_drawables[2] = context.getResources().getDrawable(R.drawable.background_all_2);
			Bg_drawables[3] = context.getResources().getDrawable(R.drawable.background_all_3);
		}
		return Bg_drawables[stage];
	}
	
}
