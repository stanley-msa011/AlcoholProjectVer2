package statistic.ui.questionnaire.listener;

import statistic.ui.QuestionMsgBox2;
import android.view.View;

public class CallOnClickListener extends QuestionnaireOnClickListener {

	public CallOnClickListener(QuestionMsgBox2 msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View v) {
		seq.add(",-1");
		msgBox.insertSeq();
		msgBox.closeBox();
	}

}
