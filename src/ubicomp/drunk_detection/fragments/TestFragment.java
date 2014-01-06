package ubicomp.drunk_detection.fragments;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Random;

import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.activities.GPSService;
import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.activities.TutorialActivity;
import ubicomp.drunk_detection.check.AlwaysFinishActivitiesCheck;
import ubicomp.drunk_detection.check.DefaultCheck;
import ubicomp.drunk_detection.config.Config;
import ubicomp.drunk_detection.ui.CustomToastSmall;
import ubicomp.drunk_detection.ui.LoadingDialogControl;
import ubicomp.drunk_detection.ui.ScaleOnTouchListener;
import ubicomp.drunk_detection.ui.ScreenSize;
import ubicomp.drunk_detection.ui.TextSize;
import ubicomp.drunk_detection.ui.Typefaces;

import test.bluetooth.BTInitHandler;
import test.bluetooth.BTRunTask;
import test.bluetooth.Bluetooth;
import test.bluetooth.BluetoothDebugMode;
import test.bluetooth.BluetoothDebugModeNormal;
import test.bluetooth.BluetoothOld;
import test.bluetooth.SimpleBluetooth;
import test.camera.CameraInitHandler;
import test.camera.CameraRecorder;
import test.camera.CameraRecorderOld;
import test.camera.CameraRunHandler;
import test.data.BracDataHandler;
import test.data.BracDataHandlerDebugMode;
import test.data.BracDataHandlerDebugModeNormal;
import test.data.BracValueDebugHandler;
import test.data.BracValueFileHandler;
import test.data.ImageFileHandler;
import test.data.QuestionFile;
import test.gps.GPSInitTask;
import test.ui.NotificationBox;
import test.ui.TestQuestionMsgBox;
import test.ui.TestQuestionMsgBoxInterface;
import test.ui.TestQuestionMsgBoxOld;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import data.uploader.DataUploader;
import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;

public class TestFragment extends Fragment {

	private static final String TAG = "TEST_PAGE";
	
	private Activity activity;
	private TestFragment testFragment;
	private View view;
	private TextView messageView;
	private String timestamp;

	private final boolean[] INIT_PROGRESS = { false, false, false };
	private final boolean[] DONE_PROGRESS = { false, false, false };

	public static final int _GPS = 0;
	public static final int _BT = 1;
	public static final int _CAMERA = 2;

	private static final long TEST_GAP_DURATION_LONG = Config.TEST_GAP_DURATION_LONG;
	private static final long TEST_GAP_DURATION_SHORT = Config.TEST_GAP_DURATION_SHORT;
	private static final int COUNT_DOWN_SECOND = Config.COUNT_DOWN_SECOND;
	private static final int COUNT_DOWN_SECOND_DEVELOP = Config.COUNT_DOWN_SECOND_DEVELOP;

	private boolean keepMsgBox;

	// GPS
	private LocationManager locationManager;
	private GPSInitTask gpsInitTask;
	private boolean gps_state = false;

	// Bluetooth
	private Bluetooth bt;
	private BTInitHandler btInitHandler;
	private BTRunTask btRunTask;

	// Camera
	private CameraInitHandler cameraInitHandler;
	private CameraRecorder cameraRecorder;
	private CameraRunHandler cameraRunHandler;

	// File
	private File mainDirectory;
	private BracValueFileHandler bracFileHandler;
	private ImageFileHandler imgFileHandler;
	private BracValueDebugHandler bracDebugHandler;

	// Uploader
	private BracDataHandler BDH;

	private RelativeLayout main_layout;
	private TestQuestionMsgBoxInterface msgBox;

	private LoadingHandler loadingHandler;
	private FailBgHandler failBgHandler;
	private MsgLoadingHandler msgLoadingHandler;
	private TestHandler testHandler;
	private TimeUpHandler timeUpHandler;
	private ChangeTabsHandler changeTabsHandler;
	private CountDownHandler countDownHandler;

	private RelativeLayout startLayout;
	private ImageView bg, startButton, stroke, testCircle;
	private TextView bracText;
	private TextView brac;
	private TextView startText;

	private FrameLayout preview_layout;

	private ImageView helpButton;

	private static Object init_lock = new Object();
	private static Object done_lock = new Object();

	private ScrollView debugScrollView;
	private EditText debugMsg;
	private ChangeMsgHandler msgHandler;

	private static final int[] BLOW_RESOURCE = { 0, R.drawable.test_circle1,
			R.drawable.test_circle2, R.drawable.test_circle3, R.drawable.test_circle4,
			R.drawable.test_circle5, R.drawable.test_circle6 };
	private Drawable[] blowDrawables;

	private ImageView face;

	private QuestionFile questionFile;

	private Typeface digitTypeface, digitTypefaceBold, wordTypefaceBold;

	private DecimalFormat format;

	private Point screen;

	private int count_down_sec = 0;
	private TextView countDownText;

	private String test_guide_end;
	private String test_guide_not_turn_on;
	private String test_guide_test_fail;
	private String test_guide_test_timeout;
	private String test_guide_connect_fail;
	private String test_guide_multi_blow;
	private String test_guide_zero_duration;

	private String[] test_guide_msg;

	private Thread start_thread = null;

	private SharedPreferences sp;

	private NotificationBox notificationBox;

	private static SoundPool soundpool;
	private static int soundId;

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
		digitTypefaceBold = Typefaces.getDigitTypeface(getActivity());
		wordTypefaceBold = Typefaces.getWordTypefaceBold(getActivity());
		test_guide_end = getString(R.string.test_guide_end);
		test_guide_not_turn_on = getString(R.string.test_guide_not_turn_on);
		test_guide_test_fail = getString(R.string.test_guide_test_fail);
		test_guide_test_timeout = getString(R.string.test_guide_test_timeout);
		test_guide_connect_fail = getString(R.string.test_guide_connect_fail);
		test_guide_msg = getResources().getStringArray(R.array.test_guide_msg);
		test_guide_multi_blow = getString(R.string.test_guide_multi_blow);
		test_guide_zero_duration = getString(R.string.test_guide_zero_duration);
		sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (soundpool == null) {
			soundpool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
			soundId = soundpool.load(this.getActivity(), R.raw.short_beep, 1);
		}
	}

	public void onPause() {
		SimpleBluetooth.closeConnection();
		if (count_down_thread != null && count_down_thread.isAlive())
			count_down_thread.interrupt();
		if (start_thread != null && start_thread.isAlive())
			start_thread.interrupt();
		if (!isKeepMsgBox()) {
			stop();
			clear();
			AlwaysFinishActivitiesCheck.dismiss();
		}
		if (notificationBox != null)
			notificationBox.dismiss();

		super.onPause();
	}

	public void onResume() {
		super.onResume();
		checkDebug(sp.getBoolean("debug", false), sp.getBoolean("debug_type", false));
		if (!isKeepMsgBox()) {
			activity = this.getActivity();
			testFragment = this;
			settingOnResume();
			loadingHandler.sendEmptyMessage(0);
			FragmentTabs.enableTabAndClick(true);
		} else {
			setKeepMsgBox(false);
			runGPS();
		}
	}

	private void settingOnResume() {
		if (msgBox == null) {
			if (Build.VERSION.SDK_INT >= 14)
				msgBox = new TestQuestionMsgBox(testFragment, main_layout);
			else
				msgBox = new TestQuestionMsgBoxOld(testFragment, main_layout);
		}
		if (notificationBox == null)
			notificationBox = new NotificationBox(testFragment.getActivity(), main_layout);

		loadingHandler = new LoadingHandler();
		msgLoadingHandler = new MsgLoadingHandler();
		failBgHandler = new FailBgHandler();
		testHandler = new TestHandler();
		timeUpHandler = new TimeUpHandler();
		changeTabsHandler = new ChangeTabsHandler();
		countDownHandler = new CountDownHandler();
	}

	public void reset() {
		SimpleBluetooth.closeConnection();
		
		timestamp = setTimeStamp();
		setStorage();
		locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		if (Build.VERSION.SDK_INT >= 9)
			cameraRecorder = new CameraRecorder(testFragment, imgFileHandler);
		else
			cameraRecorder = new CameraRecorderOld(testFragment, imgFileHandler);

		cameraRunHandler = new CameraRunHandler(cameraRecorder);
		Boolean debug = sp.getBoolean("debug", false);
		Boolean debug_type = sp.getBoolean("debug_type", false);

		if (Build.VERSION.SDK_INT >= 14) {
			if (debug) {
				if (debug_type)
					bt = new BluetoothDebugModeNormal(testFragment, cameraRunHandler,
							bracFileHandler, bracDebugHandler);
				else
					bt = new BluetoothDebugMode(testFragment, cameraRunHandler, bracFileHandler,
							bracDebugHandler);
			} else
				bt = new Bluetooth(testFragment, cameraRunHandler, bracFileHandler, true);
		} else
			bt = new BluetoothOld(testFragment, cameraRunHandler, bracFileHandler, bracDebugHandler);

		for (int i = 0; i < 3; ++i)
			INIT_PROGRESS[i] = DONE_PROGRESS[i] = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.test_fragment, container, false);
		screen = ScreenSize.getScreenSize(getActivity());
		if (screen == null) {
			if (activity != null)
				activity.finish();
			else
				return view;
		}
		bg = (ImageView) view.findViewById(R.id.test_background);
		bg.setKeepScreenOn(true);
		startLayout = (RelativeLayout) view.findViewById(R.id.test_start_layout);
		startButton = (ImageView) view.findViewById(R.id.test_start_button);
		bracText = (TextView) view.findViewById(R.id.test_brac_value_text);
		brac = (TextView) view.findViewById(R.id.test_brac_text);
		startText = (TextView) view.findViewById(R.id.test_start_button_text);
		stroke = (ImageView) view.findViewById(R.id.test_start_stroke);

		helpButton = (ImageView) view.findViewById(R.id.help_background);

		face = (ImageView) view.findViewById(R.id.test_face);

		testCircle = (ImageView) view.findViewById(R.id.test_start_circle);

		main_layout = (RelativeLayout) view.findViewById(R.id.test_fragment_main_layout);

		bracText.setTextSize(TypedValue.COMPLEX_UNIT_PX, TextSize.xxlargeTextSize(activity));
		bracText.setTypeface(digitTypeface);

		startText.setTextSize(TypedValue.COMPLEX_UNIT_PX, TextSize.largeTextSize(activity));
		startText.setTypeface(wordTypefaceBold);

		countDownText = (TextView) view.findViewById(R.id.test_start_count_down_text);
		countDownText.setTextSize(TypedValue.COMPLEX_UNIT_PX, TextSize.xlargeTextSize(activity));
		countDownText.setTypeface(digitTypefaceBold);

		brac.setTextSize(TypedValue.COMPLEX_UNIT_PX, TextSize.normalTextSize(activity));
		brac.setTypeface(wordTypefaceBold);

		messageView = (TextView) view.findViewById(R.id.test_message);
		messageView.setTextSize(TypedValue.COMPLEX_UNIT_PX, TextSize.normalTextSize(activity));
		messageView.setTypeface(wordTypefaceBold);

		RelativeLayout.LayoutParams mParam = (LayoutParams) messageView.getLayoutParams();
		mParam.topMargin = screen.x * 40 / 480;

		preview_layout = (FrameLayout) view.findViewById(R.id.test_camera_preview_layout);

		debugScrollView = (ScrollView) view.findViewById(R.id.debug_scroll_view);
		debugMsg = (EditText) view.findViewById(R.id.debug_msg);

		RelativeLayout.LayoutParams bParam = (RelativeLayout.LayoutParams) bg.getLayoutParams();
		bParam.width = screen.x;
		bParam.height = bParam.width * 1712 / 1080;

		int start_size = screen.x * 255 / 480;
		RelativeLayout.LayoutParams startLayoutParam = (LayoutParams) startLayout.getLayoutParams();
		startLayoutParam.topMargin = screen.x * 192 / 480;
		startLayoutParam.width = startLayoutParam.height = start_size;
		RelativeLayout.LayoutParams startButtonParam = (LayoutParams) startButton.getLayoutParams();
		startButtonParam.width = startButtonParam.height = screen.x * 208 / 480;
		RelativeLayout.LayoutParams startStrokeParam = (LayoutParams) stroke.getLayoutParams();
		startStrokeParam.width = startStrokeParam.height = start_size;
		RelativeLayout.LayoutParams testCircleParam = (LayoutParams) testCircle.getLayoutParams();
		testCircleParam.width = testCircleParam.height = start_size;

		RelativeLayout.LayoutParams previewParam = (LayoutParams) preview_layout.getLayoutParams();
		previewParam.width = screen.x * 254 / 480;
		previewParam.height = screen.x * 254 / 480;
		previewParam.topMargin = screen.x * 180 / 480;

		RelativeLayout.LayoutParams helpLayoutParam = (LayoutParams) helpButton.getLayoutParams();
		helpLayoutParam.topMargin = screen.x * 6 / 480;
		helpLayoutParam.rightMargin = screen.x * 6 / 480;

		int padding = screen.x * 20 / 480;
		helpButton.setPadding(padding, padding, padding, padding);
		helpButton.setOnTouchListener(new ScaleOnTouchListener());

		RelativeLayout.LayoutParams bracVParam = (LayoutParams) bracText.getLayoutParams();
		bracVParam.topMargin = screen.x * 95 / 480;

		RelativeLayout.LayoutParams bracParam = (LayoutParams) brac.getLayoutParams();
		bracParam.topMargin = screen.x * 18 / 480;

		return view;
	}

	public void startGPS(boolean enable) {
		msgBox.generateWaitingBox();
		if (enable) {
			gps_state = true;
			Object[] gps_enable = { gps_state };
			gpsInitTask = new GPSInitTask(testFragment, locationManager);
			gpsInitTask.execute(gps_enable);
		} else {
			gps_state = false;
			runGPS();
		}
	}

	public void runGPS() {
		if (gps_state) {
			gpsInitTask.cancel(true);
			Log.d(TAG, "GPS: start the service");
			Intent gpsIntent = new Intent(activity, GPSService.class);
			Bundle data = new Bundle();
			data.putString("directory", timestamp);
			gpsIntent.putExtras(data);
			activity.startService(gpsIntent);
			SharedPreferences.Editor edit = sp.edit();
			edit.putLong("LatestGPSTime", System.currentTimeMillis());
			edit.putString("LatestGPSTimestamp", timestamp);
			edit.commit();
			updateDoneState(_GPS);
		} else {
			SharedPreferences.Editor edit = sp.edit();
			edit.putLong("LatestGPSTime", 0);
			edit.putString("LatestGPSTimestamp", "0");
			edit.commit();
			updateDoneState(_GPS);
		}
	}

	public void startBT() {
		// messageView.setText(R.string.wait);
		// initialize bt task
		btInitHandler = new BTInitHandler(testFragment, bt);
		btInitHandler.sendEmptyMessage(0);
		// initialize camera task
		cameraInitHandler = new CameraInitHandler(testFragment, cameraRecorder);
		cameraInitHandler.sendEmptyMessage(0);
	}

	public void runBT() {
		if (testHandler == null)
			testHandler = new TestHandler();
		testHandler.sendEmptyMessage(0);
	}

	public void failBT() {
		messageView.setText("");
		clear();
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("msg", test_guide_not_turn_on);
		msg.setData(data);
		msg.what = 0;
		if (failBgHandler != null)
			failBgHandler.sendMessage(msg);
	}

	private class StartOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {

			if (!FragmentTabs.getClickable())
				return;

			if (DefaultCheck.check(activity)) {
				CustomToastSmall.generateToast(activity, R.string.default_forbidden);
				return;
			}

			if (notificationBox != null)
				notificationBox.dismiss();

			ClickLogger.Log(activity, ClickLogId.TEST_START_BUTTON);
			boolean firstTime = sp.getBoolean("first", true);
			helpButton.setOnClickListener(null);
			if (firstTime) {
				messageView.setText("");
				SharedPreferences.Editor editor = sp.edit();
				editor.putBoolean("first", false);
				editor.commit();
				showTutorial();
			} else {
				long lastTime = sp.getLong("LatestTestTime", 0);
				long curTime = System.currentTimeMillis();
				Boolean debug = sp.getBoolean("debug", false);
				boolean testFail = sp.getBoolean("testFail", false);
				long TEST_GAP_DURATION = testFail ? TEST_GAP_DURATION_SHORT
						: TEST_GAP_DURATION_LONG;
				long time = curTime - lastTime;
				if (time > TEST_GAP_DURATION || debug) {
					bg.setOnTouchListener(null);
					startButton.setOnClickListener(null);
					startButton.setEnabled(false);
					startText.setVisibility(View.INVISIBLE);
					bracText.setText("0.00");
					reset();
					messageView.setText(R.string.test_guide_show_turn_on);
					start_thread = new Thread(new TimeUpRunnable(0, 100));
					start_thread.start();
				} else {
					if (testFail) {
						if (time > 30000)
							CustomToastSmall.generateToast(getActivity(),
									R.string.testTimeCheckToastHalf);
						else
							CustomToastSmall.generateToast(getActivity(),
									R.string.testTimeCheckToastShort);

					} else {
						if (time > 90000)
							CustomToastSmall.generateToast(getActivity(),
									R.string.testTimeCheckToastHalf);
						else if (time > 60000)
							CustomToastSmall.generateToast(getActivity(),
									R.string.testTimeCheckToastShort);
						else
							CustomToastSmall.generateToast(getActivity(),
									R.string.testTimeCheckToastLong);
					}
				}
			}
		}
	}

	private class EndTestOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			ClickLogger.Log(getActivity(), ClickLogId.TEST_RESTART_BUTTON);
			stopDueToInit();
			if (loadingHandler != null)
				loadingHandler.sendEmptyMessage(0);
		}
	}

	private String setTimeStamp() {
		long time_in_sec = System.currentTimeMillis();
		DecimalFormat d = new DecimalFormat("0");
		return d.format(time_in_sec / 1000L);
	}

	private void setStorage() {
		String state = Environment.getExternalStorageState();
		File dir = null;
		if (state.equals(Environment.MEDIA_MOUNTED))
			dir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
		else
			dir = new File(this.getActivity().getFilesDir(), "drunk_detection");
		if (!dir.exists())
			if (!dir.mkdirs())
				Log.d(TAG, "Storage: FAIL TO CREATE DIR");

		mainDirectory = new File(dir, timestamp);
		if (!mainDirectory.exists())
			if (!mainDirectory.mkdirs()) {
				return;
			}

		bracFileHandler = new BracValueFileHandler(mainDirectory, timestamp);
		bracDebugHandler = new BracValueDebugHandler(mainDirectory, timestamp);
		imgFileHandler = new ImageFileHandler(mainDirectory, timestamp);
		questionFile = new QuestionFile(mainDirectory);
	}

	private Thread count_down_thread;

	public void updateInitState(int type) {
		synchronized (init_lock) {
			if (INIT_PROGRESS[type] == true)
				return;
			INIT_PROGRESS[type] = true;
			if (INIT_PROGRESS[_BT] && INIT_PROGRESS[_CAMERA]) {
				btInitHandler.removeMessages(0);
				cameraInitHandler.removeMessages(0);
				btRunTask = new BTRunTask(this, bt);
				btRunTask.execute();
				messageView.setText(R.string.test_guide_turn_on);
				showDebug("Device launched");
				Boolean debug = sp.getBoolean("debug", false);

				if (debug)
					count_down_thread = new Thread(new CountDownRunnable(1,
							COUNT_DOWN_SECOND_DEVELOP));
				else
					count_down_thread = new Thread(new CountDownRunnable(1, COUNT_DOWN_SECOND));
				count_down_thread.start();
			}
		}
	}

	public void updateDoneState(int type) {
		synchronized (done_lock) {
			if (DONE_PROGRESS[type] == true)
				return;
			DONE_PROGRESS[type] = true;

			if (!DONE_PROGRESS[_GPS] && DONE_PROGRESS[_BT] && DONE_PROGRESS[_CAMERA]) {
				stop();
				if (msgBox == null)
					msgBox = new TestQuestionMsgBox(testFragment, main_layout);
				if (msgLoadingHandler == null)
					msgLoadingHandler = new MsgLoadingHandler();
				msgLoadingHandler.sendEmptyMessage(0);
			}
		}
		if (DONE_PROGRESS[_GPS] && DONE_PROGRESS[_BT] && DONE_PROGRESS[_CAMERA]) {
			Boolean debug = sp.getBoolean("debug", false);
			Boolean debug_type = sp.getBoolean("debug_type", false);
			if (debug) {
				if (debug_type)
					BDH = new BracDataHandlerDebugModeNormal(timestamp, testFragment);
				else
					BDH = new BracDataHandlerDebugMode(timestamp, testFragment);
			} else
				BDH = new BracDataHandler(timestamp, testFragment);
			BDH.start();

			if (!gps_state)
				DataUploader.upload(getActivity());

			changeTabsHandler.sendEmptyMessage(0);
		}
	}

	public void stopDueToInit() {
		if (cameraRecorder != null)
			cameraRecorder.close();

		if (bt != null)
			bt = null;
		if (gpsInitTask != null)
			gpsInitTask.cancel(true);
		if (btInitHandler != null)
			btInitHandler.removeMessages(0);
		if (cameraInitHandler != null)
			cameraInitHandler.removeMessages(0);
		if (btRunTask != null)
			btRunTask.cancel(true);

		if (timeUpHandler != null) {
			timeUpHandler.removeMessages(0);
			timeUpHandler.removeMessages(1);
		}
		if (testHandler != null)
			testHandler.removeMessages(0);
	}

	public void stop() {
		if (cameraRecorder != null)
			cameraRecorder.close();

		if (bt != null)
			bt.close();
		if (gpsInitTask != null)
			gpsInitTask.cancel(true);
		if (btInitHandler != null)
			btInitHandler.removeMessages(0);
		if (cameraInitHandler != null)
			cameraInitHandler.removeMessages(0);
		if (btRunTask != null)
			btRunTask.cancel(true);
		if (loadingHandler != null) {
			loadingHandler.removeMessages(0);
			loadingHandler = null;
		}
		if (msgLoadingHandler != null) {
			msgLoadingHandler.removeMessages(0);
			msgLoadingHandler = null;
		}
		if (testHandler != null) {
			testHandler.removeMessages(0);
			testHandler = null;
		}
		if (failBgHandler != null) {
			failBgHandler.removeMessages(0);
			failBgHandler = null;
		}
		if (msgHandler != null) {
			msgHandler.removeMessages(0);
			msgHandler = null;
		}
		if (countDownHandler != null) {
			countDownHandler.removeMessages(0);
			countDownHandler = null;
		}
		if (timeUpHandler != null) {
			timeUpHandler.removeMessages(0);
			timeUpHandler.removeMessages(1);
		}
		if (changeTabsHandler != null) {
			changeTabsHandler.removeMessages(0);
		}
	}

	private void clear() {
		if (msgBox != null) {
			msgBox.clear();
			msgBox = null;
		}
		if (countDownText != null)
			countDownText.setText("");
	}

	@SuppressLint("HandlerLeak")
	private class LoadingHandler extends Handler {
		public void handleMessage(Message msg) {
			testCircle.setImageDrawable(null);
			bracText.setText("0.00");
			bracText.setVisibility(View.VISIBLE);
			messageView.setText(R.string.test_guide_start);
			startButton.setOnClickListener(new StartOnClickListener());
			startButton.setEnabled(true);
			startButton.setVisibility(View.VISIBLE);
			startText.setVisibility(View.VISIBLE);
			startText.setText(R.string.start);
			helpButton.setOnClickListener(new TutorialOnClickListener());
			face.setVisibility(View.INVISIBLE);

			if (notificationBox == null)
				notificationBox = new NotificationBox(testFragment.getActivity(), main_layout);
			notificationBox.show(NotificationBox.getType(getActivity()));

			LoadingDialogControl.dismiss();

			/*
			 * if (msgBox==null){ if (Build.VERSION.SDK_INT >= 14) msgBox = new
			 * TestQuestionMsgBox(testFragment,main_layout); else msgBox = new
			 * TestQuestionMsgBoxOld(testFragment,main_layout); }
			 * msgBox.settingPreTask(); msgBox.settingInBackground();
			 * msgBox.settingPostTask(); msgBox.generateGPSCheckBox();
			 * messageView.setText(R.string.test_guide_msg_box);
			 */
		}
	}

	@SuppressLint("HandlerLeak")
	private class MsgLoadingHandler extends Handler {
		public void handleMessage(Message msg) {
			startButton.setOnClickListener(null);
			startButton.setEnabled(false);
			if (msgBox == null) {
				if (Build.VERSION.SDK_INT >= 14)
					msgBox = new TestQuestionMsgBox(testFragment, main_layout);
				else
					msgBox = new TestQuestionMsgBoxOld(testFragment, main_layout);
			}
			msgBox.settingPreTask();
			msgBox.settingInBackground();
			msgBox.settingPostTask();
			msgBox.generateGPSCheckBox();
			messageView.setText(R.string.test_guide_msg_box);
		}
	}

	@SuppressLint("HandlerLeak")
	private class FailBgHandler extends Handler {

		public void handleMessage(Message msg) {
			if (msgLoadingHandler != null) {
				msgLoadingHandler.removeMessages(0);
				msgLoadingHandler = null;
			}
			if (testHandler != null) {
				testHandler.removeMessages(0);
				testHandler = null;
			}

			if (count_down_thread != null && count_down_thread.isAlive())
				count_down_thread.interrupt();
			countDownText.setText("");

			String msgStr = msg.getData().getString("msg");

			startButton.setOnClickListener(new EndTestOnClickListener());
			startButton.setEnabled(true);
			startButton.setVisibility(View.VISIBLE);
			startText.setVisibility(View.VISIBLE);
			startText.setText(R.string.ok);
			face.setVisibility(View.INVISIBLE);
			if (!msgStr.equals(test_guide_zero_duration))
				msgStr = msgStr.concat(test_guide_end);
			messageView.setText(msgStr);
		}
	}

	@SuppressLint("HandlerLeak")
	private class TestHandler extends Handler {
		public void handleMessage(Message msg) {
			startButton.setOnClickListener(null);
			startButton.setEnabled(false);
			if (blowDrawables == null) {
				blowDrawables = new Drawable[BLOW_RESOURCE.length];
				for (int i = 1; i < blowDrawables.length; ++i)
					blowDrawables[i] = activity.getResources().getDrawable(BLOW_RESOURCE[i]);
			}

			face.setVisibility(View.VISIBLE);

			messageView.setText(R.string.test_guide_testing);

			if (bt != null && cameraRecorder != null) {
				bt.start();
				cameraRecorder.start();
				bracText.setText("0.00");
			}
		}
	}

	private int prev_drawable_time = -1;

	public void changeTestMessage(float value, int time) {
		bracText.setText(format.format(value));
		if (time >= blowDrawables.length)
			time = blowDrawables.length - 1;
		if (prev_drawable_time == time)
			return;
		prev_drawable_time = time;
		if (blowDrawables != null)
			testCircle.setImageDrawable(blowDrawables[time]);

		if (time == 2) {
			Random rand = new Random();
			int idx = rand.nextInt(test_guide_msg.length);
			messageView.setText(test_guide_msg[idx]);
		}
	}

	public void stopByFail(int fail) {
		Message msg = new Message();
		Bundle data = new Bundle();
		switch (fail) {
		case 0:
			data.putString("msg", test_guide_connect_fail);
			break;
		case 1:
			data.putString("msg", test_guide_test_fail);
			break;
		case 2:
			data.putString("msg", test_guide_test_timeout);
			break;
		case 3:
			data.putString("msg", test_guide_multi_blow);
			break;
		case 4:
			data.putString("msg", test_guide_zero_duration);
			break;
		default:
			data.putString("msg", test_guide_test_timeout);
			break;
		}
		msg.setData(data);
		msg.what = 0;
		if (failBgHandler != null)
			failBgHandler.sendMessage(msg);
	}

	public boolean isKeepMsgBox() {
		return keepMsgBox;
	}

	public void setKeepMsgBox(boolean keepMsgBox) {
		this.keepMsgBox = keepMsgBox;
	}

	@SuppressLint("HandlerLeak")
	private class TimeUpHandler extends Handler {
		public void handleMessage(Message msg) {
			int t = msg.what;
			if (t == 0) {
				showDebug(">Try to start the device");
				startBT();
			} else if (t == 1) {
				startButton.setVisibility(View.INVISIBLE);
				countDownText.setText("");
				showDebug(">Start to run the  device");
				runBT();
			}
		}
	}

	private class TimeUpRunnable implements Runnable {

		int msg;
		int time;

		public TimeUpRunnable(int msg, int time) {
			this.msg = msg;
			this.time = time;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(time);
				timeUpHandler.sendEmptyMessage(msg);
			} catch (InterruptedException e) {
			}
		}

	}

	private class CountDownRunnable implements Runnable {

		private static final int SECOND_FIX = 1300;

		int msg;
		int sec;

		public CountDownRunnable(int msg, int sec) {
			this.msg = msg;
			this.sec = sec;
			count_down_sec = sec;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(SECOND_FIX);
				for (int i = 0; i < sec; ++i) {
					Thread.sleep(SECOND_FIX);
					countDownHandler.sendEmptyMessage(0);
				}
				timeUpHandler.sendEmptyMessageDelayed(msg, SECOND_FIX);
			} catch (InterruptedException e) {
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private class CountDownHandler extends Handler {
		public void handleMessage(Message msg) {
			--count_down_sec;
			soundpool.play(soundId, 0.6f, 0.6f, 0, 0, 1.f);
			countDownText.setText(String.valueOf(count_down_sec));
		}
	}

	@SuppressLint("HandlerLeak")
	private class ChangeTabsHandler extends Handler {
		public void handleMessage(Message msg) {
			if (msgBox != null)
				msgBox.closeBox();
			FragmentTabs.enableTabAndClick(true);
			FragmentTabs.changeTab(1);
		}
	}

	private class TutorialOnClickListener implements View.OnClickListener {
		public void onClick(View v) {
			if (!FragmentTabs.getClickable())
				return;
			ClickLogger.Log(getActivity(), ClickLogId.TEST_TUTORIAL_BUTTON);
			showTutorial();
		}
	}

	private void showTutorial() {
		Intent intent = new Intent();
		intent.setClass(activity, TutorialActivity.class);
		activity.startActivity(intent);
	}

	public void writeQuestionFile(int emotion, int desire) {
		questionFile.write(emotion, desire);
	}

	public void setPairMessage() {
		messageView.setText(R.string.test_guide_pair);
	}

	// Debug
	// --------------------------------------------------------------------------------------------------------

	private void checkDebug(boolean debug, boolean debug_type) {
		SimpleBluetooth.closeConnection();
		RelativeLayout debugLayout = (RelativeLayout) view.findViewById(R.id.debugLayout);
		if (debug) {
			debugLayout.setVisibility(View.VISIBLE);
			msgHandler = new ChangeMsgHandler();
			debugMsg.setText("");
			debugMsg.setOnKeyListener(null);
			TextView debugText = (TextView) view.findViewById(R.id.debug_mode_text);
			
			Button modeButton =(Button) view.findViewById(R.id.debug_mode_change);
			
			modeButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					SharedPreferences.Editor edit = sp.edit();
					edit.putBoolean("debug_type", !sp.getBoolean("debug_type", false));
					edit.commit();
					checkDebug(sp.getBoolean("debug", false), sp.getBoolean("debug_type", false));
				}
			});
			if (!debug_type){
				modeButton.setText("->avm");
				debugText.setText("Training(acvm)");
			}
			else{
				modeButton.setText("->acvm");
				debugText.setText("Testing(avm)");
			}
			Button[] conditionButtons = new Button[4];
			conditionButtons[0] = (Button) view.findViewById(R.id.debug_button_1);
			conditionButtons[1] = (Button) view.findViewById(R.id.debug_button_2);
			conditionButtons[2] = (Button) view.findViewById(R.id.debug_button_3);
			conditionButtons[3] = (Button) view.findViewById(R.id.debug_button_4);
			for (int i = 0; i < 4; ++i)
				conditionButtons[i].setOnClickListener(new ConditionOnClickListener(i));
			
			Button volButton = (Button) view.findViewById(R.id.debug_voltage);
			volButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					SimpleBluetooth.getInitialVoltage(testFragment);
				}
			});
			TextView vol_tv = (TextView) view.findViewById(R.id.debug_voltage_value);
			vol_tv.setText("NULL");

		} else {
			debugLayout.setVisibility(View.INVISIBLE);
			return;
		}

	}

	private class ConditionOnClickListener implements View.OnClickListener {

		private int cond;

		public ConditionOnClickListener(int cond) {
			this.cond = cond;
		}

		@Override
		public void onClick(View v) {
			SharedPreferences.Editor editor = sp.edit();
			editor.putInt("latest_result", cond);
			editor.putBoolean("tested", true);
			editor.putBoolean("hourly_alarm", false);
			editor.commit();
			FragmentTabs.changeTab(1);
		}
	}

	public void showDebugVoltage(String message){
		TextView vol_tv = (TextView) view.findViewById(R.id.debug_voltage_value);
		vol_tv.setText(message);
		vol_tv.invalidate();
	}
	
	public void showDebug(String message,int type) {
		if (this == null)
			return;
		Boolean debug = sp.getBoolean("debug", false);
		if (debug && msgHandler != null) {
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putInt("type", type);
			data.putString("message", message);
			msg.setData(data);
			msg.what = 0;
			msgHandler.sendMessage(msg);
		}
	}
	
	public void showDebug(String message) {
		showDebug(message,0);
	}

	@SuppressLint("HandlerLeak")
	private class ChangeMsgHandler extends Handler {
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			int type = data.getInt("type");
			if (type == 0){
				debugMsg.append("\n" + data.getString("message"));
				debugScrollView.scrollTo(0, debugMsg.getBottom() + 100);
			}else if (type == 1){
				showDebugVoltage(data.getString("message"));
			}
		}
	}

}
