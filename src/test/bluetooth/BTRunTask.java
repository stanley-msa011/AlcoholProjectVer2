package test.bluetooth;

import ubicomp.drunk_detection.fragments.TestFragment;
import android.os.AsyncTask;

public class BTRunTask extends AsyncTask<Void, Void, Void> {

	private TestFragment testFragment;
	private Bluetooth bt;
	
	public BTRunTask(TestFragment a,Bluetooth bt){
		this.testFragment = a;
		this.bt = bt;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		bt.read();
		return null;
	}
	
	@Override
	protected void onCancelled(Void result){
		bt.close();
		
	};

	protected void onPostExecute(Void result) {
		testFragment.updateDoneState(TestFragment._BT);
   }
}
