package test.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressLint("ViewConstructor")
public class PreviewWindow extends SurfaceView  implements SurfaceHolder.Callback{

	private CameraRecorder cameraRecorder;
	
	public PreviewWindow(Context context,CameraRecorder cameraRecorder) {
		super(context);
		this.cameraRecorder = cameraRecorder;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		 try {
			   cameraRecorder.camera.setPreviewDisplay(holder);
			   cameraRecorder.camera.startPreview();
			  } catch (Exception e) { }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	public void restartPreview(){
		if (cameraRecorder.camera!=null){
			try{
				cameraRecorder.camera.stopPreview();
				cameraRecorder.camera.startPreview();
			}catch(Exception e){
				Log.d("preview window",e.getMessage().toString());
			}
		}
	}
	
}
