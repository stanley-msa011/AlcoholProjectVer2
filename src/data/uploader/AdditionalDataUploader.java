package data.uploader;

import java.io.File;
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
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;

import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.check.DefaultCheck;

import data.calculate.WeekNum;
import data.database.AdditionalDB;
import data.database.AudioDB;
import data.database.HistoryDB;
import data.database.QuestionDB;
import data.info.AccAudioData;
import data.info.EmotionData;
import data.info.EmotionManageData;
import data.info.FacebookInfo;
import data.info.GCMInfo;
import data.info.QuestionnaireData;
import data.info.StorytellingFling;
import data.info.StorytellingUsage;
import data.info.UsedDetection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class AdditionalDataUploader extends AsyncTask<Void, Void, Void> {

	private static AdditionalDataUploader uploader = null;
	private static final String TAG = "ADDITIONAL UPLOADER";

	public static void upload(Context context) {
		if (DefaultCheck.check(context))
			return;

		if (!NetworkCheck.networkCheck(context))
			return;
		if (uploader != null)
			return;
		if (SynchronizedLock.sharedLock.tryLock()) {
			SynchronizedLock.sharedLock.lock();
			uploader = new AdditionalDataUploader(context);
			uploader.execute();
		}
	}

	public static void cancel() {
		if (uploader != null) {
			if (!uploader.isCancelled()) {
				uploader.cancel(true);
			}
		}
	}

	private QuestionDB db;
	private HistoryDB hdb;
	private AudioDB audb;
	private AdditionalDB adb;
	private Context context;
	private static String SERVER_URL_EMOTION;
	private static String SERVER_URL_EMOTION_MANAGE;
	private static String SERVER_URL_QUESTIONNAIRE;
	private static String SERVER_URL_USED;
	private static String SERVER_URL_USAGE;
	private static String SERVER_URL_FLING;
	private static String SERVER_URL_GCM_READ;
	private static String SERVER_URL_FACEBOOK;
	private static String SERVER_URL_AUDIO;

	private boolean uploadAudio = false;

	public AdditionalDataUploader(Context context) {
		db = new QuestionDB(context);
		hdb = new HistoryDB(context);
		adb = new AdditionalDB(context);
		audb = new AudioDB(context);
		this.context = context;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		boolean developer = sp.getBoolean("developer", false);
		SERVER_URL_EMOTION = ServerUrl.SERVER_URL_EMOTION(developer);
		SERVER_URL_EMOTION_MANAGE = ServerUrl.SERVER_URL_EMOTION_MANAGE(developer);
		SERVER_URL_QUESTIONNAIRE = ServerUrl.SERVER_URL_QUESTIONNAIRE(developer);
		SERVER_URL_USED = ServerUrl.SERVER_URL_USED(developer);
		SERVER_URL_USAGE = ServerUrl.SERVER_URL_USAGE(developer);
		SERVER_URL_FLING = ServerUrl.SERVER_URL_FLING(developer);
		SERVER_URL_GCM_READ = ServerUrl.SERVER_URL_GCM_READ(developer);
		SERVER_URL_FACEBOOK = ServerUrl.SERVER_URL_FACEBOOK(developer);
		SERVER_URL_AUDIO = ServerUrl.SERVER_URL_AUDIO(developer);
	}

	@Override
	protected Void doInBackground(Void... arg0) {

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		uploadAudio = sp.getBoolean("upload_audio", true);

		EmotionData e_data[] = db.getNotUploadedEmotion();
		if (e_data != null) {
			for (int i = 0; i < e_data.length; ++i) {
				int result = connectingToServer(e_data[i]);
				if (result == -1) {
					Log.d(TAG, "FAIL TO UPLOAD - EMOTION DIY");
					return null;
				}
			}
		}

		EmotionManageData[] em_data = db.getNotUploadedEmotionManage();
		if (em_data != null) {
			for (int i = 0; i < em_data.length; ++i) {
				int result = connectingToServer(em_data[i]);
				if (result == -1) {
					Log.d(TAG, "FAIL TO UPLOAD - EMOTION MANAGEMENT");
					return null;
				}
			}
		}

		QuestionnaireData[] q_data = db.getNotUploadedQuestionnaire();
		if (q_data != null) {
			for (int i = 0; i < q_data.length; ++i) {
				int result = connectingToServer(q_data[i]);
				if (result == -1) {
					Log.d(TAG, "FAIL TO UPLOAD - QUESTIONNAIRE");
					return null;
				}
			}
		}

		long[] ts_data = db.getNotUploadSHCUpdate();
		if (ts_data != null) {
			for (int i = 0; i < ts_data.length; ++i) {
				UsedDetection ud = hdb.getUsedState(ts_data[i]);
				int result = connectingToServer(ts_data[i], ud);
				if (result == -1) {
					Log.d(TAG, "FAIL TO UPLOAD - USED_DATA");
					return null;
				}
			}
		}
		StorytellingUsage[] usage = db.getNotUploadedStorytellingUsage();
		if (usage != null) {
			for (int i = 0; i < usage.length; ++i) {
				int result = connectingToServer(usage[i]);
				if (result == -1) {
					Log.d(TAG, "FAIL TO UPLOAD - STORYTELLING USAGE");
					return null;
				}
			}
		}

		StorytellingFling[] fling = adb.getNotUploadedStorytellingFling();
		if (fling != null) {
			for (int i = 0; i < fling.length; ++i) {
				int result = connectingToServer(fling[i]);
				if (result == -1) {
					Log.d(TAG, "FAIL TO UPLOAD - STORYTELLING FLING");
					return null;
				}
			}
		}

		GCMInfo[] ginfo = adb.getNotUploadedGCM();
		if (ginfo != null) {
			for (int i = 0; i < ginfo.length; ++i) {
				int result = connectingToServer(ginfo[i]);
				if (result == -1) {
					Log.d(TAG, "FAIL TO UPLOAD - GCM");
					return null;
				}
			}
		}

		FacebookInfo[] finfo = adb.getNotUploadedFacebook();
		if (finfo != null) {
			for (int i = 0; i < finfo.length; ++i) {
				int result = connectingToServer(finfo[i]);
				if (result == -1) {
					Log.d(TAG, "FAIL TO UPLOAD - FB");
					return null;
				}
			}
		}

		AccAudioData[] ais = audb.getNotUploadedInfo();
		if (ais == null)
			return null;
		for (int i = 0; i < ais.length; ++i) {
			int result = connectingToServer(ais[i]);
			if (result == -1){
				Log.d(TAG,"FAIL TO UPLOAD - AUDIO");
			}
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		uploader = null;
		SynchronizedLock.sharedLock.unlock();

	}

	@Override
	protected void onCancelled() {
		uploader = null;
		SynchronizedLock.sharedLock.unlock();
	}

	private int connectingToServer(EmotionData e_data) {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream instream = context.getResources()
					.openRawResource(R.raw.alcohol_certificate);
			try {
				trustStore.load(instream, null);
			} finally {
				instream.close();
			}
			SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
			Scheme sch = new Scheme("https", socketFactory, 443);

			httpClient.getConnectionManager().getSchemeRegistry().register(sch);

			HttpPost httpPost = new HttpPost(SERVER_URL_EMOTION);
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
					HttpVersion.HTTP_1_1);

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();

			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			String uid = sp.getString("uid", "");
			nvps.add(new BasicNameValuePair("emotionDIYData[]", uid));
			nvps.add(new BasicNameValuePair("emotionDIYData[]", String.valueOf(e_data.ts)));
			nvps.add(new BasicNameValuePair("emotionDIYData[]", String.valueOf(e_data.selection)));
			int week = WeekNum.getWeek(context, e_data.ts);
			nvps.add(new BasicNameValuePair("emotionDIYData[]", String.valueOf(week)));
			if (e_data.call != null)
				nvps.add(new BasicNameValuePair("emotionDIYData[]", e_data.call));
			for (int i = 0; i < e_data.acc.length; ++i) {
				nvps.add(new BasicNameValuePair("emotionDIYAcc[]", String.valueOf(e_data.acc[i])));
				nvps.add(new BasicNameValuePair("emotionDIYUsed[]", String.valueOf(e_data.used[i])));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			if (uploader(httpClient, httpPost, context)) {
				db.setEmotionUploaded(e_data.ts);
			} else {
				Log.d(TAG, "FAIL TO UPLOAD - EMOTION DIY");
			}

		} catch (Exception e) {
			return -1;
		}
		return 0;
	}

	private int connectingToServer(EmotionManageData em_data) {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream instream = context.getResources()
					.openRawResource(R.raw.alcohol_certificate);
			try {
				trustStore.load(instream, null);
			} finally {
				instream.close();
			}
			SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
			Scheme sch = new Scheme("https", socketFactory, 443);

			httpClient.getConnectionManager().getSchemeRegistry().register(sch);

			HttpPost httpPost = new HttpPost(SERVER_URL_EMOTION_MANAGE);
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
					HttpVersion.HTTP_1_1);

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();

			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			String uid = sp.getString("uid", "");
			nvps.add(new BasicNameValuePair("emotionManageData[]", uid));
			nvps.add(new BasicNameValuePair("emotionManageData[]", String.valueOf(em_data.ts)));
			nvps.add(new BasicNameValuePair("emotionManageData[]", String.valueOf(em_data.emotion)));
			nvps.add(new BasicNameValuePair("emotionManageData[]", String.valueOf(em_data.type)));
			nvps.add(new BasicNameValuePair("emotionManageData[]", em_data.reason));
			int week = WeekNum.getWeek(context, em_data.ts);
			nvps.add(new BasicNameValuePair("emotionManageData[]", String.valueOf(week)));
			for (int i = 0; i < em_data.acc.length; ++i) {
				nvps.add(new BasicNameValuePair("emotionManageAcc[]", String
						.valueOf(em_data.acc[i])));
				nvps.add(new BasicNameValuePair("emotionManageUsed[]", String
						.valueOf(em_data.used[i])));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			if (uploader(httpClient, httpPost, context)) {
				db.setEmotionManageUploaded(em_data.ts);
			} else {
				Log.d(TAG, "FAIL TO UPLOAD - EMOTION MANAGEMENT");
			}

		} catch (Exception e) {
			return -1;
		}
		return 0;
	}

	private int connectingToServer(QuestionnaireData q_data) {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream instream = context.getResources()
					.openRawResource(R.raw.alcohol_certificate);
			try {
				trustStore.load(instream, null);
			} finally {
				instream.close();
			}
			SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
			Scheme sch = new Scheme("https", socketFactory, 443);

			httpClient.getConnectionManager().getSchemeRegistry().register(sch);

			HttpPost httpPost = new HttpPost(SERVER_URL_QUESTIONNAIRE);
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
					HttpVersion.HTTP_1_1);
			MultipartEntity mpEntity = new MultipartEntity();

			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			String uid = sp.getString("uid", "");
			mpEntity.addPart("questionnaireData[]", new StringBody(uid));
			mpEntity.addPart("questionnaireData[]", new StringBody(String.valueOf(q_data.ts)));
			mpEntity.addPart("questionnaireData[]", new StringBody(q_data.seq));
			int week = WeekNum.getWeek(context, q_data.ts);
			mpEntity.addPart("questionnaireData[]", new StringBody(String.valueOf(week)));
			mpEntity.addPart("questionnaireData[]", new StringBody(String.valueOf(q_data.type)));
			for (int i = 0; i < q_data.acc.length; ++i) {
				mpEntity.addPart("questionnaireAcc[]",
						new StringBody(String.valueOf(q_data.acc[i])));
				mpEntity.addPart("questionnaireUsed[]",
						new StringBody(String.valueOf(q_data.used[i])));
			}

			httpPost.setEntity(mpEntity);
			if (uploader(httpClient, httpPost, context)) {
				db.setQuestionnaireUploaded(q_data.ts);
			} else {
				Log.d(TAG, "FAIL TO UPLOAD - QUESTIONNAIRE");
			}

		} catch (Exception e) {
			return -1;
		}
		return 0;
	}

	private int connectingToServer(long ts, UsedDetection ud) {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream instream = context.getResources()
					.openRawResource(R.raw.alcohol_certificate);
			try {
				trustStore.load(instream, null);
			} finally {
				instream.close();
			}
			SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
			Scheme sch = new Scheme("https", socketFactory, 443);

			httpClient.getConnectionManager().getSchemeRegistry().register(sch);

			HttpPost httpPost = new HttpPost(SERVER_URL_USED);
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
					HttpVersion.HTTP_1_1);
			MultipartEntity mpEntity = new MultipartEntity();

			Log.d(TAG, " POST");
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			String uid = sp.getString("uid", "");
			mpEntity.addPart("usedData[]", new StringBody(uid));
			mpEntity.addPart("usedData[]", new StringBody(String.valueOf(ts)));
			for (int i = 0; i < ud.test.length; ++i)
				mpEntity.addPart("usedDataTest[]", new StringBody(String.valueOf(ud.test[i])));
			for (int i = 0; i < ud.pass.length; ++i)
				mpEntity.addPart("usedDataPass[]", new StringBody(String.valueOf(ud.pass[i])));

			httpPost.setEntity(mpEntity);
			if (uploader(httpClient, httpPost, context)) {
				Log.d(TAG, " USED_TS SUCCESS");
				db.updateNotUploadSHCUpdate(ts);
			} else {
				Log.d(TAG, "FAIL TO UPLOAD - USED_TS");
			}

		} catch (Exception e) {
			return -1;
		}
		return 0;
	}

	private int connectingToServer(StorytellingUsage su) {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream instream = context.getResources()
					.openRawResource(R.raw.alcohol_certificate);
			try {
				trustStore.load(instream, null);
			} finally {
				instream.close();
			}
			SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
			Scheme sch = new Scheme("https", socketFactory, 443);

			httpClient.getConnectionManager().getSchemeRegistry().register(sch);

			HttpPost httpPost = new HttpPost(SERVER_URL_USAGE);
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
					HttpVersion.HTTP_1_1);

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();

			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			String uid = sp.getString("uid", "");
			nvps.add(new BasicNameValuePair("usageData[]", uid));
			nvps.add(new BasicNameValuePair("usageData[]", String.valueOf(su.ts)));
			nvps.add(new BasicNameValuePair("usageData[]", String.valueOf(su.daily_usage)));
			nvps.add(new BasicNameValuePair("usageData[]", String.valueOf(su.acc)));
			nvps.add(new BasicNameValuePair("usageData[]", String.valueOf(su.used)));
			int week = WeekNum.getWeek(context, su.ts);
			Log.d(TAG, "UPLOAD - STORYTELLING USAGE " + su.name + " " + su.minutes);
			nvps.add(new BasicNameValuePair("usageData[]", String.valueOf(week)));
			nvps.add(new BasicNameValuePair("usageData[]", su.name));
			nvps.add(new BasicNameValuePair("usageData[]", String.valueOf(su.minutes)));
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			if (uploader(httpClient, httpPost, context)) {
				db.setStorytellingUsageUploaded(su.ts);
			} else
				Log.d(TAG, "FAIL TO UPLOAD - STORYTELLING USAGE");

		} catch (Exception e) {
			return -1;
		}
		return 0;
	}

	private int connectingToServer(StorytellingFling sf) {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream instream = context.getResources()
					.openRawResource(R.raw.alcohol_certificate);
			try {
				trustStore.load(instream, null);
			} finally {
				instream.close();
			}
			SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
			Scheme sch = new Scheme("https", socketFactory, 443);

			httpClient.getConnectionManager().getSchemeRegistry().register(sch);

			HttpPost httpPost = new HttpPost(SERVER_URL_FLING);
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
					HttpVersion.HTTP_1_1);

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();

			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			String uid = sp.getString("uid", "");
			nvps.add(new BasicNameValuePair("flingData[]", uid));
			nvps.add(new BasicNameValuePair("flingData[]", String.valueOf(sf.ts)));
			nvps.add(new BasicNameValuePair("flingData[]", String.valueOf(sf.acc)));
			nvps.add(new BasicNameValuePair("flingData[]", String.valueOf(sf.used)));
			nvps.add(new BasicNameValuePair("flingData[]", String.valueOf(sf.isClear)));
			nvps.add(new BasicNameValuePair("flingData[]", String.valueOf(sf.page)));

			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			if (uploader(httpClient, httpPost, context)) {
				adb.setStorytellingFlingUploaded(sf.ts);
			} else
				Log.d(TAG, "FAIL TO UPLOAD - STORYTELLING FLING");

		} catch (Exception e) {
			return -1;
		}
		return 0;
	}

	private int connectingToServer(GCMInfo ginfo) {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream instream = context.getResources()
					.openRawResource(R.raw.alcohol_certificate);
			try {
				trustStore.load(instream, null);
			} finally {
				instream.close();
			}
			SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
			Scheme sch = new Scheme("https", socketFactory, 443);

			httpClient.getConnectionManager().getSchemeRegistry().register(sch);

			HttpPost httpPost = new HttpPost(SERVER_URL_GCM_READ);
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
					HttpVersion.HTTP_1_1);

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();

			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			String uid = sp.getString("uid", "");
			nvps.add(new BasicNameValuePair("data[]", uid));
			nvps.add(new BasicNameValuePair("data[]", String.valueOf(ginfo.ts)));
			nvps.add(new BasicNameValuePair("data[]", String.valueOf(ginfo.message)));

			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			if (uploader(httpClient, httpPost, context)) {
				adb.setGCMUploaded(ginfo.ts);
			} else
				Log.d(TAG, "FAIL TO UPLOAD - GCM");

		} catch (Exception e) {
			return -1;
		}
		return 0;
	}

	private int connectingToServer(FacebookInfo finfo) {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream instream = context.getResources()
					.openRawResource(R.raw.alcohol_certificate);
			try {
				trustStore.load(instream, null);
			} finally {
				instream.close();
			}
			SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
			Scheme sch = new Scheme("https", socketFactory, 443);

			httpClient.getConnectionManager().getSchemeRegistry().register(sch);

			HttpPost httpPost = new HttpPost(SERVER_URL_FACEBOOK);
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
					HttpVersion.HTTP_1_1);

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();

			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			String uid = sp.getString("uid", "");
			nvps.add(new BasicNameValuePair("data[]", uid));
			nvps.add(new BasicNameValuePair("data[]", String.valueOf(finfo.ts)));
			nvps.add(new BasicNameValuePair("data[]", String.valueOf(finfo.pageWeek)));
			nvps.add(new BasicNameValuePair("data[]", String.valueOf(finfo.pageLevel)));
			nvps.add(new BasicNameValuePair("data[]", String.valueOf(finfo.text)));
			int uploadSuccess = 0;
			if (finfo.uploadSuccess)
				uploadSuccess = 1;
			nvps.add(new BasicNameValuePair("data[]", String.valueOf(uploadSuccess)));

			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			if (uploader(httpClient, httpPost, context)) {
				adb.setFacebookUploaded(finfo.ts);
			} else
				Log.d(TAG, "FAIL TO UPLOAD - FB");

		} catch (Exception e) {
			return -1;
		}
		return 0;
	}

	private int connectingToServer(AccAudioData info) {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream instream = context.getResources()
					.openRawResource(R.raw.alcohol_certificate);
			try {
				trustStore.load(instream, null);
			} finally {
				instream.close();
			}
			SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
			Scheme sch = new Scheme("https", socketFactory, 443);

			httpClient.getConnectionManager().getSchemeRegistry().register(sch);

			HttpPost httpPost = new HttpPost(SERVER_URL_AUDIO);
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
					HttpVersion.HTTP_1_1);
			MultipartEntity mpEntity = new MultipartEntity();

			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			String uid = sp.getString("uid", "");
			mpEntity.addPart("userData[]", new StringBody(uid));
			mpEntity.addPart("userData[]", new StringBody(String.valueOf(info.ts)));

			mpEntity.addPart("userData[]", new StringBody(String.valueOf(info.year)));
			mpEntity.addPart("userData[]", new StringBody(String.valueOf(info.month + 1)));
			mpEntity.addPart("userData[]", new StringBody(String.valueOf(info.date)));
			int upload_data = 0;
			if (uploadAudio)
				upload_data = 1;
			mpEntity.addPart("userData[]", new StringBody(String.valueOf(upload_data)));

			for (int i = 0; i < 3; ++i)
				mpEntity.addPart("accData[]", new StringBody(String.valueOf(info.acc[i])));
			for (int i = 0; i < 3; ++i)
				mpEntity.addPart("usedData[]", new StringBody(String.valueOf(info.used[i])));

			File mainDirectory = setStorage();
			if (mainDirectory != null) {

				File audio = new File(mainDirectory, info.filename + ".3gp");
				if (!audio.exists()) {
					if (info.year == 0 && info.month == 0 && info.date == 0);
					else {
						Log.d(TAG, "cannot find the file " + audio.getAbsolutePath());
						return -2;
					}
				} else {
					if (uploadAudio) {
						ContentBody aFile = new FileBody(audio, "application/octet-stream");
						mpEntity.addPart("userfile[]", aFile);
					}
				}
				httpPost.setEntity(mpEntity);
				if (!uploader(httpClient, httpPost, context)) {
					Log.d(TAG, "fail to upload");
					return -1;
				}
			}

		} catch (Exception e) {
			return -1;
		}
		audb.uploadedAudio(info);
		return 0;
	}

	private File setStorage() {
		File mainDirectory;

		String state = Environment.getExternalStorageState();
		File dir = null;
		if (state.equals(Environment.MEDIA_MOUNTED))
			dir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
		else
			dir = new File(context.getFilesDir(), "drunk_detection");

		mainDirectory = new File(dir, "audio_records");
		if (!mainDirectory.exists())
			if (!mainDirectory.mkdirs()) {
				Log.d(TAG, "FAIL TO CREATE DIR");
				return null;
			}
		return mainDirectory;
	}

	private boolean uploader(HttpClient httpClient, HttpPost httpPost, Context context) {
		HttpResponse httpResponse;
		ResponseHandler<String> res = new BasicResponseHandler();
		boolean result = false;
		try {
			httpResponse = httpClient.execute(httpPost);
			int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			result = (httpStatusCode == HttpStatus.SC_OK);
			if (result) {
				String response = res.handleResponse(httpResponse).toString();
				Log.d(TAG, "ADDITIONAL response=" + response);
				result &= (response.contains("upload success"));
				Log.d(TAG, "ADDITIONAL result=" + result);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} finally {
			if (httpClient != null) {
				ClientConnectionManager ccm = httpClient.getConnectionManager();
				if (ccm != null)
					ccm.shutdown();
			}
		}
		return result;
	}

}
