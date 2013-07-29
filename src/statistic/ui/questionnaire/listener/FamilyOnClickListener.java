package statistic.ui.questionnaire.listener;

import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLoggerLog;
import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.content.ConnectContent;
import android.view.View;

public class FamilyOnClickListener extends QuestionnaireOnClickListener {

	public FamilyOnClickListener(QuestionMsgBox msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View arg0) {
		ClickLoggerLog.Log(msgBox.getContext(), ClickLogId.STATISTIC_QUESTION_NEXT);
		contentSeq.add(new ConnectContent(msgBox,ConnectContent.TYPE_FAMILY));
		contentSeq.get(contentSeq.size()-1).onPush();
	}

}
