package test.ui;

import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.activities.TestFragment;
import ubicomp.drunk_detection.activities.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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

public class UIMsgBox {

	private TestFragment testFragment;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	
	private TextView help,emotionText,desireText,gpsText;
	private SeekBar emotionSeekBar,desireSeekBar;
	private Switch gpsSwitch;
	private Button okButton;
	
	private RelativeLayout mainLayout;
	
	private ImageView emotionShow;
	private ImageView desireShow;
	private TextView emotionShowText;
	private TextView desireShowText;
	
	private LinearLayout emotionLabel,desireLabel;
	
	private static final String[] emotionStr = {"很沮喪　","　低落　","　普通　", "　愉快　","　　快樂"};
	private static final String[] desireStr = {"無　　　","輕度\n尚未行動","中~強度\n等下去買","非常強烈\n即將要喝"};
	private static final int[] desireWeight = {2,3,3,2};
	
	private LinearLayout questionLayout;
	
	private Resources r;
	private Point screen;
	
	private EndOnClickListener endListener;
	
	private Typeface digitTypeface;
	private Typeface wordTypeface;
	
	private int textSize;
	
	private Bitmap[] emotionBmps;
	private Bitmap[] desireBmps;
	
	
	public UIMsgBox(TestFragment testFragment,RelativeLayout mainLayout){
		Log.d("UIMSG","NEW");
		this.testFragment = testFragment;
		this.context = testFragment.getActivity();
		this.r = context.getResources();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		screen = FragmentTabs.getSize();
		setting();
	}
	
	private void setting(){
		
		digitTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dinproregular.ttf");
		wordTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dfheistd-w3.otf");
		
		endListener = new EndOnClickListener();
		boxLayout = (RelativeLayout) inflater.inflate(R.layout.message_box_layout,null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		questionLayout = (LinearLayout) boxLayout.findViewById(R.id.msg_question_layout);
		
		textSize = screen.x * 63/1080;
		
		help = (TextView) boxLayout.findViewById(R.id.msg_help);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		help.setTypeface(wordTypeface);
		
		emotionText = (TextView) boxLayout.findViewById(R.id.msg_emotion_text);
		emotionText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		emotionText.setTypeface(wordTypeface);
		
		desireText = (TextView) boxLayout.findViewById(R.id.msg_desire_text);
		desireText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		desireText.setTypeface(wordTypeface);
		
		gpsText = (TextView) boxLayout.findViewById(R.id.msg_gps_text);
		gpsText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		gpsText.setTypeface(wordTypeface);
		
		emotionSeekBar = (SeekBar) boxLayout.findViewById(R.id.msg_emotion_seek_bar);
		desireSeekBar = (SeekBar) boxLayout.findViewById(R.id.msg_desire_seek_bar);
		gpsSwitch = (Switch) boxLayout.findViewById(R.id.msg_gps_switch);
		gpsSwitch.setTextOff("否");
		gpsSwitch.setTextOn("是");
		gpsSwitch.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		gpsSwitch.setTypeface(wordTypeface);
		
		emotionShow = (ImageView) boxLayout.findViewById(R.id.msg_emotion_show);
		desireShow = (ImageView) boxLayout.findViewById(R.id.msg_desire_show);
		
		emotionSeekBar.setOnSeekBarChangeListener(new EmotionListener());
		desireSeekBar.setOnSeekBarChangeListener(new DesireListener());
		
		okButton = (Button) boxLayout.findViewById(R.id.msg_button);
		
		emotionShowText = (TextView) boxLayout.findViewById(R.id.msg_emotion_show_text);
		emotionShowText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		emotionShowText.setTypeface(digitTypeface);
		
		desireShowText = (TextView) boxLayout.findViewById(R.id.msg_desire_show_text);
		desireShowText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		desireShowText.setTypeface(digitTypeface);
		
		emotionLabel = (LinearLayout) boxLayout.findViewById(R.id.msg_emotion_label);
		desireLabel = (LinearLayout) boxLayout.findViewById(R.id.msg_desire_label);
	}
	
	public void settingPreTask(){
		mainLayout.addView(boxLayout);
	}
	
	private int box_width;
	
	public void settingInBackground(){
		
		Point screen = FragmentTabs.getSize();
		
		RelativeLayout.LayoutParams boxParam = (LayoutParams) boxLayout.getLayoutParams();
		boxParam.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
		boxParam.width = box_width = screen.x*900/1080;
		
		int size = screen.x*120/1080;
		
		LinearLayout.LayoutParams eParam = (LinearLayout.LayoutParams) emotionShowText.getLayoutParams();
		eParam.width = size;
		LinearLayout.LayoutParams dParam = (LinearLayout.LayoutParams) desireShowText.getLayoutParams();
		dParam.width = size;
		
		
		emotionBmps = new Bitmap[5];
		Bitmap tmp;
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_emotion_1);
		emotionBmps[0] = Bitmap.createScaledBitmap(tmp, size, size, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_emotion_2);
		emotionBmps[1] = Bitmap.createScaledBitmap(tmp, size, size, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_emotion_3);
		emotionBmps[2] = Bitmap.createScaledBitmap(tmp, size, size, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_emotion_4);
		emotionBmps[3] = Bitmap.createScaledBitmap(tmp, size, size, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_emotion_5);
		emotionBmps[4] = Bitmap.createScaledBitmap(tmp, size, size, true);
		tmp.recycle();
		
		desireBmps = new Bitmap[10];
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_desire_1);
		desireBmps[0] = Bitmap.createScaledBitmap(tmp, size, size, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_desire_2);
		desireBmps[1] = Bitmap.createScaledBitmap(tmp, size, size, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_desire_3);
		desireBmps[2] = Bitmap.createScaledBitmap(tmp, size, size, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_desire_4);
		desireBmps[3] = Bitmap.createScaledBitmap(tmp, size, size, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_desire_5);
		desireBmps[4] = Bitmap.createScaledBitmap(tmp, size, size, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_desire_6);
		desireBmps[5] = Bitmap.createScaledBitmap(tmp, size, size, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_desire_7);
		desireBmps[6] = Bitmap.createScaledBitmap(tmp, size, size, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_desire_8);
		desireBmps[7] = Bitmap.createScaledBitmap(tmp, size, size, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_desire_9);
		desireBmps[8] = Bitmap.createScaledBitmap(tmp, size, size, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_desire_10);
		desireBmps[9] = Bitmap.createScaledBitmap(tmp, size, size, true);
		tmp.recycle();
	}
	
	public  void settingPostTask(){
		emotionSeekBar.setProgress(emotionSeekBar.getMax()/2);
		desireSeekBar.setProgress(desireSeekBar.getMax()/2);
		
		int textSize2 = screen.x * 40/1080;
		
		for (int i=0;i<emotionStr.length;++i){
			TextView t = new TextView(context);
			t.setText(emotionStr[i]);
			t.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize2);
			t.setTypeface(wordTypeface);
			t.setTextColor(0xFF000000);
			t.setGravity(Gravity.CENTER_HORIZONTAL);
			emotionLabel.addView(t);
			LinearLayout.LayoutParams tp = (LinearLayout.LayoutParams)t.getLayoutParams();
			tp.weight = 1;
			tp.width = box_width /5;
		}
		
		for (int i=0;i<desireStr.length;++i){
			TextView t = new TextView(context);
			t.setText(desireStr[i]);
			t.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize2);
			t.setTypeface(wordTypeface);
			t.setTextColor(0xFF000000);
			t.setGravity(Gravity.CENTER_HORIZONTAL);
			desireLabel.addView(t);
			LinearLayout.LayoutParams tp = (LinearLayout.LayoutParams)t.getLayoutParams();
			//tp.weight = desireWeight[i];
			tp.width = box_width *desireWeight[i]/10;
		}
		
	}
	
	public void clear(){
		Log.d("UIMSG","CLEAR");
		mainLayout.removeView(boxLayout);
		if (emotionBmps!=null){
			for (int i=0;i<emotionBmps.length;++i){
				if (emotionBmps[i]!=null && !emotionBmps[i].isRecycled()){
					emotionBmps[i].recycle();
					emotionBmps[i] = null;
				}
			}
			emotionBmps = null;
		}
		if (desireBmps!=null){
			for (int i=0;i<desireBmps.length;++i){
				if (desireBmps[i]!=null && !desireBmps[i].isRecycled()){
					desireBmps[i].recycle();
					desireBmps[i] = null;
				}
			}
			desireBmps = null;
		}
	}
	
	public void generateGPSCheckBox(){
		
		help.setText("");
		questionLayout.setVisibility(View.VISIBLE);
		boxLayout.setVisibility(View.VISIBLE);
		okButton.setOnClickListener(endListener);
		
	}
	
	private class EndOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			boxLayout.setVisibility(View.INVISIBLE);
				boolean enableGPS = gpsSwitch.isChecked();
				int desire = desireSeekBar.getProgress()+1;
				int emotion =  emotionSeekBar.getProgress()+1;
				
				String setting_str = desire+"/"+emotion+"/"+enableGPS; 
				Log.d("MSGBOX SETTING",setting_str);
				testFragment.writeQuestionFile(emotion, desire);
				
				testFragment.startGPS(enableGPS);
		}
		
	}
	
	public void generateInitializingBox(){
		okButton.setOnClickListener(null);
		help.setText("請稍待");
		questionLayout.setVisibility(View.INVISIBLE);
		boxLayout.setVisibility(View.VISIBLE);
	}
	
	public void closeInitializingBox(){
			boxLayout.setVisibility(View.INVISIBLE);
			return;
	}
	
	private class EmotionListener implements SeekBar.OnSeekBarChangeListener{
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, 	boolean fromUser) {
			emotionShow.setImageBitmap(emotionBmps[progress]);
			String value = String.valueOf(progress+1);
			emotionShowText.setText(value);
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
	}
	
	private class DesireListener implements SeekBar.OnSeekBarChangeListener{
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, 	boolean fromUser) {
			desireShow.setImageBitmap(desireBmps[progress]);
			String value = String.valueOf(progress+1);
			desireShowText.setText(value);
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
	}
	
}
