package database;

import game.BracDataHandler;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;

public class Reuploader {

	private static Object lock;
	private ReuploadDB rDB;
	private Context context;
	private static reUploadData rud;
	private static Thread runThread;
	
	public Reuploader(Context activity){
		rDB = new ReuploadDB(activity);
		context = activity;
		if (lock == null)
			lock = new Object();
	}
	
	
	public void storeTS(String ts){
		synchronized(lock){
			rDB.storeNotUploadedTimeStamp(ts);
		}
	}
	
	
	public void reTransmission(){
		String[] TSs = null;
		synchronized(lock){
				TSs = rDB.getNotUploadedTimeStamps();
		}
		boolean start_run = false;
		if (rud == null){
			rud = new reUploadData(TSs);
			start_run = true;
		}
		else
			start_run = rud.reSetting(TSs);
		
		if (start_run){
			runThread = new Thread(rud);
			runThread.run();
		}
		try {
			runThread.join();
		} catch (InterruptedException e) {
		}
		Log.d("REUPLOAD","END");
	}
	
	
	private class reUploadData implements Runnable{

		private static final int ERROR = -1;
		private boolean isRunning = false;
		
		String[] TSs;
		
		public reUploadData(String[] ts){
			this.TSs = ts;
		}
		
		public boolean reSetting(String[] ts){
			if (isRunning) return false;
			TSs = ts;
			return true;
		}
		
		@Override
		public void run() {
			isRunning = true;
			if (TSs == null){
				isRunning = false;
				return;
			}
			for (int i=0;i<TSs.length;++i){
				String ts = TSs[i];
				Log.d("RETRAN","UPLOADING "+ts);
				File mainStorageDir;
				File textFile, geoFile, stateFile;
				File[] imageFiles = new File[3];

		        if(Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED))
		           break;
		        else
		        	mainStorageDir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
		        
		        textFile = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + ts + ".txt");
		        geoFile = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "geo.txt");
		        
		        boolean hasGeoFile = geoFile.exists();
		        
		        imageFiles[0] = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "IMG_" + ts + "_1.jpg");
		        imageFiles[1] = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "IMG_" + ts + "_2.jpg");
		        imageFiles[2] = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "IMG_" + ts + "_3.jpg");
		       	       	
		       
		       	stateFile = new File(mainStorageDir.getPath() + File.separator + ts + File.separator + "state.txt");
		    	boolean hasStateFile = stateFile.exists();
		       	
		    	String devId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		       	int server_connect = BracDataHandler.connectingToServer(textFile,geoFile,stateFile,imageFiles,hasGeoFile,hasStateFile,ts,devId);
				if (server_connect == ERROR)
					break;
				else
					rDB.removeNotUploadedTimeStamp(ts);
			}
			isRunning = false;
		}
		
	}
	
}
