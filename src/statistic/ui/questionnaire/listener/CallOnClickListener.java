package statistic.ui.questionnaire.listener;

import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;
import statistic.ui.QuestionnaireBox;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

public class CallOnClickListener extends QuestionnaireOnClickListener {

	private String phone;
	private boolean isEmotion = false;
	
	public CallOnClickListener(QuestionnaireBox msgBox,String phone, boolean isEmotion) {
		super(msgBox);
		this.phone = phone;
		this.isEmotion = isEmotion;
	}

	@Override
	public void onClick(View v) {
		ClickLogger.Log(msgBox.getContext(), ClickLogId.STATISTIC_QUESTION_NEXT);
		if (!isEmotion)
			seq.add(",-1");
		msgBox.insertSeq();
		Intent intentDial = new Intent("android.intent.action.CALL",Uri.parse("tel:"+phone));
		msgBox.getContext().startActivity(intentDial);
		msgBox.closeBox();
	}

}
