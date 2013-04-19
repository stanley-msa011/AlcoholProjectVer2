package main.activities;


import java.io.File;
import java.text.DecimalFormat;

import test.bluetooth.BTInitHandler;
import test.bluetooth.BTRunTask;
import test.bluetooth.Bluetooth;
import test.camera.CameraInitHandler;
import test.camera.CameraRecorder;
import test.camera.CameraRunHandler;
import test.data.BracDataHandler;
import test.file.BracValueFileHandler;
import test.file.ImageFileHandler;
import test.gps.GPSInitTask;
import test.gps.GPSRunTask;
import test.ui.UIMsgBox;
import test.ui.UIRotate;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

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
	private UIRotate rotate;

	private LoadingHandler loadingHandler;
	private AnimationHandler animationHandler;
	private AnimationTimerTask animationTimerTask;
	private FailBgHandler failBgHandler;
	private MsgLoadingHandler msgLoadingHandler;
	private TestHandler testHandler;
	
	private ImageView bg, startLine, startCircle;
	private Bitmap bgBmp, startLineBmp, startCircleBmp;
	private TextView startText;
	
	private ImageView animation;
	private Bitmap[] animationBmp;
	private AnimationDrawable animationDrawable;
	
	private FrameLayout preview_layout;
	
	private static Object init_lock  = new Object();
	private static Object done_lock  = new Object();
	
	private TextView failHelp;
	
	private ImageView load;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		keepMsgBox = false;
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
		if (!isKeepMsgBox()){
			RelativeLayout layout = (RelativeLayout) view;
			load = new ImageView(view.getContext());
			if (!FragmentTabs.loadingBmp.isRecycled())
				load.setImageBitmap(FragmentTabs.loadingBmp);
			else{
				Bitmap tmp = BitmapFactory.decodeResource(getResources(), R.drawable.loading_page);
				FragmentTabs.loadingBmp = Bitmap.createScaledBitmap(tmp, (int)(tmp.getWidth()*0.4), (int)(tmp.getHeight()*0.4), true);
				tmp.recycle();
				load.setImageBitmap(FragmentTabs.loadingBmp);
			}
			layout.addView(load);
			RelativeLayout.LayoutParams loadParam = (LayoutParams) load.getLayoutParams();
			loadParam.width = RelativeLayout.LayoutParams.MATCH_PARENT;
			loadParam.height = RelativeLayout.LayoutParams.MATCH_PARENT;
			load.setScaleType(ScaleType.FIT_XY);
			
			context = this.getActivity();
			testFragment = this;
			FragmentTabs.enableTab(true);
			setting();
			loadingHandler.sendEmptyMessage(0);
		}
		else{
			setKeepMsgBox(false);
		}
		Log.d("test","onresume end");
	}
	
	private void setting(){
		bg = (ImageView) view.findViewById(R.id.test_background);
		startLine = (ImageView) view.findViewById(R.id.test_background_line);
		startCircle = (ImageView) view.findViewById(R.id.test_start_button);
		startText =(TextView) view.findViewById(R.id.test_start_text);
		Point screen = FragmentTabs.getSize();
		startText.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 74.0/720.0));
		animation = (ImageView) view.findViewById(R.id.test_animation);
		animation.setVisibility(View.INVISIBLE);
		main_layout = (RelativeLayout) view.findViewById(R.id.test_fragment_main_layout);
		failHelp = (TextView) view.findViewById(R.id.test_fail_help);
		startText.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 56.0/720.0));
		msgBox = new UIMsgBox(testFragment,main_layout);
		rotate = new UIRotate(testFragment,main_layout);
		preview_layout = (FrameLayout) view.findViewById(R.id.test_camera_preview_layout);
		
		loadingHandler = new LoadingHandler();
		msgLoadingHandler = new MsgLoadingHandler();
		failBgHandler = new FailBgHandler();
		testHandler = new TestHandler();
		animationHandler = new AnimationHandler();
	}
	
	
	
	public void reset(){
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
    	view = inflater.inflate(R.layout.new_test_fragment, container,false);
    	return view;
    }
	
	
	public void startGPS(boolean enable){
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
		msgBox.generateBTCheckBox();
	}
	
	public void startBT(){
		msgBox.generateInitializingBox();
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
		
		cleanMsgBox();
		testHandler.sendEmptyMessage(0);

	}
	
	public void failBT(){
		msgBox.closeInitializingBox();
		cleanMsgBox();
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("msg","未啟用  \n酒測裝置及藍芽功能");
		msg.setData(data);
		msg.what = 0;
		failBgHandler.sendMessage(msg);
	}
	
	
	private class StartOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			startLine.setVisibility(View.INVISIBLE);
			startCircle.setVisibility(View.INVISIBLE);
			startText.setVisibility(View.INVISIBLE);
			reset();
			animationHandler.sendEmptyMessage(0);
		}
	}
	
	private class EndTestOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			failHelp.setVisibility(View.INVISIBLE);
			stop();
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
		if (state.equals(Environment.MEDIA_MOUNTED)){
			dir = new File(Environment.getExternalStorageDirectory(),"drunk_detection");
			Log.d("TEST_STORAGE","MEDIA");
		} 
		else{
			dir = new File(this.getActivity().getFilesDir(),"drunk_detection");
			Log.d("TEST_STORAGE","NO MEDIA");
		}
		if (!dir.exists()){
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
				msgBox.closeInitializingBox();
				msgBox.generateBTSuccessBox();
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
				if (bdh_result == BracDataHandler.ERROR){
					//Show error message
				}
				else{
					double result = BDH.getResult();
					Log.d("TEST RESULT",String.valueOf(result));
					FragmentTabs.changeTab(1);
				}
			}
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode==_GPS){
			runGPS();
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

		if (gpsRunTask!=null){
			gpsRunTask.cancel(true);
		}
		
		loadingHandler.removeMessages(0);
		msgLoadingHandler.removeMessages(0);
		testHandler.removeMessages(0);
		failBgHandler.removeMessages(0);
		animationHandler.removeMessages(0);
		if (animationTimerTask!=null)
			animationTimerTask.cancel(true);
	}
	
	private void clear(){

		if (bgBmp!=null && !bgBmp.isRecycled()){
			bgBmp.recycle();
			bgBmp = null;
		}
		if (startLineBmp!=null && !startLineBmp.isRecycled()){
			startLineBmp.recycle();
			startLineBmp = null;
		}
		if (startCircleBmp!=null && !startCircleBmp.isRecycled()){
			startCircleBmp.recycle();
			startCircleBmp = null;
		}
		if (animation!=null){
			animation.setImageDrawable(null);
		}
		if (animationBmp!=null){
			for (int i=0;i<animationBmp.length;++i){
				if(animationBmp[i]!=null && !animationBmp[i].isRecycled()){
					animationBmp[i].recycle();
					animationBmp[i] = null;
				}
			}
		}
		cleanMsgBox();
		cleanRotate();
	}
	
    private void cleanMsgBox(){
    	if (msgBox!=null){
    		msgBox.clear();
    	}
    }
	
    private void cleanRotate(){
    	if (rotate!=null){
    		rotate.clear();
    	}
    }
    
	@SuppressLint("HandlerLeak")
	private class LoadingHandler extends Handler{
		
		private Resources r;
		public void handleMessage(Message msg){
			
			Log.d("test","load handler");
			
			r = getResources();
    		bg.setImageBitmap(null);
    		
    		failHelp.setVisibility(View.INVISIBLE);
    		
    		Point screen = FragmentTabs.getSize();
			
			if (bgBmp!=null && !bgBmp.isRecycled()){
				bgBmp.recycle();
				bgBmp = null;
			}
			bgBmp = BitmapFactory.decodeResource(r, R.drawable.test_start_bg);
			
			if (startLineBmp==null ||startLineBmp.isRecycled()){
				startLineBmp = BitmapFactory.decodeResource(r, R.drawable.test_start_line);
				RelativeLayout.LayoutParams startLineParam = (LayoutParams) startLine.getLayoutParams();
				startLineParam.height = (int)(screen.x * 146.0/720.0);
				startLineParam.topMargin = (int)(screen.x * 223.0/720.0);
			}
			
			if (startCircleBmp==null ||startCircleBmp.isRecycled()){
				startCircleBmp = BitmapFactory.decodeResource(r, R.drawable.test_start_start);
				RelativeLayout.LayoutParams startCircleParam = (LayoutParams) startCircle.getLayoutParams();
				startCircleParam.width = (int)(screen.x * 358.0/720.0);
				startCircleParam.height = (int)(screen.x * 356.0/720.0);
				startCircleParam.leftMargin = (int)(screen.x * 357.0/720.0);
				startCircleParam.topMargin = (int)(screen.x * 112.0/720.0);
			}
			
			RelativeLayout.LayoutParams startTextParam = (LayoutParams) startText.getLayoutParams();
			startTextParam.topMargin = (int)(screen.x * 240.0/720.0);
			startTextParam.leftMargin =(int)(screen.x * 480.0/720.0);
			
			RelativeLayout.LayoutParams animationParam = (LayoutParams) animation.getLayoutParams();
			animationParam.width = (int)(screen.x * 573.0/720.0);
			animationParam.height = (int)(screen.x * 631.0/720.0);
			animationParam.leftMargin = 0;
			animationParam.topMargin = (int)(screen.x * 44.0/720.0);
			
			
			RelativeLayout.LayoutParams previewParam = (LayoutParams) preview_layout.getLayoutParams();
			previewParam.width = (int)(screen.x * 588.0/720.0);
			previewParam.height = (int)(screen.x * 750.0/720.0);
			previewParam.leftMargin = (int)(screen.x * 62.0/720.0);
			previewParam.topMargin = (int)(screen.x * 233.0/720.0);
			
			if(bgBmp!=null)
				bg.setImageBitmap(bgBmp);
			if(startLineBmp!=null)
				startLine.setImageBitmap(startLineBmp);
			startLine.setVisibility(View.VISIBLE);
			if (startCircleBmp!=null)
				startCircle.setImageBitmap(startCircleBmp);
			startCircle.setVisibility(View.VISIBLE);
			startText.setText("開始");
			startText.setVisibility(View.VISIBLE);
			
			startCircle.setOnClickListener(new StartOnClickListener());
			bg.setOnClickListener(null);
		
			RelativeLayout layout = (RelativeLayout) view;
			layout.removeView(load);
			
		}
	}
    
    
	@SuppressLint("HandlerLeak")
	private class MsgLoadingHandler extends Handler{
		
		public void handleMessage(Message msg){
			msgBox.settingPreTask();
			msgBox.settingInBackground();
			msgBox.settingPostTask();
			msgBox.generateGPSCheckBox();
		}
	}
    
	@SuppressLint("HandlerLeak")
	private class FailBgHandler extends Handler{
		
		private String msgStr;
		private int topMargin;
	
		public void handleMessage(Message msg){
			this.msgStr = msg.getData().getString("msg");
			
			cleanRotate();
			
			Point screen = FragmentTabs.getSize();
			failHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 56.0/720.0));
			topMargin = (int)(screen.x*240.0/720.0);
			bg.setImageBitmap(null);
			if(bgBmp!=null && !bgBmp.isRecycled()){
				bgBmp.recycle();
				bgBmp = null;
			}
			bgBmp = BitmapFactory.decodeResource(getResources(), R.drawable.test_notusing_bg);
			
			RelativeLayout.LayoutParams msgParam = (LayoutParams) failHelp.getLayoutParams();
			msgParam.topMargin = topMargin;
			
			bg.setImageBitmap(bgBmp);
			bg.setOnClickListener(new EndTestOnClickListener());
			failHelp.setText(msgStr);
			failHelp.setVisibility(View.VISIBLE);
		}
	}
	
	
	
	@SuppressLint("HandlerLeak")
	private class AnimationHandler extends Handler{
		
		@SuppressWarnings("deprecation")
		public void handleMessage(Message msg){
			animationBmp = new Bitmap[4];
			animationBmp[0] = BitmapFactory.decodeResource(getResources(), R.drawable.test_animation_0);
			animationBmp[1] = BitmapFactory.decodeResource(getResources(), R.drawable.test_animation_1);
			animationBmp[2] = BitmapFactory.decodeResource(getResources(), R.drawable.test_animation_2);
			animationBmp[3] = BitmapFactory.decodeResource(getResources(), R.drawable.test_animation_3);
			
			animationDrawable = new AnimationDrawable();
			animation.setImageDrawable(animationDrawable);
			for (int i=0;i<8;++i){
				Drawable d = new BitmapDrawable(animationBmp[i%4]);
				animationDrawable.addFrame(d,300);
			}
			animation.setVisibility(View.VISIBLE);
			animationDrawable.start();
			animationTimerTask = new AnimationTimerTask();
			animationTimerTask.execute();
		}
	}
	
	private class AnimationTimerTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(2400);
			} catch (InterruptedException e) {}
			return null;
		}
		@Override
		 protected void onPostExecute(Void result) {
			animation.setVisibility(View.INVISIBLE);
			animationDrawable.stop();
			animation.setImageDrawable(null);
			if (animationBmp!=null){
				for (int i=0;i<animationBmp.length;++i){
					if(animationBmp[i]!=null && !animationBmp[i].isRecycled()){
						animationBmp[i].recycle();
						animationBmp[i] = null;
					}
				}
			}
			msgLoadingHandler.sendEmptyMessage(0);
		}
		
		protected void onCancelled(){
			clear();
		}
	}
	
	
	@SuppressLint("HandlerLeak")
	private class TestHandler extends Handler{
		public void handleMessage(Message msg){
			bg.setImageBitmap(null);
			rotate.settingPreTask();
			
			if(bgBmp!=null && !bgBmp.isRecycled()){
				bgBmp.recycle();
				bgBmp = null;
			}
			bgBmp = BitmapFactory.decodeResource(getResources(), R.drawable.test_camera_bg);
			rotate.settingInBackground();
			
			bg.setImageBitmap(bgBmp);
			bg.setOnClickListener(null);
			rotate.settingPostTask();
			
			if (bt!=null && cameraRecorder!=null){
				bt.start();
				cameraRecorder.start();
			}
		}
	}
	
	public void changeTestMessage(int time){
		rotate.setText(time);
	}
	
	public void changeTestSpeed(int change){
		rotate.setSpeed(change);
	}
	
	public void stopByFail(){
		Log.d("test","stop by time out");
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("msg","測試失敗             \n可能因為測試超時\n或酒測器沒電了");
		msg.setData(data);
		msg.what = 0;
		failBgHandler.sendMessage(msg);
	}

	public boolean isKeepMsgBox() {
		return keepMsgBox;
	}

	public void setKeepMsgBox(boolean keepMsgBox) {
		this.keepMsgBox = keepMsgBox;
	}
	
}
