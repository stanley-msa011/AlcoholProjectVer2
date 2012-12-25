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
	private Button gameButton;
	private Button historyButton;
	private Button settingButton;
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
		gameButton = (Button) findViewById(R.id.gameButton);
		historyButton = (Button) findViewById(R.id.historyButton);
		settingButton = (Button) findViewById(R.id.btnSettings);
	}
	
	private void setListeners() {
		btnInit.setOnClickListener(this);
		btnBracHistory.setOnClickListener(this);
		gameButton.setOnClickListener(this);
		historyButton.setOnClickListener(this);
		settingButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		Intent i_Init = new Intent();
		if (v.getId() == R.id.btnInit) {
			i_Init.setClass(this, MainActivity.class);
			startActivity(i_Init);
		} else if (v.getId() == R.id.btnBracHistory) {
			i_Init.setClass(this, BracHistoryActivity.class);
			startActivity(i_Init);
		} else if (v.getId() == R.id.gameButton) {
			i_Init.setClass(this, GameActivity.class);
			startActivity(i_Init);
		}  else if (v.getId() == R.id.historyButton) {
			i_Init.setClass(this, BracHistoryActivity.class);
			startActivity(i_Init);
		}else if (v.getId() == R.id.btnSettings){
			i_Init.setClass(this, PrefSettingActivity.class);
			startActivity(i_Init);
		}
		else {
		}
	}
}
