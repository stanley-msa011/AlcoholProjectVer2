package test.bluetooth;

import android.os.Handler;
import android.os.Message;

public class BTUIHandler extends Handler {
	
	private BluetoothMessageUpdater updater;
	
	public BTUIHandler(BluetoothMessageUpdater updater){
		this.updater = updater;
	}
	
	public void handleMessage(Message msg){
		if (msg.what == 2){
			float value = msg.getData().getFloat("value");
			int time = msg.getData().getInt("TIME");
			updater.changeBluetoothCondition(value,time);
		}
	}

}
