package history.ui;

import history.data.AudioUploader;

import java.io.File;
import java.io.IOException;

import database.AudioDB;

import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.activities.HistoryFragment;
import ubicomp.drunk_detection.activities.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
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
	
	private TextView help;
	private ImageView closeButton,recButton,playButton;
	
	private RelativeLayout mainLayout;
	
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
	
	private final static int MAX_MEDIA_DURATION = 120000;
	
	private Bitmap playBmp, recBmp, stopBmp, bgBmp, closeBmp;
	
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
		
		wordTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dfheistd-w3.otf");
		
		boxLayout = (RelativeLayout) inflater.inflate(R.layout.rec_layout,null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		help = (TextView) boxLayout.findViewById(R.id.rec_help);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 64/1080);
		help.setTypeface(wordTypeface);
		RelativeLayout.LayoutParams hParam = (LayoutParams) help.getLayoutParams();
		hParam.width = screen.x * 700/1080;
		hParam.height = screen.x * 160/1080;
		
		mainLayout.addView(boxLayout);
		
		RelativeLayout.LayoutParams param = (LayoutParams) boxLayout.getLayoutParams();
		param.width = screen.x * 830/1080;
		param.height = screen.x * 345/1080;
		param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		
		closeButton = (ImageView) boxLayout.findViewById(R.id.rec_close_button);
		closeButton.setOnClickListener(new EndListener());
		RelativeLayout.LayoutParams cParam = (LayoutParams) closeButton.getLayoutParams();
		cParam.width = screen.x * 77/1080;
		cParam.height = screen.x * 77/1080;
		
		recButton = (ImageView) boxLayout.findViewById(R.id.rec_rec_button);
		RelativeLayout.LayoutParams rParam = (LayoutParams) recButton.getLayoutParams();
		rParam.width = screen.x * 100/1080;
		rParam.height = screen.x * 160/1080;
		rParam.topMargin = screen.x * 20/1080;
		rParam.rightMargin = screen.x * 200/1080; 
		playButton = (ImageView) boxLayout.findViewById(R.id.rec_play_button);
		RelativeLayout.LayoutParams pParam = (LayoutParams) playButton.getLayoutParams();
		pParam.width = screen.x * 100/1080;
		pParam.height = screen.x * 160/1080;
		pParam.topMargin = screen.x * 20/1080;
		pParam.leftMargin = screen.x * 200/1080; 
		
	}
	
	public void setImage(){
		Bitmap tmp;
		tmp=BitmapFactory.decodeResource(context.getResources(), R.drawable.record_bg);
		bgBmp = Bitmap.createScaledBitmap(tmp, screen.x * 830/1080, screen.x * 345/1080, true);
		tmp.recycle();
		tmp=BitmapFactory.decodeResource(context.getResources(), R.drawable.record_rec);
		recBmp = Bitmap.createScaledBitmap(tmp, screen.x * 49/1080, screen.x * 49/1080, true);
		tmp.recycle();
		tmp=BitmapFactory.decodeResource(context.getResources(), R.drawable.record_play);
		playBmp = Bitmap.createScaledBitmap(tmp, screen.x * 56/1080, screen.x * 49/1080, true);
		tmp.recycle();
		tmp=BitmapFactory.decodeResource(context.getResources(), R.drawable.record_stop);
		stopBmp = Bitmap.createScaledBitmap(tmp, screen.x * 49/1080, screen.x * 49/1080, true);
		tmp.recycle();
		tmp=BitmapFactory.decodeResource(context.getResources(), R.drawable.question_close);
		closeBmp = Bitmap.createScaledBitmap(tmp, screen.x * 77/1080, screen.x * 77/1080, true);
		tmp.recycle();
		
		boxLayout.setBackground(new BitmapDrawable(context.getResources(),bgBmp));
		closeButton.setImageBitmap(closeBmp);
	}
	
	public void clear(){
		closeButton.setImageBitmap(null);
		boxLayout.setBackground(null);
		playButton.setImageBitmap(null);
		recButton.setImageBitmap(null);
		if (boxLayout!=null)
			mainLayout.removeView(boxLayout);
		historyFragment.enablePage(true);
		
		if (bgBmp!=null && !bgBmp.isRecycled()){
			bgBmp.recycle();
			bgBmp = null;
		}
		if (recBmp!=null && !recBmp.isRecycled()){
			recBmp.recycle();
			recBmp = null;
		}
		if (playBmp!=null && !playBmp.isRecycled()){
			playBmp.recycle();
			playBmp = null;
		}
		if (stopBmp!=null && !stopBmp.isRecycled()){
			stopBmp.recycle();
			stopBmp = null;
		}
		if (closeBmp!=null && !closeBmp.isRecycled()){
			closeBmp.recycle();
			closeBmp = null;
		}
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
		help.setText("您對於["+dv.toString()+"]\n的心情(兩分鐘)");
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
					db.InsertAudio(curDV);
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
			recButton.setImageBitmap(recBmp);
			recButton.setOnClickListener(recListener);
			if (db.hasAudio(curDV)){
				playButton.setImageBitmap(playBmp);
				playButton.setOnClickListener(playListener);
			}
			else{
				playButton.setImageBitmap(null);
				playButton.setOnClickListener(null);
			}
			break;
		case STATE_ON_PLAY:
			recButton.setImageBitmap(null);
			recButton.setOnClickListener(null);
			playButton.setImageBitmap(stopBmp);
			playButton.setOnClickListener(endPlayListener);
			break;
		case STATE_ON_RECORD:
			recButton.setImageBitmap(stopBmp);
			recButton.setOnClickListener(endRecListener);
			playButton.setImageBitmap(null);
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
