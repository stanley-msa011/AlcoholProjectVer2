package test.gps;

import ubicomp.drunk_detection.fragments.TestFragment;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;

public class GPSInitTask extends AsyncTask<Object, Void, Boolean> {

	private TestFragment testFragment;
	private LocationManager locationManager;
	private GPSToastHandler tHandler;
	public GPSInitTask(TestFragment a,LocationManager lm){
		testFragment = a;
		locationManager = lm;
		tHandler = new GPSToastHandler(testFragment.getActivity());
	}
	
	@Override
	protected Boolean doInBackground(Object... params) {
		Boolean check = (Boolean) params[0];
		Boolean newIntent = false;
		if (check.booleanValue()){
			boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if (!network_enabled || !gps_enabled){
				newIntent = true;
				testFragment.setKeepMsgBox(true);
				Intent gpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				testFragment.startActivityForResult(gpsIntent, TestFragment._GPS);
				tHandler.sendEmptyMessage(0);
			}
		}
		return newIntent;
	}

	@Override
	 protected void onPostExecute(Boolean result) {
		locationManager = null;
		if (!result.booleanValue()){
			testFragment.runGPS();
		}
     }
	

	
}
