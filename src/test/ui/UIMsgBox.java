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
import android.graphics.drawable.BitmapDrawable;
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

public class UIMsgBox {

	private TestFragment testFragment;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	
	private TextView help,emotionText,desireText,gpsText;
	private SeekBar emotionSeekBar,desireSeekBar;
	private Switch gpsSwitch;
	
	private RelativeLayout mainLayout;
	
	private ImageView emotionShow;
	private ImageView desireShow;
	private TextView emotionShowText;
	private TextView desireShowText;
	
	private TextView eNum,dNum;
	
	private static final String[] emotionStr = {"沮喪","低落","普通", "愉快","快樂"};
	private static final String[] desireStr = {"無\n　",
																				"輕度\n尚未行動","輕度\n尚未行動","輕度\n尚未行動",
																				"中~強度\n等下去買","中~強度\n等下去買","中~強度\n等下去買",
																				"非常強烈\n即將要喝","非常強烈\n即將要喝","非常強烈\n即將要喝"};
	
	private LinearLayout questionLayout;
	
	private Resources r;
	private Point screen;
	
	private EndOnClickListener endListener;
	private CancelOnClickListener cancelListener;
	
	private Typeface digitTypeface;
	private Typeface wordTypeface;
	private Typeface wordTypefaceBold;
	
	private int textSize;
	
	private Bitmap[] emotionBmps;
	private Bitmap[] desireBmps;
	
	private Bitmap seekBarBmp, seekBarThumbBmp, seekBarProgressBmp;
	private Bitmap switchBmp, switchThumbBmp;
	private Bitmap bgBmp;
	
	private TextView title;
	private LinearLayout emotionLayout,desireLayout;
	
	private TextView send,notSend;
	
	private RelativeLayout.LayoutParams eP, dP;
	
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
		wordTypefaceBold = Typeface.createFromAsset(context.getAssets(), "fonts/dfheistd-w5.otf");
		
		endListener = new EndOnClickListener();
		cancelListener = new CancelOnClickListener();
		boxLayout = (RelativeLayout) inflater.inflate(R.layout.message_box_layout,null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		questionLayout = (LinearLayout) boxLayout.findViewById(R.id.msg_question_layout);
		
		textSize = screen.x * 63/1080;
		int gap = screen.x * 60/1080;
		int textSizeLarge = screen.x * 108/1080;
		title = (TextView)boxLayout.findViewById(R.id.msg_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSizeLarge);
		title.setTypeface(wordTypefaceBold);
		LinearLayout.LayoutParams tParam = (LinearLayout.LayoutParams)title.getLayoutParams();
		tParam.topMargin = tParam.bottomMargin =  gap;
		
		
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
		
		int textSize2 = screen.x * 40/1080;
		
		emotionShowText = (TextView) boxLayout.findViewById(R.id.msg_emotion_show_text);
		emotionShowText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize2);
		emotionShowText.setTypeface(digitTypeface);
		
		desireShowText = (TextView) boxLayout.findViewById(R.id.msg_desire_show_text);
		desireShowText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize2);
		desireShowText.setTypeface(digitTypeface);
		
		eNum = (TextView) boxLayout.findViewById(R.id.msg_emotion_num);
		eNum.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize2);
		eNum.setTypeface(digitTypeface);
		dNum = (TextView) boxLayout.findViewById(R.id.msg_desire_num);
		dNum.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize2);
		dNum.setTypeface(digitTypeface);
		
		eP = (LayoutParams) eNum.getLayoutParams();
		dP = (LayoutParams) dNum.getLayoutParams();
		
		emotionLayout = (LinearLayout) boxLayout.findViewById(R.id.msg_emotion_layout);
		LinearLayout.LayoutParams eParam =  (LinearLayout.LayoutParams)emotionLayout.getLayoutParams();
		eParam.bottomMargin = gap;
		desireLayout = (LinearLayout) boxLayout.findViewById(R.id.msg_desire_layout);
		LinearLayout.LayoutParams dParam =  (LinearLayout.LayoutParams)desireLayout.getLayoutParams();
		dParam.bottomMargin = gap;
		
		LinearLayout.LayoutParams sParam =  (LinearLayout.LayoutParams)gpsSwitch.getLayoutParams();
		sParam.bottomMargin = gap;
		
		send = (TextView) boxLayout.findViewById(R.id.msg_send);
		send.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSizeLarge);
		send.setTypeface(wordTypefaceBold);
		notSend = (TextView) boxLayout.findViewById(R.id.msg_not_send);
		notSend.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSizeLarge);
		notSend.setTypeface(wordTypefaceBold);
	}
	
	public void settingPreTask(){
		mainLayout.addView(boxLayout);
	}
	
	private int box_width;
	
	public void settingInBackground(){
		
		Point screen = FragmentTabs.getSize();
		
		RelativeLayout.LayoutParams boxParam = (LayoutParams) boxLayout.getLayoutParams();
		boxParam.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
		boxParam.width = box_width = screen.x * 987/1080;
		boxParam.height =  screen.x * 1400/1080;
		boxParam.topMargin = screen.x * 200/1080;
		
		int size = screen.x*120/1080;
		
		
		RelativeLayout.LayoutParams qParam = (RelativeLayout.LayoutParams) questionLayout.getLayoutParams();
		qParam.width = screen.x * 787/1080;
		
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
		
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_bar);
		seekBarBmp = Bitmap.createScaledBitmap(tmp, screen.x * 584 / 1080,  screen.x * 35/1080, true);
		tmp.recycle();
		
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_bar_button);
		seekBarThumbBmp = Bitmap.createScaledBitmap(tmp, screen.x * 79 / 1080,  screen.x * 78/1080, true);
		tmp.recycle();
		
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_bar_value);
		seekBarProgressBmp = Bitmap.createScaledBitmap(tmp, screen.x * 485 / 1080,  screen.x * 19/1080, true);
		tmp.recycle();
		
		RelativeLayout.LayoutParams emoParam = (RelativeLayout.LayoutParams)emotionSeekBar.getLayoutParams();
		emoParam.width = screen.x * 584 / 1080;
		emoParam.height = screen.x * 79 / 1080;
		
		RelativeLayout.LayoutParams desParam = (RelativeLayout.LayoutParams)desireSeekBar.getLayoutParams();
		desParam.width = screen.x * 584 / 1080;
		desParam.height = screen.x * 79 / 1080;
		
		LinearLayout.LayoutParams eParam = (LinearLayout.LayoutParams) emotionShowText.getLayoutParams();
		eParam.width = screen.x * 203/1080;
		LinearLayout.LayoutParams dParam = (LinearLayout.LayoutParams) desireShowText.getLayoutParams();
		dParam.width = screen.x * 203/1080;
		
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_bg);
		bgBmp = Bitmap.createScaledBitmap(tmp, box_width ,  boxParam.height, true);
		tmp.recycle();
		
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_switch);
		switchBmp = Bitmap.createScaledBitmap(tmp, screen.x * 166/1080, screen.x * 75/1080, true);
		tmp.recycle();
		
		tmp = BitmapFactory.decodeResource(r, R.drawable.msg_switch_button);
		switchThumbBmp = Bitmap.createScaledBitmap(tmp, screen.x * 117/1080, screen.x * 75/1080, true);
		tmp.recycle();
		thumb_size = screen.x * 79 / 1080;
		
		LinearLayout.LayoutParams gParam = (LinearLayout.LayoutParams) gpsSwitch.getLayoutParams();
		//gParam.width = screen.x * 300/1080;
		gParam.height = screen.x * 200/1080;
		
	}
	
	int thumb_size;
	
	public  void settingPostTask(){
		
		boxLayout.setBackground(new BitmapDrawable(r,bgBmp));
		
		emotionSeekBar.setProgress(1);
		desireSeekBar.setProgress(1);
		emotionSeekBar.setProgress(0);
		desireSeekBar.setProgress(0);
		
		emotionSeekBar.setThumb(new BitmapDrawable(r,seekBarThumbBmp));
		desireSeekBar.setThumb(new BitmapDrawable(r,seekBarThumbBmp));
		
		gpsSwitch.setTrackDrawable(new BitmapDrawable(r,switchBmp));
		gpsSwitch.setThumbDrawable(new BitmapDrawable(r,switchThumbBmp));
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
		
		if (seekBarBmp !=null && !seekBarBmp.isRecycled()){
			seekBarBmp.recycle();
			seekBarBmp = null;
		}
		if (seekBarThumbBmp !=null && !seekBarThumbBmp.isRecycled()){
			seekBarThumbBmp.recycle();
			seekBarThumbBmp = null;
		}
		if (seekBarProgressBmp !=null && !seekBarProgressBmp.isRecycled()){
			seekBarProgressBmp.recycle();
			seekBarProgressBmp = null;
		}
		if (bgBmp != null && !bgBmp.isRecycled()){
			bgBmp.recycle();
			bgBmp = null;
		}
		if (switchThumbBmp != null && !switchThumbBmp.isRecycled()){
			switchThumbBmp.recycle();
			switchThumbBmp = null;
		}
		if (switchBmp != null && !switchBmp.isRecycled()){
			switchBmp.recycle();
			switchBmp = null;
		}
	}
	
	public void generateGPSCheckBox(){
		
		help.setText("");
		questionLayout.setVisibility(View.VISIBLE);
		boxLayout.setVisibility(View.VISIBLE);
		send.setOnClickListener(endListener);
		notSend.setOnClickListener(cancelListener);
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
	
	private class CancelOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			boxLayout.setVisibility(View.INVISIBLE);
			boolean enableGPS = false;
			int desire = -1;
			int emotion =  -1;
				
			testFragment.writeQuestionFile(emotion, desire);
			testFragment.startGPS(enableGPS);
		}
	}
	
	public void generateInitializingBox(){
		send.setOnClickListener(null);
		notSend.setOnClickListener(null);
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
			emotionShowText.setText(emotionStr[progress]);
			eNum.setText(String.valueOf(progress+1));
			eP.leftMargin = emotionSeekBar.getWidth() *  progress/5;
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
			desireShowText.setText(desireStr[progress]);
			dNum.setText(String.valueOf(progress+1));
			dP.leftMargin = desireSeekBar.getWidth() * progress/10;
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
	}
	
}
