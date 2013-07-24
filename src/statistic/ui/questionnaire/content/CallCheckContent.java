package statistic.ui.questionnaire.content;

import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.listener.CallOnClickListener;
import ubicomp.drunk_detection.activities.R;

public class CallCheckContent extends QuestionnaireContent {

	private String name,phone;
	
	public CallCheckContent(QuestionMsgBox msgBox,String name, String phone) {
		super(msgBox);
		this.name = name;
		this.phone = phone;
	}
	private boolean isEmotion = false;
	public CallCheckContent(QuestionMsgBox msgBox,String name, String phone,boolean isEmotion) {
		super(msgBox);
		this.name = name;
		this.phone = phone;
		this.isEmotion = isEmotion;
	}

	@Override
	protected void setContent() {
		msgBox.setNextButton("", null);
		if (isEmotion){
			seq.add(",1");
			setHelp(R.string.call_check_help_emotion_hot_line);
		}
		else{
			String call_check = msgBox.getContext().getResources().getString(R.string.call_check_help);
			String question_sign = msgBox.getContext().getResources().getString(R.string.question_sign);
			setHelp(call_check+" "+name +" "+question_sign);
		}
		msgBox.showQuestionnaireLayout(false);
		msgBox.setNextButton(R.string.ok,new CallOnClickListener(msgBox,phone));
	}

	@Override
	public void onPop() {
		if (isEmotion)
			seq.remove(seq.size()-1);
		contentSeq.remove(contentSeq.size()-1);
	}

}
