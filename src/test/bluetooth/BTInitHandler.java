package test.bluetooth;

import ubicomp.drunk_detection.fragments.TestFragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BTInitHandler extends Handler {
	private TestFragment testFragment;
	private Bluetooth bt;
	private boolean Init_success;
	
	public BTInitHandler(TestFragment testFragment,Bluetooth bt){
		this.testFragment = testFragment;
		this.bt = bt;
	}
	
	public void handleMessage(Message msg){
		bt.enableAdapter();
		bt.pair();
		int success = bt.connect();
		if (success == 1)
			Init_success = true;
		else{
			Log.d("BT","CONNECT FAIL");
			Init_success = false;
		}
		 if (Init_success)
				 testFragment.updateInitState(TestFragment._BT);
		 else{
			 testFragment.stopDueToInit();
			 testFragment.failBT();
		 }
	}
}
