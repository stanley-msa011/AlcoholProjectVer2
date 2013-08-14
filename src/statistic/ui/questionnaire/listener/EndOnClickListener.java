package statistic.ui.questionnaire.listener;

import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLoggerLog;
import android.view.View;
import statistic.ui.QuestionMsgBox;

public class EndOnClickListener extends QuestionnaireOnClickListener {

	private int aid;
	
	public EndOnClickListener(QuestionMsgBox msgBox, int aid) {
		super(msgBox);
		this.aid = aid;
	}

	@Override
	public void onClick(View v) {
		ClickLoggerLog.Log(msgBox.getContext(), ClickLogId.STATISTIC_QUESTION_NEXT);
		if (aid >= 0)
			seq.add(","+aid);
		boolean addAcc = msgBox.insertSeq();
		msgBox.closeBox();
		msgBox.showEndOfQuestionnaire(addAcc);
		msgBox.updateSelfCounter();
	}

}
