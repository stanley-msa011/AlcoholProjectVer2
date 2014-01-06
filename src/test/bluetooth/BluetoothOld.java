package test.bluetooth;

import android.util.Log;
import test.camera.CameraRunHandler;
import test.data.BracValueDebugHandler;
import test.data.BracValueFileHandler;
import ubicomp.drunk_detection.fragments.TestFragment;

public class BluetoothOld extends Bluetooth {

	public BluetoothOld(TestFragment testFragment,
			CameraRunHandler cameraRunHandler,
			BracValueFileHandler bracFileHandler,
			BracValueDebugHandler bracDebugHandler) {
		super(testFragment, cameraRunHandler, bracFileHandler,true);
	}

	@Override
	public void close(){
		sendEnd();
		try {
			if (in != null)
				in.close();
		} catch (Exception e) {
			Log.e(TAG,"FAIL TO CLOSE THE SENSOR INPUTSTREAM");
		}
		try {
			if (out != null)
				out.close();
		} catch (Exception e) {
			Log.e(TAG,"FAIL TO CLOSE THE SENSOR OUTPUTSTREAM");
		}
		try {
			if (socket != null){
				socket.close();
			}
		} catch (Exception e) {
			Log.e(TAG,"FAIL TO CLOSE THE SENSOR");
		}
		if (bracFileHandler!= null)
			bracFileHandler.close();
	}
	
}
