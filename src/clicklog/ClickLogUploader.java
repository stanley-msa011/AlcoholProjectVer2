package clicklog;

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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class ClickLogUploader {
	
	private static ClickLogUploaderThread clickUploader = null;
	
	public static void upload(Context context){
		cancel();
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
		private File rootDir;
		private File logDir;
		public static final int Nothing = 0; 
		public static final int ERROR = -1;
		public static final int SUCCESS = 1;
		private static final String SERVER_URL = "https://140.112.30.165/develop/drunk_detection/clicklog_upload.php";
		
		
		public ClickLogUploaderThread(Context context){
			this.context = context;
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {

			String not_uploaded_files[] = getNotUploadedFiles();
			if (not_uploaded_files == null){
				Log.d("ALCOHOLDEBUG", "no logFile needed to upload");
				return null;
			}
			
			for (int i=0; i<not_uploaded_files.length; ++i){
				File logFile = new File(logDir.getPath(), not_uploaded_files[i]);
				if(logFile.exists()){
					int result = connectingToServer(logFile);
					if(result == ERROR)
						break;
				}
			}
			return null;
		}
		
		private String[] getNotUploadedFiles() {
			rootDir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
			if(!rootDir.exists()){
				return null;
			}
			
			logDir = new File(rootDir, "sequence_log");
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
				    Log.d("ClickLogUploader", "Error when reading latest_uploaded file");
				}
			}
			
			String[] all_logs = logDir.list(new logFilter(latestUpload));
			
			if(all_logs == null){
				return null;
			}
			else{
				for(int i = 0; i < all_logs.length; i++){
					Log.d("Eric", "upload:" + all_logs[i]);

				}
			}
			
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
				Log.d("ALCOHOLDEBUG", "upload logFile connecting to server");
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
				
				if (logFile.exists()){
					ContentBody cbLogFile = new FileBody(logFile, "application/octet-stream");
					mpEntity.addPart("userfile[]", cbLogFile);
				}
				
				httpPost.setEntity(mpEntity);
				int result = uploader(httpClient, httpPost,context);
				if (result == 1){
					Log.d("ALCOHOLDEBUG", "success upload logFile: " + logFile.getName());
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

		private int uploader(HttpClient httpClient, HttpPost httpPost,Context context){
			Log.d("ALCOHOLDEBUG","start upload");
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

