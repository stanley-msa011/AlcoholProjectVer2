package data.database;

import java.util.Calendar;

import data.calculate.TimeBlock;
import data.info.AccAudioData;
import data.info.AudioData;
import data.info.DateValue;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AudioDB {
	private SQLiteOpenHelper dbHelper = null;
    private SQLiteDatabase db = null;
    
    public AudioDB(Context context){
    	dbHelper = new DBHelper(context);
    }
    
    public AccAudioData[] getNotUploadedInfo(){
    	db = dbHelper.getReadableDatabase();
    	String sql = "SELECT * FROM Record WHERE upload = 0";
    	Cursor cursor = db.rawQuery(sql, null);
    	int count = cursor.getCount();
    	if (count == 0){
    		cursor.close();
    		db.close();
    		return null;
    	}
    	
    	AccAudioData[] info = new AccAudioData[count];
    	int y_idx = cursor.getColumnIndex("year");
    	int m_idx = cursor.getColumnIndex("month");
    	int d_idx = cursor.getColumnIndex("day");
    	int f_idx = cursor.getColumnIndex("filename");
    	int t_idx = cursor.getColumnIndex("ts");
    	for (int i=0;i<count;++i){
    		cursor.moveToPosition(i);
    		int y = cursor.getInt(y_idx);
    		int m = cursor.getInt(m_idx);
    		int d = cursor.getInt(d_idx);
    		String f = cursor.getString(f_idx);
    		long t = cursor.getLong(t_idx);
    		int[] acc = new int[3];
    		int[] used = new int[3];
    		for (int j=0;j<3;++j){
    			acc[j] = cursor.getInt(7+j);
    			used[j] = cursor.getInt(10+j);
    		}
    		info[i] = new AccAudioData(y,m,d,t,f,acc,used);
    	}
    	cursor.close();
    	db.close();
    	
    	return info;
    }
    
    public void uploadedAudio(AudioData ai){
    	if (ai == null)
    		return;
    	db = dbHelper.getWritableDatabase();
    	
    	String sql = "update Record SET upload = 1 WHERE " +
    			"year= " +ai.year+
    			" AND month= " +ai.month+
    			" AND day= " + ai.date+
    			" AND filename=" + "'"+ai.filename+"'";
    	
    	db.execSQL(sql);
    	db.close();
    }
    
    public boolean insertAudio(DateValue dv){
    	boolean result = false;
    	if (dv == null)
    		return result;
    	db = dbHelper.getWritableDatabase();
    	String sql;
    	Cursor cursor;
    	sql = "SELECT * FROM Record ORDER BY ts DESC LIMIT 1";
    	cursor = db.rawQuery(sql,null);
    	int[] acc = new int[3];
    	int[] used = new int[3];
    	long prev_ts = 0;
    	if (cursor.moveToFirst()){
    		prev_ts = cursor.getLong(cursor.getColumnIndex("ts"));
    		for (int i=0;i<3;++i){
    			acc[i] = cursor.getInt(i+7);
    			used[i] = cursor.getInt(i+10);
    		}
    	}
    	cursor.close();
    	sql = "SELECT * FROM Record WHERE " +
    			"year= " +dv.year+
    			" AND month= " +dv.month+
    			" AND day= " + dv.date+
    			" AND filename=" + "'"+dv.toFileString()+"'";
    	cursor = db.rawQuery(sql, null);
    	long ts = System.currentTimeMillis();
    	
    	Calendar prev_c = Calendar.getInstance();
    	prev_c.setTimeInMillis(prev_ts);
    	int prev_year = prev_c.get(Calendar.YEAR);
    	int prev_month = prev_c.get(Calendar.MONTH);
    	int prev_day = prev_c.get(Calendar.DAY_OF_MONTH);
    	int prev_tb = TimeBlock.getTimeBlock(prev_c.get(Calendar.HOUR_OF_DAY));
    	
    	Calendar cur_c = Calendar.getInstance();
    	cur_c.setTimeInMillis(ts);
    	int cur_year = cur_c.get(Calendar.YEAR);
    	int cur_month = cur_c.get(Calendar.MONTH);
    	int cur_day = cur_c.get(Calendar.DAY_OF_MONTH);
    	int cur_tb = TimeBlock.getTimeBlock(cur_c.get(Calendar.HOUR_OF_DAY));
    	
    	if (prev_year != cur_year || prev_month != cur_month || prev_day!=cur_day || prev_tb!= cur_tb){
    		++acc[cur_tb];
    		result = true;
    	}
    	
    	if (cursor.getCount() > 0){
    		sql = "UPDATE Record SET upload = 0, ts = "+ts+" , " +
    			" acc1 = " +acc[0]+", "+
    			" acc2 = " +acc[1]+", "+
    			" acc3 = " +acc[2]+", "+
    			" used1 = " +used[0]+", "+
    			" used2 = " +used[1]+", "+
    			" used3 = " +used[2]+
    			" WHERE " +
    			"year= " +dv.year+
    			" AND month= " +dv.month+
    			" AND day= " + dv.date+
    			" AND filename=" + "'"+dv.toFileString()+"'";
    		db.execSQL(sql);
    		cursor.close();
    		db.close();
    		return result;
    	}
    	cursor.close();
    	
    	sql = "INSERT INTO Record (year,month,day,filename,ts,acc1,acc2,acc3,used1,used2,used3) VALUES (" +
    					dv.year+", " +
    					dv.month+	"," +
    					dv.date+"," +
    					"'"+dv.toFileString()+"',"+
    					ts+","+
    					acc[0]+","+acc[1]+","+acc[2]+","+used[0]+","+used[1]+","+used[2]+
    					")";
    	db.execSQL(sql);
    	db.close();
    	return result;
    }
    
    public boolean hasAudio(DateValue dv){
    	if (dv == null)
    		return false;
    	db = dbHelper.getReadableDatabase();
    	String sql = "SELECT * FROM Record WHERE " +
    			"year= " +dv.year+
    			" AND month= " +dv.month+
    			" AND day= " + dv.date+
    			" AND filename=" + "'"+dv.toFileString()+"'";
    	Cursor cursor = db.rawQuery(sql, null);
    	boolean result = cursor.moveToFirst();
    	cursor.close();
    	db.close();
    	return result;
    }
    
    
    public void restoreAudio(DateValue dv,long ts,int[] acc, int[] used){
    	if (dv == null)
    		return;
    	db = dbHelper.getWritableDatabase();
    	
    	String sql;
    	
    	sql = "INSERT INTO Record (year,month,day,filename,ts,acc1,acc2,acc3,used1,used2,used3) VALUES (" +
    					dv.year+", " +
    					dv.month+	"," +
    					dv.date+"," +
    					"'"+dv.toFileString()+"',"+
    					ts+","+
    					acc[0]+","+acc[1]+","+acc[2]+","+used[0]+","+used[1]+","+used[2]+
    					")";
    	db.execSQL(sql);
    	db.close();
    }
    
    public AccAudioData getLatestAudioData(){
    	db = dbHelper.getReadableDatabase();
    	String sql = "SELECT * FROM Record ORDER BY ts DESC LIMIT 1";
    	Cursor cursor = db.rawQuery(sql,null);
    	AccAudioData a_data = null;
    	if (cursor.moveToFirst()){
    		int[] acc = new int[3];
    		int[] used = new int[3];
    		for (int i=0;i<3;++i){
    			acc[i] = cursor.getInt(7+i);
    			used[i] = cursor.getInt(10+i);
    		}
    		a_data = new AccAudioData(cursor.getInt(1),cursor.getInt(2),cursor.getInt(3),cursor.getLong(6),cursor.getString(4),acc,used);
    	}else
    		a_data = new AccAudioData(0,0,0,0,"",null,null);
		return a_data;
    }
    
    public void cleanAcc(long ts){
    	DateValue dv = new DateValue(0,0,0);
    	db = dbHelper.getWritableDatabase();
    	String sql;
    	Cursor cursor;
    	sql = "SELECT * FROM Record ORDER BY ts DESC LIMIT 1";
    	cursor = db.rawQuery(sql,null);
    	int[] acc = new int[3];
    	int[] used = new int[3];
    	if (cursor.moveToFirst()){
    		for (int i=0;i<3;++i){
    			acc[i] = cursor.getInt(i+7);
    			used[i] = cursor.getInt(i+10);
    		}
    	}
    	cursor.close();
    	sql = "SELECT * FROM Record WHERE " +
    			"year= " +dv.year+
    			" AND month= " +dv.month+
    			" AND day= " + dv.date+
    			" AND filename=" + "'"+dv.toFileString()+"'";
    	cursor = db.rawQuery(sql, null);
    	
    	for (int i=0;i<3;++i)
    		used[i] = acc[i];
    	
    	if (cursor.getCount() > 0){
    		sql = "UPDATE Record SET upload = 0, ts = "+ts+" , " +
    			" acc1 = " +acc[0]+", "+
    			" acc2 = " +acc[1]+", "+
    			" acc3 = " +acc[2]+", "+
    			" used1 = " +used[0]+", "+
    			" used2 = " +used[1]+", "+
    			" used3 = " +used[2]+
    			" WHERE " +
    			"year= " +dv.year+
    			" AND month= " +dv.month+
    			" AND day= " + dv.date+
    			" AND filename=" + "'"+dv.toFileString()+"'";
    		db.execSQL(sql);
    		cursor.close();
    		db.close();
    		return;
    	}
    	cursor.close();
    	
    	sql = "INSERT INTO Record (year,month,day,filename,ts,acc1,acc2,acc3,used1,used2,used3) VALUES (" +
    					dv.year+", " +
    					dv.month+	"," +
    					dv.date+"," +
    					"'"+dv.toFileString()+"',"+
    					ts+","+
    					acc[0]+","+acc[1]+","+acc[2]+","+used[0]+","+used[1]+","+used[2]+
    					")";
    	db.execSQL(sql);
    	db.close();
    	return;
    }
}
