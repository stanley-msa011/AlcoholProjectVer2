package statistic.ui.questionnaire.listener;

import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;
import android.view.View;
import statistic.ui.QuestionnaireBox;

public class EndOnClickListenerNoInput extends QuestionnaireOnClickListener {

	public EndOnClickListenerNoInput(QuestionnaireBox msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View v) {
		ClickLogger.Log(msgBox.getContext(), ClickLogId.STATISTIC_QUESTION_NEXT);
		msgBox.closeBoxNull();
	}

}
