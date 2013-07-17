package statistic.ui.questionnaire.listener;

import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.content.QuestionnaireContent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class SelectedListener implements OnClickListener {

	private QuestionMsgBox msgBox;
	private View.OnClickListener listener;
	private String str;
	
	public SelectedListener(QuestionMsgBox msgBox, View.OnClickListener listener,String str){
		this.msgBox = msgBox;
		this.listener = listener;
		this.str = str;
	}
	
	@Override
	public void onClick(View v) {
		msgBox.cleanSelection();
		ImageView img = (ImageView) v.findViewById(QuestionnaireContent.QUESTION_IMAGE_ID);
		img.setImageDrawable(msgBox.getChoiceSelectedDrawable());
		msgBox.setNextButton(str, listener);
	}

}
