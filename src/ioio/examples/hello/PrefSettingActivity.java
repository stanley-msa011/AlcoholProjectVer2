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

public class PrefSettingActivity extends PreferenceActivity {

	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private PrefFragment prefFragment;
	
	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        fragmentManager = getFragmentManager();  
	        fragmentTransaction = fragmentManager.beginTransaction();  
	        prefFragment = new PrefFragment();  
	        Log.d("Pref","Before replace");
	        fragmentTransaction.replace(android.R.id.content, prefFragment);          
	        Log.d("Pref","Before commit");
	        fragmentTransaction.commit();
	        Log.d("Pref","end");
	        
	 }
	 
    public void onBuildHeaders(List<Header> target) {
    }
    
    public static class PrefFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }

}
