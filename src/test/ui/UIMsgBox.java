package test.ui;

import main.activities.FragmentTabs;
import main.activities.R;
import main.activities.TestFragment;
import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class UIMsgBox {

	private TestFragment testFragment;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout box = null;
	
	private Bitmap bgBmp;
	private Bitmap buttonBmp;
	
	private TextView help,yes,no;
	private ImageView bg;
	private ImageView yesBg,noBg;
	
	private RelativeLayout mainLayout = null;
	
	private LinearLayout questionLayout;
	
	private Resources r;
	private Point screen;
	
	private GPSOnClickListener gpsListener;
	private EndOnClickListener endListener;
	
	private EndSetting endSetting;
	
	//private TimeUpHandler timeUpHandler;
	
	public UIMsgBox(TestFragment testFragment,RelativeLayout mainLayout){
		Log.d("UIMSG","NEW");
		this.testFragment = testFragment;
		this.context = testFragment.getActivity();
		this.r = context.getResources();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		screen = FragmentTabs.getSize();
		//timeUpHandler = new TimeUpHandler();
		setting();
	}
	
	private void setting(){
		gpsListener  = new GPSOnClickListener();
		endListener = new EndOnClickListener();
		box = (RelativeLayout) inflater.inflate(R.layout.test_msg_box,null);
		box.setVisibility(View.INVISIBLE);
		bg = (ImageView) box.findViewById(R.id.test_msg_box_bg);
		
		int textSize = (int)(screen.x * 49.0/720.0);
		
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
		
		if(bgBmp!=null && !bgBmp.isRecycled()){
			bgBmp.recycle();
			bgBmp=null;
		}
		if(buttonBmp!=null && !buttonBmp.isRecycled()){
			buttonBmp.recycle();
			buttonBmp=null;
		}
		
		Point screen = FragmentTabs.getSize();
		
		Bitmap tmp;
		tmp = BitmapFactory.decodeResource(r, R.drawable.test_box_bg_1);
		bgBmp = Bitmap.createScaledBitmap(tmp, (int)(screen.x * 666.0/720.0),  (int)(screen.x * 440.0/720.0), true);
		tmp.recycle();
		tmp = buttonBmp = BitmapFactory.decodeResource(r, R.drawable.test_box_button);
		buttonBmp = BitmapFactory.decodeResource(r, R.drawable.test_box_button);
		tmp.recycle();
		
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
		/*if (timeUpHandler!=null){
			timeUpHandler.removeMessages(0);
			timeUpHandler.removeMessages(1);
		}*/
		mainLayout.removeView(box);
		if(bgBmp!=null && !bgBmp.isRecycled()){
			bgBmp.recycle();
			bgBmp=null;
		}
		if(buttonBmp!=null && !buttonBmp.isRecycled()){
			buttonBmp.recycle();
			buttonBmp=null;
		}
	}
	
	@SuppressWarnings("deprecation")
	public void generateGPSCheckBox(){
		if (bgBmp == null || bgBmp.isRecycled())
			return;
		if (bg==null || help == null || yes == null || no == null || yesBg ==null || noBg == null)
			return;
		
		RelativeLayout.LayoutParams bgParam = (LayoutParams) bg.getLayoutParams();
		bgParam.height = bgParam.height*3/2;
		
		bg.setImageBitmap(bgBmp);
		help.setText("");
		/*help.setText("是否回報現在位置?");
		yes.setVisibility(View.VISIBLE);
		no.setVisibility(View.VISIBLE);
		yesBg.setVisibility(View.VISIBLE);
		noBg.setVisibility(View.VISIBLE);
		*/
		//new
		yes.setVisibility(View.INVISIBLE);
		no.setVisibility(View.INVISIBLE);
		yesBg.setVisibility(View.INVISIBLE);
		noBg.setVisibility(View.INVISIBLE);
		
		//new veersion--------------------------------------------------------
		if (questionLayout!=null)
			box.removeView(questionLayout);
		
		int textSize = (int)(screen.x * 49.0/720.0);
		questionLayout = new LinearLayout(context);
		
		questionLayout.setOrientation(LinearLayout.VERTICAL);
		TextView emotionQuestion = new TextView(context);
		emotionQuestion.setText("你的心情指數(由低到高):");
		emotionQuestion.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		emotionQuestion.setTextColor(0xFF545454);
		Spinner emotionSpinner = new Spinner(context);
		SpinnerAdapter emotionAdapter = new QuestionAdapter(10);
		emotionSpinner.setAdapter(emotionAdapter);
		emotionSpinner.setSelection(emotionAdapter.getCount()/2);
		questionLayout.addView(emotionQuestion);
		questionLayout.addView(emotionSpinner);
		
		TextView desireQuestion = new TextView(context);
		desireQuestion.setText("你的渴飲程度(由低到高):");
		desireQuestion.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		desireQuestion.setTextColor(0xFF545454);
		Spinner desireSpinner = new Spinner(context);
		SpinnerAdapter desireAdapter = new QuestionAdapter(5);
		desireSpinner.setAdapter(desireAdapter);
		desireSpinner.setSelection(desireAdapter.getCount()/2);
		questionLayout.addView(desireQuestion);
		questionLayout.addView(desireSpinner);
		
		CheckBox gpsCheckBox = new CheckBox(context);
		gpsCheckBox.setText("是否回報現在位置?");
		gpsCheckBox.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		gpsCheckBox.setTextColor(0xFF545454);
		gpsCheckBox.setSelected(false);
		questionLayout.addView(gpsCheckBox);
		
		Button checkButton = new Button(context);
		checkButton.setText("確定");
		checkButton.setOnClickListener(endListener);
		questionLayout.addView(checkButton);
		
		box.addView(questionLayout);
		
		RelativeLayout.LayoutParams qParam = (RelativeLayout.LayoutParams)questionLayout.getLayoutParams();
		qParam.leftMargin = (int)(screen.x * 50.0/720.0);
		qParam.topMargin = (int)(screen.x * 60.0/720.0);
		
		endSetting = new EndSetting();
		endSetting.gps = gpsCheckBox;
		endSetting.emotion = emotionSpinner;
		endSetting.desire = desireSpinner;
		//-----------------------------------------------------------------------
		
		//RelativeLayout.LayoutParams helpParam = (LayoutParams) help.getLayoutParams();
		//helpParam.topMargin = (int)(screen.x * 140.0/720.0);
		
		box.setOnClickListener(null);
		//yesBg.setOnClickListener(gpsListener);
		//noBg.setOnClickListener(gpsListener);
		
		box.setVisibility(View.VISIBLE);
	}
	
	private class EndOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			box.setVisibility(View.INVISIBLE);
			if (questionLayout!=null)
				box.removeView(questionLayout);
			if (endSetting!=null){
				boolean enableGPS = endSetting.gps.isChecked();
				
				int desire = (Integer) endSetting.desire.getSelectedItem();
				int emotion =  (Integer) endSetting.emotion.getSelectedItem();
				String setting_str = desire+"/"+emotion+"/"+enableGPS; 
				Log.d("MSGBOX SETTING",setting_str);
				testFragment.startGPS(enableGPS);
			}
		}
		
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
	/*
	public void generateBTCheckBox(){
		if (bgBmp == null || bgBmp.isRecycled())
			return;
		if (bg==null || help == null || yes == null || no == null || yesBg ==null || noBg == null)
			return;
		
		bg.setImageBitmap(bgBmp);
		help.setText("請啟用\n酒測裝置");
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
	*/
	public void generateInitializingBox(){
		if (bgBmp == null || bgBmp.isRecycled())
			return;
		if (bg==null || help == null || yes == null || no == null || yesBg ==null || noBg == null)
			return;
		bg.setImageBitmap(bgBmp);
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
	/*
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
	*/
	public class EndSetting{
		public CheckBox gps;
		public Spinner emotion;
		public Spinner desire;
	}
	
	private class QuestionAdapter implements SpinnerAdapter{

		private int[] results;
		private int textSize;
		public QuestionAdapter(int num){
			results = new int[num];
			for (int i=0;i<num;++i)
				results[i] = i;
			textSize = (int)(screen.x * 49.0/720.0);
		}
		
		@Override
		public int getCount() {
			return results.length;
		}

		@Override
		public Object getItem(int position) {
			return results[position];
		}

		@Override
		public long getItemId(int position) {
			return results[position];
		}

		@Override
		public int getItemViewType(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView t = new TextView(context);
			t.setText(String.valueOf(results[position]));
			t.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			t.setBackgroundColor(0xFFFFFFFF);
			t.setTextColor(0xFF545454);
			return t;
		}

		@Override
		public int getViewTypeCount() {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			TextView t = new TextView(context);
			t.setText(String.valueOf(results[position]));
			t.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			t.setBackgroundColor(0xFFFFFFFF);
			t.setTextColor(0xFF545454);
			return t;
		}
		
	}
}
