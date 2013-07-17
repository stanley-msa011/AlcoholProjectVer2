package statistic.ui.questionnaire.content;

import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.listener.SelectedListener;
import statistic.ui.questionnaire.listener.SituationOnClickListener;

public class SelfHelpContent extends QuestionnaireContent {

	public SelfHelpContent(QuestionMsgBox msgBox) {
		super(msgBox);
	}

	@Override
	protected void setContent() {
		msgBox.setNextButton("", null);
		seq.add(",6");
		setHelp("請問您現在的狀況？");
		setSelectItem("身邊有酒",new SelectedListener(msgBox,new SituationOnClickListener(msgBox,1),"下一步"));
		setSelectItem("手頭寬裕",new SelectedListener(msgBox,new SituationOnClickListener(msgBox,2),"下一步"));
		setSelectItem("感到無聊",new SelectedListener(msgBox,new SituationOnClickListener(msgBox,3),"下一步"));
		setSelectItem("覺得有壓力",new SelectedListener(msgBox,new SituationOnClickListener(msgBox,4),"下一步"));
		setSelectItem("身體不舒服",new SelectedListener(msgBox,new SituationOnClickListener(msgBox,5),"下一步"));

	}

	@Override
	public void onPop() {
		contentSeq.remove(contentSeq.size()-1);
		seq.remove(seq.size()-1);
	}

}
