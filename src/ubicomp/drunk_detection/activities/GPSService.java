package ubicomp.drunk_detection.activities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import data.uploader.Reuploader;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class GPSService extends Service{

	private CustomLocationListener gpsLocationListener;
	private CustomLocationListener networkLocationListener;
	private LocationManager locationManager;
	private File file;
	private BufferedWriter writer;
	private Location bestLoc = null;
	private static final int gap = 10;
	
	public final static long GPS_TOTAL_TIME = 33000L;
	private final static long GPS_SEARCH_TIME = 30000L;
	
	
	private Timer timer = null;
	
	public int onStartCommand(Intent intent, int flags,int startId){
		Log.d("GPS","start the service");
		
		super.onStartCommand(intent, flags, startId);
		 bestLoc = null;
		Bundle data = intent.getExtras();
		String directory = data.getString("directory");
		
		Log.d("GPS","dir: "+directory);
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		
		
		file = new File(getStorageDirectory(directory),"geo.txt");
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			Log.d("GEO WRITER","FAIL TO OPEN");
			writer = null;
		}
		
		boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if (gps_enabled){
			gpsLocationListener = new CustomLocationListener();
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,10,gpsLocationListener);
		}
		if(network_enabled){
			networkLocationListener = new CustomLocationListener();
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,500,10,networkLocationListener);
		}
		timer = new Timer();
		timer.schedule(new TimeoutTask(), GPS_SEARCH_TIME);

		this.stopSelf();
		
		return Service.START_REDELIVER_INTENT;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	private File getStorageDirectory(String timestamp){
		String state = Environment.getExternalStorageState();
		File dir = null;
		if (state.equals(Environment.MEDIA_MOUNTED))
			dir = new File(Environment.getExternalStorageDirectory(),"drunk_detection");
		else
			dir = new File(this.getFilesDir(),"drunk_detection");
		if (!dir.exists())
			if (!dir.mkdirs())
				Log.d("TEST STORAGE","FAIL TO CREATE DIR");
		
		File mainDirectory = new File(dir,timestamp);
		if (!mainDirectory.exists())
			if (!mainDirectory.mkdirs()){
				return null;
			}
		return mainDirectory;
	}
	
	private class CustomLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			
			if (location !=null)
				Log.d("GPS","update loc: "+location.getProvider());
			else
				Log.d("GPS","update loc: "+(location!=null));
			
			if (location!=null){
				if (bestLoc == null)
					bestLoc = location;
				else
					bestLoc = getBetterLocation(location, bestLoc);
			}
		}
		@Override
		public void onProviderDisabled(String provider) {}
		@Override
		public void onProviderEnabled(String provider) {}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	}
	
	private void sendLocation(Location loc){
		if (loc!=null){
			double latitude = loc.getLatitude();
			double longitude = loc.getLongitude();
		
			String location_str = latitude+"\t"+longitude;
			write_to_file(location_str);
		}else{
			boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			int l1,l2;
			l1 = gps_enabled?9999:999;
			l2 = network_enabled?9999:999;
			String location_str = l1+"\t"+l2;
			Log.d("GPS","result="+location_str);
			
			write_to_file(location_str);
		}
	}
	
	private void write_to_file(String str){
		if (writer!=null){
			try {
				writer.write(str);
				writer.close();
			} catch (IOException e) {	}
		}
	}
	
	private class TimeoutTask extends TimerTask{
		@Override
		public void run() {
			sendLocation(bestLoc);
			try {
				Thread.sleep(GPS_TOTAL_TIME - GPS_SEARCH_TIME +100);
			} catch (InterruptedException e) {}
			
			if(locationManager !=null){
				if (gpsLocationListener != null){
					locationManager.removeUpdates(gpsLocationListener);
					Log.d("GPS","remove gps");
				}
				if (networkLocationListener != null){
					locationManager.removeUpdates(networkLocationListener);
					Log.d("GPS","remove network");
				}
			}
			
			locationManager = null;
			gpsLocationListener = null;
			networkLocationListener = null;
			
			Reuploader.reuploader(getBaseContext());
			
			stopSelf();
		}
		
	}
	
//----------------------------------------------------------------------------------------------------------------------	
	private Location getBetterLocation(Location newLocation, Location currentBestLocation) {
		if (newLocation == null)
			return currentBestLocation;
		
		if (currentBestLocation == null)
			return newLocation;

		// Check whether the new location fix is newer or older
		long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > gap;
		boolean isSignificantlyOlder = timeDelta < -gap;
		boolean isNewer = timeDelta > 0;

		if (isSignificantlyNewer)
			return newLocation;
		else if (isSignificantlyOlder)
			return currentBestLocation;

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(newLocation.getProvider(), currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate)
			return newLocation;
		else if (isNewer && !isLessAccurate)
			return newLocation;
		else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
			return newLocation;

		return currentBestLocation;
	}

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
          return provider2 == null;
        }
        return provider1.equals(provider2);
    }
	
}
