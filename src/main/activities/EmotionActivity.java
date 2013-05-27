package main.activities;

import database.QuestionDB;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EmotionActivity extends Activity {

	private LayoutInflater inflater;
	
	private int textSize = 24;
	private Typeface wordTypeface;

	private LinearLayout mainLayout;
	
	private Activity activity;
	
	private ItemOnTouchListener onTouchListener;
	
	private static final int[] DRAWABLE_ID = {
		R.drawable.question_icon3_breath,
		R.drawable.question_icon3_jogging,
		R.drawable.question_icon3_music,
		R.drawable.question_icon_helper,
		R.drawable.question_icon_neighbor
	};
	
	private static final String[] texts = {
		"閉眼專注觀察呼吸",
		"專心健走三十分鐘",
		"聽音樂或閱讀書籍",
		"社區心理諮商",
		"尋求親友協助"
	} ;
	
	private OnClickListener[] clickListeners = {
			new EndOnClickListener(0),
			new EndOnClickListener(1),
			new EndOnClickListener(2),
			new HelpOnClickListener(3),
			new HelpOnClickListener(4)
	};
	
	private TextView text;
	
	QuestionDB db;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emotion);
		setTitle("心情DIY");

	}

	@Override
	protected void onResume(){
		super.onResume();
		this.activity = this;
		mainLayout = (LinearLayout) this.findViewById(R.id.emotion_main_layout);
		text = new TextView(activity);
		inflater = LayoutInflater.from(activity);
		wordTypeface = Typeface.createFromAsset(activity.getAssets(), "fonts/dfheistd-w3.otf");
		db = new QuestionDB(activity);
		
		mainLayout.removeAllViews();
		onTouchListener = new ItemOnTouchListener();
		
		text.setText("若你想飲酒的話，你可以:");
		text.setTextColor(0xFF000000);
		text.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
		
		mainLayout.addView(text);
		
		for (int i=0;i<texts.length;++i){
			mainLayout.addView(createIconView(texts[i],DRAWABLE_ID[i],clickListeners[i]));
		}
	}
	
	
	private View createIconView(String textStr, int DrawableId ,OnClickListener listener){
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize );
		text.setTypeface(wordTypeface);
		text.setTextColor(0xFF000000);
		text.setText(textStr);
		
		ImageView icon = (ImageView) layout.findViewById(R.id.question_icon);
		icon.setImageResource(DrawableId);
		
		layout.setBackgroundColor(0xFFFFFFFF);
		layout.setOnClickListener(listener);
		layout.setOnTouchListener(onTouchListener);
		
		return layout;
	}
	
	private class EndOnClickListener implements View.OnClickListener{
		int in;
		
		EndOnClickListener(int in){
			this.in = in;
		}
		
		@Override
		public void onClick(View v) {
			db.insertEmotion(in);
			activity.finish();
		}
	}
	
	private class HelpOnClickListener implements View.OnClickListener{
		
		int type;
		
		String[] dummyTexts = {
				"dummy: 0212345678",
				"dummy: 0212345678",
				"dummy: 0212345678"
				};

		
		HelpOnClickListener( int type){
			this.type = type;
		}
		
		@Override
		public void onClick(View v) {
			mainLayout.removeAllViews();
			
			text.setText("請選擇聯絡對象:");
			mainLayout.addView(text);

			String[] texts = dummyTexts;
			
			OnClickListener[] dummyListeners = {
					new EndOnClickListener(type),
					new EndOnClickListener(type),
					new EndOnClickListener(type)
			};
			
			OnClickListener[] listeners = dummyListeners;
			
			for (int i=0;i<texts.length;++i){
				mainLayout.addView(createIconView(texts[i],R.drawable.question_done,listeners[i]));
			}
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
}
