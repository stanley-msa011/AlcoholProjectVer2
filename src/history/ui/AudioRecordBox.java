package history.ui;


import history.data.AudioUploader;

import java.io.File;
import java.io.IOException;

import database.AudioDB;

import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.activities.HistoryFragment;
import ubicomp.drunk_detection.activities.R;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class AudioRecordBox {

	private HistoryFragment historyFragment;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	private FrameLayout backgroundLayout;
	private TextView help;
	private ImageView closeButton,recButton,playButton;
	
	private RelativeLayout mainLayout;
	
	private Typeface wordTypefaceBold;
	
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
	
	private final static int MAX_MEDIA_DURATION = 120000;
	
	private Drawable playDrawable,playOffDrawable, recDrawable, stopDrawable;
	
	public AudioRecordBox(HistoryFragment historyFragment,RelativeLayout mainLayout){
		Log.d("UIMSG","NEW");
		this.historyFragment = historyFragment;
		this.context = historyFragment.getActivity();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		backgroundLayout = new FrameLayout(context);
		backgroundLayout.setBackgroundColor(0x99000000);
		screen = FragmentTabs.getSize();
		mediaRecorder = null;
		mediaPlayer = null;
		db = new AudioDB(context);
		setting();
	}
	
	private void setStorage(){
		String state = Environment.getExternalStorageState();
		File dir = null;
		if (state.equals(Environment.MEDIA_MOUNTED))
			dir = new File(Environment.getExternalStorageDirectory(),"drunk_detection");
		else
			dir = new File(historyFragment.getActivity().getFilesDir(),"drunk_detection");
		if (!dir.exists())
			if (!dir.mkdirs())
				Log.d("TEST_STORAGE","FAIL TO CREATE DIR");
		
		mainDirectory = new File(dir,"audio_records");
		if (!mainDirectory.exists())
			if (!mainDirectory.mkdirs()){
				return;
			}
	}
	
	private void setting(){
		
		backgroundLayout.setVisibility(View.INVISIBLE);
		
		wordTypefaceBold = Typeface.createFromAsset(context.getAssets(), "fonts/DFLiHeiStd-W5.otf");
		
		boxLayout = (RelativeLayout) inflater.inflate(R.layout.rec_layout,null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		help = (TextView) boxLayout.findViewById(R.id.rec_help);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 21/480);
		help.setTypeface(wordTypefaceBold);
		RelativeLayout.LayoutParams hParam = (LayoutParams) help.getLayoutParams();
		hParam.width = screen.x * 349/480;
		hParam.height = screen.x * 114/480;
		
		mainLayout.addView(backgroundLayout);
		mainLayout.addView(boxLayout);
		
		RelativeLayout.LayoutParams bgParam = (LayoutParams) backgroundLayout.getLayoutParams();
		bgParam.width = bgParam.height = LayoutParams.MATCH_PARENT;
		
		RelativeLayout.LayoutParams param = (LayoutParams) boxLayout.getLayoutParams();
		param.width = screen.x * 349/480;
		param.height = screen.x * 189/480;
		param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		
		closeButton = (ImageView) boxLayout.findViewById(R.id.rec_close_button);
		closeButton.setOnClickListener(endListener);
		
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
		
		if (boxLayout!=null)
			mainLayout.removeView(boxLayout);
		
		historyFragment.enablePage(true);
	}
	private class EndListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			backgroundLayout.setVisibility(View.INVISIBLE);
			boxLayout.setVisibility(View.INVISIBLE);
			historyFragment.updateHasRecorder(curIdx);
			historyFragment.enablePage(true);
		}
	}
	
	public void showMsgBox(DateValue dv, int idx){
		this.curIdx = idx;
		curDV = dv;
		historyFragment.enablePage(false);
		setButtonState(STATE_INIT);
		backgroundLayout.setVisibility(View.VISIBLE);
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
				Log.d("RECORDER",e.getMessage());
				setButtonState(STATE_INIT);
			} catch (IOException e) {
				Log.d("RECORDER",e.getMessage());
				setButtonState(STATE_INIT);
			}
			mediaRecorder.start();
			setButtonState(STATE_ON_RECORD);
		}
	}
	
	private class RecorderListener implements MediaRecorder.OnInfoListener{

		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
				mediaRecorder.release();
				Toast.makeText(mainLayout.getContext(), "兩分鐘到了", Toast.LENGTH_LONG).show();
				setButtonState(STATE_INIT);
			}
		}
	}
	
	private class EndRecListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			if (mediaRecorder != null){
				try {
					mediaRecorder.stop();
					mediaRecorder.release();
					mediaRecorder = null;
					db.insertAudio(curDV);
					Toast.makeText(mainLayout.getContext(), "錄音完成", Toast.LENGTH_LONG).show();
				} catch (IllegalStateException e) {
					Log.d("RECORDER",e.getMessage());
				}
			}
			AudioUploader.upload(context);
			setButtonState(STATE_INIT);
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
					mediaPlayer.start();
					mediaPlayer.setOnCompletionListener(
							new OnCompletionListener(){
								@Override
								public void onCompletion(MediaPlayer arg0) {
									try {
										mediaPlayer.stop();
										mediaPlayer.release();
										mediaPlayer = null;
										setButtonState(STATE_INIT);
									} catch (IllegalStateException e) {
										Log.d("PLAYER",e.getMessage());
									}
								}
							});
				} catch (IllegalArgumentException e) {
					Log.d("PLAYER",e.getMessage());
					setButtonState(STATE_INIT);
				} catch (SecurityException e) {
					Log.d("PLAYER",e.getMessage());
					setButtonState(STATE_INIT);
				} catch (IllegalStateException e) {
					Log.d("PLAYER",e.getMessage());
					setButtonState(STATE_INIT);
				} catch (IOException e) {
					Log.d("PLAYER",e.getMessage());
					setButtonState(STATE_INIT);
				}
				setButtonState(STATE_ON_PLAY);
			}
		}
	}
	
	private static final int STATE_INIT = 0;
	private static final int STATE_ON_PLAY = 1;
	private static final int STATE_ON_RECORD= 2;
	private static final int STATE_PREPARING = 3;
	
	private void setButtonState(int state){
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
			help.setText("錄下或聆聽在"+curDV+"的心情\n(最長可達兩分鐘)");
			break;
		case STATE_ON_PLAY:
			recButton.setImageDrawable(null);
			recButton.setOnClickListener(null);
			playButton.setImageDrawable(stopDrawable);
			playButton.setOnClickListener(endPlayListener);
			closeButton.setOnClickListener(null);
			closeButton.setVisibility(View.INVISIBLE);
			help.setText("播放中");
			break;
		case STATE_ON_RECORD:
			recButton.setImageDrawable(stopDrawable);
			recButton.setOnClickListener(endRecListener);
			playButton.setImageBitmap(null);
			playButton.setOnClickListener(null);
			closeButton.setOnClickListener(null);
			closeButton.setVisibility(View.INVISIBLE);
			help.setText("錄音中");
			break;
		case STATE_PREPARING:
			recButton.setOnClickListener(null);
			playButton.setOnClickListener(null);
			closeButton.setOnClickListener(null);
			closeButton.setVisibility(View.INVISIBLE);
			help.setText("準備中");
			break;
		}
	}
	
	private class EndPlayListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			if (mediaPlayer != null){
				try {
					mediaPlayer.stop();
					mediaPlayer.release();
					mediaPlayer = null;
					Toast.makeText(mainLayout.getContext(), "結束播放", Toast.LENGTH_LONG).show();
				} catch (IllegalStateException e) {
					Log.d("PLAYER",e.getMessage());
				}
			}
			setButtonState(STATE_INIT);
		}
	}
	
	public void OnPause(){
		if (mediaRecorder != null){
			try {
				mediaRecorder.stop();
				mediaRecorder.release();
				mediaRecorder = null;
			} catch (IllegalStateException e) {
				Log.d("RECORDER",e.getMessage());
			}
		}
		if (mediaPlayer !=null){
			try {
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			} catch (IllegalStateException e) {
				Log.d("PLAYER",e.getMessage());
			}
		}
	}
}
