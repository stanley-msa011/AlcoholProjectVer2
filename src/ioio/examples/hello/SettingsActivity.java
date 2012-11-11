package ioio.examples.hello;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

public class SettingsActivity extends Activity {
	private final static String TAG = "SettingsActivity";
	
	private ListView lvSettings;
	
	private ArrayAdapter<String> settingsAdapter;
	private String mSettingsContent[] = {"Share My Location"};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		
		settingsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, mSettingsContent);
		lvSettings = (ListView) findViewById(R.id.lvSettings);
		lvSettings.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		lvSettings.setAdapter(settingsAdapter);
	}
	
	public void onCheckboxClicked(View view) {
	    // Is the view now checked?
	    boolean checked = ((CheckedTextView) view).isChecked();
	    
	    // Check which checkbox was clicked
	    switch(view.getId()) {
	        case R.id.chktvShareLoc:
	            if (checked)
	            	// Check to turn on GPS
	            break;
	    }
	}

}
