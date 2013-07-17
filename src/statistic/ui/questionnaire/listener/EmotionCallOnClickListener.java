package statistic.ui.questionnaire.listener;

import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.content.CallCheckContent;
import android.view.View;

public class EmotionCallOnClickListener extends QuestionnaireOnClickListener {

	public EmotionCallOnClickListener(QuestionMsgBox msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View v) {
		contentSeq.add(new CallCheckContent(msgBox,"心情專線","0800788995",true));
		contentSeq.get(contentSeq.size()-1).onPush();
	}

}
