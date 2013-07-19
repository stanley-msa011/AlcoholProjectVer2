package database;

import java.util.Calendar;

import data.record.AudioInfo;

import history.ui.DateValue;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AudioDB {
	private SQLiteOpenHelper dbHelper = null;
    private SQLiteDatabase db = null;
    
    public AudioDB(Context context){
    	dbHelper = new DBHelper(context);
    }
    
    public AudioInfo[] getNotUploadedInfo(){
    	db = dbHelper.getReadableDatabase();
    	String sql = "SELECT * FROM Record WHERE upload = 0";
    	Cursor cursor = db.rawQuery(sql, null);
    	int count = cursor.getCount();
    	if (count == 0){
    		cursor.close();
    		db.close();
    		return null;
    	}
    	
    	AudioInfo[] info = new AudioInfo[count];
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
    		info[i] = new AudioInfo(y,m,d,t,f);
    	}
    	cursor.close();
    	db.close();
    	
    	return info;
    }
    
    public void uploadedAudio(AudioInfo ai){
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
    
    public void insertAudio(DateValue dv){
    	if (dv == null)
    		return;
    	db = dbHelper.getWritableDatabase();
    	
    	String sql = "SELECT * FROM Record WHERE " +
    			"year= " +dv.year+
    			" AND month= " +dv.month+
    			" AND day= " + dv.date+
    			" AND filename=" + "'"+dv.toFileString()+"'";
    	Cursor cursor = db.rawQuery(sql, null);
    	long ts = Calendar.getInstance().getTimeInMillis();
    	if (cursor.getCount() > 0){
    		Log.d("AUDIO","update");
    		sql = "UPDATE Record SET upload = 0, ts = "+ts+"WHERE " +
    			"year= " +dv.year+
    			" AND month= " +dv.month+
    			" AND day= " + dv.date+
    			" AND filename=" + "'"+dv.toFileString()+"'";
    		cursor.close();
    		db.close();
    		return;
    	}
    	
    	
    	sql = "INSERT INTO Record (year,month,day,filename,ts) VALUES (" +
    					dv.year+", " +
    					dv.month+	"," +
    					dv.date+"," +
    					"'"+dv.toFileString()+"',"+
    					ts+")";
    	db.execSQL(sql);
    	db.close();
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
    	boolean result = false;
    	if (cursor.getCount()>0)
    		result = true;
    	cursor.close();
    	db.close();
    	return result;
    }
}
