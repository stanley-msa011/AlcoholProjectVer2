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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EmotionManageActivity extends Activity {

	private LayoutInflater inflater;
	
	private int textSize;
	private int height;
	private int icon_size;
	private Point screen;
	private Typeface wordTypeface, wordTypefaceBold;

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
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		wordTypeface = Typeface.createFromAsset(activity.getAssets(), "fonts/dfheistd-w3.otf");
		wordTypefaceBold = Typeface.createFromAsset(activity.getAssets(), "fonts/dfheistd-w5.otf");
		
		mainLayout.removeAllViews();
		onTouchListener = new ItemOnTouchListener();
		
		textSize = screen.x * 72/1080;
		height =  screen.x * 202/1080;
		icon_size = screen.x * 140/1080;
		
		View tv = createTextView("您現在的情緒是：");
		mainLayout.addView(tv);
		LinearLayout.LayoutParams tvparam =(LinearLayout.LayoutParams) tv.getLayoutParams();
		tvparam.height = height;
		
		for (int i=0;i<emotion_texts.length;++i){
			View v = createIconView(emotion_texts[i],EMOTION_DRAWABLE_ID[i],new EmotionOnClickListener(i+1));
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
	
	private View createEditView(int type){
		
		RelativeLayout layout = new RelativeLayout(mainLayout.getContext());
		
		EditText edit = new EditText(activity);
		edit.setBackground(mainLayout.getContext().getResources().getDrawable(R.drawable.questionnaire_input));
		edit.setTextColor(0xFF000000);
		edit.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize *3/4);
		edit.setTypeface(wordTypeface);
		
		layout.addView(edit);
		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)edit.getLayoutParams();
		param.width = screen.x * 963/1080;
		param.height = screen.x * 112/1080;
		param.addRule(RelativeLayout.CENTER_VERTICAL);
		
		ImageView icon = new ImageView(mainLayout.getContext());
		layout.addView(icon);
		icon.setImageResource(R.drawable.questionnaire_item_ok);
		RelativeLayout.LayoutParams iParam =(RelativeLayout.LayoutParams) icon.getLayoutParams();
		iParam.width = iParam.height = icon_size;
		iParam.addRule(RelativeLayout.CENTER_VERTICAL);
		iParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		icon.setOnClickListener(new ExtendOnClickListener(type));
		
		r_texts = edit;
		layout.setBackgroundResource(R.drawable.questionnaire_bar_normal);
		
		return layout;
	}
	
	private class ExtendOnClickListener implements View.OnClickListener{

		String[] select;
		boolean extended = false;
		public ExtendOnClickListener(int type){
			select = db.getInsertedReason(type);
		}
		
		@Override
		public void onClick(View v) {
			if (extended){
				int min = 2;
				int max = mainLayout.getChildCount()-4;
				mainLayout.removeViews(min, max);
				extended = false;
				return;
			}
			if (select == null)
				return;
			for (int i=0;i<select.length;++i){
				View vv = createIconView(select[i], R.drawable.questionnaire_item_ok,new ChangeTextOnClickListener(select[i]));
				mainLayout.addView(vv, 2+i);
				LinearLayout.LayoutParams vvParam =(LinearLayout.LayoutParams) vv.getLayoutParams();
				vvParam.height = height;
			}
			extended = true;
		}
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
		@Override
		public void onClick(View v) {
			db.insertEmotionManage(emotion, r_type, reason);
			activity.finish();
		}
	}
	
	private class EmotionOnClickListener implements View.OnClickListener{
		
		int r;
		EmotionOnClickListener(int r){
			this.r = r;
		}
		
		
		@Override
		public void onClick(View v) {
			mainLayout.removeAllViews();
			
			View tv = createTextView("這個感覺跟什麼有關：");
			mainLayout.addView(tv);
			LinearLayout.LayoutParams tvparam =(LinearLayout.LayoutParams) tv.getLayoutParams();
			tvparam.height = height;
			
			emotion = r;
			
			for (int i=0;i<related_texts.length;++i){
				View vv = createIconView(related_texts[i],RELATED_DRAWABLE_ID[i],new RelatedOnClickListener(i+1));
				mainLayout.addView(vv);
				LinearLayout.LayoutParams param =(LinearLayout.LayoutParams) vv.getLayoutParams();
				param.height = height;
			}
		}
	}
	
	private class RelatedOnClickListener implements View.OnClickListener{
		
		private int type;
		
				
		public RelatedOnClickListener(int type){
			this.type = type;
		}
		
		@Override
		public void onClick(View v) {
			mainLayout.removeAllViews();
			
			r_type = type;
			
			String str = "請寫下正在影響你的"+related_texts[type-1];

			View tv = createTextView(str);
			mainLayout.addView(tv);
			LinearLayout.LayoutParams tvparam =(LinearLayout.LayoutParams) tv.getLayoutParams();
			tvparam.height = height;
			
			View edv = createEditView(type);
			mainLayout.addView(edv);
			LinearLayout.LayoutParams edvParam =(LinearLayout.LayoutParams) edv.getLayoutParams();
			edvParam.height = height;
			
			View ev = createTextView("這是影響您停酒的因素\n但您仍可堅持停酒");
			mainLayout.addView(ev);
			LinearLayout.LayoutParams evParam =(LinearLayout.LayoutParams) ev.getLayoutParams();
			evParam.height = height;
			
			View vv=createIconView("確定",R.drawable.questionnaire_item_ok,new EditedOnClickListener());
			mainLayout.addView(vv);
			LinearLayout.LayoutParams vvParam =(LinearLayout.LayoutParams) vv.getLayoutParams();
			vvParam.height = height;
		}
	}
	
	private class EditedOnClickListener implements View.OnClickListener{
		
		
		@Override
		public void onClick(View v) {
			mainLayout.removeAllViews();

			if (r_texts!=null)
				reason = r_texts.getText().toString();
			
			String str = "請繼續加油!";
			View tv = createTextView(str);
			mainLayout.addView(tv);
			LinearLayout.LayoutParams tvparam =(LinearLayout.LayoutParams) tv.getLayoutParams();
			tvparam.height = height;
			View vv = createIconView("結束",R.drawable.questionnaire_item_ok,new EndOnClickListener());
			mainLayout.addView(vv);
			LinearLayout.LayoutParams vvparam =(LinearLayout.LayoutParams) vv.getLayoutParams();
			vvparam.height = height;
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
