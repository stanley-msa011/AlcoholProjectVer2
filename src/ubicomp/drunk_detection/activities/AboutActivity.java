package ubicomp.drunk_detection.activities;

import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;
import ubicomp.drunk_detection.ui.CustomToastSmall;
import ubicomp.drunk_detection.ui.CustomTypefaceSpan;
import ubicomp.drunk_detection.ui.ScreenSize;
import ubicomp.drunk_detection.ui.TextSize;
import ubicomp.drunk_detection.ui.Typefaces;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class AboutActivity extends Activity {

	private TextView titleText,aboutText, copyrightText, about, phone, phone_number, email, setting,timeText,timeMinute;
	private Spinner timeSpinner;
	private String[] time_array;
	
	private RelativeLayout logoLayout;
	private ImageView logo,logo0,logo1,logo2;
	private RelativeLayout titleLayout;
	private Point screen;
	private Typeface wordTypeface,wordTypefaceBold,digitTypeface,digitTypefaceBold;
	
	private static final String EMAIL = "ubicomplab.ntu@gmail.com";
	private static final String COPYRIGHT  = "\u00a9 2013 National Taiwan University,\nAcademia Sinica, and Taipei City Hospital";
	
	private int hidden_state;
	private Activity activity;
	
	private int textSize;
	private SharedPreferences sp;
	
	private int time_gap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_about);
		activity = this;
		
		sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		wordTypeface = Typefaces.getWordTypeface(this);
		wordTypefaceBold = Typefaces.getWordTypefaceBold(this);
		digitTypeface = Typefaces.getDigitTypeface(this);
		digitTypefaceBold = Typefaces.getDigitTypefaceBold(this);
		titleLayout = (RelativeLayout) this.findViewById(R.id.about_title_layout );
		titleText = (TextView) this.findViewById(R.id.about_title);
		phone = (TextView)this.findViewById(R.id.about_phone);
		phone_number = (TextView)this.findViewById(R.id.about_phone_number);
		email = (TextView)this.findViewById(R.id.about_email);
		about = (TextView) this.findViewById(R.id.about_about);
		aboutText = (TextView) this.findViewById(R.id.about_content);
		setting = (TextView) this.findViewById(R.id.about_setting);
		timeText = (TextView) this.findViewById(R.id.about_time_gap);
		timeMinute = (TextView) this.findViewById(R.id.about_time_minute);
		logoLayout = (RelativeLayout) this.findViewById(R.id.about_logos);
		logo = (ImageView) this.findViewById(R.id.about_logo);
		logo0 = (ImageView) this.findViewById(R.id.about_logo0);
		logo1 = (ImageView) this.findViewById(R.id.about_logo1);
		logo2 = (ImageView) this.findViewById(R.id.about_logo2);
		copyrightText = (TextView) this.findViewById(R.id.about_copyright);
		timeSpinner = (Spinner) this.findViewById(R.id.about_time_spinner);
		
		time_array = getResources().getStringArray(R.array.about_time_selection);
		
		ArrayAdapter<CharSequence> timeAdapter =
				new ArrayAdapter<CharSequence>(
						getBaseContext(),
						R.layout.time_spinner,R.id.custom_spinner_text,
						time_array); 
		timeSpinner.setAdapter(timeAdapter);
		time_gap = sp.getInt("notification_gap", 120);
		int default_spinner_idx = 0;
		for (int i=0;i<time_array.length;++i){
			if (Integer.valueOf(time_array[i])==time_gap)
				default_spinner_idx = i;
		}
		
		timeSpinner.setSelection(default_spinner_idx);
		
		timeSpinner.setOnItemSelectedListener(
				new OnItemSelectedListener(){
					@Override
					public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
						
						int new_setting = Integer.valueOf(time_array[position]);
						if (time_gap != new_setting){
							SharedPreferences.Editor edit = sp.edit();
							edit.putInt("notification_gap",new_setting);
							edit.commit();
							time_gap = new_setting;
							ClickLogger.Log(getBaseContext(), ClickLogId.ABOUT_TIME_SPINNER_SELECTION);
							BootBoardcastReceiver.testNotificationSetting(getBaseContext(), getIntent());
						}
					}
					@Override
					public void onNothingSelected(AdapterView<?> parent) {}}
		);
		
		screen = ScreenSize.getScreenSize(this);
		
		int titleSize =TextSize.smallTitleTextSize(this);
		textSize =  TextSize.normalTextSize(this);
		
		int icon_size = screen.x * 106/480;
		
		RelativeLayout.LayoutParams logoParam = (RelativeLayout.LayoutParams) logo.getLayoutParams();
		logoParam.leftMargin =  screen.x * 26/480;
		
		RelativeLayout.LayoutParams tParam = (RelativeLayout.LayoutParams) titleText.getLayoutParams();
		tParam.leftMargin =  screen.x * 27/480;
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
		titleText.setTypeface(wordTypefaceBold);
		titleText.setText(R.string.app_name);
		
		LinearLayout.LayoutParams ttParam = (LinearLayout.LayoutParams) titleLayout.getLayoutParams();
		ttParam.height = screen.x*245/1080;
		
		RelativeLayout.LayoutParams aaParam = (RelativeLayout.LayoutParams) about.getLayoutParams();
		aaParam.leftMargin = screen.x * 26/480;
		about.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		about.setTypeface(wordTypefaceBold);
		
		RelativeLayout.LayoutParams asParam = (RelativeLayout.LayoutParams) setting.getLayoutParams();
		asParam.leftMargin = screen.x * 26/480;
		setting.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		setting.setTypeface(wordTypefaceBold);
		
		RelativeLayout.LayoutParams timeTextParam = (RelativeLayout.LayoutParams) timeText.getLayoutParams();
		timeTextParam.leftMargin = screen.x * 26/480;
		timeText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		timeText.setTypeface(wordTypefaceBold);
		
		RelativeLayout.LayoutParams timeMinuteParam = (RelativeLayout.LayoutParams) timeMinute.getLayoutParams();
		timeMinuteParam.leftMargin = screen.x * 5/480;
		timeMinute.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		timeMinute.setTypeface(wordTypefaceBold);
		
		RelativeLayout.LayoutParams aParam = (RelativeLayout.LayoutParams) aboutText.getLayoutParams();
		aParam.leftMargin = aParam.rightMargin = screen.x * 27/480;
		aParam.topMargin = screen.x * 38/480;
		aboutText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		aboutText.setTypeface(wordTypefaceBold);
		aboutText.setLineSpacing(0, 1.2F);
		
		RelativeLayout.LayoutParams pParam = (RelativeLayout.LayoutParams) phone.getLayoutParams();
		pParam.leftMargin = screen.x * 27/480;
		pParam.topMargin = screen.x * 4/480;
		phone.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		phone.setTypeface(wordTypeface);
		phone.setLineSpacing(0, 1.2F);
		
		RelativeLayout.LayoutParams pnParam = (RelativeLayout.LayoutParams) phone_number.getLayoutParams();
		pnParam.leftMargin = screen.x * 10/480;
		pnParam.topMargin = screen.x * 0/480;
		phone_number.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		phone_number.setTypeface(digitTypefaceBold);
		phone_number.setLineSpacing(0, 1.2F);
		
		RelativeLayout.LayoutParams emailParam = (RelativeLayout.LayoutParams) email.getLayoutParams();
		emailParam.leftMargin = screen.x * 10/480;
		emailParam.topMargin = screen.x * 0/480;
		email.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		email.setTypeface(digitTypefaceBold);
		email.setLineSpacing(0, 1.2F);
		
		RelativeLayout.LayoutParams lParam = (RelativeLayout.LayoutParams) logoLayout.getLayoutParams();
		lParam.topMargin = screen.x * 15/480; 
		
		RelativeLayout.LayoutParams l0Param = (RelativeLayout.LayoutParams) logo0.getLayoutParams();
		l0Param.height = l0Param.width =icon_size;
		l0Param.leftMargin = screen.x * 27/480;
		l0Param.rightMargin = screen.x * 90/1080; 
		RelativeLayout.LayoutParams l1Param = (RelativeLayout.LayoutParams) logo1.getLayoutParams();
		l1Param.height = l1Param.width = icon_size;
		RelativeLayout.LayoutParams l2Param = (RelativeLayout.LayoutParams) logo2.getLayoutParams();
		l2Param.height = icon_size;
		l2Param.leftMargin = screen.x *60/1080;
		l2Param.rightMargin = screen.x * 27/480;
		
		RelativeLayout.LayoutParams cParam = (RelativeLayout.LayoutParams) copyrightText.getLayoutParams();
		cParam.leftMargin = screen.x * 27/480;
		cParam.topMargin = screen.x *42/480;
		cParam.bottomMargin = screen.x *27/480;
		copyrightText.setTextSize(TypedValue.COMPLEX_UNIT_PX, TextSize.smallTextSize(activity));
		copyrightText.setTypeface(digitTypeface);
		copyrightText.setText(COPYRIGHT);
		
		
		logo.setOnTouchListener(
				new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (hidden_state == 0)
							++hidden_state;
						else if (hidden_state ==4){
							Intent newIntent = new Intent(activity, DeveloperActivity.class);
							activity.startActivity(newIntent);
						}
						else 
							hidden_state = 0;
						return false;
					}
				}
				);
		logo0.setOnTouchListener(
				new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN){
							if (hidden_state == 1)
								++hidden_state;
							else 
								hidden_state = 0;
						}
						return false;
					}
				}
				);
		logo1.setOnTouchListener(
				new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN){
							if (hidden_state == 2)
								++hidden_state;
							else 
								hidden_state = 0;
						}
						return false;
					}
				}
				);
		logo2.setOnTouchListener(
				new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN){
							if (hidden_state == 3)
								++hidden_state;
							else 
								hidden_state = 0;
						}
						return false;
					}
				}
				);
		
		
		
		
		String[] message = getResources().getStringArray(R.array.about_message);
		String ntu = getString(R.string.ntu);
		String dot = getString(R.string.dot);
		String sinica = getString(R.string.sinica);
		String taipei_city_hospital = getString(R.string.taipei_city_hospital);
		String happ_design = getString(R.string.happ_design);
		
		String curVersion = getString(R.string.current_version);
		String rickie_wu = getString(R.string.rickie_wu);
		String yuga_huang = getString(R.string.yuda_huang);
		String versionName =" unknown";
		PackageInfo pinfo;
		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionName = pinfo.versionName;
		} catch (NameNotFoundException e) {}
		
    	Spannable helpSpannable = new SpannableString(
    			message[0]+"\n"+
    			ntu+dot+sinica+dot+taipei_city_hospital+
    			message[1]+"\n\n"+message[2]+"\n"+message[3]+"\n"+curVersion+
    			versionName+"\n\n"+
    			message[4]+
    			happ_design+"\n"+
    			message[5]+rickie_wu+"\n"+
    			message[6]+yuga_huang+"\n"
    			);
		int start= 0;
		int end =message[0].length()+1;
		helpSpannable.setSpan(new CustomTypefaceSpan("custom1",wordTypeface,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start=end;
		end = start+ntu.length()+dot.length()+sinica.length()+dot.length()+taipei_city_hospital.length();
		helpSpannable.setSpan(new CustomTypefaceSpan("custom2",wordTypefaceBold,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start+message[1].length()+2+message[2].length()+1+message[3].length()+1+curVersion.length();
		helpSpannable.setSpan(new CustomTypefaceSpan("custom1",wordTypeface,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start + versionName.length()+2;
		helpSpannable.setSpan(new CustomTypefaceSpan("custom3",digitTypefaceBold,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start + message[4].length();
		helpSpannable.setSpan(new CustomTypefaceSpan("custom1",wordTypeface,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start + happ_design.length()+1;
		helpSpannable.setSpan(new CustomTypefaceSpan("custom3",digitTypefaceBold,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start + message[5].length();
		helpSpannable.setSpan(new CustomTypefaceSpan("custom1",wordTypeface,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start + rickie_wu.length()+1;
		helpSpannable.setSpan(new CustomTypefaceSpan("custom3",digitTypefaceBold,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start + message[6].length();
		helpSpannable.setSpan(new CustomTypefaceSpan("custom1",wordTypeface,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start + yuga_huang.length()+1;
		helpSpannable.setSpan(new CustomTypefaceSpan("custom3",digitTypefaceBold,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		
		aboutText.setText(helpSpannable);
		
		inflater = LayoutInflater.from(this);
		shadowBg = new View(this);
		shadowBg.setKeepScreenOn(true);
		shadowBg.setBackgroundColor(0x99000000);
		callLayout = (RelativeLayout) inflater.inflate(R.layout.call_check_layout, null);
		setCallCheckBox();
		
		phone_number.setOnClickListener(new CallCheckOnClickListener());
		email.setOnClickListener(new EmailOnClickListener());
	}

	private LayoutInflater inflater;
	private RelativeLayout callLayout;
	private FrameLayout bgLayout;
	private TextView callOK,callCancel,callHelp;
	private View shadowBg;
	//Call
	
	private void setCallCheckBox(){
		
		callOK = (TextView) callLayout.findViewById(R.id.call_ok_button);
		callCancel = (TextView) callLayout.findViewById(R.id.call_cancel_button);
		callHelp = (TextView) callLayout.findViewById(R.id.call_help);
		
		int textSize = TextSize.normalTextSize(activity);
		
		callHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		callHelp.setTypeface(wordTypefaceBold);
		RelativeLayout.LayoutParams hParam = (LayoutParams) callHelp.getLayoutParams();
		hParam.width = screen.x * 349/480;
		hParam.height = screen.x * 114/480;
		
		callOK.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		callOK.setTypeface(wordTypefaceBold);
		callCancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		callCancel.setTypeface(wordTypefaceBold);
		
		RelativeLayout.LayoutParams rParam = (LayoutParams) callOK.getLayoutParams();
		rParam.width = screen.x * 154/480;
		rParam.height = screen.x * 60/480;
		rParam.topMargin = screen.x * 5/480;
		rParam.rightMargin = screen.x * 15/480; 
		RelativeLayout.LayoutParams pParam = (LayoutParams) callCancel.getLayoutParams();
		pParam.width = screen.x * 154/480;
		pParam.height = screen.x * 60/480;
		pParam.topMargin = screen.x * 5/480;
		pParam.leftMargin = screen.x * 35/1480; 
	}
	
	private class CallCheckOnClickListener  implements View.OnClickListener{
		
		@TargetApi(Build.VERSION_CODES.FROYO)
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			bgLayout = (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);

			bgLayout.addView(shadowBg);
			bgLayout.addView(callLayout);
			FrameLayout.LayoutParams shadowParam = (FrameLayout.LayoutParams) shadowBg.getLayoutParams();
			if (Build.VERSION.SDK_INT>=8)
				shadowParam.width = shadowParam.height = LayoutParams.MATCH_PARENT;
			else
				shadowParam.width = shadowParam.height = LayoutParams.FILL_PARENT;
			
			FrameLayout.LayoutParams boxParam = (FrameLayout.LayoutParams) callLayout.getLayoutParams();
			boxParam.width = screen.x * 349/480;
			boxParam.height = screen.x * 189/480;
			boxParam.gravity=Gravity.CENTER;
			
			callHelp.setText(R.string.phone_check);
			callOK.setOnClickListener(new CallOnClickListener());
			callCancel.setOnClickListener(new CallCancelOnClickListener());
			phone_number.setOnClickListener(null);
			email.setOnClickListener(null);
		}
	}
	
	private class CallCancelOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			bgLayout.removeView(shadowBg);
			bgLayout.removeView(callLayout);
			phone_number.setOnClickListener(new CallCheckOnClickListener());
			email.setOnClickListener(new EmailOnClickListener());
		}
		
	}
	
	private class CallOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLogger.Log(getBaseContext(), ClickLogId.EMOTIONDIY_CALL);
			Intent intentDial = new Intent("android.intent.action.CALL",Uri.parse("tel:0233664926"));
			activity.startActivity(intentDial);
			activity.finish();
		}
	}
	
	
private class EmailOnClickListener  implements View.OnClickListener{
		
		@Override
		public void onClick(View v) {
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL  , new String[]{EMAIL});
			
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			String uid = sp.getString("uid",  "sober_default_test");
			if (uid.equals( "sober_default_test")){
				CustomToastSmall.generateToast(getBaseContext(), R.string.email_reject);
				return;
			}
			i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject)+" "+uid);
			try {
			    startActivity(Intent.createChooser(i, getString(R.string.email_message)));
			} catch (android.content.ActivityNotFoundException ex) {
				CustomToastSmall.generateToast(getBaseContext(), R.string.email_fail);
			}
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK ){
			if (callLayout.getParent()!=null && callLayout.getParent().equals(bgLayout)){
				bgLayout.removeView(shadowBg);
				bgLayout.removeView(callLayout);
				phone_number.setOnClickListener(new CallCheckOnClickListener());
				email.setOnClickListener(new EmailOnClickListener());
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}
	
}