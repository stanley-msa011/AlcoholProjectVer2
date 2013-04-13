package new_database;

import java.util.Calendar;
import java.util.Date;

import test.data.BracDataHandler;

import history.BracGameHistory;
import history.DateBracGameHistory;
import history.InteractionHistory;
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
	
    public DateBracGameHistory getLatestBracGameHistory(){

    	db = dbHelper.getReadableDatabase();
    	Cursor cursor = db.rawQuery("SELECT MAX(_ID) FROM HistoryGame", null);
    	
    	cursor.moveToFirst();
    	int max_id = cursor.getInt(0);
    	cursor = db.rawQuery("SELECT * FROM HistoryGame WHERE _ID="+String.valueOf(max_id),null);

    	if (cursor.getCount()==0){
    		db.close();
    		return new DateBracGameHistory(0,0,0);
    	}
    	
    	cursor.moveToFirst();
    	int level_idx = cursor.getColumnIndex("_LEVEL");
    	int ts_idx = cursor.getColumnIndex("_TS");
    	int brac_idx = cursor.getColumnIndex("_BRAC");
    	
    	if (level_idx==-1||ts_idx==-1||brac_idx==-1){
    		Log.d("DATABASE","CANNOT FIND IDXs");
    		db.close();
    		return new DateBracGameHistory(0,0,0);
    	}
    	int level = cursor.getInt(level_idx);
    	long ts = cursor.getLong(ts_idx);
    	float brac = cursor.getFloat(brac_idx);
    	
    	db.close();
    	return new DateBracGameHistory(level,ts,brac);
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
    	db.close();
    	return historys;
    }
    
    public int getAllBracGameScore(){

    	int MIN = 1;
    	int MAX = 9;
    	int MISS = -2;
    	int PASS = +1;
    	int FAIL = -1;
    	
    	int score = (MIN+MAX)/2;
    	db = dbHelper.getReadableDatabase();
    	
    	String sql = "SELECT * FROM HistoryGame WHERE _TIMEBLOCK <> -1 ORDER BY _ID ASC";
    	Cursor cursor = db.rawQuery(sql, null);
    	
    	if (cursor.getCount()==0){
    		db.close();
    		return score;
    	}
    	cursor.moveToFirst();
    	int year_idx = cursor.getColumnIndex("_YEAR");
    	int month_idx = cursor.getColumnIndex("_MONTH");
    	int date_idx = cursor.getColumnIndex("_DATE");
    	int tb_idx = cursor.getColumnIndex("_TIMEBLOCK");
    	int brac_idx = cursor.getColumnIndex("_BRAC");
    	
    	int year = cursor.getInt(year_idx);
    	int month = cursor.getInt(month_idx);
    	int date = cursor.getInt(date_idx);
    	int tb = cursor.getInt(tb_idx);
    	float brac = cursor.getFloat(brac_idx);
    	Calendar cur = Calendar.getInstance();
    	cur.set(year, month, date, 0, 0, 0);
    	cur.set(Calendar.MILLISECOND, 0);
    	
    	if (brac > BracDataHandler.THRESHOLD)
    		score+=MISS;
    	else
    		score+=PASS;
    	
    	if (score < MIN)
    		score = MIN;
    	else if (score >MAX)
    		score =MAX;
    	
    	while (cursor.moveToNext()){
    		year = cursor.getInt(year_idx);
        	month = cursor.getInt(month_idx);
        	date = cursor.getInt(date_idx);
        	Calendar next = Calendar.getInstance();
        	next.set(year, month, date, 0, 0, 0);
        	next.set(Calendar.MILLISECOND, 0);
        	
        	int _tb = cursor.getInt(tb_idx);
        	float _brac = cursor.getFloat(brac_idx);
        	
        	if (_tb==-1){//not count
        		continue;
        	}
        	if (tb==-1){	//not count
        		cur = next;
        		tb = _tb;
        		continue;
        	}
        
        	int diff_day = next.getTime().compareTo(cur.getTime());
        	if (diff_day >0){//diff_day
        		score += MISS *((_tb-TimeBlock.MIN)+(TimeBlock.MAX-tb)+diff_day*4);
        		if (score < MIN)
        			score = MIN;
        		}
        	else{//same day
        		if (_tb -1 > tb){
        			score += MISS*(_tb-tb);
        			if (score < MIN)
            			score = MAX;
        		}
        		if (_tb!=tb){
            		if (_brac > BracDataHandler.THRESHOLD)
            			score += FAIL;
            		else
            			score += PASS;
            		if (score < MIN)
                		score = MIN;
                	else if (score >MAX)
                		score =MAX;
        		}
        	}
        	
        	cur = next;
    		tb = _tb;
    	}
    	db.close();
		return score;
    }
	
    public BracGameHistory[] getMultiDayInfo(int n_days){
    	Log.d("DB","multi-day "+String.valueOf(n_days));
    	
    	
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MILLISECOND, 0);
    	long ts = cal.getTimeInMillis()/1000L;
    	long ts_days = (n_days-1)*3600*24;
    	long start_ts = ts - ts_days;
    	
    	String sql = "SELECT * FROM HistoryGame WHERE _TIMEBLOCK <> -1 AND _TS >= "+start_ts+" ORDER BY _ID ASC";
    	
    	db = dbHelper.getReadableDatabase();
    	Cursor cursor = db.rawQuery(sql, null);

    	String cu = cursor.getCount()+"";
    	Log.d("DB",cu);
    	
    	BracGameHistory[] historys = new BracGameHistory[n_days*4];
    	
    	int brac_idx = cursor.getColumnIndex("_BRAC");
    	int ts_idx = cursor.getColumnIndex("_TS");
    	int tb_idx = cursor.getColumnIndex("_TIMEBLOCK");
    	
    	int cursor_pointer = 0;
    	cursor.moveToFirst();
    	long ts_from = start_ts;
    	long ts_to = start_ts+3600*24;
    	long _ts;
    	int _tb;
    	float _brac;
    	int _size = cursor.getCount();
    	
    	for (int i=0;i<historys.length;++i){
    		
    		cursor_pointer = 0;
    		while (cursor_pointer <_size){
    			cursor.moveToPosition(cursor_pointer);
    			_ts = cursor.getLong(ts_idx);
    			if (_ts >= ts_from && _ts < ts_to){// match date
    				_tb = cursor.getInt(tb_idx);
    				if (_tb ==i%4){
    					_brac = cursor.getFloat(brac_idx);
    					historys[i] = new BracGameHistory(0,_ts,_brac);
    					++cursor_pointer;
    					break;
    				}
    				
    			}
    			++cursor_pointer;
    		}
    		
    		if (i%4==3){
    			ts_from+=3600*24;
    			ts_to+=3600*24;
    		}
    	}
    	
    	return historys;
    }
    
    public void insertNotUploadedTS(long ts){
    	db = dbHelper.getWritableDatabase();
    	String sql = "SELECT * FROM NotUploadedTS WHERE _TS = "+ts;
    	Cursor cursor = db.rawQuery(sql, null);
    	if (cursor.getCount() == 0){
    		sql = "INSERT INTO NotUploadedTS (_TS) VALUES ("+ts+")";
    		db.execSQL(sql);
    	}
    	db.close();
    }
    
    public void removeNotUploadedTimeStamp(long ts){
    	db = dbHelper.getWritableDatabase();
    	String sql = "DELETE FROM NotUploadedTS WHERE _TS = "+ts;
    	db.execSQL( sql);
    	db.close();
    }
    
    public long[] getAllNotUploadedTS(){
    	db = dbHelper.getReadableDatabase();
    	String sql = "SELECT * FROM NotUploadedTS ORDER BY _ID ASC";
    	Cursor cursor = db.rawQuery(sql, null);
    	int count = cursor.getCount();
    	if (count == 0)
    		return null;
    	long[] ts = new long[count];
    	int ts_idx = cursor.getColumnIndex("_TS");
    	for (int i=0;i<count;++i){
    		cursor.moveToPosition(i);
    		ts[i] =cursor.getLong(ts_idx); 
    	}
    	db.close();
    	return ts;
    }
    
    public InteractionHistory[] getAllUsersHistory(){
    	InteractionHistory[] historys = null;
    	db = dbHelper.getReadableDatabase();
    	String sql = "SELECT * FROM InteractionGame ORDER BY _LEVEL DESC,  _UID ASC";
    	Cursor cursor = db.rawQuery(sql, null);
    	int count = cursor.getCount();
    	if (count == 0)
    		return null;
    	historys = new InteractionHistory[count];
    	int uid_idx = cursor.getColumnIndex("_UID");
    	int level_idx = cursor.getColumnIndex("_LEVEL");
    	for (int i=0;i<count;++i){
    		cursor.moveToPosition(i);
    		String uid = cursor.getString(uid_idx);
    		int level = cursor.getInt(level_idx);
    		historys[i] = new InteractionHistory(level,uid);
    	}
    	db.close();
    	return historys;
    }
    
    public void insertInteractionHistory(InteractionHistory history){
    	db = dbHelper.getWritableDatabase();
    	String sql = "SELECT * FROM InteractionGame WHERE _UID = '"+history.uid+"'";
    	Cursor cursor = db.rawQuery(sql, null);
    	if (cursor.getCount() == 0){
    		sql = "INSERT INTO InteractionGame (_UID,_LEVEL) VALUES ('"+history.uid+"',"+history.level+")";
    		db.execSQL(sql);
    	}
    	else{
    		sql =  "UPDATE InteractionGame SET _LEVEL = "+history.level	+" WHERE _UID ='"+history.uid+"'";
    		Log.d("update",sql);
    		db.execSQL(sql);
    	}
    	db.close();
    }
}
