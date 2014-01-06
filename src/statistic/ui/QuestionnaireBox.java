package statistic.ui;

import java.util.ArrayList;


import data.database.QuestionDB;
import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;
import statistic.ui.questionnaire.content.QuestionnaireContent;
import statistic.ui.questionnaire.content.Type0Content;
import statistic.ui.questionnaire.content.Type1Content;
import statistic.ui.questionnaire.content.Type2Content;
import statistic.ui.questionnaire.content.Type3Content;
import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.ui.CustomToast;
import ubicomp.drunk_detection.ui.ScreenSize;
import ubicomp.drunk_detection.ui.TextSize;
import ubicomp.drunk_detection.ui.Typefaces;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class QuestionnaireBox {

	private ArrayList<String> clickSequence;
	private ArrayList<QuestionnaireContent> contentSequence;
	
	private QuestionnaireBoxUpdater quesBoxUpdater;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	
	private RelativeLayout mainLayout;
	private View shadow;
	private LinearLayout questionLayout, questionAllLayout;
	private ImageView closeButton;
	private TextView help,next;
	private Drawable choiceDrawable, choiceSelectedDrawable;
	private Resources r;
	private Point screen;
	private QuestionDB db;
	private Typeface wordTypefaceBold;
	private int type;
	
	private LinearLayout.LayoutParams questionParam;
	
	public QuestionnaireBox(Context context,QuestionnaireBoxUpdater quesBoxUpdater,RelativeLayout mainLayout){
		this.context = context;
		this.quesBoxUpdater = quesBoxUpdater;
		this.r = context.getResources();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		shadow = new View(context);
		shadow.setBackgroundColor(0x99000000);
		screen = ScreenSize.getScreenSize(getContext());
		db = new QuestionDB(context);
		clickSequence = new ArrayList<String>();
		contentSequence = new ArrayList<QuestionnaireContent>();
		type = -1;
		
		setting();
	}
	
	private void setting(){
		
		wordTypefaceBold = Typefaces.getWordTypefaceBold(getContext());
		shadow.setVisibility(View.INVISIBLE);
		shadow.setKeepScreenOn(false);
		
		boxLayout = (RelativeLayout) inflater.inflate(R.layout.question_box_layout,null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		questionLayout = (LinearLayout) boxLayout.findViewById(R.id.question_layout);
		questionParam = (LinearLayout.LayoutParams) questionLayout.getLayoutParams();
		questionAllLayout = (LinearLayout) boxLayout.findViewById(R.id.question_all_layout);
		
		help = (TextView) boxLayout.findViewById(R.id.question_text);
		next = (TextView) boxLayout.findViewById(R.id.question_next);
		
		closeButton = (ImageView) boxLayout.findViewById(R.id.question_exit);
		int padding = screen.x * 20/480;
		closeButton.setPadding(padding, 0, 0, padding);
	}
	
	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	public void settingPreTask(){
		
		mainLayout.addView(shadow);
		mainLayout.addView(boxLayout);
		
		RelativeLayout.LayoutParams bgParam = (LayoutParams) shadow.getLayoutParams();
		if (Build.VERSION.SDK_INT>=8)
			bgParam.width = bgParam.height = LayoutParams.MATCH_PARENT;
		else
			bgParam.width = bgParam.height = LayoutParams.FILL_PARENT;
		RelativeLayout.LayoutParams boxParam = (LayoutParams) boxLayout.getLayoutParams();
		boxParam.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
		boxParam.width = screen.x * 348/480;
		RelativeLayout.LayoutParams qParam = (LayoutParams) questionAllLayout.getLayoutParams();
		qParam.topMargin = screen.x * 12/480;
		
		
		int textSize = TextSize.normalTextSize(context);
		help.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		help.setTypeface(wordTypefaceBold);
		RelativeLayout.LayoutParams hParam = (LayoutParams) help.getLayoutParams();
		hParam.leftMargin = screen.x * 40/480;
		
		next.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		next.setTypeface(wordTypefaceBold);
		RelativeLayout.LayoutParams nParam = (LayoutParams) next.getLayoutParams();
		nParam.rightMargin = screen.x * 40/480;
		nParam.height = textSize*2;
	}
	
	
	public void settingInBackground(){
		choiceDrawable = r.getDrawable(R.drawable.question_choice);
		choiceSelectedDrawable = r.getDrawable(R.drawable.question_choice_selection);
	}
	
	public  void settingPostTask(){
		closeButton.setOnClickListener(new ExitListener());
	}
	
	public void clear(){
		if (shadow != null)
			mainLayout.removeView(shadow);
		if (boxLayout !=null)
			mainLayout.removeView(boxLayout);
	}
	
	public void generateType0Box(){
		type = 0;
		contentSequence.clear();
		contentSequence.add(new Type0Content(this));
		contentSequence.get(contentSequence.size()-1).onPush();
	}
	
	public void generateType1Box(){
		type = 1;
		setNextButton("", null);
		contentSequence.clear();
		contentSequence.add(new Type1Content(this));
		contentSequence.get(contentSequence.size()-1).onPush();
	}
	
	public void generateType2Box(){
		type = 2;
		setNextButton("", null);
		contentSequence.clear();
		contentSequence.add(new Type2Content(this));
		contentSequence.get(contentSequence.size()-1).onPush();
	}
	
	public void generateType3Box(){
		type = 3;
		setNextButton("", null);
		contentSequence.clear();
		contentSequence.add(new Type3Content(this));
		contentSequence.get(contentSequence.size()-1).onPush();
	}
	
	public void generateNormalBox(){
		type = -1;
		setNextButton("", null);
		contentSequence.clear();
		contentSequence.add(new Type0Content(this));
		contentSequence.get(contentSequence.size()-1).onPush();
	}
	
	public void openBox(){
		quesBoxUpdater.enablePage(false);
		shadow.setVisibility(View.VISIBLE);
		shadow.setKeepScreenOn(true);
		boxLayout.setVisibility(View.VISIBLE);
		return;
}
	
	public void closeBox(){
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("latest_result", -1);
    	editor.commit();
    	quesBoxUpdater.enablePage(true);
    	shadow.setVisibility(View.INVISIBLE);
    	shadow.setKeepScreenOn(false);
		boxLayout.setVisibility(View.INVISIBLE);
		quesBoxUpdater.setQuestionAnimation();
		return;
	}
	
	public void closeBoxNull(){
		quesBoxUpdater.enablePage(true);
    	shadow.setVisibility(View.INVISIBLE);
    	shadow.setKeepScreenOn(false);
		boxLayout.setVisibility(View.INVISIBLE);
		return;
	}
	
	public Context getContext(){
		return context;
	}
	
	public Drawable getChoiceDrawable(){
		return choiceDrawable;
	}
	
	public Drawable getChoiceSelectedDrawable(){
		return choiceSelectedDrawable;
	}
	
	public LinearLayout getQuestionnaireLayout(){
		return questionLayout; 
	}
	
	public ArrayList<String> getClickSequence(){
		return clickSequence;
	}
	
	public ArrayList<QuestionnaireContent> getQuestionSequence(){
		return contentSequence;
	}
	
	public boolean insertSeq(){
		boolean addAcc = db.insertQuestionnaire(seq_toString(),type);
		Log.d("insert",seq_toString());
		return addAcc;
	}
	

	private String seq_toString(){
		int size = clickSequence.size();
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<size;++i)
			sb.append(clickSequence.get(i));
		return sb.toString();
	}
	
	public void setHelpMessage(String str){
		help.setText(str);
	}
	
	public void setHelpMessage(int str_id){
		help.setText(str_id);
	}
	
	public void setNextButton(String str, View.OnClickListener listener){
		next.setText(str);
		next.setOnClickListener(listener);
	}
	
	public void setNextButton(int str_id, View.OnClickListener listener){
		next.setText(str_id);
		next.setOnClickListener(listener);
	}
	
	public Typeface getTypeface(){
		return wordTypefaceBold;
	}
	
	public void cleanSelection(){
		int idx = contentSequence.size()-1;
		if (idx >=0)
			contentSequence.get(idx).cleanSelection();
	}
	
	public void showQuestionnaireLayout(boolean visible){
		if (visible)
			questionParam.height = LinearLayout.LayoutParams.WRAP_CONTENT;
		else
			questionParam.height = 0;
	}
	
	
	private class ExitListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			ClickLogger.Log(getContext(), ClickLogId.STATISTIC_QUESTION_CANCEL);
			clickSequence.clear();
			contentSequence.clear();
			quesBoxUpdater.enablePage(true);
			shadow.setVisibility(View.INVISIBLE);
			shadow.setKeepScreenOn(false);
			boxLayout.setVisibility(View.INVISIBLE);
		}
		
	}
	
	public void showEndOfQuestionnaire(boolean addAcc){
		if (addAcc)
			CustomToast.generateToast(context, R.string.after_questionnaire, 1);
		else
			CustomToast.generateToast(context, R.string.after_questionnaire, 0);
	}
	
	public void updateSelfCounter(){
		quesBoxUpdater.updateSelfHelpCounter();
	}
}
