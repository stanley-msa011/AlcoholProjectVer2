package ubicomp.drunk_detection.activities;


import java.io.File;
import java.text.DecimalFormat;

import ubicomp.drunk_detection.activities.R;

import test.bluetooth.BTInitHandler;
import test.bluetooth.BTRunTask;
import test.bluetooth.Bluetooth;
import test.camera.CameraInitHandler;
import test.camera.CameraRecorder;
import test.camera.CameraRunHandler;
import test.data.BracDataHandler;
import test.file.BracValueFileHandler;
import test.file.ImageFileHandler;
import test.file.QuestionFile;
import test.gps.GPSInitTask;
import test.gps.GPSRunTask;
import test.ui.UIMsgBox;
import test.ui.UIMsgBox2;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import clicklog.ClickLogger;

public class TestFragment extends Fragment {

	private Activity context;
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
	
	//Uploader
	private BracDataHandler BDH;
	
	private RelativeLayout main_layout;
	private UIMsgBox msgBox;

	private LoadingHandler loadingHandler;
	private FailBgHandler failBgHandler;
	private MsgLoadingHandler msgLoadingHandler;
	private TestHandler testHandler;
	private TimeUpHandler timeUpHandler;
	private ChangeTabsHandler changeTabsHandler;
	
	private RelativeLayout startLayout;
	private ImageView bg, startButton, startStroke;
	private Bitmap bgBmp, startButtonBmp, startStrokeBmp;
	private TextView bracText;
	private TextView brac;
	private TextView startText;
	
	
	private FrameLayout preview_layout;
	
	private RelativeLayout helpLayout;
	private ImageView helpButton;
	private Bitmap helpButtonBmp;
	private TextView helpText;
	
	private ImageView testCircle;
	
	private static Object init_lock  = new Object();
	private static Object done_lock  = new Object();
	
	private EditText debugMsg;
	private ChangeMsgHandler msgHandler;
	
	private static final String[] BLOW_MSG = {"開始\n吹氣","加油\n1","加油\n2","加油\n3","加油\n4","加油\n5"," 完成"};
	private static final int[] BLOW_RESOURCE = {0,R.drawable.test_circle1,R.drawable.test_circle2,R.drawable.test_circle3,R.drawable.test_circle4,R.drawable.test_circle5};
	private Bitmap[] blowBmp;
	
	private ImageView face;
	private Bitmap faceBmp;
	
	private QuestionFile questionFile;
	
	//private Typeface digitTypeface;
	private Typeface wordTypeface;
	private Typeface digitTypeface;
	private Typeface wordTypefaceBold;
	
	// For Click Sequence logging
	private ClickLogger clickLogger;
	
	private DecimalFormat format;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		keepMsgBox = false;
		format = new DecimalFormat();
		format.setMaximumIntegerDigits(1);
		format.setMinimumIntegerDigits(1);
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);
	}
	
	public void onPause(){
		if (!isKeepMsgBox()){
			Log.d("onpause","do");
			stop();
			clear();
		}
		else{
			Log.d("onpause","skip");
		}
		super.onPause();
	}
	
	public void onResume(){
		super.onResume();
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		checkDebug2(sp.getBoolean("debug", false));
		
		Log.d("test","onresume");
		if (!isKeepMsgBox()){
			context = this.getActivity();
			testFragment = this;
			setting();
			loadingHandler.sendEmptyMessage(0);
		}
		else{
			setKeepMsgBox(false);
			runGPS();
		}
		
		Log.d("test","onresume end");
	}
	
	private void setting(){
		
		// For Click Sequence logging
		clickLogger = new ClickLogger();
		
		digitTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dinproregular.ttf");
		wordTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dfheistd-w3.otf");
		wordTypefaceBold  = Typeface.createFromAsset(context.getAssets(), "fonts/dfheistd-w5.otf");
		
		bg = (ImageView) view.findViewById(R.id.test_background);
		startLayout = (RelativeLayout) view.findViewById(R.id.test_start_layout);
		startButton = (ImageView) view.findViewById(R.id.test_start_button);
		startStroke = (ImageView) view.findViewById(R.id.test_start_stroke);
		bracText =(TextView) view.findViewById(R.id.test_brac_value_text);
		brac = (TextView) view.findViewById(R.id.test_brac_text);
		startText = (TextView) view.findViewById(R.id.test_start_button_text);
		
		Point screen = FragmentTabs.getSize();
		
		helpLayout = (RelativeLayout) view.findViewById(R.id.help_layout);
		helpButton = (ImageView) view.findViewById(R.id.help_background);
		helpText = (TextView) view.findViewById(R.id.help_text);
		helpText.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 80/1080);
		helpText.setTypeface(wordTypefaceBold);
		
		face = (ImageView) view.findViewById(R.id.test_face);
		
		testCircle = (ImageView) view.findViewById(R.id.test_start_circle);
		
		main_layout = (RelativeLayout) view.findViewById(R.id.test_fragment_main_layout);
		
		
		bracText.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 144/1080);
		bracText.setTypeface(digitTypeface);
		
		startText.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 96/1080);
		startText.setTypeface(wordTypefaceBold);
		
		brac.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 54/1080);
		brac.setTypeface(wordTypeface);
		
		messageView = (TextView) view.findViewById(R.id.test_message);
		messageView.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 54/1080);
		messageView.setTypeface(wordTypeface);
		
		RelativeLayout.LayoutParams mParam = (LayoutParams) messageView.getLayoutParams();
		mParam.topMargin = screen.x * 54/1080;
		if (msgBox==null)
			msgBox = new UIMsgBox(testFragment,main_layout);
		preview_layout = (FrameLayout) view.findViewById(R.id.test_camera_preview_layout);

		debugMsg = (EditText) view.findViewById(R.id.debug_msg);
		
		loadingHandler = new LoadingHandler();
		msgLoadingHandler = new MsgLoadingHandler();
		failBgHandler = new FailBgHandler();
		testHandler = new TestHandler();
		timeUpHandler = new TimeUpHandler();
		changeTabsHandler = new ChangeTabsHandler(); 
	}
	
	public void reset(){
		checkDebug();
		timestamp = setTimeStamp();
		setStorage();
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		cameraRecorder = new CameraRecorder(testFragment,imgFileHandler);
		cameraRunHandler = new CameraRunHandler(cameraRecorder);
		bt = new Bluetooth(testFragment,cameraRunHandler,bracFileHandler);
		for (int i=0;i<3;++i)
			INIT_PROGRESS[i]=DONE_PROGRESS[i]=false;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	view = inflater.inflate(R.layout.test_fragment, container,false);
    	return view;
    }
	
	public void startGPS(boolean enable){
		msgBox.generateInitializingBox();
		if (enable){
			Log.d("GPS","ENABLE GPS");
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
		else{
			updateDoneState(_GPS);
		}
	}
	
	public void startBT(){
		messageView.setTextColor(0xFFFFFFFF);
		messageView.setText("請稍候");
		//initialize bt task
		btInitHandler = new BTInitHandler(testFragment,bt);
		btInitHandler.sendEmptyMessage(0);
		Log.d("INIT","BT TASK STARTED");
		
		//initialize camera task
		cameraInitHandler = new CameraInitHandler(testFragment,cameraRecorder);
		cameraInitHandler.sendEmptyMessage(0);
		Log.d("INIT","Camera TASK STARTED");
	}
	
	public void runBT(){
		if (testHandler==null)
			testHandler = new TestHandler();
		
			testHandler.sendEmptyMessage(0);

	}
	
	public void failBT(){
		messageView.setText("");
		cleanMsgBox();
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("msg","未啟用酒測裝置");
		msg.setData(data);
		msg.what = 0;
		if (failBgHandler!=null)
			failBgHandler.sendMessage(msg);
	}
	
	private class StartOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			
			clickLogger.click_logging(System.currentTimeMillis(), "TestStart_click");

			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(testFragment.getActivity());
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
				startButton.setOnClickListener(null);
				startButton.setVisibility(View.INVISIBLE);
				startText.setVisibility(View.INVISIBLE);
				helpText.setTextColor(0xFF424242);
				bracText.setText("0.00");
				reset();
				
				messageView.setText("請按酒測裝置黑色按鈕\n以啟用酒測裝置");
				messageView.setTextColor(0xFFFFFFFF);
				Thread t = new Thread(new TimeUpRunnable(0,1500));
				t.start();
			}
		}
	}
	
	private class EndTestOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			
			clickLogger.click_logging(System.currentTimeMillis(), "TestEnd_click");
			
			stopDueToInit();
			if (loadingHandler!=null)
				loadingHandler.sendEmptyMessage(0);
		}
	}
	
	private String setTimeStamp(){
		long time_in_sec = System.currentTimeMillis() / 1000L;
		DecimalFormat d = new DecimalFormat("0");
		return d.format(time_in_sec);
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
				Log.d("TEST_STORAGE","FAIL TO CREATE DIR");
		
		mainDirectory = new File(dir,timestamp);
		if (!mainDirectory.exists())
			if (!mainDirectory.mkdirs()){
				return;
			}
		
		bracFileHandler = new BracValueFileHandler(mainDirectory,timestamp);
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
				messageView.setText("已啟用酒測裝置");
				showDebug("Device launched");
				Thread t = new Thread(new TimeUpRunnable(1,1500));
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
						msgBox = new UIMsgBox(testFragment,main_layout);
					if (msgLoadingHandler == null)
						msgLoadingHandler = new MsgLoadingHandler();
					msgLoadingHandler.sendEmptyMessage(0);
				}
			}
			if (DONE_PROGRESS[_GPS]&&DONE_PROGRESS[_BT]&&DONE_PROGRESS[_CAMERA]){
				BDH = new BracDataHandler(timestamp, testFragment);
				int bdh_result = BDH.start();
				if (bdh_result != BracDataHandler.ERROR){
					double result = BDH.getResult();
					Log.d("TEST RESULT",String.valueOf(result));
				changeTabsHandler.sendEmptyMessage(0);
			}
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
		Log.d("test","stop");
		
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
	}
	
	private void clear(){
		Log.d("test","clear");
		cleanMsgBox();
		cleanBlowBmp();
	}
	
    private void cleanMsgBox(){
    	if (msgBox!=null){
    		msgBox.clear();
    		msgBox = null;
    	}
    }
    
    private void cleanBlowBmp(){
    	if (faceBmp!=null && !faceBmp.isRecycled()){
    		faceBmp.recycle();
    		faceBmp = null;
    	}
    	
    	if (blowBmp == null)
    		return;
    	for (int i=1;i<blowBmp.length;++i){
    		if (blowBmp[i]!=null && !blowBmp[i].isRecycled()){
    			blowBmp[i].recycle();
    			blowBmp[i] = null;
    		}
    	}
    	blowBmp = null;
    }
	@SuppressLint("HandlerLeak")
	private class LoadingHandler extends Handler{
		
		private Resources r;
		public void handleMessage(Message msg){
			
			Log.d("test","load handler");
			
			r = getResources();
    		bg.setImageBitmap(null);
    		
    		Point screen = FragmentTabs.getSize();
			
			Bitmap tmp;
			if (bgBmp==null || bgBmp.isRecycled()){
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inSampleSize = 4;
				tmp = BitmapFactory.decodeResource(r, R.drawable.test_background,opt);
				bgBmp = Bitmap.createScaledBitmap(tmp, screen.x, screen.x*1709/1080, true);
				tmp.recycle();
			}
			RelativeLayout.LayoutParams bParam = (RelativeLayout.LayoutParams)bg.getLayoutParams();
			bParam.width = screen.x;
			bParam.height = bParam.width*1709/1080;
			
			if (startButtonBmp==null ||startButtonBmp.isRecycled()){
				tmp = BitmapFactory.decodeResource(r, R.drawable.test_start_button);
				startButtonBmp = Bitmap.createScaledBitmap(tmp, screen.x * 497/1080, screen.x * 497/1080, true);
				tmp.recycle();
			}
			
			if (startStrokeBmp==null ||startStrokeBmp.isRecycled()){
				tmp = BitmapFactory.decodeResource(r, R.drawable.test_button_stroke);
				startStrokeBmp = Bitmap.createScaledBitmap(tmp, screen.x * 593/1080, screen.x * 591/1080, true);
				tmp.recycle();
			}
			
			if (helpButtonBmp==null ||helpButtonBmp.isRecycled()){
				tmp = BitmapFactory.decodeResource(r, R.drawable.test_tutorial_button);
				helpButtonBmp = Bitmap.createScaledBitmap(tmp,screen.x * 102/1080, screen.x * 102/1080, true);
				tmp.recycle();
			}
			
			RelativeLayout.LayoutParams startLayoutParam = (LayoutParams) startLayout.getLayoutParams();
			startLayoutParam.width= screen.x * 593/1080;
			startLayoutParam.height= screen.x * 591/1080;
			startLayoutParam.topMargin = screen.x * 423/1080;
			
			RelativeLayout.LayoutParams startButtonParam = (LayoutParams) startButton.getLayoutParams();
			startButtonParam.width = screen.x * 497/1080;
			startButtonParam.height = screen.x * 497/1080;
			
			RelativeLayout.LayoutParams startStrokeParam = (LayoutParams) startStroke.getLayoutParams();
			startStrokeParam.width = screen.x * 593/1080;
			startStrokeParam.height = screen.x * 591/1080;
			
			RelativeLayout.LayoutParams previewParam = (LayoutParams) preview_layout.getLayoutParams();
			previewParam.width =screen.x * 593/1080;
			previewParam.height = screen.x * 591/1080;
			previewParam.topMargin = screen.x * 423/1080;
			
			RelativeLayout.LayoutParams helpLayoutParam = (LayoutParams) helpLayout.getLayoutParams();
			helpLayoutParam.width = screen.x * 153/1080;
			helpLayoutParam.height = screen.x * 153/1080;
			helpLayoutParam.topMargin =screen.x * 107/1080;
			helpLayoutParam.rightMargin = screen.x * 58/1080;
			
			RelativeLayout.LayoutParams testCircleParam = (LayoutParams) testCircle.getLayoutParams();
			testCircleParam.width = screen.x * 593/1080;
			testCircleParam.height =screen.x * 591/1080;
			
			RelativeLayout.LayoutParams bracVParam = (LayoutParams) bracText.getLayoutParams();
			bracVParam.topMargin =screen.x * 150/1080;
			
			RelativeLayout.LayoutParams bracParam = (LayoutParams) brac.getLayoutParams();
			bracParam.topMargin =screen.x * 20/1080;
			
			if(bgBmp!=null && !bgBmp.isRecycled())
				bg.setImageBitmap(bgBmp);
			
			if (startButtonBmp!=null && !startButtonBmp.isRecycled())
				startButton.setImageBitmap(startButtonBmp);
			
			if (startStrokeBmp!=null && !startStrokeBmp.isRecycled())
				startStroke.setImageBitmap(startStrokeBmp);
			
			if (helpButtonBmp!=null && !helpButtonBmp.isRecycled())
				helpButton.setImageBitmap(helpButtonBmp);
			
			testCircle.setImageBitmap(null);

			bracText.setText("0.00");
			bracText.setVisibility(View.VISIBLE);
			
			messageView.setText("請點選'開始'以進行測試");
			messageView.setTextColor(0xFFFFFFFF);
			
			startButton.setOnClickListener(new StartOnClickListener());
			startButton.setVisibility(View.VISIBLE);
			startText.setVisibility(View.VISIBLE);
			helpButton.setOnClickListener(new TutorialOnClickListener());
			helpButton.setOnLongClickListener(new TutorialOnLongClickListener());
			helpText.setTextColor(0xFFc98123);
			face.setImageBitmap(null);
			FragmentTabs.detach_loading_page(0);
			LoadingBox.dismiss();
			
		}
	}
    
	@SuppressLint("HandlerLeak")
	private class MsgLoadingHandler extends Handler{
		
		public void handleMessage(Message msg){
			startButton.setOnClickListener(null);
			if (msgBox!=null){
				msgBox.settingPreTask();
				msgBox.settingInBackground();
				msgBox.settingPostTask();
			}
			
			if (msgBox!=null){
				msgBox.generateGPSCheckBox();
				messageView.setText("請依對話框指示進行操作");
				messageView.setTextColor(0xFFFFFFFF);
			}
		}
	}
    
	@SuppressLint("HandlerLeak")
	private class FailBgHandler extends Handler{
		
		private String msgStr;
	
		public void handleMessage(Message msg){
			if (msgLoadingHandler !=null){
				msgLoadingHandler.removeMessages(0);
				msgLoadingHandler = null;
			}
			if (testHandler!=null){
				testHandler.removeMessages(0);
				testHandler = null;
			}
			cleanBlowBmp();
			
			this.msgStr = msg.getData().getString("msg");

			startButton.setOnClickListener(new EndTestOnClickListener());
			startButton.setVisibility(View.VISIBLE);
			
			msgStr = msgStr.concat("\n請點選按鈕以結束");
			
			messageView.setText(msgStr);
			messageView.setTextColor(0xFFFF0000);
			
		}
	}
	@SuppressLint("HandlerLeak")
	private class TestHandler extends Handler{
		public void handleMessage(Message msg){
			startButton.setOnClickListener(null);
			
			
			
			cleanBlowBmp();
			blowBmp = new Bitmap[6];
			Bitmap tmp;
			blowBmp[0] = null;
			
			Point screen = FragmentTabs.getSize();
			
			faceBmp = BitmapFactory.decodeResource(getResources(), R.drawable.test_face);
			
			RelativeLayout.LayoutParams fParam = (LayoutParams) face.getLayoutParams();
			fParam.width = screen.x*255/1080;
			fParam.height = screen.x*187/1080;
			face.setImageBitmap(faceBmp);
			
			
			int circleWidth =  screen.x * 593/1080;
			int circleHeight = screen.x * 591/1080;
			for (int i=1;i<blowBmp.length;++i){
				tmp = BitmapFactory.decodeResource(getResources(), BLOW_RESOURCE[i]);
				blowBmp[i] = Bitmap.createScaledBitmap(tmp,  circleWidth,  circleHeight, true);
				tmp.recycle();
			}

			
			
			messageView.setText("請將臉對於螢幕中央，\n並開始吹氣");
			messageView.setTextColor(0xFFFFFFFF);
			if (bt!=null && cameraRecorder!=null){
				bt.start();
				cameraRecorder.start();
				bracText.setText("0.00");
			}
		}
	}
	public void changeTestMessage(float value,int time){
		bracText.setText(format.format(value));
		if (time >= blowBmp.length)
			time = blowBmp.length-1;
		if (blowBmp!=null){
			testCircle.setImageBitmap(blowBmp[time]);
		}
	}
	
	public void stopByFail(){
		Log.d("test","stop by time out");
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("msg","測試超時或酒測器沒電了");
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
			
			clickLogger.click_logging(System.currentTimeMillis(), "TestTutorial_click");
			
			Log.d("test","showTutorial on click");
			showTutorial();
		}
	}
	private class TutorialOnLongClickListener implements View.OnLongClickListener{
		public boolean onLongClick(View v) {
				context.openOptionsMenu();
				return true;
		}
	}
	
	private void showTutorial(){
		Log.d("test","showTutorial");
		Intent intent= new Intent();
		intent.setClass(context, TutorialActivity.class);
		context.startActivity(intent);
		//tutorialHandler.sendEmptyMessage(0);
	}
	
	
	public void writeQuestionFile(int emotion,int desire){
		questionFile.write(emotion, desire);
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
	
	private void checkDebug2(boolean debug){
		Button[] conditionButtons = new Button[4];
		conditionButtons[0] = (Button) view.findViewById(R.id.condition_button_1);
		conditionButtons[1] = (Button) view.findViewById(R.id.condition_button_2);
		conditionButtons[2] = (Button) view.findViewById(R.id.condition_button_3);
		conditionButtons[3] = (Button) view.findViewById(R.id.condition_button_4);
		
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
			
			clickLogger.click_logging(System.currentTimeMillis(), "TestCondition_click");
			
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
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
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this.getActivity());
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
