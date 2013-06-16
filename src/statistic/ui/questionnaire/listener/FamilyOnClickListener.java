package statistic.ui.questionnaire.listener;

import statistic.ui.QuestionMsgBox2;
import statistic.ui.questionnaire.content.ConnectContent;
import android.view.View;

public class FamilyOnClickListener extends QuestionnaireOnClickListener {

	public FamilyOnClickListener(QuestionMsgBox2 msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View arg0) {
		contentSeq.add(new ConnectContent(msgBox,ConnectContent.TYPE_FAMILY));
		contentSeq.get(contentSeq.size()-1).onPush();
	}

}
