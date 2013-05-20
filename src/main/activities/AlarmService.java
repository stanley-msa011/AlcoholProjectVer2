package main.activities;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import database.HistoryDB;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class AlarmService extends Service {

	private HistoryDB db;
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags,int startId){
		super.onStartCommand(intent, flags, startId);
		Log.e("BrACReceiver","Start AlarmService");   
		
		db = new HistoryDB(this);
		
		Calendar cal = Calendar.getInstance();
		
		if (db.getIsDone(cal))
			return Service.START_REDELIVER_INTENT;
		
		Intent mIntent = new Intent(this, FragmentTabs.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0,mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification.Builder notificationBuilder = new Notification.Builder(getBaseContext());
		notificationBuilder.setContentTitle("戒酒小幫手");
		notificationBuilder.setContentText("該吹氣了!");
		notificationBuilder.setSmallIcon(R.drawable.icon);
		notificationBuilder.setContentIntent(pIntent);

		Notification notification = notificationBuilder.build();
		notification.defaults = Notification.DEFAULT_ALL;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0,notification);
		
		return Service.START_REDELIVER_INTENT;
	}

}
