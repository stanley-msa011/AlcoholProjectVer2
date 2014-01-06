package statistic.ui.questionnaire.listener;

import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;
import statistic.ui.QuestionnaireBox;
import statistic.ui.questionnaire.content.ConnectContent;
import android.view.View;

public class FamilyOnClickListener extends QuestionnaireOnClickListener {

	public FamilyOnClickListener(QuestionnaireBox msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View arg0) {
		ClickLogger.Log(msgBox.getContext(), ClickLogId.STATISTIC_QUESTION_NEXT);
		contentSeq.add(new ConnectContent(msgBox,ConnectContent.TYPE_FAMILY));
		contentSeq.get(contentSeq.size()-1).onPush();
	}

}
