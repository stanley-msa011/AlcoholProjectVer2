package ubicomp.drunk_detection.gcm;

import ubicomp.drunk_detection.activities.GCMIntentService;
import ubicomp.drunk_detection.check.DefaultCheck;
import ubicomp.drunk_detection.check.LockCheck;
import ubicomp.drunk_detection.config.Config;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

public class GCMUtilities {

	public static void register(final Context context){
		
		if (DefaultCheck.check(context) || LockCheck.check(context))
			return;
		
		Log.d("GCM","start register");
		GCMRegistrar.checkDevice(context);
		GCMRegistrar.checkManifest(context);
		context.registerReceiver(GCMReceiver, new IntentFilter("GCM_RECEIVE_ACTION"));
		final String regId = GCMRegistrar.getRegistrationId(context);
		if (regId.equals("")){
			GCMRegistrar.register(context, Config.SENDER_ID);
		} else {
			if (!GCMRegistrar.isRegisteredOnServer(context)) {
				Log.d("GCM", "register");
				AsyncTask<Void, Void, Void> registerTask = new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						if (GCMRegisterUtilities.register(context, regId))
							GCMRegistrar.unregister(context);
						return null;
					}
				};
				registerTask.execute();
			}
		}
	}
	public static void unregister(Context context){
		if (DefaultCheck.check(context) || LockCheck.check(context))
			return;
		
		GCMRegistrar.unregister(context);
		GCMRegistrar.setRegisteredOnServer(context, false);
		context.unregisterReceiver(GCMReceiver);
	}
	
	private final static BroadcastReceiver GCMReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			Intent sIntent = new Intent(context,GCMIntentService.class);
			context.startService(sIntent);
		}
	};
}


