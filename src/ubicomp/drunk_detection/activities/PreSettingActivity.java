package ubicomp.drunk_detection.activities;

import java.util.Calendar;

import database.HistoryDB;
import database.QuestionDB;

import restore.RestoreData;
import statistic.ui.questionnaire.content.ConnectSocialInfo;
import test.data.Reuploader;
import ubicomp.drunk_detection.activities.R;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class PreSettingActivity extends Activity {

	private EditText uid,target_good,target,drink;
	
	private EditText connect_n0,connect_n1,connect_n2;
	private EditText connect_p0,connect_p1,connect_p2;
	
	private Button ok_button,clean_button,restoreButton,debugButton,dummyButton;
	boolean debug,dummy;
	private Activity activity;
	private static final int MIN_NAME_LENGTH = 3;
	
	private int mYear;
	private int mMonth;
	private int mDay;

	private TextView mDateDisplay;
	private Button mPickDate;
	
	private Spinner connect_s0,connect_s1,connect_s2;
	
	private TextView versionText;
	
	private String target_g;
	private int target_t,drink_t;
	
	private ArrayAdapter<String> adapter; 
	
	static final int DATE_DIALOG_ID = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pre_setting);
		activity = this;
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
		
		uid = (EditText) this.findViewById(R.id.uid_edit);
		uid.setText(sp.getString("uid", ""));
		
		target_good = (EditText) this.findViewById(R.id.target_good_edit);
		target_good.setText(String.valueOf(sp.getString("goal_good", "機車")));
		
		target = (EditText) this.findViewById(R.id.target_money_edit);
		target.setText(String.valueOf(sp.getInt("goal_money", 50000)));
		
		drink= (EditText) this.findViewById(R.id.target_drink_edit);
		drink.setText(String.valueOf(sp.getInt("drink_cost", 200)));

		
		connect_n0 = (EditText) this.findViewById(R.id.connect_name_0);
		connect_n0.setText(sp.getString("connect_n0", ""));
		
		connect_n1 = (EditText) this.findViewById(R.id.connect_name_1);
		connect_n1.setText(sp.getString("connect_n1", ""));
		
		connect_n2 = (EditText) this.findViewById(R.id.connect_name_2);
		connect_n2.setText(sp.getString("connect_n2", ""));
		
		connect_p0 = (EditText) this.findViewById(R.id.connect_0);
		connect_p0.setText(sp.getString("connect_p0", ""));
		
		connect_p1 = (EditText) this.findViewById(R.id.connect_1);
		connect_p1.setText(sp.getString("connect_p1", ""));
		
		connect_p2 = (EditText) this.findViewById(R.id.connect_2);
		connect_p2.setText(sp.getString("connect_p2", ""));
		
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,ConnectSocialInfo.NAME);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		connect_s0 = (Spinner) this.findViewById(R.id.connect_social_0);
		connect_s0.setAdapter(adapter);
		connect_s0.setSelection(sp.getInt("connect_s0", 0));
		connect_s1 = (Spinner) this.findViewById(R.id.connect_social_1);
		connect_s1.setAdapter(adapter);
		connect_s1.setSelection(sp.getInt("connect_s1", 1));
		connect_s2 = (Spinner) this.findViewById(R.id.connect_social_2);
		connect_s2.setAdapter(adapter);
		connect_s2.setSelection(sp.getInt("connect_s2", 2));
		
		mDateDisplay = (TextView) findViewById(R.id.date);        
	    mPickDate = (Button) findViewById(R.id.date_button);
		
		Calendar c = Calendar.getInstance();
	    mYear = sp.getInt("sYear", c.get(Calendar.YEAR));
	    mMonth = sp.getInt("sMonth", c.get(Calendar.MONTH));
	    mDay = sp.getInt("sDate", c.get(Calendar.DATE));
	    
		ok_button = (Button) this.findViewById(R.id.uid_OK);
		ok_button.setOnClickListener(new OKOnclickListener());
		
		versionText = (TextView) this.findViewById(R.id.version);
		
		PackageInfo pinfo;
		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String versionName = pinfo.versionName;
			versionText.setText("Verstion: "+versionName);
		} catch (NameNotFoundException e) {
		}
		
		
	    mPickDate.setOnClickListener(new View.OnClickListener() {
	        @SuppressWarnings("deprecation")
			public void onClick(View v) {
	            showDialog(DATE_DIALOG_ID);
	        }
	    });

	    updateDisplay();
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("確定清除點數?");
	    builder.setMessage("確定?");
	    builder.setPositiveButton("確定", new CleanListener());
	    builder.setNegativeButton("取消",null );
	    alertDialog = builder.create();
	    clean_button = (Button) this.findViewById(R.id.clean_OK);
	    clean_button.setOnClickListener(new CleanOnClickListener());
		
	    
	    restoreButton = (Button) this.findViewById(R.id.restore);
	    restoreButton.setOnClickListener(new RestoreOnClickListener());
	    
	    debug = sp.getBoolean("debug", false);
	    dummy = sp.getBoolean("Dummy", false);
	    debugButton = (Button) this.findViewById(R.id.debug_normal_switch);
	    if (debug)
	    	debugButton.setText("Switch to normal mode");
	    else
	    	debugButton.setText("Switch to debug mode");
	    debugButton.setOnClickListener(new DebugOnClickListener());
	    
	    
	    dummyButton = (Button) this.findViewById(R.id.dummy_data_switch);
	    if (dummy)
	    	dummyButton.setText("Delete debug data");
	    else
	    	dummyButton.setText("Use debug data");
	    dummyButton.setOnClickListener(new DummyOnClickListener());
	}

	private class RestoreOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			RestoreData rd = new RestoreData(uid.getText().toString(),activity);
			rd.execute();
		}
	}
	
	private class DebugOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
	    	debug = !debug;
	    	SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	    	SharedPreferences.Editor editor = sp.edit();
	    	editor.putBoolean("debug", debug);
			editor.commit();
			if (debug)
		    	debugButton.setText("Switch to normal mode");
		    else
		    	debugButton.setText("Switch to debug mode");
		}
	}
	
	private class DummyOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			DummyData.generateDummyData(getBaseContext());
			dummy = !dummy;
			if (dummy)
		    	dummyButton.setText("Delete debug data");
		    else
		    	dummyButton.setText("Use debug data");
		}
	}
	
	private class OKOnclickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			String text = uid.getText().toString();
			boolean check = true;
			if (text.length() <MIN_NAME_LENGTH)
				check = false;
			
			target_g = target_good.getText().toString();
			if (target_g.length()==0)
				check = false;
			
			if (target.getText().toString().length()==0)
				check = false;
			else{
				target_t = Integer.valueOf(target.getText().toString());
				if (target_t <= 0)
					check = false;
			}
			
			String connectN0,connectN1,connectN2;
			String connectP0,connectP1,connectP2;
			
			connectN0 = connect_n0.getText().toString();
			connectN1 = connect_n1.getText().toString();
			connectN2 = connect_n2.getText().toString();
			
			connectP0 = connect_p0.getText().toString();
			connectP1 = connect_p1.getText().toString();
			connectP2 = connect_p2.getText().toString();
			
			int connectS0,connectS1,connectS2;
			connectS0 = connect_s0.getSelectedItemPosition();
			connectS1 = connect_s1.getSelectedItemPosition();
			connectS2 = connect_s2.getSelectedItemPosition();
			
			if (drink.getText().toString().length()==0)
				check = false;
			else{
				drink_t = Integer.valueOf(drink.getText().toString());
				if (drink_t == 0)
					check = false;
			}
			
			if (check){
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("uid", text);
				editor.putString("goal_good", target_g);
				editor.putInt("goal_money", target_t);
				editor.putInt("drink_cost", drink_t);
				editor.putInt("sYear",mYear );
				editor.putInt("sMonth",mMonth );
				editor.putInt("sDate", mDay);
				editor.putString("connect_n0", connectN0);
				editor.putString("connect_n1", connectN1);
				editor.putString("connect_n2", connectN2);
				editor.putString("connect_p0", connectP0);
				editor.putString("connect_p1", connectP1);
				editor.putString("connect_p2", connectP2);
				editor.putInt("connect_s0", connectS0);
				editor.putInt("connect_s1", connectS1);
				editor.putInt("connect_s2", connectS2);
				editor.commit();
				Log.d("PreSetting","Done");
			}
			activity.finish();
		}
		
	}
	
	private AlertDialog alertDialog;
	
	private class CleanOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			alertDialog.show();
		}
	}
	
	private class CleanListener implements DialogInterface.OnClickListener{
		@Override
		public void onClick(DialogInterface dialog, int which) {
			HistoryDB hdb = new HistoryDB(activity);
			QuestionDB qdb = new QuestionDB(activity);
			hdb.cleanAcc();
			qdb.cleanAcc();
			Reuploader.reuploader(activity);
		}
	}
	
	private void updateDisplay() {
	    this.mDateDisplay.setText(
	        new StringBuilder()
	                // Month is 0 based so add 1
	                .append(mMonth + 1).append("-")
	                .append(mDay).append("-")
	                .append(mYear).append(" "));
	}
	
	private DatePickerDialog.OnDateSetListener mDateSetListener =
		    new DatePickerDialog.OnDateSetListener() {
		        public void onDateSet(DatePicker view, int year, 
		                              int monthOfYear, int dayOfMonth) {
		            mYear = year;
		            mMonth = monthOfYear;
		            mDay = dayOfMonth;
		            updateDisplay();
		        }
		    };
		    
		    @Override
		    protected Dialog onCreateDialog(int id) {
		       switch (id) {
		       case DATE_DIALOG_ID:
		          return new DatePickerDialog(this,
		                    mDateSetListener,
		                    mYear, mMonth, mDay);
		       }
		       return null;
		    }    
}
