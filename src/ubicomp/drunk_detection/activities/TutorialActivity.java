package ubicomp.drunk_detection.activities;

import ubicomp.drunk_detection.activities.R;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
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

	private ImageView replay,next,arrow;
	private Bitmap  replayBmp, nextBmp, replayOffBmp, nextOffBmp,bgBmp;
	private Bitmap[] arrowBmps;
	private ImageView tab;
	private Bitmap tabBmp;
	
	private TextView step;
	private TextView help;
	private TextView notify; 
	
	private LoadingHandler loadingHandler;
	private static Point size;
	
	private RelativeLayout layout;
	//private Typeface digitTypeface;
	private Typeface wordTypeface;
	private Typeface wordTypefaceBold;
	
	private static final String[] STEP_STR = {"步驟 1","步驟 2","步驟 3"};
	private static final String[] HELP_STR = {"按下開關，使指示燈亮起","進入測試頁面按下開始按鈕","對準吹氣口持續吹氣五秒鐘"};
	
	private AlphaAnimation animation;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tutorial);
		
		
		
		Display display = getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT<13){
			int w = display.getWidth();
			int h = display.getHeight();
			size = new Point(w,h);
		}
		else{
			size = new Point();
			display.getSize(size);
		}
		
		//digitTypeface = Typeface.createFromAsset(this.getAssets(), "fonts/dinproregular.ttf");
		wordTypeface = Typeface.createFromAsset(this.getAssets(), "fonts/dfheistd-w3.otf");
		wordTypefaceBold  = Typeface.createFromAsset(this.getAssets(), "fonts/dfheistd-w5.otf");
		
		replay = (ImageView) this.findViewById(R.id.tutorial_reply);
		next = (ImageView) this.findViewById(R.id.tutorial_next);
		arrow = (ImageView) this.findViewById(R.id.tutorial_arrow);
		
		step = (TextView) this.findViewById(R.id.tutorial_step);
		help = (TextView) this.findViewById(R.id.tutorial_help);
		step.setTextSize(TypedValue.COMPLEX_UNIT_PX, size.x * 144/1080);
		step.setTypeface(wordTypefaceBold);
		
		RelativeLayout.LayoutParams sParam = (RelativeLayout.LayoutParams) step.getLayoutParams();
		sParam.topMargin = size.x * 200/1080;
		
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, size.x * 64/1080);
		help.setTypeface(wordTypeface);
		RelativeLayout.LayoutParams hParam = (RelativeLayout.LayoutParams) help.getLayoutParams();
		hParam.topMargin = size.x * 1150/1080;
		
		notify= (TextView) this.findViewById(R.id.tutorial_notify);
		notify.setTextSize(TypedValue.COMPLEX_UNIT_PX, size.x * 64/1080);
		notify.setTypeface(wordTypefaceBold);
		
		RelativeLayout.LayoutParams nParam = (RelativeLayout.LayoutParams) notify.getLayoutParams();
		nParam.topMargin = size.x * 40/1080;
		
		tab = (ImageView) this.findViewById(R.id.tutorial_tab);
		
		layout = (RelativeLayout) this.findViewById(R.id.tutorial_layout);
		
		loadingHandler = new LoadingHandler();
	}

	private ProgressDialog mDialog;
	
	protected void onStart(){
		mDialog = new ProgressDialog(this);
        mDialog.setMessage("載入中");
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
		if (replay != null)
			replay.setImageBitmap(null);
		if (next != null)
			next.setImageBitmap(null);
		if (tab != null)
			tab.setImageBitmap(null);
		if (arrow != null)
			arrow.setImageBitmap(null);
		
		if (layout!=null)
			layout.setBackground(null);
		if (bgBmp!=null && !bgBmp.isRecycled()){
			bgBmp.recycle();
			bgBmp = null;
		}
		if (replayBmp!=null && !replayBmp.isRecycled()){
			replayBmp.recycle();
			replayBmp = null;
		}
		if (replayOffBmp!=null && !replayOffBmp.isRecycled()){
			replayOffBmp.recycle();
			replayOffBmp = null;
		}
		if (nextBmp!=null && !nextBmp.isRecycled()){
			nextBmp.recycle();
			nextBmp = null;
		}
		if (nextOffBmp!=null && !nextOffBmp.isRecycled()){
			nextOffBmp.recycle();
			nextOffBmp = null;
		}
		if (tabBmp!=null && !tabBmp.isRecycled()){
			tabBmp.recycle();
			tabBmp = null;
		}
		if (arrowBmps!=null){
			for (int i=0;i<arrowBmps.length;++i)
				if (arrowBmps[i]!=null && !arrowBmps[i].isRecycled()){
					arrowBmps[i].recycle();
					arrowBmps[i] = null;
				}
		}
		mDialog.dismiss();
	}
	
	@SuppressLint("HandlerLeak")
	private class LoadingHandler extends Handler{
		public void handleMessage(Message msg){
			Bitmap tmp;
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inSampleSize = 4;
			tmp = BitmapFactory.decodeResource(getResources(), R.drawable.tutorial_bg, opt);
			bgBmp = Bitmap.createScaledBitmap(tmp, size.x, size.x*1920/1080,true);
			tmp.recycle();
			int buttonSize = size.x * 213/1080;
			tmp = BitmapFactory.decodeResource(getResources(), R.drawable.tutorial_replay);
			replayBmp = Bitmap.createScaledBitmap(tmp, buttonSize, buttonSize,true);
			tmp.recycle();
			tmp = BitmapFactory.decodeResource(getResources(), R.drawable.tutorial_replay_off);
			replayOffBmp = Bitmap.createScaledBitmap(tmp, buttonSize, buttonSize,true);
			tmp.recycle();
			tmp = BitmapFactory.decodeResource(getResources(), R.drawable.tutorial_next);
			nextBmp = Bitmap.createScaledBitmap(tmp, buttonSize, buttonSize,true);
			tmp.recycle();
			tmp = BitmapFactory.decodeResource(getResources(), R.drawable.tutorial_next_off);
			nextOffBmp = Bitmap.createScaledBitmap(tmp, buttonSize, buttonSize,true);
			tmp.recycle();
			
			tmp = BitmapFactory.decodeResource(getResources(), R.drawable.tutorial_tab);
			tabBmp = Bitmap.createScaledBitmap(tmp, size.x, size.x * 207/1080,true);
			tmp.recycle();
			
			arrowBmps = new Bitmap[3];
			tmp = BitmapFactory.decodeResource(getResources(), R.drawable.tutorial_arrow1);
			arrowBmps[0] = Bitmap.createScaledBitmap(tmp, buttonSize, buttonSize,true);
			if (arrowBmps[0]!= tmp)
				tmp.recycle();
			tmp = BitmapFactory.decodeResource(getResources(), R.drawable.tutorial_arrow2);
			arrowBmps[1] = Bitmap.createScaledBitmap(tmp, buttonSize, buttonSize,true);
			if (arrowBmps[1]!= tmp)
				tmp.recycle();
			tmp = BitmapFactory.decodeResource(getResources(), R.drawable.tutorial_arrow3);
			arrowBmps[2] = Bitmap.createScaledBitmap(tmp, buttonSize, buttonSize,true);
			if (arrowBmps[2]!= tmp)
				tmp.recycle();
			
			animation = new AlphaAnimation(1.F,0.F);
			animation.setRepeatCount(Animation.INFINITE);
			animation.setRepeatMode(Animation.REVERSE);
			animation.setDuration(300);
			arrow.setAnimation(animation);
			
			if (bgBmp!=null && !bgBmp.isRecycled())
				layout.setBackground(new BitmapDrawable(layout.getResources(),bgBmp));
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
		aParam.addRule(RelativeLayout.CENTER_HORIZONTAL,0);
		if (state == 0){
			layout .setOnClickListener(new Listener(0));
			next.setOnClickListener(null);
			next.setVisibility(View.INVISIBLE);
			replay.setOnClickListener(null);
			replay.setVisibility(View.INVISIBLE);
			tab.setVisibility(View.INVISIBLE);
			if (arrowBmps!=null && arrowBmps[0]!=null && !arrowBmps[0].isRecycled())
				arrow.setImageBitmap(arrowBmps[0]);
			aParam.addRule(RelativeLayout.RIGHT_OF, help.getId());
			aParam.topMargin = size.x * 1200/1080;
			aParam.leftMargin = 0;
			aParam.width = size.x * 98/1080;
			aParam.height = size.x * 356/1080;
			animation.start();
		}
		else if (state == 1){
			layout .setOnClickListener(new Listener(1));
			next.setOnClickListener(null);
			next.setVisibility(View.INVISIBLE);
			replay.setOnClickListener(null);
			replay.setVisibility(View.INVISIBLE);
			if (tabBmp!=null && !tabBmp.isRecycled())
				tab.setImageBitmap(tabBmp);
			tab.setVisibility(View.VISIBLE);
			if (arrowBmps!=null && arrowBmps[1]!=null && !arrowBmps[1].isRecycled())
				arrow.setImageBitmap(arrowBmps[1]);
			aParam.addRule(RelativeLayout.ABOVE, tab.getId());
			aParam.topMargin = 0;
			aParam.leftMargin = size.x * 140/1080;
			aParam.width = size.x * 93/1080;
			aParam.height = size.x * 410/1080;
			animation.start();
		}
		else if (state == 2){
			layout .setOnClickListener(new EndListener());
			next.setOnClickListener(new EndListener());
			if (nextBmp!=null && !nextBmp.isRecycled())
				next.setImageBitmap(nextBmp);
			next.setVisibility(View.VISIBLE);
			replay.setOnClickListener(new Listener(-1));
			if (replayBmp!=null && !replayBmp.isRecycled())
				replay.setImageBitmap(replayBmp);
			replay.setVisibility(View.VISIBLE);
			tab.setVisibility(View.INVISIBLE);
			if (arrowBmps!=null && arrowBmps[2]!=null && !arrowBmps[2].isRecycled())
				arrow.setImageBitmap(arrowBmps[2]);
			aParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			aParam.topMargin = size.x * 850/1080;
			aParam.leftMargin = 0;
			aParam.width = size.x * 393/1080;
			aParam.height = size.x * 389/1080;
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
			settingState(step+1);
		}
		
	}
	
	private class EndListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			arrow.setAnimation(null);
			animation.cancel();
			finish();
		}
	}
	
}
