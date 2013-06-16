package statistic.ui.questionnaire.listener;

import android.view.View;
import statistic.ui.QuestionMsgBox2;

public class EndOnClickListener extends QuestionnaireOnClickListener {

	private int aid;
	
	public EndOnClickListener(QuestionMsgBox2 msgBox, int aid) {
		super(msgBox);
		this.aid = aid;
	}

	@Override
	public void onClick(View v) {
		if (aid >= 0)
			seq.add(","+aid);
		msgBox.insertSeq();
		msgBox.closeBox();
	}

}
