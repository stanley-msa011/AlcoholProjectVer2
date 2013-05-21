package test.ui;

import main.activities.FragmentTabs;
import main.activities.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Tutorial {
	private Context context;
	private LayoutInflater inflater;
	private Resources r;
	private Point screen;
	private RelativeLayout view;
	private ImageView bg, device, arrow;
	private TextView step,msg;
	private Bitmap bgBmp,deviceBmp;//,arrowBmp;
	private int curPage;
	
	private AlphaAnimation arrowAnimation;
	
	private Typeface digitTypeface;
	private Typeface wordTypeface;
	
	public Tutorial(Fragment fragment){
		this.context = fragment.getActivity();
		this.r = context.getResources();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		screen = FragmentTabs.getSize();
		view = (RelativeLayout) inflater.inflate(R.layout.tutorial_layout, null);
		setting();
		view.setVisibility(View.INVISIBLE);
	}
	
	public View getView(){
		return view;
	}
	
	private void setting(){
		
		digitTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dinpromedium.ttf");
		wordTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dfheistd-w3.otf");
		
		curPage = 1;
		
		bg = (ImageView) view.findViewById(R.id.tutorial_bg);
		RelativeLayout.LayoutParams bgParam = (RelativeLayout.LayoutParams)bg.getLayoutParams();
		bgParam.width = screen.x;
		bgParam.height = (int) (screen.x*555.0/355.0);
		
		device = (ImageView) view.findViewById(R.id.tutorial_device);
		RelativeLayout.LayoutParams dParam = (RelativeLayout.LayoutParams)device.getLayoutParams();
		dParam.width = (int) (screen.x*499.0/720.0);
		dParam.height = (int) (screen.x*643.0/720.0);
		
		arrow = (ImageView) view.findViewById(R.id.tutorial_arrow);
		RelativeLayout.LayoutParams aParam = (RelativeLayout.LayoutParams)arrow.getLayoutParams();
		aParam.width = (int) (screen.x*78.0/720.0);
		aParam.height = (int) (screen.x*87.0/720.0);
		
		step = (TextView) view.findViewById(R.id.tutorial_step);
		step.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (screen.x*72.0/720.0));
		step.setTypeface(digitTypeface);
		RelativeLayout.LayoutParams sParam = (RelativeLayout.LayoutParams)step.getLayoutParams();
		sParam.leftMargin =  (int) (screen.x*100.0/720.0);
		sParam.topMargin =  (int) (screen.x*60.0/720.0);
		
		msg = (TextView) view.findViewById(R.id.tutorial_message);
		msg.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (screen.x*36.0/720.0));
		msg.setTypeface(wordTypeface);
		RelativeLayout.LayoutParams mParam = (RelativeLayout.LayoutParams)msg.getLayoutParams();
		mParam.leftMargin =  (int) (screen.x*150.0/720.0);
		mParam.topMargin =  (int) (screen.x*90.0/720.0);
		
		view.setOnClickListener(new StepListener());
		
		arrowAnimation = new AlphaAnimation(1.0F,0.0F);
		arrowAnimation.setDuration(200);
		arrowAnimation.setRepeatCount(Animation.INFINITE);
		arrowAnimation.setRepeatMode(Animation.REVERSE);
		arrow.setAnimation(arrowAnimation);
		arrowAnimation.cancel();
		
		bgBmp = null; 
		deviceBmp = null;
	}
	
	public void loading(){
		
		Bitmap tmp;
		Log.d("Tutorial","Loading");
		
		RelativeLayout.LayoutParams vParam = (RelativeLayout.LayoutParams)view.getLayoutParams();
		vParam.width = screen.x;
		vParam.height = (int) (screen.x*555.0/355.0);
		
		if (bgBmp ==null || bgBmp.isRecycled()){
			tmp = BitmapFactory.decodeResource(r, R.drawable.tutorial_background);
			bgBmp = Bitmap.createScaledBitmap(tmp, screen.x, (int) (screen.x*555.0/355.0), true);
			tmp.recycle();
			Log.d("Tutorial","load bgBmp");
		}
		
		if (deviceBmp == null || deviceBmp.isRecycled()){
			tmp = BitmapFactory.decodeResource(r, R.drawable.tutorial_device);
			deviceBmp = Bitmap.createScaledBitmap(tmp,  (int) (screen.x*499.0/720.0), (int) (screen.x*643.0/720.0), true);
			tmp.recycle();
			Log.d("Tutorial","load deviceBmp");
		}
		
	}
	
	public void setBmp(){
		if (bgBmp!=null && !bgBmp.isRecycled())
			bg.setImageBitmap(bgBmp);
		if (deviceBmp!=null && !deviceBmp.isRecycled())
			device.setImageBitmap(deviceBmp);
		else{
			Log.d("Tutorial","no arrowBmp");
		}
	}
	
	public void clear(){
		
		Log.d("Tutorial","clear");
		view.setVisibility(View.INVISIBLE);
		
		bg.setImageBitmap(null);
		device.setImageBitmap(null);
		arrow.setImageBitmap(null);
		
		arrowAnimation.cancel();
		
		if (bgBmp!=null && !bgBmp.isRecycled()){
			bgBmp.recycle();
			bgBmp = null;
		}
		if (deviceBmp!=null && !deviceBmp.isRecycled()){
			deviceBmp.recycle();
			deviceBmp = null;
		}
	}
	
	public void setTutorial(int step_num){
		
		FragmentTabs.enableTab(false);
		RelativeLayout.LayoutParams aParam = (RelativeLayout.LayoutParams)arrow.getLayoutParams();
		msg.setTextDirection(TextView.TEXT_DIRECTION_LTR);
		switch (step_num){
		case 1:
			curPage = 1;
			step.setText("1");
			msg.setText("按下開關，使指示燈亮起");
			
			aParam.leftMargin =(int) (screen.x*620.0/720.0);
			aParam.topMargin =(int) (screen.x*277.0/355.0) +(int) (screen.x*135.0/720.0) ;
			arrow.setRotation(-90);
			arrow.setImageResource(R.drawable.tutorial_arrow);
			arrowAnimation.start();
			break;
		case 2:
			curPage = 2;
			step.setText("2");
			msg.setText("進入測試頁面按下開始按鈕");
			aParam.leftMargin =(int) (screen.x*94.0/720.0);
			aParam.topMargin =(int) (screen.x*555.0/355.0) - (int) (screen.x*90.0/720.0);
			arrow.setRotation(180);
			arrow.setImageResource(R.drawable.tutorial_arrow);
			arrowAnimation.start();
			break;
		case 3:
			curPage = 3;
			step.setText("3");
			msg.setText("對準吹氣口持續吹氣五秒鐘");
			aParam.leftMargin =(int) (screen.x*314.0/720.0);
			aParam.topMargin =(int) (screen.x*267.0/355.0) ;
			arrow.setRotation(0);
			arrow.setImageResource(R.drawable.tutorial_arrow);
			arrowAnimation.start();
			break;
		default:
			setTutorial(1);
		}
	}
	
	public class StepListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			if (curPage==1)
				setTutorial(2);
			else if (curPage == 2 )
				setTutorial(3);
			else if (curPage == 3){
				FragmentTabs.enableTab(true);
				clear();
			}
		}
		
	}
}
