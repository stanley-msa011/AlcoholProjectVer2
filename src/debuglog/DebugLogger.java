package debuglog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

public class DebugLogger {
	@SuppressLint("SimpleDateFormat")
	public void debug_logging(String debug_log){
		
		File rootDir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
		if(!rootDir.exists()){
			rootDir.mkdir();
		}
		
		File logDir = new File(rootDir, "debug_log");
		if(!logDir.exists()){
			logDir.mkdir();
		}
		
		File file = new File(logDir, "debug.txt");
		BufferedWriter writer;
		try {			
			writer = new BufferedWriter(new FileWriter(file, true));
			writer.write(System.currentTimeMillis() + "\t" + debug_log);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Log.d("Logging","FAIL TO OPEN");
			writer = null;
		}
	}
}
