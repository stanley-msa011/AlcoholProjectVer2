package ubicomp.drunk_detection.ui;

import ubicomp.drunk_detection.activities.R;
import android.app.ProgressDialog;
import android.content.Context;

public class LoadingDialogControl {
	
	static ProgressDialog dialog = null;

	public static void show(Context context){
		
		if (dialog == null){
			dialog = new ProgressDialog(context);
		}
		else if (dialog.getContext() == null ||  !dialog.getContext().equals(context)){
			dialog.cancel();
			dialog = new ProgressDialog(context);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		}
		dialog.setCancelable(true);
		if (!dialog.isShowing()){
			dialog.show();
			dialog.setContentView(R.layout.loading_box_theme);
		}
	}
	
	public static void dismiss(){
		if (dialog == null)
			return;
		dialog.cancel();
		if (dialog.isShowing())
			dialog.dismiss();
	}
}
