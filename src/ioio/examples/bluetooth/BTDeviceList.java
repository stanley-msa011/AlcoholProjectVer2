package ioio.examples.bluetooth;

import ioio.examples.hello.R;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BTDeviceList extends Activity {
	// Debugging
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = false;
    
    private final String[] macAddressList = {
    		"00:15:FF:F3:35:4D",
    		"00:15:FF:F3:35:50",
    		"00:15:FF:F3:35:53",
    		"00:15:FF:F3:35:48",
    		"00:15:FF:F3:35:37",
    		"00:15:FF:F3:35:45"
    };

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	// Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.bt_device_list);


        // Set result CANCELED incase the user backs out
        setResult(Activity.RESULT_CANCELED);

        // Initialize the button to perform device discovery
        Button scanButton = (Button) findViewById(R.id.btnScan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                
            	doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.bt_device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.bt_device_name);

        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.lv_paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = (ListView) findViewById(R.id.lv_new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        if (mReceiver == null)
        	Log.d(TAG, "mReceiver is NULL");
        Log.d(TAG, "REGISTER 1");
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        this.unregisterReceiver(mReceiver);
        Log.d(TAG, "REGISTER 2");
        this.registerReceiver(mReceiver, filter);
        Log.d(TAG, "REGISTER END");

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
//        if (pairedDevices.size() > 0) {
//            findViewById(R.id.tv_title_paired_devices).setVisibility(View.VISIBLE);
//            for (BluetoothDevice device : pairedDevices) {
//                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
//            }
//        } else {
//            String noDevices = getResources().getText(R.string.none_paired).toString();
//            mPairedDevicesArrayAdapter.add(noDevices);
//        }
        
//        SharedPreferences linkBTSettings = getPreferences(0);
        SharedPreferences linkBTSettings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = linkBTSettings.edit();
        Boolean targetSet = linkBTSettings.getBoolean("targetSet", false);
        Boolean hasMatch = false;
        Intent intent = new Intent();
        if (targetSet) {
        	Log.d(TAG, "There is a target device in shared preferences: " + linkBTSettings.getString("targetAddress", "00:00:00:00:00:00"));
        	// The phone already has a target device set
        	intent.putExtra(EXTRA_DEVICE_ADDRESS, linkBTSettings.getString("targetAddress", "00:00:00:00:00:00"));
        	// Set result and finish this Activity
        	setResult(Activity.RESULT_OK, intent);
        	finish();
        } else {
        	// This is the first time a target device is being discovered
        	Log.d(TAG, "No target device set in shared preferences");
        	if (pairedDevices.size() > 0) {
        		for (BluetoothDevice device : pairedDevices) {
        			Log.d(TAG, "Match device: " + device.getAddress());
        			for (String possibleTarget : macAddressList) {
        				if (device.getAddress().equals(possibleTarget)) {
        					Log.d(TAG, "Found a target device: " + possibleTarget);
        					
        		        	editor.putString("targetAddress", device.getAddress());
        		        	editor.putBoolean("targetSet", true);
        		        	editor.commit();
        					hasMatch = true;
        					break;
        				}
        			}
        			if (hasMatch)
        				break;
        		}
        		if (hasMatch) {
        			Log.d(TAG, "Connecting to target device: " + linkBTSettings.getString("targetAddress", "00:00:00:00:00:00"));
        			intent.putExtra(EXTRA_DEVICE_ADDRESS, linkBTSettings.getString("targetAddress", "00:00:00:00:00:00"));
                	// Set result and finish this Activity
                	setResult(Activity.RESULT_OK, intent);
                	finish();
        		}
        	} else {
        		Toast.makeText(this, "There are no targer Bluetooth devices paired to this phone", Toast.LENGTH_LONG).show();
                finish();
        	}
        }
        
//        String targetAddress = "00:15:FF:F3:35:37";
//        if (pairedDevices.size() > 0) {
//        	for (BluetoothDevice device : pairedDevices) {
//        		if (targetAddress.equals(device.getAddress())) {
//        			break;
//        		}
//        	}
//        	mBtAdapter.cancelDiscovery();
//        	// Create the result Intent and include the MAC address
//            Intent intent = new Intent();
//            intent.putExtra(EXTRA_DEVICE_ADDRESS, targetAddress);
//
//            // Set result and finish this Activity
//            setResult(Activity.RESULT_OK, intent);
//            finish();
//        }
    
    }

    @Override
    public void onPause() {
    	super.onPause();
    	Log.d(TAG, "PAUSE");
//    	this.unregisterReceiver(mReceiver);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	this.unregisterReceiver(mReceiver);
    }
    
    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        findViewById(R.id.tv_title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }
    
    
    
    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };
    
    
    
   
    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };
    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_device_list, menu);
//        return true;
//    }
}
