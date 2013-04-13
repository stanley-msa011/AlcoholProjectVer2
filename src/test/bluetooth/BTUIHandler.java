package test.bluetooth;

import ioio.examples.hello.TestFragment;
import ioio.examples.hello.R;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

public class BTUIHandler extends Handler {
	
	private TestFragment testFragment;
	
	public BTUIHandler(TestFragment testFragment){
		this.testFragment = testFragment;
	}
	
	public void handleMessage(Message msg){
		if (msg.what == 0){
			int time = msg.getData().getInt("TIME");
			testFragment.changeTestMessage(time);
		}else if (msg.what == 1){
			int change = msg.getData().getInt("CHANGE");
			testFragment.changeTestSpeed(change);
		}
	}

}
