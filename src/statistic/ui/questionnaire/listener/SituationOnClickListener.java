package statistic.ui.questionnaire.listener;

import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;
import statistic.ui.QuestionnaireBox;
import statistic.ui.questionnaire.content.SolutionContent;
import android.view.View;

public class SituationOnClickListener extends QuestionnaireOnClickListener {

	private int aid;
	public SituationOnClickListener(QuestionnaireBox msgBox,int aid) {
		super(msgBox);
		this.aid = aid;
	}

	@Override
	public void onClick(View v) {
		ClickLogger.Log(msgBox.getContext(), ClickLogId.STATISTIC_QUESTION_NEXT);
		contentSeq.add(new SolutionContent(msgBox,aid));
		contentSeq.get(contentSeq.size()-1).onPush();

	}

}
