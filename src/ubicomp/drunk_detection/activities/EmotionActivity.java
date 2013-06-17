package ubicomp.drunk_detection.activities;

import ubicomp.drunk_detection.activities.R;
import database.QuestionDB;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EmotionActivity extends Activity {

	private LayoutInflater inflater;
	
	private int textSize = 24;
	private Typeface wordTypeface;
	private Typeface wordTypefaceBold;
	
	private LinearLayout mainLayout;
	
	private Activity activity;
	
	private ItemOnTouchListener onTouchListener;
	
	private static final int[] DRAWABLE_ID = {
		R.drawable.questionnaire_item_sol_1,
		R.drawable.questionnaire_item_sol_2,
		R.drawable.questionnaire_item_sol_3,
		R.drawable.questionnaire_item_sol_4,
		R.drawable.questionnaire_item_sol_5
	};
	
	
	private static final String[] texts = {
		"閉眼專注觀察呼吸",
		"專心健走 10 ~ 30 分鐘",
		"從事休閒活動\n(如聽音樂)",
		"社區心理諮商",
		"與親友聊天"
	} ;
	
	private OnClickListener[] clickListeners = {
			new EndOnClickListener(0),
			new EndOnClickListener(1),
			new EndOnClickListener(2),
			new HelpOnClickListener(3),
			new HelpOnClickListener(4)
	};
	
	QuestionDB db;
	
	private Point screen;
	private int height;
	private int icon_size; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emotion);
		View titleView = getWindow().findViewById(android.R.id.title);
	    if (titleView != null) {
	      ViewParent titleBar = titleView.getParent();
	      if (titleBar != null) {
	        View parentView = (View)titleBar;
	        parentView.setBackgroundColor(0xFFf39800);
	      }
	    }
		
		
		Display display = getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT<13){
			@SuppressWarnings("deprecation")
			int w = display.getWidth();
			@SuppressWarnings("deprecation")
			int h = display.getHeight();
			screen = new Point(w,h);
		}
		else{
			screen = new Point();
			display.getSize(screen);
		}
		
	}

	
	
	@Override
	protected void onResume(){
		super.onResume();
		this.activity = this;
		mainLayout = (LinearLayout) this.findViewById(R.id.emotion_main_layout);
		inflater = LayoutInflater.from(activity);
		wordTypeface = Typeface.createFromAsset(activity.getAssets(), "fonts/dfheistd-w3.otf");
		wordTypefaceBold = Typeface.createFromAsset(activity.getAssets(), "fonts/dfheistd-w5.otf");
		db = new QuestionDB(activity);
		
		mainLayout.removeAllViews();
		onTouchListener = new ItemOnTouchListener();
		
		textSize = screen.x * 72/1080;
		height =  screen.x * 202/1080;
		icon_size = screen.x * 100/1080;
		
		View tv = createTextView("若你想要飲酒的話，你可以：");
		mainLayout.addView(tv);
		LinearLayout.LayoutParams tvparam =(LinearLayout.LayoutParams) tv.getLayoutParams();
		tvparam.height = height;
		
		for (int i=0;i<texts.length;++i){
			View v = createIconView(texts[i],DRAWABLE_ID[i],clickListeners[i]);
			mainLayout.addView(v);
			LinearLayout.LayoutParams param =(LinearLayout.LayoutParams) v.getLayoutParams();
			param.height = height;
		}
	}
	
	
	private View createTextView(String textStr){
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		text.setTypeface(wordTypefaceBold);
		text.setTextColor(0xFF777777);
		text.setText(textStr);
		
		layout.setBackgroundResource(R.drawable.questionnaire_bar_question);
		
		return layout;
	}
	
	
	private View createIconView(String textStr, int DrawableId ,OnClickListener listener){
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		text.setTypeface(wordTypeface);
		text.setTextColor(0xFF5c5c5c);
		text.setText(textStr);
		
		ImageView icon = (ImageView) layout.findViewById(R.id.question_icon);
		icon.setImageResource(DrawableId);
		LinearLayout.LayoutParams iParam =(LinearLayout.LayoutParams) icon.getLayoutParams();
		iParam.width = iParam.height = icon_size;
		iParam.rightMargin = icon_size;
		
		layout.setOnClickListener(listener);
		layout.setOnTouchListener(onTouchListener);
		layout.setBackgroundResource(R.drawable.questionnaire_bar_normal);
		
		return layout;
	}
	
	private class EndOnClickListener implements View.OnClickListener{
		int in;
		
		EndOnClickListener(int in){
			this.in = in+1;
		}
		
		@Override
		public void onClick(View v) {
			db.insertEmotion(in);
			activity.finish();
		}
	}
	
	private class CallOnClickListener implements View.OnClickListener{
		int in;
		String call;
		
		CallOnClickListener(int in,String call){
			this.in = in+1;
			this.call = call;
		}
		
		@Override
		public void onClick(View v) {
			db.insertEmotion(in,call);
			activity.finish();
		}
	}
	
	private class HelpOnClickListener implements View.OnClickListener{
		
		int type;
		
		String[] dummyTexts = {
				"dummy:0212345678",
				"dummy:0212345678",
				"dummy:0212345678"
				};

		
		HelpOnClickListener( int type){
			this.type = type;
		}
		
		@Override
		public void onClick(View v) {
			mainLayout.removeAllViews();
			
			View tv = createTextView("請選擇聯絡對象");
			mainLayout.addView(tv);
			LinearLayout.LayoutParams tvparam =(LinearLayout.LayoutParams) tv.getLayoutParams();
			tvparam.height = height;

			String[] texts = dummyTexts;
			
			OnClickListener[] dummyListeners = {
					new CallOnClickListener(type,dummyTexts[0]),
					new CallOnClickListener(type,dummyTexts[1]),
					new CallOnClickListener(type,dummyTexts[2])
			};
			
			OnClickListener[] listeners = dummyListeners;
			
			for (int i=0;i<texts.length;++i){
				View vv = createIconView(texts[i],R.drawable.questionnaire_item_call,listeners[i]);
				mainLayout.addView(vv);
				LinearLayout.LayoutParams vparam =(LinearLayout.LayoutParams) vv.getLayoutParams();
				vparam.height = height;
			}
		}
	}
	
	
	private class ItemOnTouchListener implements View.OnTouchListener{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int e = event.getAction();
			switch(e){
				case MotionEvent.ACTION_OUTSIDE:
					v.setBackgroundResource(R.drawable.questionnaire_bar_normal);
					break;
				case MotionEvent.ACTION_MOVE:
					v.setBackgroundResource(R.drawable.questionnaire_bar_selected);
					break;
				case MotionEvent.ACTION_UP:
					v.setBackgroundResource(R.drawable.questionnaire_bar_normal);
					break;
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundResource(R.drawable.questionnaire_bar_selected);
					break;
			}
			return false;
		}
	}
}
