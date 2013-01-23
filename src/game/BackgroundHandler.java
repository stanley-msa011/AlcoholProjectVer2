package game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import ioio.examples.hello.R;

public class BackgroundHandler {

	static private Object lock = new Object();
	private static final int[] Background_pics = {
			R.drawable.w00,R.drawable.w01,R.drawable.w02,R.drawable.w03,R.drawable.w04,
			R.drawable.w05,R.drawable.w06,R.drawable.w07,R.drawable.w08,R.drawable.w09,
			R.drawable.w10,R.drawable.w11,R.drawable.w12,R.drawable.w13,R.drawable.w14,
			R.drawable.w15,R.drawable.w16,R.drawable.w17,R.drawable.w18,R.drawable.w19,
			R.drawable.w20,R.drawable.w21,R.drawable.w22,R.drawable.w23,R.drawable.w24,
			R.drawable.w25,R.drawable.w26,R.drawable.w27
	};
	
	private static Bitmap[] bg_bitmap = null;
	
	public static int getBackgroundDrawableId(int state, int coin_num){
		int idx = Background_pics.length - coin_num -1;
		if (idx<0 || idx >= Background_pics.length)
			return Background_pics [0];
		return Background_pics [idx];
	}
	
	public static int getBackgroundIdx(int state, int coin_num){
		int idx = Background_pics.length - coin_num -1;
		if (idx<0 || idx >= Background_pics.length)
			return 0;
		return idx;
	}
	
	public static Bitmap getBackgroundBitmap(int state, int coin_num,Resources r){
		synchronized(lock){
			if (bg_bitmap == null){
				bg_bitmap = new Bitmap[Background_pics.length];
				 	for (int i=0;i<bg_bitmap.length;++i){
				 		Bitmap tmp = BitmapFactory.decodeResource(r, BackgroundHandler.Background_pics[i]);
				 		bg_bitmap[i] = Bitmap.createScaledBitmap(tmp, 100, 100, true);//105,192
				 		tmp.recycle();
				 	}
			}
		}
		return bg_bitmap[getBackgroundIdx(state, coin_num)];
	}
	public static void cleanBitmaps(){
		synchronized(lock){
			if (bg_bitmap != null){
				 	for (int i=0;i<bg_bitmap.length;++i)
				 		if (bg_bitmap[i] != null){
				 			bg_bitmap[i].recycle();
				 			bg_bitmap[i]= null;
				 		}
				 	bg_bitmap=null;
			}
		}
	}
}
