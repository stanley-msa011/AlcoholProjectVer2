package statistic.ui.questionnaire.content;

import java.util.ArrayList;

import statistic.ui.QuestionMsgBox;
import ubicomp.drunk_detection.activities.FragmentTabs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

abstract public class QuestionnaireContent {
	
	protected LinearLayout questionnaireLayout;
	private Bitmap choiceBmp, choiceSelectedBmp;
	protected ArrayList<String>seq;
	protected ArrayList <QuestionnaireContent> contentSeq;
	protected QuestionMsgBox msgBox;
	private int textSize;
	private int contentSideMargin, contentMargin;
	private Context context;
	private ItemOnTouchListener itemOnTouchListener;
	private Typeface wordTypeface;
	private int itemHeight;
	
	public QuestionnaireContent(
			QuestionMsgBox msgBox
			){
		this.questionnaireLayout = msgBox.getQuestionnaireLayout();
		this.seq = msgBox.getClickSequence();
		this.contentSeq = msgBox.getQuestionSequence();
		this.choiceBmp = msgBox.getChoiceBmp();
		this.choiceSelectedBmp = msgBox.getChoiceSelectedBmp();
		this.msgBox = msgBox;
		this.context = msgBox.getContext();
		int x = FragmentTabs.getScreenWidth();	
		textSize = x * 63/1080;
		contentSideMargin = x * 80/1080;
		contentMargin = x * 40/1080;
		itemHeight = x * 150/1080;
		this.itemOnTouchListener = new ItemOnTouchListener();
		wordTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dfheistd-w3.otf");
	}
	
	public void onPush(){
		questionnaireLayout.removeAllViews();
		setContent();
	}
	
	abstract protected void setContent();
	
	abstract public void onPop();
	
	protected void setHelp(String str){
		
		RelativeLayout v = new RelativeLayout(context);
		TextView text = new TextView(context);
		text.setText(str);
		text.setTextColor(0xFF573b3b);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		text.setTypeface(wordTypeface);
		
		v.addView(text);
		
		RelativeLayout.LayoutParams tParam = (RelativeLayout.LayoutParams) text.getLayoutParams();
		tParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		tParam.addRule(RelativeLayout.CENTER_VERTICAL);
		tParam.leftMargin = contentSideMargin;

		questionnaireLayout.addView(v);
		LinearLayout.LayoutParams vParam = (LinearLayout.LayoutParams)v.getLayoutParams();
		vParam.topMargin = vParam.bottomMargin = contentMargin;
	}
	
	private static final int imageId = 0x00;
	private static final int textId = 0x01;
	
	protected void setSelectItem(String str, View.OnClickListener listener){
		RelativeLayout v = new RelativeLayout(context);
		TextView text = new TextView(context);
		text.setText(str);
		text.setTextColor(0xFF573b3b);
		text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		text.setId(textId);
		text.setTypeface(wordTypeface);
		
		ImageView button = new ImageView(context);
		button.setImageBitmap(choiceBmp);
		button.setId(imageId);
		
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
		v.setOnTouchListener(itemOnTouchListener);
		
		questionnaireLayout.addView(v);
		LinearLayout.LayoutParams vParam = (LinearLayout.LayoutParams)v.getLayoutParams();
		//vParam.topMargin = vParam.bottomMargin = contentMargin;
		vParam.height = 	itemHeight;
	}
	
	private class ItemOnTouchListener implements View.OnTouchListener{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int e = event.getAction();
			RelativeLayout r = (RelativeLayout) v;
			ImageView vi = (ImageView) r.findViewById(imageId);
			switch(e){
				case MotionEvent.ACTION_OUTSIDE:
					vi.setImageBitmap(choiceBmp);
					break;
				case MotionEvent.ACTION_MOVE:
					vi.setImageBitmap(choiceSelectedBmp);
					break;
				case MotionEvent.ACTION_UP:
					vi.setImageBitmap(choiceBmp);
					break;
				case MotionEvent.ACTION_DOWN:
					vi.setImageBitmap(choiceSelectedBmp);
					break;
			}
			return false;
		}
	}
}
