package test.ui;

import main.activities.FragmentTabs;
import main.activities.Lang;
import main.activities.R;
import main.activities.TestFragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class UIRotate {

	private Context context;
	private LayoutInflater inflater;
	
	private Bitmap bgBmp;
	private Bitmap ringBmp;
	
	private TextView help;
	private ImageView bg;
	private ImageView ring;
	
	private Resources r;
	private Point screen;

	private View view;

	private RelativeLayout mainLayout;
	
	private RotateAnimation anim;  
	
	private static final String[] MSG = {"開始\n吹氣","加油\n1","加油\n2","加油\n3","加油\n4","加油\n5"," 完成"};
	
	
	public UIRotate(TestFragment testFragment,RelativeLayout mainLayout){
		this.context = testFragment.getActivity();
		this.r = context.getResources();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		screen = FragmentTabs.getSize();
		this.mainLayout = mainLayout;
		setting();
	}
	
	
	private void setting(){
		view = (RelativeLayout) inflater.inflate(R.layout.test_rotate,null);
		help = (TextView) view.findViewById(R.id.test_rotate_help);
		ring = (ImageView) view.findViewById(R.id.test_rotate_ring);
		bg = (ImageView) view.findViewById(R.id.test_rotate_bg);
		
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(screen.x * 56.0/720.0));
		Typeface face=Typeface.createFromAsset(context.getAssets(),"fonts/helvetica-lt-std-bold.otf");
    	help.setTypeface(face);
		
		RelativeLayout.LayoutParams helpParam = (LayoutParams) help.getLayoutParams();
		helpParam.leftMargin = (int)(screen.x * 120.0/720.0);
		
		
		RelativeLayout.LayoutParams bgParam = (LayoutParams) bg.getLayoutParams();
		bgParam.width = (int)(screen.x * 369.0/720.0);
		bgParam.height = (int)(screen.x * 367.0/720.0);
		
		
		RelativeLayout.LayoutParams ringParam = (LayoutParams) ring.getLayoutParams();
		ringParam.width = (int)(screen.x * 266.0/720.0);
		ringParam.height = (int)(screen.x * 266.0/720.0);
		ringParam.leftMargin =  (int)(screen.x * 54.0/720.0);
		ringParam.topMargin =  (int)(screen.x * 43.0/720.0);
		
		anim = new RotateAnimation(0F,+360F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setRepeatCount(100);
		anim.setRepeatMode(Animation.RESTART);
	}
	
	public void settingPreTask(){
		anim.setDuration(0);
		mainLayout.addView(view);
		RelativeLayout.LayoutParams viewParam = (LayoutParams) view.getLayoutParams();
		viewParam.width = (int)(screen.x * 369.0/720.0);
		viewParam.height = (int)(screen.x * 367.0/720.0);
		viewParam.leftMargin = (int)(screen.x * 357.0/720.0);
		viewParam.topMargin = (int)(screen.x * 70.0/720.0);
	}
	
	public void settingInBackground(){
		
		Bitmap tmp;
		tmp = BitmapFactory.decodeResource(r, R.drawable.teat_camera_rotate_bg);
		bgBmp = Bitmap.createScaledBitmap(tmp,  (int)(screen.x * 369.0/720.0), (int)(screen.x * 367.0/720.0), true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(r, R.drawable.test_camera_rotate);
		ringBmp =  Bitmap.createScaledBitmap(tmp,   (int)(screen.x * 266.0/720.0), (int)(screen.x * 266.0/720.0), true);
		tmp.recycle();
		//bgBmp = BitmapFactory.decodeResource(r, R.drawable.teat_camera_rotate_bg,opts);
		//ringBmp = BitmapFactory.decodeResource(r, R.drawable.test_camera_rotate,opts);
		
	}
	
	public void settingPostTask(){
		ring.setImageBitmap(ringBmp);
		bg.setImageBitmap(bgBmp);
		setText(0);
		ring.setAnimation(anim);
		anim.start();
		anim.setDuration(99999);
	}
	
	public void clear(){
		mainLayout.removeView(view);
		if (bgBmp!=null && !bgBmp.isRecycled()){
			bgBmp.recycle();
			bgBmp = null;
		}
		if (ringBmp!=null && !ringBmp.isRecycled()){
			ringBmp.recycle();
			ringBmp = null;
		}
	}
	
	public void setSpeed(int modify){
		
		if (modify == 0){//start
			anim.setDuration(1000);
		}
		if (modify == -1){
			anim.setDuration(Long.MAX_VALUE);
		}
	}
	
	public void setText(int i){
		if (Lang.eng && i==0){
			help.setText("Start");
			RelativeLayout.LayoutParams helpParam = (LayoutParams) help.getLayoutParams();
			helpParam.addRule(RelativeLayout.CENTER_IN_PARENT);
		}
		else
		help.setText(MSG[i]);
	}
}
