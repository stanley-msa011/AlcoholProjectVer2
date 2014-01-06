package ubicomp.drunk_detection.activities;

import data.uploader.ClickLogUploader;
import data.uploader.DataUploader;
import ubicomp.drunk_detection.ui.ScreenSize;
import ubicomp.drunk_detection.ui.TextSize;
import ubicomp.drunk_detection.ui.Typefaces;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;

public class LockedActivity extends Activity {

	private Typeface wordTypefaceBold,wordTypeface;
	private ImageView logo,icon;
	private LinearLayout nextButton;
	private RelativeLayout titleLayout;
	private TextView titleText,about,nextText;
	private TextView messageText; 
	
	private Point screen;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_locked);
		
		wordTypefaceBold = Typefaces.getWordTypefaceBold(this);
		wordTypeface = Typefaces.getWordTypeface(this);
		
		titleLayout = (RelativeLayout) this.findViewById(R.id.lock_title_layout );
		titleText = (TextView) this.findViewById(R.id.lock_title);
		logo = (ImageView) this.findViewById(R.id.lock_logo);
		about = (TextView) this.findViewById(R.id.lock_about);
		messageText = (TextView) this.findViewById(R.id.lock_message);
		nextText = (TextView)this.findViewById(R.id.lock_goto_about);
		nextButton = (LinearLayout)this.findViewById(R.id.lock_about_button);
		
		screen = ScreenSize.getScreenSize(this);
		
		RelativeLayout.LayoutParams logoParam = (RelativeLayout.LayoutParams) logo.getLayoutParams();
		logoParam.leftMargin =  screen.x * 26/480;
		
		int titleSize = TextSize.smallTitleTextSize(this);
		
		RelativeLayout.LayoutParams tParam = (RelativeLayout.LayoutParams) titleText.getLayoutParams();
		tParam.leftMargin =  screen.x * 27/480;
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
		titleText.setTypeface(wordTypefaceBold);
		titleText.setText(R.string.app_name);
		
		LinearLayout.LayoutParams ttParam = (LinearLayout.LayoutParams) titleLayout.getLayoutParams();
		ttParam.height = screen.x*245/1080;
		
		int textSize =  TextSize.normalTextSize(this);
		
		RelativeLayout.LayoutParams aaParam = (RelativeLayout.LayoutParams) about.getLayoutParams();
		aaParam.leftMargin = screen.x * 26/480;
		about.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		about.setTypeface(wordTypefaceBold);
		
		RelativeLayout.LayoutParams messageTextParam = (RelativeLayout.LayoutParams) messageText.getLayoutParams();
		messageTextParam.leftMargin = messageTextParam.rightMargin = screen.x * 27/480;
		messageTextParam.topMargin = screen.x * 38/480;
		messageText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		messageText.setTypeface(wordTypeface);
		messageText.setLineSpacing(0, 1.2F);
		
		nextText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		nextText.setTypeface(wordTypefaceBold);
		LinearLayout.LayoutParams nextTextParam = (LinearLayout.LayoutParams)nextText.getLayoutParams();
		nextTextParam.leftMargin = textSize;
		
		int iconMargin = screen.x * 33/480;
		icon = (ImageView) findViewById(R.id.lock_next);
		LinearLayout.LayoutParams iParam =(LinearLayout.LayoutParams) icon.getLayoutParams();
		iParam.rightMargin = iconMargin;
		
		nextButton.setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(getBaseContext(),AboutActivity.class);
						startActivity(intent);
					}
		});
	}

	@Override
	public void onResume(){
		super.onResume();
		DataUploader.upload(this);
		ClickLogUploader.upload(this);
		Intent a_intent = new Intent(this,RegularCheckService.class);
		this.startService(a_intent);
	}
}
