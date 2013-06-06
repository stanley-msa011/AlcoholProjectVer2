package database;

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
    
    public void InsertAudio(DateValue dv){
    	if (dv == null)
    		return;
    	db = dbHelper.getWritableDatabase();
    	String sql = "INSERT INTO RecDB (_YEAR,_MONTH,_DATE,_FILENAME) VALUES (" +
    					dv.year+", " +
    					dv.month+	"," +
    					dv.date+"," +
    					"'"+dv.toFileString()+"'"+")";
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
