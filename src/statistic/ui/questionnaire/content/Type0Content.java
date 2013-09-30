package statistic.ui.questionnaire.content;

import java.util.Random;

import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.listener.EndOnClickListener;
import ubicomp.drunk_detection.activities.R;

public class Type0Content extends QuestionnaireContent {

	private static String[] help,next;
	
	public Type0Content(QuestionMsgBox msgBox) {
		super(msgBox);
		if (help==null)
			help = msgBox.getContext().getResources().getStringArray(R.array.question_type0_help);
		if (next == null)
		next = msgBox.getContext().getResources().getStringArray(R.array.question_type0_next);
	}

	@Override
	protected void setContent() {
		msgBox.setNextButton("", null);
		seq.clear();
		seq.add("1");
		msgBox.openBox();
		Random rand = new Random();
		int idx = rand.nextInt(help.length);
		
		setHelp(help[idx]);
		msgBox.showQuestionnaireLayout(false);
		msgBox.setNextButton(next[idx],new EndOnClickListener(msgBox,1));
	}

	@Override
	public void onPop() {
		contentSeq.clear();
		seq.clear();
	}

}
