package test.ui;

import test.gps.GPSInitTask;
import ioio.examples.hello.FragmentTabs;
import ioio.examples.hello.R;
import ioio.examples.hello.TestFragment;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class UIMsgBox {

	private TestFragment testFragment;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout GPSCheckBox = null;
	private RelativeLayout BTCheckBox = null;
	private RelativeLayout BTSuccessBox = null;
	private RelativeLayout BTFailBox = null;
	private RelativeLayout InitializingBox = null;
	
	
	private RelativeLayout Main = null;
	
	private final static int YES = 1, NO = 0;
	
	public UIMsgBox(TestFragment testFragment){
		this.testFragment = testFragment;
		this.context = testFragment.getActivity();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void generateGPSCheckBox(RelativeLayout mainLayout){
		if (GPSCheckBox!=null){
			GPSCheckBox.setVisibility(View.VISIBLE);
			return;
		}
		if (Main == null){
			Main = mainLayout;
		}
		GPSCheckBox = new RelativeLayout(context);
		GPSCheckBox.setBackgroundColor(0xFFAAAAAA);
		ImageView noButton,yesButton;
		TextView message;
		
		Point size = FragmentTabs.getSize();
		
		mainLayout.addView(GPSCheckBox);
		LayoutParams bgParam =(LayoutParams) GPSCheckBox.getLayoutParams();
		
		//Set View
		int bg_width,bg_height;
		bgParam.width = bg_width = (int) (size.x*0.8);
		bgParam.height = bg_height =  (int) (size.y*0.4);
		int bg_margin_left = (size.x - bg_width)/2;
		int bg_margin_top = (size.y - bg_height)/2;
		bgParam.leftMargin = bg_margin_left;
		bgParam.topMargin = bg_margin_top;
		
		GPSOnClickListener gpsListener = new GPSOnClickListener();
		
		//Set No Button
		noButton = new ImageView(context);
		noButton.setId(NO);
		noButton.setOnClickListener(gpsListener);
		noButton.setImageResource(R.drawable.apple_bad);
		GPSCheckBox.addView(noButton);
		LayoutParams noParam = (LayoutParams) noButton.getLayoutParams();
		noParam.width = bg_width/3;
		noParam.height = bg_height/3;
		noParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		noParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		noParam.leftMargin = bg_width/10;
		noParam.bottomMargin = bg_height/10;
		
		//Set Yes Button
		yesButton = new ImageView(context);
		yesButton.setId(YES);
		yesButton.setOnClickListener(gpsListener);
		yesButton.setImageResource(R.drawable.apple_good);
		GPSCheckBox.addView(yesButton);
		LayoutParams yesParam = (LayoutParams) yesButton.getLayoutParams();
		yesParam.width = bg_width/3;
		yesParam.height = bg_height/3;
		yesParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		yesParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		yesParam.rightMargin = bg_width/10;
		yesParam.bottomMargin = bg_height/10;
		
		//Set message
		message = new TextView(context);
		message.setText("Enable GPS?");
		message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
		message.setTextColor(0xFF000000);
		GPSCheckBox.addView(message);
		LayoutParams msgParam = (LayoutParams) message.getLayoutParams();
		msgParam.width = (int) (bg_width*0.8);
		msgParam.height = (int) (bg_height*0.8);
		msgParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		msgParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
		msgParam.topMargin=bg_height/10;
	}
	
	private class GPSOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			if (v.getId()==YES){
				testFragment.startGPS(true);
			}
			else{
				testFragment.startGPS(false);
			}
			GPSCheckBox.setVisibility(View.INVISIBLE);
		}
	}
	
	public void generateBTCheckBox(RelativeLayout mainLayout){
		if (BTCheckBox!=null){
			BTCheckBox.setVisibility(View.VISIBLE);
			return;
		}
		if (Main == null){
			Main = mainLayout;
		}
		BTCheckBox = new RelativeLayout(context);
		BTCheckBox.setBackgroundColor(0xFFFFAAAA);
		TextView message;
		Point size = FragmentTabs.getSize();
		
		mainLayout.addView(BTCheckBox);
		LayoutParams bgParam =(LayoutParams) BTCheckBox.getLayoutParams();
		
		//Set View
		int bg_width,bg_height;
		bgParam.width = bg_width = (int) (size.x*0.8);
		bgParam.height = bg_height =  (int) (size.y*0.4);
		int bg_margin_left = (size.x - bg_width)/2;
		int bg_margin_top = (size.y - bg_height)/2;
		bgParam.leftMargin = bg_margin_left;
		bgParam.topMargin = bg_margin_top;
		
		//Set message
		message = new TextView(context);
		message.setText("Please enable BT & Device");
		message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
		message.setTextColor(0xFF000000);
		message.setTextColor(0xFF000000);
		BTCheckBox.addView(message);
		LayoutParams msgParam = (LayoutParams) message.getLayoutParams();
		msgParam.width = (int) (bg_width*0.8);
		msgParam.height = (int) (bg_height*0.8);
		msgParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		msgParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
		msgParam.topMargin=bg_height/10;
		
		BTCheckBox.setOnClickListener(new BTOnClickListener());
		
	}
	
	private class BTOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			BTCheckBox.setVisibility(View.INVISIBLE);
			testFragment.startBT();
		}
	}
	
	
	public void generateBTSuccessBox(RelativeLayout mainLayout){
		if (BTSuccessBox!=null){
			BTSuccessBox.setVisibility(View.VISIBLE);
			return;
		}
		if (Main == null){
			Main = mainLayout;
		}
		
		BTSuccessBox = new RelativeLayout(context);
		BTSuccessBox.setBackgroundColor(0xFFFFFFAA);
		TextView message;
		Point size = FragmentTabs.getSize();
		
		mainLayout.addView(BTSuccessBox);
		LayoutParams bgParam =(LayoutParams) BTSuccessBox.getLayoutParams();
		
		//Set View
		int bg_width,bg_height;
		bgParam.width = bg_width = (int) (size.x*0.8);
		bgParam.height = bg_height =  (int) (size.y*0.4);
		int bg_margin_left = (size.x - bg_width)/2;
		int bg_margin_top = (size.y - bg_height)/2;
		bgParam.leftMargin = bg_margin_left;
		bgParam.topMargin = bg_margin_top;
		
		//Set message
		message = new TextView(context);
		message.setText("Press to start");
		message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
		message.setTextColor(0xFF000000);
		BTSuccessBox.addView(message);
		LayoutParams msgParam = (LayoutParams) message.getLayoutParams();
		msgParam.width = (int) (bg_width*0.8);
		msgParam.height = (int) (bg_height*0.8);
		msgParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		msgParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
	
		msgParam.topMargin=bg_height/10;
		
		BTSuccessBox.setOnClickListener(new BTSuccessOnClickListener());
	}
	
	private class BTSuccessOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			testFragment.runBT();
			BTSuccessBox.setVisibility(View.INVISIBLE);
		}
	}
	
	public void generateBTFailBox(RelativeLayout mainLayout){
		if (BTFailBox!=null){
			BTFailBox.setVisibility(View.VISIBLE);
			return;
		}
		if (Main == null){
			Main = mainLayout;
		}
		
		BTFailBox = new RelativeLayout(context);
		BTFailBox.setBackgroundColor(0xFFFFAAFF);
		TextView message;
		Point size = FragmentTabs.getSize();
		
		mainLayout.addView(BTFailBox);
		LayoutParams bgParam =(LayoutParams) BTFailBox.getLayoutParams();
		
		//Set View
		int bg_width,bg_height;
		bgParam.width = bg_width = (int) (size.x*0.8);
		bgParam.height = bg_height =  (int) (size.y*0.4);
		int bg_margin_left = (size.x - bg_width)/2;
		int bg_margin_top = (size.y - bg_height)/2;
		bgParam.leftMargin = bg_margin_left;
		bgParam.topMargin = bg_margin_top;
		
		//Set message
		message = new TextView(context);
		message.setText("BT INIT FAIL");
		message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
		message.setTextColor(0xFF000000);
		BTFailBox.addView(message);
		LayoutParams msgParam = (LayoutParams) message.getLayoutParams();
		msgParam.width = (int) (bg_width*0.8);
		msgParam.height = (int) (bg_height*0.8);
		msgParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		msgParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
	
		msgParam.topMargin=bg_height/10;
		
		BTFailBox.setOnClickListener(new BTFailOnClickListener());
	}

	private class BTFailOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			BTFailBox.setVisibility(View.INVISIBLE);
		}
	}
	
	public void generateInitializingBox(RelativeLayout mainLayout){
		if (InitializingBox!=null){
			InitializingBox.setVisibility(View.VISIBLE);
			return;
		}
		if (Main == null){
			Main = mainLayout;
		}
		
		InitializingBox = new RelativeLayout(context);
		InitializingBox.setBackgroundColor(0xFFAAAAFF);
		TextView message;
		Point size = FragmentTabs.getSize();
		
		mainLayout.addView(InitializingBox);
		LayoutParams bgParam =(LayoutParams) InitializingBox.getLayoutParams();
		
		//Set View
		int bg_width,bg_height;
		bgParam.width = bg_width = (int) (size.x*0.8);
		bgParam.height = bg_height =  (int) (size.y*0.4);
		int bg_margin_left = (size.x - bg_width)/2;
		int bg_margin_top = (size.y - bg_height)/2;
		bgParam.leftMargin = bg_margin_left;
		bgParam.topMargin = bg_margin_top;
		
		//Set message
		message = new TextView(context);
		message.setText("INITIALIZING");
		message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
		message.setTextColor(0xFF000000);
		InitializingBox.addView(message);
		LayoutParams msgParam = (LayoutParams) message.getLayoutParams();
		msgParam.width = (int) (bg_width*0.8);
		msgParam.height = (int) (bg_height*0.8);
		msgParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		msgParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
	
		msgParam.topMargin=bg_height/10;
		
	}
	
	public void closeInitializingBox(){
		if (InitializingBox!=null){
			InitializingBox.setVisibility(View.INVISIBLE);
			return;
		}
	}
	public void hideAll(){
		if (GPSCheckBox != null)
			GPSCheckBox.setVisibility(View.INVISIBLE);
		if (BTCheckBox != null)
			BTCheckBox.setVisibility(View.INVISIBLE);
		if (BTSuccessBox != null)
			BTSuccessBox.setVisibility(View.INVISIBLE);
		if (BTFailBox != null)
			BTFailBox.setVisibility(View.INVISIBLE);
	}
	
	public void deleteAll(){
		Log.d("MSGBOX","delete all");
		if (Main != null){
			if (GPSCheckBox != null){
				Main.removeView(GPSCheckBox);
				GPSCheckBox.destroyDrawingCache();
				GPSCheckBox = null;
			}
			if (BTCheckBox != null){
				Main.removeView(BTCheckBox);
				BTCheckBox.destroyDrawingCache();
				BTCheckBox = null;
			}
			if (BTSuccessBox != null){
				Main.removeView(BTSuccessBox);
				BTSuccessBox.destroyDrawingCache();
				BTSuccessBox = null;
			}
			if (BTFailBox != null){
				Main.removeView(BTFailBox);
				BTFailBox.destroyDrawingCache();
				BTFailBox = null;
			}
		}

	}
	
}
