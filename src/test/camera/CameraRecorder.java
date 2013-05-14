package test.camera;

import java.util.Iterator;
import java.util.List;

import main.activities.FragmentTabs;
import main.activities.R;
import main.activities.TestFragment;

import test.file.ImageFileHandler;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class CameraRecorder {
    
    private TestFragment testFragment;
    private Activity activity;
    Camera camera;
    private PictureCallback pictureCallback;
    private ImageFileHandler imgFileHandler;
    
    private PreviewWindow preview;
    private FrameLayout previewFrame = null;
    private RelativeLayout previewCircleLayout = null;
    SurfaceHolder previewHolder;
    private ImageView circle;
    private RelativeLayout.LayoutParams pParam;
    
    public int picture_count=0;
    
    public CameraRecorder(TestFragment testFragment, ImageFileHandler imgFileHandler){
    	this.testFragment = testFragment;
    	this.activity = testFragment.getActivity();
    	this.imgFileHandler =  imgFileHandler;
    	imgFileHandler.setRecorder(this);
    	pictureCallback = new PictureCallback();
    }
    
    public void init(){
    	picture_count = 0;
    	int camera_count = Camera.getNumberOfCameras();
		if (camera_count > 1)
			camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
		else
			camera = Camera.open();
		
		camera.setDisplayOrientation(90);
		Parameters params = camera.getParameters();
		List<Size> list = params.getSupportedPictureSizes();
		Point bestSize = getBestSize(list);
		params.setPictureSize(bestSize.x, bestSize.y);
		camera.setParameters(params);
		camera.startFaceDetection();
    }
    
    private Point getBestSize(List<Size> list){
		int bestWidth = Integer.MAX_VALUE;
		int bestHeight = Integer.MAX_VALUE;
		if (list.size()>1){
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
    	preview.setVisibility(View.VISIBLE);
    }
    
    public void setSurfaceCallback(){
    	previewFrame = null;
    	circle = new ImageView(activity);
    	circle.setBackgroundColor(0xAAAACCFF);
    	previewFrame =(FrameLayout) activity.findViewById(R.id.test_camera_preview_layout);
    	previewCircleLayout = new RelativeLayout(activity);
    	pParam = (RelativeLayout.LayoutParams) previewFrame.getLayoutParams();
    	if (previewFrame!=null){
    		preview = new PreviewWindow(activity,this);
    		previewHolder = preview.getHolder();
    		previewHolder.addCallback(preview);
    		previewFrame.addView(preview);
    		preview.setVisibility(View.INVISIBLE);
    	}
    	previewFrame.addView(previewCircleLayout);
    	previewCircleLayout.addView(circle);
    	circle.setVisibility(View.INVISIBLE);
    	RelativeLayout.LayoutParams cParam = (RelativeLayout.LayoutParams) circle.getLayoutParams();
    	Point screen = FragmentTabs.getSize();
    	cParam.width = (int)(screen.x * 320.0/720.0);
    	cParam.height = (int)(screen.x * 320.0/720.0);
    }
    
    
    public void takePicture(){
    		Log.d("TAKE PICTURE","TAKE PICTURE");
    		camera.takePicture(null,null, pictureCallback);
    }
    
    
    public void close(){
    	if (preview !=null)
    		preview.setVisibility(View.INVISIBLE);
    	if (previewFrame!=null){
    		previewFrame.removeView(preview);
    	}
    	if (camera!=null){
    		Camera tmp = camera;
    		camera = null;
    		tmp.stopFaceDetection();
    		tmp.stopPreview();
    		tmp.release();
    		tmp = null;
    	}
    	
    }
    
    public class PictureCallback implements Camera.PictureCallback{
    	
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			picture_count++;
			Log.d("CAMERA","TAKE PICTURE "+picture_count);
			
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
		testFragment.updateDoneState(TestFragment._CAMERA);
    }
    
    public void CloseFail(){
    	close();
    	circle.setVisibility(View.INVISIBLE);
    	testFragment.stopByFail();
    }
    
    public void drawFace(boolean detected){
    	if (detected)
    		circle.setVisibility(View.VISIBLE);
    	else
    		circle.setVisibility(View.INVISIBLE);
    }
}
