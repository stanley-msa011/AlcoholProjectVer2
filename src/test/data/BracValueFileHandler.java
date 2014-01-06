package test.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BracValueFileHandler extends Handler {
	private File file;
	private BufferedWriter writer;
	private File directory;
	private String timestamp;
	
	private static final String TAG = "BrAC_VALUE_HANDLER";
	
	public BracValueFileHandler(File directory, String timestamp){
		this.directory = directory;
		this.timestamp = timestamp;
		file = new File(directory,timestamp+".txt");
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			Log.d(TAG,"FAIL TO OPEN");
			writer = null;
		}
	}
	
	public String getTimestamp(){
		return timestamp;
	}
	
	public File getDirectory(){
		return directory;
	}
	
	public void handleMessage(Message msg){
		String str = msg.getData().getString("ALCOHOL");
		if (writer!=null){
			try {
				writer.write(str);
			} catch (IOException e) {
				Log.d(TAG,"FAIL TO WRITE");
			}
		}else{
			Log.d(TAG,"NULL TO WRITE");
		}
	}
	
	public void close(){
		if (writer!=null){
			try {
				writer.close();
			} catch (IOException e) {
				Log.d(TAG,"FAIL TO CLOSE");
			}
		}
	}
}
