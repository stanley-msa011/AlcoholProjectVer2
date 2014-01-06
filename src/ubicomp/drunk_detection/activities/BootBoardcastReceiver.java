package ubicomp.drunk_detection.activities;

import java.util.Calendar;

import data.uploader.ClickLogUploader;
import data.uploader.DataUploader;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;



public class BootBoardcastReceiver extends BroadcastReceiver{

	private static final int requestCode = 0x2013;
	private static final int requestCode2 = 0x2014;
	
	private static final String TAG = "BOOT_BC_RECEIVER";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction();
		
		Log.d(TAG,"BootBroadcastReceiver - "+action);
		
		if (action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_TIMEZONE_CHANGED)){
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			SharedPreferences.Editor edit = sp.edit();
			edit.putLong("latest_regular_check", 0L);
			edit.putLong("LatestTestTime", 0L);
			edit.putLong("share_storytelling_time", 0);
			edit.putLong("LatestGPSTime", 0);
			edit.putString("LatestGPSTimestamp", "0");
			/*edit.putLong("LatestEmotionDIYTime", 0L);
			edit.putLong("LatestEmotionManageTime", 0L);
			edit.putLong("LatestStorytellingSharingTime", 0L);
			edit.putLong("LatestStorytellingRecordingTime", 0L);*/
			edit.commit();
		}
		
		testNotificationSetting(context,intent);
		regularCheckSetting(context, intent);
	}
	
	public static void testNotificationSetting(Context context, Intent intent){
		
		Log.d(TAG,"notification_setting");
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		int notification_minutes = sp.getInt("notification_gap", 120);
		long notification_gap = notification_minutes * 60 * 1000;
		
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
		
		if (notification_minutes == 120){
			if (cur_min < 30){
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
		}else if (notification_minutes == 60){
			if (cur_min < 30){
				c.set(cur_year, cur_month, cur_date, cur_hour, 30, 0);
			}else{
				c.set(cur_year, cur_month, cur_date, cur_hour, 30, 0);
				c.add(Calendar.HOUR_OF_DAY, 1);
			}
		}else if (notification_minutes == 30){
			if (cur_min < 30){
				c.set(cur_year, cur_month, cur_date, cur_hour, 30, 0);
			}else{
				c.set(cur_year, cur_month, cur_date, cur_hour, 0, 0);
				c.add(Calendar.HOUR_OF_DAY, 1);
			}
		}else{
			//do not change c
		}
		
		PendingIntent pending = PendingIntent.getBroadcast(context, requestCode, service_intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.cancel(pending);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis()+10,notification_gap,pending);
	}

	
	public static void regularCheckSetting(Context context, Intent intent){
		Log.d(TAG,"regular_check_setting");
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		Intent check_intent = new Intent();
		check_intent.setClass(context, AlarmReceiver.class);
		check_intent.setAction("Regular_check");
		
		PendingIntent pending2 = PendingIntent.getBroadcast(context, requestCode2, check_intent, PendingIntent.FLAG_CANCEL_CURRENT);
		alarm.cancel(pending2);
		alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+10000, AlarmManager.INTERVAL_HALF_HOUR, pending2);
		
		
		DataUploader.upload(context);
		ClickLogUploader.upload(context);
		Intent regularCheckIntent = new Intent(context,RegularCheckService.class);
		context.startService(regularCheckIntent);
	}
}
