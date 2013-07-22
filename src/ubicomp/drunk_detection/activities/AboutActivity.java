package ubicomp.drunk_detection.activities;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.Html;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AboutActivity extends Activity {

	private TextView titleText,aboutText, copyrightText, about;
	private LinearLayout logoLayout;
	private ImageView logo,logo0,logo1,logo2;
	private RelativeLayout titleLayout;
	private Point screen;
	private Typeface wordTypefaceBold,digitTypeface;
	
	private static final String COPYRIGHT  = "\u00a9 2013 National Taiwan University,\n Academia Sinica, Taipei City Hospital";
	
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
		
		wordTypefaceBold = Typeface.createFromAsset(this.getAssets(), "fonts/DFLiHeiStd-W5.otf");
		digitTypeface = Typeface.createFromAsset(this.getAssets(), "fonts/dinproregular.ttf");
		titleLayout = (RelativeLayout) this.findViewById(R.id.about_title_layout );
		titleText = (TextView) this.findViewById(R.id.about_title);
		about = (TextView) this.findViewById(R.id.about_about);
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
		int titleSize = screen.x * 24/480;
		textSize =  screen.x * 21/480;
		
		int icon_size = screen.x * 104/480;
		
		RelativeLayout.LayoutParams logoParam = (RelativeLayout.LayoutParams) logo.getLayoutParams();
		logoParam.leftMargin =  screen.x * 26/480;
		
		RelativeLayout.LayoutParams tParam = (RelativeLayout.LayoutParams) titleText.getLayoutParams();
		tParam.leftMargin =  screen.x * 26/480;
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
		titleText.setTypeface(wordTypefaceBold);
		titleText.setText("戒酒小幫手");
		
		LinearLayout.LayoutParams ttParam = (LinearLayout.LayoutParams) titleLayout.getLayoutParams();
		ttParam.height = screen.x*230/1080;
		
		RelativeLayout.LayoutParams aaParam = (RelativeLayout.LayoutParams) about.getLayoutParams();
		aaParam.leftMargin = screen.x * 26/480;
		about.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		about.setTypeface(wordTypefaceBold);
		
		RelativeLayout.LayoutParams aParam = (RelativeLayout.LayoutParams) aboutText.getLayoutParams();
		aParam.leftMargin = aParam.rightMargin = screen.x * 26/480;
		aParam.topMargin = screen.x * 38/480;
		aboutText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		aboutText.setTypeface(wordTypefaceBold);
		aboutText.setLineSpacing(0, 1.2F);
		
		RelativeLayout.LayoutParams lParam = (RelativeLayout.LayoutParams) logoLayout.getLayoutParams();
		lParam.topMargin = screen.x * 60/480; 
		
		int gap = screen.x * 18/480;
		LinearLayout.LayoutParams l0Param = (LinearLayout.LayoutParams) logo0.getLayoutParams();
		l0Param.height = icon_size;
		l0Param.leftMargin = l0Param.rightMargin = gap; 
		LinearLayout.LayoutParams l1Param = (LinearLayout.LayoutParams) logo1.getLayoutParams();
		l1Param.height = icon_size;
		l1Param.leftMargin = l1Param.rightMargin = gap; 
		LinearLayout.LayoutParams l2Param = (LinearLayout.LayoutParams) logo2.getLayoutParams();
		l2Param.height = icon_size;
		
		RelativeLayout.LayoutParams cParam = (RelativeLayout.LayoutParams) copyrightText.getLayoutParams();
		cParam.leftMargin = screen.x * 26/480;
		cParam.topMargin = screen.x *100/480;
		copyrightText.setTextSize(TypedValue.COMPLEX_UNIT_PX, screen.x * 16/480);
		copyrightText.setTypeface(digitTypeface);
		copyrightText.setText(COPYRIGHT);
		
		
		
		
		logo.setOnClickListener(
				new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						if (hidden_state == 0)
							++hidden_state;
						else if (hidden_state ==4){
							Intent newIntent = new Intent(activity, DeveloperActivity.class);
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
		
		sb.append("此應用程式由<br/><strong>國立台灣大學、</strong><br/><strong>中央研究院、</strong><br/><strong>臺北市立聯合醫院</strong>合作開發。<br/><br/>" +
				"開發目為協助酗酒患者戒酒並自我控制使用。<br/>" +
				"此應用程式需搭配專用酒測器方可使用。");
		
		PackageInfo pinfo;
		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String versionName = pinfo.versionName;
			sb.append("<br/><br/>目前版本：");
			sb.append(versionName);
		} catch (NameNotFoundException e) {
		}
		aboutText.setText(Html.fromHtml(sb.toString()));
		
	}

}
