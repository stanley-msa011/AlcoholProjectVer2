package ubicomp.drunk_detection.activities;

import ubicomp.drunk_detection.check.DefaultCheck;
import ubicomp.drunk_detection.check.LockCheck;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	private static final String TAG = "ALARM_RECEIVER";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(DefaultCheck.check(context))
			return;
		if (LockCheck.check(context))
			return;
		
		if (intent.getAction()=="") return;
		if (intent.getAction().equals("Regular_notification")){
			Log.d(TAG,"Regular");
			Intent a_intent = new Intent(context,AlarmService.class);
			context.startService(a_intent);
		} else if (intent.getAction().equals("Hourly_notification")){
			Log.d(TAG,"Hourly");
			Intent a_intent = new Intent(context,HourlyAlarmService.class);
			context.startService(a_intent);
		} else if (intent.getAction().equals("Regular_check")){
			Log.d(TAG,"Regular Check");
			Intent a_intent = new Intent(context,RegularCheckService.class);
			context.startService(a_intent);
		}
	}

}
