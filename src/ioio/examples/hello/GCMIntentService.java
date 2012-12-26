/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ioio.examples.hello;

import static ioio.examples.hello.CommonUtilities.SENDER_ID;

import java.util.Random;

import ioio.examples.hello.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {


    public GCMIntentService() {
        super(SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        ServerUtilities.register(context, registrationId);
        Log.d("GCMService","isRegistered");
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            ServerUtilities.unregister(context, registrationId);
        } 
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
    	//String message = "Receive Msg";
    	Log.d("GCM receive string","Receive");
    	//String message =  "MESSAGE TEST";
    	String message = intent.getExtras().getString("price");
    	//Log.d("GCM receive string",message);
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

    
    private static final long[] vibrate = {0,50,100,150,200};
    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
    	int icon = R.drawable.icon;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)  context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, "有人為您加油", when);
        notification.defaults = Notification.DEFAULT_ALL; 
        
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, GameActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra("msgmsg", message);
        int coding = 0;
        for (int i=0;i<message.length();++i){
        	coding += message.charAt(i);
        }
        
       
        Log.d("MESSAGE CODE",message);
         PendingIntent intent =  PendingIntent.getActivity(context, coding, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
         notification.setLatestEventInfo(context, title,  "有人為您加油", intent);
         notification.flags |= Notification.FLAG_AUTO_CANCEL;
         notificationManager.notify(coding, notification);
    }

}
