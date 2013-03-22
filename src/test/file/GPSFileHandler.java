package test.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GPSFileHandler extends Handler {
	private File file;
	private BufferedWriter writer;
	public GPSFileHandler(File directory, String timestamp){
		file = new File(directory,"geo.txt");
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			Log.d("GEO WRITER","FAIL TO OPEN");
			writer = null;
		}
	}
	
	public void handleMessage(Message msg){
		String str = msg.getData().getString("GEO");
		if (writer!=null){
			try {
				Log.d("GEO WRITER","WRITE");
				writer.write(str);
			} catch (IOException e) {
				Log.d("GEO WRITER","FAIL TO WRITE");
			}
		}else{
			Log.d("GEO WRITER","NULL TO WRITE");
		}
	}
	
	public void close(){
		if (writer!=null){
			try {
				writer.close();
			} catch (IOException e) {
				Log.d("GEO WRITER","FAIL TO CLOSE");
			}
		}
	}
}
