package statistic.ui.questionnaire.listener;

import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;
import statistic.ui.QuestionnaireBox;
import statistic.ui.questionnaire.content.SelfHelpContentType1;
import android.view.View;

public class SelfOnClickListenerType1 extends QuestionnaireOnClickListener {

	public SelfOnClickListenerType1(QuestionnaireBox msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View v) {
		ClickLogger.Log(msgBox.getContext(), ClickLogId.STATISTIC_QUESTION_NEXT);
		contentSeq.add(new SelfHelpContentType1(msgBox));
		contentSeq.get(contentSeq.size()-1).onPush();
	}

}
