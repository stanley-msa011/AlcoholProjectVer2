package test.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressLint("ViewConstructor")
public class PreviewWindow extends SurfaceView  implements SurfaceHolder.Callback{

	private CameraRecorder cameraRecorder;
	private SurfaceHolder surfaceHolder;
	private Paint paint;
	
	public PreviewWindow(Context context,CameraRecorder cameraRecorder) {
		super(context);
		this.surfaceHolder = this.getHolder();
		this.cameraRecorder = cameraRecorder;
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(10);
		
		if (surfaceHolder.getSurface().isValid()){
			Canvas canvas = surfaceHolder.lockCanvas();
			int save_count = canvas.getSaveCount();
			if (save_count > 0)
				canvas.restore();
			else
				canvas.save();
			
			canvas.drawCircle(50, 50, 40, paint);
		}
		
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
			}catch(Exception e){	}
		}
	}
}
