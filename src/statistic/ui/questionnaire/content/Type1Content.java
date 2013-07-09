package statistic.ui.questionnaire.content;

import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.listener.EmotionCallOnClickListener;
import statistic.ui.questionnaire.listener.FamilyOnClickListener;
import statistic.ui.questionnaire.listener.SocialCallOnClickListener;

public class Type1Content extends QuestionnaireContent {

	public Type1Content(QuestionMsgBox msgBox) {
		super(msgBox);
	}

	@Override
	protected void setContent() {
		contentSeq.clear();
		seq.clear();
		seq.add("2");
		msgBox.openBox();
		setHelp("重新整理浮動的心，\n堅持持續戒酒的決心，\n建議你可以：");
		setSelectItem("跟親友聊天", new FamilyOnClickListener(msgBox));
		setSelectItem("聯絡心情專線", new EmotionCallOnClickListener(msgBox));
		setSelectItem("尋求社區心理諮商", new SocialCallOnClickListener(msgBox));
	}

	@Override
	public void onPop() {
		contentSeq.remove(contentSeq.size()-1);
		seq.remove(seq.size()-1);
	}

}
