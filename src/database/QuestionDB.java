package database;

import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class QuestionDB {

	
	private SQLiteOpenHelper dbHelper = null;
    private SQLiteDatabase db = null;
	
	public QuestionDB(Context context){
		dbHelper = new DBHelper(context);
	}
	
	public void insertEmotion(int emotion){
		Calendar cal = Calendar.getInstance();
		long ts = cal.getTimeInMillis();
		
		db = dbHelper.getWritableDatabase();
		String sql = "INSERT INTO EmotionDB (_TS,_EMOTION) VALUES ("+ts+","+emotion+")";
		db.execSQL(sql);
		db.close();
	}
	
	public void insertEmotionManage(int emotion, int type, String reason){
		Calendar cal = Calendar.getInstance();
		long ts = cal.getTimeInMillis();
		int len = 255;
		if (reason.length()<255)
			len = reason.length();
		String str = String.copyValueOf(reason.toCharArray(), 0, len);
		
		db = dbHelper.getWritableDatabase();
		String sql = "INSERT INTO EmotionManageDB (_TS,_EMOTION,_TYPE,_REASON) VALUES ("+ts+","+emotion+","+type+",'"+str+"')";
		db.execSQL(sql);
		db.close();
	}
	
    public String[] getInsertedReason(int type){
    	db = dbHelper.getReadableDatabase();
    	String sql = "SELECT _REASON FROM EmotionManageDB WHERE _TYPE = "+type+" ORDER BY _TS DESC LIMIT 5";
    	String[] out = null;
    	
    	Cursor cursor = db.rawQuery(sql, null);
    	if (cursor.getCount() == 0){
    		cursor.close();
        	db.close();
    		return null;
    	}
    	out = new String[cursor.getCount()];
    	int idx = cursor.getColumnIndex("_REASON");
    	Log.d("QuestionDB","idx: "+idx);
    	
    	for (int i=0;i<out.length;++i){
    		if (cursor.moveToPosition(i)){
    			out[i] = cursor.getString(idx);
    		}
    	}
    	int exist = 0;
    	for (int i=0;i<out.length;++i){
    		if (out[i].length() > 0){
    			++exist;
    		}
    	}
    	String[] out2 = new String[exist];
    	int c = 0;
    	for (int i=0;i<out.length;++i){
    		if (out[i].length() >0){
    			out2[c] = out[i];
    			++c;
    		}
    	}
    	
    	
    	cursor.close();
    	db.close();
    	return out2;
    }
}
