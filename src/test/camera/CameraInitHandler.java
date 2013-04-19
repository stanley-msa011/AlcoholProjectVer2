package test.camera;

import main.activities.TestFragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CameraInitHandler extends Handler {
	
	private TestFragment testFragment;
	private CameraRecorder cameraRecorder;
	
	public CameraInitHandler(TestFragment testFragment,CameraRecorder cameraRecorder){
		this.testFragment = testFragment;
		this.cameraRecorder = cameraRecorder;
	}
	
	public void handleMessage(Message msg){
		Log.d("CAMERA","START SETTING");
		cameraRecorder.init();
		Log.d("CAMERA","END INIT");
		 cameraRecorder.setSurfaceCallback();
		 testFragment.updateInitState(TestFragment._CAMERA);
	}
}
