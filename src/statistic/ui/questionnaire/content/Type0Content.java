package statistic.ui.questionnaire.content;

import statistic.ui.QuestionMsgBox2;
import statistic.ui.questionnaire.listener.EndOnClickListener;

public class Type0Content extends QuestionnaireContent {

	public Type0Content(QuestionMsgBox2 msgBox) {
		super(msgBox);
	}

	@Override
	protected void setContent() {
		contentSeq.clear();
		seq.clear();
		seq.add("1,1");
		msgBox.openBox();
		setHelp("為了家人\n請繼續加油!");
		setSelectItem("完成", new EndOnClickListener(msgBox,1));
	}

	@Override
	public void onPop() {
		contentSeq.clear();
		seq.clear();
	}

}
