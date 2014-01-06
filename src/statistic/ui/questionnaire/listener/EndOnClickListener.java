package statistic.ui.questionnaire.listener;

import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;
import android.view.View;
import statistic.ui.QuestionnaireBox;

public class EndOnClickListener extends QuestionnaireOnClickListener {

	private int aid;
	
	public EndOnClickListener(QuestionnaireBox msgBox, int aid) {
		super(msgBox);
		this.aid = aid;
	}

	@Override
	public void onClick(View v) {
		ClickLogger.Log(msgBox.getContext(), ClickLogId.STATISTIC_QUESTION_NEXT);
		if (aid >= 0)
			seq.add(","+aid);
		boolean addAcc = msgBox.insertSeq();
		msgBox.closeBox();
		msgBox.showEndOfQuestionnaire(addAcc);
		msgBox.updateSelfCounter();
	}

}
