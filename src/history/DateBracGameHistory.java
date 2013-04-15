package history;

import java.util.Calendar;

import database.TimeBlock;


public class DateBracGameHistory extends BracGameHistory {

	public int year,month,date,hour,timeblock;
	
	public DateBracGameHistory(int level, long timestamp, float brac) {
		super(level, timestamp, brac);
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(timestamp*1000);
    	year = cal.get(Calendar.YEAR);
    	month = cal.get(Calendar.MONTH)+1;
    	date = cal.get(Calendar.DATE);
    	hour = cal.get(Calendar.HOUR_OF_DAY);
    	timeblock = TimeBlock.getTimeBlock(hour);
	}


}
