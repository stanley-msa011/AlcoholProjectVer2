package game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import ioio.examples.hello.R;

public class TreeImageHandler {

	static private Object lock = new Object();
	private static final int[] Tree_pics = {
			R.drawable.t00,R.drawable.t01,R.drawable.t02,R.drawable.t03,R.drawable.t04,
			R.drawable.t05,R.drawable.t06,R.drawable.t07,R.drawable.t08,R.drawable.t09,
			R.drawable.t10,R.drawable.t11,R.drawable.t12,R.drawable.t13,R.drawable.t14,
			R.drawable.t15,R.drawable.t16,R.drawable.t17,R.drawable.t18,R.drawable.t19,
			R.drawable.t20,R.drawable.t21,R.drawable.t22,R.drawable.t23,R.drawable.t24,
			R.drawable.t25,R.drawable.t26,R.drawable.t27,R.drawable.t28,R.drawable.t29,
			R.drawable.t30,R.drawable.t31,R.drawable.t32,R.drawable.t33,R.drawable.t34,
			
			R.drawable.t35,R.drawable.t36,R.drawable.t37,R.drawable.t38,R.drawable.t39,
			R.drawable.t40,R.drawable.t41,R.drawable.t42,R.drawable.t43,R.drawable.t44,
			R.drawable.t45,R.drawable.t46,R.drawable.t47,R.drawable.t48,R.drawable.t49,
			R.drawable.t50,R.drawable.t51,R.drawable.t52,R.drawable.t53,R.drawable.t54,
			R.drawable.t55,R.drawable.t56,R.drawable.t57,R.drawable.t58,R.drawable.t59,
			R.drawable.t60,R.drawable.t61,R.drawable.t62,R.drawable.t63,R.drawable.t64,
			R.drawable.t65,R.drawable.t66,R.drawable.t67,R.drawable.t68,R.drawable.t69,
			
			R.drawable.t00,R.drawable.t01,R.drawable.t02,R.drawable.t03,R.drawable.t04,
			R.drawable.t05,R.drawable.t06,R.drawable.t07,R.drawable.t08,R.drawable.t09,
			R.drawable.t10,R.drawable.t11,R.drawable.t12,R.drawable.t13,R.drawable.t14,
			R.drawable.t15,R.drawable.t16,R.drawable.t17,R.drawable.t18,R.drawable.t19,
			R.drawable.t20,R.drawable.t21,R.drawable.t22,R.drawable.t23,R.drawable.t24,
			R.drawable.t25,R.drawable.t26,R.drawable.t27,R.drawable.t28,R.drawable.t29,
			R.drawable.t30,R.drawable.t31,R.drawable.t32,R.drawable.t33,R.drawable.t34,
			
			R.drawable.t00,R.drawable.t01,R.drawable.t02,R.drawable.t03,R.drawable.t04,
			R.drawable.t05,R.drawable.t06,R.drawable.t07,R.drawable.t08,R.drawable.t09,
			R.drawable.t10,R.drawable.t11,R.drawable.t12,R.drawable.t13,R.drawable.t14,
			R.drawable.t15,R.drawable.t16,R.drawable.t17,R.drawable.t18,R.drawable.t19,
			R.drawable.t20,R.drawable.t21,R.drawable.t22,R.drawable.t23,R.drawable.t24,
			R.drawable.t25,R.drawable.t26,R.drawable.t27,R.drawable.t28,R.drawable.t29,
			R.drawable.t30,R.drawable.t31,R.drawable.t32,R.drawable.t33,R.drawable.t34
	};
	
	private static Bitmap[] tree_bitmap = null;
	
	public static int getTreeImageDrawableId(int stage, int coin_num){
		int idx = coin_num;
		if (idx<0 || idx > GameState.MAX_COINS[stage] || idx >=Tree_pics.length)
			return Tree_pics [0];
		return Tree_pics [idx];
	}
	
	public static int getTreeImageIdx(int stage, int coin_num){
		int idx = coin_num;
		if (idx<0 || idx >= GameState.MAX_COINS[stage] || idx >=Tree_pics.length)
			return 0;
		return idx;
	}
	
	public static Bitmap getTreeImageBitmap(int stage, int coin_num,Resources r){
		synchronized(lock){
			if (tree_bitmap == null){
				tree_bitmap = new Bitmap[Tree_pics.length];
				 	for (int i=0;i<tree_bitmap.length;++i){
				 		Bitmap tmp = BitmapFactory.decodeResource(r, TreeImageHandler.Tree_pics[i]);
				 		tree_bitmap[i] = Bitmap.createScaledBitmap(tmp, 100,100, true);//105,192
				 		tmp.recycle();
				 	}
			}
		}
		return tree_bitmap[getTreeImageIdx(stage, coin_num)];
	}
	public static void cleanBitmaps(){
		synchronized(lock){
			if (tree_bitmap != null){
				 	for (int i=0;i<tree_bitmap.length;++i)
				 		if (tree_bitmap[i] != null){
				 			tree_bitmap[i].recycle();
				 			tree_bitmap[i]= null;
				 		}
				 	tree_bitmap=null;
			}
		}
	}
}
