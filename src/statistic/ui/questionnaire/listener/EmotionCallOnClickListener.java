package statistic.ui.questionnaire.listener;

import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLoggerLog;
import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.content.CallCheckContent;
import ubicomp.drunk_detection.activities.R;
import android.view.View;

public class EmotionCallOnClickListener extends QuestionnaireOnClickListener {

	public EmotionCallOnClickListener(QuestionMsgBox msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View v) {
		ClickLoggerLog.Log(msgBox.getContext(), ClickLogId.STATISTIC_QUESTION_NEXT);
		String emotion_hot_line = msgBox.getContext().getString(R.string.call_check_help_emotion_hot_line);
		contentSeq.add(new CallCheckContent(msgBox,emotion_hot_line,"0800788995",true));
		contentSeq.get(contentSeq.size()-1).onPush();
	}

}
