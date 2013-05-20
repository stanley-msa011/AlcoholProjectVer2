package database;

import java.util.Calendar;

import android.content.Context;

public class TimeBlock {

	public final static int MAX = 3,MIN=0;
	
	public static boolean hasBlock(int block,int type){
		if (block == -1 || block == 0 || block == 3)
			return true;
		switch(type){
			case 3:
				if (block == 1)
					return true;
				break;
			case 4:
				if (block == 1 || block ==2)
					return true;
				break;
		}
		return false;
	}
	
	
	public static int getTimeBlock(int hour_24,int type){
		switch(type){
		case 2:
			if (hour_24 >= 6 && hour_24 <10)
				return 0;
			else if (hour_24 >=18 && hour_24 < 24)
				return 3;
			break;
		case 3:
			if (hour_24 >= 6 && hour_24 <10)
				return 0;
			else if(hour_24 >=10 && hour_24 < 14)
				return 1;
			else if (hour_24 >=18 && hour_24 < 24)
				return 3;
			break;
		case 4:
			if (hour_24 >= 6 && hour_24 <10)
				return 0;
			else if(hour_24 >=10 && hour_24 < 14)
				return 1;
			else if (hour_24 >= 14 && hour_24 < 18)
				return 2;
			else if (hour_24 >=18 && hour_24 < 24)
				return 3;
			break;
		default:
			return getTimeBlock(hour_24,2);
		}
		return -1;
	}
	
	static public boolean  isBlock(int hour_24,int type){
		switch(type){
		case 2:
			if (hour_24 >= 6 && hour_24 <10)
				return true;
			else if (hour_24 >=18 && hour_24 < 24)
				return true;
			break;
		case 3:
			if (hour_24 >= 6 && hour_24 <14)
				return true;
			else if (hour_24 >=18 && hour_24 < 24)
				return true;
			break;
		case 4:
			if (hour_24 >= 6 && hour_24 <24)
				return true;
			break;
		default:
			return false;
		}
		return false;
	}
	
	public static Calendar[] getCalendar(Context context,int type){
		Calendar[] cals = new Calendar[type];
		for (int i=0;i<cals.length;++i){
			cals[i] = Calendar.getInstance();
		}
		int year = cals[0].get(Calendar.YEAR);
		int month = cals[0].get(Calendar.MONTH);
		int day = cals[0].get(Calendar.DAY_OF_MONTH);
		
		switch (type){
		case 2 :
			cals[0].set(year, month, day, 8, 0, 0);
			cals[1].set(year, month, day, 21, 0, 0);
			break;
		case 3:
			cals[0].set(year, month, day, 8, 0, 0);
			cals[1].set(year, month, day, 12, 0, 0);
			cals[2].set(year, month, day, 21, 0, 0);
			break;
		case 4:
			cals[0].set(year, month, day, 8, 0, 0);
			cals[1].set(year, month, day, 12, 0, 0);
			cals[2].set(year, month, day, 16, 0, 0);
			cals[3].set(year, month, day, 21, 0, 0);
			break;
		}
		
		return cals;
	}
}
