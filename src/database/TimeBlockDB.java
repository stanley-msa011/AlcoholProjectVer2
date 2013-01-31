package database;

import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class TimeBlockDB {
	private SQLiteOpenHelper DBHelper = null;
    private SQLiteDatabase DB = null;
    
    private static int [] BLOCK_RANGE = {0,6,6,12,12,18,18,24};
    
    public TimeBlockDB(Context context){
		DBHelper = new DBHelper(context);
    }
    
    public boolean checkSameTimeBlock(long ts_sec){
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(ts_sec*1000L);
    	int year = cal.get(Calendar.YEAR);
    	int month = cal.get(Calendar.MONTH) + 1;
    	int date = cal.get(Calendar.DATE);
    	int hour = cal.get(Calendar.HOUR_OF_DAY);
    	int block = getBlock(hour);
    	if (block == -1)
    		return true;
    	boolean result = false;
    	DB = DBHelper.getReadableDatabase();
    	String query = 
    			"SELECT COUNT(_ID) FROM DayCompletion WHERE _YEAR="+year+" AND _MONTH="+month+" AND _DATE="+date+" AND _BLOCK="+block;
    	Cursor cursor = DB.rawQuery(query, null);
    	if (cursor.moveToFirst()){
    		int count = cursor.getInt(0);
    		if (count > 0)
    			result =  true;
    	}
    	DB.close();
    	return result;
    }
    
    public void updateTimeBlock(long ts_sec){
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(ts_sec*1000L);
    	int year = cal.get(Calendar.YEAR);
    	int month = cal.get(Calendar.MONTH) + 1;
    	int date = cal.get(Calendar.DATE);
    	int hour = cal.get(Calendar.HOUR_OF_DAY);
    	int block = getBlock(hour);
    	if (block == -1)
    		return;
    	DB = DBHelper.getWritableDatabase();
    	String query = 
    			"SELECT COUNT(_ID) FROM DayCompletion WHERE _YEAR="+year+" AND _MONTH="+month+" AND _DATE="+date+" AND _BLOCK="+block;
    	Cursor cursor = DB.rawQuery(query, null);
    	if (cursor.moveToFirst()){
    		int count = cursor.getInt(0);
    		if (count == 0){
    			query = "INSERT INTO DayCompletion (_YEAR,_MONTH,_DATE,_BLOCK) VALUES ("+year+","+month+","+date+","+block+")";
    			DB.execSQL(query);
    		}
    	}
    	DB.close();
    }
    private int getBlock (int hour){
    	if (hour >= BLOCK_RANGE[0] && hour <  BLOCK_RANGE[1])
    		return 0;
    	else if (hour >= BLOCK_RANGE[2] && hour <  BLOCK_RANGE[3])
    		return 1;
    	else if (hour >= BLOCK_RANGE[4] && hour <  BLOCK_RANGE[5])
    		return 2;
    	else if (hour >= BLOCK_RANGE[6] && hour <  BLOCK_RANGE[7])
    		return 3;
    	return -1;
    }
    
    public boolean checkSameTimeBlock(int year,int month, int date,int block){
    	if (block < 0 || block >= 4)
    		return false;
    	boolean result = false;
    	DB = DBHelper.getReadableDatabase();
    	String query = 
    			"SELECT COUNT(_ID) FROM DayCompletion WHERE _YEAR="+year+" AND _MONTH="+month+" AND _DATE="+date+" AND _BLOCK="+block;
    	Cursor cursor = DB.rawQuery(query, null);
    	if (cursor.moveToFirst()){
    		int count = cursor.getInt(0);
    		if (count > 0)
    			result =  true;
    	}
    	DB.close();
    	return result;
    }
    
}
