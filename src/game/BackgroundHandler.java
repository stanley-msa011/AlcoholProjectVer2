package game;

import ioio.examples.hello.R;

public class BackgroundHandler {

	private static final int[] Background_pics = {
			R.drawable.w00,R.drawable.w01,R.drawable.w02,R.drawable.w03,R.drawable.w04,
			R.drawable.w05,R.drawable.w06,R.drawable.w07,R.drawable.w08,R.drawable.w09,
			R.drawable.w10,R.drawable.w11,R.drawable.w12,R.drawable.w13,R.drawable.w14,
			R.drawable.w15,R.drawable.w16,R.drawable.w17,R.drawable.w18,R.drawable.w19,
			R.drawable.w20,R.drawable.w21,R.drawable.w22,R.drawable.w23,R.drawable.w24,
			R.drawable.w25,R.drawable.w26,R.drawable.w27,R.drawable.w28,R.drawable.w29,
			R.drawable.w30,R.drawable.w31,R.drawable.w32,R.drawable.w33,R.drawable.w34
	};
	
	public static int getBackgroundDrawableId(int state, int coin_num){
		int idx = state*(GameState.MAX_COINS+1) + coin_num;
		idx = Background_pics.length - idx -1;
		return Background_pics [idx];
	}
	
}
