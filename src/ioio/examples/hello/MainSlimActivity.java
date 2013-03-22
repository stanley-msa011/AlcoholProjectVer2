package ioio.examples.hello;

import ioio.examples.bluetooth.BTDeviceList;
import ioio.examples.bluetooth.BTService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MainSlimActivity extends Activity implements SurfaceHolder.Callback {
	private final static String TAG = "IOIO";
	private final static boolean DEBUG_MODE = true;
	
	// State of the Activity
	public static final int STATE_SENSOR_CHECK = 0x00;
	public static final int STATE_BT_FINDING = 0x01;
	public static final int STATE_BT_CONNECTING = 0x02;
	public static final int STATE_RUN = 0x03;
	public static final int STATE_COMPLETE = 0x04;
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	public static final int PHOTO_COUNT = 3;
	
	private static int state;
	
	private Context mContext;
	
	public ImageView imgCountdown;
	public ImageView imgCountdown2;
	public AnimationDrawable countdownAnimation;
	
	private Handler mLocHandler;
	// The Handler that gets information back from the BluetoothChatService
    private Handler btHandler;
	
	private Camera mCamera;
	private SurfaceView mPreview;
	private SurfaceHolder mPreviewHolder = null;
	private PictureCallback mPicture;
	
	private File mainStorageDir;
	private File sessionDir;
	private String dirTimeStamp;
	private String dataTimeStamp;
	private int pictureCount = 1;
	
	
	DecimalFormat nf;
	DecimalFormat stamp;
	
	TextView row_value;
	TextView voltage;
	TextView storage_state;
	TextView brac;
	
	TextView tvSignal;

	File textfile;
	File geoFile;
	FileOutputStream stream, geoStream;
	OutputStreamWriter sensor_value;
	OutputStreamWriter geo_value;
	
	Calendar calendar;
	
	private Timer timer;
	private LocationManager mLocationManager;
	private Location mLoc = null;
	private TextView tvLatLng;
	private boolean deviceChecked;
	private boolean locationTrackPermitted;
	private boolean permissionChecked;

    private static final int UPDATE_LATLNG = 2;
    
	private static final int TWO_SECONDS = 1000 * 2;
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
    private static final int REQUEST_ENABLE_GPS = 4;
    
 // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
    private BluetoothAdapter mBTAdapter = null;
    private BTService mBTService;
    private float pressureReading1 = 0.f, pressureReading2 = 0.f;
    
    /*
     * Balloon load screen variables
     */
    private static final double NANO_TIME = 1000000000.0;
    private static final float BLOW_SENSE_THRESHOLD = 300.f;
    private static final float BLOW_THRESHOLD = 150.f;
    private static final double TOTAL_BLOW_DURATION = 5;
    private boolean uiRun = false;
    private ImageView ivBalloonLoader;
    private boolean isPeak = false;
    private int[] balloons = {
    		R.drawable.balloon0,
    		R.drawable.balloon1,
    		R.drawable.balloon2,
    		R.drawable.balloon3,
    		R.drawable.balloon4,
    		R.drawable.balloon5,
    		R.drawable.balloon6,
    		R.drawable.balloon7,
    		R.drawable.balloon8,
    		R.drawable.balloon9,
    		R.drawable.count_go_5,
    		R.drawable.count_go_4,
    		R.drawable.count_go_3,
    		R.drawable.count_go_2,
    		R.drawable.count_go_1,
    		R.drawable.count_end
    		};
    private Bitmap[] balloonsBM = new Bitmap[16];
    private int bDraw = 0;
    private long blowStartTime;
    private long blowEndTime;
    private double blowDuration = 0.0;
    private MediaPlayer mpBlowEnd;
    public static boolean mHasEnforcedStream = true;
    public final static int MAX_VOLUME = 100;
    public static int STREAM_SYSTEM_ENFORCED = 0;
    static {
    	try {
    		Class asClass = Class.forName("android.media.AudioSystem");
    		Field sseField = asClass.getDeclaredField("STREAM_SYSTEM_ENFORCED");
    		STREAM_SYSTEM_ENFORCED = sseField.getInt(null);
    	} catch (Exception e) {
    		Log.e(TAG, e.getMessage());
    		mHasEnforcedStream = false;
    	}
    }
    
	/**
	 * Called when the activity is first created. Here we normally initialize
	 * our GUI.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		findViews();
		mContext = this;
		// Set count down animation
        imgCountdown.setBackgroundResource(R.drawable.countdown);
    	countdownAnimation = (AnimationDrawable) imgCountdown.getBackground();
    	
    	mLocHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
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
                    if(DEBUG_MODE) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    break;
                case MESSAGE_WRITE:
                    break;
                case MESSAGE_READ:
                	if (!uiRun) {
                		uiRun = true;
            			
            			setupCamera();
            	    	textfile = new File(sessionDir + File.separator + dirTimeStamp + ".txt");
            	    	geoFile = new File(sessionDir + File.separator + "geo.txt");
            			
            			try {
            				stream = new FileOutputStream(textfile);
            				sensor_value = new OutputStreamWriter(stream, "US-ASCII");
            				
            			} catch (FileNotFoundException e1) {
            				e1.printStackTrace();
            			} catch (UnsupportedEncodingException e) {
            				e.printStackTrace();
            			}
            	    	
                		countdownAnimation.start();
            	    	
            	    	long totalDuration = 0;
            	        
            	    	for(int i = 0; i< countdownAnimation.getNumberOfFrames();i++){  
            	    		totalDuration += countdownAnimation.getDuration(i);  
            	        }
            	        
            	        timer = new Timer();
            	        TimerTask timerTask = new TimerTask(){  
            	        @Override
            		        public void run() {
            	        		Log.d(TAG, "Countdown animation is stopping");
            	        		runOnUiThread(new Runnable() { 
	        				        public void run() {
	        				        	imgCountdown.setVisibility(View.INVISIBLE);
	        				        	imgCountdown2.setVisibility(View.VISIBLE);
	        				        	imgCountdown2.setImageBitmap(balloonsBM[10]);
	        				        } 
	        				    });
            	        		new Thread (new Runnable() {
            	        			@Override
            	        			public void run() {
            	        				for (int i = 0; i < PHOTO_COUNT; i++) {
            	                			try {
            	                				mCamera.takePicture(null, null, mPicture);
            	        						Thread.sleep(1000);
            	        						mCamera.startPreview();
            	        						state = STATE_RUN;
            	        					} catch (InterruptedException e) {
            	        						Log.e(TAG, "Thread Interrupted Exception! Camera could not take picture: " + e.getMessage());
            	        					} catch (NullPointerException npe) {
            	        						Log.e(TAG, "Null Pointer Exception! Camera could not take picture: " + npe.getMessage());
            	        					}
            	                    	}
            	        			}
            	        		}).start();
            	        	}
            		    };
            	        timer.schedule(timerTask, totalDuration);
            	        if (locationTrackPermitted)
            	        	initLocationTrack();
            		}
                	
                	if (state == STATE_RUN) {
	                	String readMessage = (String) msg.obj;
	                	Log.d(TAG, "BT data: " + readMessage);
	                	
	                	if (readMessage.charAt(0) == 'm') {
	                		pressureReading1 = pressureReading2;
	                		try {
	                			pressureReading2 = Float.parseFloat(readMessage.substring(1));
	                			Log.d(TAG, "BT data Pressure reading 1: " + pressureReading1 + " Pressure reading 2: " + pressureReading2);
	                        	
	                        	float diff = pressureReading2 - pressureReading1;
	                        	Log.d(TAG, "BT data diff: " + diff);
	                        	if (diff > BLOW_SENSE_THRESHOLD && diff < 20000.f && !isPeak) {
	                        		isPeak = true;
	                        		blowStartTime = System.nanoTime();
	                        		if (bDraw < 18) {
	                        			bDraw++;
	                        		}
	                        		
	                        		// Save the alcohol reading
	                        	} else if (diff >= BLOW_THRESHOLD && diff < BLOW_SENSE_THRESHOLD && !isPeak) {
	                        		if (bDraw < 16) {
	                        			bDraw++;
	                        		}
	                        	} else if (diff >= -BLOW_SENSE_THRESHOLD && diff <= BLOW_SENSE_THRESHOLD) {
	                        		if (isPeak) {
	                        			// Save the alcohol reading
	                        			
	                        			blowEndTime = System.nanoTime();
	                        			blowDuration += (blowEndTime - blowStartTime) / NANO_TIME;
	                        			blowStartTime = blowEndTime;
	                        			imgCountdown2.setImageBitmap(balloonsBM[(int)blowDuration + 10]);
	                        			Log.d(TAG, "BT data Blow start time: " + blowStartTime + "Blow end time: " + blowEndTime);
	                        			Log.d(TAG, "BT data duration: " + blowDuration);
	                        			if (blowDuration > TOTAL_BLOW_DURATION) {
	                        				// User has blown the required amount of time
	                        				// Close the file and finish the activity
	                        				sensor_value.close();
	                        				writeLocationFile();
	                    				    mpBlowEnd.start();
	                    				    runOnUiThread(new Runnable() { 
	        	        				        public void run() {
	        	        				        	imgCountdown2.setImageBitmap(balloonsBM[15]);
	        	        				        }
	        	        				    });
	                        				Intent i_return = new Intent();
	                        				Bundle bData = new Bundle();
	                        				bData.putString("testfilename", dirTimeStamp);
	                        				i_return.putExtras(bData);
	                        				setResult(RESULT_OK, i_return);
	                        				state = STATE_COMPLETE;
	                        				finish();
	                        			} else {
	                        				if (bDraw < 18) {
	                        					bDraw++;
	                        				}
	                        			}
	                        			
	                        		} else {
	                        			if (bDraw > 0) {
	                        				bDraw--;
	                        			}
	                        		}
	                        	} else if (diff < -BLOW_SENSE_THRESHOLD) {
	                        		isPeak = false;
	                        		if (bDraw > 0) {
	                        			bDraw--;
	                        		}
	                        		blowStartTime = blowEndTime = 0;
	                        	}
	                        	runOnUiThread(new Runnable() { 
	        				        public void run() {
	        				        	ivBalloonLoader.setImageBitmap(balloonsBM[bDraw/2]);
	        				        } 
	        				    });
	                		} catch (Exception e) {
	                			Log.e(TAG, "ERROR! : " + e.getMessage());
	                		}
	                	} else if (readMessage.charAt(0) == 'a') {
	                		Log.d(TAG, "BT data Alcohol reading: " + readMessage.substring(1));
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
                	}
                    break;
                case MESSAGE_DEVICE_NAME:
                    break;
                case MESSAGE_TOAST:
                    break;
                }
            }
        };
        
        /*
         * Set variables for writing into data file
         */
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
	    
	    // Location Detection start
	    mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    
	    // Get local Bluetooth adapter
	    mBTAdapter = BluetoothAdapter.getDefaultAdapter();
	    
	    // If the adapter is null, then Bluetooth is not supported
        if (mBTAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        mpBlowEnd = MediaPlayer.create(this, R.raw.completed_beep);
        state = STATE_SENSOR_CHECK;
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		Log.d(TAG, "Restarting...");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "Starting...");
		if (DEBUG_MODE) Log.d(TAG, "State: " + printState());
	}
	
	@Override
    public void onResume() {
    	super.onResume();
    	Log.d(TAG, "Resuming...");
    	for (int i = 0; i < 16; i++) {
    		balloonsBM[i] = BitmapFactory.decodeResource(this.getResources(), balloons[i]);
    	}
    	switch (state) {
		case STATE_SENSOR_CHECK:
			// Check if the GPS setting is currently enabled on the device.
	        // This verification should be done during onStart() because the system calls this method
	        // when the user returns to the activity, which ensures the desired location provider is
	        // enabled each time the activity resumes from the stopped state.
			if (!deviceChecked) {
				deviceChecked = true;
				new CheckDeviceDialogFragment().show(getFragmentManager(), "checkDeviceDialog");
			} else {
				SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
		        if (!sp.getBoolean("enable_gps_check", true)) {
		        	Log.d(TAG, "Settings FALSE");
		        	locationTrackPermitted = false;
		        	permissionChecked = true;
		        }
				if (!permissionChecked)
					runPermissionCheck();
				else
					runSensorCheck();
			}
			break;
		case STATE_BT_FINDING:
			// Run location tracking after sensor checking is completed
			initLocationTrack();
			// Bluetooth needs to look for a device to connect to
			// We will set up Bluetooth Service and open BTDeviceList
			setupBTTransfer();
        	Intent serverIntent = new Intent(this, BTDeviceList.class);
        	startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
			break;
		case STATE_BT_CONNECTING:
			// The mobile device is trying to connect to the
			// Bluetooth device. Check if mBTService is connected yet.
			if (mBTService.getState() == BTService.STATE_NONE) {
				mBTService.start();
			}
			break;
    	}
    }
	
	@Override
    public void onPause() {
    	super.onPause();
    	Log.d(TAG, "Pausing...");
    	
    	releaseCamera();
    	Log.d(TAG, "Camera is released");
    	mLocationManager.removeUpdates(locListener);
    	Log.d(TAG, "Location listener stopped updating");
    	for (int i = 0; i < 16; i++) {
    		if (!balloonsBM[i].isRecycled()) {
    			balloonsBM[i].recycle();
    		}
    	}
    	if (state == STATE_COMPLETE || state == STATE_BT_CONNECTING) {
			mBTService.stop();
			mBTService = null;
			state = STATE_SENSOR_CHECK;
		} else if (state == STATE_RUN) {
			if (timer != null)
				timer.cancel();
			mBTService.stop();
			mBTService = null;
			state = STATE_SENSOR_CHECK;
		}
    }
	
	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "Stopping...");
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "Destroying...");
		mpBlowEnd.release();
	}
	
	/**
	 * Find all the view widgets necessary for your UI
	 */
	private void findViews() {
    	imgCountdown = (ImageView) findViewById(R.id.imgCountdown);
    	imgCountdown2 = (ImageView) findViewById(R.id.imgCountdown2);
    	tvLatLng = (TextView) findViewById(R.id.tvLatLng);
    	ivBalloonLoader = (ImageView) findViewById(R.id.ivBalloonLoader);
    }
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "On activity result");
		switch (requestCode) {
        case REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
            	Log.d(TAG, "Found devices to (securely) connect to");
                connectDevice(data, true);
            }
            break;
        case REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
            	Log.d(TAG, "Found devices to (insecurely) connect to");
                connectDevice(data, false);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
            	Log.d(TAG, "Returned from Bluetooth enablement");
                // Bluetooth is now enabled, now to look for the device to connect to
            	Toast.makeText(this, "藍牙已啟動", Toast.LENGTH_LONG).show();
            	state = STATE_BT_FINDING;
            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "User did not enable BT");
                Toast.makeText(this, "啟動藍牙失敗，本測試將停止。", Toast.LENGTH_LONG).show();
                finish();
            }
            break;
        case REQUEST_ENABLE_GPS:
        	if (!mBTAdapter.isEnabled()) {
            } else {
            	state = STATE_BT_FINDING;
            }
        	break;
        }
	}
	
	private void runSensorCheck() {
		if (locationTrackPermitted) {
			 if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				Log.d(TAG, "Showing Enable GPS Dialog");
				new EnableGpsDialogFragment().show(getFragmentManager(), "enableGpsDialog");
			} else {
				 if (!mBTAdapter.isEnabled()) {
					Log.d(TAG, "Showing Enable Bluetooth Dialog");
					new EnableBluetoothDialogFragment().show(getFragmentManager(), "enableBTDialog");
				 } else {
					 setupBTTransfer();
					 Intent serverIntent = new Intent(this, BTDeviceList.class);
					 startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
				 }
			}
		} else if (!mBTAdapter.isEnabled()) {
			Log.d(TAG, "Showing Enable Bluetooth Dialog");
			new EnableBluetoothDialogFragment().show(getFragmentManager(), "enableBTDialog");
		} else {
			setupBTTransfer();
        	Intent serverIntent = new Intent(this, BTDeviceList.class);
        	startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
		}
	}
	
	private void runPermissionCheck() {
			permissionChecked = true;
			Log.d(TAG, "Asking for location tracking permission");
			new TrackPermitDialogFragment().show(getFragmentManager(), "trackPermitDialog");
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
			if (Build.VERSION.SDK_INT >= 11) {
				mCamera.setDisplayOrientation(90);
			}
			Log.d(TAG, "Camera opened");
			
			try {
				Camera.Parameters params = mCamera.getParameters();
				params.setPictureSize(320, 240);
				mCamera.setParameters(params);
    			mCamera.setPreviewDisplay(holder);
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
        	if (Build.VERSION.SDK_INT > 11) {
	        	if (Camera.getNumberOfCameras() > 1) {
	        		cam = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
	        	} else {
	        		// There is only one back-facing camera
	        		cam = Camera.open(); // attempt to get a Camera instance
	        	}
        	} else {
        		cam = Camera.open();
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
	
	private void initLocationTrack() {
		Location gpsLocation = null;
		Location networkLocation = null;
		gpsLocation = requestUpdatesFromProvider(LocationManager.GPS_PROVIDER);
		networkLocation = requestUpdatesFromProvider(LocationManager.NETWORK_PROVIDER);
		
		// If both providers return last known locations, compare the two and use the better
        // one to update the UI.  If only one provider returns a location, use it.
        if (gpsLocation != null && networkLocation != null) {
        	mLoc = getBetterLocation(gpsLocation, networkLocation);
            updateUILocation(mLoc);
        } else if (gpsLocation != null) {
        	mLoc = gpsLocation;
            updateUILocation(mLoc);
        } else if (networkLocation != null) {
        	mLoc = networkLocation;
            updateUILocation(mLoc);
        }
	}
	
	/** Determines whether one Location reading is better than the current Location fix.
	 * Code taken from
	 * http://developer.android.com/guide/topics/location/obtaining-user-location.html
	 *
	 * @param newLocation  The new Location that you want to evaluate
	 * @param currentBestLocation  The current Location fix, to which you want to compare the new
	 *        one
	 * @return The better Location object based on recency and accuracy.
	 */
	protected Location getBetterLocation(Location newLocation, Location currentBestLocation) {
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
	
	/** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
          return provider2 == null;
        }
        return provider1.equals(provider2);
    }
	
	private Location requestUpdatesFromProvider(final String provider) {
        Location location = null;
        if (mLocationManager.isProviderEnabled(provider)) {
            mLocationManager.requestLocationUpdates(provider, TWO_SECONDS, TEN_METERS, locListener);
            location = mLocationManager.getLastKnownLocation(provider);
        }
        return location;
    }
	
	private void writeLocationFile() {
		try {
			geoStream = new FileOutputStream(geoFile);
			geo_value = new OutputStreamWriter(geoStream);
			if (mLoc != null) {
				geo_value.write(String.valueOf(mLoc.getLatitude()) + "\t" + String.valueOf(mLoc.getLongitude()));
			} else {
				geo_value.write("-1\t-1");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (geo_value != null) {
				try {
					geo_value.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	// Method to launch Settings
    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(settingsIntent, REQUEST_ENABLE_GPS);
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
    		.setNegativeButton(R.string.dont_enable_gps, new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				if (!mBTAdapter.isEnabled()) {
    	            	Log.d(TAG, "User chose not to enable GPS, now prompt for Bluetooth enablement");
    	            	new EnableBluetoothDialogFragment().show(getFragmentManager(), "enableBTDialog");
    	            }
    				dismiss();
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
    
    /**
	 * Dialog to ask for permission to track user location
	 */
    private class TrackPermitDialogFragment extends DialogFragment {
    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		return new AlertDialog.Builder(getActivity())
    		.setTitle(R.string.permit_track_title)
    		.setMessage(R.string.permit_track_dialog)
    		.setPositiveButton(R.string.permit_track, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					locationTrackPermitted = true;
					runSensorCheck();
				}
			})
			.setNegativeButton(R.string.dont_permit_track, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					locationTrackPermitted = false;
					runSensorCheck();
				}
			})
			.create();
    	}
    	
    	// If user cancels this dialog (by pressing back)
    	// Assume the same as declining to share location (negative button)
    	@Override
    	public void onCancel(DialogInterface dialog) {
    		locationTrackPermitted = false;
			runSensorCheck();
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
    }
    
    private void setupBTTransfer() {
    	Log.d(TAG, "Setup BT Transfer");
    	mBTService = new BTService(this, btHandler);
    }
    
    /*
     * End Bluetooth-related methods
     */
    
    public class CheckDeviceDialogFragment extends DialogFragment {
    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		return new AlertDialog.Builder(getActivity())
    		.setTitle(R.string.check_device_title)
    		.setMessage(R.string.check_device_dialog)
    		.setPositiveButton(R.string.check_device, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(mContext);
			        if (!sp.getBoolean("enable_gps_check", true)) {
			        	Log.d(TAG, "Settings FALSE");
			        	locationTrackPermitted = false;
			        	permissionChecked = true;
			        }
					if (!permissionChecked)
						runPermissionCheck();
					else
						runSensorCheck();
				}
			}).create();
    	}
    	
    	// If user cancels this dialog (by pressing back)
    	// Leave the activity. Assume device is not turned on.
    	@Override
    	public void onCancel(DialogInterface dialog) {
    		finish();
    	}
    }
    
    
    private String printState() {
    	switch (state) {
    	case 0:
    		return "STATE_SENSOR_CHECK";
    	case 1:
    		return "STATE_BT_FINDING";
    	case 2:
    		return "STATE_BT_CONNECTING";
    	case 3:
    		return "STATE_RUN";
    	case 4:
    		return "STATE_COMPLETE";
    	default:
    		return String.valueOf(state);
    	}
    }
}