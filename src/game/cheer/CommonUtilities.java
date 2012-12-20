package game.cheer;



import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {

    public static final String SERVER_URL = "http://140.112.30.165/drunk_detection/GCM/register.php";

    public static final String SENDER_ID = "1075576910063";

    static final String TAG = "GCMDemo";

    static final String DISPLAY_MESSAGE_ACTION =
            "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";

    static final String EXTRA_MESSAGE = "message";

    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
