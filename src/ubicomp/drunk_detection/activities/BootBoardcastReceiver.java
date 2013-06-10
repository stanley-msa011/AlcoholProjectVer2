package ubicomp.drunk_detection.activities;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;



public class BootBoardcastReceiver extends BroadcastReceiver{

	private static final int requestCode = 0x2013;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("BrACReceiver",intent.getAction());
		
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		Intent service_intent = new Intent();
		service_intent.setClass(context, AlarmReceiver.class);
		service_intent.setAction("Regular_notification");
		
		Calendar c = Calendar.getInstance();

		int cur_year = c.get(Calendar.YEAR);
		int cur_month = c.get(Calendar.MONTH);
		int cur_date = c.get(Calendar.DAY_OF_MONTH);
		int cur_hour = c.get(Calendar.HOUR_OF_DAY);
		int cur_min = c.get(Calendar.MINUTE);
		
		if (cur_min < 29){
			if (cur_hour%2 == 0){
				c.set(cur_year, cur_month, cur_date, cur_hour, 30, 0);
			}else{
				c.set(cur_year, cur_month, cur_date, cur_hour, 30, 0);
				c.add(Calendar.HOUR_OF_DAY, 1);
			}
		}else{
			if (cur_hour%2 == 0){
				c.set(cur_year, cur_month, cur_date, cur_hour, 30, 0);
				c.add(Calendar.HOUR_OF_DAY, 2);
			}else{
				c.set(cur_year, cur_month, cur_date, cur_hour, 30, 0);
				c.add(Calendar.HOUR_OF_DAY, 1);
			}
		}
		
		PendingIntent pending = PendingIntent.getBroadcast(context, requestCode, service_intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.cancel(pending);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),2*AlarmManager.INTERVAL_HOUR,pending);
		
	}

}
