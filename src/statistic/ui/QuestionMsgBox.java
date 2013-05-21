package statistic.ui;

import main.activities.FragmentTabs;
import main.activities.R;
import main.activities.StatisticFragment;
import main.activities.TestFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;

public class QuestionMsgBox {

	private StatisticFragment statisticFragment;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	
	private RelativeLayout mainLayout;
	
	private LinearLayout questionLayout;
	
	private Resources r;
	private Point screen;
	
	private EndOnClickListener endListener;
	
	private Typeface digitTypeface;
	private Typeface wordTypeface;
	
	private int textSize;
	
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
		
		digitTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dinproregular.ttf");
		wordTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dfheistd-w3.otf");
		
		endListener = new EndOnClickListener();
		boxLayout = (RelativeLayout) inflater.inflate(R.layout.question_box_layout,null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		questionLayout = (LinearLayout) boxLayout.findViewById(R.id.question_layout);
		
		textSize = (int)(screen.x * 42.0/720.0);
		
	}
	
	public void settingPreTask(){
		mainLayout.addView(boxLayout);
	}
	
	
	public void settingInBackground(){
		
		Point screen = FragmentTabs.getSize();
		
		RelativeLayout.LayoutParams boxParam = (LayoutParams) boxLayout.getLayoutParams();
		boxParam.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
		boxParam.width = (int)(screen.x*600.0/720.0);
		
	}
	
	public  void settingPostTask(){
	}
	
	public void clear(){
		Log.d("UIMSG","CLEAR");
		mainLayout.removeView(boxLayout);
	}
	
	public void generateType0Box(){
		
		questionLayout.removeAllViews();
		
		TextView help = new TextView(context);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		help.setTypeface(wordTypeface);
		help.setTextColor(0xFF000000);
		help.setText("如果想要喝酒的話，" +
				"\n請先離開至無酒精的地方，" +
				"\n並做閉眼觀呼吸");
		
		Button connectButton = new Button(context);
		connectButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		connectButton.setText("聯絡親近的人");
		connectButton.setOnClickListener(endListener);
		
		Button neighborButton = new Button(context);
		neighborButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		neighborButton.setText("社區心理諮商門診");
		neighborButton.setOnClickListener(endListener);
		
		Button mindButton = new Button(context);
		mindButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		mindButton.setText("心情專線");
		mindButton.setOnClickListener(endListener);
		
		questionLayout.addView(help);
		questionLayout.addView(connectButton);
		questionLayout.addView(neighborButton);
		questionLayout.addView(mindButton);
		questionLayout.setOnClickListener(null);
		
		boxLayout.setVisibility(View.VISIBLE);
		
	}
	
	public void generateType1Box(){
		
		questionLayout.removeAllViews();
		
		TextView help = new TextView(context);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		help.setTypeface(wordTypeface);
		help.setTextColor(0xFF000000);
		help.setText("如果想要喝酒的話，" +
				"\n請先離開至無酒精的地方，" +
				"\n做閉眼深呼吸" +
				"\n並確認今日行程是否要更改");
		
		
		questionLayout.addView(help);
		questionLayout.setOnClickListener(endListener);
		
		boxLayout.setVisibility(View.VISIBLE);
		
	}
	
	public void generateType2Box(){
		
		questionLayout.removeAllViews();
		
		TextView help = new TextView(context);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		help.setTypeface(wordTypeface);
		help.setTextColor(0xFF000000);
		help.setText("您似乎有飲酒，" +
				"\n請問能否自行處理?");
		
		Button selfButton = new Button(context);
		selfButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		selfButton.setText("可自行處理");
		selfButton.setOnClickListener(endListener);
		
		Button helpButton = new Button(context);
		helpButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		helpButton.setText("需要協助");
		helpButton.setOnClickListener(endListener);
		
		
		questionLayout.addView(help);
		questionLayout.addView(selfButton);
		questionLayout.addView(helpButton);
		questionLayout.setOnClickListener(null);
		
		boxLayout.setVisibility(View.VISIBLE);
		
	}
	
	public void generateType3Box(){
		
		questionLayout.removeAllViews();
		
		TextView help = new TextView(context);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		help.setTypeface(wordTypeface);
		help.setTextColor(0xFF000000);
		help.setText("您似乎喝了不少酒，" +
				"\n請先離開至無酒精的地方，" +
				"\n並做閉眼深呼吸" );
		
		
		Button helpButton = new Button(context);
		helpButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		helpButton.setText("請求協助");
		helpButton.setOnClickListener(endListener);
		
		Button hospitalButton = new Button(context);
		hospitalButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		hospitalButton.setText("就醫");
		hospitalButton.setOnClickListener(endListener);
		
		
		questionLayout.addView(help);
		questionLayout.addView(helpButton);
		questionLayout.addView(hospitalButton);
		questionLayout.setOnClickListener(null);
		
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
	
	
	public void closeBox(){
			boxLayout.setVisibility(View.INVISIBLE);
			return;
	}
	
	
}
