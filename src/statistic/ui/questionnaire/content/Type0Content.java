package statistic.ui.questionnaire.content;

import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.listener.EndOnClickListener;
import ubicomp.drunk_detection.activities.R;

public class Type0Content extends QuestionnaireContent {

	public Type0Content(QuestionMsgBox msgBox) {
		super(msgBox);
	}

	@Override
	protected void setContent() {
		msgBox.setNextButton("", null);
		seq.clear();
		seq.add("1");
		msgBox.openBox();
		setHelp(R.string.question_type0_help);
		msgBox.showQuestionnaireLayout(false);
		msgBox.setNextButton(R.string.question_type0_next,new EndOnClickListener(msgBox,1));
	}

	@Override
	public void onPop() {
		contentSeq.clear();
		seq.clear();
	}

}
