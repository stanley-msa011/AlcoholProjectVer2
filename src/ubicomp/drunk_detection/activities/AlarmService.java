package ubicomp.drunk_detection.activities;

import java.util.Calendar;

import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.check.DefaultCheck;

import data.database.HistoryDB;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class AlarmService extends Service {

	private HistoryDB db;
	private static final String TAG = "ALARM_SERVICE";
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int onStartCommand(Intent intent, int flags,int startId){
		super.onStartCommand(intent, flags, startId);
		
		if(DefaultCheck.check(getBaseContext()))
			return Service.START_REDELIVER_INTENT;
		
		Log.d(TAG,"Regular Service");
		
		db = new HistoryDB(this);
		
		Calendar cal = Calendar.getInstance();
		
		if (db.getIsDone(cal))
			return Service.START_REDELIVER_INTENT;
		
		int cur_hour = cal.get(Calendar.HOUR_OF_DAY);
		if (cur_hour < 8)
			return Service.START_REDELIVER_INTENT;
		
		Intent mIntent = new Intent(this, FragmentTabs.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0,mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification;
		
		String title = getResources().getString(R.string.app_name);
		String msgText = getResources().getString(R.string.notification_msg_1);
		
		if (Build.VERSION.SDK_INT>=11){
		
			Notification.Builder notificationBuilder = new Notification.Builder(getBaseContext());
		
			notificationBuilder.setContentTitle(title);
			notificationBuilder.setContentText(msgText);
			notificationBuilder.setSmallIcon(R.drawable.icon);
			notificationBuilder.setContentIntent(pIntent);

			if (Build.VERSION.SDK_INT <16)
				notification = notificationBuilder.getNotification();
			else
				notification = notificationBuilder.build();
		
			notification.defaults = Notification.DEFAULT_ALL;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			
			NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(0,notification);
		}else{
			notification = new Notification();
			notification.defaults = Notification.DEFAULT_ALL;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.setLatestEventInfo(getBaseContext(), title, msgText,pIntent);
			NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(0,notification);
		}
		stopSelf();
		
		return Service.START_REDELIVER_INTENT;
	}

}
