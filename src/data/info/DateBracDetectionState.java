package data.info;

import java.util.Calendar;

import data.calculate.TimeBlock;

public class DateBracDetectionState extends BracDetectionState {

	public int year,month,day,timeblock;
	
	public DateBracDetectionState(int week, long timestamp, float brac, int emotion, int desire) {
		super(week, timestamp, brac,emotion,desire);
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(timestamp);
    	year = cal.get(Calendar.YEAR);
    	month = cal.get(Calendar.MONTH);
    	day = cal.get(Calendar.DATE);
    	int hour = cal.get(Calendar.HOUR_OF_DAY);
    	timeblock = TimeBlock.getTimeBlock(hour);
	}
	
	public DateBracDetectionState(int week, long timestamp, int year, int month, int day, int timeblock, float brac, int emotion, int desire) {
		super(week, timestamp, brac,emotion,desire);
    	this.year = year;
    	this.month = month;
    	this.day = day;
    	this.timeblock = timeblock;
	}
	
}
