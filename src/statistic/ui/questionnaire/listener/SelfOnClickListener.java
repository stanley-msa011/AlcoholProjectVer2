package statistic.ui.questionnaire.listener;

import statistic.ui.QuestionMsgBox2;
import statistic.ui.questionnaire.content.SelfHelpContent;
import android.view.View;

public class SelfOnClickListener extends QuestionnaireOnClickListener {

	public SelfOnClickListener(QuestionMsgBox2 msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View v) {
		contentSeq.add(new SelfHelpContent(msgBox));
		contentSeq.get(contentSeq.size()-1).onPush();
	}

}
