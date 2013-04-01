package test.gps;

import ioio.examples.hello.TestFragment;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1000,locationListener);
		if(network_enabled)
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,1000,locationListener);
		
		//String enabled = gps_enabled+"/"+network_enabled;
		//Log.d("LOCATION",enabled);
	}
	
	

	

	
}
