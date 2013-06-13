package questionnaire.data;

import history.data.AudioUploader;

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

import ubicomp.drunk_detection.activities.R;

import database.QuestionDB;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;

public class EmotionDataUploader extends AsyncTask<Void, Void, Void> {

	private static EmotionDataUploader  reuploader = null;
	
	public static void reuploader(Context context){
		cancel();
		reuploader = new EmotionDataUploader (context);
		reuploader.execute();
	}
	
	public static void cancel(){
		if (reuploader!=null){
			if (!reuploader.isCancelled()){
				reuploader.cancel(true);
			}
		}
	}
	
	
		private QuestionDB db;
		private Context context;
		private static final String SERVER_URL_EMOTION = "https://140.112.30.165/develop/drunk_detection/emotionDIY_upload.php";
		private static final String SERVER_URL_EMOTION_MANAGE = "https://140.112.30.165/develop/drunk_detection/emotion_manage_upload.php";
		private static final String SERVER_URL_QUESTIONNAIRE = "https://140.112.30.165/develop/drunk_detection/questionnaire_upload.php";
		
		public EmotionDataUploader (Context context){
			db = new QuestionDB(context);
			this.context = context;
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			Log.d("EMOTION_UPLOADER","start");
			
			EmotionData e_data[] = db.getNotUploadedEmotion();
			if (e_data != null){
				for (int i=0;i<e_data.length;++i){
			       	int result = connectingToServer(e_data[i]);
			        if (result == -1){
			        	Log.d("EMOTION_UPLOADER","fail");
			        	return null;
			        }
				}
			}

			EmotionManageData[] em_data = db.getNotUploadedEmotionManage();
			if (em_data != null){
				for (int i=0;i<em_data.length;++i){
			       	int result = connectingToServer(em_data[i]);
			        if (result == -1){
			        	Log.d("EMOTION_UPLOADER","fail2");
			        	return null;
			        }
				}
			}
			
			QuestionnaireData[] q_data = db.getNotUploadedQuestionnaire();
			if (q_data != null){
				for (int i=0;i<q_data.length;++i){
			       	int result = connectingToServer(q_data[i]);
			        if (result == -1){
			        	Log.d("EMOTION_UPLOADER","fail3");
			        	return null;
			        }
				}
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result ){
			AudioUploader.upload(context);
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
				MultipartEntity mpEntity = new MultipartEntity();
				
				
				SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
				String uid = sp.getString("uid", "");
				mpEntity.addPart("emotionDIYData[]", new StringBody(uid));
				mpEntity.addPart("emotionDIYData[]", new StringBody(String.valueOf(e_data.ts)));
				mpEntity.addPart("emotionDIYData[]", new StringBody(String.valueOf(e_data.selection)));
				if (e_data.call !=null)
					mpEntity.addPart("emotionDIYData[]", new StringBody(String.valueOf(e_data.call)));
				
				httpPost.setEntity(mpEntity);
				int result = uploader(httpClient, httpPost,context);
				if (result == 1){
					db.setEmotionUploaded(e_data.ts);
				}else{
					Log.d("EMOTION_UPLOADER","fail1");
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
				MultipartEntity mpEntity = new MultipartEntity();
				
				
				SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
				String uid = sp.getString("uid", "");
				mpEntity.addPart("emotionManageData[]", new StringBody(uid));
				mpEntity.addPart("emotionManageData[]", new StringBody(String.valueOf(em_data.ts)));
				mpEntity.addPart("emotionManageData[]", new StringBody(String.valueOf(em_data.emotion)));
				mpEntity.addPart("emotionManageData[]", new StringBody(String.valueOf(em_data.type)));
				mpEntity.addPart("emotionManageData[]", new StringBody(em_data.reason));
				
				httpPost.setEntity(mpEntity);
				int result = uploader(httpClient, httpPost,context);
				if (result == 1){
					db.setEmotionManageUploaded(em_data.ts);
				}else{
					Log.d("EMOTION_UPLOADER","fail2");
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
				
				httpPost.setEntity(mpEntity);
				int result = uploader(httpClient, httpPost,context);
				if (result == 1){
					db.setQuestionnaireUploaded(q_data.ts);
				}else{
					Log.d("EMOTION_UPLOADER","fail3");
				}
				
			} catch (Exception e) {
				return -1;
			} 
			return 0;
		}
		
		private int uploader(HttpClient httpClient, HttpPost httpPost,Context context){
			Log.d("EMOTION_UPLOADER","start reupload");
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
