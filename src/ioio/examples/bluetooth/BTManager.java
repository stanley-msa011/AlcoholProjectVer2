package ioio.examples.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;


/*
 * A wrapper class to separate Bluetooth related operations from
 * main code. Allows for easier change of code in the future (at least I hope so)
 */
public class BTManager {
	private final static String TAG = "BTManager";
	
	// Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
 // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
	
	private BluetoothAdapter mBTAdapter;
	private Context mContext;
	
	public BTManager(Context ctx) {
		mBTAdapter = null;
		mContext = ctx;
	}
	
	public boolean isAdapterEnabled() {
		return mBTAdapter.isEnabled();
	}
	
}
