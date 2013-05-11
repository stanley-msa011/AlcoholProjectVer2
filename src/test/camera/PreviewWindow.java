package test.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

@SuppressLint("ViewConstructor")
public class PreviewWindow extends SurfaceView  implements SurfaceHolder.Callback{

	private CameraRecorder cameraRecorder;
	private PreviewWindow previewWindow;
	private SurfaceHolder surfaceHolder;
	private FaceListener faceListener;
	private Paint paint;
	
	public PreviewWindow(Context context,CameraRecorder cameraRecorder) {
		super(context);
		this.previewWindow = this;
		this.surfaceHolder = this.getHolder();
		this.cameraRecorder = cameraRecorder;
		faceListener = new FaceListener();
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
			 cameraRecorder.camera.setFaceDetectionListener(faceListener);
			 cameraRecorder.camera.setPreviewDisplay(holder);
			 cameraRecorder.camera.startPreview();
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
				cameraRecorder.camera.stopFaceDetection();
				cameraRecorder.camera.stopPreview();
				cameraRecorder.camera.startPreview();
				cameraRecorder.camera.setFaceDetectionListener(faceListener);
				cameraRecorder.camera.startFaceDetection();
			}catch(Exception e){
				Log.d("preview window",e.getMessage().toString());
			}
		}
	}
	
	public class FaceListener implements FaceDetectionListener{
		@Override
		public void onFaceDetection(Face[] faces, Camera camera) {
			if (faces.length>0){
				for (int i=faces.length-1;i<faces.length;++i){
					Rect rect = faces[i].rect;
					cameraRecorder.drawFace(rect);
				}
			}
		}
	}
}
