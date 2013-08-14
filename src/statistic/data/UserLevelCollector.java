package statistic.data;


import java.io.InputStream;
import java.security.KeyStore;


import ubicomp.drunk_detection.activities.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import data.info.RankHistory;
import data.uploader.ServerUrl;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserLevelCollector {

	private static String SERVER_URL;
	private static String SERVER_URL2;
	
	private Context context;
	private ResponseHandler< String> responseHandler;
	
	public UserLevelCollector(Context context){
		this.context = context;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		boolean developer = sp.getBoolean("developer", false);
		SERVER_URL = ServerUrl.SERVER_URL_RANK(developer);
		SERVER_URL2 = ServerUrl.SERVER_URL_RANK_TODAY(developer);
		responseHandler=new BasicResponseHandler();
	}
	
	public RankHistory[] update(){
		try{
			
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
			
			HttpResponse httpResponse;
			httpResponse = httpClient.execute(httpPost);
			String responseString = responseHandler.handleResponse(httpResponse);
			int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			
			if (responseString != null && httpStatusCode == HttpStatus.SC_OK){
				RankHistory[] historys= parse(responseString);
				return historys;
			}
			
		}catch(Exception e){}
		
		return null;
	}
	
	public RankHistory[] updateAverage(){
		try{
			
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
		
			HttpPost httpPost = new HttpPost(SERVER_URL2);
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			
			HttpResponse httpResponse;
			httpResponse = httpClient.execute(httpPost);
			String responseString = responseHandler.handleResponse(httpResponse);
			int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			
			if (responseString != null && httpStatusCode == HttpStatus.SC_OK){
				RankHistory[] historys= parse(responseString);
				return historys;
			}
			
		}catch(Exception e){}
		
		return null;
	}
	
	RankHistory[] parse(String response){
		if (response == null)
			return null;
		response = response.substring(2, response.length()-2);
		String[] tmp = response.split("]," );
		if (tmp.length==0)
			return null;
		RankHistory[] historys;
		historys = new RankHistory[tmp.length];
		for (int i=0;i<tmp.length;++i){
			if (tmp[i].charAt(0)=='[')
				tmp[i]=tmp[i].substring(1,tmp[i].length());
			String[] items = tmp[i].split(",");
			String uid = items[0].substring(1, items[0].length()-1);
			int level;
			if (items[1].equals("null"))
				level = 0;
			else
				level= Integer.valueOf(items[1]);
			historys[i] = new RankHistory(level,uid);
		}
		
		return historys;
	}
	
}
