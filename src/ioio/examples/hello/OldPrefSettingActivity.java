package ioio.examples.hello;

import ioio.examples.hello.R;

import java.util.List;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

public class OldPrefSettingActivity extends PreferenceActivity {

	
	 @SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        this.addPreferencesFromResource(R.xml.preferences);
	 }
	 
}
