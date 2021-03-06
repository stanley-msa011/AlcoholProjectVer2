package statistic.ui.questionnaire.listener;

import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLoggerLog;
import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.content.CallCheckContent;
import android.view.View;

public class CallCheckOnClickListener extends QuestionnaireOnClickListener {

	private String name,phone;
	public CallCheckOnClickListener(QuestionMsgBox msgBox,String name,String phone) {
		super(msgBox);
		this.name = name;
		this.phone = phone;
	}

	@Override
	public void onClick(View v) {
		ClickLoggerLog.Log(msgBox.getContext(), ClickLogId.STATISTIC_QUESTION_NEXT);
		contentSeq.add(new CallCheckContent(msgBox,name,phone));
		contentSeq.get(contentSeq.size()-1).onPush();
	}

}
