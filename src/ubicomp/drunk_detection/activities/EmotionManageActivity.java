package ubicomp.drunk_detection.activities;

import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.ui.CustomToast;
import ubicomp.drunk_detection.ui.Typefaces;
import data.database.QuestionDB;
import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLoggerLog;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EmotionManageActivity extends Activity {

	private LayoutInflater inflater;
	
	private int textSize;
	private int iconMargin;
	private Point screen;
	private Typeface wordTypefaceBold;

	private LinearLayout mainLayout;
	
	private Activity activity;
	
	private ItemOnTouchListener onTouchListener;
	
	private int emotion, r_type;
	private String reason;
	private EditText r_texts;
	
	private Toast endToast;
	
	private static final int[] EMOTION_DRAWABLE_ID = {
		R.drawable.questionnaire_item_e1,
		R.drawable.questionnaire_item_e2,
		R.drawable.questionnaire_item_e3,
		R.drawable.questionnaire_item_e4,
		R.drawable.questionnaire_item_e5,
		R.drawable.questionnaire_item_e6,
		R.drawable.questionnaire_item_e7,
	};
	
	private static String[] emotion_texts;
	
	private static final int[] RELATED_DRAWABLE_ID = {
		R.drawable.questionnaire_item_c1,
		R.drawable.questionnaire_item_c2,
		R.drawable.questionnaire_item_c3,
		R.drawable.questionnaire_item_c4
	};
	
	private static String[] related_texts;
	
	private QuestionDB db;
	private static final int TOTAL_BLOCK = 15;	
	private int state = 0;
	
	private ImageView updown;
	private Drawable upDrawable,downDrawable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_emotion_manage);
		
		emotion =  r_type = -1;
		reason = "";
		r_texts = null;
		
		emotion_texts = getResources().getStringArray(R.array.emotion_manage_emotion);
		related_texts = getResources().getStringArray(R.array.emotion_manage_related);
		
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
		
		this.activity = this;
		db = new QuestionDB(activity);
		mainLayout = (LinearLayout) this.findViewById(R.id.emotion_main_layout);
		inflater = LayoutInflater.from(activity);
		wordTypefaceBold = Typefaces.getWordTypefaceBold(this);
		
		mainLayout.removeAllViews();
		onTouchListener = new ItemOnTouchListener();
		
		textSize = screen.x * 24/480;
		iconMargin = screen.x * 33/480;
		
		upDrawable = getResources().getDrawable(R.drawable.questionnaire_item_up);
		downDrawable = getResources().getDrawable(R.drawable.questionnaire_item_down);
	}

	@Override
	protected void onResume(){
		super.onResume();
		setQuestionEmotion();
	}
	
	private void setQuestionEmotion(){
		mainLayout.removeAllViews();
		
		View title = createTitleView();
		mainLayout.addView(title);
		LinearLayout.LayoutParams titleparam =(LinearLayout.LayoutParams) title.getLayoutParams();
		titleparam.height = screen.x*230/1080;
		
		View tv = createTextView(R.string.emotion_manage_help1);
		mainLayout.addView(tv);
		
		for (int i=0;i<emotion_texts.length;++i){
			View v = createIconView(emotion_texts[i],EMOTION_DRAWABLE_ID[i],new EmotionOnClickListener(i+1));
			mainLayout.addView(v);
		}
		
		int rest_block = TOTAL_BLOCK  - mainLayout.getChildCount();
		for (int i=0;i<rest_block;++i){
			View v = createBlankView();
			mainLayout.addView(v);
		}
	}
	
	private void setQuestionType(){
		mainLayout.removeAllViews();
		
		View title = createTitleView();
		mainLayout.addView(title);
		LinearLayout.LayoutParams titleparam =(LinearLayout.LayoutParams) title.getLayoutParams();
		titleparam.height = screen.x*230/1080;
		
		View tv = createTextView(R.string.emotion_manage_help2);
		mainLayout.addView(tv);
		
		for (int i=0;i<related_texts.length;++i){
			View vv = createIconView(related_texts[i],RELATED_DRAWABLE_ID[i],new RelatedOnClickListener(i+1));
			mainLayout.addView(vv);
		}
		
		int rest_block = TOTAL_BLOCK  - mainLayout.getChildCount();
		for (int i=0;i<rest_block;++i){
			View v = createBlankView();
			mainLayout.addView(v);
		}
	}
	
	private String[] select_item;
	
	private void setQuestionEdit(){
		mainLayout.removeAllViews();

		select_item = db.getInsertedReason(r_type);
		
		View title = createTitleView();
		mainLayout.addView(title);
		LinearLayout.LayoutParams titleparam =(LinearLayout.LayoutParams) title.getLayoutParams();
		titleparam.height = screen.x*230/1080;
		
		String str = getResources().getString(R.string.emotion_manage_help3)+related_texts[r_type-1];

		View tv = createTextView(str);
		mainLayout.addView(tv);
		
		View edv = createEditView(r_type);
		mainLayout.addView(edv);
		
		View ev = createTextView(R.string.emotion_manage_help4);
		mainLayout.addView(ev);
		
		View vv=createIconView(R.string.ok,R.drawable.questionnaire_item_ok,new EditedOnClickListener());
		mainLayout.addView(vv);
		
		int rest_block = TOTAL_BLOCK  - mainLayout.getChildCount();
		for (int i=0;i<rest_block;++i){
			View v = createBlankView();
			mainLayout.addView(v);
		}
	}
	
	private void setQuestionEnd(){
		mainLayout.removeAllViews();
		if (r_texts!=null)
			reason = r_texts.getText().toString();
		
		View title = createTitleView();
		mainLayout.addView(title);
		LinearLayout.LayoutParams titleparam =(LinearLayout.LayoutParams) title.getLayoutParams();
		titleparam.height = screen.x*230/1080;
		
		View tv = createTextView(R.string.emotion_end_message);
		mainLayout.addView(tv);
		View vv = createIconView(R.string.done,R.drawable.questionnaire_item_ok,new EndOnClickListener());
		mainLayout.addView(vv);
		
		int rest_block = TOTAL_BLOCK  - mainLayout.getChildCount();
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
	
private View createTextView(int textStr){
		
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
	
	@SuppressWarnings("deprecation")
	private View createEditView(int type){
		
		RelativeLayout layout = new RelativeLayout(mainLayout.getContext());
		
		EditText edit = new EditText(activity);
		if (Build.VERSION.SDK_INT>=16)
			edit.setBackground(mainLayout.getContext().getResources().getDrawable(R.drawable.questionnaire_input));
		else
			edit.setBackgroundDrawable(mainLayout.getContext().getResources().getDrawable(R.drawable.questionnaire_input));
		edit.setTextColor(0xFF000000);
		edit.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize*4/3);
		edit.setTypeface(wordTypefaceBold);
		edit.addTextChangedListener(
				new TextWatcher(){
					@Override
					public void afterTextChanged(Editable arg0) {	}
					@Override
					public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {}
					@Override
					public void onTextChanged(CharSequence arg0, int arg1,	int arg2, int arg3) {
						ClickLoggerLog.Log(getBaseContext(), ClickLogId.EMOTIONMANAGE_EDIT);					
					}
		});
		edit.setId(0x1999);
		layout.addView(edit);
		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)edit.getLayoutParams();
		param.addRule(RelativeLayout.CENTER_VERTICAL);
		param.leftMargin = screen.x * 10/480;
		
		if (select_item != null){
			updown = new ImageView(this);
			isShow = false;
			layout.addView(updown);
			updown.setImageDrawable(downDrawable);
			RelativeLayout.LayoutParams uparam = (RelativeLayout.LayoutParams)updown.getLayoutParams();
			uparam.addRule(RelativeLayout.CENTER_VERTICAL);
			uparam.addRule(RelativeLayout.RIGHT_OF,0x1999);
			layout.setOnTouchListener(onTouchListener);
			layout.setOnClickListener(new SelectionOnClickListener());
		}
		r_texts = edit;
		layout.setBackgroundResource(R.drawable.questionnaire_bar_normal);
		return layout;
	}
	
	private View createTitleView(){
		
		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.titlebar, null);
		TextView text = (TextView) layout.findViewById(R.id.titlebar_text);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		text.setTypeface(wordTypefaceBold);
		text.setText(R.string.emotion_manage_title);
		
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
	
	private class ChangeTextOnClickListener implements View.OnClickListener{

		private String str;
		public ChangeTextOnClickListener(String str){
			this.str = str;
		}
		
		@Override
		public void onClick(View v) {
			ClickLoggerLog.Log(getBaseContext(), ClickLogId.EMOTIONMANAGE_SELECTION);
			r_texts.setText(str);
			isShow = false;
			if (select_item != null){
				mainLayout.removeViews(3,select_item.length);
				updown.setImageDrawable(downDrawable);
			}
		}
		
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
		if (DrawableId!=0)
			icon.setImageDrawable(getResources().getDrawable(DrawableId));
		else
			icon.setImageDrawable(null);
		LinearLayout.LayoutParams iParam =(LinearLayout.LayoutParams) icon.getLayoutParams();
		iParam.rightMargin = iconMargin;
		
		layout.setOnClickListener(listener);
		layout.setOnTouchListener(onTouchListener);
		layout.setBackgroundResource(R.drawable.questionnaire_bar_normal);
		
		return layout;
	}
	
private View createIconView(int textStr, int DrawableId ,OnClickListener listener){
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		text.setTypeface(wordTypefaceBold);
		text.setTextColor(0xFF5c5c5c);
		text.setText(textStr);
		
		LinearLayout.LayoutParams tParam = (LinearLayout.LayoutParams)text.getLayoutParams();
		tParam.leftMargin = textSize;
		
		ImageView icon = (ImageView) layout.findViewById(R.id.question_icon);
		icon.setImageDrawable(getResources().getDrawable(DrawableId));
		LinearLayout.LayoutParams iParam =(LinearLayout.LayoutParams) icon.getLayoutParams();
		iParam.rightMargin = iconMargin;
		
		layout.setOnClickListener(listener);
		layout.setOnTouchListener(onTouchListener);
		layout.setBackgroundResource(R.drawable.questionnaire_bar_normal);
		
		return layout;
	}
	
	private class EndOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLoggerLog.Log(getBaseContext(), ClickLogId.EMOTIONMANAGE_SELECTION);
			boolean addAcc = db.insertEmotionManage(emotion, r_type, reason);
			if (addAcc)
				CustomToast.generateToast(activity, R.string.emotion_manage_end_toast, 1, screen);
			else
				CustomToast.generateToast(activity, R.string.emotion_manage_end_toast, 0, screen);
			activity.finish();
		}
	}
	
	private class EmotionOnClickListener implements View.OnClickListener{
		
		int _emotion;
		EmotionOnClickListener(int _emotion){
			this._emotion = _emotion;
		}
		@Override
		public void onClick(View v) {
			ClickLoggerLog.Log(getBaseContext(), ClickLogId.EMOTIONMANAGE_SELECTION);
			state = 1;
			emotion = _emotion;
			setQuestionType();
		}
	}
	
	private class RelatedOnClickListener implements View.OnClickListener{
		
		private int type;
				
		public RelatedOnClickListener(int type){
			this.type = type;
		}
		
		@Override
		public void onClick(View v) {
			ClickLoggerLog.Log(getBaseContext(), ClickLogId.EMOTIONMANAGE_SELECTION);
			state = 2;
			r_type = type;
			setQuestionEdit();
		}
	}
	
	private class EditedOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLoggerLog.Log(getBaseContext(), ClickLogId.EMOTIONMANAGE_SELECTION);
			state = 3;
			setQuestionEnd();
		}
	}
	
	private class ItemOnTouchListener implements View.OnTouchListener{

		private Rect rect;
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int e = event.getAction();
			switch(e){
				case MotionEvent.ACTION_MOVE:
					if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY()))
						v.setBackgroundResource(R.drawable.questionnaire_bar_normal);
					break;
				case MotionEvent.ACTION_UP:
					v.setBackgroundResource(R.drawable.questionnaire_bar_normal);
					break;
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundResource(R.drawable.questionnaire_bar_selected);
					rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
					break;
			}
			return false;
		}
	}
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK){
			ClickLoggerLog.Log(getBaseContext(), ClickLogId.EMOTIONMANAGE_RETURN_BUTTON);
			if (state == 0){
				if (endToast!=null)
					endToast.cancel();
				endToast = Toast.makeText(this,R.string.emotion_manage_toast, Toast.LENGTH_SHORT);
				endToast.show();
				--state;
			}else if (state == -1)
				return super.onKeyDown(keyCode, event);
			else{
				--state;
				if (state == 0)
					setQuestionEmotion();
				else if (state == 1)
					setQuestionType();
				else if (state == 2)
					setQuestionEdit();
				else if (state == 3)
					setQuestionEnd();
			}
		}
		
		return false;
	}
	
	
	private boolean isShow = false;
	private class SelectionOnClickListener implements View.OnClickListener{

		
		
		@Override
		public void onClick(View v) {
			if (select_item == null)
				return;
			if (isShow){
				mainLayout.removeViews(3,select_item.length);
				updown.setImageDrawable(downDrawable);
			}else{
				for (int i=0;i<select_item.length;++i){
					View vv = createIconView(select_item[i], 0,new ChangeTextOnClickListener(select_item[i]));
					mainLayout.addView(vv, 3+i);
				}
				updown.setImageDrawable(upDrawable);
			}
			isShow = !isShow;
			
		}
		
	}
}
