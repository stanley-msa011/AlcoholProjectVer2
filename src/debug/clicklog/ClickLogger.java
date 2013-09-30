package debug.clicklog;

import ubicomp.drunk_detection.activities.ClickLoggerService;
import android.content.Context;
import android.content.Intent;

public class ClickLogger {

	public static void Log(Context context,long id){
		Intent intent = new Intent(context,ClickLoggerService.class);
		intent.putExtra(ClickLogId.LOG_MSG_ID, id);
		context.startService(intent);
	}
	
}
