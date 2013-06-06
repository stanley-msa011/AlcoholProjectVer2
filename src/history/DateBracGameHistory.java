package history;

import java.util.Calendar;

import ubicomp.drunk_detection.activities.FragmentTabs;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import database.TimeBlock;


public class DateBracGameHistory extends BracGameHistory {

	public int year,month,date,hour,timeblock;
	
	public DateBracGameHistory(int level, long timestamp, float brac, int emotion, int desire) {
		super(level, timestamp, brac,emotion,desire);
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(timestamp*1000);
    	year = cal.get(Calendar.YEAR);
    	month = cal.get(Calendar.MONTH)+1;
    	date = cal.get(Calendar.DATE);
    	hour = cal.get(Calendar.HOUR_OF_DAY);

    	
    	Context context = FragmentTabs.getContext();
    	if (context == null)
    		timeblock = TimeBlock.getTimeBlock(hour,2);
    	else{
    		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(FragmentTabs.getContext());
    		int timeblock_type = sp.getInt("timeblock_num", 2);
    		timeblock = TimeBlock.getTimeBlock(hour,timeblock_type);
    	}
	}
	
	public String toString(){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp*1000);
		String s= "lv: " + level + " brac: "+ brac + " e: "+emotion + " d: "+ desire +"@"+cal.toString(); 
		return s;
	}


}
