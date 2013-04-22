package test.ui;

import main.activities.FragmentTabs;
import main.activities.R;
import main.activities.TestFragment;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class UIMsgBox {

	private TestFragment testFragment;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout box = null;
	
	private Bitmap[] bgBmps;
	private Bitmap buttonBmp;
	
	private TextView help,yes,no;
	private ImageView bg;
	private ImageView yesBg,noBg;
	
	private RelativeLayout mainLayout = null;
	
	
	private Resources r;
	private Point screen;
	
	private GPSOnClickListener gpsListener;
	private BTOnClickListener btListener;
	private BTSuccessOnClickListener btSuccessListener;
	
	private TimeUpHandler timeUpHandler;
	
	public UIMsgBox(TestFragment testFragment,RelativeLayout mainLayout){
		Log.d("UIMSG","NEW");
		this.testFragment = testFragment;
		this.context = testFragment.getActivity();
		this.r = context.getResources();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		screen = FragmentTabs.getSize();
		timeUpHandler = new TimeUpHandler();
		setting();
	}
	
	private void setting(){
		gpsListener  = new GPSOnClickListener();
		btListener = new BTOnClickListener();
		btSuccessListener = new BTSuccessOnClickListener();
		
		box = (RelativeLayout) inflater.inflate(R.layout.test_msg_box,null);
		
		box.setVisibility(View.INVISIBLE);
		bg = (ImageView) box.findViewById(R.id.test_msg_box_bg);
		
		int textSize = (int)(screen.x * 56.0/720.0);
		
		help = (TextView) box.findViewById(R.id.test_msg_box_help);
		
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		
		yesBg =  (ImageView) box.findViewById(R.id.test_msg_box_o_bg);
		noBg =  (ImageView) box.findViewById(R.id.test_msg_box_x_bg);
		
		yes = (TextView) box.findViewById(R.id.test_msg_box_o);
		no = (TextView) box.findViewById(R.id.test_msg_box_x);
		Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/helvetica-lt-std-bold.otf");
		yes.setTypeface(face);
		no.setTypeface(face);
		yes.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize );
		no.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize );
	}
	
	public void settingPreTask(){
		mainLayout.addView(box);
	}
	
	
	public void settingInBackground(){
		
		if (bgBmps!=null){
			for (int i=0;i<bgBmps.length;++i){
				if(bgBmps[i]!=null && !bgBmps[i].isRecycled()){
					bgBmps[i].recycle();
					bgBmps[i]=null;
				}
			}
			bgBmps = null;
		}
		if(buttonBmp!=null && !buttonBmp.isRecycled()){
			buttonBmp.recycle();
			buttonBmp=null;
		}
		bgBmps = new Bitmap[2];
		bgBmps[0] = BitmapFactory.decodeResource(r, R.drawable.test_box_bg_1);
		bgBmps[1] = BitmapFactory.decodeResource(r, R.drawable.test_box_bg_2);
		buttonBmp = BitmapFactory.decodeResource(r, R.drawable.test_box_button);
		
		
		RelativeLayout.LayoutParams boxParam = (LayoutParams) box.getLayoutParams();
		boxParam.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
		boxParam.topMargin = (int)(screen.x * 372.0/720.0);
		
		RelativeLayout.LayoutParams bgParam = (LayoutParams) bg.getLayoutParams();
		bgParam.width = (int)(screen.x * 666.0/720.0);
		bgParam.height = (int)(screen.x * 440.0/720.0);
		
		int buttonWidth =  (int)(screen.x * 250.0/720.0);
		int buttonHeight =  (int)(screen.x * 91.0/720.0);
		int buttonHorizonMargin =(int)(screen.x * 83.0/720.0);
		int buttonTopMargin =(int)(screen.x * 255.0/720.0);
		
		RelativeLayout.LayoutParams yesBgParam = (LayoutParams) yesBg.getLayoutParams();
		yesBgParam.width = buttonWidth;
		yesBgParam.height = buttonHeight;
		yesBgParam.rightMargin = buttonHorizonMargin;
		yesBgParam.topMargin = buttonTopMargin;
		
		RelativeLayout.LayoutParams noBgParam = (LayoutParams) noBg.getLayoutParams();
		noBgParam.width = buttonWidth;
		noBgParam.height = buttonHeight;
		noBgParam.leftMargin = buttonHorizonMargin;
		noBgParam.topMargin = buttonTopMargin;
		
		int ansHorizonMargin =(int)(screen.x * 192.0/720.0);
		int ansTopMargin =(int)(screen.x * 262.0/720.0);
		
		
		RelativeLayout.LayoutParams yesParam = (LayoutParams) yes.getLayoutParams();
		yesParam.rightMargin = ansHorizonMargin;
		yesParam.topMargin = ansTopMargin;
		
		RelativeLayout.LayoutParams noParam = (LayoutParams) no.getLayoutParams();
		noParam.leftMargin = ansHorizonMargin - (int)(screen.x * 10.0/720.0);
		noParam.topMargin = ansTopMargin;
	}
	
	public  void settingPostTask(){
		yesBg.setImageBitmap(buttonBmp);
		noBg.setImageBitmap(buttonBmp);
	}
	
	public void clear(){
		Log.d("UIMSG","CLEAR");
		if (timeUpHandler!=null){
			timeUpHandler.removeMessages(0);
			timeUpHandler.removeMessages(1);
		}
		mainLayout.removeView(box);
		if (bgBmps!=null){
			for (int i=0;i<bgBmps.length;++i){
				if(bgBmps[i]!=null && !bgBmps[i].isRecycled()){
					Log.d("UIMSG","recycle bgBmps");
					bgBmps[i].recycle();
					bgBmps[i]=null;
				}
			}
			bgBmps = null;
		}
		if(buttonBmp!=null && !buttonBmp.isRecycled()){
			buttonBmp.recycle();
			buttonBmp=null;
		}
	}
	
	public void generateGPSCheckBox(){
		if (bgBmps == null)
			return;
		if (bg==null || help == null || yes == null || no == null || yesBg ==null || noBg == null)
			return;
		
		bg.setImageBitmap(bgBmps[0]);
		help.setText("是否回報現在位置?");
		yes.setVisibility(View.VISIBLE);
		no.setVisibility(View.VISIBLE);
		yesBg.setVisibility(View.VISIBLE);
		noBg.setVisibility(View.VISIBLE);
		
		RelativeLayout.LayoutParams helpParam = (LayoutParams) help.getLayoutParams();
		helpParam.topMargin = (int)(screen.x * 140.0/720.0);
		
		box.setOnClickListener(null);
		yesBg.setOnClickListener(gpsListener);
		noBg.setOnClickListener(gpsListener);
		
		box.setVisibility(View.VISIBLE);
	}
	
	private class GPSOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			box.setVisibility(View.INVISIBLE);
			if (v.getId()==R.id.test_msg_box_o_bg){
				testFragment.startGPS(true);
			}
			else{
				testFragment.startGPS(false);
			}
		}
	}
	
	public void generateBTCheckBox(){
		if (bgBmps == null)
			return;
		if (bg==null || help == null || yes == null || no == null || yesBg ==null || noBg == null)
			return;
		
		bg.setImageBitmap(bgBmps[0]);
		help.setText("請啟用\n酒測裝置及藍芽功能");
		yes.setVisibility(View.INVISIBLE);
		no.setVisibility(View.INVISIBLE);
		yesBg.setVisibility(View.INVISIBLE);
		noBg.setVisibility(View.INVISIBLE);
		
		RelativeLayout.LayoutParams helpParam = (LayoutParams) help.getLayoutParams();
		helpParam.topMargin = (int)(screen.x * 140.0/720.0);
		
		box.setOnClickListener(null);
		yesBg.setOnClickListener(null);
		noBg.setOnClickListener(null);
		
		box.setVisibility(View.VISIBLE);
		
		Runnable r = new Runnable(){
			@Override
			public void run() {
				try {
					Thread.sleep(2500);
					timeUpHandler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
				}
			}
		};
		Thread t = new Thread(r);
		t.start();
	}
	
	private class BTOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			box.setVisibility(View.INVISIBLE);
			testFragment.startBT();
		}
	}
	
	
	public void generateBTSuccessBox(){
		if (bgBmps == null)
			return;
		if (bg==null || help == null || yes == null || no == null || yesBg ==null || noBg == null)
			return;
		
		bg.setImageBitmap(bgBmps[1]);
		help.setText("已啟用  \n酒測裝置及藍芽功能");
		yes.setVisibility(View.INVISIBLE);
		no.setVisibility(View.INVISIBLE);
		yesBg.setVisibility(View.INVISIBLE);
		noBg.setVisibility(View.INVISIBLE);
		
		RelativeLayout.LayoutParams helpParam = (LayoutParams) help.getLayoutParams();
		helpParam.topMargin = (int)(screen.x * 140.0/720.0);
		
		box.setOnClickListener(null); 
		yesBg.setOnClickListener(null);
		noBg.setOnClickListener(null);
		
		box.setVisibility(View.VISIBLE);
		
		timeUpHandler.removeMessages(0);
		Runnable r = new Runnable(){
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
					timeUpHandler.sendEmptyMessage(1);
				} catch (InterruptedException e) {
				}
			}
		};
		Thread t = new Thread(r);
		t.start();
	}
	
	private class BTSuccessOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			box.setVisibility(View.INVISIBLE);
			testFragment.runBT();
		}
	}
	
	
	public void generateInitializingBox(){
		if (bgBmps == null)
			return;
		if (bg==null || help == null || yes == null || no == null || yesBg ==null || noBg == null)
			return;
		bg.setImageBitmap(bgBmps[0]);
		help.setText("請稍待");
		yes.setVisibility(View.INVISIBLE);
		no.setVisibility(View.INVISIBLE);
		yesBg.setVisibility(View.INVISIBLE);
		noBg.setVisibility(View.INVISIBLE);
		
		RelativeLayout.LayoutParams helpParam = (LayoutParams) help.getLayoutParams();
		helpParam.topMargin = (int)(screen.x * 140.0/720.0);
		
		box.setOnClickListener(null);
		yesBg.setOnClickListener(null);
		noBg.setOnClickListener(null);
		
		box.setVisibility(View.VISIBLE);
	}
	
	public void closeInitializingBox(){
			box.setVisibility(View.INVISIBLE);
			return;
	}
	
	@SuppressLint("HandlerLeak")
	private class TimeUpHandler extends Handler{
		public void handleMessage(Message msg){
			int t = msg.what;
			box.setVisibility(View.INVISIBLE);
			if (t == 0)
				testFragment.startBT();
			else
				testFragment.runBT();
		}
	}
	
}
