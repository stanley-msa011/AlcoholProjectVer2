package data.uploader;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Calendar;

import network.NetworkCheck;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;



import data.cleaner.Cleaner;
import data.database.HistoryDB;
import data.info.BracDetectionState;

import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.check.DefaultCheck;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;

public class DataUploader {

	private static DataReuploader uploader = null;
	
	private static Thread cleanThread = null;
	
	private static final String TAG = "UPLOADER";
	
	public static void upload(final Context context){
		
		if (cleanThread !=null && !cleanThread.isInterrupted()){
			cleanThread.interrupt();
			cleanThread = null;
		}
		
		cleanThread = new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					Cleaner.clean(context);
				}catch(Exception e){	}
			}
		});
		cleanThread.start();
		if(DefaultCheck.check(context))
			return;
		
		if(!NetworkCheck.networkCheck(context))
			return;
		if (uploader!=null){
				return;
		}
		if(SynchronizedLock.sharedLock.tryLock()){
			SynchronizedLock.sharedLock.lock();
			uploader = new DataReuploader(context);
			uploader.execute();
		}
	}
	
	public static void cancel(){
		if (uploader!=null){
			if (!uploader.isCancelled()){
				uploader.cancel(true);
			}
		}
		AdditionalDataUploader.cancel();
	}
	
	
	public static class DataReuploader extends AsyncTask<Void, Void, Void>{

		private HistoryDB db;
		private Context context;
		private File mainStorageDir;
		public static final int Nothing = 0; 
		public static final int ERROR = -1;
		public static final int SUCCESS = 1;
		private static String SERVER_URL;
		private String devId;
		
		private SharedPreferences sp;
		public DataReuploader(Context context){
			db = new HistoryDB(context);
			this.context = context;
			this.devId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
			 sp = PreferenceManager.getDefaultSharedPreferences(context);
			SERVER_URL = ServerUrl.SERVER_URL_TEST(sp.getBoolean("developer", false));
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			
			 if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		        	mainStorageDir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
		        else
		        	mainStorageDir = new File(context.getFilesDir(),"drunk_detection");
			
			BracDetectionState state[] = db.getAllNotUploadedDetection();
			if (state == null)
				return null;
			
			
			for (int i=0;i<state.length;++i){
				String _ts =String.valueOf(state[i].timestamp/1000L);
				File[]  imageFiles;
				File textFile, geoFile,stateFile, questionFile, pressureFile;
				imageFiles = new File[3];
				
				textFile = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + _ts + ".txt");
		        geoFile = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "geo.txt");
		        pressureFile = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + _ts + "_pressure.txt");
		        
		        imageFiles[0] = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "IMG_" + _ts + "_1.sob");
		        if (!imageFiles[0].exists())
		        	 imageFiles[0] = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "IMG_" + _ts + "_1.jpg");
		        imageFiles[1] = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "IMG_" + _ts + "_2.sob");
		        if (!imageFiles[1].exists())
		        	 imageFiles[1] = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "IMG_" + _ts + "_2.jpg");
		        imageFiles[2] = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "IMG_" + _ts + "_3.sob");
		        if (!imageFiles[2].exists())
		        	 imageFiles[2] = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "IMG_" + _ts + "_3.jpg");
		        
		    	stateFile = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "state.txt");
		    	questionFile = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "question.txt");
		    	
		       	int result = connectingToServer(textFile,geoFile,stateFile,imageFiles,questionFile,pressureFile,_ts);
		        if (result == ERROR)
		        	break;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result ){
			uploader = null;
			SynchronizedLock.sharedLock.unlock();
			AdditionalDataUploader.upload(context);
		}
		
		@Override
		protected void onCancelled(){
			uploader = null;
			SynchronizedLock.sharedLock.unlock();
		}
		
		private int connectingToServer(File textFile, File geoFile, File stateFile, File[] imageFiles, File questionFile, File pressureFile, String ts){
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
				InputStream instream = context.getResources().openRawResource(R.raw.alcohol_certificate);
				try{
					trustStore.load(instream, null);
				} finally{
					instream.close();
				}
				SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
				Scheme sch = new Scheme("https",socketFactory,443);
				
				httpClient.getConnectionManager().getSchemeRegistry().register(sch);
				
				HttpPost httpPost = new HttpPost(SERVER_URL);
				httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
				SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
				String uid = sp.getString("uid", "");
				
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.addTextBody("userData[]", uid);
				builder.addTextBody("userData[]", ts);
				builder.addTextBody("userData[]", devId);
				
				Calendar c = Calendar.getInstance();
			    int mYear = sp.getInt("sYear", c.get(Calendar.YEAR));
			    int mMonth = sp.getInt("sMonth", c.get(Calendar.MONTH));
			    int mDay = sp.getInt("sDate", c.get(Calendar.DATE));
			    String joinDate = mYear+"-"+(mMonth+1)+"-"+mDay;
			    builder.addTextBody("userData[]", joinDate);
				
			    PackageInfo pinfo;
				try {
					pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
					String versionName = pinfo.versionName;
					builder.addTextBody("userData[]", versionName);
				} catch (NameNotFoundException e) {	}
			    
				if (textFile.exists())
					builder.addPart("userfile[]", new FileBody(textFile));
				if (geoFile.exists())
					builder.addPart("userfile[]", new FileBody(geoFile));
				if (stateFile.exists())
					builder.addPart("userfile[]", new FileBody(stateFile));
				if (questionFile.exists())
					builder.addPart("userfile[]", new FileBody(questionFile));
				if (pressureFile.exists())
					builder.addPart("userfile[]", new FileBody(pressureFile));
				for (int i=0;i<3;++i)
					if (imageFiles[i].exists())
						builder.addPart("userfile[]", new FileBody(imageFiles[i]));
				
				httpPost.setEntity(builder.build());
//				MultipartEntity mpEntity = new MultipartEntity();
//				
//				mpEntity.addPart("userData[]", new StringBody(uid));
//				mpEntity.addPart("userData[]", new StringBody(ts));
//				mpEntity.addPart("userData[]", new StringBody(devId));
//				
//				Calendar c = Calendar.getInstance();
//				
//			    int mYear = sp.getInt("sYear", c.get(Calendar.YEAR));
//			    int mMonth = sp.getInt("sMonth", c.get(Calendar.MONTH));
//			    int mDay = sp.getInt("sDate", c.get(Calendar.DATE));
//			    
//			    
//			    String joinDate = mYear+"-"+(mMonth+1)+"-"+mDay;
//			    mpEntity.addPart("userData[]", new StringBody(joinDate));
//				
//			    PackageInfo pinfo;
//				try {
//					pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//					String versionName = pinfo.versionName;
//					mpEntity.addPart("userData[]", new StringBody( versionName));
//				} catch (NameNotFoundException e) {	}
//			    
//				ContentBody cbFile = new FileBody(textFile, "application/octet-stream");
//				mpEntity.addPart("userfile[]", cbFile);
//				if (geoFile.exists()){
//					ContentBody cbGeoFile = new FileBody(geoFile, "application/octet-stream");
//					mpEntity.addPart("userfile[]", cbGeoFile);
//				}
//				if(stateFile.exists()){
//					ContentBody cbStateFile = new FileBody(stateFile, "application/octet-stream");
//					mpEntity.addPart("userfile[]", cbStateFile);
//				}
//				if (questionFile.exists()){
//					ContentBody cbQuestionFile = new FileBody(questionFile, "application/octet-stream");
//					mpEntity.addPart("userfile[]", cbQuestionFile);
//				}
//				
//				if (pressureFile.exists()){
//					ContentBody cbPressureFile = new FileBody(pressureFile, "application/octet-stream");
//					mpEntity.addPart("userfile[]", cbPressureFile);
//				}
//				
//				if (imageFiles[0].exists())
//					mpEntity.addPart("userfile[]", new FileBody(imageFiles[0], "image/jpeg"));
//				if (imageFiles[1].exists())
//					mpEntity.addPart("userfile[]", new FileBody(imageFiles[1], "image/jpeg"));
//				if (imageFiles[2].exists())
//					mpEntity.addPart("userfile[]", new FileBody(imageFiles[2], "image/jpeg"));
//				
//				httpPost.setEntity(mpEntity);
				if (uploader(httpClient, httpPost,ts,context)){
					db.updateDetectionUploaded(Long.valueOf(ts)*1000L);
				}
				
			} catch (Exception e) {
				Log.d(TAG,"EXCEPTION:"+e.toString());
				return ERROR;
			} 
			return SUCCESS;
		}
		
		private boolean uploader(HttpClient httpClient, HttpPost httpPost,String ts,Context context){
			HttpResponse httpResponse;
			ResponseHandler <String> res=new BasicResponseHandler();  
			boolean  result = false;
			try {
				httpResponse = httpClient.execute(httpPost);
				int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
				result = (httpStatusCode == HttpStatus.SC_OK);
				if (result){
					String response = res.handleResponse(httpResponse).toString();
					Log.d(TAG,"response = " + response);
					result &= (response.equals("10111") || response.equals("11111"));
				}
				Log.d(TAG,"result = "+result);
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			} finally{
				if ( httpClient!=null){
					ClientConnectionManager ccm= httpClient.getConnectionManager();
						if (ccm!=null)
							ccm.shutdown();
					}
			}
			return result;
		}
	}
	
	
	
}

