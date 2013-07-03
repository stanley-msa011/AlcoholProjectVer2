package ubicomp.drunk_detection.activities;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class AboutActivity extends Activity {

	private TextView titleText,aboutText, copyrightText;
	private LinearLayout logoLayout;
	private ImageView logo,logo0,logo1,logo2;
	private Point screen;
	private Typeface wordTypeface,wordTypefaceBold;
	
	private static final String COPYRIGHT  = "\u00a9 2013 National Taiwan University,\n Academia Sinica, Taipei City Hospital";
	
	private int hidden_state;
	private Activity activity;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab_layout);
		setContentView(R.layout.activity_about);
		activity = this;
		
		wordTypeface = Typeface.createFromAsset(this.getAssets(), "fonts/dfheistd-w3.otf");
		wordTypefaceBold = Typeface.createFromAsset(this.getAssets(), "fonts/dfheistd-w5.otf");
		
		titleText = (TextView) this.findViewById(R.id.about_title);
		aboutText = (TextView) this.findViewById(R.id.about_content);
		logoLayout = (LinearLayout) this.findViewById(R.id.about_logos);
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
		
		int icon_size = screen.x * 200/1080;
		int icon_gap = screen.x * 50/1080;
		
		RelativeLayout.LayoutParams logoParam = (LayoutParams) logo.getLayoutParams();
		logoParam.width = screen.x * 900/1080;
		logoParam.topMargin = icon_gap*3;
		logoParam.leftMargin = screen.x * 90/1080;;
		logoParam.width = logoParam.height = icon_size;
		
		RelativeLayout.LayoutParams tParam = (LayoutParams) titleText.getLayoutParams();
		tParam.width = screen.x * 900/1080;
		tParam.topMargin = icon_gap * 3;
		tParam.leftMargin = icon_gap;
		tParam.height = icon_size;
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 96/1080);
		titleText.setTypeface(wordTypefaceBold);
		titleText.setText("戒酒小幫手");
		
		RelativeLayout.LayoutParams aParam = (LayoutParams) aboutText.getLayoutParams();
		aParam.width = screen.x * 900/1080;
		aParam.topMargin = icon_size;
		aboutText.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 64/1080);
		aboutText.setTypeface(wordTypeface);
		
		RelativeLayout.LayoutParams lParam = (LayoutParams) logoLayout.getLayoutParams();
		lParam.width = screen.x * 900/1080;
		lParam.height = icon_size;
		lParam.topMargin = icon_size;
		
		LinearLayout.LayoutParams l0Param = (LinearLayout.LayoutParams) logo0.getLayoutParams();
		l0Param.width = icon_size;
		l0Param.leftMargin = l0Param.rightMargin = icon_gap;
		LinearLayout.LayoutParams l1Param = (LinearLayout.LayoutParams) logo1.getLayoutParams();
		l1Param.width = icon_size;
		l1Param.leftMargin = l1Param.rightMargin = icon_gap;
		LinearLayout.LayoutParams l2Param = (LinearLayout.LayoutParams) logo2.getLayoutParams();
		l2Param.width = icon_size;
		l2Param.leftMargin = l2Param.rightMargin = icon_gap;
		
		RelativeLayout.LayoutParams cParam = (LayoutParams) copyrightText.getLayoutParams();
		cParam.width = screen.x * 900/1080;
		cParam.bottomMargin = icon_gap*3;
		copyrightText.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 48/1080);
		copyrightText.setTypeface(wordTypeface);
		copyrightText.setText(COPYRIGHT);
		
		logo.setImageResource(R.drawable.icon);
		logo0.setImageResource(R.drawable.logo0);
		logo1.setImageResource(R.drawable.logo1);
		logo2.setImageResource(R.drawable.logo2);
		
		
		
		logo.setOnClickListener(
				new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						if (hidden_state == 0)
							++hidden_state;
						else if (hidden_state ==4){
							Intent newIntent = new Intent(activity, PreSettingActivity.class);
							activity.startActivity(newIntent);
						}
						else 
							hidden_state = 0;
					}
				});
		
		logo0.setOnClickListener(
				new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						if (hidden_state == 1)
							++hidden_state;
						else 
							hidden_state = 0;
					}
				});
		
		logo1.setOnClickListener(
				new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						if (hidden_state == 2)
							++hidden_state;
						else 
							hidden_state = 0;
					}
				});
		
		logo2.setOnClickListener(
				new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						if (hidden_state == 3)
							++hidden_state;
						else 
							hidden_state = 0;
					}
				});
		StringBuilder sb = new StringBuilder();
		
		sb.append("此應用程式由國立台灣大學、中央研究院、臺北市立聯合醫院合作開發。\n" +
				"開發目為協助酗酒患者戒酒並自我控制使用。\n" +
				"此應用程式需搭配專用酒測器方可使用。");
		
		PackageInfo pinfo;
		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String versionName = pinfo.versionName;
			sb.append("\n\n目前版本：");
			sb.append(versionName);
		} catch (NameNotFoundException e) {
		}
		aboutText.setText(sb.toString());
		
	}

}
