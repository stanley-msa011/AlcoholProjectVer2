package ubicomp.drunk_detection.activities;

import java.util.Calendar;

import ubicomp.drunk_detection.activities.R;

import data.database.HistoryDB;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class AlarmService extends Service {

	private HistoryDB db;
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int onStartCommand(Intent intent, int flags,int startId){
		super.onStartCommand(intent, flags, startId);
		
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
		
		if (Build.VERSION.SDK_INT>=16){
			Notification.Builder notificationBuilder = new Notification.Builder(getBaseContext());
			
			notificationBuilder.setContentTitle(title);
			notificationBuilder.setContentText(msgText);
			notificationBuilder.setSmallIcon(R.drawable.icon);
			notificationBuilder.setContentIntent(pIntent);

			notification = notificationBuilder.build();
		}
		else{
			notification = new Notification();
			notification.contentIntent = pIntent;
			notification.icon = R.drawable.icon;
			notification.setLatestEventInfo(this,title,msgText, pIntent);
		}
		notification.defaults = Notification.DEFAULT_ALL;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0,notification);
		return Service.START_REDELIVER_INTENT;
	}

}
