package statistic.ui.questionnaire.content;

import statistic.ui.QuestionMsgBox2;
import statistic.ui.questionnaire.listener.EmotionCallOnClickListener;
import statistic.ui.questionnaire.listener.FamilyOnClickListener;
import statistic.ui.questionnaire.listener.SocialCallOnClickListener;

public class Type1Content extends QuestionnaireContent {

	public Type1Content(QuestionMsgBox2 msgBox) {
		super(msgBox);
	}

	@Override
	protected void setContent() {
		contentSeq.clear();
		seq.clear();
		seq.add("2,2");
		msgBox.openBox();
		setHelp("您似乎想要喝酒，\n請問您要?");
		setSelectItem("尋求親友協助", new FamilyOnClickListener(msgBox));
		setSelectItem("心情專線", new EmotionCallOnClickListener(msgBox));
		setSelectItem("社區心理諮商", new SocialCallOnClickListener(msgBox));
	}

	@Override
	public void onPop() {
		contentSeq.remove(contentSeq.size()-1);
		seq.remove(seq.size()-1);
	}

}
