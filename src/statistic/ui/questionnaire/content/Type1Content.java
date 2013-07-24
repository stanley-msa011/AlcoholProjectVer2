package statistic.ui.questionnaire.content;

import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.listener.EmotionCallOnClickListener;
import statistic.ui.questionnaire.listener.FamilyOnClickListener;
import statistic.ui.questionnaire.listener.SelectedListener;
import statistic.ui.questionnaire.listener.SocialCallOnClickListener;
import ubicomp.drunk_detection.activities.R;

public class Type1Content extends QuestionnaireContent {

	public Type1Content(QuestionMsgBox msgBox) {
		super(msgBox);
	}

	@Override
	protected void setContent() {
		msgBox.setNextButton("", null);
		seq.clear();
		seq.add("2");
		msgBox.openBox();
		setHelp(R.string.question_type1_help);
		setSelectItem(R.string.connect_to_family, new SelectedListener (msgBox,new FamilyOnClickListener(msgBox),R.string.next));
		setSelectItem(R.string.connect_to_emotion_hot_line, new SelectedListener(msgBox,new EmotionCallOnClickListener(msgBox),R.string.next));
		setSelectItem(R.string.connect_for_social_help, new SelectedListener(msgBox,new SocialCallOnClickListener(msgBox),R.string.next));
		msgBox.showQuestionnaireLayout(true);
	}

	@Override
	public void onPop() {
		contentSeq.remove(contentSeq.size()-1);
		seq.remove(seq.size()-1);
	}

}
