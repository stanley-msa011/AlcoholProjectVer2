package statistic.ui.questionnaire.listener;

import statistic.ui.QuestionMsgBox;
import android.view.View;

public class CallOnClickListener extends QuestionnaireOnClickListener {

	public CallOnClickListener(QuestionMsgBox msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View v) {
		seq.add(",-1");
		msgBox.insertSeq();
		msgBox.closeBox();
	}

}
