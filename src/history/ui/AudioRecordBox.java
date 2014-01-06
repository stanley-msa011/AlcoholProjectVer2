package history.ui;



import java.io.File;
import java.io.IOException;


import data.database.AudioDB;
import data.info.DateValue;
import data.uploader.AdditionalDataUploader;
import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;

import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.fragments.HistoryFragment;
import ubicomp.drunk_detection.ui.CustomToast;
import ubicomp.drunk_detection.ui.CustomToastSmall;
import ubicomp.drunk_detection.ui.CustomTypefaceSpan;
import ubicomp.drunk_detection.ui.ScreenSize;
import ubicomp.drunk_detection.ui.TextSize;
import ubicomp.drunk_detection.ui.Typefaces;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class AudioRecordBox {

	private HistoryFragment historyFragment;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout ,contentLayout;
	private View backgroundLayout;
	private TextView help;
	private ImageView closeButton,recButton,playButton;
	
	private RelativeLayout mainLayout;
	
	private Typeface wordTypefaceBold,digitTypefaceBold;
	
	private Point screen;
	
	private DateValue curDV;
	private int curIdx;
	
	private MediaRecorder mediaRecorder;
	private MediaPlayer mediaPlayer;
	
	private File mainDirectory;
	
	private AudioDB db;
	
	private RecListener recListener = new RecListener();
	private EndRecListener endRecListener = new EndRecListener();
	private PlayListener playListener = new PlayListener();
	private EndPlayListener endPlayListener= new EndPlayListener();
	private EndListener endListener = new EndListener();
	
	private final static int MAX_MEDIA_DURATION = 60000;
	
	private Drawable playDrawable,playOffDrawable, recDrawable, stopDrawable;
	
	private String[] helpStr;
	
	private CountDownTimer countDownTimer;
	
	private String record_str,second_str;
	
	private ChangeStateHandler changeStateHandler;
	
	private Spannable helpSpannable;
	
	private Thread media_thread;
	private String playString;
	private MediaUpdateHandler media_handler;
	
	public AudioRecordBox(HistoryFragment historyFragment,RelativeLayout mainLayout){
		this.historyFragment = historyFragment;
		this.context = historyFragment.getActivity();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		backgroundLayout = new View(context);
		backgroundLayout.setBackgroundColor(0x99000000);
		screen = ScreenSize.getScreenSize(context);
		mediaRecorder = null;
		mediaPlayer = null;
		helpStr = context.getResources().getStringArray(R.array.audio_box_help);
		db = new AudioDB(context);
		changeStateHandler = new ChangeStateHandler();
		record_str = context.getString(R.string.audio_box_recording);
		second_str = context.getString(R.string.second);
		playString = context.getString(R.string.emotion_box_playing);
		media_handler = new MediaUpdateHandler();
		setting();
	}
	
	private void setStorage(){
		String state = Environment.getExternalStorageState();
		File dir = null;
		if (state.equals(Environment.MEDIA_MOUNTED))
			dir = new File(Environment.getExternalStorageDirectory(),"drunk_detection");
		else
			dir = new File(historyFragment.getActivity().getFilesDir(),"drunk_detection");
		mainDirectory = new File(dir,"audio_records");
		if (!mainDirectory.exists())
			if (!mainDirectory.mkdirs()){
				Log.d("AUDIO_STORAGE","FAIL TO CREATE DIR");
				return;
			}
	}
	
	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	private void setting(){
		
		backgroundLayout.setVisibility(View.INVISIBLE);
		backgroundLayout.setKeepScreenOn(false);
		
		wordTypefaceBold = Typefaces.getWordTypefaceBold(context);
		digitTypefaceBold = Typefaces.getDigitTypefaceBold(context);
		
		boxLayout = (RelativeLayout) inflater.inflate(R.layout.rec_layout,null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		contentLayout = (RelativeLayout) boxLayout.findViewById(R.id.rec_box_layout);
		
		help = (TextView) boxLayout.findViewById(R.id.rec_help);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, TextSize.normalTextSize(context));
		help.setTypeface(wordTypefaceBold);
		RelativeLayout.LayoutParams hParam = (LayoutParams) help.getLayoutParams();
		hParam.width = screen.x * 349/480;
		hParam.height = screen.x * 114/480;
		
		mainLayout.addView(backgroundLayout);
		mainLayout.addView(boxLayout);
		
		RelativeLayout.LayoutParams bgParam = (LayoutParams) backgroundLayout.getLayoutParams();
		if (Build.VERSION.SDK_INT >=8)
			bgParam.width = bgParam.height = LayoutParams.MATCH_PARENT;
		else
			bgParam.width = bgParam.height = LayoutParams.FILL_PARENT;
		
		RelativeLayout.LayoutParams param = (LayoutParams) boxLayout.getLayoutParams();
		param.width = screen.x * 359/480;
		param.height = screen.x * 199/480;
		param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		
		RelativeLayout.LayoutParams bparam = (LayoutParams) contentLayout.getLayoutParams();
		bparam.width = screen.x * 349/480;
		bparam.height = screen.x * 189/480;
		bparam.topMargin = screen.x *10/480;
		
		closeButton = (ImageView) boxLayout.findViewById(R.id.rec_close_button);
		closeButton.setOnClickListener(endListener);
		
		int padding = screen.x * 20/480;
		closeButton.setPadding(padding, 0, 0, padding);
		
		
		recButton = (ImageView) boxLayout.findViewById(R.id.rec_rec_button);
		RelativeLayout.LayoutParams rParam = (LayoutParams) recButton.getLayoutParams();
		rParam.width = screen.x * 154/480;
		rParam.height = screen.x * 60/480;
		rParam.topMargin = screen.x * 5/480;
		rParam.rightMargin = screen.x * 15/480; 
		playButton = (ImageView) boxLayout.findViewById(R.id.rec_play_button);
		RelativeLayout.LayoutParams pParam = (LayoutParams) playButton.getLayoutParams();
		pParam.width = screen.x * 154/480;
		pParam.height = screen.x * 60/480;
		pParam.topMargin = screen.x * 5/480;
		pParam.leftMargin = screen.x * 35/1480; 
		
	}
	
	public void setImage(){
		recDrawable = context.getResources().getDrawable(R.drawable.record_rec);
		playDrawable = context.getResources().getDrawable(R.drawable.record_play);
		stopDrawable = context.getResources().getDrawable(R.drawable.record_stop);
		playOffDrawable = context.getResources().getDrawable(R.drawable.record_play_off);
	}
	
	public void clear(){
		if (backgroundLayout != null)
			mainLayout.removeView(backgroundLayout);
		
		if (changeStateHandler!=null)
			changeStateHandler.removeMessages(0);
		
		if (boxLayout!=null)
			mainLayout.removeView(boxLayout);
		
		historyFragment.enablePage(true);
	}
	private class EndListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLogger.Log(context, ClickLogId.STORYTELLING_RECORD_CANCEL);
			backgroundLayout.setVisibility(View.INVISIBLE);
			backgroundLayout.setKeepScreenOn(false);
			boxLayout.setVisibility(View.INVISIBLE);
			historyFragment.updateHasRecorder(curIdx);
			historyFragment.enablePage(true);
		}
	}
	
	public void showMsgBox(DateValue dv, int idx){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("LatestStorytellingRecordingTime", System.currentTimeMillis());
		edit.commit();
		
		this.curIdx = idx;
		curDV = dv;
		historyFragment.enablePage(false);
		String cur_date = curDV.toString();
		helpSpannable = new SpannableString(helpStr[0]+cur_date+helpStr[1]);
		int start = 0;
		int end = helpStr[0].length();
		helpSpannable.setSpan(new CustomTypefaceSpan("c1",wordTypefaceBold,0xFF8a8b8b), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start+cur_date.length();
		helpSpannable.setSpan(new CustomTypefaceSpan("c2",digitTypefaceBold,0xFF8a8b8b), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start+helpStr[1].length();
		helpSpannable.setSpan(new CustomTypefaceSpan("c1",wordTypefaceBold,0xFF8a8b8b), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		setButtonState(STATE_INIT);
		backgroundLayout.setVisibility(View.VISIBLE);
		backgroundLayout.setKeepScreenOn(true);
		boxLayout.setVisibility(View.VISIBLE);
	}
	
	private class RecListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			if (curDV == null)
				return;
			setButtonState(STATE_PREPARING);
			setStorage();
			File file = new File(mainDirectory,curDV.toFileString()+".3gp");
			mediaRecorder = new MediaRecorder();
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			mediaRecorder.setOutputFile(file.getAbsolutePath());
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			mediaRecorder.setMaxDuration(MAX_MEDIA_DURATION);
			mediaRecorder.setOnInfoListener(new RecorderListener());
			try {
				mediaRecorder.prepare();
			} catch (IllegalStateException e) {
				setButtonState(STATE_INIT);
			} catch (IOException e) {
				setButtonState(STATE_INIT);
			}
			ClickLogger.Log(context, ClickLogId.STORYTELLING_RECORD_RECORD);
			mediaRecorder.start();
			setButtonState(STATE_ON_RECORD);
		}
	}
	
	private class RecorderListener implements MediaRecorder.OnInfoListener{

		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
				if (mediaRecorder != null){
					try {
						mediaRecorder.stop();
						mediaRecorder.release();
						mediaRecorder = null;
						boolean result = db.insertAudio(curDV);
						
						if (result)
							CustomToast.generateToast(context, R.string.audio_box_toast_timeup, 1);
						else
							CustomToast.generateToast(context, R.string.audio_box_toast_timeup, 0);
						
						historyFragment.updateHasRecorder(curIdx);
					} catch (IllegalStateException e) {}
				}
				ClickLogger.Log(context, ClickLogId.STORYTELLING_RECORD_CANCEL_RECORD);
				AdditionalDataUploader.upload(context);
				setButtonState(STATE_INIT);
			}
		}
	}
	
	private class EndRecListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			setButtonState(STATE_BEFORE_INIT);
			Thread t = new Thread (new WaitRunnable(STATE_INIT));
			t.start();
			
			if (mediaRecorder != null){
				try {
					mediaRecorder.stop();
					mediaRecorder.release();
					mediaRecorder = null;
					boolean result = db.insertAudio(curDV);
					if (result)
						CustomToast.generateToast(context, R.string.audio_box_toast_record_end, 1);
					else
						CustomToast.generateToast(context, R.string.audio_box_toast_record_end, 0);
				} catch (IllegalStateException e) {}
			}
			ClickLogger.Log(context, ClickLogId.STORYTELLING_RECORD_CANCEL_RECORD);
			AdditionalDataUploader.upload(context);
		}
	}
	
	private class PlayListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			setButtonState(STATE_PREPARING);
			setStorage();
			File file = new File(mainDirectory,curDV.toFileString()+".3gp");
			
			mediaPlayer = new MediaPlayer();
			if (file.exists() && db.hasAudio(curDV)){
				try {
					mediaPlayer.setDataSource(file.getAbsolutePath());
					mediaPlayer.setScreenOnWhilePlaying(true);
					mediaPlayer.setVolume(5F, 5F);
					mediaPlayer.prepare();
					media_thread = new Thread(new MediaRunnable());
					media_thread.start();
					mediaPlayer.start();
					mediaPlayer.setOnCompletionListener(
							new OnCompletionListener(){
								@Override
								public void onCompletion(MediaPlayer arg0) {
									try {
										if (media_thread != null && !media_thread.isInterrupted())
											media_thread.interrupt();
										if (media_handler != null)
											media_handler.removeMessages(0);
										mediaPlayer.stop();
										mediaPlayer.release();
										mediaPlayer = null;
										setButtonState(STATE_INIT);
										CustomToastSmall.generateToast(context, R.string.audio_box_toast_play_end);
									} catch (IllegalStateException e) {
									}
								}
							});
				} catch (IllegalArgumentException e) {
					setButtonState(STATE_INIT);
				} catch (SecurityException e) {
					setButtonState(STATE_INIT);
				} catch (IllegalStateException e) {
					setButtonState(STATE_INIT);
				} catch (IOException e) {
					setButtonState(STATE_INIT);
				}
				ClickLogger.Log(context, ClickLogId.STORYTELLING_RECORD_PLAY);
				setButtonState(STATE_ON_PLAY);
			}
		}
	}
	
	private static final int STATE_INIT = 0;
	private static final int STATE_ON_PLAY = 1;
	private static final int STATE_ON_RECORD= 2;
	private static final int STATE_PREPARING = 3;
	private static final int STATE_BEFORE_INIT = 4;
	
	private void setButtonState(int state){
		if (countDownTimer !=null){
			countDownTimer.cancel();
			countDownTimer = null;
		}
		switch (state){
		case STATE_INIT:
			recButton.setImageDrawable(recDrawable);
			recButton.setOnClickListener(recListener);
			closeButton.setOnClickListener(endListener);
			closeButton.setVisibility(View.VISIBLE);
			if (db.hasAudio(curDV)){
				playButton.setImageDrawable(playDrawable);
				playButton.setOnClickListener(playListener);
			}
			else{
				playButton.setImageDrawable(playOffDrawable);
				playButton.setOnClickListener(null);
			}
			help.setText(helpStr[0]+curDV+helpStr[1]);
			break;
		case STATE_ON_PLAY:
			recButton.setImageDrawable(null);
			recButton.setOnClickListener(null);
			playButton.setImageDrawable(stopDrawable);
			playButton.setOnClickListener(endPlayListener);
			closeButton.setOnClickListener(null);
			closeButton.setVisibility(View.INVISIBLE);
			help.setText(R.string.audio_box_playing);
			break;
		case STATE_ON_RECORD:
			recButton.setImageDrawable(stopDrawable);
			recButton.setOnClickListener(endRecListener);
			playButton.setImageBitmap(null);
			playButton.setOnClickListener(null);
			closeButton.setOnClickListener(null);
			closeButton.setVisibility(View.INVISIBLE);
			countDownTimer = new RecordCountDownTimer(MAX_MEDIA_DURATION,1000);
			countDownTimer.start();
			break;
		case STATE_PREPARING:
			recButton.setOnClickListener(null);
			playButton.setOnClickListener(null);
			closeButton.setOnClickListener(null);
			closeButton.setVisibility(View.INVISIBLE);
			help.setText(R.string.audio_box_preparing);
			break;
		case STATE_BEFORE_INIT:
			recButton.setOnClickListener(null);
			playButton.setOnClickListener(null);
			closeButton.setOnClickListener(null);
			closeButton.setVisibility(View.INVISIBLE);
			help.setText(R.string.wait);
			break;
		}
	}
	
	private class EndPlayListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			setButtonState(STATE_BEFORE_INIT);
			Thread t = new Thread (new WaitRunnable(STATE_INIT));
			t.start();
			if (mediaPlayer != null){
				try {
					if (media_thread != null && !media_thread.isInterrupted())
						media_thread.interrupt();
					if (media_handler != null)
						media_handler.removeMessages(0);
					mediaPlayer.stop();
					mediaPlayer.release();
					mediaPlayer = null;
					CustomToastSmall.generateToast(context, R.string.audio_box_toast_play_end);
				} catch (IllegalStateException e) {
				}
			}
			ClickLogger.Log(context, ClickLogId.STORYTELLING_RECORD_CANCEL_PLAY);
		}
	}
	
	public void OnPause(){
		if (mediaRecorder != null){
			try {
				mediaRecorder.stop();
				mediaRecorder.release();
				mediaRecorder = null;
			} catch (IllegalStateException e) {
			}
		}
		if (mediaPlayer !=null){
			try {
				if (media_thread != null && !media_thread.isInterrupted())
					media_thread.interrupt();
				if (media_handler != null)
					media_handler.removeMessages(0);
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			} catch (IllegalStateException e) {
			}
		}
	}
	
	private class WaitRunnable implements Runnable{
		
		private int state;
		
		public WaitRunnable(int state){
			this.state = state;
		}
		
		@Override
		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putInt("STATE", state);
			msg.setData(data);
			msg.what = 0;
			changeStateHandler.sendMessage(msg);
		}
	}
	
	@SuppressLint("HandlerLeak")
	private class ChangeStateHandler extends Handler{
		@Override
		public void handleMessage(Message msg){
			int state = msg.getData().getInt("STATE");
			setButtonState(state);
		}
	}
	
	private class RecordCountDownTimer extends CountDownTimer{
		
		public RecordCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTick(long millisUntilFinished) {
			help.setText(record_str+" "+millisUntilFinished/1000+" "+second_str);
		}
	}
	
	private class MediaRunnable implements Runnable{
		@Override
		public void run() {
			
			try {
				while(true){
					Thread.sleep(1000);
					media_handler.sendEmptyMessage(0);
					Log.d("PLAYING","IN THREAD");
				}
			} catch (InterruptedException e) {}
		}
	}
	
	@SuppressLint("HandlerLeak")
	private class MediaUpdateHandler extends Handler{
		public void handleMessage(Message msg){
			Log.d("PLAYING","IN HANDLER");
			try{
				help.setText(playString+"("+getFormattedTime(mediaPlayer.getCurrentPosition())+"/"+getFormattedTime(mediaPlayer.getDuration())+")");
			}catch(Exception e){}
		}
	}
	
	private static String getFormattedTime(long time){
		time = time/1000L;
		long min = time/60L;
		long sec = time%60L;
		if (sec<10)
			return min+":0"+sec;
		else
			return min+":"+sec;
	}
}
