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
	
	private TextView help;
	private ImageView bg;
	
	private RelativeLayout mainLayout = null;
	
	private LinearLayout questionLayout;
	
	private Resources r;
	private Point screen;
	
	private EndOnClickListener endListener;
	
	private EndSetting endSetting;
	
	private Typeface digitTypeface;
	private Typeface wordTypeface;
	
	public UIMsgBox(TestFragment testFragment,RelativeLayout mainLayout){
		Log.d("UIMSG","NEW");
		this.testFragment = testFragment;
		this.context = testFragment.getActivity();
		this.r = context.getResources();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		screen = FragmentTabs.getSize();
		setting();
	}
	
	private void setting(){
		
		digitTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dinproregular.ttf");
		wordTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dfheistd-w3.otf");
		
		endListener = new EndOnClickListener();
		box = (RelativeLayout) inflater.inflate(R.layout.test_msg_box,null);
		box.setVisibility(View.INVISIBLE);
		bg = (ImageView) box.findViewById(R.id.test_msg_box_bg);
		
		int textSize = (int)(screen.x * 49.0/720.0);
		
		help = (TextView) box.findViewById(R.id.test_msg_box_help);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		help.setTypeface(wordTypeface);
		
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
		
	}
	
	public  void settingPostTask(){
	}
	
	public void clear(){
		Log.d("UIMSG","CLEAR");
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
		if (bg==null || help == null)
			return;
		
		RelativeLayout.LayoutParams bgParam = (LayoutParams) bg.getLayoutParams();
		bgParam.height = bgParam.height*3/2;
		
		bg.setImageBitmap(bgBmp);
		help.setText("");
		//new
		
		//new version--------------------------------------------------------
		if (questionLayout!=null)
			box.removeView(questionLayout);
		
		int textSize = (int)(screen.x * 49.0/720.0);
		questionLayout = new LinearLayout(context);
		
		questionLayout.setOrientation(LinearLayout.VERTICAL);
		TextView emotionQuestion = new TextView(context);
		emotionQuestion.setText("你的心情指數(由低到高):");
		emotionQuestion.setTypeface(wordTypeface);
		emotionQuestion.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		emotionQuestion.setTextColor(0xFF545454);
		Spinner emotionSpinner = new Spinner(context);
		SpinnerAdapter emotionAdapter = new QuestionAdapter(5);
		emotionSpinner.setAdapter(emotionAdapter);
		emotionSpinner.setSelection(emotionAdapter.getCount()/2);
		questionLayout.addView(emotionQuestion);
		questionLayout.addView(emotionSpinner);
		
		TextView desireQuestion = new TextView(context);
		desireQuestion.setText("你的渴飲程度(由低到高):");
		desireQuestion.setTypeface(wordTypeface);
		desireQuestion.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		desireQuestion.setTextColor(0xFF545454);
		Spinner desireSpinner = new Spinner(context);
		SpinnerAdapter desireAdapter = new QuestionAdapter(10);
		desireSpinner.setAdapter(desireAdapter);
		desireSpinner.setSelection(desireAdapter.getCount()/2);
		questionLayout.addView(desireQuestion);
		questionLayout.addView(desireSpinner);
		
		CheckBox gpsCheckBox = new CheckBox(context);
		gpsCheckBox.setText("是否回報現在位置?");
		gpsCheckBox.setTypeface(wordTypeface);
		gpsCheckBox.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		gpsCheckBox.setTextColor(0xFF545454);
		gpsCheckBox.setSelected(false);
		questionLayout.addView(gpsCheckBox);
		
		Button checkButton = new Button(context);
		checkButton.setText("確定");
		checkButton.setTypeface(wordTypeface);
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
		
		
		box.setOnClickListener(null);
		
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
				testFragment.writeQuestionFile(emotion, desire);
				
				testFragment.startGPS(enableGPS);
			}
		}
		
	}
	
	public void generateInitializingBox(){
		if (bgBmp == null || bgBmp.isRecycled())
			return;
		if (bg==null || help == null)
			return;
		bg.setImageBitmap(bgBmp);
		help.setText("請稍待");
		
		RelativeLayout.LayoutParams helpParam = (LayoutParams) help.getLayoutParams();
		helpParam.topMargin = (int)(screen.x * 140.0/720.0);
		
		box.setOnClickListener(null);
		
		box.setVisibility(View.VISIBLE);
	}
	
	public void closeInitializingBox(){
			box.setVisibility(View.INVISIBLE);
			return;
	}
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
			for (int i=1;i<=num;++i)
				results[i-1] = i;
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
			t.setTypeface(digitTypeface);
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
			t.setTypeface(digitTypeface);
			return t;
		}
		
	}
}
