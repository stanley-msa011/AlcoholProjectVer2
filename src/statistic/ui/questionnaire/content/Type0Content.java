package statistic.ui.questionnaire.content;

import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.listener.EndOnClickListener;

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
		setHelp("為了家人和不一樣的\n自己，請繼續加油，\n堅持下去");
		//setSelectItem("我相信我做得到的", new SelectedListener(msgBox,new EndOnClickListener(msgBox,1),"確定"));
		msgBox.setNextButton("我相信我做得到的",new EndOnClickListener(msgBox,1));
	}

	@Override
	public void onPop() {
		contentSeq.clear();
		seq.clear();
	}

}
