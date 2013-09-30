package test.gps;

import ubicomp.drunk_detection.activities.R;
import ubicomp.drunk_detection.ui.CustomToastSmall;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class GPSToastHandler extends Handler {
	
	private Context context;
	public GPSToastHandler(Context context){
		this.context = context;
	}
	
	public void handleMessage(Message msg){
		CustomToastSmall.generateToast(context, R.string.open_gps);
	}
}
