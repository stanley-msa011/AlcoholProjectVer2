package ubicomp.drunk_detection.activities;

import ubicomp.drunk_detection.activities.R;
import database.QuestionDB;
import android.os.Bundle;
import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class EmotionManageActivity extends Activity {

	private LayoutInflater inflater;
	
	private int textSize = 24;
	private Typeface wordTypeface;

	private LinearLayout mainLayout;
	
	private Activity activity;
	
	private ItemOnTouchListener onTouchListener;
	
	private int emotion, r_type;
	private String reason;
	private EditText r_texts;
	
	private static final int[] EMOTION_DRAWABLE_ID = {
		R.drawable.emotion_icon_1_e1,
		R.drawable.emotion_icon_1_e2,
		R.drawable.emotion_icon_1_e3,
		R.drawable.emotion_icon_1_e4,
		R.drawable.emotion_icon_1_e5,
		R.drawable.emotion_icon_1_e6,
		R.drawable.emotion_icon_1_e7,
	};
	
	private static final String[] emotion_texts = {
		"喜",	"怒",	"哀",	"傷",	"悲",	"恐",	"驚"
	} ;
	
	private OnClickListener[] emotionClickListeners = {
			new EmotionOnClickListener(0),
			new EmotionOnClickListener(1),
			new EmotionOnClickListener(2),
			new EmotionOnClickListener(3),
			new EmotionOnClickListener(4),
			new EmotionOnClickListener(5),
			new EmotionOnClickListener(6)
	};
	
	private static final int[] RELATED_DRAWABLE_ID = {
		R.drawable.emotion_icon_2_who,
		R.drawable.emotion_icon_2_how,
		R.drawable.emotion_icon_2_what,
		R.drawable.emotion_icon_2_where
	};
	
	private static final String[] related_texts = {
		"人",	"事",	"物",	"現在情境"
	} ;
	
	private OnClickListener[] relatedClickListeners = {
			new RelatedOnClickListener(0),
			new RelatedOnClickListener(1),
			new RelatedOnClickListener(2),
			new RelatedOnClickListener(3)
	};
	
	private TextView text;
	QuestionDB db;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emotion_manage);
		setTitle("心情管理");
		emotion =  r_type = -1;
		reason = "";
		r_texts = null;
		
	}

	@Override
	protected void onResume(){
		super.onResume();
		this.activity = this;
		db = new QuestionDB(activity);
		mainLayout = (LinearLayout) this.findViewById(R.id.emotion_main_layout);
		text = new TextView(activity);
		inflater = LayoutInflater.from(activity);
		wordTypeface = Typeface.createFromAsset(activity.getAssets(), "fonts/dfheistd-w3.otf");

		
		mainLayout.removeAllViews();
		onTouchListener = new ItemOnTouchListener();
		
		text.setText("您現在的情緒是:");
		text.setTextColor(0xFF000000);
		text.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
		
		mainLayout.addView(text);
		
		for (int i=0;i<emotion_texts.length;++i){
			mainLayout.addView(createIconView(emotion_texts[i],EMOTION_DRAWABLE_ID[i],emotionClickListeners[i]));
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
			
			text.setText("這個感覺跟什麼有關:");
			mainLayout.addView(text);

			emotion = r;
			
			for (int i=0;i<related_texts.length;++i){
				mainLayout.addView(createIconView(related_texts[i],RELATED_DRAWABLE_ID[i],relatedClickListeners[i]));
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
			
			String str = "請寫下正在影響你的"+related_texts[type];
			text.setText(str);
			mainLayout.addView(text);

			EditText edit = new EditText(activity);
			edit.setTextSize(textSize);
			edit.setBackgroundColor(0xCCDDDDDD);
			edit.setTextColor(0xFF000000);
			edit.setTypeface(wordTypeface);
			
			mainLayout.addView(edit);
			
			r_texts = edit;
			
			TextView helpText = new TextView(activity);
			helpText.setText("參考選項:");
			helpText.setTextColor(0xFF000000);
			helpText.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
			
			mainLayout.addView(helpText);
			
			Spinner spinner = new Spinner(getBaseContext());
			SpinnerAdapter adapter = new RSpinnerAdapter(type);
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(new OnSpinnerItemSelectListener());
			mainLayout.addView(spinner);
			
			TextView endText = new TextView(activity);
			endText.setText("這是影響您此時停酒的因素\n但您仍可堅持停酒");
			endText.setTextColor(0xFF000000);
			endText.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
			
			mainLayout.addView(endText);
			
			mainLayout.addView(createIconView("確定",R.drawable.question_done,new EditedOnClickListener()));
		}
	}
	
	private class EditedOnClickListener implements View.OnClickListener{
		
		
		@Override
		public void onClick(View v) {
			mainLayout.removeAllViews();

			if (r_texts!=null)
				reason = r_texts.getText().toString();
			
			String str = "請繼續加油!";
			text.setText(str);
			mainLayout.addView(text);
			mainLayout.addView(createIconView("結束",R.drawable.question_done,new EndOnClickListener()));

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
	
	private class RSpinnerAdapter implements SpinnerAdapter{

		private String[] select;
		
		RSpinnerAdapter(int type){
			select = db.getInsertedReason(type);
		}
		
		@Override
		public int getCount() {
			if (select == null)
				return 0;
			return select.length;
		}

		@Override
		public Object getItem(int arg0) {
			return select[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView t = new TextView(activity);
			t.setText(String.valueOf(select[position]));
			t.setTextSize(textSize);
			t.setTypeface(wordTypeface);
			t.setBackgroundColor(0xFFFFFFFF);
			t.setTextColor(0xFF000000);
			t.setPadding(0, 20, 0, 20);
			return t;
		}

		@Override
		public int getViewTypeCount() {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isEmpty() {
			return (select==null || select.length == 0);
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
		}

		@Override
		public View getDropDownView(int arg0, View arg1, ViewGroup arg2) {
			TextView t = new TextView(activity);
			t.setText(String.valueOf(select[arg0]));
			t.setTextSize(textSize);
			t.setTypeface(wordTypeface);
			t.setBackgroundColor(0xFFFFFFFF);
			t.setTextColor(0xFF000000);
			t.setPadding(0, 20, 0, 20);
			return t;
		}
		
	}
	
	
	private class OnSpinnerItemSelectListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if (r_texts !=null){
				TextView t = (TextView) arg1;
				r_texts.setText(t.getText().toString());
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			if (r_texts !=null){
				r_texts.setText("");
			}
			
		}
	}
	
}
