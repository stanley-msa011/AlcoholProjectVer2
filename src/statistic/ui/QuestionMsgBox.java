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
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

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
	
	private Drawable choiceDrawable, choiceSelectedDrawable;
	private ImageView top,bottom;
	
	private Resources r;
	private Point screen;
	
	private QuestionDB db;
	
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
		
		boxLayout = (RelativeLayout) inflater.inflate(R.layout.question_box_layout2,null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		questionLayout = (LinearLayout) boxLayout.findViewById(R.id.question_layout);
		questionAllLayout = (LinearLayout) boxLayout.findViewById(R.id.question_all_layout);
		exitView = (ImageView) boxLayout.findViewById(R.id.question_exit);
		top = (ImageView) boxLayout.findViewById(R.id.question_top);
		bottom = (ImageView) boxLayout.findViewById(R.id.question_bottom);
	}
	
	public void settingPreTask(){
		mainLayout.addView(backgroundLayout);
		mainLayout.addView(boxLayout);
		
		RelativeLayout.LayoutParams bgParam = (LayoutParams) backgroundLayout.getLayoutParams();
		bgParam.width = bgParam.height = LayoutParams.MATCH_PARENT;
		
		RelativeLayout.LayoutParams boxParam = (LayoutParams) boxLayout.getLayoutParams();
		boxParam.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
		boxParam.width = screen.x * 358/480;
		RelativeLayout.LayoutParams qParam = (LayoutParams) questionAllLayout.getLayoutParams();
		qParam.topMargin = screen.x * 12/480;
	}
	
	
	public void settingInBackground(){
		choiceDrawable = r.getDrawable(R.drawable.question_choice);
		choiceSelectedDrawable = r.getDrawable(R.drawable.question_choice_selection);
	}
	
	public  void settingPostTask(){
		exitView.setOnClickListener(new ExitListener());
	}
	
	public void clear(){
		top.setImageBitmap(null);
		bottom.setImageBitmap(null);
		exitView.setImageBitmap(null);
		questionLayout.setBackground(null);
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
		contentSequence.clear();
		contentSequence.add(new Type1Content(this));
		contentSequence.get(contentSequence.size()-1).onPush();
	}
	
	public void generateType2Box(){
		type = 2;
		contentSequence.clear();
		contentSequence.add(new Type2Content(this));
		contentSequence.get(contentSequence.size()-1).onPush();
	}
	
	public void generateType3Box(){
		type = 3;
		contentSequence.clear();
		contentSequence.add(new Type3Content(this));
		contentSequence.get(contentSequence.size()-1).onPush();
	}
	
	public void generateNormalBox(){
		type = -1;
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
	
}
