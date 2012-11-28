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
	private Button gameGetButton;
	private Button gameLoseButton;
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
		gameGetButton = (Button) findViewById(R.id.game_get);
		gameLoseButton = (Button) findViewById(R.id.game_lose);
	}
	
	private void setListeners() {
		btnInit.setOnClickListener(this);
		btnBracHistory.setOnClickListener(this);
		gameButton.setOnClickListener(this);
		gameGetButton.setOnClickListener(this);
		gameLoseButton.setOnClickListener(this);
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
		case R.id.gameButton:
			Intent GameIntent = new Intent();
			GameActivity.setStartAction(GameActivity.START_DO_NOTHING);
			GameIntent.setClass(this, GameActivity.class);
			startActivity(GameIntent);
			break;
		case R.id.game_get:
			Intent GameIntent2 = new Intent();
			GameActivity.setStartAction(GameActivity.START_GET_COIN);
			GameIntent2.setClass(this, GameActivity.class);
			startActivity(GameIntent2);
			break;
		case R.id.game_lose:
			Intent GameIntent3 = new Intent();
			GameActivity.setStartAction(GameActivity.START_LOSE_COIN);
			GameIntent3.setClass(this, GameActivity.class);
			startActivity(GameIntent3);
			break;
		default:
			break;
		}
	}
}
