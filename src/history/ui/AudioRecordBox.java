package history.ui;

import java.io.File;
import java.io.IOException;

import database.AudioDB;

import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.activities.HistoryFragment;
import ubicomp.drunk_detection.activities.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Switch;
import android.widget.TextView;

public class AudioRecordBox {

	private HistoryFragment historyFragment;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	
	private TextView help;
	private Button closeButton,recButton,playButton;
	
	private RelativeLayout mainLayout;
	
	private Typeface digitTypeface;
	private Typeface wordTypeface;
	
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
	
	public AudioRecordBox(HistoryFragment historyFragment,RelativeLayout mainLayout){
		Log.d("UIMSG","NEW");
		this.historyFragment = historyFragment;
		this.context = historyFragment.getActivity();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
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
		
		digitTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dinproregular.ttf");
		wordTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dfheistd-w3.otf");
		
		boxLayout = (RelativeLayout) inflater.inflate(R.layout.rec_layout,null);
		boxLayout.setVisibility(View.INVISIBLE);
		help = (TextView) boxLayout.findViewById(R.id.rec_help);
		help.setTypeface(wordTypeface);
		
		mainLayout.addView(boxLayout);
		
		RelativeLayout.LayoutParams param = (LayoutParams) boxLayout.getLayoutParams();
		param.width = screen.x * 2 / 3;
		param.height = screen.x * 2 / 3;
		param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		
		closeButton = (Button) boxLayout.findViewById(R.id.rec_close_button);
		closeButton.setOnClickListener(new EndListener());
		
		recButton = (Button) boxLayout.findViewById(R.id.rec_rec_button);
		playButton = (Button) boxLayout.findViewById(R.id.rec_play_button);
		
	}
	
	private class EndListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			boxLayout.setVisibility(View.INVISIBLE);
			historyFragment.updateHasRecorder(curIdx);
			historyFragment.enablePage(true);
		}
	}
	
	public void showMsgBox(DateValue dv, int idx){
		this.curIdx = idx;
		curDV = dv;
		historyFragment.enablePage(false);
		help.setText(dv.toString());
		setButtonState(STATE_INIT);
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
	
	private class EndRecListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			if (mediaRecorder != null){
				try {
					mediaRecorder.stop();
					mediaRecorder.release();
					mediaRecorder = null;
					db.InsertAudio(curDV);
				} catch (IllegalStateException e) {
					Log.d("RECORDER",e.getMessage());
				}
			}
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
			recButton.setText("錄音");
			recButton.setOnClickListener(recListener);
			if (db.hasAudio(curDV)){
				playButton.setText("播放");
				playButton.setOnClickListener(playListener);
			}
			else{
				playButton.setText("-");
				playButton.setOnClickListener(null);
			}
			break;
		case STATE_ON_PLAY:
			recButton.setText("-");
			recButton.setOnClickListener(null);
			playButton.setText("停止");
			playButton.setOnClickListener(endPlayListener);
			break;
		case STATE_ON_RECORD:
			recButton.setText("停止");
			recButton.setOnClickListener(endRecListener);
			playButton.setText("-");
			playButton.setOnClickListener(null);
			break;
		case STATE_PREPARING:
			recButton.setOnClickListener(null);
			playButton.setOnClickListener(null);
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
