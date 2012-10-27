package ioio.examples.hello;

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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	public static final int PHOTO_COUNT = 3;
	
	private static final int MAX_PROGRESS = 6;
	
	//private ToggleButton button_;

//	private ToggleButton button_led_2;
//	private ToggleButton button_led_4;
	
	public ImageView imgCountdown;
//	public ImageView imgSensing;
	public AnimationDrawable countdownAnimation;
//	public AnimationDrawable sensingAnimation;
	
	private ProgressBar progSensing;
	private Handler mHandler, mLocHandler;
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
	private boolean mUseFine;
	private boolean mUseBoth;

    private static final int UPDATE_LATLNG = 2;
    
	private static final int TEN_SECONDS = 1000 * 10;
    private static final int TEN_METERS = 10;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
	
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
    	progSensing.setMax(MAX_PROGRESS);
	
		num = 100.0;
		nf = new DecimalFormat("0.000000");
		stamp = new DecimalFormat("0");
		row_value = (TextView)findViewById(R.id.row_value);
		voltage = (TextView)findViewById(R.id.voltage);
		brac = (TextView)findViewById(R.id.brac);
	
		storage_state = (TextView)findViewById(R.id.storage_state);
		
		long unixTime = System.currentTimeMillis() / 1000L;
		dirTimeStamp = stamp.format(unixTime);
		
		// To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)) {
			// SD card is mounted and ready for storage
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
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
	    
	    mCamera = getCameraInstance();

	    // Create our Preview view and set it as the content of our activity.
	    mPreview = new SurfaceView(this);
	    SurfaceHolder mPreviewHolder = mPreview.getHolder();
	    mPreviewHolder.addCallback(this);
	    mPreviewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	    //mPreview = new CameraPreview(this, mCamera);
	    FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
	    preview.addView(mPreview);

	    mCamera.setDisplayOrientation(90);

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
	    			Log.d(TAG, "File not found: " + e.getMessage());
	    		} catch (IOException e) {
	    			Log.d(TAG, "Error accessing file: " + e.getMessage());
	    		}

	    	}
	    };
	    
	    // Location Detection start
	    mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    mUseFine = true;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		// Check if the GPS setting is currently enabled on the device.
        // This verification should be done during onStart() because the system calls this method
        // when the user returns to the activity, which ensures the desired location provider is
        // enabled each time the activity resumes from the stopped state.
//        LocationManager locationManager =
//                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            // Build an alert dialog here that requests that the user enable
            // the location services, then when the user clicks the "OK" button,
            // call enableLocationSettings()
        	EnableGpsDialogFragment gpsDialog = new EnableGpsDialogFragment();
            gpsDialog.show(gpsDialog.getFragmentManager(), "enableGpsDialog");
        }
	}
	
	@Override
    public void onResume() {
    	super.onResume();
    	Log.d(TAG, "Resuming...");
    	if (mCamera == null) {
    		mCamera = getCameraInstance();
    		Log.d(TAG, "Camera reopened");
    	}
    	Log.d(TAG, "Start looking for location via GPS");
    	Location loc = null;
    	if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
    		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locListener);
    		loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	}
    	updateUILocation(loc);
    }
	
	@Override
    public void onPause() {
    	super.onPause();
    	Log.d(TAG, "Pausing...");
    	releaseCamera();
    	Log.d(TAG, "Camera is released");
    	mLocationManager.removeUpdates(locListener);
    	Log.d(TAG, "Location listener stopped updating");
    }
	
	private void findViews() {
		//button_ = (ToggleButton) findViewById(R.id.button);
//		button_led_2 = (ToggleButton) findViewById(R.id.button_led2);
//		button_led_4 = (ToggleButton) findViewById(R.id.button_led4);	//used to control alcohol sensor
    	imgCountdown = (ImageView) findViewById(R.id.imgCountdown);
//    	imgSensing = (ImageView) findViewById(R.id.imgSensing);
    	tvProgress = (TextView) findViewById(R.id.tvProgress);
    	tvSignal = (TextView) findViewById(R.id.tvSignal);
    	progSensing = (ProgressBar) findViewById(R.id.progSensing);
    	tvLatLng = (TextView) findViewById(R.id.tvLatLng);
    }
	
	@Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	super.onWindowFocusChanged(hasFocus);
    	// When the UI is focused start running the countdown animation
    	if (hasFocus) {
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
	        		doSenseProgress();
	        		new Thread (new Runnable() {
	        			@Override
	        			public void run() {
	        				for (int i = 0; i < PHOTO_COUNT; i++) {
	                			try {
	                				mCamera.takePicture(null, null, mPicture);
	        						Thread.sleep(1000);
	        					} catch (InterruptedException e) {
	        						Log.d(TAG, "Camera could not take picture: " + e.getMessage());
	        					}
	                    	}
	        			}
	        		}).start();
	        	}
		    };  
	        timer.schedule(timerTask, totalDuration);
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
			Log.d(TAG, "Error in countDown(): " + ie.getMessage());
		}
	}
	
	/*
	 * Start camera-related methods.
	 * Starts and releases the camera instances.
	 */
	
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
		Log.d(TAG, "Creating surface...");
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }
	
	public void surfaceDestroyed(SurfaceHolder holder) {
        // Empty. Take care of releasing the Camera preview in your activity.
		Log.d(TAG, "Destroying surface...");
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
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
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
		   .create();
	   }
   }

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