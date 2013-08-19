package data.uploader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import ubicomp.drunk_detection.activities.R;

import data.database.AudioDB;
import data.info.AccAudioData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class AudioUploader extends AsyncTask<Void, Void, Void> {

	private static String SERVER_URL;
	
	private static AudioUploader uploader;
	public static void upload(Context context){
		if (uploader != null)
			return;
		else{
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			SERVER_URL = ServerUrl.SERVER_URL_AUDIO(sp.getBoolean("developer", false));
			uploader  = new AudioUploader(context);
			uploader.execute();
		}
	}
	
	private AudioDB db;
	private Context context;
	private File mainDirectory;
	private boolean upload;
	
	private AudioUploader(Context context){
		db = new AudioDB(context);
		this.context = context;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		upload = sp.getBoolean("upload_audio", true);
	}
	
	@Override
	protected void onPreExecute(){
		setStorage();
	}
	
	private void setStorage(){
		String state = Environment.getExternalStorageState();
		File dir = null;
		if (state.equals(Environment.MEDIA_MOUNTED))
			dir = new File(Environment.getExternalStorageDirectory(),"drunk_detection");
		else
			dir = new File(context.getFilesDir(),"drunk_detection");
		
		mainDirectory = new File(dir,"audio_records");
		if (!mainDirectory.exists())
			if (!mainDirectory.mkdirs()){
				Log.d("AUDIO UPLOADER","FAIL TO CREATE DIR");
				return;
			}
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		AccAudioData[] ais = db.getNotUploadedInfo();
		if (ais == null)
			return null;
		for (int i=0;i<ais.length;++i){
			int result = connectingToServer(ais[i]);
			if (result == 0) // pass
				db.uploadedAudio(ais[i]);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result){
		uploader = null;
	}
	
	@Override
	protected void onCancelled(){
		uploader = null;
	}
	
	private int connectingToServer(AccAudioData info){
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
			mpEntity.addPart("userData[]", new StringBody(String.valueOf(info.ts/1000L)));
			
			mpEntity.addPart("userData[]", new StringBody(String.valueOf(info.year)));
			mpEntity.addPart("userData[]", new StringBody(String.valueOf(info.month+1)));
			mpEntity.addPart("userData[]", new StringBody(String.valueOf(info.date)));
			int upload_data = 0;
			if (upload)
				upload_data = 1;
			mpEntity.addPart("userData[]", new StringBody(String.valueOf(upload_data)));
			
			for (int i=0;i<3;++i)
				mpEntity.addPart("accData[]",new StringBody(String.valueOf(info.acc[i])));
			for (int i=0;i<3;++i)
				mpEntity.addPart("usedData[]",new StringBody(String.valueOf(info.used[i])));
			
			File audio = new File(mainDirectory,info.filename+".3gp");
			if (!audio.exists()){
				if ( info.year == 0 && info.month == 0 && info.date == 0);
				else{
					Log.d("AUDIO UPLOADER","cannot find the file "+audio.getAbsolutePath());
					return -2;
				}
			}else{
				if (upload){
					ContentBody aFile = new FileBody(audio, "application/octet-stream");
					mpEntity.addPart("userfile[]", aFile);
				}
			}
			httpPost.setEntity(mpEntity);
			if (!uploader(httpClient, httpPost,context)){
				Log.d("AUDIO UPLOADER","fail to upload");
				return -1;
			}
			
		} catch (Exception e) {
			return -1;
		} 
		
		return 0;
	}
	
	private boolean uploader(HttpClient httpClient, HttpPost httpPost,Context context){
		HttpResponse httpResponse;
		ResponseHandler <String> res=new BasicResponseHandler();  
		boolean  result = false;
		try {
			httpResponse = httpClient.execute(httpPost);
			int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			result = (httpStatusCode == HttpStatus.SC_OK);
			if (result){
				String response = res.handleResponse(httpResponse).toString();
				Log.d("UPLOADER","audio response="+response);
				result &= (response.contains("upload success"));
				Log.d("UPLOADER","audio result="+result);
			}
			
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} finally{
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}
	
}
