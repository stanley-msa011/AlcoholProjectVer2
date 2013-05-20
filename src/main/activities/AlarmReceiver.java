package main.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("BrACReceiver","Start AlarmReceiver");   
		Intent a_intent = new Intent(context,AlarmService.class);
		context.startService(a_intent);
	}

}
