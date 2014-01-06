package statistic.ui.questionnaire.content;

import statistic.ui.QuestionnaireBox;
import statistic.ui.questionnaire.listener.EndOnClickListener;
import statistic.ui.questionnaire.listener.FamilyOnClickListener;
import statistic.ui.questionnaire.listener.SelectedListener;
import statistic.ui.questionnaire.listener.SelfOnClickListener;
import ubicomp.drunk_detection.activities.R;

public class Type3Content extends QuestionnaireContent {

	public Type3Content(QuestionnaireBox msgBox) {
		super(msgBox);
	}

	@Override
	protected void setContent() {
		msgBox.setNextButton("", null);
		seq.clear();
		seq.add("4");
		msgBox.openBox();
		setHelp(R.string.question_type3_help);
		setSelectItem(R.string.go_home, new SelectedListener(msgBox,new EndOnClickListener(msgBox,3),R.string.ok));
		setSelectItem(R.string.connect_to_family, new SelectedListener(msgBox,new FamilyOnClickListener(msgBox),R.string.next));
		setSelectItem(R.string.self_help,new SelectedListener(msgBox,new SelfOnClickListener(msgBox),R.string.next));
		msgBox.showQuestionnaireLayout(true);
	}

	@Override
	public void onPop() {
		contentSeq.remove(contentSeq.size()-1);
		seq.remove(seq.size()-1);
	}

}
