package ubicomp.drunk_detection.activities;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import ubicomp.drunk_detection.check.DefaultCheck;

import data.uploader.ServerUrl;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;

public class RegularCheckService extends Service {

	private static final String TAG = "REGULAR_CHECK_SERVICE";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private static final long time_gap = AlarmManager.INTERVAL_HOUR;

	private static String SERVER_URL;

	private static Thread runThread = null;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		if (DefaultCheck.check(getBaseContext()))
			return Service.START_REDELIVER_INTENT;

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		SERVER_URL = ServerUrl.SERVER_URL_REGULAR_CHECK(sp.getBoolean("developer", false));
		long latest_time = sp.getLong("latest_regular_check", 0L);
		if (time_gap > System.currentTimeMillis() - latest_time)
			return Service.START_REDELIVER_INTENT;
		if (runThread != null && runThread.isAlive())
			return Service.START_REDELIVER_INTENT;
		runThread = new Thread(new NetworkRunnable());
		runThread.start();
		return Service.START_REDELIVER_INTENT;
	}

	private int connectingToServer() {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream instream = this.getResources().openRawResource(R.raw.alcohol_certificate);
			try {
				trustStore.load(instream, null);
			} finally {
				instream.close();
			}
			SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
			Scheme sch = new Scheme("https", socketFactory, 443);

			httpClient.getConnectionManager().getSchemeRegistry().register(sch);

			HttpPost httpPost = new HttpPost(SERVER_URL);
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
			String uid = sp.getString("uid", "");
			if (uid.length() == 0)
				return -1;

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addTextBody("user[]", uid);
			String app_version = "unknown";
			PackageInfo pinfo;
			try {
				pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				app_version = pinfo.versionName;
			} catch (NameNotFoundException e) {
			}
			builder.addTextBody("user[]", app_version);
			String devId = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
			builder.addTextBody("user[]", devId);

			Calendar c = Calendar.getInstance();
			int mYear = sp.getInt("sYear", c.get(Calendar.YEAR));
			int mMonth = sp.getInt("sMonth", c.get(Calendar.MONTH));
			int mDay = sp.getInt("sDate", c.get(Calendar.DATE));

			String joinDate = mYear + "-" + (mMonth + 1) + "-" + mDay;
			builder.addTextBody("user[]", joinDate);
			builder.addTextBody("sensor", sp.getString("currentSensor", "unknown"));

			httpPost.setEntity(builder.build());
			// MultipartEntity mpEntity = new MultipartEntity();
			//
			// if (uid.length() == 0)
			// return -1;
			// mpEntity.addPart("user[]", new StringBody(uid));
			//
			// String app_version = "unknown";
			// PackageInfo pinfo;
			// try {
			// pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			// app_version = pinfo.versionName;
			// } catch (NameNotFoundException e) {
			// }
			//
			// mpEntity.addPart("user[]", new StringBody(app_version));
			//
			// String devId = Secure.getString(this.getContentResolver(),
			// Secure.ANDROID_ID);
			// mpEntity.addPart("user[]", new StringBody(devId));
			//
			// Calendar c = Calendar.getInstance();
			//
			// int mYear = sp.getInt("sYear", c.get(Calendar.YEAR));
			// int mMonth = sp.getInt("sMonth", c.get(Calendar.MONTH));
			// int mDay = sp.getInt("sDate", c.get(Calendar.DATE));
			//
			// String joinDate = mYear+"-"+(mMonth+1)+"-"+mDay;
			// mpEntity.addPart("user[]", new StringBody(joinDate));
			//
			// mpEntity.addPart("sensor", new
			// StringBody(sp.getString("currentSensor", "unknown")));
			//
			// httpPost.setEntity(mpEntity);
			if (uploader(httpClient, httpPost, this)) {
				Log.d(TAG, "SUCCESS");
				SharedPreferences.Editor edit = sp.edit();
				edit.putLong("latest_regular_check", System.currentTimeMillis());
				edit.commit();
			} else {
				Log.d(TAG, "FAIL TO CONNECT");
				return -1;
			}

		} catch (Exception e) {
			Log.d(TAG, "EXCEPTION:" + e.toString());
			return -1;
		}

		return 0;
	}

	private class NetworkRunnable implements Runnable {
		@Override
		public void run() {
			connectingToServer();
		}

	}

	private boolean uploader(HttpClient httpClient, HttpPost httpPost, Context context) {
		HttpResponse httpResponse;
		ResponseHandler<String> res = new BasicResponseHandler();
		boolean result = false;
		try {
			httpResponse = httpClient.execute(httpPost);
			int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			result = (httpStatusCode == HttpStatus.SC_OK);
			{
				if (result) {
					String response = res.handleResponse(httpResponse).toString();
					result &= (response.contains("regular check pass"));
				}
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}

}
