package database;

import java.util.Calendar;

import questionnaire.data.EmotionData;
import questionnaire.data.EmotionManageData;
import questionnaire.data.QuestionnaireData;

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
	
	public void insertQuestionnaire(String seq){
		Calendar cal = Calendar.getInstance();
		long ts = cal.getTimeInMillis();
		db = dbHelper.getWritableDatabase();
		Log.d("QuestionnaireDB","insert: " +seq);
		String sql = "INSERT INTO QuestionnaireDB (_TS,_SEQUENCE,_UPLOAD) VALUES ("+ts+",'"+seq+"', 0 )";
		db.execSQL(sql);
		db.close();
	}
	
	public QuestionnaireData[] getNotUploadedQuestionnaire(){
		QuestionnaireData[] data = null;
		db =dbHelper.getReadableDatabase();
		String sql = "SELECT * FROM QuestionnaireDB WHERE _UPLOAD = 0";
		Cursor cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0){
			cursor.close();
			db.close();
			return null;
		}
		data = new QuestionnaireData[count];
		int ts_idx = cursor.getColumnIndex("_TS");
		int s_idx = cursor.getColumnIndex("_SEQUENCE");
		long ts;
		String seq;
		for (int i=0;i<count;++i){
			cursor.moveToPosition(i);
			ts = cursor.getLong(ts_idx);
			seq = cursor.getString(s_idx);
			data[i] = new QuestionnaireData(ts,seq);
		}
		
		cursor.close();
		db.close();
		
		return data;
	}
	
	public void setQuestionnaireUploaded(long ts){
		db = dbHelper.getWritableDatabase();
		String sql = "UPDATE QuestionnaireDB SET _UPLOAD = 1 WHERE _TS = "+ts;
		db.execSQL(sql);
		db.close();
	}
	
	
	public void insertEmotion(int emotion){
		Calendar cal = Calendar.getInstance();
		long ts = cal.getTimeInMillis();
		
		db = dbHelper.getWritableDatabase();
		String sql = "INSERT INTO EmotionDB (_TS,_EMOTION,_UPLOAD) VALUES ("+ts+","+emotion+", 0 )";
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
		String sql = "INSERT INTO EmotionManageDB (_TS,_EMOTION,_TYPE,_REASON,_UPLOAD) VALUES ("+ts+","+emotion+","+type+",'"+str+"' , 0 )";
		db.execSQL(sql);
		db.close();
	}
	
	public EmotionData[] getNotUploadedEmotion(){
		EmotionData[] data = null;
		db =dbHelper.getReadableDatabase();
		String sql = "SELECT * FROM EmotionDB WHERE _UPLOAD = 0";
		Cursor cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0){
			cursor.close();
			db.close();
			return null;
		}
		data = new EmotionData[count];
		int ts_idx = cursor.getColumnIndex("_TS");
		int e_idx = cursor.getColumnIndex("_EMOTION");
		long ts;
		int emotion;
		for (int i=0;i<count;++i){
			cursor.moveToPosition(i);
			ts = cursor.getLong(ts_idx);
			emotion = cursor.getInt(e_idx);
			data[i] = new EmotionData(ts,emotion);
		}
		
		cursor.close();
		db.close();
		
		return data;
	}
	
	public void setEmotionUploaded(long ts){
		db = dbHelper.getWritableDatabase();
		String sql = "UPDATE EmotionDB SET _UPLOAD = 1 WHERE _TS = "+ts;
		db.execSQL(sql);
		db.close();
	}
	
	public EmotionManageData[] getNotUploadedEmotionManage(){
		EmotionManageData[] data = null;
		db =dbHelper.getReadableDatabase();
		String sql = "SELECT * FROM EmotionManageDB WHERE _UPLOAD = 0";
		Cursor cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0){
			cursor.close();
			db.close();
			return null;
		}
		data = new EmotionManageData[count];
		int ts_idx = cursor.getColumnIndex("_TS");
		int e_idx = cursor.getColumnIndex("_EMOTION");
		int t_idx =cursor.getColumnIndex("_TYPE");
		int r_idx = cursor.getColumnIndex("_REASON");
		long ts;
		int emotion, type;
		String reason;
		for (int i=0;i<count;++i){
			cursor.moveToPosition(i);
			ts = cursor.getLong(ts_idx);
			emotion = cursor.getInt(e_idx);
			type = cursor.getInt(t_idx);
			reason = cursor.getString(r_idx);
			data[i] = new EmotionManageData(ts,emotion,type,reason);
		}
		
		cursor.close();
		db.close();
		
		return data;
	}
	
	public void setEmotionManageUploaded(long ts){
		db = dbHelper.getWritableDatabase();
		String sql = "UPDATE EmotionManageDB SET _UPLOAD = 1 WHERE _TS = "+ts;
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
