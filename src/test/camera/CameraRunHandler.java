package test.camera;

import android.os.Handler;
import android.os.Message;

public class CameraRunHandler extends Handler {
	
	public CameraRecorder cameraRecorder;
	
	public CameraRunHandler(CameraRecorder cameraRecorder){
		this.cameraRecorder = cameraRecorder;
	}
	
	public void handleMessage(Message msg){
		if (msg.what == 0)
			cameraRecorder.takePicture();
		else if (msg.what == 1)
			cameraRecorder.CloseFail(false);
		else if (msg.what == 2)
			cameraRecorder.CloseFail(true);
	}
	
	public void takePicture(){
		cameraRecorder.takePicture();
	}
}
