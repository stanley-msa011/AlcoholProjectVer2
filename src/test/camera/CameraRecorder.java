package test.camera;

import java.util.Iterator;
import java.util.List;

import test.Tester;
import test.data.ImageFileHandler;
import ubicomp.drunk_detection.ui.ScreenSize;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

@SuppressLint("InlinedApi")
public class CameraRecorder {
    
    private CameraCaller cameraCaller;
    private Context context;
    protected Camera camera;
    protected Camera.PictureCallback pictureCallback;
    protected ImageFileHandler imgFileHandler;
    
    private PreviewWindow preview;
    private FrameLayout previewFrame = null;
    private RelativeLayout previewCircleLayout = null;
    private SurfaceHolder previewHolder;
    private ImageView circle;
    
    public int picture_count=0;
    
    public CameraRecorder(Context context,CameraCaller cameraCaller, ImageFileHandler imgFileHandler){
    	this.cameraCaller = cameraCaller;
    	this.context = context;
    	this.imgFileHandler =  imgFileHandler;
    	imgFileHandler.setRecorder(this);
    	pictureCallback = new PictureCallback();
    }
    
    public void init(){
    	picture_count = 0;
    	int camera_count = 1;
    	if (Build.VERSION.SDK_INT>=9)
    		camera_count = Camera.getNumberOfCameras();
		if (camera_count > 1)
			camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
		else
			camera = Camera.open();
		
		camera.setDisplayOrientation(90);
		Parameters params = camera.getParameters();
		List<Size> list = params.getSupportedPictureSizes();
		Point bestSize = getBestSize(list);
		params.setPictureSize(bestSize.x, bestSize.y);
		try{
			camera.setParameters(params);
		}catch(Exception e){}
		
		setSurfaceCallback();
    }
    
    protected Point getBestSize(List<Size> list){
		int bestWidth = Integer.MAX_VALUE;
		int bestHeight = Integer.MAX_VALUE;
		if (list.size()>0){
			Iterator<Camera.Size> iter = list.iterator();
			while(iter.hasNext()){
				Camera.Size cur = iter.next();
				if(cur.width < bestWidth && cur.height < bestHeight){
					bestWidth = cur.width;
					bestHeight = cur.height;
				}
			}
		}
		return new Point(bestWidth,bestHeight);
    }
    
    public void start(){
    	if (preview!=null)
    		preview.setVisibility(View.VISIBLE);
    }
    
    @SuppressWarnings("deprecation")
	private void setSurfaceCallback(){
    	previewFrame = null;
    	circle = new ImageView(context);
    	circle.setBackgroundColor(0xAAAACCFF);
    	previewFrame =cameraCaller.getPreviewFrameLayout();
    	previewCircleLayout = new RelativeLayout(context);
    	if (previewFrame!=null){
    		preview = new PreviewWindow(context,this);
    		previewHolder = preview.getHolder();
    		if (Build.VERSION.SDK_INT < 11)
				previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    		previewHolder.addCallback(preview);
    		previewFrame.addView(preview);
    		preview.setVisibility(View.INVISIBLE);
    	}
    	if (previewFrame!=null)
    		previewFrame.addView(previewCircleLayout);
    	previewCircleLayout.addView(circle);
    	circle.setVisibility(View.INVISIBLE);
    	RelativeLayout.LayoutParams cParam = (RelativeLayout.LayoutParams) circle.getLayoutParams();
    	Point screen = ScreenSize.getScreenSize(context);
    	cParam.width = (int)(screen.x * 320.0/720.0);
    	cParam.height = (int)(screen.x * 320.0/720.0);
    }
    
    
    public void takePicture(){
    	if (camera!=null)
    		camera.takePicture(null,null, pictureCallback);
    }
    
    
    public void close(){
    	if (preview !=null)
    		preview.setVisibility(View.INVISIBLE);
    	if (previewFrame!=null)
    		previewFrame.removeView(preview);
    	if (camera!=null){
    		Camera tmp = camera;
    		camera = null;
    		tmp.stopPreview();
    		tmp.release();
    		tmp = null;
    	}
    	
    }
    
    public class PictureCallback implements Camera.PictureCallback{
    	
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			picture_count++;
			
			Message msg = new Message();
			Bundle data_b = new Bundle();
			data_b.putByteArray("Img", data);
			msg.setData(data_b);
			msg.what=picture_count;
			imgFileHandler.sendMessage(msg);
			preview.restartPreview();
		}
    }
    
    public void CloseSuccess(){
    	close();
    	circle.setVisibility(View.INVISIBLE);
		cameraCaller.updateDoneState(Tester._CAMERA);
    }
    
    public void CloseFail(int type){
    	close();
    	if (circle!=null)
    		circle.setVisibility(View.INVISIBLE);
    	cameraCaller.stopByFail(type);
    }
    
    public void drawFace(boolean detected){
    	if (detected)
    		circle.setVisibility(View.VISIBLE);
    	else
    		circle.setVisibility(View.INVISIBLE);
    }
}
