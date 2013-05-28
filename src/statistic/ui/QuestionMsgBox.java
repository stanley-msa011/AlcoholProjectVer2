package statistic.ui;

import main.activities.AlarmReceiver;
import main.activities.FragmentTabs;
import main.activities.R;
import main.activities.StatisticFragment;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class QuestionMsgBox {

	private StatisticFragment statisticFragment;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	
	private RelativeLayout mainLayout;
	
	private LinearLayout questionLayout;
	
	private ImageView exitView;
	
	private Resources r;
	private Point screen;
	
	private ItemOnTouchListener onTouchListener;
	
	//private Typeface digitTypeface;
	private Typeface wordTypeface;
	
	private int textSize;
	private int iconSize;
	private int margin;
	
	private static final int ICON_NUM = 6;
	private static final int[] ICON_DRAWABLE_ID = {
		R.drawable.question_icon_helper,
		R.drawable.question_icon_neighbor,
		R.drawable.question_icon_mind,
		R.drawable.question_icon_hospital,
		R.drawable.question_icon_self,
		R.drawable.question_icon_home
	};
	private static final String[] ICON_TEXT = {
		"尋求親友協助",
		"社區心理諮商",
		"心情專線",
		"就醫",
		"自行處理",
		"回家休息"
	};
	
	private Bitmap[] iconBmps;
	
	private static final int ICON2_NUM = 5;
	private static final int[] ICON2_DRAWABLE_ID = {
		R.drawable.question_icon2_alcohol,
		R.drawable.question_icon2_money,
		R.drawable.question_icon2_boring,
		R.drawable.question_icon2_stress,
		R.drawable.question_icon2_sick
	};
	private static final String[] ICON2_TEXT = {
		"身邊有酒",
		"手頭寬裕",
		"無聊",
		"壓力",
		"身體不適"
	};
	
	private Bitmap[] icon2Bmps;
	
	private static final int ICON3_NUM = 5;
	private static final int[] ICON3_DRAWABLE_ID = {
		R.drawable.question_icon3_exit,
		R.drawable.question_icon3_notice,
		R.drawable.question_icon3_jogging,
		R.drawable.question_icon3_breath,
		R.drawable.question_icon3_sleep
	};
	private static final String[] ICON3_TEXT = {
		"離開有酒的場所",
		"請少喝酒",
		"請專心健走30分鐘",
		"請數息15次",
		"請休息或就診"
	};
	
	private Bitmap[] icon3Bmps;
	
	private static final int ICON4_NUM = 3;
	private static final int[] ICON4_DRAWABLE_ID = {
		R.drawable.question_done,
		R.drawable.question_icon_clock,
		R.drawable.question_done
	};
	private static final String[] ICON4_TEXT = {
		"完成",
		"啟用",
		"不啟用"
	};
	
	private Bitmap[] icon4Bmps;
	
	private Bitmap iconDoneBmp;
	
	public QuestionMsgBox(StatisticFragment statisticFragment,RelativeLayout mainLayout){
		Log.d("UIMSG","NEW");
		this.statisticFragment = statisticFragment;
		this.context = statisticFragment.getActivity();
		this.r = context.getResources();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		screen = FragmentTabs.getSize();
		setting();
	}
	
	private void setting(){
		
		//digitTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dinproregular.ttf");
		wordTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dfheistd-w3.otf");
		
		onTouchListener = new ItemOnTouchListener();
		
		boxLayout = (RelativeLayout) inflater.inflate(R.layout.question_box_layout,null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		questionLayout = (LinearLayout) boxLayout.findViewById(R.id.question_layout);
		exitView = (ImageView) boxLayout.findViewById(R.id.question_exit);
		RelativeLayout.LayoutParams exitParam = (RelativeLayout.LayoutParams) exitView.getLayoutParams();
		exitParam.width = exitParam.height =  (int)(screen.x * 90.0/720.0);
		exitView.setOnClickListener(new ExitOnClickListener());
		
		textSize = (int)(screen.x * 42.0/720.0);
		iconSize = (int)(screen.x * 90.0/720.0);
		margin = (int)(screen.x * 20.0/720.0);
		
	}
	
	public void settingPreTask(){
		mainLayout.addView(boxLayout);
	}
	
	
	public void settingInBackground(){
		
		Point screen = FragmentTabs.getSize();
		
		RelativeLayout.LayoutParams boxParam = (LayoutParams) boxLayout.getLayoutParams();
		boxParam.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
		boxParam.width = (int)(screen.x*600.0/720.0);
		
		iconBmps = new Bitmap[ICON_NUM];
		icon2Bmps = new Bitmap[ICON2_NUM];
		icon3Bmps = new Bitmap[ICON3_NUM];
		icon4Bmps = new Bitmap[ICON4_NUM];
		
		Bitmap tmp;
		for (int i=0;i<ICON_NUM;++i){
			tmp = BitmapFactory.decodeResource(r, ICON_DRAWABLE_ID[i]);
			iconBmps[i] = Bitmap.createScaledBitmap(tmp, iconSize, iconSize, true);
			tmp.recycle();
		}
		for (int i=0;i<ICON2_NUM;++i){
			tmp = BitmapFactory.decodeResource(r, ICON2_DRAWABLE_ID[i]);
			icon2Bmps[i] = Bitmap.createScaledBitmap(tmp, iconSize, iconSize, true);
			tmp.recycle();
		}
		for (int i=0;i<ICON3_NUM;++i){
			tmp = BitmapFactory.decodeResource(r, ICON3_DRAWABLE_ID[i]);
			icon3Bmps[i] = Bitmap.createScaledBitmap(tmp, iconSize, iconSize, true);
			tmp.recycle();
		}
		for (int i=0;i<ICON4_NUM;++i){
			tmp = BitmapFactory.decodeResource(r, ICON4_DRAWABLE_ID[i]);
			icon4Bmps[i] = Bitmap.createScaledBitmap(tmp, iconSize, iconSize, true);
			tmp.recycle();
		}
		
		tmp = BitmapFactory.decodeResource(r, R.drawable.question_done);
		iconDoneBmp = Bitmap.createScaledBitmap(tmp, iconSize, iconSize, true);
		tmp.recycle();
		
	}
	
	public  void settingPostTask(){
	}
	
	public void clear(){
		Log.d("UIMSG","CLEAR");
		mainLayout.removeView(boxLayout);
		if (iconBmps!=null){
			for (int i=0;i<iconBmps.length;++i){
				if (iconBmps[i]!=null && ! iconBmps[i].isRecycled()){
					iconBmps[i].recycle();
					iconBmps[i] = null;
				}
			}
			iconBmps = null;
		}
		if (icon2Bmps!=null){
			for (int i=0;i<icon2Bmps.length;++i){
				if (icon2Bmps[i]!=null && ! icon2Bmps[i].isRecycled()){
					icon2Bmps[i].recycle();
					icon2Bmps[i] = null;
				}
			}
			icon2Bmps = null;
		}
		if (icon3Bmps!=null){
			for (int i=0;i<icon3Bmps.length;++i){
				if (icon3Bmps[i]!=null && ! icon3Bmps[i].isRecycled()){
					icon3Bmps[i].recycle();
					icon3Bmps[i] = null;
				}
			}
			icon3Bmps = null;
		}
		
		if (icon4Bmps!=null){
			for (int i=0;i<icon4Bmps.length;++i){
				if (icon4Bmps[i]!=null && ! icon4Bmps[i].isRecycled()){
					icon4Bmps[i].recycle();
					icon4Bmps[i] = null;
				}
			}
			icon4Bmps = null;
		}
		
		if (iconDoneBmp !=null && !iconDoneBmp.isRecycled()){
			iconDoneBmp.recycle();
			iconDoneBmp = null;
		}
	}
	
	public void generateType0Box(){
		
		questionLayout.removeAllViews();
		
		TextView help = new TextView(context);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		help.setTypeface(wordTypeface);
		help.setTextColor(0xFF000000);
		help.setText("為了家人" +
				"\n請繼續加油"
				);
		
		questionLayout.addView(help);

		
		View view = createIconView(ICON4_TEXT[0], icon4Bmps[0],new EndOnClickListener());
		questionLayout.addView(view);
		
		for (int i=0;i<questionLayout.getChildCount();++i){
			View v = questionLayout.getChildAt(i);
			LinearLayout.LayoutParams param =(LinearLayout.LayoutParams) v.getLayoutParams();
			param.topMargin = param.bottomMargin = margin;
		}
		
		
		boxLayout.setVisibility(View.VISIBLE);
		
	}
	
	
	public void generateType1Box(){
		
		questionLayout.removeAllViews();
		
		TextView help = new TextView(context);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		help.setTypeface(wordTypeface);
		help.setTextColor(0xFF000000);
		help.setText("您似乎想要喝酒，" +
				"\n請問您要?");
		
		questionLayout.addView(help);
		
		int[] item = {0,2,1};
		
		OnClickListener[] listener = new OnClickListener[item.length];
		listener[0] = new HelpOnClickListener(-1);
		listener[1] = new EndOnClickListener();
		listener[2] = new HelpOnClickListener(-1);
		
		for (int i=0;i<item.length;++i){
			questionLayout.addView(createIconView(ICON_TEXT[item[i]],iconBmps[item[i]],listener[i]));
		}
		for (int i=0;i<questionLayout.getChildCount();++i){
			View v = questionLayout.getChildAt(i);
			LinearLayout.LayoutParams param =(LinearLayout.LayoutParams) v.getLayoutParams();
			param.topMargin = param.bottomMargin = margin;
		}
		
		boxLayout.setVisibility(View.VISIBLE);
		
	}
	

	
	public void generateType2Box(){
		
		questionLayout.removeAllViews();
		
		TextView help = new TextView(context);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		help.setTypeface(wordTypeface);
		help.setTextColor(0xFF000000);
		help.setText("您似乎有飲酒，" +
				"\n請問您要?");
		
		questionLayout.addView(help);

		int[] item = {4,0};
		
		OnClickListener[] listener = new OnClickListener[item.length];
		listener[0] = new SelfOnClickListener(1);
		listener[1] = new HelpOnClickListener(-1);

		
		for (int i=0;i<item.length;++i){
			questionLayout.addView(createIconView(ICON_TEXT[item[i]],iconBmps[item[i]],listener[i]));
		}
		for (int i=0;i<questionLayout.getChildCount();++i){
			View v = questionLayout.getChildAt(i);
			LinearLayout.LayoutParams param =(LinearLayout.LayoutParams) v.getLayoutParams();
			param.topMargin = param.bottomMargin = margin;
		}
		
		boxLayout.setVisibility(View.VISIBLE);
		
	}
	
	public void generateType3Box(){
		
		questionLayout.removeAllViews();
		
		TextView help = new TextView(context);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		help.setTypeface(wordTypeface);
		help.setTextColor(0xFF000000);
		help.setText("您似乎喝了不少酒，" +
				"\n請問您要?");
		
		questionLayout.addView(help);
		
		int[] item = {5,0,4};
		OnClickListener[] listener = new OnClickListener[item.length];
		listener[0] = new EndOnClickListener();
		listener[1] = new HelpOnClickListener(-1);
		listener[2] = new SelfOnClickListener(0);
		
		
		for (int i=0;i<item.length;++i){
			questionLayout.addView(createIconView(ICON_TEXT[item[i]],iconBmps[item[i]],listener[i]));
		}
		for (int i=0;i<questionLayout.getChildCount();++i){
			View v = questionLayout.getChildAt(i);
			LinearLayout.LayoutParams param =(LinearLayout.LayoutParams) v.getLayoutParams();
			param.topMargin = param.bottomMargin = margin;
		}
		
		boxLayout.setVisibility(View.VISIBLE);
		
	}
	
	private class EndOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			boxLayout.setVisibility(View.INVISIBLE);
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
			SharedPreferences.Editor editor = sp.edit();
			editor.putInt("latest_result", 0);
	    	editor.commit();
	    	statisticFragment.setQuestionAnimation();
		}
	}
	
	private class ExitOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			boxLayout.setVisibility(View.INVISIBLE);
		}
	}
	
	
	private class SelfOnClickListener implements View.OnClickListener{
		
		int type;
		//type == 1 : clock
		//type == 0 : no clock
		
		SelfOnClickListener(int type){
			this.type = type;
		}
		@Override
		public void onClick(View v) {
			questionLayout.removeAllViews();
			
			TextView help = new TextView(context);
			help.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
			help.setTypeface(wordTypeface);
			help.setTextColor(0xFF000000);
			help.setText("請問您現在的情境?");
			
			questionLayout.addView(help);
			
			int[] item = {0,1,2,3,4};
			OnClickListener[] listener = new OnClickListener[item.length];
			
			for (int i=0;i<listener.length;++i)
				listener[i] = new SolverOnClickListener(i,type);
			
			for (int i=0;i<item.length;++i){
				questionLayout.addView(createIconView(ICON2_TEXT[item[i]],icon2Bmps[item[i]],listener[i]));
			}
			for (int i=0;i<questionLayout.getChildCount();++i){
				View view = questionLayout.getChildAt(i);
				LinearLayout.LayoutParams param =(LinearLayout.LayoutParams) view.getLayoutParams();
				param.topMargin = param.bottomMargin = margin;
			}
			
			boxLayout.setVisibility(View.VISIBLE);
		}
	}
	
	private class SolverOnClickListener implements View.OnClickListener{
		
		private int result;
		private int type;
		SolverOnClickListener(int result,int type){
			this.result = result;
			this.type = type;
		}
		
		@Override
		public void onClick(View v) {
			questionLayout.removeAllViews();
			
			TextView help = new TextView(context);
			help.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
			help.setTypeface(wordTypeface);
			help.setTextColor(0xFF000000);
			help.setText("請依照以下指示:");
			
			questionLayout.addView(help);
			
			int[] item = {result};
			
			OnClickListener[] listener = new OnClickListener[item.length];
			if (type == 0){
				listener[0] = new EndOnClickListener();
			}else {
				listener[0] = new ClockOnClickListener(0);
			}
			
			for (int i=0;i<item.length;++i){
				questionLayout.addView(createIconView(ICON3_TEXT[item[i]],icon3Bmps[item[i]],listener[i]));
			}
			for (int i=0;i<questionLayout.getChildCount();++i){
				View view = questionLayout.getChildAt(i);
				LinearLayout.LayoutParams param =(LinearLayout.LayoutParams) view.getLayoutParams();
				param.topMargin = param.bottomMargin = margin;
			}
			
			boxLayout.setVisibility(View.VISIBLE);
		}
	}
	
	private class HelpOnClickListener implements View.OnClickListener{
		
		int type;
		
		String[] dummyTexts = {
				"dummy: 0212345678",
				"dummy: 0212345678",
				"dummy: 0212345678"
				};
		OnClickListener[] dummyListeners = {
				new EndOnClickListener(),
				new EndOnClickListener(),
				new EndOnClickListener()
		};
		OnClickListener[] dummyListeners2 = {
				new ClockOnClickListener(1),
				new ClockOnClickListener(1),
				new ClockOnClickListener(1)
		};
		
		HelpOnClickListener( int type){
			this.type = type;
		}
		
		@Override
		public void onClick(View v) {
			questionLayout.removeAllViews();
			
			TextView help = new TextView(context);
			help.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
			help.setTypeface(wordTypeface);
			help.setTextColor(0xFF000000);
			help.setText("請選擇聯絡對象:");
			
			questionLayout.addView(help);

			String[] texts = dummyTexts;
			OnClickListener[] listeners = dummyListeners;
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
			int cond = sp.getInt("latest_result", 0);
			if (cond == 2)
				listeners = dummyListeners2;
			
			for (int i=0;i<texts.length;++i){
				questionLayout.addView(createIconView(texts[i],iconDoneBmp,listeners[i]));
			}
			for (int i=0;i<questionLayout.getChildCount();++i){
				View view = questionLayout.getChildAt(i);
				LinearLayout.LayoutParams param =(LinearLayout.LayoutParams) view.getLayoutParams();
				param.topMargin = param.bottomMargin = margin;
			}
			
			boxLayout.setVisibility(View.VISIBLE);
		}
	}
	
	private class ClockOnClickListener implements View.OnClickListener{
		
		int type;
		public ClockOnClickListener(int type){
			this.type = type;
		}
		
		@Override
		public void onClick(View v) {
			questionLayout.removeAllViews();
			
			TextView help = new TextView(context);
			help.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
			help.setTypeface(wordTypeface);
			help.setTextColor(0xFF000000);
			help.setText("將啟用每小時定期提醒:");
			
			questionLayout.addView(help);
			
			int[] item = {1,2};
			OnClickListener[] listener = new OnClickListener[item.length];
			listener[0] = new StartClockOnClickListener();
			listener[1] = new EndOnClickListener();
			
			
			for (int i=0;i<item.length;++i){
				questionLayout.addView(createIconView(ICON4_TEXT[item[i]],icon4Bmps[item[i]],listener[i]));
			}
			for (int i=0;i<questionLayout.getChildCount();++i){
				View view = questionLayout.getChildAt(i);
				LinearLayout.LayoutParams param =(LinearLayout.LayoutParams) view.getLayoutParams();
				param.topMargin = param.bottomMargin = margin;
			}
			
			boxLayout.setVisibility(View.VISIBLE);
		}
	}
	
	private class StartClockOnClickListener implements View.OnClickListener{
		
		static public final long HOURMILLIS = 60*60*1000;
		
		@Override
		public void onClick(View v) {

			AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			
			Intent service_intent = new Intent();
			service_intent.setClass(context, AlarmReceiver.class);
			service_intent.setAction("Hourly_notification");
			
			PendingIntent pending = PendingIntent.getBroadcast(context, 0x9999, service_intent, PendingIntent.FLAG_UPDATE_CURRENT);
			alarm.cancel(pending);
			alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,HOURMILLIS,HOURMILLIS,pending);
			
			boxLayout.setVisibility(View.INVISIBLE);
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
			SharedPreferences.Editor editor = sp.edit();
			editor.putInt("latest_result", 0);
			editor.putBoolean("hourly_alarm", true);
	    	editor.commit();
	    	statisticFragment.setQuestionAnimation();
		}
	}
	
	private class ItemOnTouchListener implements View.OnTouchListener{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int e = event.getAction();
			switch(e){
				case MotionEvent.ACTION_OUTSIDE:
					v.setBackgroundColor(0xFFFFFFFF);
					break;
				case MotionEvent.ACTION_MOVE:
					v.setBackgroundColor(0xCC00CCFF);
					break;
				case MotionEvent.ACTION_UP:
					v.setBackgroundColor(0xFFFFFFFF);
					break;
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundColor(0xCC00CCFF);
					break;
			}
			return false;
		}
	}
	
	
	public void closeBox(){
			boxLayout.setVisibility(View.INVISIBLE);
			return;
	}

	private View createIconView(String textStr, Bitmap bmp,OnClickListener listener){
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		text.setTypeface(wordTypeface);
		text.setTextColor(0xFF000000);
		text.setText(textStr);
		
		ImageView icon = (ImageView) layout.findViewById(R.id.question_icon);
		icon.setImageBitmap(bmp);
		LinearLayout.LayoutParams param =(LinearLayout.LayoutParams) icon.getLayoutParams();
		param.rightMargin = margin;
		
		layout.setBackgroundColor(0xFFFFFFFF);
		layout.setOnClickListener(listener);
		layout.setOnTouchListener(onTouchListener);
		
		return layout;
	}
	
}
