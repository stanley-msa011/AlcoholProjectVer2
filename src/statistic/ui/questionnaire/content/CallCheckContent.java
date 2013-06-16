package statistic.ui.questionnaire.content;

import statistic.ui.QuestionMsgBox2;
import statistic.ui.questionnaire.listener.CallOnClickListener;

public class CallCheckContent extends QuestionnaireContent {

	private String name,phone;
	
	public CallCheckContent(QuestionMsgBox2 msgBox,String name, String phone) {
		super(msgBox);
		this.name = name;
		this.phone = phone;
	}
	private boolean isEmotion = false;
	public CallCheckContent(QuestionMsgBox2 msgBox,String name, String phone,boolean isEmotion) {
		super(msgBox);
		this.name = name;
		this.phone = phone;
		this.isEmotion = isEmotion;
	}

	@Override
	protected void setContent() {
		if (isEmotion)
			seq.add(",1");
		setHelp("確定要撥給 "+name +" ?");
		setSelectItem("確定",new CallOnClickListener(msgBox));
	}

	@Override
	public void onPop() {
		if (isEmotion)
			seq.remove(seq.size()-1);
		contentSeq.remove(contentSeq.size()-1);
	}

}
