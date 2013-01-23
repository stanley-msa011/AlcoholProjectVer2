package ioio.examples.hello;

import ioio.examples.hello.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class OldPrefSettingActivity extends PreferenceActivity {

	
	 @SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        this.addPreferencesFromResource(R.xml.preferences);
	 }
	 
}
