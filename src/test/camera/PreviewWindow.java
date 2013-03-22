package test.camera;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PreviewWindow extends SurfaceView  implements SurfaceHolder.Callback{

	private Camera camera;
	
	public PreviewWindow(Context context,Camera camera) {
		super(context);
		this.camera = camera;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		  try {
			   camera.setPreviewDisplay(holder);
			   camera.startPreview();
			  } catch (Exception e) { }
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (camera != null){
			camera.stopPreview();
			camera.release();
		}
	}

}
