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
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;



import data.database.HistoryDB;
import data.info.BracDetectionState;

import ubicomp.drunk_detection.activities.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;

public class Reuploader {

	private static DataReuploader reuploader = null;
	
	public static void reuploader(Context context){
		if(!NetworkCheck.networkCheck(context))
			return;
		if (reuploader!=null){
				return;
		}
		if(SynchronizedLock.sharedLock.tryLock()){
			SynchronizedLock.sharedLock.lock();
			reuploader = new DataReuploader(context);
			reuploader.execute();
		}
	}
	
	public static void cancel(){
		if (reuploader!=null){
			if (!reuploader.isCancelled()){
				reuploader.cancel(true);
			}
		}
		QuestionnaireDataUploader.cancel();
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
		
		
		public DataReuploader(Context context){
			db = new HistoryDB(context);
			this.context = context;
			this.devId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			SERVER_URL = ServerUrl.SERVER_URL_TEST(sp.getBoolean("developer", false));
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			
			Log.d("REUPLOADER","START");
			
			 if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		        	mainStorageDir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
		        else
		        	mainStorageDir = new File(context.getFilesDir(),"drunk_detection");
			
			
			BracDetectionState state[] = db.getAllNotUploadedDetection();
			if (state == null)
				return null;
			
			Log.d("REUPLOADER","state.length "+state.length);
			
			for (int i=0;i<state.length;++i){
				String _ts =String.valueOf(state[i].timestamp/1000L);
				File[]  imageFiles;
				File textFile, geoFile,stateFile, questionFile;
				imageFiles = new File[3];
				
				textFile = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + _ts + ".txt");
		        geoFile = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "geo.txt");
		        
		        imageFiles[0] = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "IMG_" + _ts + "_1.jpg");
		        imageFiles[1] = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "IMG_" + _ts + "_2.jpg");
		        imageFiles[2] = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "IMG_" + _ts + "_3.jpg");
		        
		    	stateFile = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "state.txt");
		    	questionFile = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "question.txt");
		    	
		       	int result = connectingToServer(textFile,geoFile,stateFile,imageFiles,questionFile,_ts);
		        if (result == ERROR)
		        	break;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result ){
			reuploader = null;
			SynchronizedLock.sharedLock.unlock();
			Log.d("REUPLOADER","END");
			QuestionnaireDataUploader.uploader(context);
		}
		
		@Override
		protected void onCancelled(){
			reuploader = null;
			SynchronizedLock.sharedLock.unlock();
			Log.d("REUPLOADER","CANCEL");
		}
		
		private int connectingToServer(File textFile, File geoFile, File stateFile, File[] imageFiles, File questionFile, String ts){
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
				MultipartEntity mpEntity = new MultipartEntity();
				
				
				SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
				String uid = sp.getString("uid", "");
				mpEntity.addPart("userData[]", new StringBody(uid));
				mpEntity.addPart("userData[]", new StringBody(ts));
				mpEntity.addPart("userData[]", new StringBody(devId));
				
				Calendar c = Calendar.getInstance();
				
			    int mYear = sp.getInt("sYear", c.get(Calendar.YEAR));
			    int mMonth = sp.getInt("sMonth", c.get(Calendar.MONTH));
			    int mDay = sp.getInt("sDate", c.get(Calendar.DATE));
			    
			    
			    String joinDate = mYear+"-"+(mMonth+1)+"-"+mDay;
			    mpEntity.addPart("userData[]", new StringBody(joinDate));
				
			    PackageInfo pinfo;
				try {
					pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
					String versionName = pinfo.versionName;
					mpEntity.addPart("userData[]", new StringBody( versionName));
				} catch (NameNotFoundException e) {	}
			    
				ContentBody cbFile = new FileBody(textFile, "application/octet-stream");
				mpEntity.addPart("userfile[]", cbFile);
				if (geoFile.exists()){
					ContentBody cbGeoFile = new FileBody(geoFile, "application/octet-stream");
					mpEntity.addPart("userfile[]", cbGeoFile);
				}
				if(stateFile.exists()){
					ContentBody cbStateFile = new FileBody(stateFile, "application/octet-stream");
					mpEntity.addPart("userfile[]", cbStateFile);
				}
				if (questionFile.exists()){
					ContentBody cbQuestionFile = new FileBody(questionFile, "application/octet-stream");
					mpEntity.addPart("userfile[]", cbQuestionFile);
				}
				
				if (imageFiles[0].exists())
					mpEntity.addPart("userfile[]", new FileBody(imageFiles[0], "image/jpeg"));
				if (imageFiles[1].exists())
					mpEntity.addPart("userfile[]", new FileBody(imageFiles[1], "image/jpeg"));
				if (imageFiles[2].exists())
					mpEntity.addPart("userfile[]", new FileBody(imageFiles[2], "image/jpeg"));
				
				httpPost.setEntity(mpEntity);
				if (uploader(httpClient, httpPost,ts,context)){
					db.updateDetectionUploaded(Long.valueOf(ts)*1000L);
				}
				
			} catch (Exception e) {
				Log.d("REUPLOADER","EXCEPTION:"+e.toString());
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
					Log.d("UPLOADER","response = " + response);
					result &= (response.equals("10111") || response.equals("11111"));
				}
				Log.d("UPLOADER","result = "+result);
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

