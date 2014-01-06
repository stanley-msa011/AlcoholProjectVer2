package test.camera;

import test.Tester;
import android.os.Handler;
import android.os.Message;

public class CameraInitHandler extends Handler {
	
	private Tester tester;
	private CameraRecorder cameraRecorder;
	
	public CameraInitHandler(Tester tester,CameraRecorder cameraRecorder){
		this.tester = tester;
		this.cameraRecorder = cameraRecorder;
	}
	
	public void handleMessage(Message msg){
		cameraRecorder.init();
		tester.updateInitState(Tester._CAMERA);
	}
}
