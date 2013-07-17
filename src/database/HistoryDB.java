package database;

import java.util.Calendar;

import test.data.BracDataHandler;

import data.history.AccumulatedHistoryState;
import data.history.BracDetectionState;
import data.history.DateBracDetectionState;
import data.history.UsedDetection;
import data.rank.RankHistory;


import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

public class HistoryDB {

	
	private SQLiteOpenHelper dbHelper = null;
    private SQLiteDatabase db = null;
	
    private Calendar start_date;
    
    private static final int nBlocks = 3;
    private Context context;
    
	public HistoryDB(Context context){
		dbHelper = new DBHelper(context);
		this.context = context;
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		start_date = Calendar.getInstance();
		int year = sp.getInt("sYear", start_date.get(Calendar.YEAR));
		int month = sp.getInt("sMonth", start_date.get(Calendar.MONTH));
		int date = sp.getInt("sDay", Calendar.DAY_OF_MONTH);
		start_date.set(year, month, date, 0, 0,0);
		start_date.set(Calendar.MILLISECOND, 0);
	}
	
	public DateBracDetectionState[] getAllHistory(){
    	
    	String sql = "SELECT brac, ts, year, month, day, timeblock, week, emotion, desire FROM Detection ORDER BY id ASC";
    	
    	db = dbHelper.getReadableDatabase();
    	Cursor cursor = db.rawQuery(sql, null);
    	int num = cursor.getCount();
    	if (num == 0){
    		cursor.close();
    		db.close();
    		return null;
    	}
    	
    	DateBracDetectionState[] historys = new DateBracDetectionState[num];
    	int brac_idx = cursor.getColumnIndex("brac");
    	int ts_idx = cursor.getColumnIndex("ts");
    	int y_idx = cursor.getColumnIndex("year");
    	int m_idx = cursor.getColumnIndex("month");
    	int d_idx = cursor.getColumnIndex("day");
    	int t_idx = cursor.getColumnIndex("timeblock");
    	int w_idx = cursor.getColumnIndex("week");
    	int e_idx = cursor.getColumnIndex("emotion");
    	int de_idx = cursor.getColumnIndex("desire");
    	for (int i=0;i<historys.length;++i){
    		cursor.moveToPosition(i);
    		float brac = cursor.getFloat(brac_idx);
    		long ts = cursor.getLong(ts_idx);
    		int week = cursor.getInt(w_idx);
    		int year = cursor.getInt(y_idx);
    		int month = cursor.getInt(m_idx);
    		int day = cursor.getInt(d_idx);
    		int tb = cursor.getInt(t_idx);
    		int emotion = cursor.getInt(e_idx);
    		int desire = cursor.getInt(de_idx);
    		historys[i] = new DateBracDetectionState(week, ts, year,month,day,tb,brac, emotion, desire);
    	}
    	cursor.close();
    	db.close();
    	return historys;
	}
	
    public DateBracDetectionState getLatestBracDetection(){

    	db = dbHelper.getReadableDatabase();
    	Cursor cursor = db.rawQuery("SELECT brac, ts, year, month, day, timeblock, week, emotion, desire FROM Detection ORDER BY id DESC LIMIT 1",null);

    	if (cursor.getCount()==0){
    		cursor.close();
    		db.close();
    		return new DateBracDetectionState(0,0,0,0,0,0,0,0,0);
    	}
    	
    	int brac_idx = cursor.getColumnIndex("brac");
    	int ts_idx = cursor.getColumnIndex("ts");
    	int y_idx = cursor.getColumnIndex("year");
    	int m_idx = cursor.getColumnIndex("month");
    	int d_idx = cursor.getColumnIndex("day");
    	int t_idx = cursor.getColumnIndex("timeblock");
    	int w_idx = cursor.getColumnIndex("week");
    	int e_idx = cursor.getColumnIndex("emotion");
    	int de_idx = cursor.getColumnIndex("desire");
    	
    	cursor.moveToFirst();
    	
		float brac = cursor.getFloat(brac_idx);
		long ts = cursor.getLong(ts_idx);
		int week = cursor.getInt(w_idx);
		int year = cursor.getInt(y_idx);
		int month = cursor.getInt(m_idx);
		int day = cursor.getInt(d_idx);
		int tb = cursor.getInt(t_idx);
		int emotion = cursor.getInt(e_idx);
		int desire = cursor.getInt(de_idx);
    	cursor.close();
    	db.close();
    	return new DateBracDetectionState(week,ts, year,month,day,tb,brac, emotion, desire);
    }
    
    public AccumulatedHistoryState getLatestAccumulatedHistoryState(){
    	db = dbHelper.getReadableDatabase();
    	Cursor cursor = db.rawQuery("SELECT " +
    			" a.w_morning,a.w_noon,a.w_night," +
    			" a.morning,a.noon,a.night," +
    			" a.w_morning_pass, a.w_noon_pass, a.w_night_pass," +
    			" a.morning_pass,a.noon_pass, a.night_pass, d.week " +
    			" FROM AccDetection AS a, Detection AS d WHERE a.detection_id = d.id ORDER BY a.id DESC LIMIT 1",null);

    	if (cursor.getCount()==0){
    		cursor.close();
    		db.close();
    		return new AccumulatedHistoryState(0,null,null,null,null);
    	}
    	
    	cursor.moveToFirst();
    	int[] accTest = new int[3];
    	int[] accPass = new int[3];
    	int[] w_accTest = new int[3];
    	int[] w_accPass = new int[3];
    	
    	for (int i=0;i<3;++i){
    		w_accTest[i] = cursor.getInt(i);
    		accTest[i] = cursor.getInt(i+3);
    		w_accPass[i] = cursor.getInt(i+6);
    		accPass[i] = cursor.getInt(i+9);
    	}
    	
    	int week = cursor.getInt(12);
    	
    	cursor.close();
    	db.close();
    	AccumulatedHistoryState a = new AccumulatedHistoryState(week,w_accTest,w_accPass,accTest,accPass);
    	return a;
    }
    
    public AccumulatedHistoryState[] getAccumulatedHistoryStateByWeek(){
    	db = dbHelper.getReadableDatabase();
    	int curWeek = WeekNum.getWeek(context, Calendar.getInstance().getTimeInMillis());
    	
    	AccumulatedHistoryState[] historys;
    	historys = new AccumulatedHistoryState[curWeek + 1];
    	
    	String sql = "SELECT " +
    			" a.w_morning,a.w_noon,a.w_night," +
    			" a.w_morning_pass,a.w_noon_pass,a.w_night_pass," +
    			" d.week " +
    			" FROM AccDetection AS a, Detection AS d WHERE a.detection_id = d.id AND week <=" +curWeek+
    			" GROUP BY d.week";
    	
    	Cursor cursor = db.rawQuery(sql,null);
    	
    	int count = cursor.getCount();
    	
    	int cursor_pos = 0;
    	
    	int[] w_accTest = new int[3];
    	int[] w_accPass = new int[3];
    	int week;
    	
    	for (int i=0;i<historys.length;++i){
    		while (cursor_pos < count){
    			cursor.moveToPosition(cursor_pos);
    			week = cursor.getInt(6);
    			if (week < i){
    				++cursor_pos;
    				continue;
    			}else if (week > i)
    				break;
    			for (int j=0;j<3;++j){
        			w_accTest[j] = cursor.getInt(j);
        			w_accPass[j] = cursor.getInt(j+3);
        		}
    			//Log.d("AccHistory",week+">>"+w_accTest[0]+"/"+w_accTest[1]+"/"+w_accTest[2]+"  "+w_accPass[0]+"/"+w_accPass[1]+"/"+w_accPass[2]);
    			historys[i] = new AccumulatedHistoryState(week,w_accTest,w_accPass,null,null);
    			break;
    		}
    	}
    	
    	for (int i=0;i<historys.length;++i)
    		if (historys[i] == null)
    			historys[i] = new AccumulatedHistoryState(i,null,null,null,null);
    	
    	cursor.close();
    	db.close();
    	return historys;
    }
    
    
    public UsedDetection getLatestUsedState(){
    	
    	db = dbHelper.getReadableDatabase();
    	String sql = "SELECT * FROM UsedDetection ORDER BY id DESC LIMIT 1";
    	Cursor cursor = db.rawQuery(sql, null);
    	if (cursor.getCount() == 0){
    		cursor.close();
    		db.close();
    		return new UsedDetection(null,null);
    	}
    	
    	cursor.moveToFirst();
    	int[] test = new int[3];
    	int[] pass = new int[3];
    	for (int i=0;i<3;++i){
    		test[i] = cursor.getInt(i+2);
    		pass[i] = cursor.getInt(i+5);
    	}
    	
    	cursor.close();
		db.close();
		return new UsedDetection(test,pass);
    	
    }
    
    
    public void insertNewState(DateBracDetectionState state, AccumulatedHistoryState a_state){
    	
    	db = dbHelper.getWritableDatabase();
    	
    	int[] accTest = a_state.acc_test;
    	int[] accPass = a_state.acc_pass;
    	int[] t_accTest = a_state.total_acc_test;
    	int[] t_accPass = a_state.total_acc_pass;
    	long ts = state.timestamp;
    	float brac = state.brac;
    	int year = state.year;
    	int month = state.month;
    	int day =state.day;
    	int week = state.week;
    	int timeblock = state.timeblock;
    	int emotion = state.emotion;
    	int desire = state.desire;
    	
    	String sql = "INSERT INTO Detection (year,month,day,week,ts,timeblock,brac, emotion, desire) VALUES (" +
    			year+","+month+","+day+","+week+","+ts+","+timeblock+","+brac+","+emotion+","+desire+")";
    	db.execSQL(sql);
    	
    	int did = 0;
    	sql = "SELECT MAX(id) FROM Detection";
    	Cursor cursor = db.rawQuery(sql, null);
    	if (cursor.moveToFirst())
    		did = cursor.getInt(0);
    	cursor.close();
    	
    	sql = "INSERT INTO AccDetection (" +
    			" detection_id," +
    			" w_morning,w_noon,w_night," +
    			" morning,noon,night," +
    			" w_morning_pass,w_noon_pass,w_night_pass," +
    			" morning_pass,noon_pass,night_pass" +
    			") VALUES (" +
    			did+","+
    			accTest[0]+","+accTest[1]+","+accTest[2]+","+
    			t_accTest[0]+","+t_accTest[1]+","+t_accTest[2]+","+
    			accPass[0]+","+accPass[1]+","+accPass[2]+","+
    			t_accPass[0]+","+t_accPass[1]+","+t_accPass[2]+
    			")";
    	
    	db.execSQL(sql);
    	
    	db.close();
    }
    
    public BracDetectionState[] getTodayBracState(){

    	BracDetectionState[] states = new BracDetectionState[nBlocks];
    	
    	Calendar cal = Calendar.getInstance();
    	int year = cal.get(Calendar.YEAR);
    	int month = cal.get(Calendar.MONTH);
    	int day = cal.get(Calendar.DATE);
    	
    	db = dbHelper.getReadableDatabase();
    	
    	for (int i=0;i<nBlocks;++i){
    		if (!TimeBlock.hasBlock(i))
    			continue;
    		String sql = "SELECT brac FROM Detection WHERE "
    				+" year="+year
    				+" AND month="+month
    				+" AND day="+day
    				+" AND timeblock="+i
    				+" ORDER BY id ASC LIMIT 1";
    		Cursor cursor = db.rawQuery(sql, null);
    		
    		if (cursor.getCount()==0){
        		states[i]=null;
        		continue;
        	}
    		cursor.moveToFirst();
    		float brac = cursor.getFloat(0);
    		states[i] = new BracDetectionState(0,0,brac,0,0);
    		cursor.close();
    	}
    	db.close();
    	return states;
    }
    
    public int getAllBracDetectionScore(){
    	int score = 0;
    	db = dbHelper.getReadableDatabase();
    	
    	String sql = "SELECT * FROM Detection WHERE id GROUP BY year,month,day,timeblock ORDER BY id ASC";
    	Cursor cursor = db.rawQuery(sql, null);

    	int num = cursor.getCount();
    	
    	if (num==0){
    		cursor.close();
    		db.close();
    		return score;
    	}
    	
    	int brac_idx = cursor.getColumnIndex("brac");
    	
    	for (int i=0;i<num;++i){
    		cursor.moveToPosition(i);
    		float brac = cursor.getFloat(brac_idx);
        	if (brac < BracDataHandler.THRESHOLD)
        		score+=1;
    	}
    	
    	cursor.close();
    	db.close();
    	return score;
    }
	
    public BracDetectionState[] getMultiDayInfo(int n_days){
    	
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MILLISECOND, 0);
    	final long DAY = AlarmManager.INTERVAL_DAY;
    	long ts_days = (long)(n_days-1)*DAY;
    	long start_ts = cal.getTimeInMillis() - ts_days;
    	
    	String sql = "SELECT * FROM Detection WHERE ts >= "+start_ts+" GROUP BY year,month,day,timeblock ORDER BY id ASC";
    	
    	db = dbHelper.getReadableDatabase();
    	Cursor cursor = db.rawQuery(sql, null);

    	BracDetectionState[] historys = new BracDetectionState[n_days*nBlocks];
    	
    	int brac_idx = cursor.getColumnIndex("brac");
    	int ts_idx = cursor.getColumnIndex("ts");
    	int tb_idx = cursor.getColumnIndex("timeblock");
    	
    	long ts_from = start_ts;
    	long ts_to = start_ts+DAY;
    	long ts;
    	int tb;
    	float brac;
    	int cursor_count = cursor.getCount();
    	
    	int cursor_pointer = 0;
    	
    	for (int i=0;i<historys.length;++i){
    		int block = i%nBlocks;
    		
    		while (cursor_pointer < cursor_count){
    			cursor.moveToPosition(cursor_pointer);
    			ts = cursor.getLong(ts_idx);
    			if (ts < ts_from){ 
    				++cursor_pointer;
    				continue;
    			}else if (ts >= ts_to){
    				break;
    			}
    			// match date
    			tb = cursor.getInt(tb_idx);
    			if (tb > block)
    				break;
    			else if (tb < block){
    				++cursor_pointer;
    				continue;
    			}
    			//match time block
    			brac = cursor.getFloat(brac_idx);
    			historys[i] = new BracDetectionState(0,ts,brac,0,0);
    			break;
    		}
    		if (cursor_pointer == cursor_count)
    			break;
    		
    		if (block == nBlocks-1){//next day
    			ts_from+=DAY;
    			ts_to+=DAY;
    		}
    	}
    	cursor.close();
    	db.close();
    	return historys;
    }
      
    public void updateDetectionUploaded(long ts){
    	db = dbHelper.getWritableDatabase();
    	String sql = "UPDATE Detection SET upload = 1 WHERE ts="+ts;
    	db.execSQL( sql);
    	db.close();
    }
    
    public void updateAllDetectionUploaded(){//Used for dummy
    	db = dbHelper.getWritableDatabase();
    	String sql = "UPDATE Detection SET upload = 1 WHERE id >= 0";
    	db.execSQL( sql);
    	db.close();
    }
    
    public BracDetectionState[] getAllNotUploadedDetection(){
    	db = dbHelper.getReadableDatabase();
    	String sql = "SELECT id,week, ts, brac, emotion, desire FROM Detection WHERE upload = 0 ORDER BY id ASC";
    	Cursor cursor = db.rawQuery(sql, null);
    	int count = cursor.getCount();
    	if (count == 0){
    		cursor.close();
    		db.close();
    		return null;
    	}
    	BracDetectionState[] states = new BracDetectionState[count];
    	int w_idx = cursor.getColumnIndex("week");
    	int ts_idx = cursor.getColumnIndex("ts");
    	int b_idx = cursor.getColumnIndex("brac");
    	int e_idx = cursor.getColumnIndex("emotion");
    	int d_idx = cursor.getColumnIndex("desire");
    	for (int i=0;i<count;++i){
    		cursor.moveToPosition(i);
    		int week = cursor.getInt(w_idx);
    		long ts = cursor.getLong(ts_idx);
    		float brac = cursor.getFloat(b_idx);
    		int emotion = cursor.getInt(e_idx);
    		int desire = cursor.getInt(d_idx);
    		states[i] =  new BracDetectionState(week,ts,brac,emotion,desire);
    	}
    	cursor.close();
    	db.close();
    	return states;
    }
    
    public RankHistory[] getAllUsersHistory(){
    	RankHistory[] historys = null;
    	db = dbHelper.getReadableDatabase();
    	String sql = "SELECT * FROM Ranking ORDER BY score DESC,  user_id ASC";
    	Cursor cursor = db.rawQuery(sql, null);
    	int count = cursor.getCount();
    	if (count == 0){
    		cursor.close();
    		db.close();
    		return null;
    	}
    	historys = new RankHistory[count];
    	int uid_idx = cursor.getColumnIndex("user_id");
    	int level_idx = cursor.getColumnIndex("score");
    	for (int i=0;i<count;++i){
    		cursor.moveToPosition(i);
    		String uid = cursor.getString(uid_idx);
    		int score = cursor.getInt(level_idx);
    		historys[i] = new RankHistory(score,uid);
    	}
    	cursor.close();
    	db.close();
    	return historys;
    }
    
    public void insertInteractionHistory(RankHistory history){
    	db = dbHelper.getWritableDatabase();
    	String sql = "SELECT * FROM Ranking WHERE user_id = '"+history.uid+"'";
    	Cursor cursor = db.rawQuery(sql, null);
    	if (cursor.getCount() == 0){
    		sql = "INSERT INTO Ranking (user_id,score) VALUES ('"+history.uid+"',"+history.score+")";
    		db.execSQL(sql);
    	}
    	else{
    		sql =  "UPDATE Ranking SET score = "+history.score	+" WHERE user_id ='"+history.uid+"'";
    		db.execSQL(sql);
    	}
    	cursor.close();
    	db.close();
    }
    
    public void cleanInteractionHistory(){
    	db = dbHelper.getWritableDatabase();
    	String sql = "DELETE FROM Ranking";
    	db.execSQL(sql);
    	db.close();
    }
    
    public boolean getIsDone(Calendar curCal){
    	db = dbHelper.getReadableDatabase();
    	
    	int year = curCal.get(Calendar.YEAR);
    	int month = curCal.get(Calendar.MONTH);
    	int day = curCal.get(Calendar.DATE);
    	int hour = curCal.get(Calendar.HOUR_OF_DAY);
    	int time_block = TimeBlock.getTimeBlock(hour);
    	
    	String sql = "SELECT id FROM Detection WHERE year ="+year
    							+" AND month = "+month
    							+" AND day= "+day
    							+" AND timeblock= "+time_block;
    	Cursor cursor = db.rawQuery(sql , null);
    	int len = cursor.getCount();
    	boolean result =( len > 0);
    	cursor.close();
    	db.close();
		return result;
    }
    
    public void cleanAcc(){
    	db = dbHelper.getWritableDatabase();
    	String sql = "SELECT morning,noon,night,morning_pass,noon_pass,night_pass FROM AccDetection ORDER BY id DESC LIMIT 1";
    	Cursor cursor = db.rawQuery(sql, null);
    	int count = cursor.getCount();
    	if (count == 0){
    		cursor.close();
    		db.close();
    		return;
    	}
    	
    	cursor.moveToFirst();
    	int t_morning = cursor.getInt(0);
    	int t_noon = cursor.getInt(1);
    	int t_night = cursor.getInt(2);
    	int p_morning = cursor.getInt(3);
    	int p_noon = cursor.getInt(4);
    	int p_night = cursor.getInt(5);
    	long ts = System.currentTimeMillis();
    	sql = "INSERT INTO UsedDetection (morning,noon,night,morning_pass,noon_pass,night_pass,ts) VALUES (" +
    			t_morning+","+t_noon+","+t_night+","+p_morning+","+p_noon+","+p_night+","+ts+
    			")";
    	db.execSQL(sql);
    	cursor.close();
    	db.close();
    }
    
}
