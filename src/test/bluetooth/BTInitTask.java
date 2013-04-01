package test.bluetooth;

import ioio.examples.hello.TestFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

public class BTInitTask extends AsyncTask<Void, Void, Void> {

	private TestFragment testFragment;
	private Bluetooth bt;
	private BTUIHandler btUIHandler;
	private boolean Init_success;
	
	public BTInitTask(TestFragment testFragment,Bluetooth bt){
		this.testFragment = testFragment;
		this.bt = bt;
		this.btUIHandler = new BTUIHandler(testFragment);
	}
	
	
	@Override
	protected Void doInBackground(Void... params) {
		Log.d("BT","BT INIT");
		bt.enableAdapter();
		Log.d("BT","BT ENABLE ADAPTER");
		bt.pair();
		Log.d("BT","BT PAIR");
		int success = bt.connect();
		if (success == 1){
			Log.d("BT","BT CONNECT");
			Init_success = true;
		}
		else{
			Log.d("BT","CONNECT FAIL");
			
			/*Message msg = new Message();
			Bundle data = new Bundle();
			data.putString("ALCOHOL", "FAIL TO CONNECT");
			msg.setData(data);
			btUIHandler.sendMessage(msg);
			*/
			Init_success = false;
			//testFragment.stop();
		}
		return null;
	}
	
	 protected void onPostExecute(Void result) {
		 Log.d("BT","END INIT");
		 if (Init_success)
			 testFragment.updateInitState(TestFragment._BT);
		 else{
			 testFragment.stop();
			 testFragment.failBT();
		 }
    }

}
