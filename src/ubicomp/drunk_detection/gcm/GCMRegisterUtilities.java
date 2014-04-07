package ubicomp.drunk_detection.gcm;

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
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import com.google.android.gcm.GCMRegistrar;

import data.uploader.ServerUrl;

import ubicomp.drunk_detection.activities.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class GCMRegisterUtilities {

	public static boolean register(Context context,String regId){
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String serverUrl = ServerUrl.SERVER_URL_GCM(sp.getBoolean("developer", false));
        String uid = sp.getString("uid", "");
        
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
			
			HttpPost httpPost = new HttpPost(serverUrl);
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addTextBody("uid", uid);
			builder.addTextBody("regId", regId);
			httpPost.setEntity(builder.build());
			
//			MultipartEntity mpEntity = new MultipartEntity();
//			mpEntity.addPart("uid", new StringBody(uid));
//			mpEntity.addPart("regId", new StringBody(regId));
//			httpPost.setEntity(mpEntity);
			if(uploader(httpClient, httpPost,context)){
				
			}else{
				GCMRegistrar.setRegisteredOnServer(context, false);
				return false;
			}
			
		} catch (Exception e) {
			GCMRegistrar.setRegisteredOnServer(context, false);
			return false;
		} 
		GCMRegistrar.setRegisteredOnServer(context, true);
		return true;
	}
	
	private static boolean uploader(HttpClient httpClient, HttpPost httpPost,Context context){
		HttpResponse httpResponse;
		ResponseHandler <String> res=new BasicResponseHandler();  
		boolean  result = false;
		try {
			httpResponse = httpClient.execute(httpPost);
			int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			result = (httpStatusCode == HttpStatus.SC_OK);
			if (result){
				String response = res.handleResponse(httpResponse).toString();
				Log.d("GCM","register response = " + response);
				result = response.contains("success");
				Log.d("GCM","register result = " + result);
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
