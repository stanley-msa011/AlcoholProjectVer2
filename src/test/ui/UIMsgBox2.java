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
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Switch;
import android.widget.TextView;

public class UIMsgBox2 {

	private TestFragment testFragment;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	private LinearLayout questionLayout = null;
	private ImageView boxBg;
	private Bitmap boxBgBmp;
	
	private ImageView boxLine;
	private Bitmap boxLineBmp;
	
	private Bitmap radioBmp, radioOnBmp;
	private Bitmap switchBmp, switchOnBmp;
	
	private TextView title,emotionText,desireText,gpsText;
	private TextView ok,cancel,wait;
	
	private Switch gpsSwitch;
	private TextView gpsOff,gpsOn;
	private RadioGroup emotionGroup,desireGroup1,desireGroup2;
	private RadioButton[] emotionButtons,desireButtons;
	
	private RelativeLayout mainLayout;
	
	private LinearLayout checkLayout;
	
	private Resources r;
	private Point screen;
	
	private Typeface digitTypeface;
	private Typeface wordTypeface;
	private Typeface wordTypefaceBold;
	
	public UIMsgBox2(TestFragment testFragment,RelativeLayout mainLayout){
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
		
		boxLayout = (RelativeLayout) inflater.inflate(R.layout.test_msg_box_2,null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		questionLayout = (LinearLayout) boxLayout.findViewById(R.id.msg_box_content_layout);
		boxBg = (ImageView) boxLayout.findViewById(R.id.msg_box_bg);
		boxLine = (ImageView) boxLayout.findViewById(R.id.msg_box_line);
		
		int textSize = screen.x * 63/1080;
		
		int textSizeLarge = screen.x * 80/1080;
		
		title = (TextView) boxLayout.findViewById(R.id.msg_box_title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSizeLarge );
		title.setTypeface(wordTypefaceBold);
		
		wait = (TextView) boxLayout.findViewById(R.id.msg_box_wait);
		wait.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSizeLarge );
		wait.setTypeface(wordTypefaceBold);
		
		emotionText = (TextView) boxLayout.findViewById(R.id.msg_box_emotion);
		emotionText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		emotionText.setTypeface(wordTypeface);
		
		emotionGroup = (RadioGroup) boxLayout.findViewById(R.id.msg_box_emotion_group);

		emotionButtons = new RadioButton[5];
		emotionButtons[0] = (RadioButton) boxLayout.findViewById(R.id.msg_box_e1);
		emotionButtons[1] = (RadioButton) boxLayout.findViewById(R.id.msg_box_e2);
		emotionButtons[2] = (RadioButton) boxLayout.findViewById(R.id.msg_box_e3);
		emotionButtons[3] = (RadioButton) boxLayout.findViewById(R.id.msg_box_e4);
		emotionButtons[4] = (RadioButton) boxLayout.findViewById(R.id.msg_box_e5);
		for (int i=0;i<emotionButtons.length;++i){
			emotionButtons[i].setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
			emotionButtons[i].setTypeface(digitTypeface);
		}
		
		
		desireText = (TextView) boxLayout.findViewById(R.id.msg_box_desire);
		desireText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		desireText.setTypeface(wordTypeface);
		
		desireGroup1 =  (RadioGroup) boxLayout.findViewById(R.id.msg_box_desire_group_1);
		desireGroup2 =  (RadioGroup) boxLayout.findViewById(R.id.msg_box_desire_group_2);
		
		
		desireButtons = new RadioButton[10];
		desireButtons[0] = (RadioButton) boxLayout.findViewById(R.id.msg_box_d1);
		desireButtons[1] = (RadioButton) boxLayout.findViewById(R.id.msg_box_d2);
		desireButtons[2] = (RadioButton) boxLayout.findViewById(R.id.msg_box_d3);
		desireButtons[3] = (RadioButton) boxLayout.findViewById(R.id.msg_box_d4);
		desireButtons[4] = (RadioButton) boxLayout.findViewById(R.id.msg_box_d5);
		desireButtons[5] = (RadioButton) boxLayout.findViewById(R.id.msg_box_d6);
		desireButtons[6] = (RadioButton) boxLayout.findViewById(R.id.msg_box_d7);
		desireButtons[7] = (RadioButton) boxLayout.findViewById(R.id.msg_box_d8);
		desireButtons[8] = (RadioButton) boxLayout.findViewById(R.id.msg_box_d9);
		desireButtons[9] = (RadioButton) boxLayout.findViewById(R.id.msg_box_d10);
		for (int i=0;i<desireButtons.length;++i){
			desireButtons[i].setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
			desireButtons[i].setTypeface(digitTypeface);
		}
		
		
		gpsText = (TextView) boxLayout.findViewById(R.id.msg_box_location);
		gpsText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		gpsText.setTypeface(wordTypeface);
		
		gpsSwitch = (Switch) boxLayout.findViewById(R.id.msg_box_location_switch);
		gpsSwitch.setTextOff("");
		gpsSwitch.setTextOn("");
		gpsSwitch.setSwitchMinWidth(screen.x*241/1080);
		
		gpsOff = (TextView) boxLayout.findViewById(R.id.msg_box_location_off);
		gpsOn = (TextView) boxLayout.findViewById(R.id.msg_box_location_on);
		gpsOff.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		gpsOff.setTypeface(wordTypeface);
		gpsOn.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		gpsOn.setTypeface(wordTypeface);
		
		ok = (TextView) boxLayout.findViewById(R.id.msg_box_ok);
		ok.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSizeLarge );
		ok.setTypeface(wordTypefaceBold);
		cancel = (TextView) boxLayout.findViewById(R.id.msg_box_cancel);
		cancel.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSizeLarge );
		cancel .setTypeface(wordTypefaceBold);
		
		checkLayout = (LinearLayout) boxLayout.findViewById(R.id.msg_box_check);
		
		for (int i=0;i<emotionButtons.length;++i){
			emotionButtons[i].setId(i+1);
		}
		for (int i=0;i<desireButtons.length;++i){
			desireButtons[i].setId(i+1);
		}

		emotionGroup.check(3);
		desireGroup1.check(3);
		
	}
	
	public void settingPreTask(){
		mainLayout.addView(boxLayout);
	}
	
	private class DesireOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			for (int i=0;i<desireButtons.length;++i){
				if (desireButtons[i] == v){
					desireButtons[i].setChecked(true);
					if (i <5)
						desireGroup2.clearCheck();
					else
						desireGroup1.clearCheck();
				}
			}
			
		}
		
	}
	
	public void settingInBackground(){
		
		Point s = FragmentTabs.getSize();
		
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inSampleSize = 2;
		
		Bitmap tmp;
		tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.test_question_background,opt);
		boxBgBmp = Bitmap.createScaledBitmap(tmp, s.x*984/1080, s.x*1300/1080, true);
		tmp.recycle();
		
		tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.test_question_line,opt);
		boxLineBmp = Bitmap.createScaledBitmap(tmp, s.x*781/1080, s.x*44/1080, true);
		tmp.recycle();
		
		tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.test_question_radio_button);
		radioBmp = Bitmap.createScaledBitmap(tmp, s.x*49/1080, s.x*50/1080, true);
		tmp.recycle();
		
		tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.test_question_radio_button_on);
		radioOnBmp = Bitmap.createScaledBitmap(tmp, s.x*49/1080, s.x*50/1080, true);
		tmp.recycle();
		
		tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.test_question_location_switch);
		switchBmp = Bitmap.createScaledBitmap(tmp, s.x*241/1080, s.x*65/1080, true);
		tmp.recycle();
		
		tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.test_question_location_switch_button);
		switchOnBmp = Bitmap.createScaledBitmap(tmp, s.x*170/1080, s.x*65/1080, true);
		tmp.recycle();
		
		RelativeLayout.LayoutParams boxParam = (LayoutParams) boxLayout.getLayoutParams();
		boxParam.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
		boxParam.width =s.x*984/1080;
		boxParam.height =s.x*1758/1080;
		
		int bSize= s.x*50/1080;
		/*
		for (int i=0;i<emotionButtons.length;++i){
			LinearLayout.LayoutParams bParam =( LinearLayout.LayoutParams) emotionButtons[i].getLayoutParams();
			bParam.width = bParam.height=bSize;
		}
		for (int i=0;i<desireButtons.length;++i){
			LinearLayout.LayoutParams bParam =( LinearLayout.LayoutParams) desireButtons[i].getLayoutParams();
			bParam.width = bParam.height=bSize;
		}
		*/
		LinearLayout.LayoutParams switchParam =( LinearLayout.LayoutParams) gpsSwitch.getLayoutParams();
		//switchParam.width = s.x*170/1080;
		//switchParam.height = s.x*65/1080;
		
		LinearLayout.LayoutParams checkParam =( LinearLayout.LayoutParams) checkLayout.getLayoutParams();
		checkParam.topMargin = s.x*80/1080;
		
	}
	
	private Drawable buttonDrawable;
	private Drawable buttonOnDrawable;
	
	@SuppressWarnings("deprecation")
	public  void settingPostTask(){
		boxBg.setImageBitmap(boxBgBmp);
		boxLine.setImageBitmap(boxLineBmp);
		gpsSwitch.setTrackDrawable(new BitmapDrawable(switchBmp));
		gpsSwitch.setThumbDrawable(new BitmapDrawable(switchOnBmp));
		
		buttonDrawable = new BitmapDrawable(radioBmp);
		buttonOnDrawable = new BitmapDrawable(radioOnBmp);

		DesireOnClickListener desireListener = new DesireOnClickListener();
		for (int i=0;i<desireButtons.length;++i){
			desireButtons[i].setOnClickListener(desireListener);
		}
		
		
	}
	
	public void clear(){
		Log.d("UIMSG","CLEAR");
		mainLayout.removeView(boxLayout);
		
		if (boxBgBmp!=null && !boxBgBmp.isRecycled()){
			boxBgBmp.recycle();
			boxBgBmp = null;
		}
		
		if (boxLineBmp!=null && !boxLineBmp.isRecycled()){
			boxLineBmp.recycle();
			boxLineBmp = null;
		}
		
		if (radioBmp!=null && !radioBmp.isRecycled()){
			radioBmp.recycle();
			radioBmp = null;
		}
		
		if (radioOnBmp!=null && !radioOnBmp.isRecycled()){
			radioOnBmp.recycle();
			radioOnBmp = null;
		}
		
		if (switchBmp!=null && !switchBmp.isRecycled()){
			switchBmp.recycle();
			switchBmp = null;
		}
		
		if (switchOnBmp!=null && !switchOnBmp.isRecycled()){
			switchOnBmp.recycle();
			switchOnBmp = null;
		}
	}
	
	public void generateGPSCheckBox(){
		
		title.setText("問卷調查");
		questionLayout.setVisibility(View.VISIBLE);
		boxLayout.setVisibility(View.VISIBLE);
		ok.setOnClickListener(new EndOnClickListener());
		cancel.setOnClickListener(new EndCancelOnClickListener());
		wait.setVisibility(View.INVISIBLE);
	}
	
	private class EndOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			boxLayout.setVisibility(View.INVISIBLE);
				boolean enableGPS = gpsSwitch.isChecked();
				int desire = -1;
				int d1 = desireGroup1.getCheckedRadioButtonId();
				int d2 = desireGroup2.getCheckedRadioButtonId();
				if (d1 >0)
					desire = d1;
				else if (d2 >0)
					desire = d2;
				
				int emotion =  -1;
				int e1 = emotionGroup.getCheckedRadioButtonId();
				if (e1 > 0)
					emotion = e1;
				
				String setting_str = desire+"/"+emotion+"/"+enableGPS; 
				Log.d("MSGBOX SETTING",setting_str);
				testFragment.writeQuestionFile(emotion, desire);
				
				testFragment.startGPS(enableGPS);
		}
		
	}
	
	private class EndCancelOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			boxLayout.setVisibility(View.INVISIBLE);
				int desire = -1;
				int emotion =  -1;
				testFragment.writeQuestionFile(emotion, desire);
				testFragment.startGPS(false);
		}
		
	}
	
	public void generateInitializingBox(){
		questionLayout.setVisibility(View.INVISIBLE);
		wait.setVisibility(View.VISIBLE);
		boxLayout.setVisibility(View.VISIBLE);
	}
	
	public void closeInitializingBox(){
			boxLayout.setVisibility(View.INVISIBLE);
			return;
	}
	
	
}
