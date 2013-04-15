package test.gps;

import ioio.examples.hello.TestFragment;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

public class GPSInitTask extends AsyncTask<Object, Void, Boolean> {

	private TestFragment testFragment;
	private LocationManager locationManager;
	public GPSInitTask(TestFragment a,LocationManager lm){
		testFragment = a;
		locationManager = lm;
	}
	
	@Override
	protected Boolean doInBackground(Object... params) {
		Log.d("GPS","INIT TASK");
		Boolean check = (Boolean) params[0];
		Boolean newIntent = false;
		if (check.booleanValue()){
			boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if (!gps_enabled && !network_enabled){
				newIntent = true;
				Intent gpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				testFragment.startActivityForResult(gpsIntent, TestFragment._GPS);
			}
		}
		return newIntent;
	}

	@Override
	 protected void onPostExecute(Boolean result) {
		if (!result.booleanValue()){
			Log.d("GPS","NO INTENT");
			//testFragment.updateGPSInitState();
			testFragment.runGPS();
		}
     }
	

	
}
