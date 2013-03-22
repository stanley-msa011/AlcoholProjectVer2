package test.camera;

import ioio.examples.hello.TestFragment;

import android.os.AsyncTask;
import android.util.Log;

public class CameraInitTask extends AsyncTask<Void, Void, Void> {

	private TestFragment testFragment;
	private CameraRecorder cameraRecorder;
	
	public CameraInitTask(TestFragment testFragment,CameraRecorder cameraRecorder){
		this.testFragment = testFragment;
		this.cameraRecorder = cameraRecorder;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		Log.d("CAMERA","START SETTING");
		cameraRecorder.init();
		return null;
	}

	 protected void onPostExecute(Void result) {
		 Log.d("CAMERA","END INIT");
		 cameraRecorder.setSurfaceCallback();
		 testFragment.updateInitState(TestFragment._CAMERA);
    }
	
}
