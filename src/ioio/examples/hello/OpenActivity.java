package ioio.examples.hello;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OpenActivity extends Activity implements View.OnClickListener {
	
	private static final String TAG = "OpenActivity";
	
	private Button btnInit;
	private Button btnBracHistory;
	
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
	
	private void findViews() {
		btnInit = (Button) findViewById(R.id.btnInit);
		btnBracHistory = (Button) findViewById(R.id.btnBracHistory);
	}
	
	private void setListeners() {
		btnInit.setOnClickListener(this);
		btnBracHistory.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnInit:
			Intent i_Init = new Intent();
			i_Init.setClass(this, MainActivity.class);
			startActivity(i_Init);
			break;
		case R.id.btnBracHistory:
			Intent i_Hist = new Intent();
			i_Hist.setClass(this, BracListActivity.class);
			startActivity(i_Hist);
			break;
		default:
			break;
		}
	}
}
