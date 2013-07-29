package test.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import test.camera.CameraRecorder;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ImageFileHandler extends Handler {
	private File file;
	private FileOutputStream writer;
	private File directory;
	private String timestamp;
	private CameraRecorder recorder;
	
	
	public ImageFileHandler(File directory, String timestamp){
		this.directory = directory;
		this.timestamp = timestamp;
	}
	
	public void setRecorder(CameraRecorder recorder){
		this.recorder = recorder;
	}
	
	public void handleMessage(Message msg){
		
		int count = msg.what;
		String file_name = "IMG_"+timestamp+"_"+count+".jpg";
		
		file = new File(directory,file_name);
		byte[] img = msg.getData().getByteArray("Img");
		try {
			writer = new FileOutputStream(file);
			writer.write(img);
			writer.close();
		} catch (IOException e) {
			Log.d("IMAGE WRITER","FAIL TO OPEN");
			try {
				writer.close();
			} catch (IOException e1) {}
			writer = null;
		}
		if (count == 3){
			recorder.CloseSuccess();
		}
	}
}
