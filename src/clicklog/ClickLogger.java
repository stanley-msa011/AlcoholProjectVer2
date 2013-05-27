package clicklog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

public class ClickLogger {
	@SuppressLint("SimpleDateFormat")
	public void click_logging(long timestamp_millis, String click_log){
		Log.d("Eric", "logging called:" + timestamp_millis + "," + click_log);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp_millis);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
		String date = sdf.format(cal.getTime());
		
		File rootDir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
		if(!rootDir.exists()){
			rootDir.mkdir();
		}
		
		File logDir = new File(rootDir, "sequence_log");
		if(!logDir.exists()){
			logDir.mkdir();
		}
		
		File file = new File(logDir, date + ".txt");
		BufferedWriter writer;
		try {			
			writer = new BufferedWriter(new FileWriter(file, true));
			writer.write(timestamp_millis/1000 + "\t" + click_log);
			writer.newLine();
			writer.flush();
			writer.close();
			long end_time = System.currentTimeMillis();
			Log.d("Eric","Done, elapsed time=" + ((end_time - timestamp_millis)) + "ms");
		} catch (IOException e) {
			Log.d("BRAC WRITER","FAIL TO OPEN");
			writer = null;
		}
	}
}
