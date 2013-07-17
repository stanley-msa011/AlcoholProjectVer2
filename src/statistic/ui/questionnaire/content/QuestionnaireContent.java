package statistic.ui.questionnaire.content;

import java.util.ArrayList;

import statistic.ui.QuestionMsgBox;
import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.activities.R;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

abstract public class QuestionnaireContent {
	
	protected LinearLayout questionnaireLayout;
	private Drawable choiceDrawable, choiceSelectedDrawable;
	protected ArrayList<String>seq;
	protected ArrayList <QuestionnaireContent> contentSeq;
	protected QuestionMsgBox msgBox;
	private int textSize;
	private int contentSideMargin, contentMargin;
	private Context context;
	//private ItemOnTouchListener itemOnTouchListener;
	private Typeface wordTypefaceBold;
	private int itemHeight;
	
	public QuestionnaireContent(
			QuestionMsgBox msgBox
			){
		this.questionnaireLayout = msgBox.getQuestionnaireLayout();
		this.seq = msgBox.getClickSequence();
		this.contentSeq = msgBox.getQuestionSequence();
		this.choiceDrawable = msgBox.getChoiceDrawable();
		this.choiceSelectedDrawable = msgBox.getChoiceSelectedDrawable();
		this.msgBox = msgBox;
		this.context = msgBox.getContext();
		int x = FragmentTabs.getScreenWidth();	
		textSize = x * 21/480;
		contentSideMargin = x * 40/480;
		contentMargin = x * 10/480;
		itemHeight = x * 60/480;
		//this.itemOnTouchListener = new ItemOnTouchListener();
		wordTypefaceBold = msgBox.getTypeface();
	}
	
	public void onPush(){
		questionnaireLayout.removeAllViews();
		setContent();
	}
	
	abstract protected void setContent();
	
	abstract public void onPop();
	
	protected void setHelp(String str){
		/*
		RelativeLayout v = new RelativeLayout(context);
		TextView text = new TextView(context);
		text.setText(str);
		text.setTextColor(0xFF573b3b);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		text.setTypeface(wordTypefaceBold);
		
		v.addView(text);
		
		RelativeLayout.LayoutParams tParam = (RelativeLayout.LayoutParams) text.getLayoutParams();
		tParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		tParam.addRule(RelativeLayout.CENTER_VERTICAL);
		tParam.leftMargin = contentSideMargin;

		questionnaireLayout.addView(v);
		LinearLayout.LayoutParams vParam = (LinearLayout.LayoutParams)v.getLayoutParams();
		vParam.topMargin = vParam.bottomMargin = contentMargin;
		*/
		msgBox.setHelpMessage(str);
	}
	
	public static final int QUESTION_IMAGE_ID = 0x00;
	public static final int QUESTION_TEXT_ID = 0x01;
	
	protected void setSelectItem(String str, View.OnClickListener listener){
		RelativeLayout v = new RelativeLayout(context);
		TextView text = new TextView(context);
		text.setText(str);
		text.setTextColor(0xFF573b3b);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		text.setId(QUESTION_TEXT_ID);
		text.setTypeface(wordTypefaceBold);
		
		ImageView button = new ImageView(context);
		button.setImageDrawable(choiceDrawable);
		button.setId(QUESTION_IMAGE_ID);
		
		v.addView(text);
		v.addView(button);
		
		RelativeLayout.LayoutParams tParam = (RelativeLayout.LayoutParams) text.getLayoutParams();
		tParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		tParam.addRule(RelativeLayout.CENTER_VERTICAL);
		tParam.leftMargin = contentSideMargin;
		
		RelativeLayout.LayoutParams bParam = (RelativeLayout.LayoutParams) button.getLayoutParams();
		bParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		bParam.addRule(RelativeLayout.CENTER_VERTICAL);
		bParam.rightMargin = contentSideMargin;
		
		v.setOnClickListener(listener);
		//v.setOnTouchListener(itemOnTouchListener);
		
		questionnaireLayout.addView(v);
		LinearLayout.LayoutParams vParam = (LinearLayout.LayoutParams)v.getLayoutParams();
		vParam.topMargin = vParam.bottomMargin = contentMargin;
		vParam.height = 	itemHeight;
	}
	
	public void cleanSelection(){
		int count = questionnaireLayout.getChildCount();
		for (int i=0;i<count;++i){
			ViewGroup v = (ViewGroup) questionnaireLayout.getChildAt(i);
			ImageView img = (ImageView) v.findViewById(QUESTION_IMAGE_ID);
			img.setImageDrawable(choiceDrawable);
		}
	}
	
	/*
	private class ItemOnTouchListener implements View.OnTouchListener{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int e = event.getAction();
			RelativeLayout r = (RelativeLayout) v;
			ImageView vi = (ImageView) r.findViewById(imageId);
			switch(e){
				case MotionEvent.ACTION_OUTSIDE:
					vi.setImageDrawable(choiceDrawable);
					break;
				case MotionEvent.ACTION_MOVE:
					vi.setImageDrawable(choiceSelectedDrawable);
					break;
				case MotionEvent.ACTION_UP:
					vi.setImageDrawable(choiceDrawable);
					break;
				case MotionEvent.ACTION_DOWN:
					vi.setImageDrawable(choiceSelectedDrawable);
					break;
			}
			return false;
		}
	}*/
}
