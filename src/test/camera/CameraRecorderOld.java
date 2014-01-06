package test.camera;

import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import test.data.ImageFileHandler;

public class CameraRecorderOld extends CameraRecorder {

	public CameraRecorderOld(Context context,CameraCaller caller,
			ImageFileHandler imgFileHandler) {
		super(context, caller, imgFileHandler);
	}

    public void init(){
    	picture_count = 0;
		camera = Camera.open();
		Parameters params = camera.getParameters();
		List<Size> list = params.getSupportedPictureSizes();
		Point bestSize = getBestSize(list);
		params.setPictureSize(bestSize.x, bestSize.y);
		camera.setParameters(params);
    }
    
}
