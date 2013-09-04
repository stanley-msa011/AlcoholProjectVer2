package data.database;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StartDateCheck {
	public static boolean check(Context context){
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		
		Calendar now = Calendar.getInstance();
		
		Calendar start_date = Calendar.getInstance();
		int year = sp.getInt("sYear", start_date.get(Calendar.YEAR));
		int month = sp.getInt("sMonth", start_date.get(Calendar.MONTH));
		int date = sp.getInt("sDay", Calendar.DAY_OF_MONTH);
		start_date.set(year, month, date, 0, 0,0);
		start_date.set(Calendar.MILLISECOND, 0);
		
		if (now.after(start_date))
			return true;
		return false;
	}
}
