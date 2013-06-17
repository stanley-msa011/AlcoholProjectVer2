package test.bluetooth;

import ubicomp.drunk_detection.activities.TestFragment;
import android.os.Handler;
import android.os.Message;

public class BTUIHandler extends Handler {
	
	private TestFragment testFragment;
	
	public BTUIHandler(TestFragment testFragment){
		this.testFragment = testFragment;
	}
	
	public void handleMessage(Message msg){
		if (msg.what == 0){
			//int time = msg.getData().getInt("TIME");
			//testFragment.changeTestMessage(time);
		}else if (msg.what == 1){
			//int change = msg.getData().getInt("CHANGE");
		}else if (msg.what == 2){
			float value = msg.getData().getFloat("value");
			int time = msg.getData().getInt("TIME");
			testFragment.changeTestMessage(value,time);
		}
	}

}
