package ubicomp.drunk_detection.activities;

import android.app.ProgressDialog;
import android.content.Context;

public class LoadingBox {
	
	static ProgressDialog dialog = null;
	
	public static void cancel(){
		if (dialog !=null)
			dialog.cancel();
	}
	
	public static void show(Context context){
		if (dialog == null)
			dialog = new ProgressDialog(context);
		else if (dialog.getContext() == null ||  !dialog.getContext().equals(context)){
			dialog.cancel();
			dialog = new ProgressDialog(context);
		}
		dialog.setMessage(context.getResources().getString(R.string.loading));
		dialog.setCancelable(true);
		if (!dialog.isShowing())
			dialog.show();
	}
	
	public static void dismiss(){
		if (dialog == null)
			return;
		dialog.cancel();
		if (dialog.isShowing())
			dialog.dismiss();
	}
}
