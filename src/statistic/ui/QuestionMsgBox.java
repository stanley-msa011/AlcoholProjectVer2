package statistic.ui;

import java.util.ArrayList;

import database.QuestionDB;
import statistic.ui.questionnaire.content.QuestionnaireContent;
import statistic.ui.questionnaire.content.Type0Content;
import statistic.ui.questionnaire.content.Type1Content;
import statistic.ui.questionnaire.content.Type2Content;
import statistic.ui.questionnaire.content.Type3Content;
import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.activities.StatisticFragment;
import ubicomp.drunk_detection.activities.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class QuestionMsgBox {

	private ArrayList<String> clickSequence;
	private ArrayList<QuestionnaireContent> contentSequence;
	
	private StatisticFragment statisticFragment;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	
	private RelativeLayout mainLayout;
	
	private FrameLayout backgroundLayout;
	
	private LinearLayout questionLayout, questionAllLayout;
	
	private ImageView exitView;
	
	private TextView help,next;
	
	private Drawable choiceDrawable, choiceSelectedDrawable;
	
	private Resources r;
	private Point screen;
	
	private QuestionDB db;
	private Typeface wordTypefaceBold;
	private int type;
	
	public QuestionMsgBox(StatisticFragment statisticFragment,RelativeLayout mainLayout){
		this.statisticFragment = statisticFragment;
		this.context = statisticFragment.getActivity();
		this.r = context.getResources();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		backgroundLayout = new FrameLayout(context);
		backgroundLayout.setBackgroundColor(0x99000000);
		screen = FragmentTabs.getSize();
		db = new QuestionDB(context);
		clickSequence = new ArrayList<String>();
		contentSequence = new ArrayList<QuestionnaireContent>();
		type = -1;
		
		setting();
	}
	
	private void setting(){
		
		backgroundLayout.setVisibility(View.INVISIBLE);
		
		boxLayout = (RelativeLayout) inflater.inflate(R.layout.question_box_layout,null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		questionLayout = (LinearLayout) boxLayout.findViewById(R.id.question_layout);
		questionAllLayout = (LinearLayout) boxLayout.findViewById(R.id.question_all_layout);
		
		help = (TextView) boxLayout.findViewById(R.id.question_text);
		next = (TextView) boxLayout.findViewById(R.id.question_next);
		
		exitView = (ImageView) boxLayout.findViewById(R.id.question_exit);
	}
	
	public void settingPreTask(){
		
		wordTypefaceBold = Typeface.createFromAsset(context.getAssets(), "fonts/DFLiHeiStd-W5.otf");
		mainLayout.addView(backgroundLayout);
		mainLayout.addView(boxLayout);
		
		RelativeLayout.LayoutParams bgParam = (LayoutParams) backgroundLayout.getLayoutParams();
		bgParam.width = bgParam.height = LayoutParams.MATCH_PARENT;
		
		RelativeLayout.LayoutParams boxParam = (LayoutParams) boxLayout.getLayoutParams();
		boxParam.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
		boxParam.width = screen.x * 348/480;
		RelativeLayout.LayoutParams qParam = (LayoutParams) questionAllLayout.getLayoutParams();
		qParam.topMargin = screen.x * 12/480;
		
		
		int textSize = screen.x * 21/480;
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
		exitView.setOnClickListener(new ExitListener());
	}
	
	public void clear(){
		if (backgroundLayout != null)
			mainLayout.removeView(backgroundLayout);
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
		statisticFragment.enablePage(false);
		backgroundLayout.setVisibility(View.VISIBLE);
		boxLayout.setVisibility(View.VISIBLE);
		return;
}
	
	public void closeBox(){
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("latest_result", -1);
    	editor.commit();
    	statisticFragment.enablePage(true);
    	backgroundLayout.setVisibility(View.INVISIBLE);
		boxLayout.setVisibility(View.INVISIBLE);
		statisticFragment.setQuestionAnimation();
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
	
	public void insertSeq(){
		db.insertQuestionnaire(seq_toString(),type);
	}
	

	private String seq_toString(){
		int size = clickSequence.size();
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<size;++i)
			sb.append(clickSequence.get(i));
		Log.d("Questionnaire",sb.toString());
		return sb.toString();
	}
	
	public void setHelpMessage(String str){
		help.setText(str);
	}
	
	public void setNextButton(String str, View.OnClickListener listener){
		next.setText(str);
		next.setOnClickListener(listener);
	}
	
	public Typeface getTypeface(){
		return wordTypefaceBold;
	}
	
	public void cleanSelection(){
		int idx = contentSequence.size()-1;
		Log.d("Questionnaire","Size = "+contentSequence.size());
		if (idx >=0)
			contentSequence.get(idx).cleanSelection();
	}
	
	private class ExitListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			clickSequence.clear();
			contentSequence.clear();
			statisticFragment.enablePage(true);
			backgroundLayout.setVisibility(View.INVISIBLE);
			boxLayout.setVisibility(View.INVISIBLE);
		}
		
	}
	
	public void showEndOfQuestionnaire(){
		statisticFragment.showEndOfQuestionnaire();
	}
	
}
