package test.data;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import database.HistoryDB;

import ubicomp.drunk_detection.activities.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;

public class Reuploader {

	private static DataReuploader reuploader = null;
	
	public static void reuploader(Context context){
		cancel();
		reuploader = new DataReuploader(context);
		reuploader.execute();
	}
	
	public static void cancel(){
		if (reuploader!=null){
			if (!reuploader.isCancelled()){
				reuploader.cancel(true);
			}
		}
	}
	
	
	public static class DataReuploader extends AsyncTask<Void, Void, Void>{

		private HistoryDB db;
		private Context context;
		private File mainStorageDir;
		public static final int Nothing = 0; 
		public static final int ERROR = -1;
		public static final int SUCCESS = 1;
		//private static final String SERVER_URL = "https://140.112.30.165/develop/drunk_detect_upload.php";
		private static final String SERVER_URL = "https://140.112.30.165/develop/drunk_detection/drunk_detect_upload.php";
		private String devId;
		
		
		public DataReuploader(Context context){
			db = new HistoryDB(context);
			this.context = context;
			this.devId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			Log.d("reuploader","start");
			 if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		        	mainStorageDir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
		        else
		        	mainStorageDir = new File(context.getFilesDir(),"drunk_detection");
			
			
			long ts[] = db.getAllNotUploadedTS();
			if (ts == null){
				Log.d("reuploader","null");
				return null;
			}
			for (int i=0;i<ts.length;++i){
				String _ts = String.valueOf(ts[i]);
				File[]  imageFiles;
				File textFile, geoFile,stateFile;
				imageFiles = new File[3];
				
				textFile = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + _ts + ".txt");
		        geoFile = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "geo.txt");
		        
		        imageFiles[0] = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "IMG_" + _ts + "_1.jpg");
		        imageFiles[1] = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "IMG_" + _ts + "_2.jpg");
		        imageFiles[2] = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "IMG_" + _ts + "_3.jpg");
		        
		    	stateFile = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "state.txt");
		    	
		       	int result = connectingToServer(textFile,geoFile,stateFile,imageFiles,_ts);
		        if (result == ERROR)
		        	break;
			}
			return null;
		}
		
		private int connectingToServer(File textFile, File geoFile, File stateFile, File[] imageFiles, String ts){
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
				
				if (imageFiles[0].exists())
					mpEntity.addPart("userfile[]", new FileBody(imageFiles[0], "image/jpeg"));
				if (imageFiles[1].exists())
					mpEntity.addPart("userfile[]", new FileBody(imageFiles[1], "image/jpeg"));
				if (imageFiles[2].exists())
					mpEntity.addPart("userfile[]", new FileBody(imageFiles[2], "image/jpeg"));
				
				httpPost.setEntity(mpEntity);
				int result = uploader(httpClient, httpPost,ts,context);
				if (result == 1){
					db.removeNotUploadedTimeStamp(Long.valueOf(ts));
				}
				
			} catch (Exception e) {
				return ERROR;
			} 
			return SUCCESS;
		}
		
		private int uploader(HttpClient httpClient, HttpPost httpPost,String ts,Context context){
			Log.d("ReDataUploader","start reupload");
			HttpResponse httpResponse;
			int  result = -1;
			try {
				httpResponse = httpClient.execute(httpPost);
				int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
				if (httpStatusCode == HttpStatus.SC_OK)
					result = 1;
				else
					result = -1;
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			} finally{
				httpClient.getConnectionManager().shutdown();
			}
			return result;
		}
	}
	
	
	
}

