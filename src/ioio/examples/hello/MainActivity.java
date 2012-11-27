package ioio.examples.hello;

import ioio.examples.bluetooth.BTDeviceList;
import ioio.examples.bluetooth.BTService;
import ioio.examples.hello.SenseView.SenseLoop;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.AbstractIOIOActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the main activity of the HelloIOIO example application.
 * 
 * It displays a toggle button on the screen, which enables control of the
 * on-board LED. This example shows a very simple usage of the IOIO, by using
 * the {@link AbstractIOIOActivity} class. For a more advanced use case, see the
 * HelloIOIOPower example.
 */
public class MainActivity extends AbstractIOIOActivity implements SurfaceHolder.Callback {
	private final static String TAG = "IOIO";
	private final static boolean D = false;
	
	// State of the Activity
	public static final int STATE_SENSOR_CHECK = 0x00;
	public static final int STATE_BT_FINDING = 0x01;
	public static final int STATE_BT_CONNECTING = 0x02;
	public static final int STATE_RUN = 0x03;
	// ignore below --vvvv--
	public static final int STATE_PAUSE = 0x04;
	public static final int STATE_STOP = 0x05;
	public static final int STATE_RESUME = 0X06;
	// ignore above --^^^^--
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	public static final int PHOTO_COUNT = 3;
	private static final int MAX_PROGRESS = 6;
	
	//private ToggleButton button_;

//	private ToggleButton button_led_2;
//	private ToggleButton button_led_4;
	private static int state;
	
	public ImageView imgCountdown;
//	public ImageView imgSensing;
	public AnimationDrawable countdownAnimation;
//	public AnimationDrawable sensingAnimation;
	
	private ProgressBar progSensing;
	private Handler mHandler, mLocHandler;
	// The Handler that gets information back from the BluetoothChatService
    private Handler btHandler;
	private int progressStatus = 0;
	
	private Camera mCamera;
	//private Camera.Parameters mCamParameters;
	private SurfaceView mPreview;
	private SurfaceHolder mPreviewHolder = null;
	private PictureCallback mPicture;
	
	private File mainStorageDir;
	private File sessionDir;
	private String dirTimeStamp;
	private String dataTimeStamp;
	private int pictureCount = 1;
	
	private double num; 
	
	DecimalFormat nf;
	DecimalFormat stamp;
	
	TextView row_value;
	TextView voltage;
	TextView storage_state;
	TextView brac;
	
	TextView tvProgress;
	TextView tvSignal;
	
	
	private float value;
	private float volts;
	private double brac_value;
	
	private boolean isSensing = false;
	private boolean isWriting = false;
	private boolean doneSensing = false;

	File textfile;
	FileOutputStream stream;
	OutputStreamWriter sensor_value;
	
	Calendar calendar;
	
	private LocationManager mLocationManager;
	private TextView tvLatLng;
	private boolean gpsEnabled;
	private boolean gpsChecked;
	private boolean mUseFine;
	private boolean mUseBoth;

    private static final int UPDATE_LATLNG = 2;
    
	private static final int TEN_SECONDS = 1000 * 10;
    private static final int TEN_METERS = 10;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
	
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
    
 // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
    private BluetoothAdapter mBTAdapter = null;
    private BTService mBTService;
    private float pressureReading1 = 0.f, pressureReading2 = 0.f;
    private boolean btEnabled = false;
    private boolean btConnected = false;
    private boolean foundDevice = false;
    
    private SenseView senseView;
    private SenseLoop senseLoop;
    private boolean uiRun = false;
    private ImageView senseView2;
    private boolean isPeak = false;
    private Bitmap balloon;
    private int[] balloons = {
    		R.drawable.balloon1,
    		R.drawable.balloon2,
    		R.drawable.balloon3,
    		R.drawable.balloon4,
    		R.drawable.balloon5
    		};
    private int bDraw = 0;
    private long blowStartTime;
    private long blowEndTime;
    private double blowDuration = 0.0;
    private static final double NANO_TIME = 1000000000.0;
    
	/**
	 * Called when the activity is first created. Here we normally initialize
	 * our GUI.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		findViews();
		
		// Set count down animation
        imgCountdown.setBackgroundResource(R.drawable.countdown);
//        imgSensing.setBackgroundResource(R.drawable.sensing);
    	countdownAnimation = (AnimationDrawable) imgCountdown.getBackground();
//    	sensingAnimation = (AnimationDrawable) imgSensing.getBackground();
    	mHandler = new Handler();
    	mLocHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
//                    case UPDATE_ADDRESS:
//                        mAddress.setText((String) msg.obj);
//                        break;
                    case UPDATE_LATLNG:
                        tvLatLng.setText((String) msg.obj);
                        break;
                }
            }
        };
        btHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    /*
                    switch (msg.arg1) {
                    case BluetoothChatService.STATE_CONNECTED:
                        mTitle.setText(R.string.title_connected_to);
                        mTitle.append(mConnectedDeviceName);
                        mConversationArrayAdapter.clear();
                        break;
                    case BluetoothChatService.STATE_CONNECTING:
                        mTitle.setText(R.string.title_connecting);
                        break;
                    case BluetoothChatService.STATE_LISTEN:
                    case BluetoothChatService.STATE_NONE:
                        mTitle.setText(R.string.title_not_connected);
                        break;
                    }
                    */
                    break;
                case MESSAGE_WRITE:
//                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
//                    String writeMessage = new String(writeBuf);
//                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case MESSAGE_READ:
                	if (!uiRun) {
                		uiRun = true;
            			
            			setupCamera();
            			
            			runOnUiThread(new Runnable() { 
            		        public void run() {
            		        	//取得外部儲存媒體的狀態
            					String state = Environment.getExternalStorageState();
            					//判斷狀態
            					if (Environment.MEDIA_MOUNTED.equals(state)) {
            						storage_state.setText("can written");
            					} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            						storage_state.setText("read only, can't written");
            					} else {
            						storage_state.setText("can't read and written");
            					}
            		        } 
            		    });
            	    	
            	    	textfile = new File(sessionDir + File.separator + dirTimeStamp + ".txt");
            			
            			try {
            				stream = new FileOutputStream(textfile);
            				
            				sensor_value = new OutputStreamWriter(stream, "US-ASCII");
            				
            			} catch (FileNotFoundException e1) {
            				// TODO Auto-generated catch block
            				e1.printStackTrace();
            			} catch (UnsupportedEncodingException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			}
            	    	
            	    	Log.d(TAG, "Start looking for location via GPS");
            	    	Location loc = null;
            	    	if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            	    		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locListener);
            	    		loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            	    	}
            	    	if (loc != null)
            	    		updateUILocation(loc);
            	    	
                		countdownAnimation.start();
            	    	
            	    	long totalDuration = 0;
            	        
            	    	for(int i = 0; i< countdownAnimation.getNumberOfFrames();i++){  
            	    		totalDuration += countdownAnimation.getDuration(i);  
            	        }
            	        
            	        Timer timer = new Timer();
            	        TimerTask timerTask = new TimerTask(){  
            	        @Override
            		        public void run() {
            	        		Log.d(TAG, "Countdown animation is stopping");
            	        		isSensing = true;
//            	        		doSenseProgress();
            	        		new Thread (new Runnable() {
            	        			@Override
            	        			public void run() {
            	        				for (int i = 0; i < PHOTO_COUNT; i++) {
            	                			try {
            	                				mCamera.takePicture(null, null, mPicture);
            	        						Thread.sleep(1000);
            	        					} catch (InterruptedException e) {
            	        						Log.e(TAG, "Camera could not take picture: " + e.getMessage());
            	        					}
            	                    	}
            	        			}
            	        		}).start();
            	        	}
            		    };
            	        timer.schedule(timerTask, totalDuration);
            		}
                	
//                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    // Check if the byte array begins with an 'a' or 'm'
//                    if (readBuf[0] == 109) {
//                    	if (readBuf[1] != 10) {
//                    		senseView.currentPressure = barr2float(readBuf, 1, readBuf.length-2);
//                    	}
//                    } else if (readBuf[0] == 97) {
//                    	if (readBuf[1] != 10) {
//                    		senseView.currentBrac = barr2float(readBuf, 1, readBuf.length-2);
//                    	}
//                    }
                    
//                    String readMessage = new String(readBuf, 0, msg.arg1);
                	String readMessage = (String) msg.obj;
                	Log.d(TAG, "BT data: " + readMessage);
                	
                	if (readMessage.charAt(0) == 'm') {
                		pressureReading1 = pressureReading2;
                		try {
                			pressureReading2 = Float.parseFloat(readMessage.substring(1));
                			Log.d(TAG, "BT data Pressure reading 1: " + pressureReading1 + " Pressure reading 2: " + pressureReading2);
//                        	Log.d(TAG, "BT data Alcohol reading: " + alcoholReading);
                        	
                        	float diff = pressureReading2 - pressureReading1;
                        	Log.d(TAG, "BT data diff: " + diff);
                        	if (diff > 500.f && diff < 10000.f && !isPeak) {
                        		isPeak = true;
                        		blowStartTime = System.nanoTime();
//                        		bDraw++;
                        		
                        		// Save the alcohol reading
                        	} else if (diff >= -500.f && diff <= 500.f) {
                        		if (isPeak) {
                        			// Save the alcohol reading
                        			
                        			blowEndTime = System.nanoTime();
                        			blowDuration += (blowEndTime - blowStartTime) / NANO_TIME;
                        			Log.d(TAG, "BT data Blow start time: " + blowStartTime + "Blow end time: " + blowEndTime);
                        			Log.d(TAG, "BT data duration: " + blowDuration);
                        			if (blowDuration > 5) {
                        				bDraw = 4;
                        			} else {
                        				if (bDraw < 3)
                            				bDraw++;
                            			else
                            				bDraw = bDraw % 3;
                        			}
                        			
                        		} else {
                        			// Don't save alcohol reading
                        			
                        		}
                        	} else if (diff <= -500.f) {
                        		isPeak = false;
                        		// Clear data
                        		bDraw = 0;
                        		blowStartTime = blowEndTime = 0;
                        	}
                        	runOnUiThread(new Runnable() { 
        				        public void run() 
        				        {
        				        	senseView2.setImageResource(balloons[bDraw]);
        				        } 
        				    });
                		} catch (Exception e) {
                			Log.e(TAG, "ERROR! : " + e.getMessage());
                		}
                	} else if (readMessage.charAt(0) == 'a') {
                		Log.d(TAG, "BT data Alcohol reading: " + readMessage.substring(1));
//                		alcoholReading = Float.parseFloat(readMessage.substring(1));
                		if (isPeak) {
                			long unixTime = (int) (System.currentTimeMillis() / 1000L);
        					dataTimeStamp = stamp.format(unixTime);
        					try {
	        					sensor_value.write(dataTimeStamp + "\t");
	        					
	        					sensor_value.write(readMessage.substring(1));
	        					sensor_value.write("\r\n");
        					} catch (IOException ioe) {
        						ioe.printStackTrace();
        					}
                		}
                	}
                    
                   /*  Toast.makeText(getApplicationContext(), "Sensor messages: "
                            + readMessage, Toast.LENGTH_SHORT).show(); */
                    
//                    mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
//                    outputFile(readBuf);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
//                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
//                    Toast.makeText(getApplicationContext(), "Connected to "
//                                   + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
//                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
//                                   Toast.LENGTH_SHORT).show();
                    break;
                    
                }
            }
        };
        
//    	progSensing.setMax(MAX_PROGRESS);
        
        /*
         * Set variables for writing into data file
         */
		num = 100.0;
		nf = new DecimalFormat("0.000000");
		stamp = new DecimalFormat("0");
		row_value = (TextView)findViewById(R.id.row_value);
		voltage = (TextView)findViewById(R.id.voltage);
		brac = (TextView)findViewById(R.id.brac);
		
		/*
		 * Set up storage
		 */
		storage_state = (TextView)findViewById(R.id.storage_state);
		
		long unixTime = System.currentTimeMillis() / 1000L;
		dirTimeStamp = stamp.format(unixTime);
		
		// To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.
		String storageState = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(storageState)) {
			// SD card is mounted and ready for storage
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(storageState)) {
			// SD card is mounted in READ ONLY mode, cannot save file
			Log.e(TAG, "External storage is read only");
			return;
		}
		
		mainStorageDir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.
	    // This is main directory for all the session folders

	    // Create the storage directory if it does not exist
	    if (!mainStorageDir.exists()){
	        if (!mainStorageDir.mkdirs()){
	            Log.d(TAG, "Failed to create directory");
	            return;
	        }
	    }
	    
	    sessionDir = new File(mainStorageDir, dirTimeStamp);
	    // This is direction for each checking session
	    // Create the storage directory if it does not exist
	    if (!sessionDir.exists()){
	        if (!sessionDir.mkdirs()){
	            Log.d(TAG, "Failed to create session directory");
	            return;
	        }
	    }
	    
	    // Set up the camera
//	    setupCamera();
	    
	    /*
	     * Set up SenseView canvas and thread
	     */
//	    senseView = (SenseView) findViewById(R.id.senseView);
//    	senseLoop = senseView.getThread();
	    
	    // Location Detection start
	    mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    mUseFine = true;
	    
	    // Get local Bluetooth adapter
	    mBTAdapter = BluetoothAdapter.getDefaultAdapter();
	    
	    // If the adapter is null, then Bluetooth is not supported
        if (mBTAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        // Check right away if GPS and Bluetooth are enabled
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && mBTAdapter.isEnabled()) {
        	state = STATE_BT_FINDING;
        	gpsChecked = true;
        } else {
        	state = STATE_SENSOR_CHECK;
        	if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        		gpsChecked = true;
        }
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		Log.d(TAG, "Restarting...");
		
		// The activity is returning from another activity
		// Check if returning from BTDeviceList activity
		// by checking if a Bluetooth device is found
//		if (!btConnected) {
			// Set state to STATE_SENSOR_CHECK so that we will
			// do a check for the sensor settings again.
//			state = STATE_SENSOR_CHECK;
//		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "Starting...");
		
//		switch (state) {
//		case STATE_SENSOR_CHECK:
//			// Check if the GPS setting is currently enabled on the device.
//	        // This verification should be done during onStart() because the system calls this method
//	        // when the user returns to the activity, which ensures the desired location provider is
//	        // enabled each time the activity resumes from the stopped state.
//			runSensorCheck();
//			break;
//		case STATE_BT_FINDING:
//			// Bluetooth needs to look for a device to connect to
//			// We will set up Bluetooth Service and open BTDeviceList
//			setupBTTransfer();
//        	Intent serverIntent = new Intent(this, BTDeviceList.class);
//        	startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
//			break;
//		case STATE_BT_CONNECTING:
//			setupBTTransfer();
//        	Intent serverIntent = new Intent(this, BTDeviceList.class);
//        	startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
//			break;
//		}
//		if (state == STATE_SENSOR_CHECK) {
			// Check if the GPS setting is currently enabled on the device.
	        // This verification should be done during onStart() because the system calls this method
	        // when the user returns to the activity, which ensures the desired location provider is
	        // enabled each time the activity resumes from the stopped state.
//	        gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//	        btEnabled = mBTAdapter.isEnabled();
	        
//	    	if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//	        	new EnableGpsDialogFragment().show(getFragmentManager(), "enableGpsDialog");
//	        } else {
//	        	if (!mBTAdapter.isEnabled()) {
//	        		new EnableBluetoothDialogFragment().show(getFragmentManager(), "enableBTDialog");
//	        	} else if (!btConnected) {
//	        		if (mBTService == null)
//	        			setupBTTransfer();
//	        		Intent serverIntent = new Intent(this, BTDeviceList.class);
//	            	startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
//	        	}
//	        }
//			runSensorCheck();
//		}
	}
	
	@Override
    public void onResume() {
    	super.onResume();
    	Log.d(TAG, "Resuming...");
    	switch (state) {
		case STATE_SENSOR_CHECK:
			// Check if the GPS setting is currently enabled on the device.
	        // This verification should be done during onStart() because the system calls this method
	        // when the user returns to the activity, which ensures the desired location provider is
	        // enabled each time the activity resumes from the stopped state.
			runSensorCheck();
			break;
		case STATE_BT_FINDING:
			// Bluetooth needs to look for a device to connect to
			// We will set up Bluetooth Service and open BTDeviceList
			setupBTTransfer();
        	Intent serverIntent = new Intent(this, BTDeviceList.class);
        	startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
			break;
		case STATE_BT_CONNECTING:
			// The mobile device is trying to connect to the
			// Bluetooth device. Check if mBTService is connected yet.
			if (mBTService.getState() == BTService.STATE_CONNECTED)
				state = STATE_RUN;
			break;
    	}
    	
//    	if (state == STATE_PAUSE) {
    		// We've returned from a paused state
    		// Check the sensors if they need to be re-enabled
//    		if (gpsChecked && mBTAdapter.isEnabled()) {
//    			state = STATE_BT_CONNECTING;
//    			setupBTTransfer();
//            	Intent serverIntent = new Intent(this, BTDeviceList.class);
//            	startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
//    		} else {
//    			runSensorCheck();
//    		}
//    	} else if (state == STATE_BT_CONNECTING) {
    		// Do Bluetooth device connection
//    		setupBTTransfer();
//        	Intent serverIntent = new Intent(this, BTDeviceList.class);
//        	startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
//    	}
//    	
    	// Only start the camera and the location search if Bluetooth has been enabled
//    	if (btEnabled && btConnected) {
//	    	if (mCamera == null) {
//	    		mCamera = getCameraInstance();
//	    		Log.d(TAG, "Camera reopened");
//	    	}
//	    	
//	    	Log.d(TAG, "Start looking for location via GPS");
//	    	Location loc = null;
//	    	if (gpsEnabled) {
//	    		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locListener);
//	    		loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//	    	}
//	    	if (loc != null)
//	    		updateUILocation(loc);
//    	}
    }
	
	@Override
    public void onPause() {
    	super.onPause();
    	Log.d(TAG, "Pausing...");
    	
//    	state = STATE_PAUSE;
    	releaseCamera();
    	Log.d(TAG, "Camera is released");
    	mLocationManager.removeUpdates(locListener);
    	Log.d(TAG, "Location listener stopped updating");
    }
	
	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "Stopping...");
	}
	
	private void findViews() {
		//button_ = (ToggleButton) findViewById(R.id.button);
//		button_led_2 = (ToggleButton) findViewById(R.id.button_led2);
//		button_led_4 = (ToggleButton) findViewById(R.id.button_led4);	//used to control alcohol sensor
    	imgCountdown = (ImageView) findViewById(R.id.imgCountdown);
//    	imgSensing = (ImageView) findViewById(R.id.imgSensing);
    	tvProgress = (TextView) findViewById(R.id.tvProgress);
//    	tvSignal = (TextView) findViewById(R.id.tvSignal);
//    	progSensing = (ProgressBar) findViewById(R.id.progSensing);
    	tvLatLng = (TextView) findViewById(R.id.tvLatLng);
    	senseView2 = (ImageView) findViewById(R.id.senseView2);
    }
	
	@Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	super.onWindowFocusChanged(hasFocus);
    	
    	if (hasFocus) {
    		Log.d(TAG, "HAS FOCUS");
    		
    		if (state == STATE_RUN) {
    			
    			setupCamera();
    	    	
    	    	Log.d(TAG, "Start looking for location via GPS");
    	    	Location loc = null;
    	    	if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
    	    		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locListener);
    	    		loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	    	}
    	    	if (loc != null)
    	    		updateUILocation(loc);
    	    	
        		countdownAnimation.start();
    	    	
    	    	long totalDuration = 0;
    	        
    	    	for(int i = 0; i< countdownAnimation.getNumberOfFrames();i++){  
    	    		totalDuration += countdownAnimation.getDuration(i);  
    	        }
    	        
    	        Timer timer = new Timer();
    	        TimerTask timerTask = new TimerTask(){  
    	        @Override
    		        public void run() {
    	        		Log.d(TAG, "Countdown animation is stopping");
    	        		isSensing = true;
//    	        		doSenseProgress();
    	        		new Thread (new Runnable() {
    	        			@Override
    	        			public void run() {
    	        				for (int i = 0; i < PHOTO_COUNT; i++) {
    	                			try {
    	                				mCamera.takePicture(null, null, mPicture);
    	        						Thread.sleep(1000);
    	        					} catch (InterruptedException e) {
    	        						Log.e(TAG, "Camera could not take picture: " + e.getMessage());
    	        					}
    	                    	}
    	        			}
    	        		}).start();
    	        	}
    		    };
    	        timer.schedule(timerTask, totalDuration);
    		}
    	}
    }
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "On activity result");
		switch (requestCode) {
        case REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
            	Log.d(TAG, "Found devices to (securely) connect to");
            	if (mBTService != null) {
    	            // Only if the state is STATE_NONE, do we know that we haven't started already
    	            if (mBTService.getState() == BTService.STATE_NONE) {
    	              // Start the Bluetooth chat services
    	            	mBTService.start();
    	            	Log.d(TAG, "mBTService has started!");
    	            	
    	            }
    	        }
                connectDevice(data, true);
            }
            break;
        case REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
            	Log.d(TAG, "Found devices to (insecurely) connect to");
            	if (mBTService != null) {
    	            // Only if the state is STATE_NONE, do we know that we haven't started already
    	            if (mBTService.getState() == BTService.STATE_NONE) {
    	              // Start the Bluetooth chat services
    	            	mBTService.start();
    	            	Log.d(TAG, "mBTService has started!");
    	            	
    	            }
    	        }
                connectDevice(data, false);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
            	Log.d(TAG, "Returned from Bluetooth enablement");
                // Bluetooth is now enabled, now to look for the device to connect to
            	Toast.makeText(this, "Bluetooth is enabled", Toast.LENGTH_LONG).show();
            	state = STATE_BT_FINDING;
            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "User did not enable BT");
                Toast.makeText(this, "User did not enable Bluetooth", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
	}
	
	public void doSenseProgress() {
		new Thread (new Runnable() {
			@Override
			public void run() {
				while (progressStatus < MAX_PROGRESS) {
					runOnUiThread(new Runnable() { 
				        public void run() 
				        {			
				        	tvProgress.setText("Amount of data: " + progressStatus + " (in progress thread)");
				        	tvSignal.setText("Keep blowing!");
				        } 
				    });
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							progSensing.setProgress(progressStatus);
							if (progressStatus == progSensing.getMax()) {
								runOnUiThread(new Runnable() { 
							        public void run() 
							        {
							        	tvSignal.setText("STOP!");
							        } 
							    });
							}
						}
					});
					countDown();
				}
				isSensing = false;
				doneSensing = true;
				Intent i_ShowBrac = new Intent();
    			i_ShowBrac.putExtra("timestamp", dirTimeStamp);
    			i_ShowBrac.setClass(MainActivity.this, ShowBracActivity.class);
    			startActivity(i_ShowBrac);
				
			}
		}).start();
	}
	
	private void countDown() {
		try {
			progressStatus++;
			Thread.sleep(1000);
		} catch (InterruptedException ie) {
			Log.e(TAG, "Error in countDown(): " + ie.getMessage());
		}
	}
	
	private void runSensorCheck() {
		if (!gpsChecked) {
			gpsChecked = true;
			Log.d(TAG, "Showing Enable GPS Dialog");
        	new EnableGpsDialogFragment().show(getFragmentManager(), "enableGpsDialog");
		} else if (!mBTAdapter.isEnabled()) {
			Log.d(TAG, "Showing Enable Bluetooth Dialog");
			new EnableBluetoothDialogFragment().show(getFragmentManager(), "enableBTDialog");
		}
//        } else {
//        	if (!mBTAdapter.isEnabled()) {
//        		new EnableBluetoothDialogFragment().show(getFragmentManager(), "enableBTDialog");
//        	} else if (!btConnected) {
//        		if (mBTService == null)
//        			setupBTTransfer();
//        		Intent serverIntent = new Intent(this, BTDeviceList.class);
//            	startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
//        	}
//        }
	}
	
	/*
	 * Start camera-related methods.
	 * Starts and releases the camera instances.
	 */
	
	public void setupCamera() {
		
	    // Set up the camera
	    // Camera instance is not started yet here
	    
	    // Create our Preview view and set it as the content of our activity.
	    mPreview = new SurfaceView(this);
	    SurfaceHolder mPreviewHolder = mPreview.getHolder();
	    mPreviewHolder.addCallback(this);
	    mPreviewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	    //mPreview = new CameraPreview(this, mCamera);
	    FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
	    preview.addView(mPreview);
	    
	    // Set up the callback for when taking pictures
	    mPicture = new PictureCallback() {
	    	@Override
	    	public void onPictureTaken(byte[] data, Camera camera) {
	    		File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
	    		if (pictureFile == null){
	    			Log.d(TAG, "Error creating media file, check storage permissions");
	    			return;
	    		}

	    		try {
	    			FileOutputStream fos = new FileOutputStream(pictureFile);
	    			fos.write(data);
	    			fos.close();
	    		} catch (FileNotFoundException e) {
	    			Log.e(TAG, "File not found: " + e.getMessage());
	    		} catch (IOException e) {
	    			Log.e(TAG, "Error accessing file: " + e.getMessage());
	    		}

	    	}
	    };
	}
	
	public void initCamera(SurfaceHolder holder) {
		// Initialize the camera
		// Preview should become visible and ready to take a photo
		if (mCamera == null) {
			mCamera = getCameraInstance();
			mCamera.setDisplayOrientation(90);
			Log.d(TAG, "Camera opened");
			
			try {
    			mCamera.setPreviewDisplay(holder);
    			mCamera.startPreview();
	        } catch (IOException e) {
	            Log.e(TAG, "Error setting camera preview: " + e.getMessage());
	        }
		}
	}
	
	/** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera cam = null;
        try {
        	// Check if the device has a front-facing camera
        	if (Camera.getNumberOfCameras() > 1) {
        		cam = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        	} else {
        		// There is only one back-facing camera
        		cam = Camera.open(); // attempt to get a Camera instance
        	}
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        	Log.e(TAG, "Failed to get camera instance: " + e.getMessage());
        }
        return cam; // returns null if camera is unavailable
    }
    
    private void releaseCamera() {
        if (mCamera != null){
        	mCamera.setPreviewCallback(null);
        	mCamera.stopPreview();
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
    
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
		Log.d(TAG, "Creating camera preview surface...");
		initCamera(holder);
//        try {
//        	if (btEnabled) {
//        		if (mCamera != null) {
//        			mCamera.setPreviewDisplay(holder);
//        			mCamera.startPreview();
//        		}
//        	}
//        } catch (IOException e) {
//            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
//        }
    }
	
	public void surfaceDestroyed(SurfaceHolder holder) {
        // Empty. Take care of releasing the Camera preview in your activity.
		Log.d(TAG, "Destroying camera preview surface...");
    }
	
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
		
		Log.d(TAG, "Changing surface...");
		
        if (holder.getSurface() == null) {
          // preview surface does not exist
        	Log.d(TAG, "holder.getSurface() == null");
        	return;
        }

        mPreviewHolder = holder;
        
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mPreviewHolder);
            mCamera.startPreview();

        } catch (Exception e) {
            Log.e(TAG, "Error restarting camera preview: " + e.getMessage());
        }
    }
	
	/** Create a File for saving an image or video */
	private File getOutputMediaFile(int type){

	    // Create a media file name
	    //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	    	if (pictureCount > PHOTO_COUNT)
	    		pictureCount = 1;
	        mediaFile = new File(sessionDir.getPath() + File.separator + "IMG_"+ dirTimeStamp + "_" + pictureCount + ".jpg");
	        //Log.d(TAG, "File name: " + mediaFile.getPath());
	        pictureCount++;
	    /*} else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");*/
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	/*
	 * End camera-related methods
	 */
	
	/*
	 * Start location tracking methods
	 */
	
	// Method to launch Settings
    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }

    private final LocationListener locListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            // A new location update is received.  Do something useful with it.  Update the UI with
            // the location update.
            updateUILocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
    
    private void updateUILocation(Location location) {
    	// We're sending the update to a handler which then updates the UI with the new
    	// location.
    	Message.obtain(mLocHandler,
    			UPDATE_LATLNG,
    			"Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude()).sendToTarget();
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
    		.setNegativeButton("No", new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				dismiss();
    				runSensorCheck();
    			}
    		})
    		.create();
    	}
    	
    	// If the user chooses not to turn on the GPS (by pressing back)
    	// Then go straight to checking the Bluetooth status
    	@Override
    	public void onCancel(DialogInterface dialog) {
    		if (!mBTAdapter.isEnabled()) {
            	Log.d(TAG, "User chose not to enable GPS, now prompt for Bluetooth enablement");
            	new EnableBluetoothDialogFragment().show(getFragmentManager(), "enableBTDialog");
            }
    	}
    }

    /*
     * End location tracking methods
     */
    
    /*
	 * Start Bluetooth-related methods
	 */
    
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
    
    private void connectDevice(Intent data, boolean secure) {
    	Log.d(TAG, "Connecting to the device");
        // Get the device MAC address
        String address = data.getExtras().getString(BTDeviceList.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mBTService.connect(device, secure);
        Log.d(TAG, "Just ran mBTService.connect()");
        state = STATE_BT_CONNECTING;
//        state = STATE_RUN;
    }
    
    private void setupBTTransfer() {
    	Log.d(TAG, "Setup BT Transfer");
    	mBTService = new BTService(this, btHandler);
    }
    
    private float barr2float(byte[] barr, int offset, int len) {
		String str = new String(barr, offset, len);
		return Float.parseFloat(str);
	}
    
    private void writeIntoFile(float value) {
		try {
			long unixTime = (int) (System.currentTimeMillis() / 1000L);
			dataTimeStamp = stamp.format(unixTime);

			sensor_value.write(dataTimeStamp + "\t");

			sensor_value.write(String.valueOf(value));
			sensor_value.write("\r\n");			
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /*
     * End Bluetooth-related methods
     */

   /**
    * This is the thread on which all the IOIO activity happens. It will be run
    * every time the application is resumed and aborted when it is paused. The
    * method setup() will be called right after a connection with the IOIO has
    * been established (which might happen several times!). Then, loop() will
    * be called repetitively until the IOIO gets disconnected.
    */
	
	class IOIOThread extends AbstractIOIOActivity.IOIOThread {
		/** The on-board LED. */
		//private DigitalOutput led_;
		private DigitalOutput led_2;
		private DigitalOutput led_4;	// high/low pin for alcohol sensor

		
		private AnalogInput in_40;	//receive the sensor data
		

		
		
		/**
		 * Called every time a connection with IOIO has been established.
		 * Typically used to open pins.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#setup()
		 */
		@Override
		protected void setup() throws ConnectionLostException {
			
			led_2 = ioio_.openDigitalOutput(2, true);
			led_4 = ioio_.openDigitalOutput(4, true) ;	
			
			
			in_40 = ioio_.openAnalogInput(40);
	
			runOnUiThread(new Runnable() { 
		        public void run() 
		        {			
		        	//取得外部儲存媒體的狀態
					String state = Environment.getExternalStorageState();
					//判斷狀態
					if (Environment.MEDIA_MOUNTED.equals(state)) {
						storage_state.setText("can written");
					} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
						storage_state.setText("read only, can't written");
					} else {
						storage_state.setText("can't read and written");
					}
  
		        } 
		    });
			
			
			//calendar = Calendar.getInstance(); 
			//filename = calendar.get(Calendar.YEAR) +"_" +calendar.get(Calendar.MONTH)+ "_" + calendar.get(Calendar.DATE) 
			//			+ "_" + calendar.get(Calendar.HOUR_OF_DAY) + "_" + calendar.get(Calendar.MINUTE) 
			//			+ "_"+ calendar.get(Calendar.SECOND) + ".txt";
		
			
			
			
			
			//long unixTime = (int) (System.currentTimeMillis() / 1000L);
		
			//filename = stamp.format(unixTime);
			textfile = new File(sessionDir + File.separator + dirTimeStamp + ".txt");
						
			try {
				stream = new FileOutputStream(textfile);
				
				sensor_value = new OutputStreamWriter(stream, "US-ASCII");
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		
		}

		/**
		 * Called repetitively while the IOIO is connected.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#loop()
		 */
		
		
		
		@Override
		protected void loop() throws ConnectionLostException {
			try {
				if(isSensing) {	//turn on the alcohol sensor (default)
				  
					led_4.write(true);
					
					value = in_40.read();
					volts = in_40.getVoltage();
					calendar = Calendar.getInstance(); 
					
					if (volts < 3.1) {
					
						brac_value = ((-0.015) + Math.sqrt(0.015*0.015 - 4* (-0.00002)*(0.3326-volts)))/(2*(-0.00002));
						brac_value = brac_value/500;
						
						if (brac_value < 0)
							brac_value = 0;
					} else {
						brac_value = 0.7;
					}
					
					long unixTime = (int) (System.currentTimeMillis() / 1000L);
					dataTimeStamp = stamp.format(unixTime);
					
					//sensor_value.write(calendar.get(Calendar.HOUR_OF_DAY) + "_" + calendar.get(Calendar.MINUTE) 
					//		+ "_"+ calendar.get(Calendar.SECOND) + "_"+ calendar.get(Calendar.MILLISECOND) + "\t");
					sensor_value.write(dataTimeStamp + "\t");
					
					//sensor_value.write(String.valueOf(value) + "\t");
					sensor_value.write(String.valueOf(brac_value));
					sensor_value.write("\r\n");
					
					
					//--show the sensor info on the screen
					runOnUiThread(new Runnable() { 
				        public void run() 
				        {			
				        	row_value.setText("Row value: " + nf.format(value));
		  
				        	voltage.setText("Voltage: " + nf.format(volts));
				        	brac.setText("Breath Alcohol Concentration(mg/l): " + nf.format(brac_value));
				        	
				        } 
				    });
				} else if (doneSensing) {
					try {
	        			sensor_value.close();
	        			led_4.write(false);
	        			
	        		} catch (IOException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	        		}
				} else {
					//turn off
					led_4.write(false);
				}
				
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * A method to create our IOIO thread.
	 * 
	 * @see ioio.lib.util.AbstractIOIOActivity#createIOIOThread()
	 */
	@Override
	protected AbstractIOIOActivity.IOIOThread createIOIOThread() {
		return new IOIOThread();
	}
}