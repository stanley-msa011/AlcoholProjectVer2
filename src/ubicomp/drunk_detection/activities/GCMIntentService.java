
package ubicomp.drunk_detection.activities;

import static ubicomp.drunk_detection.config.Config.SENDER_ID;
import ubicomp.drunk_detection.gcm.GCMServerUtilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

public class GCMIntentService extends GCMBaseIntentService {

	public GCMIntentService() {
		super(SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		GCMServerUtilities.register(context, registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			GCMServerUtilities.unregister(context, registrationId);
		}
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.d("GCM Intent","On message");
		String message = intent.getExtras().getString("gcm_message");
		generateNotification(context, message);
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
	}

	@Override
	public void onError(Context context, String errorId) {
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		return super.onRecoverableError(context, errorId);
	}

	@SuppressWarnings("deprecation")
	private static void generateNotification(Context context, String message) {
		Log.d("GCM Notification","Receive "+message);
		
		Intent sIntent = new Intent(context,GCMAlertActivity.class);
		sIntent.putExtra("gcm_message", message);
		
		PendingIntent pIntent = PendingIntent.getActivity(context, 0,sIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification;
		
		String title = context.getResources().getString(R.string.app_name);
		String msgText = message;
		
		if (Build.VERSION.SDK_INT>=11){
		
			Notification.Builder notificationBuilder = new Notification.Builder(context);
		
			notificationBuilder.setContentTitle(title);
			notificationBuilder.setContentText(msgText);
			notificationBuilder.setSmallIcon(R.drawable.icon);
			notificationBuilder.setContentIntent(pIntent);

			if (Build.VERSION.SDK_INT <16)
				notification = notificationBuilder.getNotification();
			else
				notification = notificationBuilder.build();
		
			notification.defaults = Notification.DEFAULT_ALL;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			
			NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(1,notification);
		}else{
			notification = new Notification();
			notification.defaults = Notification.DEFAULT_ALL;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.setLatestEventInfo(context, title, msgText , pIntent);
			NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(1,notification);
		}

	}

}