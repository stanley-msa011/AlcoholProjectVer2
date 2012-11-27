package ioio.examples.hello;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class OpenActivity extends Activity implements View.OnClickListener {
	
	private static final String TAG = "OpenActivity";
	
	private Button btnInit;
	private Button btnBracHistory;
	private Button btnSettings;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.open_activity);
        
        findViews();
        setListeners();
        
        // Start service at back

      	Intent intent = new Intent(this, TimerService.class);
      	startService(intent);

	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		Log.d(TAG, "Restarting...");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "Starting...");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "Resuming...");
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "Pausing...");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "Stopping...");
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if (hasFocus)
			Log.d(TAG, "HAS FOCUS");
	}
	
	private void findViews() {
		btnInit = (Button) findViewById(R.id.btnInit);
		btnBracHistory = (Button) findViewById(R.id.btnBracHistory);
		btnSettings = (Button) findViewById(R.id.btnSettings);
	}
	
	private void setListeners() {
		btnInit.setOnClickListener(this);
		btnBracHistory.setOnClickListener(this);
		btnSettings.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnInit:
			Intent i_Init = new Intent();
			i_Init.setClass(this, MainActivity.class);
//			i_Init.setClass(this, SensorCheckActivity.class);
			startActivity(i_Init);
			break;
		case R.id.btnBracHistory:
			Intent i_Hist = new Intent();
			i_Hist.setClass(this, BracListActivity.class);
			startActivity(i_Hist);
			break;
		case R.id.btnSettings:
			Intent i_Settings = new Intent();
			i_Settings.setClass(this, SettingsActivity.class);
			startActivity(i_Settings);
			break;
		default:
			break;
		}
	}
}
