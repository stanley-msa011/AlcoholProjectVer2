package test.ui;

import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.activities.TestFragment;
import ubicomp.drunk_detection.activities.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Switch;
import android.widget.TextView;

public class UIMsgBox {

	private TestFragment testFragment;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	
	private TextView help,emotionText,desireText,gpsText;
	private ImageView emotionSeekBg,desireSeekBg;
	private SeekBar emotionSeekBar,desireSeekBar;
	private Switch gpsSwitch;
	
	private RelativeLayout mainLayout;
	
	private RelativeLayout emotionSeekBarLayout,desireSeekBarLayout;
	
	private ImageView emotionShow;
	private ImageView desireShow;
	//private ImageView eStart,eEnd,dStart,dEnd;
	private TextView emotionShowText;
	private TextView desireShowText;
	private TextView gpsNo,gpsYes;
	
	private TextView eNum,dNum;
	
	private static final String[] emotionStr = {"　沮喪　","　低落　","　普通　", "　愉快　","　快樂　"};
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
	
	private int textSize, textSizeLarge;
	
	private Drawable[] emotionDrawables;
	private Drawable[] desireDrawables;
	
	private TextView title;
	private RelativeLayout emotionLayout;
	private RelativeLayout  desireLayout;
	
	private TextView send,notSend;
	
	private RelativeLayout.LayoutParams eP, dP;
	
	private boolean done;
	
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
		wordTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/DFLiHeiStd-W3.otf");
		wordTypefaceBold = Typeface.createFromAsset(context.getAssets(), "fonts/DFLiHeiStd-W5.otf");
		
		endListener = new EndOnClickListener();
		cancelListener = new CancelOnClickListener();
		boxLayout = (RelativeLayout) inflater.inflate(R.layout.message_box_layout,null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		questionLayout = (LinearLayout) boxLayout.findViewById(R.id.msg_question_layout);
		
		textSize = screen.x * 21/480;
		textSizeLarge = screen.x * 32/480;
		title = (TextView)boxLayout.findViewById(R.id.msg_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSizeLarge);
		title.setTypeface(wordTypefaceBold);
		LinearLayout.LayoutParams tParam = (LinearLayout.LayoutParams)title.getLayoutParams();
		tParam.topMargin = screen.x*53/480;
		tParam.bottomMargin = screen.x*60/480;
		
		help = (TextView) boxLayout.findViewById(R.id.msg_help);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		help.setTypeface(wordTypefaceBold);
		
		emotionText = (TextView) boxLayout.findViewById(R.id.msg_emotion_text);
		emotionText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		emotionText.setTypeface(wordTypefaceBold);
		
		desireText = (TextView) boxLayout.findViewById(R.id.msg_desire_text);
		desireText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		desireText.setTypeface(wordTypefaceBold);
		
		gpsText = (TextView) boxLayout.findViewById(R.id.msg_gps_text);
		gpsText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		gpsText.setTypeface(wordTypefaceBold);
		
		gpsNo = (TextView) boxLayout.findViewById(R.id.msg_gps_no);
		gpsNo.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		gpsNo.setTypeface(wordTypefaceBold);
		
		gpsYes = (TextView) boxLayout.findViewById(R.id.msg_gps_yes);
		gpsYes.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		gpsYes.setTypeface(wordTypefaceBold);
		
		emotionSeekBar = (SeekBar) boxLayout.findViewById(R.id.msg_emotion_seek_bar);
		desireSeekBar = (SeekBar) boxLayout.findViewById(R.id.msg_desire_seek_bar);
		emotionSeekBg = (ImageView) boxLayout.findViewById(R.id.msg_emotion_bar_bg);
		desireSeekBg = (ImageView) boxLayout.findViewById(R.id.msg_desire_bar_bg);
		
		gpsSwitch = (Switch) boxLayout.findViewById(R.id.msg_gps_switch);
		gpsSwitch.setHeight(screen.x * 40/480);
		gpsSwitch.setTextSize(TypedValue.COMPLEX_UNIT_PX,screen.x * 40/480 );
		
		emotionShow = (ImageView) boxLayout.findViewById(R.id.msg_emotion_show);
		desireShow = (ImageView) boxLayout.findViewById(R.id.msg_desire_show);
		
		emotionSeekBar.setOnSeekBarChangeListener(new EmotionListener());
		desireSeekBar.setOnSeekBarChangeListener(new DesireListener());

		emotionShowText = (TextView) boxLayout.findViewById(R.id.msg_emotion_show_text);
		emotionShowText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		emotionShowText.setTypeface(wordTypeface);
		
		desireShowText = (TextView) boxLayout.findViewById(R.id.msg_desire_show_text);
		desireShowText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		desireShowText.setTypeface(wordTypeface);
		
		eNum = (TextView) boxLayout.findViewById(R.id.msg_emotion_num);
		eNum.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		eNum.setTypeface(digitTypeface);
		dNum = (TextView) boxLayout.findViewById(R.id.msg_desire_num);
		dNum.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		dNum.setTypeface(digitTypeface);
		
		eP = (LayoutParams) eNum.getLayoutParams();
		dP = (LayoutParams) dNum.getLayoutParams();
		
		eP.width =dP.width =  screen.x * 26/480;
		
		emotionSeekBarLayout = (RelativeLayout) boxLayout.findViewById(R.id.msg_emotion_seek_bar_layout);
		RelativeLayout.LayoutParams eSBParam = (LayoutParams) emotionSeekBarLayout.getLayoutParams();
		eSBParam.width = screen.x * 260/480;
		desireSeekBarLayout = (RelativeLayout) boxLayout.findViewById(R.id.msg_desire_seek_bar_layout);
		RelativeLayout.LayoutParams dSBParam = (LayoutParams) desireSeekBarLayout.getLayoutParams();
		dSBParam.width = screen.x * 260/480;
		
		emotionLayout = (RelativeLayout) boxLayout.findViewById(R.id.msg_emotion_layout);
		LinearLayout.LayoutParams eParam =  (LinearLayout.LayoutParams)emotionLayout.getLayoutParams();
		eParam.bottomMargin = screen.x * 50/480;
		desireLayout = (RelativeLayout) boxLayout.findViewById(R.id.msg_desire_layout);
		LinearLayout.LayoutParams dParam =  (LinearLayout.LayoutParams)desireLayout.getLayoutParams();
		dParam.bottomMargin = screen.x * 50/480;
		
		LinearLayout.LayoutParams sParam =  (LinearLayout.LayoutParams)gpsSwitch.getLayoutParams();
		sParam.bottomMargin = screen.x * 16/480;
		
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
	
	public void settingInBackground(){
		
		RelativeLayout.LayoutParams boxParam = (LayoutParams) boxLayout.getLayoutParams();
		boxParam.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
		boxParam.topMargin = screen.x * 80/480;
		boxParam.width = screen.x * 435/480;
		boxParam.height = screen.x * 637/480;
		
		RelativeLayout.LayoutParams qParam = (RelativeLayout.LayoutParams) questionLayout.getLayoutParams();
		qParam.leftMargin = qParam.rightMargin = screen.x * 44/480;
		qParam.width = screen.x * 392/480;
		
		emotionDrawables = new Drawable[5];
		emotionDrawables[0]  = r.getDrawable(R.drawable.msg_emotion_1);
		emotionDrawables[1]  = r.getDrawable(R.drawable.msg_emotion_2);
		emotionDrawables[2]  = r.getDrawable(R.drawable.msg_emotion_3);
		emotionDrawables[3]  = r.getDrawable(R.drawable.msg_emotion_4);
		emotionDrawables[4]  = r.getDrawable(R.drawable.msg_emotion_5);
		
		desireDrawables = new Drawable[10];
		desireDrawables[0]  = r.getDrawable(R.drawable.msg_desire_1);
		desireDrawables[1]  = r.getDrawable(R.drawable.msg_desire_2);
		desireDrawables[2]  = r.getDrawable(R.drawable.msg_desire_3);
		desireDrawables[3]  = r.getDrawable(R.drawable.msg_desire_4);
		desireDrawables[4]  = r.getDrawable(R.drawable.msg_desire_5);
		desireDrawables[5]  = r.getDrawable(R.drawable.msg_desire_6);
		desireDrawables[6]  = r.getDrawable(R.drawable.msg_desire_7);
		desireDrawables[7]  = r.getDrawable(R.drawable.msg_desire_8);
		desireDrawables[8]  = r.getDrawable(R.drawable.msg_desire_9);
		desireDrawables[9]  = r.getDrawable(R.drawable.msg_desire_10);
		
		
		int padding_ver = screen.x * 5/480;
		int padding_hor = screen.x * 24/480;
		
		emotionSeekBar.setPadding(padding_hor , padding_ver,padding_hor , padding_ver);
		desireSeekBar.setPadding(padding_hor , padding_ver, padding_hor , padding_ver);

		emotionSeekBar.bringToFront();
		desireSeekBar.bringToFront();
		
		RelativeLayout.LayoutParams esParam = (LayoutParams) emotionSeekBar.getLayoutParams();
		esParam.width = screen.x * 260/480;
		esParam.topMargin = screen.x * 13/480;
		RelativeLayout.LayoutParams dsParam = (LayoutParams) desireSeekBar.getLayoutParams();
		dsParam.width = screen.x * 260/480;
		dsParam.topMargin = screen.x * 13/480;
		RelativeLayout.LayoutParams esbgParam = (LayoutParams) emotionSeekBg.getLayoutParams();
		esbgParam.topMargin = screen.x * 44/960;
		RelativeLayout.LayoutParams dsbgParam = (LayoutParams) desireSeekBg.getLayoutParams();
		dsbgParam.topMargin = screen.x * 44/960;
		
		LinearLayout.LayoutParams eParam = (LinearLayout.LayoutParams) emotionShowText.getLayoutParams();
		eParam.width = screen.x * 90/480;
		eParam.topMargin = screen.x *8/480;
		LinearLayout.LayoutParams dParam = (LinearLayout.LayoutParams) desireShowText.getLayoutParams();
		dParam.width = screen.x * 90/480;
		dParam.topMargin = screen.x *8/480;
		
		LinearLayout.LayoutParams gParam = (LinearLayout.LayoutParams) gpsSwitch.getLayoutParams();
		gParam.height = screen.x * 80/480;
		gParam = (LinearLayout.LayoutParams) gpsNo.getLayoutParams();
		gParam.height = screen.x * 80/480;
		gParam = (LinearLayout.LayoutParams) gpsYes.getLayoutParams();
		gParam.height = screen.x * 80/480;
	}
	
	public  void settingPostTask(){
		
		emotionSeekBar.setProgress(1);
		desireSeekBar.setProgress(1);
		emotionSeekBar.setProgress(0);
		desireSeekBar.setProgress(0);
		
		gpsSwitch.setOnClickListener(
				new View.OnClickListener(){
					@Override
					public void onClick(View v) {enableSend(true);}});
	}
	
	public void clear(){
		Log.d("UIMSG","CLEAR");
		mainLayout.removeView(boxLayout);
	}
	
	private void enableSend(boolean enable){
		if (enable){
			send.setTextColor(0xFFf39800);
			notSend.setTextColor(0xFFf39800);
		}
		else{
			send.setTextColor(0xFF898989);
			notSend.setTextColor(0xFF898989);
		}
		done = enable;
	}
	
	public void generateGPSCheckBox(){
		enableSend(false);
		help.setText("");
		questionLayout.setVisibility(View.VISIBLE);
		boxLayout.setVisibility(View.VISIBLE);
		send.setOnClickListener(endListener);
		notSend.setOnClickListener(cancelListener);
	}
	
	private class EndOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			if (!done){
				Toast.makeText(mainLayout.getContext(), "確定已完成？", Toast.LENGTH_LONG).show();
				enableSend(true);
				return;
			}
			
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
			if (!done){
				Toast.makeText(mainLayout.getContext(), "確定不填寫？", Toast.LENGTH_LONG).show();
				enableSend(true);
				return;
			}
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
			emotionShow.setImageDrawable(emotionDrawables[progress]);
			emotionShowText.setText(emotionStr[progress]);
			eNum.setText(String.valueOf(progress+1));
			Log.d("SEEK_BAR","emotion progress="+progress);
			int margin = screen.x*52/480;
			eP.leftMargin =progress*margin;
			enableSend(true);
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
	}
	
	private class DesireListener implements SeekBar.OnSeekBarChangeListener{
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, 	boolean fromUser) {
			desireShow.setImageDrawable(desireDrawables[progress]);
			desireShowText.setText(desireStr[progress]);
			dNum.setText(String.valueOf(progress+1));
			Log.d("SEEK_BAR","desire progress="+progress);
			int margin = screen.x*26/480;
			dP.leftMargin =progress*margin;
			enableSend(true);
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
	}
	
}
