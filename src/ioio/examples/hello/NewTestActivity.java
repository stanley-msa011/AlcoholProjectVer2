package ioio.examples.hello;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NewTestActivity extends Activity {

	ButtonOnClickListener bocl;
	TimeOnClickListener tocl;
	private Button GoodButton,BadButton;
	
	private Button Good_add6,Good_add24;
	private Button Good_sub6,Good_sub24;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_test);
		GoodButton = (Button) this.findViewById(R.id.test_good);
		BadButton = (Button) this.findViewById(R.id.test_bad);
		bocl = new ButtonOnClickListener();
		GoodButton.setOnClickListener(bocl);
		BadButton.setOnClickListener(bocl);
		
		Good_add6 = (Button) this.findViewById(R.id.test_add_6_hour);
		Good_add24 = (Button) this.findViewById(R.id.test_add_24_hour);
		Good_sub6 = (Button) this.findViewById(R.id.test_sub_6_hour);
		Good_sub24 = (Button) this.findViewById(R.id.test_sub_24_hour);
		tocl = new TimeOnClickListener();
		Good_add6.setOnClickListener(tocl);
		Good_add24.setOnClickListener(tocl);
		Good_sub6.setOnClickListener(tocl);
		Good_sub24.setOnClickListener(tocl);
	}

	private class ButtonOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Calendar cal = Calendar.getInstance();
			long ts = cal.getTimeInMillis()/1000; // in second
			File mainStorageDir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
		    if (!mainStorageDir.exists())
		        if (!mainStorageDir.mkdirs())
		            return;
		    File sessionDir = new File(mainStorageDir, String.valueOf(ts));
		    if (!sessionDir.exists())
		        if (!sessionDir.mkdirs())
		            return;
		    File textfile = new File(sessionDir + File.separator + String.valueOf(ts) + ".txt");
		    
			if (v.getId() == R.id.test_good){
		       	try {
		       		BufferedWriter writer = new BufferedWriter(new FileWriter(textfile));
		       		writer.write(String.valueOf(ts)+"	0.08\n");
		       		writer.flush();
		       		writer.close();
				} catch (Exception e) {	
				}
			}else if(v.getId() == R.id.test_bad){
		       	try {
		       		BufferedWriter writer = new BufferedWriter(new FileWriter(textfile));
		       		writer.write(String.valueOf(ts)+"	0.8\n");
		       		writer.flush();
		       		writer.close();
				} catch (Exception e) {	
				}
			}
			Intent i_return =new Intent();
			Bundle bdata = new Bundle();
			bdata.putString("testfilename", String.valueOf(ts));
			i_return.putExtras(bdata);
			setResult(RESULT_OK,i_return);
			finish();
		}
	}
	
	
	private class TimeOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Calendar cal = Calendar.getInstance();
			long ts = cal.getTimeInMillis()/1000; // in second
			
			if (v.getId() == R.id.test_add_6_hour)
				ts += 21600;
			else if (v.getId() == R.id.test_add_24_hour)
				ts += 86400;
			else if (v.getId() == R.id.test_sub_6_hour)
				ts -= 21600;
			else if (v.getId() == R.id.test_sub_24_hour)
				ts -= 86400;
			
			File mainStorageDir = new File(Environment.getExternalStorageDirectory(), "drunk_detection");
		    if (!mainStorageDir.exists())
		        if (!mainStorageDir.mkdirs())
		            return;
		    File sessionDir = new File(mainStorageDir, String.valueOf(ts));
		    if (!sessionDir.exists())
		        if (!sessionDir.mkdirs())
		            return;
		    File textfile = new File(sessionDir + File.separator + String.valueOf(ts) + ".txt");
		    
		    try {
		    	BufferedWriter writer = new BufferedWriter(new FileWriter(textfile));
		       	writer.write(String.valueOf(ts)+"	0.08\n");
		       	writer.flush();
		       	writer.close();
			} catch (Exception e) {	}
		    
			Intent i_return =new Intent();
			Bundle bdata = new Bundle();
			bdata.putString("testfilename", String.valueOf(ts));
			i_return.putExtras(bdata);
			setResult(RESULT_OK,i_return);
			finish();
		}
	}
}
