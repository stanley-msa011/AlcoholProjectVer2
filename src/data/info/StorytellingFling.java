package data.info;

import android.util.Log;

public class StorytellingFling  implements SelfHelpCounter{
	public long ts;
	public int acc = 0;
	public int used = 0;
	public int isClear = 0;
	public int page  = -1;
	
	public StorytellingFling(long ts,  int acc, int used, int isClear, int page) {
		this.ts = ts;
		this.acc = acc;
		this.used = used;
		this.isClear = isClear;
		this.page = page;
	}

	@Override 
	public String toString(){
		return ts+" "+acc+" "+used+" "+isClear+" "+page;
	}
	
	@Override
	public int getSelfHelpCounter() {
		Log.d("FLING",acc+" "+used);
		return 3*(acc - used);
	}

}
