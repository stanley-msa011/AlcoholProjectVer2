package test.camera;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CameraRunHandler extends Handler {
	
	public CameraRecorder cameraRecorder;
	
	public CameraRunHandler(CameraRecorder cameraRecorder){
		this.cameraRecorder = cameraRecorder;
	}
	
	public void handleMessage(Message msg){
		if (msg.what == 0){
			cameraRecorder.takePicture();
		}else if (msg.what == 1){
			cameraRecorder.CloseFail();
		}
	}
	
	public void takePicture(){
		cameraRecorder.takePicture();
	}
	
	public void closeFail(){
		cameraRecorder.CloseFail();
	}
}
