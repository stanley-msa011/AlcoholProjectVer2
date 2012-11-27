package ioio.examples.hello;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

public class SensorCheckActivity extends Activity {
	private final static String TAG = "SensorCheckActivity";
	
	private static final int UPDATE_LATLNG = 2;
	
	// Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
	
	private LocationManager mLocationManager;
	
	private BluetoothAdapter mBTAdapter = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mBTAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "Starting...");
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "Resuming...");
		
		// Check if the GPS setting is currently enabled on the device.
        // This verification should be done during onStart() because the system calls this method
        // when the user returns to the activity, which ensures the desired location provider is
        // enabled each time the activity resumes from the stopped state.
		final boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		//for (int i=0; i<2; i++) {
        	if (!gpsEnabled) {
        		Log.d(TAG, "GPS is not enabled, time to enable it");
            	new EnableGpsDialogFragment().show(getFragmentManager(), "enableGpsDialog");
            } else if (!mBTAdapter.isEnabled()) {
            	Log.d(TAG, "Bluetooth is not enabled, user must enable it");
            	new EnableBluetoothDialogFragment().show(getFragmentManager(), "enableBTDialog");
            }
        //}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "Pausing...");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "Stopping...");
	}
	
	// Method to launch Settings
    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }

    /**
     * Dialog to prompt users to enable GPS on the device.
     */
    private class EnableGpsDialogFragment extends DialogFragment {

    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		return new AlertDialog.Builder(getActivity())
    		.setTitle(R.string.enable_gps)
    		.setMessage(R.string.enable_gps_dialog)
    		.setPositiveButton(R.string.enable_gps, new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				enableLocationSettings();
    			}
    		})
    		.create();
    	}
    }
    
    private void enableBluetoothSettings() {
    	Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }
    
    /**
     * Dialog to prompt users to enable Bluetooth on the device.
     */
    private class EnableBluetoothDialogFragment extends DialogFragment {

    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		return new AlertDialog.Builder(getActivity())
    		.setTitle(R.string.enable_BT)
    		.setMessage(R.string.enable_BT_dialog)
    		.setPositiveButton(R.string.enable_BT, new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				enableBluetoothSettings();
    			}
    		})
    		.create();
    	}
    	
    	@Override
    	public void onCancel(DialogInterface dialog) {
    		if (!mBTAdapter.isEnabled()) {
            	Log.d(TAG, "User chose not to enable Bluetooth, prompt again");
            	new EnableBluetoothDialogFragment().show(getFragmentManager(), "enableBTDialog");
            }
    	}
    }

}
