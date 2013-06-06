package database;

public class TimeBlock {

	public final static int MAX = 3,MIN=0;
	
	public static boolean hasBlock(int block,int type){
		if (block == -1 || block == 0 || block == 1 || block == 3)
			return true;
		/*
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
		*/
		return false;
	}
	
	
	public static int getTimeBlock(int hour_24,int type){
		if (hour_24 <12)
			return 0;
		else if(hour_24 >=12 && hour_24 < 18)
			return 1;
		else if (hour_24 >=18 && hour_24 < 24)
			return 3;
		return -1;
	/*	switch(type){
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
		*/
	}
	
	static public boolean  isBlock(int hour_24,int type){
		return true;
		/*
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
		*/
	}
	
	public static boolean isEmpty(int timeblock,  int cur_hour){
		switch (timeblock){
		case 0:
			if (cur_hour >= 12)
				return false;
			break;
		case 1:
			if (cur_hour >= 18)
				return false;
			break;
		case 3:
			if (cur_hour >=24)
				return false;
			break;
		}
		
		/*
		switch (timeblock){
			case 0:
				if (cur_hour >= 10)
					return false;
				break;
			case 1:
				if (cur_hour >= 14)
					return false;
				break;
			case 2:
				if (cur_hour >= 18)
					return false;
				break;
			case 3:
				if (cur_hour >=24)
					return false;
				break;
		}
		*/
		return true;
	}
}
