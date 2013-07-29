package debug.debuglog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class DebugLoggingThread extends AsyncTask<Void, Void, Void> {
	BufferedWriter writer;
	File logFile;

	public DebugLoggingThread(){
		Log.d("ALCOHOLDEBUG", "DebugLoggingThread constructed");
		
		// open drunk_detection/debug_log/debug_log.txt and mkdirs if necessary
		File rootDir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
		if(!rootDir.exists()){
			rootDir.mkdir();
		}
		
		File logDir = new File(rootDir, "debug_log");
		if(!logDir.exists()){
			logDir.mkdir();
		}
		
		logFile = new File(logDir, "debug_log.txt");
	}

	protected Void doInBackground(Void... params) {
		Log.d("ALCOHOLDEBUG", "debug thread in background");
		try {
			writer = new BufferedWriter(new FileWriter(logFile, true));
			Process process = Runtime.getRuntime().exec("logcat -d -s ALCOHOLDEBUG");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			String line;
			while ((line = bufferedReader.readLine()) != null && !this.isCancelled()) {
				writer.write(line);
				writer.flush();
			}
		} catch (Exception e) {
			Log.d("debugLogging", e.getMessage());
		}
		
		return null;
	}
	
	protected void onPostExecute(){
		Log.d("debugLogging", "debug thread Ended");
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
