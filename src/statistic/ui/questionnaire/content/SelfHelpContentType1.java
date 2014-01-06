package statistic.ui.questionnaire.content;

import statistic.ui.QuestionnaireBox;
import statistic.ui.questionnaire.listener.SelectedListener;
import statistic.ui.questionnaire.listener.SituationOnClickListener;
import ubicomp.drunk_detection.activities.R;

public class SelfHelpContentType1 extends QuestionnaireContent {

	public SelfHelpContentType1(QuestionnaireBox msgBox) {
		super(msgBox);
	}

	@Override
	protected void setContent() {
		msgBox.setNextButton("", null);
		seq.add(",4,6");
		setHelp(R.string.self_help_help);
		setSelectItem(R.string.self_help_selection0,new SelectedListener(msgBox,new SituationOnClickListener(msgBox,1),R.string.next));
		setSelectItem(R.string.self_help_selection1,new SelectedListener(msgBox,new SituationOnClickListener(msgBox,2),R.string.next));
		setSelectItem(R.string.self_help_selection2,new SelectedListener(msgBox,new SituationOnClickListener(msgBox,3),R.string.next));
		setSelectItem(R.string.self_help_selection3,new SelectedListener(msgBox,new SituationOnClickListener(msgBox,4),R.string.next));
		setSelectItem(R.string.self_help_selection4,new SelectedListener(msgBox,new SituationOnClickListener(msgBox,5),R.string.next));
		msgBox.showQuestionnaireLayout(true);

	}

	@Override
	public void onPop() {
		contentSeq.remove(contentSeq.size()-1);
		seq.remove(seq.size()-1);
	}

}
