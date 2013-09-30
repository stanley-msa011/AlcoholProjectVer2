package test.bluetooth;

import ubicomp.drunk_detection.fragments.TestFragment;
import android.os.AsyncTask;
import android.util.Log;

public class BTRunTaskOld extends AsyncTask<Void, Void, Void> {

	private TestFragment testFragment;
	private Bluetooth bt;
	
	public BTRunTaskOld(TestFragment a,Bluetooth bt){
		this.testFragment = a;
		this.bt = bt;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		Log.d("BT","Run in background");
		if(bt.sendStart())
			bt.read();
		else
			bt.closeWithCamera();
		return null;
	}
	
	protected void onPostExecute(Void result) {
		testFragment.updateDoneState(TestFragment._BT);
   }
}
