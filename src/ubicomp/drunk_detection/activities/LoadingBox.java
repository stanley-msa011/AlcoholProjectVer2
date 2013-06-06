package ubicomp.drunk_detection.activities;

import android.app.ProgressDialog;
import android.content.Context;

public class LoadingBox {
	
	static ProgressDialog dialog = null;
	
	public static ProgressDialog loading(Context context){
		ProgressDialog mDialog = new ProgressDialog(context);
        mDialog.setMessage("載入中");
        mDialog.setCancelable(false);
        return mDialog;
	}
	
	public static void cancel(){
		if (dialog !=null){
			dialog.cancel();
		}
	}
	
	public static void show(Context context){
		dialog = new ProgressDialog(context);
		dialog.setMessage("載入中");
		dialog.setCancelable(true);
		if (!dialog.isShowing()){
			dialog.show();
		}
	}
	
	public static void dismiss(){
		if (dialog == null)
			return;
		if (dialog.isShowing())
			dialog.dismiss();
	}
	
}
