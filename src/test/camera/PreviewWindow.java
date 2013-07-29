package test.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressLint("ViewConstructor")
public class PreviewWindow extends SurfaceView  implements SurfaceHolder.Callback{

	private CameraRecorder cameraRecorder;
	private SurfaceHolder surfaceHolder;
	private FaceListener faceListener;
	private Paint paint;
	private boolean face;
	
	public PreviewWindow(Context context,CameraRecorder cameraRecorder) {
		super(context);
		this.surfaceHolder = this.getHolder();
		this.cameraRecorder = cameraRecorder;
		faceListener = new FaceListener();
		face = (cameraRecorder.camera.getParameters().getMaxNumDetectedFaces()>0);
		face = false;
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
			
			if (face)
				cameraRecorder.camera.setFaceDetectionListener(faceListener);
			 cameraRecorder.camera.setPreviewDisplay(holder);
			 cameraRecorder.camera.startPreview();
			 if (face)
				 cameraRecorder.camera.startFaceDetection();
		 } catch (Exception e) { }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	public void restartPreview(){
		if (cameraRecorder.camera!=null){
			try{
				cameraRecorder.camera.setFaceDetectionListener(null);
				if (face)
					cameraRecorder.camera.stopFaceDetection();
				cameraRecorder.camera.stopPreview();
				cameraRecorder.camera.startPreview();
				if (face){
					cameraRecorder.camera.setFaceDetectionListener(faceListener);
					cameraRecorder.camera.startFaceDetection();
				}
			}catch(Exception e){
				Log.d("PREVIEW WINDOW",e.getMessage().toString());
			}
		}
	}
	
	public class FaceListener implements FaceDetectionListener{
		@Override
		public void onFaceDetection(Face[] faces, Camera camera) {
			if (faces.length>0){
				cameraRecorder.drawFace(true);
			}
			else{
				cameraRecorder.drawFace(false);
			}
		}
	}
}
