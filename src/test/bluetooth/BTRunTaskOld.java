package test.bluetooth;

import test.Tester;
import android.os.AsyncTask;

public class BTRunTaskOld extends AsyncTask<Void, Void, Void> {

	private BluetoothCaller btCaller;
	private Bluetooth bt;
	
	public BTRunTaskOld(BluetoothCaller caller,Bluetooth bt){
		this.btCaller = caller;
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
	
	protected void onPostExecute(Void result) {
		btCaller.updateDoneState(Tester._BT);
   }
}
