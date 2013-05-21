package database;

import java.util.Calendar;

import main.activities.FragmentTabs;

import test.data.BracDataHandler;

import history.BracGameHistory;
import history.DateBracGameHistory;
import history.InteractionHistory;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

public class HistoryDB {

	private static final int nBlocks = 4;
	private int timeblock_type;
	
	private SQLiteOpenHelper dbHelper = null;
    private SQLiteDatabase db = null;
	
	public HistoryDB(Context context){
		dbHelper = new DBHelper(context);
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		timeblock_type = sp.getInt("timeblock_num", 2);
	}
	
    public DateBracGameHistory getLatestBracGameHistory(){

    	db = dbHelper.getReadableDatabase();
    	Cursor cursor = db.rawQuery("SELECT MAX(_ID) FROM HistoryGame", null);
    	
    	cursor.moveToFirst();
    	int max_id = cursor.getInt(0);
    	cursor = db.rawQuery("SELECT * FROM HistoryGame WHERE _ID="+String.valueOf(max_id),null);

    	if (cursor.getCount()==0){
    		cursor.close();
    		db.close();
    		return new DateBracGameHistory(0,0,0,0,0);
    	}
    	
    	cursor.moveToFirst();
    	int level_idx = cursor.getColumnIndex("_LEVEL");
    	int ts_idx = cursor.getColumnIndex("_TS");
    	int brac_idx = cursor.getColumnIndex("_BRAC");
    	int emotion_idx = cursor.getColumnIndex("_EMOTION");
    	int desire_idx = cursor.getColumnIndex("_DESIRE");
    	
    	if (level_idx==-1||ts_idx==-1||brac_idx==-1){
    		Log.d("DATABASE","CANNOT FIND IDXs");
    		cursor.close();
    		db.close();
    		return new DateBracGameHistory(0,0,0,0,0);
    	}
    	int level = cursor.getInt(level_idx);
    	long ts = cursor.getLong(ts_idx);
    	float brac = cursor.getFloat(brac_idx);
    	int emotion = cursor.getInt(emotion_idx);
    	int desire = cursor.getInt(desire_idx);
    	cursor.close();
    	db.close();
    	return new DateBracGameHistory(level,ts,brac,emotion,desire);
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
    	timeblock = TimeBlock.getTimeBlock(hour,nBlocks);
    	
    	int emotion = history.emotion;
    	int desire = history.desire;
    	
    	String sql = "INSERT INTO HistoryGame (_LEVEL,_YEAR,_MONTH,_DATE,_TS,_TIMEBLOCK, _BRAC, _EMOTION, _DESIRE) VALUES ("
    							+level+","+year+","+month+","+date+","+ts+","+timeblock+","+brac+","+emotion+","+desire+")";
    	db.execSQL(sql);
    	db.close();
    }
    
    public BracGameHistory[] getTodayBracGameHistory(){

    	BracGameHistory[] historys = new BracGameHistory[nBlocks];
    	
    	Calendar cal = Calendar.getInstance();
    	int year = cal.get(Calendar.YEAR);
    	int month = cal.get(Calendar.MONTH)+1;
    	int date = cal.get(Calendar.DATE);
    	
    	db = dbHelper.getReadableDatabase();
    	
    	for (int i=0;i<nBlocks;++i){
    		if (!TimeBlock.hasBlock(i, timeblock_type))
    			continue;
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
    		historys[i] = new BracGameHistory(0,0,brac,0,0);
    		cursor.close();
    	}
    	db.close();
    	return historys;
    }
    
    public int getAllBracGameScore(){

    	int MIN = 0;
    	int MAX = Integer.MAX_VALUE;
    	int MISS = 0;
    	int PASS = 1;
    	int FAIL = 0;
    	
    	int score = 0;
    	db = dbHelper.getReadableDatabase();
    	
    	String sql = "SELECT * FROM HistoryGame WHERE _TIMEBLOCK <> -1 ORDER BY _ID ASC";
    	Cursor cursor = db.rawQuery(sql, null);
    	
    	if (cursor.getCount()==0){
    		cursor.close();
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
    	cursor.close();
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
    	
    	BracGameHistory[] historys = new BracGameHistory[n_days*nBlocks];
    	
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
    		int block = i%nBlocks;
    		if (!TimeBlock.hasBlock(block, timeblock_type))
    			continue;
    		
    		cursor_pointer = 0;
    		while (cursor_pointer <_size){
    			cursor.moveToPosition(cursor_pointer);
    			_ts = cursor.getLong(ts_idx);
    			if (_ts >= ts_from && _ts < ts_to){// match date
    				_tb = cursor.getInt(tb_idx);
    				if (_tb ==i%nBlocks){
    					_brac = cursor.getFloat(brac_idx);
    					historys[i] = new BracGameHistory(0,_ts,_brac,0,0);
    					++cursor_pointer;
    					break;
    				}
    			}
    			++cursor_pointer;
    		}
    		
    		if (i%nBlocks==nBlocks-1){
    			ts_from+=3600*24;
    			ts_to+=3600*24;
    		}
    	}
    	cursor.close();
    	db.close();
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
    	cursor.close();
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
    	if (count == 0){
    		cursor.close();
    		db.close();
    		return null;
    	}
    	long[] ts = new long[count];
    	int ts_idx = cursor.getColumnIndex("_TS");
    	for (int i=0;i<count;++i){
    		cursor.moveToPosition(i);
    		ts[i] =cursor.getLong(ts_idx); 
    	}
    	cursor.close();
    	db.close();
    	return ts;
    }
    
    public InteractionHistory[] getAllUsersHistory(){
    	InteractionHistory[] historys = null;
    	db = dbHelper.getReadableDatabase();
    	String sql = "SELECT * FROM InteractionGame ORDER BY _LEVEL DESC,  _UID ASC";
    	Cursor cursor = db.rawQuery(sql, null);
    	int count = cursor.getCount();
    	if (count == 0){
    		cursor.close();
    		db.close();
    		return null;
    	}
    	historys = new InteractionHistory[count];
    	int uid_idx = cursor.getColumnIndex("_UID");
    	int level_idx = cursor.getColumnIndex("_LEVEL");
    	for (int i=0;i<count;++i){
    		cursor.moveToPosition(i);
    		String uid = cursor.getString(uid_idx);
    		int level = cursor.getInt(level_idx);
    		historys[i] = new InteractionHistory(level,uid);
    	}
    	cursor.close();
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
    	cursor.close();
    	db.close();
    }
    
    public Calendar getFirstTestDate(){
    	db = dbHelper.getReadableDatabase();
    	String sql = "SELECT * FROM HistoryGame ORDER BY  _ID ASC";
    	Cursor cursor = db.rawQuery(sql, null);
    	int count = cursor.getCount();
    	if (count == 0){
    		cursor.close();
    		db.close();
    		return null;
    	}
    	cursor.moveToFirst();
    	int ts_idx = cursor.getColumnIndex("_TS");
    	long ts = cursor.getLong(ts_idx)*1000L;
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(ts);
    	cursor.close();
    	db.close();
		return cal;
    }
    
    public boolean getIsDone(Calendar curCal){
    	db = dbHelper.getReadableDatabase();
    	
    	int year = curCal.get(Calendar.YEAR);
    	int month = curCal.get(Calendar.MONTH)+1;
    	int date = curCal.get(Calendar.DATE);
    	int hour = curCal.get(Calendar.HOUR_OF_DAY);
    	int time_block = TimeBlock.getTimeBlock(hour,timeblock_type);
    	
    	if (!TimeBlock.isBlock(hour, timeblock_type)){
    		db.close();
    		return true;
    	}
    	String sql = "SELECT * FROM HistoryGame WHERE _YEAR ="+year
    							+" AND _MONTH = "+month
    							+" AND _DATE= "+date
    							+" AND _TIMEBLOCK= "+time_block;
    	Cursor cursor = db.rawQuery(sql , null);
    	int len = cursor.getCount();
    	
    	boolean result = false;
    	Log.d("isDone", String.valueOf(len));
    	if (len > 0){
    		result = true;
    	}
    	cursor.close();
    	db.close();
		return result;
    }
    
}
