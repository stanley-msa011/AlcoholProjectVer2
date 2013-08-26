package data.uploader;



import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import network.NetworkCheck;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;

import ubicomp.drunk_detection.activities.R;

import data.calculate.WeekNum;
import data.database.HistoryDB;
import data.database.QuestionDB;
import data.info.EmotionData;
import data.info.EmotionManageData;
import data.info.QuestionnaireData;
import data.info.StorytellingUsage;
import data.info.UsedDetection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class QuestionnaireDataUploader extends AsyncTask<Void, Void, Void> {

	private static QuestionnaireDataUploader  questionnaire_uploader = null;
	
	public static void uploader(Context context){
		if(!NetworkCheck.networkCheck(context))
			return;
		if (questionnaire_uploader!=null)
			return;
		if(SynchronizedLock.sharedLock.tryLock()){
			SynchronizedLock.sharedLock.lock();
			questionnaire_uploader = new QuestionnaireDataUploader (context);
			questionnaire_uploader.execute();
		}
	}
	
	public static void cancel(){
		if (questionnaire_uploader!=null){
			if (!questionnaire_uploader.isCancelled()){
				questionnaire_uploader.cancel(true);
			}
		}
	}
	
		private QuestionDB db;
		private HistoryDB hdb;
		private Context context;
		private static String SERVER_URL_EMOTION ;
		private static String SERVER_URL_EMOTION_MANAGE;
		private static String SERVER_URL_QUESTIONNAIRE;
		private static String SERVER_URL_USED ;
		private static String SERVER_URL_USAGE = "";
		
		public QuestionnaireDataUploader (Context context){
			db = new QuestionDB(context);
			hdb = new HistoryDB(context);
			this.context = context;
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			boolean developer = sp.getBoolean("developer", false);
			SERVER_URL_EMOTION = ServerUrl.SERVER_URL_EMOTION(developer);
			SERVER_URL_EMOTION_MANAGE = ServerUrl.SERVER_URL_EMOTION_MANAGE(developer);
			SERVER_URL_QUESTIONNAIRE = ServerUrl.SERVER_URL_QUESTIONNAIRE(developer);
			SERVER_URL_USED = ServerUrl.SERVER_URL_USED(developer);
			SERVER_URL_USAGE = ServerUrl.SERVER_URL_USAGE(developer);
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			
			Log.d("QUESTIONNAIRE UPLOADER","START");
			
			EmotionData e_data[] = db.getNotUploadedEmotion();
			if (e_data != null){
				for (int i=0;i<e_data.length;++i){
			       	int result = connectingToServer(e_data[i]);
			        if (result == -1){
			        	Log.d("QUESTIONNAIRE UPLOADER","FAIL TO UPLOAD - EMOTION DIY");
			        	return null;
			        }
				}
			}

			EmotionManageData[] em_data = db.getNotUploadedEmotionManage();
			if (em_data != null){
				for (int i=0;i<em_data.length;++i){
			       	int result = connectingToServer(em_data[i]);
			        if (result == -1){
			        	Log.d("QUESTIONNAIRE UPLOADER","FAIL TO UPLOAD - EMOTION MANAGEMENT");
			        	return null;
			        }
				}
			}
			
			QuestionnaireData[] q_data = db.getNotUploadedQuestionnaire();
			if (q_data != null){
				for (int i=0;i<q_data.length;++i){
			       	int result = connectingToServer(q_data[i]);
			        if (result == -1){
			        	Log.d("QUESTIONNAIRE UPLOADER","FAIL TO UPLOAD - QUESTIONNAIRE");
			        	return null;
			        }
				}
			}
			
			
			long[] ts_data = db.getNotUploadSHCUpdate();
			if (ts_data !=null){
				for (int i=0;i<ts_data.length;++i){
					UsedDetection ud = hdb.getUsedState(ts_data[i]);
			       	int result = connectingToServer(ts_data[i],ud);
			        if (result == -1){
			        	Log.d("QUESTIONNAIRE UPLOADER","FAIL TO UPLOAD - USED_DATA");
			        	return null;
			        }
				}
			}
			StorytellingUsage[] usage = db.getNotUploadedStorytellingUsage();
			if (usage !=null){
				Log.d("QUESTIONNAIRE UPLOADER"," STORYTELLING USAGE START NOT NULL");
				for (int i=0;i<usage.length;++i){
			       	int result = connectingToServer(usage[i]);
			        if (result == -1){
			        	Log.d("QUESTIONNAIRE UPLOADER","FAIL TO UPLOAD - STORYTELLING USAGE");
			        	return null;
			        }
				}
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result ){
			questionnaire_uploader = null;
			SynchronizedLock.sharedLock.unlock();
			Log.d("QUESTIONNAIRE UPLOADER","END");
			AudioUploader.upload(context);
			
		}
		
		@Override
		protected void onCancelled(){
			questionnaire_uploader = null;
			SynchronizedLock.sharedLock.unlock();
			Log.d("QUESTIONNAIRE UPLOADER","CANCEL");
		}
		
		private int connectingToServer(EmotionData e_data){
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
				
				HttpPost httpPost = new HttpPost(SERVER_URL_EMOTION);
				httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
				
				List <NameValuePair> nvps = new ArrayList <NameValuePair>();
				
				SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
				String uid = sp.getString("uid", "");
				nvps.add(new BasicNameValuePair("emotionDIYData[]",uid));
				nvps.add(new BasicNameValuePair("emotionDIYData[]",String.valueOf(e_data.ts)));
				nvps.add(new BasicNameValuePair("emotionDIYData[]",String.valueOf(e_data.selection)));
				int week = WeekNum.getWeek(context, e_data.ts);
				nvps.add(new BasicNameValuePair("emotionDIYData[]",String.valueOf(week)));
				if (e_data.call != null)
					nvps.add(new BasicNameValuePair("emotionDIYData[]",e_data.call));
				for (int i=0;i<e_data.acc.length;++i){
					nvps.add(new BasicNameValuePair("emotionDIYAcc[]",String.valueOf(e_data.acc[i])));
					nvps.add(new BasicNameValuePair("emotionDIYUsed[]",String.valueOf(e_data.used[i])));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
				
				if (uploader(httpClient, httpPost,context)){
					db.setEmotionUploaded(e_data.ts);
				}else{
					Log.d("QUESTIONNAIRE UPLOADER","FAIL TO UPLOAD - EMOTION DIY");
				}
				
			} catch (Exception e) {
				return -1;
			} 
			return 0;
		}
		
		private int connectingToServer(EmotionManageData em_data){
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
				
				HttpPost httpPost = new HttpPost(SERVER_URL_EMOTION_MANAGE);
				httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
				
				List <NameValuePair> nvps = new ArrayList <NameValuePair>();
				
				SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
				String uid = sp.getString("uid", "");
				nvps.add(new BasicNameValuePair("emotionManageData[]",uid));
				nvps.add(new BasicNameValuePair("emotionManageData[]",String.valueOf(em_data.ts)));
				nvps.add(new BasicNameValuePair("emotionManageData[]",String.valueOf(em_data.emotion)));
				nvps.add(new BasicNameValuePair("emotionManageData[]",String.valueOf(em_data.type)));
				nvps.add(new BasicNameValuePair("emotionManageData[]",em_data.reason));
				int week = WeekNum.getWeek(context, em_data.ts);
				nvps.add(new BasicNameValuePair("emotionManageData[]",String.valueOf(week)));
				for (int i=0;i<em_data.acc.length;++i){
					nvps.add(new BasicNameValuePair("emotionManageAcc[]",String.valueOf(em_data.acc[i])));
					nvps.add(new BasicNameValuePair("emotionManageUsed[]",String.valueOf(em_data.used[i])));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
				if (uploader(httpClient, httpPost,context)){
					db.setEmotionManageUploaded(em_data.ts);
				}else{
					Log.d("QUESTIONNAIRE UPLOADER","FAIL TO UPLOAD - EMOTION MANAGEMENT");
				}
				
			} catch (Exception e) {
				return -1;
			} 
			return 0;
		}
		
		private int connectingToServer(QuestionnaireData q_data){
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
				
				HttpPost httpPost = new HttpPost(SERVER_URL_QUESTIONNAIRE);
				httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
				MultipartEntity mpEntity = new MultipartEntity();
				
				
				SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
				String uid = sp.getString("uid", "");
				mpEntity.addPart("questionnaireData[]", new StringBody(uid));
				mpEntity.addPart("questionnaireData[]", new StringBody(String.valueOf(q_data.ts)));
				mpEntity.addPart("questionnaireData[]", new StringBody(q_data.seq));
				int week = WeekNum.getWeek(context, q_data.ts);
				mpEntity.addPart("questionnaireData[]", new StringBody(String.valueOf(week)));
				mpEntity.addPart("questionnaireData[]", new StringBody(String.valueOf(q_data.type)));
				for (int i=0;i<q_data.acc.length;++i){
					mpEntity.addPart("questionnaireAcc[]", new StringBody(String.valueOf(q_data.acc[i])));
					mpEntity.addPart("questionnaireUsed[]", new StringBody(String.valueOf(q_data.used[i])));
				}
				
				httpPost.setEntity(mpEntity);
				if (uploader(httpClient, httpPost,context)){
					db.setQuestionnaireUploaded(q_data.ts);
				}else{
					Log.d("QUESTIONNAIRE UPLOADER","FAIL TO UPLOAD - QUESTIONNAIRE");
				}
				
			} catch (Exception e) {
				return -1;
			} 
			return 0;
		}
		
		
		private int connectingToServer(long ts,UsedDetection ud){
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
				
				HttpPost httpPost = new HttpPost(SERVER_URL_USED);
				httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
				MultipartEntity mpEntity = new MultipartEntity();
				
				Log.d("QUESTIONNAIRE UPLOADER"," POST");
				SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
				String uid = sp.getString("uid", "");
				mpEntity.addPart("usedData[]", new StringBody(uid));
				mpEntity.addPart("usedData[]", new StringBody(String.valueOf(ts)));
				for (int i=0;i<ud.test.length;++i)
					mpEntity.addPart("usedDataTest[]",new StringBody(String.valueOf(ud.test[i])));
				for (int i=0;i<ud.pass.length;++i)
					mpEntity.addPart("usedDataPass[]",new StringBody(String.valueOf(ud.pass[i])));
				
				httpPost.setEntity(mpEntity);
				if (uploader(httpClient, httpPost,context)){
					Log.d("QUESTIONNAIRE UPLOADER"," USED_TS SUCCESS");
					db.updateNotUploadSHCUpdate(ts);
				}else{
					Log.d("QUESTIONNAIRE UPLOADER","FAIL TO UPLOAD - USED_TS");
				}
				
			} catch (Exception e) {
				return -1;
			} 
			return 0;
		}
		
		private int connectingToServer(StorytellingUsage su){
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
				
				HttpPost httpPost = new HttpPost(SERVER_URL_USAGE);
				httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
				
				List <NameValuePair> nvps = new ArrayList <NameValuePair>();
				
				SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
				String uid = sp.getString("uid", "");
				nvps.add(new BasicNameValuePair("usageData[]",uid));
				nvps.add(new BasicNameValuePair("usageData[]",String.valueOf(su.ts)));
				nvps.add(new BasicNameValuePair("usageData[]",String.valueOf(su.daily_usage)));
				nvps.add(new BasicNameValuePair("usageData[]",String.valueOf(su.acc)));
				nvps.add(new BasicNameValuePair("usageData[]",String.valueOf(su.used)));
				int week = WeekNum.getWeek(context, su.ts);
				Log.d("QUESTIONNAIRE UPLOADER","UPLOAD - STORYTELLING USAGE "+su.name+" "+su.minutes);
				nvps.add(new BasicNameValuePair("usageData[]",String.valueOf(week)));
				nvps.add(new BasicNameValuePair("usageData[]",su.name));
				nvps.add(new BasicNameValuePair("usageData[]",String.valueOf(su.minutes)));
				httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
				
				if (uploader(httpClient, httpPost,context)){
					db.setStorytellingUsageUploaded(su.ts);
				}
				else
					Log.d("QUESTIONNAIRE UPLOADER","FAIL TO UPLOAD - STORYTELLING USAGE");
				
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
				result =  (httpStatusCode == HttpStatus.SC_OK);
				if (result){
					String response = res.handleResponse(httpResponse).toString();
					Log.d("QUESTIONNAIRE UPLOADER","ques response="+response);
					result &= (response.contains("upload success"));
					Log.d("QUESTIONNAIRE UPLOADER","ques result="+result);
				}
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			} finally{
				httpClient.getConnectionManager().shutdown();
			}
			return result;
		}
}
