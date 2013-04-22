package test.bluetooth;

import main.activities.TestFragment;
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
		if (success == 1){
			Log.d("BT","BT CONNECT");
			Init_success = true;
		}
		else{
			Log.d("BT","CONNECT FAIL");
			
			Init_success = false;
		}
		Log.d("BT","END INIT");
		 if (Init_success)
			 testFragment.updateInitState(TestFragment._BT);
		 else{
			 //testFragment.stop();
			 testFragment.stopDueToInit();
			 testFragment.failBT();
		 }
	}
}
