package main.activities;

import java.util.Calendar;

import database.HistoryDB;
import database.TimeBlock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;



public class BootBoardcastReceiver extends BroadcastReceiver{

	private static final int requestCode = 0x2013;
	
	private int timeblock_type;
	
	static public final long DAYMILLIS = 60*60*24*1000;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("BrACReceiver",intent.getAction());
		
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		timeblock_type = sp.getInt("timeblock_num", 2);
		
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		Calendar[] cal = TimeBlock.getCalendar(context, timeblock_type);
		
		Log.d("BrACReceiver","type:"+String.valueOf(cal.length));
		
		Intent service_intent = new Intent();
		service_intent.setClass(context, AlarmReceiver.class);

		PendingIntent pending = PendingIntent.getBroadcast(context, requestCode, service_intent, 0);

		alarm.cancel(pending);
		
		Calendar c = Calendar.getInstance();
		
		for (int i=0;i<cal.length;++i){
			long time;
			if (cal[i].after(c))
				time = cal[i].getTimeInMillis() - c.getTimeInMillis();
			else
				time = cal[i].getTimeInMillis() + DAYMILLIS - c.getTimeInMillis();
			alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, time,AlarmManager.INTERVAL_DAY, pending);
		}
		
		//Testing
		//alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 12000,AlarmManager.INTERVAL_DAY, pending);
		
	}

}
