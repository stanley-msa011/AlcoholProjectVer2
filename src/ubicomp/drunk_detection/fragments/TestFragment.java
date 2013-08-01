package ubicomp.drunk_detection.fragments;


import java.io.File;
import java.text.DecimalFormat;

import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.activities.TutorialActivity;
import ubicomp.drunk_detection.ui.LoadingBox;
import ubicomp.drunk_detection.ui.Typefaces;

import test.bluetooth.BTInitHandler;
import test.bluetooth.BTRunTask;
import test.bluetooth.Bluetooth;
import test.bluetooth.BluetoothDebugMode;
import test.bluetooth.BluetoothDebugModeNormal;
import test.camera.CameraInitHandler;
import test.camera.CameraRecorder;
import test.camera.CameraRunHandler;
import test.data.BracDataHandler;
import test.data.BracDataHandlerDebugMode;
import test.data.BracDataHandlerDebugModeNormal;
import test.data.BracValueDebugHandler;
import test.data.BracValueFileHandler;
import test.data.ImageFileHandler;
import test.data.QuestionFile;
import test.gps.GPSInitTask;
import test.gps.GPSRunTask;
import test.ui.TestQuestionMsgBox;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLoggerLog;

public class TestFragment extends Fragment {

	private Activity activity;
	private TestFragment testFragment;
	private View view;
	private TextView messageView;
	private String timestamp;
	
	private final boolean[] INIT_PROGRESS={false,false,false};
	private final boolean[] DONE_PROGRESS={false,false,false};
	
	public static final int _GPS=0;
	public static final int _BT = 1;
	public static final int _CAMERA = 2;
	
	private boolean keepMsgBox;
	
	//GPS
	private LocationManager locationManager;
	private GPSInitTask gpsInitTask;
	private GPSRunTask gpsRunTask;
	private boolean gps_state = false;
	
	//Bluetooth
	private Bluetooth bt;
	private BTInitHandler btInitHandler;
	private BTRunTask btRunTask;
	
	//Camera
	private CameraInitHandler cameraInitHandler;
	private CameraRecorder cameraRecorder;
	private CameraRunHandler cameraRunHandler;
	
	//File
	private File mainDirectory;
	private BracValueFileHandler bracFileHandler;
	private ImageFileHandler imgFileHandler;
	private BracValueDebugHandler bracDebugHandler;
	
	//Uploader
	private BracDataHandler BDH;
	
	private RelativeLayout main_layout;
	private TestQuestionMsgBox msgBox;

	private LoadingHandler loadingHandler;
	private FailBgHandler failBgHandler;
	private MsgLoadingHandler msgLoadingHandler;
	private TestHandler testHandler;
	private TimeUpHandler timeUpHandler;
	private ChangeTabsHandler changeTabsHandler;
	private CountDownHandler countDownHandler;
	
	private RelativeLayout startLayout;
	private ImageView bg, startButton;
	private TextView bracText;
	private TextView brac;
	private TextView startText;
	
	
	private FrameLayout preview_layout;
	
	private RelativeLayout helpLayout;
	private ImageView helpButton;
	
	private ImageView testCircle;
	
	private static Object init_lock  = new Object();
	private static Object done_lock  = new Object();
	
	private EditText debugMsg;
	private ChangeMsgHandler msgHandler;
	
	private static final int[] BLOW_RESOURCE = {0,R.drawable.test_circle1,R.drawable.test_circle2,R.drawable.test_circle3,R.drawable.test_circle4,R.drawable.test_circle5,R.drawable.test_circle6};
	private Drawable[] blowDrawables;
	
	private ImageView face;
	
	private QuestionFile questionFile;
	
	private Typeface digitTypeface;
	private Typeface wordTypefaceBold;
	
	private DecimalFormat format;
	
	private Toast startToast;
	
	private Point screen;
	
	private int count_down_sec = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		keepMsgBox = false;
		format = new DecimalFormat();
		format.setMaximumIntegerDigits(1);
		format.setMinimumIntegerDigits(1);
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);
		digitTypeface = Typefaces.getDigitTypeface(getActivity());
		wordTypefaceBold  = Typefaces.getWordTypefaceBold(getActivity());
		screen = FragmentTabs.getSize();
	}
	
	public void onPause(){
		if (!isKeepMsgBox()){
			stop();
			clear();
		}
		super.onPause();
	}
	
	public void onResume(){
		super.onResume();
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		checkDebug2(sp.getBoolean("debug", false),sp.getBoolean("debug_type", false));
		if (!isKeepMsgBox()){
			activity = this.getActivity();
			testFragment = this;
			settingOnResume();
			loadingHandler.sendEmptyMessage(0);
		}
		else{
			setKeepMsgBox(false);
			runGPS();
		}
	}
	private void settingOnResume(){
		if (msgBox==null)
			msgBox = new TestQuestionMsgBox(testFragment,main_layout);
		loadingHandler = new LoadingHandler();
		msgLoadingHandler = new MsgLoadingHandler();
		failBgHandler = new FailBgHandler();
		testHandler = new TestHandler();
		timeUpHandler = new TimeUpHandler();
		changeTabsHandler = new ChangeTabsHandler(); 
		countDownHandler = new CountDownHandler();
	}
	
	public void reset(){
		checkDebug();
		timestamp = setTimeStamp();
		setStorage();
		locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		cameraRecorder = new CameraRecorder(testFragment,imgFileHandler);
		cameraRunHandler = new CameraRunHandler(cameraRecorder);
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		Boolean debug = sp.getBoolean("debug", false);
		Boolean debug_type = sp.getBoolean("debug_type", false);
		if (debug){
			if (debug_type)
				bt = new BluetoothDebugModeNormal(testFragment,cameraRunHandler,bracFileHandler,bracDebugHandler);
			else
				bt = new BluetoothDebugMode(testFragment,cameraRunHandler,bracFileHandler,bracDebugHandler);
		}
		else
			bt = new Bluetooth(testFragment,cameraRunHandler,bracFileHandler,bracDebugHandler);
		for (int i=0;i<3;++i)
			INIT_PROGRESS[i]=DONE_PROGRESS[i]=false;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	view = inflater.inflate(R.layout.test_fragment, container,false);
    	bg = (ImageView) view.findViewById(R.id.test_background);
		startLayout = (RelativeLayout) view.findViewById(R.id.test_start_layout);
		startButton = (ImageView) view.findViewById(R.id.test_start_button);
		bracText =(TextView) view.findViewById(R.id.test_brac_value_text);
		brac = (TextView) view.findViewById(R.id.test_brac_text);
		startText = (TextView) view.findViewById(R.id.test_start_button_text);
		
		helpLayout = (RelativeLayout) view.findViewById(R.id.help_layout);
		helpButton = (ImageView) view.findViewById(R.id.help_background);
		
		face = (ImageView) view.findViewById(R.id.test_face);
		
		testCircle = (ImageView) view.findViewById(R.id.test_start_circle);
		
		main_layout = (RelativeLayout) view.findViewById(R.id.test_fragment_main_layout);
		
		bracText.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 75/480);
		bracText.setTypeface(digitTypeface);
		
		startText.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 42/480);
		startText.setTypeface(wordTypefaceBold);
		
		brac.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 22/480);
		brac.setTypeface(wordTypefaceBold);
		
		messageView = (TextView) view.findViewById(R.id.test_message);
		messageView.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 22/480);
		messageView.setTypeface(wordTypefaceBold);
		
		RelativeLayout.LayoutParams mParam = (LayoutParams) messageView.getLayoutParams();
		mParam.topMargin = screen.x * 40/480;
		
		preview_layout = (FrameLayout) view.findViewById(R.id.test_camera_preview_layout);

		debugMsg = (EditText) view.findViewById(R.id.debug_msg);
		
		RelativeLayout.LayoutParams bParam = (RelativeLayout.LayoutParams)bg.getLayoutParams();
		bParam.width = screen.x;
		bParam.height = bParam.width*1709/1080;
		
		RelativeLayout.LayoutParams startLayoutParam = (LayoutParams) startLayout.getLayoutParams();
		startLayoutParam.topMargin = screen.x * 192/480;
		startLayoutParam.width = startLayoutParam.height = screen.x * 255/480;
		
		RelativeLayout.LayoutParams previewParam = (LayoutParams) preview_layout.getLayoutParams();
		previewParam.width =screen.x *254/480;
		previewParam.height = screen.x * 254/480;
		previewParam.topMargin = screen.x * 180/480;
		
		RelativeLayout.LayoutParams helpLayoutParam = (LayoutParams) helpLayout.getLayoutParams();
		helpLayoutParam.topMargin =screen.x * 26/480;
		helpLayoutParam.rightMargin = screen.x * 26/480;
		
		RelativeLayout.LayoutParams bracVParam = (LayoutParams) bracText.getLayoutParams();
		bracVParam.topMargin =screen.x * 95/480;
		
		RelativeLayout.LayoutParams bracParam = (LayoutParams) brac.getLayoutParams();
		bracParam.topMargin =screen.x * 18/480;
		
    	return view;
    }
	
	public void startGPS(boolean enable){
		msgBox.generateInitializingBox();
		if (enable){
			gps_state = true;
			Object[] gps_enable={gps_state};
			gpsInitTask = new GPSInitTask(testFragment,locationManager);
			gpsInitTask.execute(gps_enable);
		}
		else{
			gps_state = false;
			runGPS();
		}
	}
	
	public void runGPS(){
		if (gps_state){
			gpsInitTask.cancel(true);
			gpsRunTask = new GPSRunTask(this,locationManager,mainDirectory);
			Object[] a = {gps_state};
			gpsRunTask.execute(a);
		}
		else
			updateDoneState(_GPS);
	}
	
	public void startBT(){
		messageView.setText(R.string.wait);
		//initialize bt task
		btInitHandler = new BTInitHandler(testFragment,bt);
		btInitHandler.sendEmptyMessage(0);
		//initialize camera task
		cameraInitHandler = new CameraInitHandler(testFragment,cameraRecorder);
		cameraInitHandler.sendEmptyMessage(0);
	}
	
	public void runBT(){
		if (testHandler==null)
			testHandler = new TestHandler();
		testHandler.sendEmptyMessage(0);
	}
	
	public void failBT(){
		messageView.setText("");
		clear();
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("msg",getString(R.string.test_guide_not_turn_on));
		msg.setData(data);
		msg.what = 0;
		if (failBgHandler!=null)
			failBgHandler.sendMessage(msg);
	}
	
	private class StartOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			
			ClickLoggerLog.Log(getActivity(), ClickLogId.TEST_START_BUTTON);
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(getActivity());
			boolean firstTime = sp.getBoolean("first", true);
			helpButton.setOnClickListener(null);
			helpButton.setOnLongClickListener(null);
			if (firstTime){
				messageView.setText("");
				SharedPreferences.Editor editor = sp.edit();
				editor.putBoolean("first", false);
				editor.commit();
				showTutorial();
			}
			else{
				long lastTime = sp.getLong("LatestTestTime", 0);
				long curTime = System.currentTimeMillis();
				if (curTime - lastTime > 120000){
					startButton.setOnClickListener(null);
					startButton.setVisibility(View.INVISIBLE);
					startText.setText("");
					bracText.setText("0.00");
					reset();
					messageView.setText(R.string.test_guide_show_turn_on);
					Thread t = new Thread(new TimeUpRunnable(0,1500));
					t.start();
				}else{
					if (startToast !=null)
						startToast.cancel();
					startToast = Toast.makeText(getActivity(), R.string.testTimeCheckToast, Toast.LENGTH_SHORT);
					startToast.show();
				}
			}
		}
	}
	
	private class EndTestOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLoggerLog.Log(getActivity(), ClickLogId.TEST_RESTART_BUTTON);
			stopDueToInit();
			if (loadingHandler!=null)
				loadingHandler.sendEmptyMessage(0);
		}
	}
	
	private String setTimeStamp(){
		long time_in_sec = System.currentTimeMillis();
		DecimalFormat d = new DecimalFormat("0");
		return d.format(time_in_sec/1000L);
	}
	
	private void setStorage(){
		String state = Environment.getExternalStorageState();
		File dir = null;
		if (state.equals(Environment.MEDIA_MOUNTED))
			dir = new File(Environment.getExternalStorageDirectory(),"drunk_detection");
		else
			dir = new File(this.getActivity().getFilesDir(),"drunk_detection");
		if (!dir.exists())
			if (!dir.mkdirs())
				Log.d("TEST STORAGE","FAIL TO CREATE DIR");
		
		mainDirectory = new File(dir,timestamp);
		if (!mainDirectory.exists())
			if (!mainDirectory.mkdirs()){
				return;
			}
		
		bracFileHandler = new BracValueFileHandler(mainDirectory,timestamp);
		bracDebugHandler = new BracValueDebugHandler(mainDirectory,timestamp);
		imgFileHandler = new ImageFileHandler(mainDirectory,timestamp);
		questionFile = new QuestionFile(mainDirectory);
	}
	
	public void updateInitState(int type){
		synchronized(init_lock){
			if (INIT_PROGRESS[type]==true)
				return;
			INIT_PROGRESS[type]=true;
			if (INIT_PROGRESS[_BT] && INIT_PROGRESS[_CAMERA]){
				btInitHandler.removeMessages(0);
				cameraInitHandler.removeMessages(0);
				btRunTask = new BTRunTask(this,bt);
				btRunTask.execute();
				messageView.setText(R.string.test_guide_turn_on);
				showDebug("Device launched");
				Thread t = new Thread(new CountDownRunnable(1,10));
				t.start();
			}
	}
	}
	
	public void updateDoneState(int type){
		synchronized(done_lock){
			if (DONE_PROGRESS[type]==true)
				return;
			DONE_PROGRESS[type]=true;
			
			if (!DONE_PROGRESS[_GPS]&&DONE_PROGRESS[_BT]&&DONE_PROGRESS[_CAMERA]){
				stop();
					if (msgBox == null)
						msgBox = new TestQuestionMsgBox(testFragment,main_layout);
					if (msgLoadingHandler == null)
						msgLoadingHandler = new MsgLoadingHandler();
					msgLoadingHandler.sendEmptyMessage(0);
				}
			}
			if (DONE_PROGRESS[_GPS]&&DONE_PROGRESS[_BT]&&DONE_PROGRESS[_CAMERA]){
				SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this.getActivity());
				Boolean debug = sp.getBoolean("debug", false);
				Boolean debug_type = sp.getBoolean("debug_type", false);
				if (debug){
					if (debug_type)
						BDH = new BracDataHandlerDebugModeNormal(timestamp,testFragment);
					else
						BDH = new BracDataHandlerDebugMode(timestamp,testFragment);
				}
				else
					BDH = new BracDataHandler(timestamp, testFragment);
				int bdh_result = BDH.start();
				if (bdh_result != BracDataHandler.ERROR)
					changeTabsHandler.sendEmptyMessage(0);
		}
	}
	
	public void stopDueToInit(){
		if (cameraRecorder!=null)
			cameraRecorder.close();
		if (bt!=null)
			bt.close();
		if (gpsInitTask!=null)
			gpsInitTask.cancel(true);
		if (btInitHandler!=null)
			btInitHandler.removeMessages(0);
		if (cameraInitHandler!=null)
			cameraInitHandler.removeMessages(0);
		if (btRunTask!=null)
			btRunTask.cancel(true);
		if (gpsRunTask!=null){
			gpsRunTask.cancel(true);
		}
		
	}
	
	public void stop(){
		if (cameraRecorder!=null)
			cameraRecorder.close();
		
		if (bt!=null)
			bt.close();
		if (gpsInitTask!=null)
			gpsInitTask.cancel(true);
		if (btInitHandler!=null)
			btInitHandler.removeMessages(0);
		if (cameraInitHandler!=null)
			cameraInitHandler.removeMessages(0);
		if (btRunTask!=null)
			btRunTask.cancel(true);
		if (gpsRunTask!=null)
			gpsRunTask.cancel(true);
		if (loadingHandler!=null){
			loadingHandler.removeMessages(0);
			loadingHandler = null;
		}
		if (msgLoadingHandler !=null){
			msgLoadingHandler.removeMessages(0);
			msgLoadingHandler = null;
		}
		if (testHandler!=null){
			testHandler.removeMessages(0);
			testHandler = null;
		}
		if (failBgHandler!=null){
			failBgHandler.removeMessages(0);
			failBgHandler = null;
		}
		if (msgHandler!=null){
			msgHandler.removeMessages(0);
			msgHandler = null;
		}
		if (countDownHandler!=null){
			countDownHandler.removeMessages(0);
			countDownHandler = null;
		}
	}
	
	private void clear(){
		if (msgBox!=null){
    		msgBox.clear();
    		msgBox = null;
    	}
	}
	
	@SuppressLint("HandlerLeak")
	private class LoadingHandler extends Handler{
		public void handleMessage(Message msg){
			testCircle.setImageDrawable(null);
			bracText.setText("0.00");
			bracText.setVisibility(View.VISIBLE);
			messageView.setText(R.string.test_guide_start);
			startButton.setOnClickListener(new StartOnClickListener());
			startButton.setVisibility(View.VISIBLE);
			startText.setText(R.string.start);
			startText.setTextColor(0xFF454545);
			helpButton.setOnClickListener(new TutorialOnClickListener());
			helpButton.setOnLongClickListener(new TutorialOnLongClickListener());
			face.setVisibility(View.INVISIBLE);
			
			bg.setOnTouchListener(new BackgroundDoubleOnTouchListener());
			LoadingBox.dismiss();
		}
	}
    
	@SuppressLint("HandlerLeak")
	private class MsgLoadingHandler extends Handler{
		public void handleMessage(Message msg){
			startButton.setOnClickListener(null);
			if (msgBox==null)
				msgBox = new TestQuestionMsgBox(testFragment,main_layout);
			msgBox.settingPreTask();
			msgBox.settingInBackground();
			msgBox.settingPostTask();
			msgBox.generateGPSCheckBox();
			messageView.setText(R.string.test_guide_msg_box);
		}
	}
    
	@SuppressLint("HandlerLeak")
	private class FailBgHandler extends Handler{
		
	
		public void handleMessage(Message msg){
			if (msgLoadingHandler !=null){
				msgLoadingHandler.removeMessages(0);
				msgLoadingHandler = null;
			}
			if (testHandler!=null){
				testHandler.removeMessages(0);
				testHandler = null;
			}
			String msgStr = msg.getData().getString("msg");
			
			startButton.setOnClickListener(new EndTestOnClickListener());
			startButton.setVisibility(View.VISIBLE);
			face.setVisibility(View.INVISIBLE);
			msgStr = msgStr.concat(getString(R.string.test_guide_end));
			messageView.setText(msgStr);
			
		}
	}
	@SuppressLint("HandlerLeak")
	private class TestHandler extends Handler{
		public void handleMessage(Message msg){
			startButton.setOnClickListener(null);

			if (blowDrawables == null){
				blowDrawables = new Drawable[BLOW_RESOURCE.length];
				for (int i=1;i<blowDrawables.length;++i)
					blowDrawables[i] = activity.getResources().getDrawable(BLOW_RESOURCE[i]);
			}
			
			face.setVisibility(View.VISIBLE);
			
			messageView.setText(R.string.test_guide_testing);
			if (bt!=null && cameraRecorder!=null){
				bt.start();
				cameraRecorder.start();
				bracText.setText("0.00");
			}
		}
	}
	public void changeTestMessage(float value,int time){
		bracText.setText(format.format(value));
		if (time >= blowDrawables.length)
			time = blowDrawables.length-1;
		if (blowDrawables!=null)
			testCircle.setImageDrawable(blowDrawables[time]);
	}
	
	public void stopByFail(){
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("msg",getResources().getString(R.string.test_guide_test_fail));
		msg.setData(data);
		msg.what = 0;
		if (failBgHandler!=null)
			failBgHandler.sendMessage(msg);
	}

	public boolean isKeepMsgBox() {
		return keepMsgBox;
	}

	public void setKeepMsgBox(boolean keepMsgBox) {
		this.keepMsgBox = keepMsgBox;
	}
	
	@SuppressLint("HandlerLeak")
	private class TimeUpHandler extends Handler{
		public void handleMessage(Message msg){
			int t = msg.what;
			if (t == 0){
				showDebug(">Try to start the device");
				startBT();
			}
			else if (t == 1){
				messageView.setVisibility(View.VISIBLE);
				startText.setText("");
				showDebug(">Start to run the  device");
				runBT();
			}
		}
	}
	
	private class TimeUpRunnable implements Runnable{

		int msg;
		int time;
		public TimeUpRunnable (int msg, int time){
			this.msg = msg;
			this.time = time;
		}
		
		@Override
		public void run() {
			try {
				Thread.sleep(time);
				timeUpHandler.sendEmptyMessage(msg);
			} catch (InterruptedException e) {}
		}
		
	}
	
	private class CountDownRunnable implements Runnable{

		int msg;
		int sec;
		public CountDownRunnable (int msg, int sec){
			this.msg = msg;
			this.sec = sec;
			count_down_sec = sec;
		}
		
		@Override
		public void run() {
			try {
				for (int i=0;i<sec;++i){
					Thread.sleep(500);
					if (countDownHandler!=null)
						countDownHandler.sendEmptyMessage(0);
					Thread.sleep(500);
					if (countDownHandler!=null)
						countDownHandler.sendEmptyMessage(0);
				}
				timeUpHandler.sendEmptyMessage(msg);
			} catch (InterruptedException e) {}
		}
	}

	@SuppressLint("HandlerLeak")
	private class CountDownHandler extends Handler{
		public void handleMessage(Message msg){
			if (messageView.getVisibility() == View.VISIBLE)
				messageView.setVisibility(View.INVISIBLE);
			else{
				messageView.setVisibility(View.VISIBLE);
				--count_down_sec;
				startText.setTextColor(0xFFc98123);
				startText.setText(String.valueOf(count_down_sec));
			}
		}
	}
	
	@SuppressLint("HandlerLeak")
	private class ChangeTabsHandler extends Handler{
		public void handleMessage(Message msg){
			if (msgBox!=null)
				msgBox.closeInitializingBox();
			FragmentTabs.changeTab(1);
		}
	}
	
	private class TutorialOnClickListener implements View.OnClickListener{
		public void onClick(View v) {
			ClickLoggerLog.Log(getActivity(), ClickLogId.TEST_TUTORIAL_BUTTON);
			showTutorial();
		}
	}
	private class TutorialOnLongClickListener implements View.OnLongClickListener{
		public boolean onLongClick(View v) {
				activity.openOptionsMenu();
				return true;
		}
	}
	
	private void showTutorial(){
		Intent intent= new Intent();
		intent.setClass(activity, TutorialActivity.class);
		activity.startActivity(intent);
	}
	
	
	public void writeQuestionFile(int emotion,int desire){
		questionFile.write(emotion, desire);
	}
	
	private boolean doubleClickState = false;
	
	private class BackgroundDoubleOnTouchListener implements View.OnTouchListener{
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (!doubleClickState){
				doubleClickState = true;
				Thread t = new Thread(new BackgroundDoubleOnTouchRunnable());
				t.start();
			}
			else
				activity.openOptionsMenu();
			return false;
		}
	}
	
	private class BackgroundDoubleOnTouchRunnable implements Runnable{
		@Override
		public void run() {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
			}finally{
				doubleClickState = false;
			}
		}
	}
	
	//Debug --------------------------------------------------------------------------------------------------------
	private void checkDebug(){
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		Boolean debug = sp.getBoolean("debug", false);
		ScrollView sv = (ScrollView) view.findViewById(R.id.debugView);

		if (debug){
			sv.setVisibility(View.VISIBLE);
			msgHandler = new ChangeMsgHandler();
			debugMsg.setText("");
			debugMsg.setVisibility(View.VISIBLE);
			debugMsg.setClickable(false);
			debugMsg.setOnKeyListener(null);
			debugMsg.setEnabled(false);
		}else{
			sv.setVisibility(View.INVISIBLE);
			debugMsg.setVisibility(View.INVISIBLE);
		}
	}
	
	private void checkDebug2(boolean debug, boolean debug_type){
		Button[] conditionButtons = new Button[4];
		conditionButtons[0] = (Button) view.findViewById(R.id.condition_button_1);
		conditionButtons[1] = (Button) view.findViewById(R.id.condition_button_2);
		conditionButtons[2] = (Button) view.findViewById(R.id.condition_button_3);
		conditionButtons[3] = (Button) view.findViewById(R.id.condition_button_4);
		if (debug_type){
			conditionButtons[0].setText("avm-1");
			conditionButtons[1].setText("avm-2");
			conditionButtons[2].setText("avm-3");
			conditionButtons[3].setText("avm-4");
		}else{
			conditionButtons[0].setText("acvm-1");
			conditionButtons[1].setText("acvm-2");
			conditionButtons[2].setText("acvm-3");
			conditionButtons[3].setText("acvm-4");
		}
		
		if (debug){
			for (int i=0;i<4;++i){
				conditionButtons[i].setVisibility(View.VISIBLE);
				conditionButtons[i].setOnClickListener(new ConditionOnClickListener(i));
			}
			
		}else{
			for (int i=0;i<4;++i){
				conditionButtons[i].setVisibility(View.INVISIBLE);
				conditionButtons[i].setOnClickListener(null);
			}
		}
			
	}
	
	private class ConditionOnClickListener implements View.OnClickListener{

		private int cond;
		public ConditionOnClickListener(int cond){
			this.cond = cond;
			
		}
		
		@Override
		public void onClick(View v) {
			
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(activity);
			SharedPreferences.Editor editor = sp.edit();
	    	editor.putInt("latest_result", cond);
	    	editor.putBoolean("tested", true);
	    	editor.putBoolean("hourly_alarm", false);
	    	editor.commit();
	    	FragmentTabs.changeTab(1);
		}
		
	}
	
	
	public void showDebug(String message){
		if (this == null)
			return;
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(activity);
		Boolean debug = sp.getBoolean("debug", false);
		if (msgHandler!=null && debug){
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putString("message", message);
			msg.setData(data);
			msg.what = 0;
			msgHandler.sendMessage(msg);
		}
		
	}
	
	@SuppressLint("HandlerLeak")
	private class ChangeMsgHandler extends Handler{
		public void handleMessage(Message msg){
			debugMsg.append("\n"+msg.getData().getString("message"));
			debugMsg.setSelection(debugMsg.getText().length());
		}
	}
	

}
