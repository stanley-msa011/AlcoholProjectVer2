package ioio.examples.hello;


import game.interaction.MsgService;

import java.util.Calendar;
import java.util.Date;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class TimerService extends Service {
	private Handler handler = new Handler();
	MediaPlayer mp;
	private long startTime;
	
	@Override
	public void onCreate(){
		Log.i("...","onCreate");
		Log.i("startTime = ",String.valueOf(startTime) );
		mp = MediaPlayer.create(this, R.raw.alcohol_test);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId){
		startTime = System.currentTimeMillis();
		Log.i("...","onStart");
		Log.i("startTime = ",String.valueOf(startTime) );
		handler.postDelayed(showTime, 1000);
		super.onStart(intent, startId);
		
	}
	
	@Override
	public void onDestroy(){
		handler.removeCallbacks(showTime);
		Log.i("...","onDestroy");
		Log.i("startTime = ",String.valueOf(startTime) );
		super.onDestroy();
	}
	
	private Runnable showTime = new Runnable(){
		public void run(){
			//log current time
			//Log.i("time :", new Date().toString() );
			//Log.i("startTime = ",String.valueOf(startTime) );
			handler.postDelayed(this, 1000);
			
			Long spentTime = System.currentTimeMillis() - startTime;
			//Log.i("spentTime start = ",String.valueOf(System.currentTimeMillis()) );

			Long minutes = (spentTime/1000)/60;
			//Long hour = minutes/60;
			Long seconds = (spentTime/1000)%60;
			
			if(minutes == 0 && seconds == 0){
				Log.i("一小時摟","hello");
			}
			
			Calendar calendar = Calendar.getInstance();
			int minute = calendar.get(Calendar.MINUTE);
			int second = calendar.get(Calendar.SECOND);
			
			
			if( minute % 5 == 0 && second == 0){
				
				
				mp.start();
				//get a reference to notificationManager
				String ns = Context.NOTIFICATION_SERVICE;
				NotificationManager notificationManager = (NotificationManager)getSystemService(ns);
				
				//Instantiate the notification
				int icon = R.drawable.ioio_icon_status;
				CharSequence tickerText = "Alc test!";
				long when = System.currentTimeMillis();
				
				long[] tVibrate = {0,100,200,300};

				Notification notification = new Notification(icon,tickerText,when);
				notification.vibrate = tVibrate;
				notification.flags = Notification.FLAG_AUTO_CANCEL;
				
				Context context = getApplicationContext();
				CharSequence contentTitle = "IOIO Alcohol test";
				CharSequence contentText = "該吹氣摟！！";
				Intent notificationIntent = new Intent(context, GameActivity.class);
				notificationIntent.putExtra("notify", true);
				PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
				notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
				notificationManager.notify(16, notification); //前面的只是一個tag而已 ＝ ＝
				
			}
			
			
		}
	};
	
}
