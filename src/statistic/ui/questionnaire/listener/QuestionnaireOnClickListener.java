package statistic.ui.questionnaire.listener;

import java.util.ArrayList;

import statistic.ui.QuestionnaireBox;
import statistic.ui.questionnaire.content.QuestionnaireContent;
import android.view.View.OnClickListener;

public abstract class QuestionnaireOnClickListener implements OnClickListener {

	protected QuestionnaireBox msgBox;
	protected ArrayList<String>seq;
	protected ArrayList <QuestionnaireContent> contentSeq;
	
	public QuestionnaireOnClickListener (QuestionnaireBox msgBox){
		this.msgBox = msgBox;
		this.seq = msgBox.getClickSequence();
		this.contentSeq = msgBox.getQuestionSequence();
	}
	
}
