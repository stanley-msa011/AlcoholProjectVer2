package statistic.ui.questionnaire.listener;

import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.content.SolutionContent;
import android.view.View;

public class SituationOnClickListener extends QuestionnaireOnClickListener {

	private int aid;
	public SituationOnClickListener(QuestionMsgBox msgBox,int aid) {
		super(msgBox);
		this.aid = aid;
	}

	@Override
	public void onClick(View v) {
		contentSeq.add(new SolutionContent(msgBox,aid));
		contentSeq.get(contentSeq.size()-1).onPush();

	}

}
