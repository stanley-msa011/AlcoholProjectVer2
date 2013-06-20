package statistic.ui.questionnaire.listener;

import java.util.ArrayList;

import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.content.QuestionnaireContent;
import android.view.View.OnClickListener;

public abstract class QuestionnaireOnClickListener implements OnClickListener {

	protected QuestionMsgBox msgBox;
	protected ArrayList<String>seq;
	protected ArrayList <QuestionnaireContent> contentSeq;
	
	public QuestionnaireOnClickListener (QuestionMsgBox msgBox){
		this.msgBox = msgBox;
		this.seq = msgBox.getClickSequence();
		this.contentSeq = msgBox.getQuestionSequence();
	}
	
}
