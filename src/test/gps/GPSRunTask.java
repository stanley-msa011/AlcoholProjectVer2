package test.gps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import main.activities.TestFragment;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;



public class GPSRunTask extends AsyncTask<Object, Void, Boolean> {

	private TestFragment testFragment;
	private GPSHandler gpsHandler;
	private mLocationListener locationListener;
	private LocationManager locationManager;
	private File file;
	private BufferedWriter writer;
	private static final int TWO_MINUTES = 1000*120;
	
	public GPSRunTask(TestFragment a,LocationManager lm,File directory){
		testFragment = a;
		locationManager = lm;
		locationListener = new mLocationListener();
		gpsHandler = new GPSHandler(a, locationManager, locationListener);
		
		file = new File(directory,"geo.txt");
		try {
			writer = new BufferedWriter(new FileWriter(file));
			Log.d("GEO WRITER","open successfully");
		} catch (IOException e) {
			Log.d("GEO WRITER","Fail to open");
			writer = null;
		}
	}
	public final static int GPS_HANDLER_WHAT = 0;
	
	@Override
	protected Boolean doInBackground(Object... params) {
		Log.d("GPS","RUN TASK");
		Boolean check = (Boolean) params[0];
		if (check.booleanValue()){
			gpsHandler.sendEmptyMessage(GPS_HANDLER_WHAT );
			return true;
		}
		return false;
	}

	@Override
	 protected void onPostExecute(Boolean result) {
		Thread t = new Thread(new Timer());
		t.start();
     }
	
	private class mLocationListener implements LocationListener{

		public Location bestLoc = null;
		
		@Override
		public void onLocationChanged(Location location) {
			if (location!=null){
				Log.d("GPS_RUN_TASK","GET LOCATION");
				if (bestLoc == null){
					bestLoc = location;
				}
				else{
					Location tempLoc = bestLoc;
					bestLoc = getBetterLocation(location, tempLoc);
				}
			}
		}
		@Override
		public void onProviderDisabled(String provider) {
			
		}
		@Override
		public void onProviderEnabled(String provider) {
			// Do nothing.
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// Do nothing.
		}
	}
	
	private void sendLocation(Location loc){
		if (loc!=null){
			locationManager.removeUpdates(locationListener);
			double latitude = loc.getLatitude();
			double longitude = loc.getLongitude();
		
			String location_str = latitude+"\t"+longitude;
			Log.d("LOCATION",location_str);
		
			write_to_file(location_str);
		}
		else{
			Log.d("LOCATION","NULL");
		}
	}
	
	private void write_to_file(String str){
		if (writer!=null){
			try {
				Log.d("GEO WRITER","WRITE");
				writer.write(str);
			} catch (IOException e) {
				Log.d("GEO WRITER",e.toString());
				Log.d("GEO WRITER","FAIL TO WRITE");
			}
		}else{
			Log.d("GEO WRITER","NULL TO WRITE");
		}
		try {
			writer.close();
		} catch (IOException e) {
		}
	}
	
	public void close(){
		try {
			writer.close();
		} catch (IOException e) {
		}
		if (locationManager!=null && locationListener!=null)
			locationManager.removeUpdates(locationListener);
	}
	
	public class Timer implements Runnable{

		@Override
		public void run() {
			Log.d("GPS TIMER","RUN");
			try {
				Thread.sleep(8000);
				Log.d("GPS TIMER","END");

				 if (locationListener.bestLoc!=null){
					 sendLocation(locationListener.bestLoc);
				 }else{
						try {
							writer.close();
						} catch (IOException e) {
						}
				 }
				locationManager.removeUpdates(locationListener);
				testFragment.updateDoneState(TestFragment._GPS);
			} catch (InterruptedException e) {
				Log.d("GPS TIMER","EXCEPTION");
			}
		}
	}
	
//----------------------------------------------------------------------------------------------------------------------	
	private Location getBetterLocation(Location newLocation, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return newLocation;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved.
		if (isSignificantlyNewer) {
			return newLocation;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return currentBestLocation;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(newLocation.getProvider(), currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return newLocation;
		} else if (isNewer && !isLessAccurate) {
			return newLocation;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return newLocation;
		}
		return currentBestLocation;
	}

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
          return provider2 == null;
        }
        return provider1.equals(provider2);
    }
	
	
}
