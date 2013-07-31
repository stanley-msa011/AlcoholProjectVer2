package statistic.ui.questionnaire.listener;

import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLoggerLog;
import statistic.ui.QuestionMsgBox;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

public class CallOnClickListener extends QuestionnaireOnClickListener {

	private String phone;
	private boolean isEmotion = false;
	
	public CallOnClickListener(QuestionMsgBox msgBox,String phone, boolean isEmotion) {
		super(msgBox);
		this.phone = phone;
		this.isEmotion = isEmotion;
	}

	@Override
	public void onClick(View v) {
		ClickLoggerLog.Log(msgBox.getContext(), ClickLogId.STATISTIC_QUESTION_NEXT);
		if (!isEmotion)
			seq.add(",-1");
		msgBox.insertSeq();
		Intent intentDial = new Intent("android.intent.action.CALL",Uri.parse("tel:"+phone));
		msgBox.getContext().startActivity(intentDial);
		msgBox.closeBox();
	}

}
