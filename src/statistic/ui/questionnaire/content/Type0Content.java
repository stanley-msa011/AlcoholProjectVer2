package statistic.ui.questionnaire.content;

import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.listener.EndOnClickListener;

public class Type0Content extends QuestionnaireContent {

	public Type0Content(QuestionMsgBox msgBox) {
		super(msgBox);
	}

	@Override
	protected void setContent() {
		contentSeq.clear();
		seq.clear();
		seq.add("1,1");
		msgBox.openBox();
		setHelp("為了家人\n請繼續加油!");
		setSelectItem("我做得到", new EndOnClickListener(msgBox,1));
	}

	@Override
	public void onPop() {
		contentSeq.clear();
		seq.clear();
	}

}
