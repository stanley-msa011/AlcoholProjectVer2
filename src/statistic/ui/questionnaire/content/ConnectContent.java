package statistic.ui.questionnaire.content;

import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.listener.CallCheckOnClickListener;

public class ConnectContent extends QuestionnaireContent {

	String[] dummyNames = {
			"dummy1",
			"dummy2",
			"dummy3"
			};
	String[] dummyPhones = {
			"0212345678",
			"0312345678",
			"0412345678"
			};
	
	private int type;
	public static final int TYPE_FAMILY = 2, TYPE_SOCIAL = 3;
	
	public ConnectContent(QuestionMsgBox msgBox,int type) {
		super(msgBox);
		this.type = type;
	}

	@Override
	protected void setContent() {
		seq.add(","+type+",5");
		setHelp("要打給誰？");
		for (int i=0;i<dummyNames.length;++i){
			setSelectItem(dummyNames[i]+":"+dummyPhones[i],new CallCheckOnClickListener(msgBox,dummyNames[i],dummyPhones[i]));
		}
	}

	@Override
	public void onPop() {
		contentSeq.remove(contentSeq.size()-1);
		seq.remove(seq.size()-1);
	}

}
