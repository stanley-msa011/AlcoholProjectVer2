package statistic.ui.questionnaire.content;

import statistic.ui.QuestionnaireBox;
import statistic.ui.questionnaire.listener.EndOnClickListener;
import ubicomp.drunk_detection.activities.R;

public class SolutionContent extends QuestionnaireContent {

	private static String[] TEXT;
	private int aid;
	public SolutionContent(QuestionnaireBox msgBox, int aid) {
		super(msgBox);
		this.aid = aid;
		TEXT = msgBox.getContext().getResources().getStringArray(R.array.question_solutions);
	}

	@Override
	protected void setContent() {
		msgBox.setNextButton("", null);
		seq.add(","+aid);
		setHelp(R.string.follow_the_guide);
		msgBox.setNextButton(TEXT[aid-1],new EndOnClickListener(msgBox,-1));
		msgBox.showQuestionnaireLayout(false);
	}

	@Override
	public void onPop() {
		contentSeq.remove(contentSeq.size()-1);
		seq.remove(seq.size()-1);
	}

}
