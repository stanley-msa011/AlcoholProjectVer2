package test.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BracValueDebugHandler extends Handler {
	private File file;
	private BufferedWriter writer;
	
	private static final String TAG = "BrAC_DEBUG_HANDLER";
	public BracValueDebugHandler(File directory, String timestamp){
		file = new File(directory,timestamp+"_debug.txt");
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			Log.d(TAG,"FAIL TO OPEN");
			writer = null;
		}
	}
	
	public void handleMessage(Message msg){
		String str = msg.getData().getString("ALCOHOL_DEBUG");
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
				Log.d("BRAC DEUBG WRITER","FAIL TO CLOSE");
			}
		}
	}
}
