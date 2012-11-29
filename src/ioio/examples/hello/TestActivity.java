package ioio.examples.hello;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class TestActivity extends Activity {
	
	private Button testGetButton;
	private Button testLoseButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		testGetButton = (Button)findViewById(R.id.testGetButton);
		testGetButton.setOnClickListener(new testListener());
		testLoseButton = (Button)findViewById(R.id.testLoseButton);
		testLoseButton.setOnClickListener(new testListener());
	}

	public class testListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			if (v.getId()==R.id.testGetButton){
				Intent i_return =new Intent();
				Bundle bdata = new Bundle();
				bdata.putString("testfilename", "1354163716");
				i_return.putExtras(bdata);
				setResult(RESULT_OK,i_return);
				finish();
			}
			else if (v.getId()==R.id.testLoseButton){
				Intent i_return =new Intent();
				Bundle bdata = new Bundle();
				bdata.putString("testfilename", "1354560000");
				i_return.putExtras(bdata);
				setResult(RESULT_OK,i_return);
				finish();
			}
		}
		
	}
	


}
