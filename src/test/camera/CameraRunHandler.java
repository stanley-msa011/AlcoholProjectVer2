package test.camera;

import android.os.Handler;
import android.os.Message;

public class CameraRunHandler extends Handler {
	
	private CameraRecorder cameraRecorder;
	
	public CameraRunHandler(CameraRecorder cameraRecorder){
		this.cameraRecorder = cameraRecorder;
	}
	
	public void handleMessage(Message msg){
		cameraRecorder.takePicture();
	}
}
