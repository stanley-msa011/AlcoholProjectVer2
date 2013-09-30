package ubicomp.drunk_detection.check;

import ubicomp.drunk_detection.activities.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;

public class AlwaysFinishActivitiesCheck {

	@SuppressWarnings("deprecation")
	@SuppressLint("InlinedApi")
	public static boolean check(Context context){
		int check = 0;
		if (Build.VERSION.SDK_INT >= 17) 
			check = Settings.System.getInt(context.getApplicationContext().getContentResolver(), Settings.Global.ALWAYS_FINISH_ACTIVITIES, 0);
		else 
			check = Settings.System.getInt(context.getApplicationContext().getContentResolver(), Settings.System.ALWAYS_FINISH_ACTIVITIES, 0);
	
		return (check == 1);
		
	}
	
	private static Dialog dialog = null;
	
	public static void openDialog(Context context){
		if (dialog != null && !dialog.isShowing()){
			dialog.show();
			return;
		}
		AlertDialog.Builder builder= new AlertDialog.Builder(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (inflater == null)
			return;
		View v = inflater.inflate(R.layout.alert_dialog, null);
		if (v == null)
			return;
		builder.setView(v);
		builder.setCancelable(true);
		dialog = builder.create();
		dialog.show();
	}
	
	public static void dismiss(){
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
	}
}
