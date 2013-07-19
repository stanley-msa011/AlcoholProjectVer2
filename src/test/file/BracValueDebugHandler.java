package test.file;

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
	public BracValueDebugHandler(File directory, String timestamp){
		file = new File(directory,timestamp+"_debug.txt");
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			Log.d("BRAC DEBUG WRITER","FAIL TO OPEN");
			writer = null;
		}
	}
	
	public void handleMessage(Message msg){
		String str = msg.getData().getString("ALCOHOL_DEBUG");
		Log.d("DEBUG","BEFORE WRITE DEBUG = "+str);
		if (writer!=null){
			try {
				Log.d("BRAC DEBUG WRITER","SUCCESS");
				writer.write(str);
			} catch (IOException e) {
				Log.d("BRAC DEBUG WRITER","FAIL TO WRITE");
			}
		}else{
			Log.d("BRAC DEBUG WRITER","NULL TO WRITE");
		}
	}
	
	public void close(){
		if (writer!=null){
			try {
				Log.d("BRAC DEBUG WRITER","CLOSE");
				writer.close();
			} catch (IOException e) {
				Log.d("BRAC DEUBG WRITER","FAIL TO CLOSE");
			}
		}
	}
}
