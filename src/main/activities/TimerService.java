package main.activities;


import java.util.Calendar;

import database.HistoryDB;
import database.TimeBlock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class TimerService extends Service {
	private Handler handler;
	private HistoryDB db;
	private Service service;
	
	private int timeblock_type;
	
	@Override
	public void onCreate(){
		db = new HistoryDB(this);
		handler = new Handler();
		this.service = this;
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
		timeblock_type = sp.getInt("timeblock_num", 2);
	}

	@Override
	public int onStartCommand(Intent intent, int flags,int startId){
		super.onStartCommand(intent, flags, startId);
		handler.postDelayed(showTime,1000);
		return Service.START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy(){
		handler.removeCallbacks(showTime);
		super.onDestroy();
	}
	
	private Runnable showTime = new Runnable(){
		public void run(){
			
			Calendar calendar = Calendar.getInstance();
			int minute = calendar.get(Calendar.MINUTE);
			int second = calendar.get(Calendar.SECOND);
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			
			Log.d("serviceTime",String.valueOf(minute)+""+String.valueOf(second));
			
			while(true){
				if( minute%1 == 0 ){
					if (TimeBlock.isBlock(hour, timeblock_type)){
						if (!db.getIsDone(calendar)){
							Intent intent = new Intent(service, FragmentTabs.class);
							PendingIntent pIntent = PendingIntent.getActivity(service, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				
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
							
						}
					}
				}
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
}
