package ubicomp.drunk_detection.activities;

import network.NetworkCheck;
import data.uploader.ClickLogUploader;
import data.uploader.Reuploader;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		
		String action = intent.getAction();
		Log.d("NETWORK",action);
		
		if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION) && !action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
			return;
		
		if(!NetworkCheck.networkCheck(context)){
			Log.d("NETWORK","NOT CONNECTED");
			return;
		}
		Log.d("NETWORK","CONNECTED");
		
		Intent regularIntent = new Intent(context,RegularCheckService.class);
		context.startService(regularIntent);
		
		Reuploader.reuploader(context);
		ClickLogUploader.upload(context);
	}
	

}
