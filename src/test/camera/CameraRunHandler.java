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
		else if (msg.what == 1)//on connection
			cameraRecorder.CloseFail(0);
		else if (msg.what == 2)//No battery
			cameraRecorder.CloseFail(1);
		else if (msg.what == 3)//timeout
			cameraRecorder.CloseFail(2);
		else if (msg.what == 4)//blow_twice
			cameraRecorder.CloseFail(3);
		else if (msg.what == 5)//zero duration
			cameraRecorder.CloseFail(4);
	}
	
}
