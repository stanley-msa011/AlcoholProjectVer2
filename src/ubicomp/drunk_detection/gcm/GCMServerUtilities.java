package ubicomp.drunk_detection.gcm;

import com.google.android.gcm.GCMRegistrar;

import data.uploader.ServerUrl;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public final class GCMServerUtilities {

	
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    private static final String TAG = "Server Utilities";
    
    /**
* Register this account/device pair within the server.
*
* @return whether the registration succeeded or not.
*/
    public static boolean register(final Context context, final String regId) {
    	
    	
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String serverUrl = ServerUrl.SERVER_URL_GCM(sp.getBoolean("developer", false));
        String uid = sp.getString("uid", "");
        Map<String, String> params = new HashMap<String, String>();
        
        params.put("regId", regId);
        params.put("uid", uid);
        
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            try {
                post(serverUrl, params);
                GCMRegistrar.setRegisteredOnServer(context, true);
                return true;
            } catch (IOException e) {
                if (i == MAX_ATTEMPTS)
                    break;
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                    return false;
                }
                backoff <<=1;
            }
        }
        return false;
    }

    /**
* Unregister this account/device pair within the server.
*/
    public static void unregister(final Context context, final String regId) {
        Log.i(TAG, "unregistering device (regId = " + regId + ")");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String serverUrl = ServerUrl.SERVER_URL_GCM(sp.getBoolean("developer", false))+"/unregister";
        String uid = sp.getString("uid", "");
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        params.put("uid", uid);
        try {
            post(serverUrl, params);
            GCMRegistrar.setRegisteredOnServer(context, false);
        } catch (IOException e) {
        }
    }

    /**
* Issue a POST request to the server.
*
* @param endpoint POST address.
* @param params request parameters.
*
* @throws IOException propagated from POST.
*/
    private static void post(String endpoint, Map<String, String> params)
            throws IOException {
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
              throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
      }
}