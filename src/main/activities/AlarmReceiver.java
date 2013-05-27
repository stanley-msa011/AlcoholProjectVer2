package main.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("BrACReceiver","Start AlarmReceiver");
		if (intent.getAction().equals("Regular_notification")){
			Log.e("BrACReceiver","Regular");
			Intent a_intent = new Intent(context,AlarmService.class);
			context.startService(a_intent);
		} else if (intent.getAction().equals("Hourly_notification")){
			Log.e("BrACReceiver","Hourly");
			Intent a_intent = new Intent(context,HourlyAlarmService.class);
			context.startService(a_intent);
		}
	}

}
