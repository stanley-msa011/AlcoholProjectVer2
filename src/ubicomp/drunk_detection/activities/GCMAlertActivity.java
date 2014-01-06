package ubicomp.drunk_detection.activities;

import ubicomp.drunk_detection.ui.ScaleOnTouchListener;
import ubicomp.drunk_detection.ui.Typefaces;
import data.database.AdditionalDB;
import data.uploader.DataUploader;
import debug.clicklog.ClickLogId;
import debug.clicklog.ClickLogger;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class GCMAlertActivity extends Activity {
	
	private Typeface wordTypefaceBold, wordTypeface;
	private String message;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		message = getIntent().getExtras().getString("gcm_message", "");
		if (message.length() == 0){
			finish();
			return;
		}
		
		wordTypefaceBold = Typefaces.getWordTypefaceBold(getBaseContext());
		wordTypeface = Typefaces.getWordTypeface(getBaseContext());
		
		Builder builder = new AlertDialog.Builder(this);
	    
	    final AlertDialog dialog = builder.create();
	    dialog.show();
	    dialog.setOnDismissListener(new OnDismissListener(){
			@Override
			public void onDismiss(DialogInterface arg0) {
	        	finish();	
			}
	    });
	    dialog.getWindow().setContentView(R.layout.gcm_alert_dialog);
	    
	    TextView msg_title = (TextView) dialog.findViewById(R.id.gcm_alert_title);
	    msg_title.setTypeface(wordTypefaceBold);
	    TextView msg_text =  (TextView) dialog.findViewById(R.id.gcm_text);
	    msg_text.setText(message);
	    msg_text.setTypeface(wordTypeface);
	    TextView msg_ok = (TextView) dialog.findViewById(R.id.gcm_alert_ok);
	    msg_ok.setTypeface(wordTypefaceBold);
	    msg_ok.setOnClickListener(
	    		new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
	    		});
	    msg_ok.setOnTouchListener(new ScaleOnTouchListener());
		
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		if (message!= null){
			ClickLogger.Log(getBaseContext(), ClickLogId.GCM_RECEIVE_CLICK);
			AdditionalDB db = new AdditionalDB(getBaseContext());
			db.insertGCM(message);
		}
		DataUploader.upload(getBaseContext());
		finish();
	}
}
