package test.gps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ioio.examples.hello.TestFragment;
import ioio.examples.hello.R;
import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;



public class GPSRunTask extends AsyncTask<Object, Void, Boolean> {

	private TestFragment testFragment;
	private Activity activity;
	private GPSHandler gpsHandler;
	private LocationListener locationListener;
	private LocationManager locationManager;
	private File file;
	private BufferedWriter writer;
	
	
	public GPSRunTask(TestFragment a,LocationManager lm,File directory){
		testFragment = a;
		activity = a.getActivity();
		locationManager = lm;
		locationListener = new mLocationListener();
		gpsHandler = new GPSHandler(a, locationManager, locationListener);
		
		file = new File(directory,"geo.txt");
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			Log.d("GEO WRITER","FAIL TO OPEN");
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

		@Override
		public void onLocationChanged(Location location) {
			if (location!=null){
				sendLocation(location);
				locationManager.removeUpdates(this);
				testFragment.updateDoneState(TestFragment._GPS);
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
		
			TextView lat = (TextView) activity.findViewById(R.id.latitude);
			TextView lon = (TextView) activity.findViewById(R.id.longitude);
		
			String location_str = latitude+"\t"+longitude;
			Log.d("LOCATION",location_str);
		
			write_to_file(location_str);
			
			lat.setText(String.valueOf(latitude));
			lon.setText(String.valueOf(longitude));
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
				Thread.sleep(10000);
				Log.d("GPS TIMER","END");
				try {
					writer.close();
				} catch (IOException e) {
				}
				locationManager.removeUpdates(locationListener);
				testFragment.updateDoneState(TestFragment._GPS);
			} catch (InterruptedException e) {
				Log.d("GPS TIMER","EXCEPTION");
			}
		}
	}
	
}
