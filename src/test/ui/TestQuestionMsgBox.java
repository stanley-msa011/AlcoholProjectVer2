package test.ui;

import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLoggerLog;
import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.fragments.TestFragment;
import ubicomp.drunk_detection.ui.CustomToastSmall;
import ubicomp.drunk_detection.ui.Typefaces;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Switch;
import android.widget.TextView;

public class TestQuestionMsgBox {

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
	private TextView emotionShowText;
	private TextView desireShowText;
	private TextView gpsNo,gpsYes;
	
	private TextView[] eNum,dNum;
	private RelativeLayout.LayoutParams eP, dP;
	
	private static String[] emotionStr ;
	private static String[] desireStr ;
	
	private LinearLayout questionLayout;
	
	private Resources r;
	private Point screen;
	private boolean isWideScreen;
	
	private EndOnClickListener endListener;
	private CancelOnClickListener cancelListener;
	
	private Typeface digitTypeface;
	private Typeface wordTypeface;
	private Typeface wordTypefaceBold;
	
	private int textSize, textSizeLarge, textSizeXLarge;
	
	private Drawable[] emotionDrawables;
	private Drawable[] desireDrawables;
	
	private TextView title;
	private RelativeLayout emotionLayout;
	private RelativeLayout  desireLayout;
	
	private View divider;
	private TextView send,notSend;
	
	private EndOnTouchListener endOnTouchListener;
	
	private boolean done,doneByDoubleClick;
	
	public TestQuestionMsgBox(TestFragment testFragment,RelativeLayout mainLayout){
		this.testFragment = testFragment;
		this.context = testFragment.getActivity();
		this.r = context.getResources();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		emotionStr = context.getResources().getStringArray(R.array.emotion_state);
		desireStr = context.getResources().getStringArray(R.array.craving_state);
		screen = FragmentTabs.getSize();
		isWideScreen = FragmentTabs.isWideScreen();
		digitTypeface = Typefaces.getDigitTypeface(context);
		wordTypeface = Typefaces.getWordTypeface(context);
		wordTypefaceBold = Typefaces.getWordTypefaceBold(context);
		setting();
	}
	
	private void setting(){
		
		endListener = new EndOnClickListener();
		cancelListener = new CancelOnClickListener();
		boxLayout = (RelativeLayout) inflater.inflate(R.layout.message_box_layout,null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		questionLayout = (LinearLayout) boxLayout.findViewById(R.id.msg_question_layout);
		
		textSize = screen.x * 21/480;
		textSizeLarge = screen.x * 32/480;
		textSizeXLarge = screen.x * 48/480;
		title = (TextView)boxLayout.findViewById(R.id.msg_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSizeLarge);
		title.setTypeface(wordTypefaceBold);
		LinearLayout.LayoutParams tParam = (LinearLayout.LayoutParams)title.getLayoutParams();
		tParam.topMargin = screen.x*53/480;
		if(isWideScreen)
			tParam.bottomMargin = screen.x*60/480;
		else
			tParam.bottomMargin = screen.x*30/480;
		
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
		gpsSwitch.setHeight(screen.x * 80/480);
		gpsSwitch.setSwitchMinWidth(screen.x*52/480);
		gpsSwitch.setTextSize(TypedValue.COMPLEX_UNIT_PX,screen.x * 40/480 );
		
		emotionShow = (ImageView) boxLayout.findViewById(R.id.msg_emotion_show);
		desireShow = (ImageView) boxLayout.findViewById(R.id.msg_desire_show);
		
		emotionSeekBar.setOnSeekBarChangeListener(new EmotionListener());
		emotionSeekBar.setOnTouchListener(new EmotionOnTouchListener());
		desireSeekBar.setOnSeekBarChangeListener(new DesireListener());
		desireSeekBar.setOnTouchListener(new DesireOnTouchListener());
		
		emotionShowText = (TextView) boxLayout.findViewById(R.id.msg_emotion_show_text);
		emotionShowText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		emotionShowText.setTypeface(wordTypeface);
		
		desireShowText = (TextView) boxLayout.findViewById(R.id.msg_desire_show_text);
		desireShowText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		desireShowText.setTypeface(wordTypeface);
		
		emotionSeekBarLayout = (RelativeLayout) boxLayout.findViewById(R.id.msg_emotion_seek_bar_layout);
		RelativeLayout.LayoutParams eSBParam = (LayoutParams) emotionSeekBarLayout.getLayoutParams();
		eSBParam.width = screen.x * 260/480;
		RelativeLayout.LayoutParams eSBgParam = (LayoutParams) emotionSeekBg.getLayoutParams();
		eSBgParam.width = screen.x * 260/480;
		desireSeekBarLayout = (RelativeLayout) boxLayout.findViewById(R.id.msg_desire_seek_bar_layout);
		RelativeLayout.LayoutParams dSBParam = (LayoutParams) desireSeekBarLayout.getLayoutParams();
		dSBParam.width = screen.x * 260/480;
		RelativeLayout.LayoutParams dSBgParam = (LayoutParams) desireSeekBg.getLayoutParams();
		dSBgParam.width = screen.x * 260/480;
		
		int num_size = screen.x * 26/480;
		int num_size2 = screen.x * 90/480;
		int num_size3 = screen.x * 260/480 - (num_size + num_size2)*2;
		
		eNum = new TextView[5];
		eNum[0] = (TextView) boxLayout.findViewById(R.id.msg_emotion_num0);
		eNum[1] = (TextView) boxLayout.findViewById(R.id.msg_emotion_num1);
		eNum[2] = (TextView) boxLayout.findViewById(R.id.msg_emotion_num2);
		eNum[3] = (TextView) boxLayout.findViewById(R.id.msg_emotion_num3);
		eNum[4] = (TextView) boxLayout.findViewById(R.id.msg_emotion_num4);

		for (int i=0;i<5;++i){
			eNum[i].setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
			eNum[i].setTypeface(digitTypeface);
		}
		
		int topMarginOfText = screen.x * 5 /480;
		eP = (LayoutParams) eNum[0].getLayoutParams();
		eP.width =  num_size;
		eP.topMargin =topMarginOfText;
		eP = (LayoutParams) eNum[1].getLayoutParams();
		eP.width =  num_size2;
		eP.topMargin =topMarginOfText;
		eP = (LayoutParams) eNum[2].getLayoutParams();
		eP.width =  num_size3;
		eP.topMargin =topMarginOfText;
		eP = (LayoutParams) eNum[3].getLayoutParams();
		eP.width =  num_size2;
		eP.topMargin =topMarginOfText;
		eP = (LayoutParams) eNum[4].getLayoutParams();
		eP.width =  num_size;
		eP.topMargin =topMarginOfText;
		
		dNum = new TextView[10];
		dNum[0] = (TextView) boxLayout.findViewById(R.id.msg_desire_num0);
		dNum[1] = (TextView) boxLayout.findViewById(R.id.msg_desire_num1);
		dNum[2] = (TextView) boxLayout.findViewById(R.id.msg_desire_num2);
		dNum[3] = (TextView) boxLayout.findViewById(R.id.msg_desire_num3);
		dNum[4] = (TextView) boxLayout.findViewById(R.id.msg_desire_num4);
		dNum[5] = (TextView) boxLayout.findViewById(R.id.msg_desire_num5);
		dNum[6] = (TextView) boxLayout.findViewById(R.id.msg_desire_num6);
		dNum[7] = (TextView) boxLayout.findViewById(R.id.msg_desire_num7);
		dNum[8] = (TextView) boxLayout.findViewById(R.id.msg_desire_num8);
		dNum[9] = (TextView) boxLayout.findViewById(R.id.msg_desire_num9);
		for (int i=0;i<10;++i){
			dNum[i].setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
			dNum[i].setTypeface(digitTypeface);
			dP = (LayoutParams) dNum[i].getLayoutParams();
			dP.width =  num_size;
			dP.topMargin = topMarginOfText;
		}
		
		emotionLayout = (RelativeLayout) boxLayout.findViewById(R.id.msg_emotion_layout);
		LinearLayout.LayoutParams eParam =  (LinearLayout.LayoutParams)emotionLayout.getLayoutParams();
		if (isWideScreen)
			eParam.bottomMargin = screen.x * 50/480;
		else
			eParam.bottomMargin = screen.x * 25/480;
		desireLayout = (RelativeLayout) boxLayout.findViewById(R.id.msg_desire_layout);
		LinearLayout.LayoutParams dParam =  (LinearLayout.LayoutParams)desireLayout.getLayoutParams();
		if (isWideScreen)
			dParam.bottomMargin = screen.x * 50/480;
		else
			dParam.bottomMargin = screen.x * 25/480;
		
		LinearLayout.LayoutParams sParam =  (LinearLayout.LayoutParams)gpsSwitch.getLayoutParams();
		sParam.bottomMargin = screen.x * 16/480;
		
		divider = boxLayout.findViewById(R.id.msg_divider);
		
		endOnTouchListener = new EndOnTouchListener();
		send = (TextView) boxLayout.findViewById(R.id.msg_send);
		send.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSizeLarge);
		send.setOnTouchListener(endOnTouchListener);
		send.setTypeface(wordTypefaceBold);
		notSend = (TextView) boxLayout.findViewById(R.id.msg_not_send);
		notSend.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSizeLarge);
		notSend.setOnTouchListener(endOnTouchListener);
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
		if (isWideScreen)
			boxParam.height = screen.x * 637/480;
		else
			boxParam.height = screen.x * 578/480;
		
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

		RelativeLayout.LayoutParams esParam = (LayoutParams) emotionSeekBar.getLayoutParams();
		esParam.width = screen.x * 260/480;
		RelativeLayout.LayoutParams dsParam = (LayoutParams) desireSeekBar.getLayoutParams();
		dsParam.width = screen.x * 260/480;
		
		LinearLayout.LayoutParams eParam = (LinearLayout.LayoutParams) emotionShowText.getLayoutParams();
		eParam.width = screen.x * 90/480;
		eParam.topMargin = screen.x *8/480;
		LinearLayout.LayoutParams dParam = (LinearLayout.LayoutParams) desireShowText.getLayoutParams();
		dParam.width = screen.x * 90/480;
		dParam.topMargin = screen.x *8/480;
		
		LinearLayout.LayoutParams gParam = (LinearLayout.LayoutParams) gpsSwitch.getLayoutParams();
		gParam.height = screen.x * 80/480;
		gParam = (LinearLayout.LayoutParams) gpsNo.getLayoutParams();
		gParam.height = screen.x * 72/480;
		gParam = (LinearLayout.LayoutParams) gpsYes.getLayoutParams();
		gParam.height = screen.x * 72/480;
		
		RelativeLayout.LayoutParams divParam = (LayoutParams) divider.getLayoutParams();
		divParam.width = screen.x * 25/480;
		divParam.height = screen.x * 48/480;
		
		RelativeLayout.LayoutParams sendParam = (LayoutParams) send.getLayoutParams();
		sendParam.width = screen.x * 205/480;
		sendParam.height = screen.x * 48/480;
		RelativeLayout.LayoutParams nsendParam = (LayoutParams) notSend.getLayoutParams();
		nsendParam.width = screen.x * 205/480;
		nsendParam.height = screen.x * 48/480;
		
	}
	
	public  void settingPostTask(){
		emotionSeekBar.setProgress(0);
		desireSeekBar.setProgress(0);
		gpsSwitch.setOnClickListener(
				new View.OnClickListener(){
					@Override
					public void onClick(View v) {enableSend(true);}});
	}
	
	public void clear(){
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
		doneByDoubleClick = false;
	}
	
	private void enableSend(boolean enable,boolean click){
		if (enable){
			send.setTextColor(0xFFf39800);
			notSend.setTextColor(0xFFf39800);
		}
		else{
			send.setTextColor(0xFF898989);
			notSend.setTextColor(0xFF898989);
		}
		done = enable;
		doneByDoubleClick = click;
	}
	
	public void generateGPSCheckBox(){
		enableSend(false);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("LatestTestTime", System.currentTimeMillis());
		edit.commit();
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
				CustomToastSmall.generateToast(mainLayout.getContext(),R.string.msg_box_toast_send);
				enableSend(true,true);
				return;
			}
			if (doneByDoubleClick)
				ClickLoggerLog.Log(context, ClickLogId.TEST_QUESTION_SEND);
			else
				ClickLoggerLog.Log(context, ClickLogId.TEST_QUESTION_SEND_DATA);
			
			boxLayout.setVisibility(View.INVISIBLE);
			boolean enableGPS = gpsSwitch.isChecked();
			int desire = desireSeekBar.getProgress()+1;
			int emotion =  emotionSeekBar.getProgress()+1;
				
				testFragment.writeQuestionFile(emotion, desire);
				testFragment.startGPS(enableGPS);
		}
	}
	
	private class CancelOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			if (!done){
				CustomToastSmall.generateToast(mainLayout.getContext(),R.string.msg_box_toast_cancel);
				enableSend(true);
				return;
			}
			boxLayout.setVisibility(View.INVISIBLE);
			boolean enableGPS = false;
			int desire = -1;
			int emotion =  -1;
			
			ClickLoggerLog.Log(context, ClickLogId.TEST_QUESTION_CANCEL);
			
			testFragment.writeQuestionFile(emotion, desire);
			testFragment.startGPS(enableGPS);
		}
	}
	
	public void generateWaitingBox(){
		send.setOnClickListener(null);
		notSend.setOnClickListener(null);
		help.setText(R.string.wait);
		questionLayout.setVisibility(View.INVISIBLE);
		boxLayout.setVisibility(View.VISIBLE);
	}
	
	public void closeInitializingBox(){
			boxLayout.setVisibility(View.INVISIBLE);
			return;
	}
	
	private class EmotionOnTouchListener implements View.OnTouchListener{
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int progress = emotionSeekBar.getProgress();
			int margin = screen.x * 52/480;
			eP.leftMargin = progress* margin;
			return false;
		}
	}
	
	private class DesireOnTouchListener implements View.OnTouchListener{
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int progress = desireSeekBar.getProgress();
			int margin = screen.x * 26/480;
			dP.leftMargin = progress* margin;
			return false;
		}
	}
	
	private class EmotionListener implements SeekBar.OnSeekBarChangeListener{
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, 	boolean fromUser) {
			emotionShow.setImageDrawable(emotionDrawables[progress]);
			emotionShowText.setText(emotionStr[progress]);
			for (int i=0;i<eNum.length;++i)
				eNum[i].setVisibility(View.INVISIBLE);
			eNum[progress].setVisibility(View.VISIBLE);
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
			for (int i=0;i<dNum.length;++i)
				dNum[i].setVisibility(View.INVISIBLE);
			dNum[progress].setVisibility(View.VISIBLE);
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
	}
	
	
	private class EndOnTouchListener implements View.OnTouchListener{

		private Rect rect;
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int e = event.getAction();
			TextView tv =(TextView)v;
			switch(e){
				case MotionEvent.ACTION_MOVE:
					if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())){
						tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeLarge);
			        }
					break;
				case MotionEvent.ACTION_UP:
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeLarge);
					break;
				case MotionEvent.ACTION_DOWN:
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeXLarge);
					rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
					break;
			}
			return false;
		}
	}
}
