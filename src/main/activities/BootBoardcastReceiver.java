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
	static public final long HOURMILLIS = 60*60*1000;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("BrACReceiver",intent.getAction());
		
		//SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		//timeblock_type = sp.getInt("timeblock_num", 2);
		
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		//Calendar[] cal = TimeBlock.getCalendar(context, timeblock_type);
		
		//Log.d("BrACReceiver","type:"+String.valueOf(cal.length));
		
		Intent service_intent = new Intent();
		service_intent.setClass(context, AlarmReceiver.class);

		
		Calendar c_init = Calendar.getInstance();
		Calendar c = Calendar.getInstance();

		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND,0);
		c.set(Calendar.MILLISECOND, 0);
		int add_hour;
		if (c.get(Calendar.HOUR_OF_DAY)%2 == 1)
			add_hour = 1;
		else
			add_hour = 2;
		
		c.add(Calendar.HOUR_OF_DAY, add_hour);
		long time = c.getTimeInMillis()-c_init.getTimeInMillis();
		
		PendingIntent pending = PendingIntent.getBroadcast(context, requestCode, service_intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.cancel(pending);
		alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, time,HOURMILLIS,pending);
		
		//Testing
		/*Long minHalfMillis = 30L*1000L; 
		PendingIntent pending = PendingIntent.getBroadcast(context, requestCode+0xAA, service_intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.cancel(pending);
		alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 10000,minHalfMillis,pending);
		*/
		/*
		PendingIntent pending2 = PendingIntent.getBroadcast(context, requestCode+0xAAA, service_intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.cancel(pending2);
		alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 100000,AlarmManager.INTERVAL_DAY, pending2);*/
		
	}

}
