package statistic.ui.questionnaire.content;

import statistic.ui.QuestionMsgBox2;
import statistic.ui.questionnaire.listener.EndOnClickListener;
import statistic.ui.questionnaire.listener.FamilyOnClickListener;
import statistic.ui.questionnaire.listener.SelfOnClickListener;

public class Type3Content extends QuestionnaireContent {

	public Type3Content(QuestionMsgBox2 msgBox) {
		super(msgBox);
	}

	@Override
	protected void setContent() {
		contentSeq.clear();
		seq.clear();
		seq.add("4,4");
		msgBox.openBox();
		setHelp("您似乎喝了不少酒，\n請問您要?");
		setSelectItem("回家休息", new EndOnClickListener(msgBox,3));
		setSelectItem("尋求親友協助", new FamilyOnClickListener(msgBox));
		setSelectItem("自行處理",new SelfOnClickListener(msgBox));
		
	}

	@Override
	public void onPop() {
		contentSeq.remove(contentSeq.size()-1);
		seq.remove(seq.size()-1);
	}

}
