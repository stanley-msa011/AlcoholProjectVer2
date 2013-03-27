package ioio.examples.hello;

import java.io.File;
import java.text.DecimalFormat;

import test.bluetooth.BTInitTask;
import test.bluetooth.BTRunTask;
import test.bluetooth.Bluetooth;
import test.camera.CameraInitTask;
import test.camera.CameraRecorder;
import test.camera.CameraRunHandler;
import test.data.BracDataHandler;
import test.file.BracValueFileHandler;
import test.file.ImageFileHandler;
import test.gps.GPSInitTask;
import test.gps.GPSRunTask;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class TestFragment extends Fragment {

	private Activity context;
	private TestFragment testFragment;
	private View view;
	private String timestamp;
	
	private final boolean[] INIT_PROGRESS={false,false,false};
	private final boolean[] DONE_PROGRESS={false,false,false};
	
	public static final int _GPS=0;
	public static final int _BT = 1;
	public static final int _CAMERA = 2;
	
	//GPS
	private LocationManager locationManager;
	private GPSInitTask gpsInitTask;
	private GPSRunTask gpsRunTask;
	private boolean gps_state = false;
	
	//Bluetooth
	private Bluetooth bt;
	private BTInitTask btInitTask;
	private BTRunTask btRunTask;
	
	//Camera
	private CameraInitTask cameraInitTask;
	private CameraRecorder cameraRecorder;
	private CameraRunHandler cameraRunHandler;
	
	//File
	private File mainDirectory;
	private BracValueFileHandler bracFileHandler;
	private ImageFileHandler imgFileHandler;
	
	//Uploader
	private BracDataHandler BDH;
	
	private Button gps_enable_button,gps_disable_button,end_button,move_to_statistic_button;
	
	
	private static Object init_lock  = new Object();
	private static Object done_lock  = new Object();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void onPause(){
		super.onPause();
		stop();
	}
	
	public void onResume(){
		super.onResume();
		context = this.getActivity();
		testFragment = this;
	}
	
	
	
	public void reset(){
		timestamp = setTimeStamp();
		setStorage();
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		cameraRecorder = new CameraRecorder(testFragment,imgFileHandler);
		cameraRunHandler = new CameraRunHandler(cameraRecorder);
		bt = new Bluetooth(testFragment,cameraRunHandler,bracFileHandler);
	}
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	view = inflater.inflate(R.layout.test_fragment, container,false);
    	setViews();
    	return view;
    }
	
	void setViews(){
		gps_enable_button = (Button) view.findViewById(R.id.GPS_enable);
		gps_disable_button = (Button) view.findViewById(R.id.GPS_disable);
		end_button = (Button) view.findViewById(R.id.end_test);
		GPSOnClickListener  gpsOnclickListener= new GPSOnClickListener();
		gps_enable_button.setOnClickListener(gpsOnclickListener);
		gps_disable_button.setOnClickListener(gpsOnclickListener);
		end_button.setOnClickListener(new EndTestOnClickListener());
		
		move_to_statistic_button = (Button) view.findViewById(R.id.move_to_statistic);
		move_to_statistic_button.setOnClickListener(new MoveToStatisticOnClickListener());
	}
	
	private class GPSOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			reset();
			for (int i=0;i<3;++i)
				INIT_PROGRESS[i]=DONE_PROGRESS[i]=false;
			
			if (v.getId() == R.id.GPS_enable){
				Log.d("GPS","ENABLE GPS");
				gps_state = true;
				Object[] gps_enable={gps_state};
				gpsInitTask = new GPSInitTask(testFragment,locationManager);
				gpsInitTask.execute(gps_enable);
			}
			else if (v.getId() == R.id.GPS_disable){
				gps_state = false;
				updateGPSInitState();
			}
		}
	}
	
	private class EndTestOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			stop();
		}
		
	}
	
	private class MoveToStatisticOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			FragmentTabs.changeTab(0);
		}
	}
	
	String setTimeStamp(){
		long time_in_sec = System.currentTimeMillis() / 1000L;
		DecimalFormat d = new DecimalFormat("0");
		return d.format(time_in_sec);
	}
	
	void setStorage(){
		String state = Environment.getExternalStorageState();
		File dir = null;
		if (state.equals(Environment.MEDIA_MOUNTED)){
			dir = new File(Environment.getExternalStorageDirectory(),"drunk_detection");
			Log.d("TEST_STORAGE","MEDIA");
		} 
		else{
			dir = new File(this.getActivity().getFilesDir(),"drunk_detection");
			Log.d("TEST_STORAGE","NO MEDIA");
		}
		String path = dir.getAbsolutePath();
		Log.d("TEST_STORAGE","PATH: "+path);
		if (!dir.exists()){
			Log.d("TEST_STORAGE","NO DIR");
			if (!dir.mkdirs())
				Log.d("TEST_STORAGE","FAIL TO CREATE DIR");
		}
		
		mainDirectory = new File(dir,timestamp);
		if (!mainDirectory.exists())
			if (!mainDirectory.mkdirs()){
				Log.e("TEST_STORAGE", "CANNOT CREATE DIRECTORY");
				return;
			}
		
		bracFileHandler = new BracValueFileHandler(mainDirectory,timestamp);
		imgFileHandler = new ImageFileHandler(mainDirectory,timestamp);
		
	}
	
	public void updateGPSInitState(){
		synchronized(init_lock){
			Log.d("INIT","GPS INIT END");
			if (gps_state){
				gpsInitTask.cancel(true);
				Object[] gps_enable={gps_state};
				gpsRunTask = new GPSRunTask(this,locationManager,mainDirectory);
				gpsRunTask.execute(gps_enable);
			}
			else{
				updateDoneState(_GPS);
			}
			
			//initialize bt task after gps init task
			
			btInitTask = new BTInitTask(testFragment,bt);
			btInitTask.execute();
			Log.d("INIT","BT TASK STARTED");
			
			//initialize camera task after gps init task
			cameraInitTask = new CameraInitTask(testFragment,cameraRecorder);
			cameraInitTask.execute();
		}
	}
	
	public void updateInitState(int type){
		synchronized(init_lock){
			if (INIT_PROGRESS[type]==true)
				return;
			INIT_PROGRESS[type]=true;
			String init_str=INIT_PROGRESS[0]+"/"+INIT_PROGRESS[1]+"/"+INIT_PROGRESS[2];
			Log.d("INIT",init_str);
			
			if (INIT_PROGRESS[_BT] && INIT_PROGRESS[_CAMERA]){
				btInitTask.cancel(true);
				cameraInitTask.cancel(true);
				btRunTask = new BTRunTask(this,bt);
				btRunTask.execute();
				
				showStart();
			}
		}
	}
	
	public void updateDoneState(int type){
		synchronized(done_lock){
			if (DONE_PROGRESS[type]==true)
				return;
			DONE_PROGRESS[type]=true;
			String init_str=DONE_PROGRESS[0]+"/"+DONE_PROGRESS[1]+"/"+DONE_PROGRESS[2];
			Log.d("DONE",init_str);
			
			if (DONE_PROGRESS[0]&&DONE_PROGRESS[1]&&DONE_PROGRESS[2]){
				Log.d("NEW MAIN","ALL PROGRESS DONE");
				stop();
				BDH = new BracDataHandler(timestamp, testFragment);
				int bdh_result = BDH.start();
				if (bdh_result == BDH.ERROR){
					//Show error message
				}
				else{
					double result = BDH.getResult();
					Log.d("TEST RESULT",String.valueOf(result));
					//Show success or fail message
				}
			}
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode==_GPS){
			updateGPSInitState();
		}
	}
	
	public void showStart(){
		TextView bt_view = (TextView) this.getActivity().findViewById(R.id.bt_value);
		bt_view.setText("Start");
	}
	
	public void stop(){
		Log.d("STOP","STOP");
		if (btRunTask!=null){
			bt.close();
			btRunTask.cancel(true);
			btRunTask = null;
		}
		if (gpsRunTask!=null){
			gpsRunTask.close();
			gpsRunTask.cancel(true);
			gpsRunTask = null;
		}
			
		if (cameraRecorder!=null)
			cameraRecorder.close();
	}
	
}
