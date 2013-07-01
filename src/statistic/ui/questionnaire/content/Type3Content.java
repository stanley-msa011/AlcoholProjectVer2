package statistic.ui.questionnaire.content;

import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.listener.EndOnClickListener;
import statistic.ui.questionnaire.listener.FamilyOnClickListener;
import statistic.ui.questionnaire.listener.SelfOnClickListener;

public class Type3Content extends QuestionnaireContent {

	public Type3Content(QuestionMsgBox msgBox) {
		super(msgBox);
	}

	@Override
	protected void setContent() {
		contentSeq.clear();
		seq.clear();
		seq.add("4");
		msgBox.openBox();
		setHelp("您似乎喝了不少酒，\n建議你可以：");
		setSelectItem("回家休息", new EndOnClickListener(msgBox,3));
		setSelectItem("跟親友聊天", new FamilyOnClickListener(msgBox));
		setSelectItem("自行處理",new SelfOnClickListener(msgBox));
		
	}

	@Override
	public void onPop() {
		contentSeq.remove(contentSeq.size()-1);
		seq.remove(seq.size()-1);
	}

}
