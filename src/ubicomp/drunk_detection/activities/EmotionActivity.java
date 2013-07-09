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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EmotionActivity extends Activity {

	private LayoutInflater inflater;
	
	private int textSize;
	private int iconMargin;
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
			new NormalSelectionOnClickListener(0),
			new NormalSelectionOnClickListener(1),
			new NormalSelectionOnClickListener(2),
			new HelpOnClickListener(3),
			new HelpOnClickListener(4)
	};
	
	QuestionDB db;
	
	private Point screen;
	
	private int state = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_emotion);
		
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
		wordTypefaceBold = Typeface.createFromAsset(activity.getAssets(), "fonts/DFLiHeiStd-W5.otf");
		db = new QuestionDB(activity);
		setQuestionStart();

	}
	
	private void setQuestionStart(){
		state = 0;
		
		mainLayout.removeAllViews();
		onTouchListener = new ItemOnTouchListener();
		
		textSize = screen.x * 24/480;
		iconMargin = screen.x * 33/480;
		View title = createTitleView();
		mainLayout.addView(title);
		LinearLayout.LayoutParams titleparam =(LinearLayout.LayoutParams) title.getLayoutParams();
		titleparam.height = screen.x*230/1080;
		
		View tv = createTextView("照顧情緒，我可以：");
		mainLayout.addView(tv);
		
		for (int i=0;i<texts.length;++i){
			View v = createIconView(texts[i],DRAWABLE_ID[i],clickListeners[i]);
			mainLayout.addView(v);
		}
		
		int rest_block = 10 - mainLayout.getChildCount();
		for (int i=0;i<rest_block;++i){
			View v = createBlankView();
			mainLayout.addView(v);
		}
	}
	
	private static final String[] dummyTexts = {
			"dummy:0212345678",
			"dummy:0212345678",
			"dummy:0212345678"
			};
	
	private void setQuestionCall(int type){
		state = 1;
		
		mainLayout.removeAllViews();
		
		View title = createTitleView();
		mainLayout.addView(title);
		LinearLayout.LayoutParams titleparam =(LinearLayout.LayoutParams) title.getLayoutParams();
		titleparam.height = screen.x*230/1080;
		
		View tv = createTextView("請選擇聯絡對象：");
		mainLayout.addView(tv);

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
		}
		
		int rest_block = 10 - mainLayout.getChildCount();
		for (int i=0;i<rest_block;++i){
			View vv = createBlankView();
			mainLayout.addView(vv);
		}
	}
	
	private void setQuestionEnd(int selection){
		state = 1;
		
		mainLayout.removeAllViews();
		View title = createTitleView();
		mainLayout.addView(title);
		LinearLayout.LayoutParams titleparam =(LinearLayout.LayoutParams) title.getLayoutParams();
		titleparam.height = screen.x*244/1080;
		
		String str = "請繼續加油!";
		View tv = createTextView(str);
		mainLayout.addView(tv);
		View vv = createIconView("完成",R.drawable.questionnaire_item_ok,new EndOnClickListener(selection));
		mainLayout.addView(vv);
		
		int rest_block = 10 - mainLayout.getChildCount();
		for (int i=0;i<rest_block;++i){
			View v = createBlankView();
			mainLayout.addView(v);
		}
	}
	
	private View createTextView(String textStr){
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		text.setTypeface(wordTypefaceBold);
		text.setTextColor(0xFF777777);
		text.setText(textStr);
		
		LinearLayout.LayoutParams tParam = (LinearLayout.LayoutParams)text.getLayoutParams();
		tParam.leftMargin = textSize;
		
		layout.setBackgroundResource(R.drawable.questionnaire_bar_question);
		
		return layout;
	}
	
	
	private View createIconView(String textStr, int DrawableId ,OnClickListener listener){
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		text.setTypeface(wordTypefaceBold);
		text.setTextColor(0xFF5c5c5c);
		text.setText(textStr);
		
		LinearLayout.LayoutParams tParam = (LinearLayout.LayoutParams)text.getLayoutParams();
		tParam.leftMargin = textSize;
		
		ImageView icon = (ImageView) layout.findViewById(R.id.question_icon);
		icon.setImageResource(DrawableId);
		LinearLayout.LayoutParams iParam =(LinearLayout.LayoutParams) icon.getLayoutParams();
		iParam.rightMargin = iconMargin;
		
		layout.setOnClickListener(listener);
		layout.setOnTouchListener(onTouchListener);
		layout.setBackgroundResource(R.drawable.questionnaire_bar_normal);
		
		return layout;
	}
	
	private View createTitleView(){
		
		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.titlebar, null);
		TextView text = (TextView) layout.findViewById(R.id.titlebar_text);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		text.setTypeface(wordTypefaceBold);
		text.setText("心情DIY");
		
		RelativeLayout.LayoutParams tParam = (RelativeLayout.LayoutParams)text.getLayoutParams();
		tParam.leftMargin = textSize;
		
		ImageView icon = (ImageView) layout.findViewById(R.id.titlebar_icon);
		RelativeLayout.LayoutParams iParam =(RelativeLayout.LayoutParams) icon.getLayoutParams();
		iParam.leftMargin = textSize;
		
		return layout;
	}
	
	private View createBlankView(){
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
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
			db.insertEmotion(in,null);
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
	
	private class NormalSelectionOnClickListener implements View.OnClickListener{
		int in;
		NormalSelectionOnClickListener( int in){
			this.in = in;
		}
		
		@Override
		public void onClick(View v) {
			setQuestionEnd(in);
		}
	}
	
	private class HelpOnClickListener implements View.OnClickListener{
		int type;
		HelpOnClickListener( int type){
			this.type = type;
		}
		
		@Override
		public void onClick(View v) {
			setQuestionCall(type);
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK){
			if (state == 0){
				Toast.makeText(mainLayout.getContext(), "確定放棄填答心情DIY？", Toast.LENGTH_LONG).show();
				--state;
			}else if (state == -1)
				return super.onKeyDown(keyCode, event);
			else{
				--state;
				if (state == 0)
					setQuestionStart();
			}
		}
		
		return false;
	}
}
