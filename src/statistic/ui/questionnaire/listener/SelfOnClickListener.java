package statistic.ui.questionnaire.listener;

import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;
import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.content.SelfHelpContent;
import android.view.View;

public class SelfOnClickListener extends QuestionnaireOnClickListener {

	public SelfOnClickListener(QuestionMsgBox msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View v) {
		ClickLogger.Log(msgBox.getContext(), ClickLogId.STATISTIC_QUESTION_NEXT);
		contentSeq.add(new SelfHelpContent(msgBox));
		contentSeq.get(contentSeq.size()-1).onPush();
	}

}
