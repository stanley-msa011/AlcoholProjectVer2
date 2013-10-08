package test.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BracPressureHandler extends Handler {
	private File file;
	private BufferedWriter writer;
	public BracPressureHandler(File directory, String timestamp){
		file = new File(directory,timestamp+"_pressure.txt");
	}
	
	public void handleMessage(Message msg){
		String str = msg.getData().getString("pressure");
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			Log.d("BRAC PRESSURE WRITER","FAIL TO OPEN");
			writer = null;
		}
		if (writer!=null){
			try {
				writer.write(str);
			} catch (IOException e) {
				Log.d("BRAC PRESSURE WRITER","FAIL TO WRITE");
			}
		}else{
			Log.d("BRAC PRESSURE WRITER","NULL TO WRITE");
		}
		close();
	}
	
	private void close(){
		if (writer!=null){
			try {
				writer.close();
				writer = null;
			} catch (IOException e) {
				Log.d("BRAC PRESSURE WRITER","FAIL TO CLOSE");
			}
		}
	}
}
