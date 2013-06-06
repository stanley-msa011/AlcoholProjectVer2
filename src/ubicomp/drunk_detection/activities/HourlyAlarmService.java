package ubicomp.drunk_detection.activities;

import ubicomp.drunk_detection.activities.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class HourlyAlarmService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int onStartCommand(Intent intent, int flags,int startId){
		super.onStartCommand(intent, flags, startId);
		Log.e("BrACReceiver","Start AlarmService(Hourly)");   
		
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
		boolean start = sp.getBoolean("hourly_alarm", false);
		if (!start)
			return Service.START_REDELIVER_INTENT;
		
		Intent mIntent = new Intent(this, FragmentTabs.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0,mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification;
		
		if (Build.VERSION.SDK_INT>=16){
			Notification.Builder notificationBuilder = new Notification.Builder(getBaseContext());
			notificationBuilder.setContentTitle("戒酒小幫手");
			notificationBuilder.setContentText("您已停酒了一小時，請再繼續努力");
			notificationBuilder.setSmallIcon(R.drawable.icon);
			notificationBuilder.setContentIntent(pIntent);

			notification = notificationBuilder.build();
		}
		else{
			notification = new Notification();
			notification.contentIntent = pIntent;
			notification.icon = R.drawable.icon;
			notification.setLatestEventInfo(this,"戒酒小幫手","您已停酒了一小時，請再繼續努力", pIntent);
		}
		notification.defaults = Notification.DEFAULT_ALL;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0,notification);
		
		return Service.START_REDELIVER_INTENT;
	}

}
