package ubicomp.drunk_detection.activities;

import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLoggerLog;
import ubicomp.drunk_detection.activities.R;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class TutorialActivity extends Activity {

	private ImageView replay,arrow;
	private ImageView tab;
	private Drawable[] arrowDrawables;
	
	private TextView step;
	private TextView help;
	private TextView notify; 
	
	private LoadingHandler loadingHandler;
	private static Point size;
	
	private RelativeLayout layout;
	private Typeface digitTypeface;
	private Typeface wordTypefaceBold;
	
	private static final String[] STEP_STR = {"1","2","3"};
	private static String[] HELP_STR;
	
	private AlphaAnimation animation;
	
	private boolean isWideScreen;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tutorial);
		
		Display display = getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT<13){
			@SuppressWarnings("deprecation")
			int w = display.getWidth();
			@SuppressWarnings("deprecation")
			int h = display.getHeight();
			size = new Point(w,h);
		}
		else{
			size = new Point();
			display.getSize(size);
		}
		isWideScreen = (float)size.y/(float)size.x > 1.67;
		
		digitTypeface = Typeface.createFromAsset(this.getAssets(), "fonts/dinproregular.ttf");
		wordTypefaceBold  = Typeface.createFromAsset(this.getAssets(), "fonts/DFLiHeiStd-W5.otf");
		
		replay = (ImageView) this.findViewById(R.id.tutorial_reply);
		arrow = (ImageView) this.findViewById(R.id.tutorial_arrow);
		
		RelativeLayout.LayoutParams rParam = (RelativeLayout.LayoutParams) replay.getLayoutParams();
		rParam.bottomMargin = size.x * 32/480;
		
		step = (TextView) this.findViewById(R.id.tutorial_step);
		step.setTextSize(TypedValue.COMPLEX_UNIT_PX, size.x * 85/480);
		step.setTypeface(digitTypeface);
		
		RelativeLayout.LayoutParams sParam = (RelativeLayout.LayoutParams) step.getLayoutParams();
		sParam.topMargin = size.x * 79/480;
		sParam.height = size.x * 85/480;
		
		notify= (TextView) this.findViewById(R.id.tutorial_notify);
		notify.setTextSize(TypedValue.COMPLEX_UNIT_PX, size.x * 25/480);
		notify.setTypeface(wordTypefaceBold);
		
		RelativeLayout.LayoutParams nParam = (RelativeLayout.LayoutParams) notify.getLayoutParams();
		nParam.topMargin = size.x * 25/480;
		nParam.height = size.x * 25/480;
		
		help = (TextView) this.findViewById(R.id.tutorial_help);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, size.x * 25/480);
		help.setTypeface(wordTypefaceBold);
		RelativeLayout.LayoutParams hParam = (RelativeLayout.LayoutParams) help.getLayoutParams();
		if (isWideScreen)
			hParam.topMargin = size.x * 486/480;
		else
			hParam.topMargin = size.x * 446/480;
		hParam.height = size.x * 25/480;
		
		tab = (ImageView) this.findViewById(R.id.tutorial_tab);
		layout = (RelativeLayout) this.findViewById(R.id.tutorial_layout);
		
		HELP_STR = getResources().getStringArray(R.array.tutorial_step);
		
		loadingHandler = new LoadingHandler();
	}

	private ProgressDialog mDialog;
	
	protected void onStart(){
		mDialog = new ProgressDialog(this);
        mDialog.setMessage(this.getResources().getString(R.string.loading));
        mDialog.setCancelable(false);
        if (!mDialog.isShowing())
        	mDialog.show();
		super.onStart();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		loadingHandler.sendEmptyMessage(0);
	}
	
	protected void onPause(){
		super.onPause();
		loadingHandler.removeMessages(0);
		if (animation!=null)
			animation.cancel();
		if (arrow != null){
			arrow.setAnimation(null);
		}
	}
	
	@SuppressLint("HandlerLeak")
	private class LoadingHandler extends Handler{
		public void handleMessage(Message msg){

			arrowDrawables = new Drawable[3];
			arrowDrawables[0] = getResources().getDrawable(R.drawable.tutorial_arrow1);
			arrowDrawables[1] = getResources().getDrawable(R.drawable.tutorial_arrow2);
			arrowDrawables[2] = getResources().getDrawable(R.drawable.tutorial_arrow3);
			
			animation = new AlphaAnimation(1.F,0.F);
			animation.setRepeatCount(Animation.INFINITE);
			animation.setRepeatMode(Animation.REVERSE);
			animation.setDuration(300);
			arrow.setAnimation(animation);
			
			settingState(0);
			if (mDialog!=null && mDialog.isShowing())
				mDialog.dismiss();
			
		}
	}

	private void settingState(int state){
		step.setText(STEP_STR[state]);
		help.setText(HELP_STR[state]);
		
		RelativeLayout.LayoutParams aParam = (LayoutParams) arrow.getLayoutParams();
		aParam.addRule(RelativeLayout.RIGHT_OF,0);
		aParam.addRule(RelativeLayout.ABOVE,0);
		aParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT,0);
		if (state == 0){
			layout .setOnClickListener(new Listener(0));
			replay.setOnClickListener(null);
			replay.setVisibility(View.INVISIBLE);
			tab.setVisibility(View.INVISIBLE);
			arrow.setImageDrawable(arrowDrawables[0]);
			aParam.addRule(RelativeLayout.RIGHT_OF, help.getId());
			if (isWideScreen)
				aParam.topMargin = size.x * 550/480;
			else
				aParam.topMargin = size.x * 510/480;
			aParam.leftMargin = size.x * 10/480;
			animation.start();
		}
		else if (state == 1){
			layout .setOnClickListener(new Listener(1));
			replay.setOnClickListener(null);
			replay.setVisibility(View.INVISIBLE);
			tab.setVisibility(View.VISIBLE);
			arrow.setImageDrawable(arrowDrawables[1]);
			aParam.addRule(RelativeLayout.ABOVE, tab.getId());
			aParam.topMargin = 0;
			aParam.leftMargin = size.x * 40/480;
			animation.start();
		}
		else if (state == 2){
			layout .setOnClickListener(new EndListener());
			replay.setOnClickListener(new Listener(-1));
			replay.setVisibility(View.VISIBLE);
			tab.setVisibility(View.INVISIBLE);
			arrow.setImageDrawable(arrowDrawables[2]);
			aParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			if (isWideScreen)
				aParam.topMargin = size.x * 377/480;
			else
				aParam.topMargin = size.x * 357/480;
			aParam.leftMargin = size.x * 170/480;
			animation.start();
		}
	}
	
	private class Listener implements View.OnClickListener{

		private int step;
		Listener(int step){
			this.step = step;
		}
		@Override
		public void onClick(View v) {
			if (step == -1)
				ClickLoggerLog.Log(getBaseContext(), ClickLogId.TUTORIAL_REPLAY);
			else
				ClickLoggerLog.Log(getBaseContext(), ClickLogId.TUTORIAL_CLICK);
			settingState(step+1);
		}
		
	}
	
	private class EndListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLoggerLog.Log(getBaseContext(), ClickLogId.TUTORIAL_END);
			arrow.setAnimation(null);
			animation.cancel();
			finish();
		}
	}
	
}
