package test.bluetooth;

import test.Tester;
import android.os.Handler;
import android.os.Message;

public class BTInitHandler extends Handler {
	private BluetoothCaller btCaller;
	private Bluetooth bt;
	
	public BTInitHandler(BluetoothCaller btCaller,Bluetooth bt){
		this.btCaller = btCaller;
		this.bt = bt;
	}
	
	public void handleMessage(Message msg){
		bt.enableAdapter();
		if (bt.pair()){
			if (bt.connect())
				btCaller.updateInitState(Tester._BT);
			else{
				btCaller.stopDueToInit();
				btCaller.failBT();
			}
		}else{
			btCaller.setPairMessage();
		}
	}
}
