package history.data;

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

import ubicomp.drunk_detection.activities.R;

import database.AudioDB;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class AudioUploader extends AsyncTask<Void, Void, Void> {

	private static AudioUploader uploader;
	public static void upload(Context context){
		if (uploader != null)
			return;
		else{
			uploader  = new AudioUploader(context);
			Log.d("Audio Uploader", "call execute");
			uploader.execute();
		}
	}
	
	private AudioDB db;
	private Context context;
	private File mainDirectory;
	
	private AudioUploader(Context context){
		db = new AudioDB(context);
		this.context = context;
		Log.d("Audio Uploader", "new");
	}
	
	@Override
	protected void onPreExecute(){
		setStorage();
	}
	
	private void setStorage(){
		Log.d("Audio Uploader", "set storage");
		String state = Environment.getExternalStorageState();
		File dir = null;
		if (state.equals(Environment.MEDIA_MOUNTED))
			dir = new File(Environment.getExternalStorageDirectory(),"drunk_detection");
		else
			dir = new File(context.getFilesDir(),"drunk_detection");
		if (!dir.exists())
			if (!dir.mkdirs())
				Log.d("TEST_STORAGE","FAIL TO CREATE DIR");
		
		mainDirectory = new File(dir,"audio_records");
		if (!mainDirectory.exists())
			if (!mainDirectory.mkdirs()){
				return;
			}
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		AudioInfo[] ais = db.getNotUploadedInfo();
		Log.d("Audio Uploader", "in background");
		if (ais == null)
			return null;
		for (int i=0;i<ais.length;++i){
			int result = connectingToServer(ais[i]);
			if (result == 0){
				db.uploadedAudio(ais[i]);
				Log.d("Audio Uploader", "upload: "+ais[i].toString());
			}else if (result == -2);//no file
			else{
				Log.d("Audio Uploader", "fail to upload: "+ais[i].toString());
				break;
			}
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
	
	private static final String SERVER_URL = "https://140.112.30.165/develop/drunk_detection/audio_upload.php";
	
	private int connectingToServer(AudioInfo info){
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
			mpEntity.addPart("userData[]", new StringBody(String.valueOf(info.month)));
			mpEntity.addPart("userData[]", new StringBody(String.valueOf(info.date)));
			
			File audio = new File(mainDirectory,info.filename+".3gp");
			if (!audio.exists()){
				Log.d("Audio Uploader", "cannot find the file "+audio.getAbsolutePath());
				return -2;
			}
			ContentBody aFile = new FileBody(audio, "application/octet-stream");
			mpEntity.addPart("userfile[]", aFile);
			
			httpPost.setEntity(mpEntity);
			int result = uploader(httpClient, httpPost,context);
			if (result == -1){
				Log.d("Audio Uploader", "fail when uploading");
				return -1;
			}
			
		} catch (Exception e) {
			Log.d("Audio Uploader", "fail by exception "+e.getMessage());
			return -1;
		} 
		
		return 0;
	}
	
	private int uploader(HttpClient httpClient, HttpPost httpPost,Context context){
		Log.d("AudioDataUploader","start upload");
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
