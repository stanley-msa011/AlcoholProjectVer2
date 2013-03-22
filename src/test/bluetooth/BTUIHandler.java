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
	private Activity activity;
	private View view;
	private TextView text;
	
	public BTUIHandler(TestFragment testFragment){
		this.testFragment = testFragment;
		activity = testFragment.getActivity();
		text = (TextView) activity.findViewById(R.id.bt_value);
	}
	
	public void handleMessage(Message msg){
		String str = msg.getData().getString("ALCOHOL");
		text.setText(str);
	}

}
