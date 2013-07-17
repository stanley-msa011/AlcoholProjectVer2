package interaction;


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

import data.rank.RankHistory;

import android.content.Context;
import android.util.Log;

public class UserLevelCollector {

	private static final String SERVER_URL = "https://140.112.30.165/develop/drunk_detection/userStates2.php";
	
	private Context context;
	private ResponseHandler< String> responseHandler;
	
	public UserLevelCollector(Context context){
		this.context = context;
		responseHandler=new BasicResponseHandler();
	}
	
	public RankHistory[] update(){
		try{
			
			Log.d("level collector","start");
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
			
			Log.d("level collector","end init");
			
			HttpResponse httpResponse;
			httpResponse = httpClient.execute(httpPost);
			String responseString = responseHandler.handleResponse(httpResponse);
			int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			if (httpStatusCode == HttpStatus.SC_OK){
				Log.d("user states", responseString);
			}
			else{
				Log.d("user states", "xxx");
			}
			if (responseString != null){
				RankHistory[] historys= parse(responseString);
				for (int i=0;i<historys.length;++i){
					Log.d("user states","history "+historys[i].uid +" "+historys[i].score);
				}
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
		Log.d("user states","splited");
		if (tmp.length==0){
			Log.d("user states","null split");
			return null;
		}
		Log.d("user states","len "+tmp.length);
		RankHistory[] historys;
		historys = new RankHistory[tmp.length];
		Log.d("user states","before for loop");
		for (int i=0;i<tmp.length;++i){
			Log.d("user states","loop");
			if (tmp[i].charAt(0)=='[')
				tmp[i]=tmp[i].substring(1,tmp[i].length());
			Log.d("user states","loop-1:  "+tmp[i]);
			String[] items = tmp[i].split(",");
			String uid = items[0].substring(1, items[0].length()-1);
			Log.d("user states","loop-2 items length: "+ items.length);
			Log.d("user states","items1: "+items[1]);
			int level;
			if (items[1].equals("null"))
				level = 0;
			else
				level= Integer.valueOf(items[1]);
			Log.d("user states","loop uid "+uid+": "+level);
			historys[i] = new RankHistory(level,uid);
		}
		
		return historys;
	}
	
}
