package data.uploader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.check.DefaultCheck;

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


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class ClickLogUploader {
	
	private static ClickLogUploaderThread clickUploader = null;
	
	private static final String TAG = "Click Log Uploader";
	
	public static void upload(Context context){
		if(DefaultCheck.check(context))
			return;
		
		if(!NetworkCheck.networkCheck(context))
			return;
		if (clickUploader!=null)
			return;
			clickUploader = new ClickLogUploaderThread(context);
			clickUploader.execute();
	}
	
	public static void cancel(){
		if (clickUploader!=null){
			if (!clickUploader.isCancelled()){
				clickUploader.cancel(true);
			}
		}
	}
	
	
	public static class ClickLogUploaderThread extends AsyncTask<Void, Void, Void>{

		private Context context;
		private File logDir;
		public static final int Nothing = 0; 
		public static final int ERROR = -1;
		public static final int SUCCESS = 1;
		private static String SERVER_URL;
		
		public ClickLogUploaderThread(Context context){
			this.context = context;
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			SERVER_URL = ServerUrl.SERVER_URL_CLICKLOG(sp.getBoolean("developer", false));
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {

			Log.d(TAG, "START");
			
			String not_uploaded_files[] = getNotUploadedFiles();
			if (not_uploaded_files == null){
				Log.d(TAG, "no logFile needed to upload");
				return null;
			}
			
			for (int i=0; i<not_uploaded_files.length; ++i){
				File logFile = new File(logDir.getPath(), not_uploaded_files[i]);
				if(logFile.exists()){
					Log.d(TAG, "file = "+logFile.getPath());
					int result = connectingToServer(logFile);
					if(result == ERROR)
						break;
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			clickUploader = null;
			SynchronizedLock.sharedLock.unlock();
			Log.d(TAG, "END");
		}
		
		@Override
		protected void onCancelled(){
			clickUploader = null;
			SynchronizedLock.sharedLock.unlock();
			Log.d(TAG, "CANCEL");
		}
		
		private String[] getNotUploadedFiles() {
			Log.d(TAG,"get not uploaded files");
			logDir = new File(Environment.getExternalStorageDirectory(), "drunk_detection/sequence_log_binary");
			if(!logDir.exists()){
				return null;
			}
			
			String latestUpload = null;
			File latestUploadFile = new File(logDir, "latest_uploaded");
			if(latestUploadFile.exists()){	
				try {
				    BufferedReader br = new BufferedReader(new FileReader(latestUploadFile));
				    latestUpload = br.readLine();
				}catch (IOException e) {
				    Log.d("Click Log Uploade", "Error when reading latest_uploaded file");
				}
			}
			
			String[] all_logs = logDir.list(new logFilter(latestUpload));
			Log.d(TAG,"get all logs");
			
			return all_logs;
		}
		
		class logFilter implements FilenameFilter{
			String _latestUpload;
			String today;
			
			@SuppressLint("SimpleDateFormat")
			public logFilter(String latestUpload){
				_latestUpload = latestUpload;
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
				today = sdf.format(cal.getTime()) + ".txt";
			}

			@Override
			public boolean accept(File arg0, String arg1) {
				if(arg1.equals("latest_uploaded")){
					return false;
				}
				else{
					if(today.compareTo(arg1) > 0){
						if(_latestUpload == null || (_latestUpload != null && (arg1.compareTo(_latestUpload)) > 0)){
							return true;
						}
					}
					return false;
				}
			}
			
		}

		private int connectingToServer(File logFile){
			try {
				Log.d(TAG, "upload logFile connecting to server");
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
				if (logFile.exists()){
					builder.addPart("userfile[]", new FileBody(logFile));
				}
				httpPost.setEntity(builder.build());
				
//				MultipartEntity mpEntity = new MultipartEntity();
//				
//				mpEntity.addPart("userData[]", new StringBody(uid));			
//				
//				if (logFile.exists()){
//					ContentBody cbLogFile = new FileBody(logFile, "application/octet-stream");
//					mpEntity.addPart("userfile[]", cbLogFile);
//				}
//				httpPost.setEntity(mpEntity);
				if (uploader(httpClient, httpPost,context)){
					Log.d(TAG, "success upload logFile: " + logFile.getName());
					set_uploaded_logfile(logFile.getName());
				}
				
			} catch (Exception e) {
				return ERROR;
			} 
			return SUCCESS;
		}
		
		private void set_uploaded_logfile(String name) {
			File latestUploadFile = new File(logDir, "latest_uploaded");
			BufferedWriter writer;
			try {			
				writer = new BufferedWriter(new FileWriter(latestUploadFile));
				writer.write(name);
				writer.newLine();
				writer.flush();
				writer.close();
			} catch (IOException e) {
				writer = null;
			}
		}

		private boolean uploader(HttpClient httpClient, HttpPost httpPost,Context context){
			Log.d(TAG,"start upload");
			HttpResponse httpResponse;
			ResponseHandler <String> res=new BasicResponseHandler();  
			boolean  result = false;
			try {
				httpResponse = httpClient.execute(httpPost);
				int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
				result = (httpStatusCode == HttpStatus.SC_OK);
				if (result){
					String response = res.handleResponse(httpResponse).toString();
					Log.d("CLICKLOG UPLOADER","clicklog response="+response);
					result &= (response.contains("upload success"));
					Log.d("CLICKLOG UPLOADER","clicklog result="+result);
				}
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

