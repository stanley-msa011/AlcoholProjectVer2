package test.bluetooth;

import test.Tester;
import android.os.AsyncTask;

public class BTRunTask extends AsyncTask<Void, Void, Void> {

	private BluetoothCaller btCaller;
	private Bluetooth bt;
	
	public BTRunTask(BluetoothCaller btCaller,Bluetooth bt){
		this.btCaller = btCaller;
		this.bt = bt;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		if(bt.sendStart())
			bt.read();
		else
			bt.closeWithCamera();
		return null;
	}
	
	@Override
	protected void onCancelled(Void result){
		bt.close();
	};

	protected void onPostExecute(Void result) {
		btCaller.updateDoneState(Tester._BT);
   }
}
