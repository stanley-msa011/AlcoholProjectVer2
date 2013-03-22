package test.file;

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
	public BracValueFileHandler(File directory, String timestamp){
		file = new File(directory,timestamp+".txt");
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			Log.d("BRAC WRITER","FAIL TO OPEN");
			writer = null;
		}
	}
	
	public void handleMessage(Message msg){
		String str = msg.getData().getString("ALCOHOL");
		if (writer!=null){
			try {
				Log.d("BRAC WRITER","WRITE");
				writer.write(str);
			} catch (IOException e) {
				Log.d("BRAC WRITER","FAIL TO WRITE");
			}
		}else{
			Log.d("BRAC WRITER","NULL TO WRITE");
		}
	}
	
	public void close(){
		if (writer!=null){
			try {
				writer.close();
			} catch (IOException e) {
				Log.d("BRAC WRITER","FAIL TO CLOSE");
			}
		}
	}
}
