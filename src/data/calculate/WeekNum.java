package data.calculate;

import java.util.Calendar;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class WeekNum {

	public static int getWeek(Context context, long ts){
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Calendar c = Calendar.getInstance();
	    int year = sp.getInt("sYear", c.get(Calendar.YEAR));
	    int month = sp.getInt("sMonth", c.get(Calendar.MONTH));
	    int day = sp.getInt("sDate", c.get(Calendar.DATE));
		
	    c.set(year, month, day, 0, 0, 0);
	    c.set(Calendar.MILLISECOND, 0);
	    
	    long WEEK = AlarmManager.INTERVAL_DAY*7L;
	    
	    long time = ts - c.getTimeInMillis();
	    if (time < 0)
	    	return 0;
	    else
	    	return (int)(time/WEEK);
	}
	
}
