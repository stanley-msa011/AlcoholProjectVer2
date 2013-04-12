package ioio.examples.hello;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class PreSettingActivity extends Activity {

	private EditText uid;
	private Button ok_button;
	private Activity activity;
	private static final int MIN_NAME_LENGTH = 3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pre_setting);
		activity = this;
		uid = (EditText) this.findViewById(R.id.uid_edit);
		ok_button = (Button) this.findViewById(R.id.uid_OK);
		ok_button.setOnClickListener(new OKOnclickListener());
	}

	private class OKOnclickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			String text = uid.getText().toString();
			Log.d("UID",text);
			if (text.length() ==MIN_NAME_LENGTH){
				Log.d("UID edit","edit");
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("uid", text);
				editor.commit();
			}
			activity.finish();
		}
		
	}
	
	
}
