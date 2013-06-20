package statistic.ui.questionnaire.content;

import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.listener.SituationOnClickListener;

public class SelfHelpContent extends QuestionnaireContent {

	public SelfHelpContent(QuestionMsgBox msgBox) {
		super(msgBox);
	}

	@Override
	protected void setContent() {
		seq.add(",6");
		setHelp("請問您現在的狀況？");
		setSelectItem("身邊有酒",new SituationOnClickListener(msgBox,1));
		setSelectItem("手頭寬裕",new SituationOnClickListener(msgBox,2));
		setSelectItem("感到無聊",new SituationOnClickListener(msgBox,3));
		setSelectItem("覺得有壓力",new SituationOnClickListener(msgBox,4));
		setSelectItem("身體不舒服",new SituationOnClickListener(msgBox,5));

	}

	@Override
	public void onPop() {
		contentSeq.remove(contentSeq.size()-1);
		seq.remove(seq.size()-1);
	}

}
