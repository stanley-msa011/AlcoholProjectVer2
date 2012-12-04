package ioio.examples.hello;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class TestActivity extends Activity {
	
	private Button test_less_0_01;
	private Button test_more_0_01;
	private Button test_more_0_1;
	private Button test_more_0_25;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		View.OnClickListener test_listener = new testListener();
		test_less_0_01 = (Button)findViewById(R.id.test_less_0_01);
		test_less_0_01.setOnClickListener(test_listener);
		test_more_0_01 = (Button)findViewById(R.id.test_more_0_01);
		test_more_0_01.setOnClickListener(test_listener);
		test_more_0_1 = (Button)findViewById(R.id.test_more_0_1);
		test_more_0_1.setOnClickListener(test_listener);
		test_more_0_25 = (Button)findViewById(R.id.test_more_0_25);
		test_more_0_25.setOnClickListener(test_listener);
	}

	public class testListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			if (v.getId()==R.id.test_less_0_01){
				Intent i_return =new Intent();
				Bundle bdata = new Bundle();
				bdata.putString("testfilename", "1354163716");
				i_return.putExtras(bdata);
				setResult(RESULT_OK,i_return);
				finish();
			}
			else if (v.getId()==R.id.test_more_0_01){
				Intent i_return =new Intent();
				Bundle bdata = new Bundle();
				bdata.putString("testfilename", "1354560000");
				i_return.putExtras(bdata);
				setResult(RESULT_OK,i_return);
				finish();
			}
			else if (v.getId()==R.id.test_more_0_1){
				Intent i_return =new Intent();
				Bundle bdata = new Bundle();
				bdata.putString("testfilename", "1354255979");
				i_return.putExtras(bdata);
				setResult(RESULT_OK,i_return);
				finish();
			}
			else if (v.getId()==R.id.test_more_0_25){
				Intent i_return =new Intent();
				Bundle bdata = new Bundle();
				bdata.putString("testfilename", "1354255845");
				i_return.putExtras(bdata);
				setResult(RESULT_OK,i_return);
				finish();
			}
		}
		
	}
	


}
