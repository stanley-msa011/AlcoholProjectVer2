package statistic.ui.questionnaire.content;

import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.listener.EndOnClickListener;

public class SolutionContent extends QuestionnaireContent {

	private static final String[] TEXT = {
		"離開有酒的場所",
		"請少喝酒",
		"請專心健走30分鐘",
		"請數息15次",
		"請休息或就診"
	};
	
	private int aid;
	public SolutionContent(QuestionMsgBox msgBox, int aid) {
		super(msgBox);
		this.aid = aid;
	}

	@Override
	protected void setContent() {
		seq.add(","+aid);
		setHelp("請依照以下指示:");
		setSelectItem(TEXT[aid-1],new EndOnClickListener(msgBox,-1));

	}

	@Override
	public void onPop() {
		contentSeq.remove(contentSeq.size()-1);
		seq.remove(seq.size()-1);
	}

}
