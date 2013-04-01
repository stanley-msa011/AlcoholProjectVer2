package test.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

import test.file.ImageFileHandler;

import ioio.examples.hello.R;
import ioio.examples.hello.TestFragment;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;

public class CameraRecorder {
    
    private TestFragment testFragment;
    private Activity activity;
    private Camera camera;
    private Camera.PictureCallback pictureCallback;
    private int picture_count;
    private ImageFileHandler imgFileHandler;
    
    private PreviewWindow preview;
    private FrameLayout previewFrame = null;
    private SurfaceHolder previewHolder;
    
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
			params.setPictureSize(bestWidth, bestHeight);
			camera.setParameters(params);
		}
    }
    
    public void start(){
    	preview.setVisibility(View.VISIBLE);
    }
    
    public void setSurfaceCallback(){
    	previewFrame = null;
    	previewFrame =(FrameLayout) activity.findViewById(R.id.new_camera_preview);
    	preview = new PreviewWindow(activity,camera);
    	previewHolder = preview.getHolder();
    	previewHolder.addCallback(preview);
    	previewFrame.addView(preview);
    	preview.setVisibility(View.INVISIBLE);
    	//camera.startPreview();
    }
    
    public void takePicture(){
    	picture_count++;
    	camera.takePicture(null,null, pictureCallback);
    }
    
    
    public void close(){
    	if (previewFrame!=null){
    		previewFrame.removeAllViews();
    		previewFrame.removeAllViewsInLayout();
    	}
    	if (camera!=null){
    		camera.stopPreview();
    		camera.release();
    		camera = null;
    	}
    }
    
    private class PictureCallback implements Camera.PictureCallback{

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d("CAMERA","TAKE PICTURE "+picture_count);
			
			Message msg = new Message();
			Bundle data_b = new Bundle();
			data_b.putByteArray("Img", data);
			msg.setData(data_b);
			msg.what=picture_count;
			
			imgFileHandler.sendMessage(msg);
		}
    }
    
    public void CloseSuccess(){
    	close();
		testFragment.updateDoneState(TestFragment._CAMERA);
    }
    
    public void CloseFail(){
    	close();
    	testFragment.stop();
    }
    
}
