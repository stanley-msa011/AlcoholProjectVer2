package statistic.ui;

import java.util.ArrayList;

import database.QuestionDB;
import statistic.ui.questionnaire.content.QuestionnaireContent;
import statistic.ui.questionnaire.content.Type0Content;
import statistic.ui.questionnaire.content.Type1Content;
import statistic.ui.questionnaire.content.Type2Content;
import statistic.ui.questionnaire.content.Type3Content;
import ubicomp.drunk_detection.activities.AlarmReceiver;
import ubicomp.drunk_detection.activities.FragmentTabs;
import ubicomp.drunk_detection.activities.StatisticFragment;
import ubicomp.drunk_detection.activities.R;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class QuestionMsgBox2 {

	private ArrayList<String> clickSequence;
	private ArrayList<QuestionnaireContent> contentSequence;
	
	private StatisticFragment statisticFragment;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	
	private RelativeLayout mainLayout;
	
	private LinearLayout questionLayout, questionAllLayout;
	
	private ImageView exitView;
	
	private Bitmap choiceBmp, choiceSelectedBmp;
	private Bitmap bgBmp, bgTopBmp, bgBottomBmp,exitBmp;
	private ImageView top,bottom;
	
	private Resources r;
	private Point screen;
	
	private QuestionDB db;
	
	public QuestionMsgBox2(StatisticFragment statisticFragment,RelativeLayout mainLayout){
		this.statisticFragment = statisticFragment;
		this.context = statisticFragment.getActivity();
		this.r = context.getResources();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		screen = FragmentTabs.getSize();
		db = new QuestionDB(context);
		clickSequence = new ArrayList<String>();
		contentSequence = new ArrayList<QuestionnaireContent>();
		setting();
	}
	
	private void setting(){
		
		boxLayout = (RelativeLayout) inflater.inflate(R.layout.question_box_layout2,null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		questionLayout = (LinearLayout) boxLayout.findViewById(R.id.question_layout);
		questionAllLayout = (LinearLayout) boxLayout.findViewById(R.id.question_all_layout);
		exitView = (ImageView) boxLayout.findViewById(R.id.question_exit);
		top = (ImageView) boxLayout.findViewById(R.id.question_top);
		bottom = (ImageView) boxLayout.findViewById(R.id.question_bottom);
	}
	
	public void settingPreTask(){
		mainLayout.addView(boxLayout);
		RelativeLayout.LayoutParams boxParam = (LayoutParams) boxLayout.getLayoutParams();
		boxParam.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
		boxParam.width = screen.x * 810/1080;
		RelativeLayout.LayoutParams qParam = (LayoutParams) questionAllLayout.getLayoutParams();
		qParam.width = screen.x * 771/1080;
		qParam.topMargin = screen.x * 39/1080;
		LinearLayout.LayoutParams topParam = (LinearLayout.LayoutParams) top.getLayoutParams();
		topParam.width = screen.x * 771/1080;
		topParam.height = screen.x * 50/1080;
		LinearLayout.LayoutParams bottomParam = (LinearLayout.LayoutParams) bottom.getLayoutParams();
		bottomParam.width = screen.x * 771/1080;
		bottomParam.height = screen.x * 69/1080;
		RelativeLayout.LayoutParams eParam = (LayoutParams) exitView.getLayoutParams();
		eParam.width = eParam.height = screen.x * 77/1080;
	}
	
	
	public void settingInBackground(){
		Bitmap tmp;
		tmp = BitmapFactory.decodeResource(r, R.drawable.question_bg_top);
		bgTopBmp = Bitmap.createScaledBitmap(tmp,screen.x *771/1080, screen.x * 50 / 1080, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(r, R.drawable.question_bg_bottom);
		bgBottomBmp = Bitmap.createScaledBitmap(tmp,screen.x *771/1080, screen.x * 69 / 1080, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(r, R.drawable.question_bg_content);
		bgBmp = Bitmap.createScaledBitmap(tmp,screen.x *771/1080, screen.x * 305 / 1080, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(r, R.drawable.question_close);
		exitBmp = Bitmap.createScaledBitmap(tmp,screen.x *77/1080, screen.x * 77 / 1080, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(r, R.drawable.question_choice);
		choiceBmp = Bitmap.createScaledBitmap(tmp,screen.x *48/1080, screen.x * 48/ 1080, true);
		tmp.recycle();
		tmp = BitmapFactory.decodeResource(r, R.drawable.question_choice_selection);
		choiceSelectedBmp = Bitmap.createScaledBitmap(tmp,screen.x *48/1080, screen.x * 48/ 1080, true);
		tmp.recycle();
	}
	
	public  void settingPostTask(){
		exitView.setImageBitmap(exitBmp);
		top.setImageBitmap(bgTopBmp);
		bottom.setImageBitmap(bgBottomBmp);
		questionLayout.setBackground(new BitmapDrawable(r,bgBmp));
		exitView.setOnClickListener(new ExitListener());
	}
	
	public void clear(){
		if (bgBmp!=null && !bgBmp.isRecycled()){
			bgBmp.recycle();
			bgBmp = null;
		}
		if (bgTopBmp!=null && !bgTopBmp.isRecycled()){
			bgTopBmp.recycle();
			bgTopBmp = null;
		}
		if (bgBottomBmp!=null && !bgBottomBmp.isRecycled()){
			bgBottomBmp.recycle();
			bgBottomBmp = null;
		}
		if (exitBmp!=null && !exitBmp.isRecycled()){
			exitBmp.recycle();
			exitBmp = null;
		}
		if (choiceBmp!=null && !choiceBmp.isRecycled()){
			choiceBmp.recycle();
			choiceBmp = null;
		}
		if (choiceSelectedBmp!=null && !choiceSelectedBmp.isRecycled()){
			choiceSelectedBmp.recycle();
			choiceSelectedBmp = null;
		}
	}
	
	public void generateType0Box(){
		contentSequence.clear();
		contentSequence.add(new Type0Content(this));
		contentSequence.get(contentSequence.size()-1).onPush();
	}
	
	public void generateType1Box(){
		contentSequence.clear();
		contentSequence.add(new Type1Content(this));
		contentSequence.get(contentSequence.size()-1).onPush();
	}
	
	public void generateType2Box(){
		contentSequence.clear();
		contentSequence.add(new Type2Content(this));
		contentSequence.get(contentSequence.size()-1).onPush();
	}
	
	public void generateType3Box(){
		contentSequence.clear();
		contentSequence.add(new Type3Content(this));
		contentSequence.get(contentSequence.size()-1).onPush();
	}
	
	public void openBox(){
		boxLayout.setVisibility(View.VISIBLE);
		return;
}
	
	public void closeBox(){
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("latest_result", 0);
    	editor.commit();
		boxLayout.setVisibility(View.INVISIBLE);
		statisticFragment.setQuestionAnimation();
		return;
	}
	
	public Context getContext(){
		return context;
	}
	
	public Bitmap getChoiceBmp(){
		return choiceBmp;
	}
	
	public Bitmap getChoiceSelectedBmp(){
		return choiceSelectedBmp;
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
		db.insertQuestionnaire(seq_toString());
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
			boxLayout.setVisibility(View.INVISIBLE);
		}
		
	}
	
}
