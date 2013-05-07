package main.activities;

import android.app.ProgressDialog;
import android.content.Context;

public class LoadingBox {
	
	public static ProgressDialog loading(Context context){
		ProgressDialog mDialog = new ProgressDialog(context);
        mDialog.setMessage("載入中");
        mDialog.setCancelable(false);
        return mDialog;
	}
	
}
