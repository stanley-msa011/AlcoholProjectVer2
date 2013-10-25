package test.bluetooth;

import ubicomp.drunk_detection.fragments.TestFragment;
import android.os.Handler;
import android.os.Message;

public class BTInitHandler extends Handler {
	private TestFragment testFragment;
	private Bluetooth bt;
	
	public BTInitHandler(TestFragment testFragment,Bluetooth bt){
		this.testFragment = testFragment;
		this.bt = bt;
	}
	
	public void handleMessage(Message msg){
		bt.enableAdapter();
		if (bt.pair()){
			if (bt.connect())
				testFragment.updateInitState(TestFragment._BT);
			else{
				testFragment.stopDueToInit();
				testFragment.failBT();
			}
		}else{
			testFragment.setPairMessage();
		}
	}
}
