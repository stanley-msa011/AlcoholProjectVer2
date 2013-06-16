package statistic.ui.questionnaire.listener;

import statistic.ui.QuestionMsgBox2;
import statistic.ui.questionnaire.content.CallCheckContent;
import android.view.View;

public class EmotionCallOnClickListener extends QuestionnaireOnClickListener {

	public EmotionCallOnClickListener(QuestionMsgBox2 msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View v) {
		contentSeq.add(new CallCheckContent(msgBox,"心情專線","0212345678",true));
		contentSeq.get(contentSeq.size()-1).onPush();
	}

}
