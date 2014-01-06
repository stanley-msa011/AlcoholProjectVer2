package test.gps;

import android.content.Context;
import android.location.LocationManager;
import android.os.AsyncTask;

public class GPSInitTask extends AsyncTask<Object, Void, Boolean> {

	private LocationManager locationManager;
	private GPSToastHandler tHandler;
	private GPSInterface gpsInterface;
	public GPSInitTask(Context context, GPSInterface gps,LocationManager lm){
		gpsInterface = gps;
		locationManager = lm;
		tHandler = new GPSToastHandler(context);
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
				gpsInterface.setKeepMsgBox(true);
				gpsInterface.callGPSActivity();
				tHandler.sendEmptyMessage(0);
			}
		}
		return newIntent;
	}

	@Override
	 protected void onPostExecute(Boolean result) {
		locationManager = null;
		if (!result.booleanValue()){
			gpsInterface.runGPS();
		}
     }
	

	
}
