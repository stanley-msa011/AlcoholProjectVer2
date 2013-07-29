package statistic.ui.questionnaire.listener;

import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLoggerLog;
import android.view.View;
import statistic.ui.QuestionMsgBox;

public class EndOnClickListenerNoInput extends QuestionnaireOnClickListener {

	public EndOnClickListenerNoInput(QuestionMsgBox msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View v) {
		ClickLoggerLog.Log(msgBox.getContext(), ClickLogId.STATISTIC_QUESTION_NEXT);
		msgBox.closeBoxNull();
	}

}
