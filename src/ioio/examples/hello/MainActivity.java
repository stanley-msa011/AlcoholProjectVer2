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

import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

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
	
	//private ToggleButton button_;

//	private ToggleButton button_led_2;
//	private ToggleButton button_led_4;
	
	public ImageView imgCountdown;
	public ImageView imgSensing;
	public AnimationDrawable countdownAnimation;
	public AnimationDrawable sensingAnimation;
	
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
	
	private float value;
	private float volts;
	private double brac_value;
	
	private boolean isSensing = false;
	private boolean isWriting = false;

	//String filename;
	File textfile;
	FileOutputStream stream;
//	String path;
//	String path2;
//	String path_current;
	OutputStreamWriter sensor_value;
	
	Calendar calendar;
	
	
	
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
        imgSensing.setBackgroundResource(R.drawable.sensing);
    	countdownAnimation = (AnimationDrawable) imgCountdown.getBackground();
    	sensingAnimation = (AnimationDrawable) imgSensing.getBackground();
	
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
	}
	
	@Override
    public void onResume() {
    	super.onResume();
    	Log.d(TAG, "Resuming...");
    	if (mCamera == null) {
    		mCamera = getCameraInstance();
    		Log.d(TAG, "Camera reopened");
    	}
    }
	
	@Override
    public void onPause() {
    	super.onPause();
    	Log.d(TAG, "Pausing...");
    	releaseCamera();
    	Log.d(TAG, "Camera is released");
    }
	
	private void findViews() {
		//button_ = (ToggleButton) findViewById(R.id.button);
//		button_led_2 = (ToggleButton) findViewById(R.id.button_led2);
//		button_led_4 = (ToggleButton) findViewById(R.id.button_led4);	//used to control alcohol sensor
    	imgCountdown = (ImageView) findViewById(R.id.imgCountdown);
    	imgSensing = (ImageView) findViewById(R.id.imgSensing);
    }
	
	@Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	super.onWindowFocusChanged(hasFocus);
    	// When the UI is focused start running the countdown animation
    	countdownAnimation.start();
    	
    	long totalDuration = 0;  
        
    	for(int i = 0; i< countdownAnimation.getNumberOfFrames();i++){  
    		totalDuration += countdownAnimation.getDuration(i);  
        }
        
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask(){  
        @Override  
	        public void run() {
        		Log.d(TAG, "Animation is stopping");
        		isSensing = true;
        		Log.d(TAG, "isSensing state: " + isSensing);
        	}
	    };  
        timer.schedule(timerTask, totalDuration);
        
    }
	
	/** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera cam = null;
        try {
            cam = Camera.open(); // attempt to get a Camera instance
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
	    	if (pictureCount > 5)
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
			led_4 = ioio_.openDigitalOutput(4, DigitalOutput.Spec.Mode.OPEN_DRAIN, true) ;	
			
			
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
			//led_.write(!button_.isChecked());
			//led_2.write(!button_led_2.isChecked());
			
			//led_4.write(!button_led_4.isChecked());
			

			
			
			
			try {
				if(isSensing) {	//turn on the alcohol sensor (default)
					
					if (!sensingAnimation.isRunning()) {
						sensingAnimation.start();
						
						long totalDuration = 0;  
				        
				    	for(int i = 0; i< sensingAnimation.getNumberOfFrames();i++){  
				    		totalDuration += sensingAnimation.getDuration(i);  
				        }
				        
				        Timer timer = new Timer();
				        TimerTask timerTask = new TimerTask(){  
				        @Override  
					        public void run() {
				        		Log.d(TAG, "Sensing animation is stopping");
				        		isSensing = false;
				        		try {
				        			sensor_value.close();
				        		} catch (IOException e) {
				    				// TODO Auto-generated catch block
				    				e.printStackTrace();
				        		}
				        	}
					    };  
				        timer.schedule(timerTask, totalDuration);
					}
				  
					led_4.write(true);
					
					value = in_40.read();
					volts = in_40.getVoltage();
					calendar = Calendar.getInstance(); 
					
					brac_value = Math.exp((double)(volts - 0.5706)/1.6263);
					brac_value = brac_value * 0.002;
				
					long unixTime = (int) (System.currentTimeMillis() / 1000L);
					dataTimeStamp = stamp.format(unixTime);
					
					//sensor_value.write(calendar.get(Calendar.HOUR_OF_DAY) + "_" + calendar.get(Calendar.MINUTE) 
					//		+ "_"+ calendar.get(Calendar.SECOND) + "_"+ calendar.get(Calendar.MILLISECOND) + "\t");
					sensor_value.write(dataTimeStamp + "\t");
					
					sensor_value.write(String.valueOf(value) + "\t");
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
					
				} else {
					//turn off
					led_4.write(false);
				}
			
				
				
					
					
//				if(isSensing) {		//--file is opened to write (default)
//	
//					long unixTime = (int) (System.currentTimeMillis() / 1000L);
//					dataTimeStamp = stamp.format(unixTime);
//					
//					//sensor_value.write(calendar.get(Calendar.HOUR_OF_DAY) + "_" + calendar.get(Calendar.MINUTE) 
//					//		+ "_"+ calendar.get(Calendar.SECOND) + "_"+ calendar.get(Calendar.MILLISECOND) + "\t");
//					sensor_value.write(dataTimeStamp + "\t");
//					
//					sensor_value.write(String.valueOf(value) + "\t");
//					sensor_value.write(String.valueOf(brac_value));
//					sensor_value.write("\r\n");
//					
//					
//					//--show the sensor info on the screen
//					runOnUiThread(new Runnable() { 
//				        public void run() 
//				        {			
//				        	row_value.setText("Row value: " + nf.format(value));
//		  
//				        	voltage.setText("Voltage: " + nf.format(volts));
//				        	brac.setText("Breath Alcohol Concentration(mg/l): " + nf.format(brac_value));
//				        	
//				        } 
//				    });
//					
//				}
//				
//				if(!isWriting) {	//--close and save the file
//					
//					sensor_value.close();
//					
//					runOnUiThread(new Runnable() { 
//						public void run() 
//				        {			
//				        	row_value.setText("file saved");
//		  
//				        	
//				        } 
//				    });
//					
//				}
				
				
				
				
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			//led_2.write(true);	//for testing the IOIO is working
			
			//		sleep(1000);
			//		led_2.write(false);
			//		sleep(1000);
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