package ubicomp.drunk_detection.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.Activity;
import android.content.Intent;

public class DeveloperActivity extends Activity {

	private EditText password;
	private Button enter;
	
	private static final String PASSWORD = "UBICOMP_MLL336_2013"; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_developer);
		
		password = (EditText) this.findViewById(R.id.developer_edit);
		enter = (Button) this.findViewById(R.id.developer_button);
		enter.setOnClickListener(new EnterOnClickListener());
	}
	
	private class EnterOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			String pwd = password.getText().toString();
			if (pwd.equals(PASSWORD)){
				Intent newIntent = new Intent(getBaseContext(), PreSettingActivity.class);
				startActivity(newIntent);
				finish();
			}else
				finish();
		}
		
	}

}
