package statistic.ui.questionnaire.content;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import statistic.ui.QuestionMsgBox2;
import statistic.ui.questionnaire.listener.FamilyOnClickListener;
import statistic.ui.questionnaire.listener.SelfOnClickListener;
import ubicomp.drunk_detection.activities.AlarmReceiver;

public class Type2Content extends QuestionnaireContent {

	public Type2Content(QuestionMsgBox2 msgBox) {
		super(msgBox);
	}

	@Override
	protected void setContent() {
		
		Context context=msgBox.getContext();
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent service_intent = new Intent();
		service_intent.setClass(context, AlarmReceiver.class);
		service_intent.setAction("Hourly_notification");
		
		PendingIntent pending = PendingIntent.getBroadcast(context, 0x9999, service_intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.cancel(pending);
		long trigger_time = 3600*1000L + System.currentTimeMillis();
		alarm.set(AlarmManager.RTC_WAKEUP, trigger_time, pending);
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean("hourly_alarm", true);
    	editor.commit();
		
    	contentSeq.clear();
		seq.clear();
    	seq.add("3,3");
		msgBox.openBox();
		setHelp("您似乎有飲酒，\n請問您要?\n並啟用一小時停酒提醒");
		setSelectItem("自行處理",new SelfOnClickListener(msgBox));
		setSelectItem("尋求親友協助", new FamilyOnClickListener(msgBox));
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
