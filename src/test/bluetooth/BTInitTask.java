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
		bt.connect();
		Log.d("BT","BT CONNECT");
		return null;
	}
	
	 protected void onPostExecute(Void result) {
		 Log.d("BT","END INIT");
		testFragment.updateInitState(TestFragment._BT);
    }

}
