package ubicomp.drunk_detection.activities;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

import ubicomp.drunk_detection.check.DefaultCheck;

import debug.clicklog.ClickLogId;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class ClickLoggerService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags,int startId){
		
		if(DefaultCheck.check(getBaseContext()))
			return Service.START_REDELIVER_INTENT;
		
		long message = intent.getLongExtra(ClickLogId.LOG_MSG_ID, -1);
		long timestamp = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
		String date = sdf.format(timestamp);
		
		File dir = new File(Environment.getExternalStorageDirectory(), "drunk_detection/sequence_log_binary");
		if(!dir.exists()){
			dir.mkdirs();
		}
		
		File logFile =  new File(dir, date + ".txt");
		DataOutputStream ds = null; 
		try {
			ds = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(logFile,logFile.exists())));
			ds.writeLong(timestamp);
			ds.writeLong(message);
			ds.flush();
		} catch (Exception e) {
			Log.d("CLICK LOGGER","FAIL");
		} finally{
			try {
				ds.close();
			} catch (Exception e) {}
		}
		this.stopSelf();
		return startId;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		long message = arg0.getLongExtra(ClickLogId.LOG_MSG_ID, -1);
		long timestamp = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
		String date = sdf.format(timestamp);
		
		File dir = new File(Environment.getExternalStorageDirectory(), "drunk_detection/sequence_log_binary");
		if(!dir.exists()){
			dir.mkdirs();
		}
		
		File logFile =  new File(dir, date + ".txt");
		DataOutputStream ds = null; 
		try {
			ds = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(logFile,logFile.exists())));
			ds.writeLong(timestamp);
			ds.writeLong(message);
			ds.flush();
		} catch (Exception e) {
			Log.d("CLICK LOGGER","FAIL");
		} finally{
			try {
				ds.close();
			} catch (Exception e) {}
		}
		
		return null;
	}

}
