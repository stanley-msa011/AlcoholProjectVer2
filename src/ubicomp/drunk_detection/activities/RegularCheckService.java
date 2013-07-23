package ubicomp.drunk_detection.activities;

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
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class RegularCheckService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private static final String SERVER_URL = "https://140.112.30.165/develop/drunk_detection/regular_check.php";
	
	@Override
	public int onStartCommand(Intent intent, int flags,int startId){
		super.onStartCommand(intent, flags, startId);
		Thread t = new Thread(new NetworkRunnable());
		t.start();
		return Service.START_REDELIVER_INTENT;
	}
	
	private int connectingToServer(){
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream instream = this.getResources().openRawResource(R.raw.alcohol_certificate);
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
			
			Log.d("Regular Check","start set entity");
			
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
			String uid = sp.getString("uid", "");
			mpEntity.addPart("user[]", new StringBody(uid));
			httpPost.setEntity(mpEntity);
			int result = uploader(httpClient, httpPost,this);
			if (result == -1){
				Log.d("Regular Check", "fail on connection");
				return -1;
			}
			
		} catch (Exception e) {
			Log.d("Regular Check", "fail by exception "+e.toString());
			return -1;
		} 
		
		return 0;
	}
	
	private class NetworkRunnable implements Runnable{
		@Override
		public void run() {
			connectingToServer();			
		}
		
	}
	
	
	private int uploader(HttpClient httpClient, HttpPost httpPost,Context context){
		Log.d("Regular Check","connecting");
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
