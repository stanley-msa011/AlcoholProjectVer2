package test.camera;

import ubicomp.drunk_detection.fragments.TestFragment;
import android.os.Handler;
import android.os.Message;

public class CameraInitHandler extends Handler {
	
	private TestFragment testFragment;
	private CameraRecorder cameraRecorder;
	
	public CameraInitHandler(TestFragment testFragment,CameraRecorder cameraRecorder){
		this.testFragment = testFragment;
		this.cameraRecorder = cameraRecorder;
	}
	
	public void handleMessage(Message msg){
		cameraRecorder.init();
		 cameraRecorder.setSurfaceCallback();
		 testFragment.updateInitState(TestFragment._CAMERA);
	}
}
