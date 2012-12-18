package ioio.examples.hello;

import ioio.examples.hello.R;

import java.util.List;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;

public class PrefSettingActivity extends PreferenceActivity {

	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private PrefFragment prefFragment;
	
	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        fragmentManager = getFragmentManager();  
	        fragmentTransaction = fragmentManager.beginTransaction();  
	        prefFragment = new PrefFragment();  
	        fragmentTransaction.replace(android.R.id.content, prefFragment);          
	        fragmentTransaction.commit();
	        
	 }
	 
    public void onBuildHeaders(List<Header> target) {
    }
    
    public static class PrefFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            
        }
        public Preference getGPSPref(){
        	return this.findPreference("enable_gps_check");
        }
    }

}
