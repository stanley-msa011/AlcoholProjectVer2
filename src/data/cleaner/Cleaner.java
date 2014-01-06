package data.cleaner;

import java.io.File;

import data.database.HistoryDB;
import data.info.BracDetectionState;
import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class Cleaner {

	public static void clean(Context context){
		
		long cur_ts = System.currentTimeMillis()/1000L - AlarmManager.INTERVAL_DAY/1000L;
		
		SharedPreferences sp =PreferenceManager.getDefaultSharedPreferences(context);
		if (!sp.getBoolean("auto_clean", true))
			return;
		
		Log.d("cleaner","start cleaning");
		HistoryDB db = new HistoryDB(context);
		BracDetectionState state[] = db.getAllNotUploadedDetection();
		
		 File mainStorageDir;
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
	        	mainStorageDir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
	        else
	        	mainStorageDir = new File(context.getFilesDir(),"drunk_detection");
		if (!mainStorageDir.exists())
			return;
		
		
		for (File file:mainStorageDir.listFiles()){
			String name = file.getName();
			if (name == null)
				continue;
			if (name.contains("audio_records"))
				continue;
			if (name.contains("sequence_log_binary"))
				continue;
			if (state!=null){
				boolean uploaded = true;
				for (int j=0;j<state.length;++j){
					String ts = String.valueOf(state[j].timestamp/1000L);
					if (name.contains(ts)){
						uploaded = false;
						break;
					}
				}
				if (!uploaded)
					continue;
			}
			try{
				long dir_time= Long.valueOf(name);
				if (dir_time > cur_ts){
					Log.d("cleaner","cur_ts "+dir_time+" "+cur_ts);
					continue;
				}
			}catch(Exception e){	}
			if (file.isDirectory()){
				Log.d("cleaner","delete "+name);
				recursiveDelete(file);
			}
		}
	}
	
	private static void recursiveDelete(File file){
		if (file.isDirectory()){
			for (File child : file.listFiles()){
				recursiveDelete(child);
			}
		}
		file.delete();
	}
}
