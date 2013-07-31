package statistic.ui.questionnaire.content;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.listener.FamilyOnClickListener;
import statistic.ui.questionnaire.listener.SelectedListener;
import statistic.ui.questionnaire.listener.SelfOnClickListener;
import ubicomp.drunk_detection.activities.AlarmReceiver;
import ubicomp.drunk_detection.activities.R;

public class Type2Content extends QuestionnaireContent {

	public Type2Content(QuestionMsgBox msgBox) {
		super(msgBox);
	}

	private static final long HOUR = AlarmManager.INTERVAL_HOUR;
	
	@Override
	protected void setContent() {
		msgBox.setNextButton("", null);
		Context context=msgBox.getContext();
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent service_intent = new Intent();
		service_intent.setClass(context, AlarmReceiver.class);
		service_intent.setAction("Hourly_notification");
		
		PendingIntent pending = PendingIntent.getBroadcast(context, 0x9999, service_intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.cancel(pending);
		long trigger_time = HOUR + System.currentTimeMillis();
		alarm.set(AlarmManager.RTC_WAKEUP, trigger_time, pending);
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean("hourly_alarm", true);
    	editor.commit();
		
		seq.clear();
    	seq.add("3");
		msgBox.openBox();
		setHelp(R.string.question_type2_help);
		setSelectItem(R.string.self_help,new SelectedListener(msgBox,new SelfOnClickListener(msgBox),R.string.next));
		setSelectItem(R.string.connect_to_family, new SelectedListener(msgBox,new FamilyOnClickListener(msgBox),R.string.next));
		msgBox.showQuestionnaireLayout(true);
	}

	@Override
	public void onPop() {
		Context context=msgBox.getContext();
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent service_intent = new Intent();
		service_intent.setClass(context, AlarmReceiver.class);
		service_intent.setAction("Hourly_notification");
		
		PendingIntent pending = PendingIntent.getBroadcast(context, 0x9999, service_intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.cancel(pending);
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean("hourly_alarm", false);
    	editor.commit();
    	
    	contentSeq.remove(contentSeq.size()-1);
		seq.remove(seq.size()-1);
	}

}
