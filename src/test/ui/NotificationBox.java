package test.ui;

import java.util.Random;

import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;

import ubicomp.drunk_detection.activities.EmotionActivity;
import ubicomp.drunk_detection.activities.EmotionManageActivity;
import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.ui.ScreenSize;
import ubicomp.drunk_detection.ui.Typefaces;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class NotificationBox {

	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout mainLayout, selectLayout;
	private Point screen;
	
	private Typeface wordTypeface;
	private Typeface wordTypefaceBold;
	
	private LinearLayout boxLayout;
	private View divider;
	private TextView text, yes, no;
	private String[] message;
	private int textSize, textSizeLarge, textSizeXLarge;
	
	public static final int TYPE_EMOTION_DIY = 0;
	public static final int TYPE_EMOTION_MANAGEMENT = 1;
	public static final int TYPE_STORYTELLING_SHARING = 2;
	public static final int TYPE_STORYTELLING_RECORDING = 3;
	
	public NotificationBox(Context context, RelativeLayout mainLayout){
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		screen = ScreenSize.getScreenSize(context);
		wordTypeface = Typefaces.getWordTypeface(context);
		wordTypefaceBold = Typefaces.getWordTypefaceBold(context);
		message = context.getResources().getStringArray(R.array.notification_box_message);
		setting();
	}
	
	private void setting(){
		boxLayout = (LinearLayout) inflater.inflate(R.layout.notification_box_layout, null);
		text = (TextView) boxLayout.findViewById(R.id.notification_message);
		yes = (TextView) boxLayout.findViewById(R.id.notification_enter);
		no = (TextView) boxLayout.findViewById(R.id.notification_cancel);
		divider = boxLayout.findViewById(R.id.notification_divider);
		selectLayout = (RelativeLayout) boxLayout.findViewById(R.id.notification_select_layout);
		
		textSize = screen.x * 24/480;
		textSizeLarge = screen.x * 32/480;
		textSizeXLarge = screen.x * 48/480;
		
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		text.setTypeface(wordTypeface);
		LinearLayout.LayoutParams tParam = (LinearLayout.LayoutParams)text.getLayoutParams();
		tParam.topMargin = screen.x*20/480;
		tParam.bottomMargin = screen.x * 20/480;
		tParam.leftMargin = tParam.rightMargin = screen.x * 30/480;
		
		yes.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeLarge);
		yes.setTypeface(wordTypefaceBold);
		no.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeLarge);
		no.setTypeface(wordTypefaceBold);
		
		LinearLayout.LayoutParams sParam = (LinearLayout.LayoutParams)selectLayout.getLayoutParams();
		sParam.bottomMargin = screen.y * 15/480;
		
		RelativeLayout.LayoutParams divParam = (LayoutParams) divider.getLayoutParams();
		divParam.width = screen.x * 25/480;
		divParam.height = screen.x * 48/480;
		
		RelativeLayout.LayoutParams yesParam = (LayoutParams) yes.getLayoutParams();
		yesParam.width = screen.x * 110/480;
		yesParam.height = screen.x * 48/480;
		
		RelativeLayout.LayoutParams noParam = (LayoutParams) no.getLayoutParams();
		noParam.width = screen.x * 110/480;
		noParam.height = screen.x * 48/480;
		
		no.setOnClickListener(new CancelOnClickListener());
		
		yes.setOnTouchListener(new SelectOnTouchListener());
		yes.setOnClickListener(null);
		no.setOnTouchListener(new SelectOnTouchListener());
	}
	
	public void show(int type){
		if (type == -1)
			return;
		
		if (mainLayout!=null){
			if (boxLayout.getParent() == null){
				mainLayout.addView(boxLayout);
				RelativeLayout.LayoutParams param = (LayoutParams) boxLayout.getLayoutParams();
				param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				param.bottomMargin = screen.x * 260/1080;
				param.addRule(RelativeLayout.CENTER_HORIZONTAL);
				param.width = screen.x * 320/480;
			}
		}
		
		switch(type){
		case TYPE_EMOTION_DIY:
			text.setText(message[type]);
			yes.setOnClickListener(new TypeEmotionDIYOnClickListener());
			break;
		case TYPE_EMOTION_MANAGEMENT:
			text.setText(message[type]);
			yes.setOnClickListener(new TypeEmotionManageOnClickListener());
			break;
		case TYPE_STORYTELLING_SHARING:
			text.setText(message[type]);
			yes.setOnClickListener(new TypeStorytellingSharingOnClickListener());
			break;
		case TYPE_STORYTELLING_RECORDING:
			text.setText(message[type]);
			yes.setOnClickListener(new TypeStorytellingRecordingOnClickListener());
			break;
		}
		
	}
	
	private class SelectOnTouchListener implements View.OnTouchListener{
		private Rect rect;
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int e = event.getAction();
			TextView tv =(TextView)v;
			switch(e){
				case MotionEvent.ACTION_MOVE:
					if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY()))
						tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeLarge);
					break;
				case MotionEvent.ACTION_UP:
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeLarge);
					break;
				case MotionEvent.ACTION_DOWN:
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeXLarge);
					rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
					break;
			}
			return false;
		}
	}
	
	private class TypeEmotionDIYOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLogger.Log(context, ClickLogId.TEST_NOTIFICATION_OK+10);
			Intent intent = new Intent(context,EmotionActivity.class);
			context.startActivity(intent);
		}
	}
	
	private class TypeEmotionManageOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLogger.Log(context, ClickLogId.TEST_NOTIFICATION_OK+20);
			Intent intent = new Intent(context,EmotionManageActivity.class);
			context.startActivity(intent);
		}
	}
	
	private class TypeStorytellingSharingOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLogger.Log(context, ClickLogId.TEST_NOTIFICATION_OK+30);
			FragmentTabs.changeTab(2,2);			
		}
	}
	
	private class TypeStorytellingRecordingOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLogger.Log(context, ClickLogId.TEST_NOTIFICATION_OK+40);
			FragmentTabs.changeTab(2,3);			
		}
	}
	
	private class CancelOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLogger.Log(context, ClickLogId.TEST_NOTIFICATION_CANCEL);
			dismiss();
		}
	}
	
	public void dismiss(){
		if (boxLayout!=null && mainLayout != null){
			if (boxLayout.getParent()!=null && boxLayout.getParent().equals(mainLayout))
				mainLayout.removeView(boxLayout);
		}
	}
	
	private static final long TWO_DAYS = AlarmManager.INTERVAL_DAY * 2;
	
	public static int getType(Context context){
		if (context == null)
			return -1;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		long t0 = sp.getLong("LatestEmotionDIYTime", 0L);
		long t1 = sp.getLong("LatestEmotionManageTime", 0L);
		long t2 = sp.getLong("LatestStorytellingSharingTime", 0L);
		long t3 = sp.getLong("LatestStorytellingRecordingTime", 0L);
		
		Log.d("Notify",t0+" "+t1+" "+t2+" "+t3);
		
		long cur_time = System.currentTimeMillis();
		
		boolean[] b = new boolean[4];
		b[0] = (cur_time - t0 > TWO_DAYS)?true:false;
		b[1] = (cur_time - t1 > TWO_DAYS)?true:false;
		b[2] = (cur_time - t2 > TWO_DAYS)?true:false;
		b[3] = (cur_time - t3 > TWO_DAYS)?true:false;

		Log.d("Notify",">"+(cur_time - t0)+"/"+(cur_time - t1)+"/"+(cur_time - t2)+"/"+(cur_time - t3)+"/"+TWO_DAYS);
		Log.d("Notify",">"+(b[0])+"/"+(b[1])+"/"+(b[2])+"/"+(b[3]));
		int mod = 0;
		for (int i=0;i<b.length;++i){
			if (b[i])++mod;
		}
		
		if (mod == 0)
			return -1;
		
		Random rand = new Random();
		int t = rand.nextInt(mod);
		int type = -1;
		for (int i=0;i<b.length;++i){
			if (t == 0){
				type = i;
				break;
			}
			else if (b[i])
				--t;
		}

		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("LatestEmotionDIYTime", cur_time);
		edit.putLong("LatestEmotionManageTime", cur_time);
		edit.putLong("LatestStorytellingSharingTime", cur_time);
		edit.putLong("LatestStorytellingRecordingTime", cur_time);
		edit.commit();
		
		Log.d("Notify","Commit");
		return type;
	}
	
}
