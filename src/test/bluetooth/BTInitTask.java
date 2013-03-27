package test.bluetooth;

import ioio.examples.hello.TestFragment;
import android.os.AsyncTask;
import android.util.Log;

public class BTInitTask extends AsyncTask<Void, Void, Void> {

	private TestFragment testFragment;
	private Bluetooth bt;
	
	public BTInitTask(TestFragment testFragment,Bluetooth bt){
		this.testFragment = testFragment;
		this.bt = bt;
	}
	
	
	@Override
	protected Void doInBackground(Void... params) {
		Log.d("BT","BT INIT");
		bt.enableAdapter();
		Log.d("BT","BT ENABLE ADAPTER");
		bt.pair();
		Log.d("BT","BT PAIR");
		int success = bt.connect();
		if (success == 1)
			Log.d("BT","BT CONNECT");
		else{
			testFragment.stop();
		}
		return null;
	}
	
	 protected void onPostExecute(Void result) {
		 Log.d("BT","END INIT");
		testFragment.updateInitState(TestFragment._BT);
    }

}
