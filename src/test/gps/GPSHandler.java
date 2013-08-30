package test.gps;

import ubicomp.drunk_detection.fragments.TestFragment;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;

public class GPSHandler extends Handler {
	
	
	private  LocationManager  locationManager;
	private LocationListener locationListener;
	
	public GPSHandler(TestFragment a, LocationManager locationManager,LocationListener locationListener){
		this.locationManager =  locationManager;
		this.locationListener = locationListener;
	}
	
	public void handleMessage(Message msg){
		
		boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if (gps_enabled)
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,10,locationListener);
		if(network_enabled)
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,500,10,locationListener);
		
	}
	
	

	

	
}
