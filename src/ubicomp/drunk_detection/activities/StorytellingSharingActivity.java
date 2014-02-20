package ubicomp.drunk_detection.activities;

import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.ui.CustomToast;
import ubicomp.drunk_detection.ui.CustomToastSmall;
import ubicomp.drunk_detection.ui.ScreenSize;
import ubicomp.drunk_detection.ui.TextSize;
import ubicomp.drunk_detection.ui.Typefaces;
import data.database.QuestionDB;
import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
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

public class StorytellingSharingActivity extends Activity {

	private LayoutInflater inflater;
	
	private int titleSize;
	private int iconMargin;
	private Point screen;
	private Typeface wordTypefaceBold;

	private LinearLayout mainLayout;
	
	private Activity activity;
	
	private ItemOnTouchListener onTouchListener;
	
	private EditText who_texts, time_texts, content_texts;
	
	private QuestionDB db;
	private static final int TOTAL_BLOCK = 15;	
	private int state = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_storytelling_sharing);
		
		who_texts = null;
		time_texts = null;
		
		screen = ScreenSize.getScreenSize(getBaseContext());
		
		this.activity = this;
		db = new QuestionDB(activity);
		mainLayout = (LinearLayout) this.findViewById(R.id.storytelling_main_layout);
		inflater = LayoutInflater.from(activity);
		wordTypefaceBold = Typefaces.getWordTypefaceBold(this);
		
		mainLayout.removeAllViews();
		onTouchListener = new ItemOnTouchListener();
		
		titleSize = TextSize.smallTitleTextSize(activity);
		iconMargin = screen.x * 33/480;
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("LatestStorytellingSharingTime", System.currentTimeMillis());
		edit.commit();
		
	}

	@Override
	protected void onResume(){
		super.onResume();
		setList();
	}
	
	private void setList(){
		mainLayout.removeAllViews();

		View title = createTitleView();
		mainLayout.addView(title);
		LinearLayout.LayoutParams titleparam =(LinearLayout.LayoutParams) title.getLayoutParams();
		titleparam.height = screen.x*245/1080;
		
		String str = getResources().getString(R.string.storytelling_help1);;

		View who_text_v = createTextView(str);
		mainLayout.addView(who_text_v);
		
		View whov = createEditView(0);
		mainLayout.addView(whov);
		
		View time_text_v = createTextView(R.string.storytelling_help2);
		mainLayout.addView(time_text_v);
		
		View timev = createEditView(1);
		mainLayout.addView(timev);
		
		View content_text_v = createTextView(R.string.storytelling_help3);
		mainLayout.addView(content_text_v);
		
		View content_v = createEditView(2);
		mainLayout.addView(content_v);
		
		View endv=createIconView(R.string.ok,R.drawable.questionnaire_item_ok,new EndOnClickListener());
		mainLayout.addView(endv);
		
		int rest_block = TOTAL_BLOCK  - mainLayout.getChildCount();
		for (int i=0;i<rest_block;++i){
			View v = createBlankView();
			mainLayout.addView(v);
		}
	}
	
	private View createTextView(String textStr){
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize );
		text.setTypeface(wordTypefaceBold);
		text.setTextColor(0xFF777777);
		text.setText(textStr);
		
		LinearLayout.LayoutParams tParam = (LinearLayout.LayoutParams)text.getLayoutParams();
		tParam.leftMargin = titleSize;
		
		layout.setBackgroundResource(R.drawable.questionnaire_bar_question);
		
		return layout;
	}
	
private View createTextView(int textStr){
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize );
		text.setTypeface(wordTypefaceBold);
		text.setTextColor(0xFF777777);
		text.setText(textStr);
		
		LinearLayout.LayoutParams tParam = (LinearLayout.LayoutParams)text.getLayoutParams();
		tParam.leftMargin = titleSize;
		
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
		edit.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize*4/3);
		edit.setTypeface(wordTypefaceBold);
		edit.setHint(R.string.emotion_manage_input);
		final long ClickLog;
		if (type==0)
			ClickLog = ClickLogId.STORYTELLING_SHARE_EDIT_NAME;
		else
			ClickLog = ClickLogId.STORYTELLING_SHARE_EDIT_TIME;
		edit.addTextChangedListener(
			new TextWatcher(){
				@Override
				public void afterTextChanged(Editable arg0) {	}
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {}
				@Override
				public void onTextChanged(CharSequence arg0, int arg1,	int arg2, int arg3) {
					ClickLogger.Log(getBaseContext(), ClickLog);					
				}
			});
		
		edit.setId(0x1999 + type);
		layout.addView(edit);
		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)edit.getLayoutParams();
		param.addRule(RelativeLayout.CENTER_VERTICAL);
		param.leftMargin = screen.x * 10/480;
		if (type == 0)
			who_texts = edit;
		else if (type == 1) {
			time_texts = edit;
			time_texts.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
			edit.setInputType(InputType.TYPE_CLASS_NUMBER);
		}else if (type == 2){
			content_texts = edit;
			edit.setMaxEms(200);
		}
		
		layout.setBackgroundResource(R.drawable.questionnaire_bar_normal);
		return layout;
	}
	
	private View createTitleView(){
		
		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.titlebar, null);
		TextView text = (TextView) layout.findViewById(R.id.titlebar_text);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize );
		text.setTypeface(wordTypefaceBold);
		text.setText(R.string.storytelling_share_title);
		
		RelativeLayout.LayoutParams tParam = (RelativeLayout.LayoutParams)text.getLayoutParams();
		tParam.leftMargin = titleSize;
		
		ImageView icon = (ImageView) layout.findViewById(R.id.titlebar_icon);
		RelativeLayout.LayoutParams iParam =(RelativeLayout.LayoutParams) icon.getLayoutParams();
		iParam.leftMargin = titleSize;
		
		return layout;
	}
	
	private View createBlankView(){
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		layout.setBackgroundResource(R.drawable.questionnaire_bar_normal);
		return layout;
	}
	
private View createIconView(int textStr, int DrawableId ,OnClickListener listener){
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize );
		text.setTypeface(wordTypefaceBold);
		text.setTextColor(0xFF5c5c5c);
		text.setText(textStr);
		
		LinearLayout.LayoutParams tParam = (LinearLayout.LayoutParams)text.getLayoutParams();
		tParam.leftMargin = titleSize;
		
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
			ClickLogger.Log(getBaseContext(), ClickLogId.STORYTELLING_SHARE_END);
			String who = who_texts.getText().toString();
			String time_str = time_texts.getText().toString();

			
			if (who.length() == 0 && time_str.length() == 0){
				CustomToastSmall.generateToast(getBaseContext(),R.string.storytelling_check_toast3);
				state = 0;
				return;
			}else if (who.length() == 0){
				CustomToastSmall.generateToast(getBaseContext(),R.string.storytelling_check_toast1);
				state = 0;
				return;
			}else if (time_str.length() == 0){
				CustomToastSmall.generateToast(getBaseContext(),R.string.storytelling_check_toast2);
				state = 0;
				return;
			}
			int time = Integer.valueOf(time_str);
			
			boolean getSHC = db.insertStorytellingUsage(who,time,content_texts.getText().toString());
			
			if (getSHC)
				CustomToast.generateToast(getBaseContext(), R.string.storytelling_end_toast, 1);
			else
				CustomToast.generateToast(getBaseContext(), R.string.storytelling_end_toast, 0);
			
			long cur_time = System.currentTimeMillis();
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			SharedPreferences.Editor edit= sp.edit();
			edit.putLong("share_storytelling_time", cur_time);
			edit.commit();
			activity.finish();
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
			ClickLogger.Log(getBaseContext(), ClickLogId.STORYTELLING_SHARE_RETURN_BUTTON);
			if (state == 0){
				CustomToastSmall.generateToast(this,R.string.storytelling_leave_toast);
				--state;
			}else if (state == -1)
				return super.onKeyDown(keyCode, event);
		}
		
		return false;
	}
	
	
}
