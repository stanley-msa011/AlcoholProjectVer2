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
	
	private static final int[] EMOTION_DRAWABLE_ID = {
		R.drawable.questionnaire_item_e1,
		R.drawable.questionnaire_item_e2,
		R.drawable.questionnaire_item_e3,
		R.drawable.questionnaire_item_e4,
		R.drawable.questionnaire_item_e5,
		R.drawable.questionnaire_item_e6,
		R.drawable.questionnaire_item_e7,
	};
	
	private static final String[] emotion_texts = {
		"喜",	"怒",	"哀",	"傷",	"悲",	"恐",	"驚"
	} ;
	
	private static final int[] RELATED_DRAWABLE_ID = {
		R.drawable.questionnaire_item_c1,
		R.drawable.questionnaire_item_c2,
		R.drawable.questionnaire_item_c3,
		R.drawable.questionnaire_item_c4
	};
	
	private static final String[] related_texts = {
		"人",	"事",	"物",	"現在情境"
	} ;
	
	QuestionDB db;
	
	private int state = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_emotion_manage);
		
		emotion =  r_type = -1;
		reason = "";
		r_texts = null;
		
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
		db = new QuestionDB(activity);
		mainLayout = (LinearLayout) this.findViewById(R.id.emotion_main_layout);
		inflater = LayoutInflater.from(activity);
		wordTypefaceBold = Typeface.createFromAsset(activity.getAssets(), "fonts/DFLiHeiStd-W5.otf");
		
		mainLayout.removeAllViews();
		onTouchListener = new ItemOnTouchListener();
		
		textSize = screen.x * 24/480;
		iconMargin = screen.x * 33/480;
		
		setQuestionEmotion();
	}
	
	private void setQuestionEmotion(){
		mainLayout.removeAllViews();
		
		View title = createTitleView();
		mainLayout.addView(title);
		LinearLayout.LayoutParams titleparam =(LinearLayout.LayoutParams) title.getLayoutParams();
		titleparam.height = screen.x*244/1080;
		
		View tv = createTextView("您現在的情緒是：");
		mainLayout.addView(tv);
		
		for (int i=0;i<emotion_texts.length;++i){
			View v = createIconView(emotion_texts[i],EMOTION_DRAWABLE_ID[i],new EmotionOnClickListener(i+1));
			mainLayout.addView(v);
		}
		
		int rest_block = 10 - mainLayout.getChildCount();
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
		
		View tv = createTextView("這個感覺跟什麼有關：");
		mainLayout.addView(tv);
		
		for (int i=0;i<related_texts.length;++i){
			View vv = createIconView(related_texts[i],RELATED_DRAWABLE_ID[i],new RelatedOnClickListener(i+1));
			mainLayout.addView(vv);
		}
		
		int rest_block = 10 - mainLayout.getChildCount();
		for (int i=0;i<rest_block;++i){
			View v = createBlankView();
			mainLayout.addView(v);
		}
	}
	
	private String[] select_item;
	
	private void setQuestionEdit(){
		mainLayout.removeAllViews();

		View title = createTitleView();
		mainLayout.addView(title);
		LinearLayout.LayoutParams titleparam =(LinearLayout.LayoutParams) title.getLayoutParams();
		titleparam.height = screen.x*230/1080;
		
		String str = "請寫下正在影響你的"+related_texts[r_type-1];

		View tv = createTextView(str);
		mainLayout.addView(tv);
		
		View edv = createEditView(r_type);
		mainLayout.addView(edv);
		
		View ev = createTextView("這是影響您停酒的因素\n但您仍可堅持停酒");
		mainLayout.addView(ev);
		
		View vv=createIconView("確定",R.drawable.questionnaire_item_ok,new EditedOnClickListener());
		mainLayout.addView(vv);
		
		select_item = db.getInsertedReason(r_type);
		if (select_item != null){
			for (int i=0;i<select_item.length;++i){
				View v = createIconView(select_item[i], 0,new ChangeTextOnClickListener(select_item[i]));
				mainLayout.addView(v);
			}
		}
		
		int rest_block = 10 - mainLayout.getChildCount();
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
		
		String str = "請繼續加油!";
		View tv = createTextView(str);
		mainLayout.addView(tv);
		View vv = createIconView("完成",R.drawable.questionnaire_item_ok,new EndOnClickListener());
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
	
	private View createEditView(int type){
		
		RelativeLayout layout = new RelativeLayout(mainLayout.getContext());
		
		EditText edit = new EditText(activity);
		edit.setBackground(mainLayout.getContext().getResources().getDrawable(R.drawable.questionnaire_input));
		edit.setTextColor(0xFF000000);
		edit.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize *3/4);
		edit.setTypeface(wordTypefaceBold);
		
		layout.addView(edit);
		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)edit.getLayoutParams();
		param.addRule(RelativeLayout.CENTER_VERTICAL);
		
		r_texts = edit;
		layout.setBackgroundResource(R.drawable.questionnaire_bar_normal);
		
		return layout;
	}
	
	private View createTitleView(){
		
		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.titlebar, null);
		TextView text = (TextView) layout.findViewById(R.id.titlebar_text);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize );
		text.setTypeface(wordTypefaceBold);
		text.setText("情緒管理");
		
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

		String str;
		public ChangeTextOnClickListener(String str){
			this.str = str;
		}
		
		@Override
		public void onClick(View v) {
			r_texts.setText(str);
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
		icon.setImageResource(DrawableId);
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
			db.insertEmotionManage(emotion, r_type, reason);
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
			state = 2;
			r_type = type;
			setQuestionEdit();
		}
	}
	
	private class EditedOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			state = 3;
			setQuestionEnd();
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
				Toast.makeText(mainLayout.getContext(), "確定放棄填答情緒管理？", Toast.LENGTH_LONG).show();
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
}
