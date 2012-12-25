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
			R.drawable.w25,R.drawable.w26,R.drawable.w27,R.drawable.w28,R.drawable.w29,
			R.drawable.w30,R.drawable.w31,R.drawable.w32,R.drawable.w33,R.drawable.w34
	};
	
	private static final int[] treePics ={
			R.drawable.tree1,
			R.drawable.tree2,
			R.drawable.tree3,
			R.drawable.tree4,
			R.drawable.tree5,
			R.drawable.tree6,
			R.drawable.tree7
		};
	
	private static Bitmap[] bg_bitmap = null;
	
	public static int getBackgroundDrawableId(int state, int coin_num){
		int idx = state*(GameState.MAX_COINS+1) + coin_num;
		idx = Background_pics.length - idx -1;
		return Background_pics [idx];
	}
	
	public static int getBackgroundIdx(int state, int coin_num){
		int idx = state*(GameState.MAX_COINS+1) + coin_num;
		idx = Background_pics.length - idx -1;
		return idx;
	}
	
	public static int getTreeDrawableId(int state){
		return treePics[state];
	}
	
	public static Bitmap getBackgroundBitmap(int state, int coin_num,Resources r){
		synchronized(lock){
			if (bg_bitmap == null){
				bg_bitmap = new Bitmap[35];
				 	for (int i=0;i<bg_bitmap.length;++i){
				 		Bitmap tmp = BitmapFactory.decodeResource(r, BackgroundHandler.Background_pics[i]);
				 		bg_bitmap[i] = Bitmap.createScaledBitmap(tmp, 105, 192, true);
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
