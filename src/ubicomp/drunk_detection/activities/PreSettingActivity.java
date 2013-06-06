package ubicomp.drunk_detection.activities;

import java.util.Calendar;

import ubicomp.drunk_detection.activities.R;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class PreSettingActivity extends Activity {

	private EditText uid,target,drink,tb;
	private Button ok_button;
	private Activity activity;
	private static final int MIN_NAME_LENGTH = 3;
	
	private int mYear;
	private int mMonth;
	private int mDay;

	private TextView mDateDisplay;
	private Button mPickDate;
	
	private TextView versionText;
	
	private int target_t,drink_t,tb_t;
	
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
		
		target = (EditText) this.findViewById(R.id.target_money_edit);
		target.setText(String.valueOf(sp.getInt("goal_money", 10000)));
		drink= (EditText) this.findViewById(R.id.target_drink_edit);
		drink.setText(String.valueOf(sp.getInt("drink_cost", 200)));

		tb= (EditText) this.findViewById(R.id.target_tb_edit);
		tb.setText(String.valueOf(sp.getInt("timeblock_num", 3)));
		
		mDateDisplay = (TextView) findViewById(R.id.date);        
	    mPickDate = (Button) findViewById(R.id.date_button);
		
		Calendar c = Calendar.getInstance();
	    mYear = c.get(Calendar.YEAR);
	    mMonth = c.get(Calendar.MONTH);
	    mDay = c.get(Calendar.DAY_OF_MONTH);
		
	    mYear = sp.getInt("sYear", c.get(Calendar.YEAR));
	    mMonth = sp.getInt("sMonth", c.get(Calendar.MONTH));
	    mDay = sp.getInt("sDate", c.get(Calendar.DATE));
	    
		ok_button = (Button) this.findViewById(R.id.uid_OK);
		ok_button.setOnClickListener(new OKOnclickListener());
		
		versionText = (TextView) this.findViewById(R.id.version);
		
		versionText.setText("Verstion: "+Version.VERSION);
		
	    mPickDate.setOnClickListener(new View.OnClickListener() {
	        @SuppressWarnings("deprecation")
			public void onClick(View v) {
	            showDialog(DATE_DIALOG_ID);
	        }
	    });

	    updateDisplay();
		
	}

	private class OKOnclickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			String text = uid.getText().toString();
			boolean check = true;
			if (text.length() <MIN_NAME_LENGTH)
				check = false;
			if (target.getText().toString().length()==0)
				check = false;
			else{
				target_t = Integer.valueOf(target.getText().toString());
				if (target_t == 0)
					check = false;
			}
			
			if (drink.getText().toString().length()==0)
				check = false;
			else{
				drink_t = Integer.valueOf(drink.getText().toString());
				if (drink_t == 0)
					check = false;
			}
			if (tb.getText().toString().length()!=1)
				check = false;
			else{
				tb_t = Integer.valueOf(tb.getText().toString());
				if (tb_t<2 || tb_t > 4)
					check = false;
			}
			
			if (check){
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("uid", text);
				editor.putInt("goal_money", target_t);
				editor.putInt("drink_cost", drink_t);
				editor.putInt("sYear",mYear );
				editor.putInt("sMonth",mMonth );
				editor.putInt("sDate", mDay);
				//editor.putInt("timeblock_num", tb_t);
				editor.putInt("timeblock_num", 3);
				editor.commit();
				Log.d("PreSetting","Done");
			}
			activity.finish();
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
