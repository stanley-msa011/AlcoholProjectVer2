package database;

public class TimeBlock {

	public final static int MAX = 3,MIN=0;
	
	
	public static int getTimeBlock(int hour_24){
		if (hour_24 >= 6 && hour_24 <11)
			return 0;
		else if (hour_24 >=11 && hour_24 < 14)
			return 1;
		else if (hour_24 >=14 && hour_24 < 19)
			return 2;
		else if (hour_24 >= 19 && hour_24 <24)
			return 3;
		return -1;
	}
}
