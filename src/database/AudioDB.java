package database;

import java.util.Calendar;

import history.data.AudioInfo;
import history.ui.DateValue;
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
    
    public AudioInfo[] getNotUploadedInfo(){
    	db = dbHelper.getReadableDatabase();
    	String sql = "SELECT * FROM RecDB WHERE _UPLOAD = 0";
    	Cursor cursor = db.rawQuery(sql, null);
    	int count = cursor.getCount();
    	if (count == 0){
    		cursor.close();
    		db.close();
    		return null;
    	}
    	
    	AudioInfo[] info = new AudioInfo[count];
    	int y_idx = cursor.getColumnIndex("_YEAR");
    	int m_idx = cursor.getColumnIndex("_MONTH");
    	int d_idx = cursor.getColumnIndex("_DATE");
    	int f_idx = cursor.getColumnIndex("_FILENAME");
    	int t_idx = cursor.getColumnIndex("_TS");
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
    	
    	String sql = "UPDATE RecDB SET _UPLOAD = 1 WHERE " +
    			"_YEAR= " +ai.year+
    			" AND _MONTH= " +ai.month+
    			" AND _DATE= " + ai.date+
    			" AND _FILENAME=" + "'"+ai.filename+"'";
    	
    	db.execSQL(sql);
    	db.close();
    }
    
    public void InsertAudio(DateValue dv){
    	if (dv == null)
    		return;
    	db = dbHelper.getWritableDatabase();
    	
    	String sql = "SELECT * FROM RecDB WHERE " +
    			"_YEAR= " +dv.year+
    			" AND _MONTH= " +dv.month+
    			" AND _DATE= " + dv.date+
    			" AND _FILENAME=" + "'"+dv.toFileString()+"'";
    	Cursor cursor = db.rawQuery(sql, null);
    	long ts = Calendar.getInstance().getTimeInMillis();
    	if (cursor.getCount() > 0){
    		sql = "UPDATE RecDB SET _UPLOAD = 0, _TS = "+ts+"WHERE " +
    			"_YEAR= " +dv.year+
    			" AND _MONTH= " +dv.month+
    			" AND _DATE= " + dv.date+
    			" AND _FILENAME=" + "'"+dv.toFileString()+"'";
    		cursor.close();
    		db.close();
    		return;
    	}
    	
    	
    	sql = "INSERT INTO RecDB (_YEAR,_MONTH,_DATE,_FILENAME,_TS) VALUES (" +
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
    	String sql = "SELECT * FROM RecDB WHERE " +
    			"_YEAR= " +dv.year+
    			" AND _MONTH= " +dv.month+
    			" AND _DATE= " + dv.date+
    			" AND _FILENAME=" + "'"+dv.toFileString()+"'";
    	Cursor cursor = db.rawQuery(sql, null);
    	boolean result = false;
    	if (cursor.getCount()>0)
    		result = true;
    	cursor.close();
    	db.close();
    	return result;
    }
}
