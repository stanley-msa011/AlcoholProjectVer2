package new_database;

import java.util.Calendar;

import game.GameState;
import history.BracGameHistory;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HistoryDB {

	private SQLiteOpenHelper dbHelper = null;
    private SQLiteDatabase db = null;
	
	public HistoryDB(Context context){
		dbHelper = new DBHelper(context);
	}
	
    public BracGameHistory getLatestBracGameHistory(){

    	db = dbHelper.getReadableDatabase();
    	Cursor cursor = db.rawQuery("SELECT MAX(_ID) FROM HistoryGame", null);
    	
    	cursor.moveToFirst();
    	int max_id = cursor.getInt(0);
    	cursor = db.rawQuery("SELECT * FROM HistoryGame WHERE _ID="+String.valueOf(max_id),null);

    	if (cursor.getCount()==0){
    		db.close();
    		return new BracGameHistory(0,0,0);
    	}
    	
    	cursor.moveToFirst();
    	int level_idx = cursor.getColumnIndex("_LEVEL");
    	int ts_idx = cursor.getColumnIndex("_TS");
    	int brac_idx = cursor.getColumnIndex("_BRAC");
    	
    	if (level_idx==-1||ts_idx==-1||brac_idx==-1){
    		Log.d("DATABASE","CANNOT FIND IDXs");
    		db.close();
    		return new BracGameHistory(0,0,0);
    	}
    	int level = cursor.getInt(level_idx);
    	long ts = cursor.getLong(ts_idx);
    	float brac = cursor.getFloat(brac_idx);
    	
    	db.close();
    	return new BracGameHistory(level,ts,brac);
    }
    
    public void insertNewState(BracGameHistory history){
    	int level = history.level;
    	long ts = history.timestamp;
    	long ts_inMillis = ts*1000L;
    	float brac = history.brac;
    	db = dbHelper.getWritableDatabase();
    	int year,month,date,timeblock,hour;
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(ts_inMillis);
    	year = cal.get(Calendar.YEAR);
    	month = cal.get(Calendar.MONTH)+1;
    	date = cal.get(Calendar.DATE);
    	hour = cal.get(Calendar.HOUR_OF_DAY);
    	timeblock = TimeBlock.getTimeBlock(hour);
    	
    	String sql = "INSERT INTO HistoryGame (_LEVEL,_YEAR,_MONTH,_DATE,_TS,_TIMEBLOCK, _BRAC) VALUES ("
    							+level+","+year+","+month+","+date+","+ts+","+timeblock+","+brac+")";
    	db.execSQL(sql);
    	db.close();
    }
    
    public BracGameHistory[] getTodayBracGameHistory(){

    	BracGameHistory[] historys = new BracGameHistory[4];
    	
    	Calendar cal = Calendar.getInstance();
    	int year = cal.get(Calendar.YEAR);
    	int month = cal.get(Calendar.MONTH)+1;
    	int date = cal.get(Calendar.DATE);
    	
    	db = dbHelper.getReadableDatabase();
    	
    	for (int i=0;i<4;++i){
    		String sql = "SELECT _BRAC FROM HistoryGame WHERE _YEAR="+year
    				+" AND _MONTH="+month
    				+" AND _DATE="+date
    				+" AND _TIMEBLOCK="+i
    				+" ORDER BY _ID DESC";
    		Cursor cursor = db.rawQuery(sql, null);
    		
    		if (cursor.getCount()==0){
        		historys[i]=null;
        		continue;
        	}
    		cursor.moveToFirst();
    		float brac = cursor.getFloat(0);
    		historys[i] = new BracGameHistory(0,0,brac);
    	}
    	return historys;
    }
    
	
}
