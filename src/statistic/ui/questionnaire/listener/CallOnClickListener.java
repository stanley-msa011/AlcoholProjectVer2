package statistic.ui.questionnaire.listener;

import statistic.ui.QuestionMsgBox;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

public class CallOnClickListener extends QuestionnaireOnClickListener {

	private String phone;
	
	public CallOnClickListener(QuestionMsgBox msgBox,String phone) {
		super(msgBox);
		this.phone = phone;
	}

	@Override
	public void onClick(View v) {
		seq.add(",-1");
		msgBox.insertSeq();
		Intent intentDial = new Intent("android.intent.action.CALL",Uri.parse("tel:"+phone));
		msgBox.getContext().startActivity(intentDial);
		msgBox.closeBox();
	}

}