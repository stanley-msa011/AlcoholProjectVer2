package test.bluetooth;

import android.content.Context;
import android.util.Log;
import test.camera.CameraRunHandler;
import test.data.BracValueDebugHandler;
import test.data.BracValueFileHandler;

public class BluetoothOld extends Bluetooth {

	public BluetoothOld(Context context, BluetoothDebugger debugger, BluetoothMessageUpdater updater,
			CameraRunHandler cameraRunHandler,
			BracValueFileHandler bracFileHandler,
			BracValueDebugHandler bracDebugHandler) {
		super(context, debugger, updater,  cameraRunHandler, bracFileHandler,true);
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
