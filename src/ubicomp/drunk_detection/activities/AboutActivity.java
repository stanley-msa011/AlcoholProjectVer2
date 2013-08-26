package ubicomp.drunk_detection.activities;

import ubicomp.drunk_detection.ui.CustomTypefaceSpan;
import ubicomp.drunk_detection.ui.Typefaces;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AboutActivity extends Activity {

	private TextView titleText,aboutText, copyrightText, about;
	private RelativeLayout logoLayout;
	private ImageView logo,logo0,logo1,logo2;
	private RelativeLayout titleLayout;
	private Point screen;
	private Typeface wordTypeface,wordTypefaceBold,digitTypeface,digitTypefaceBold;
	
	private static final String COPYRIGHT  = "\u00a9 2013 National Taiwan University,\nAcademia Sinica, and Taipei City Hospital";
	
	private int hidden_state;
	private Activity activity;
	
	private int textSize;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_about);
		activity = this;
		
		wordTypeface = Typefaces.getWordTypeface(this);
		wordTypefaceBold = Typefaces.getWordTypefaceBold(this);
		digitTypeface = Typefaces.getDigitTypeface(this);
		digitTypefaceBold = Typefaces.getDigitTypefaceBold(this);
		titleLayout = (RelativeLayout) this.findViewById(R.id.about_title_layout );
		titleText = (TextView) this.findViewById(R.id.about_title);
		about = (TextView) this.findViewById(R.id.about_about);
		aboutText = (TextView) this.findViewById(R.id.about_content);
		logoLayout = (RelativeLayout) this.findViewById(R.id.about_logos);
		logo = (ImageView) this.findViewById(R.id.about_logo);
		logo0 = (ImageView) this.findViewById(R.id.about_logo0);
		logo1 = (ImageView) this.findViewById(R.id.about_logo1);
		logo2 = (ImageView) this.findViewById(R.id.about_logo2);
		copyrightText = (TextView) this.findViewById(R.id.about_copyright);
		
		Display display = getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT<13){
			@SuppressWarnings("deprecation")
			int w = display.getWidth();
			@SuppressWarnings("deprecation")
			int h = display.getHeight();
			screen = new Point(w,h);
		}
		else{
			screen = new Point();
			display.getSize(screen);
		}
		int titleSize = screen.x * 24/480;
		textSize =  screen.x * 21/480;
		
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
		
		RelativeLayout.LayoutParams aParam = (RelativeLayout.LayoutParams) aboutText.getLayoutParams();
		aParam.leftMargin = aParam.rightMargin = screen.x * 27/480;
		aParam.topMargin = screen.x * 38/480;
		aboutText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		aboutText.setTypeface(wordTypefaceBold);
		aboutText.setLineSpacing(0, 1.2F);
		
		RelativeLayout.LayoutParams lParam = (RelativeLayout.LayoutParams) logoLayout.getLayoutParams();
		lParam.topMargin = screen.x * 60/480; 
		
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
		cParam.bottomMargin = screen.x *27/480;
		copyrightText.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 16/480);
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
    			message[6]+yuga_huang
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
		end = start + yuga_huang.length();
		helpSpannable.setSpan(new CustomTypefaceSpan("custom3",digitTypefaceBold,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		
		aboutText.setText(helpSpannable);
		
	}

}
