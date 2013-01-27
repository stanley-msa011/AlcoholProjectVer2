package game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import ioio.examples.hello.R;

public class TreeImageHandler {

	static private Object lock = new Object();
	private static final int[] Tree_pics = {
			R.drawable.t000,R.drawable.t001,R.drawable.t002,R.drawable.t003,R.drawable.t004,
			R.drawable.t005,R.drawable.t006,R.drawable.t007,R.drawable.t008,R.drawable.t009,
			R.drawable.t010,R.drawable.t011,R.drawable.t012,R.drawable.t013,R.drawable.t014,
			R.drawable.t015,R.drawable.t016,R.drawable.t017,R.drawable.t018,R.drawable.t019,
			R.drawable.t020,R.drawable.t021,R.drawable.t022,R.drawable.t023,R.drawable.t024,
			R.drawable.t025,R.drawable.t026,R.drawable.t027,R.drawable.t028,R.drawable.t029,
			R.drawable.t030,R.drawable.t031,R.drawable.t032,R.drawable.t033,R.drawable.t034,
			
			R.drawable.t035,R.drawable.t036,R.drawable.t037,R.drawable.t038,R.drawable.t039,
			R.drawable.t040,R.drawable.t041,R.drawable.t042,R.drawable.t043,R.drawable.t044,
			R.drawable.t045,R.drawable.t046,R.drawable.t047,R.drawable.t048,R.drawable.t049,
			R.drawable.t050,R.drawable.t051,R.drawable.t052,R.drawable.t053,R.drawable.t054,
			R.drawable.t055,R.drawable.t056,R.drawable.t057,R.drawable.t058,R.drawable.t059,
			R.drawable.t060,R.drawable.t061,R.drawable.t062,R.drawable.t063,R.drawable.t064,
			R.drawable.t065,R.drawable.t066,R.drawable.t067,R.drawable.t068,R.drawable.t069,
			
			R.drawable.t070,R.drawable.t071,R.drawable.t072,R.drawable.t073,R.drawable.t074,
			R.drawable.t075,R.drawable.t076,R.drawable.t077,R.drawable.t078,R.drawable.t079,
			R.drawable.t080,R.drawable.t081,R.drawable.t082,R.drawable.t083,R.drawable.t084,
			R.drawable.t085,R.drawable.t086,R.drawable.t087,R.drawable.t088,R.drawable.t089,
			R.drawable.t090,R.drawable.t091,R.drawable.t092,R.drawable.t093,R.drawable.t094,
			R.drawable.t095,R.drawable.t096,R.drawable.t097,R.drawable.t098,R.drawable.t099,
			R.drawable.t100,R.drawable.t101,R.drawable.t102,R.drawable.t103,R.drawable.t104,
			
			R.drawable.t105,R.drawable.t106,R.drawable.t107,R.drawable.t108,R.drawable.t109,
			R.drawable.t110,R.drawable.t111,R.drawable.t112,R.drawable.t113,R.drawable.t114,
			R.drawable.t115,R.drawable.t116,R.drawable.t117,R.drawable.t118,R.drawable.t119,
			R.drawable.t120,R.drawable.t121,R.drawable.t122,R.drawable.t123,R.drawable.t124,
			R.drawable.t125,R.drawable.t126,R.drawable.t127,R.drawable.t128,R.drawable.t129,
			R.drawable.t130,R.drawable.t131,R.drawable.t132,R.drawable.t133,R.drawable.t134,
			R.drawable.t135,R.drawable.t136,R.drawable.t137,R.drawable.t138,R.drawable.t139
	};
	
	private static Bitmap[] tree_bitmap = null;
	
	public static int getTreeImageDrawableId(int stage, int coin_num){
		int idx = coin_num;
		if (idx<0 || idx > GameState.MAX_COINS[stage] || idx >=Tree_pics.length)
			return Tree_pics [0];
		return Tree_pics [idx];
	}
	
	public static int getTreeImageDrawableId(int idx){
		if (idx<0 || idx >=Tree_pics.length)
			return Tree_pics [0];
		return Tree_pics [idx];
	}
	
	public static int getTreeImageIdx(int stage, int coin_num){
		int idx = coin_num;
		if (idx<0 || idx > GameState.MAX_COINS[stage] || idx >=Tree_pics.length)
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
