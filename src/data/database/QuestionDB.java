package data.database;

import java.util.Calendar;

import data.calculate.TimeBlock;
import data.info.EmotionData;
import data.info.EmotionManageData;
import data.info.QuestionnaireData;
import data.info.StorytellingUsage;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

public class QuestionDB {

	
	private SQLiteOpenHelper dbHelper = null;
    private SQLiteDatabase db = null;
    private Context context;
    
	public QuestionDB(Context context){
		this.context = context;
		dbHelper = new DBHelper(context);
	}
	
	public boolean insertQuestionnaire(String seq, int type){
		boolean addAcc = false;
		Calendar cal = Calendar.getInstance();
		long ts = cal.getTimeInMillis();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int tb = TimeBlock.getTimeBlock(cal.get(Calendar.HOUR_OF_DAY));
		
		String sql;
		Cursor cursor;
		db = dbHelper.getWritableDatabase();
		
		sql = "SELECT * FROM Questionnaire WHERE type <> -1 ORDER BY id DESC LIMIT 1";
		cursor  = db.rawQuery(sql, null);
		int[] acc = new int[12];
		int[] used = new int[12];
		long _ts = 0L;
		if (cursor.moveToFirst()){
			//_ts = cursor.getLong(1);
			for (int i=0;i<12;++i){
				acc[i] = cursor.getInt(i+5);
				used[i] = cursor.getInt(i+17);
			}
		}
		cursor.close();
		
		sql = "SELECT * FROM Questionnaire WHERE type >= 0 ORDER BY id DESC LIMIT 1";
		cursor  = db.rawQuery(sql, null);
		if (cursor.moveToFirst())
			_ts = cursor.getLong(1);
		cursor.close();
		
		Calendar cal2 = Calendar.getInstance();
		cal2.setTimeInMillis(_ts);
		int _year = cal2.get(Calendar.YEAR);
		int _month = cal2.get(Calendar.MONTH);
		int _day = cal2.get(Calendar.DAY_OF_MONTH);
		int _tb = TimeBlock.getTimeBlock(cal2.get(Calendar.HOUR_OF_DAY));
		
		
		if (StartDateCheck.check(context))
		if (year!=_year || month != _month || day != _day || tb != _tb)
			if (type >=0 && type < 4){
				++acc[type*3 + tb];
				addAcc = true;
			}
		
		sql = "INSERT INTO Questionnaire (ts,type,sequence," +
				"acc_tb0_0, acc_tb1_0, acc_tb2_0,used_tb0_0, used_tb1_0, used_tb2_0,"+
				"acc_tb0_1, acc_tb1_1, acc_tb2_1,used_tb0_1, used_tb1_1, used_tb2_1,"+
				"acc_tb0_2, acc_tb1_2, acc_tb2_2,used_tb0_2, used_tb1_2, used_tb2_2,"+
				"acc_tb0_3, acc_tb1_3, acc_tb2_3,used_tb0_3, used_tb1_3, used_tb2_3"+
				") VALUES ("+ts+","+type+","+"'"+seq+"'"+","+
				acc[0]+","+acc[1]+","+acc[2]+","+used[0]+","+used[1]+","+used[2]+","+
				acc[3]+","+acc[4]+","+acc[5]+","+used[3]+","+used[4]+","+used[5]+","+
				acc[6]+","+acc[7]+","+acc[8]+","+used[6]+","+used[7]+","+used[8]+","+
				acc[9]+","+acc[10]+","+acc[11]+","+used[9]+","+used[10]+","+used[11]+
				")";
		db.execSQL(sql);
		
		db.close();
		return addAcc;
	}
	
	public QuestionnaireData getLatestQuestionnaire(){
		db =dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;
		sql = "SELECT * FROM Questionnaire ORDER BY id DESC LIMIT 1";
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0){
			cursor.close();
			db.close();
			return new QuestionnaireData(0,0,null,null,null);
		}
		cursor.moveToFirst();
		int[] acc = new int[12];
		int[] used = new int[12];
		for (int j=0; j<12;++j){
			acc[j] = cursor.getInt(j+5);
			used[j] = cursor.getInt(j+17);
		}
		cursor.close();
		db.close();
		return new QuestionnaireData(0,0,null,acc,used);
	}
	
	public QuestionnaireData[] getNotUploadedQuestionnaire(){
		QuestionnaireData[] data = null;
		db =dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;
		
		sql = "SELECT * FROM Questionnaire WHERE upload = 0";
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0){
			cursor.close();
			db.close();
			return null;
		}
		
		data = new QuestionnaireData[count];
		
		long ts;
		int type;
		String seq;
		int[] acc = new int[12];
		int[] used = new int[12];
		
		for (int i=0;i<count;++i){
			for (int j=0;j<12;++j)
				acc[j] = used[j] = 0;
			
			cursor.moveToPosition(i);
			ts = cursor.getLong(1);
			type = cursor.getInt(2);
			seq = cursor.getString(3);
			for (int j=0; j<12;++j){
				acc[j] = cursor.getInt(j+5);
				used[j] = cursor.getInt(j+17);
			}
			data[i] = new QuestionnaireData(ts,type,seq,acc,used);
		}
		
		cursor.close();
		db.close();
		
		return data;
	}
	
	public void setQuestionnaireUploaded(long ts){
		db = dbHelper.getWritableDatabase();
		String sql = "UPDATE Questionnaire SET upload = 1 WHERE ts = "+ts;
		db.execSQL(sql);
		db.close();
	}
	
	
	public boolean insertEmotion(int selection, String call){
		boolean addAcc = false;
		
		Calendar cal = Calendar.getInstance();
		long ts = cal.getTimeInMillis();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int tb = TimeBlock.getTimeBlock(cal.get(Calendar.HOUR_OF_DAY));
		
		String sql;
		Cursor cursor;
		db = dbHelper.getWritableDatabase();
		sql ="SELECT  * FROM Emotion ORDER BY id DESC LIMIT 1";
		cursor  = db.rawQuery(sql, null);
		int[] acc = new int[3];
		int[] used = new int[3];
		long _ts = 0L;
		if (cursor.moveToFirst()){
			//_ts = cursor.getLong(1);
			for (int i=0;i<3;++i){
				acc[i] = cursor.getInt(i+5);
				used[i] = cursor.getInt(i+8);
			}
		}
		cursor.close();
		
		sql ="SELECT  * FROM Emotion WHERE selection <> -1 ORDER BY id DESC LIMIT 1";
		cursor  = db.rawQuery(sql, null);
		if (cursor.moveToFirst())
			_ts = cursor.getLong(1);
		cursor.close();
		
		Calendar cal2 = Calendar.getInstance();
		cal2.setTimeInMillis(_ts);
		int _year = cal2.get(Calendar.YEAR);
		int _month = cal2.get(Calendar.MONTH);
		int _day = cal2.get(Calendar.DAY_OF_MONTH);
		int _tb = TimeBlock.getTimeBlock(cal2.get(Calendar.HOUR_OF_DAY));
		
		if (StartDateCheck.check(context))
		if (year!=_year || month != _month || day != _day || tb != _tb){
			addAcc = true;
			++acc[tb];
		}
		if (call != null)
			sql = "INSERT INTO Emotion (ts,selection,call," +
				"acc_tb0, acc_tb1, acc_tb2,used_tb0, used_tb1, used_tb2"+
				") VALUES ("+ts+","+selection+","+"'"+call+"'"+","+
				acc[0]+","+acc[1]+","+acc[2]+","+used[0]+","+used[1]+","+used[2]+
				")";
		else
			sql = "INSERT INTO Emotion (ts,selection," +
				"acc_tb0, acc_tb1, acc_tb2,used_tb0, used_tb1, used_tb2"+
				") VALUES ("+ts+","+selection+","+
				acc[0]+","+acc[1]+","+acc[2]+","+used[0]+","+used[1]+","+used[2]+
				")";
		
		db.execSQL(sql);
		db.close();
		return addAcc;
	}
	
	public boolean insertEmotionManage(int emotion, int type, String reason){
		boolean addAcc = false;
		Calendar cal = Calendar.getInstance();
		long ts = cal.getTimeInMillis();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int tb = TimeBlock.getTimeBlock(cal.get(Calendar.HOUR_OF_DAY));
		
		String sql;
		Cursor cursor;
		db = dbHelper.getWritableDatabase();
		sql ="SELECT  * FROM EmotionManage ORDER BY id DESC LIMIT 1";
		cursor  = db.rawQuery(sql, null);
		int[] acc = new int[3];
		int[] used = new int[3];
		long _ts = 0L;
		if (cursor.moveToFirst()){
			//_ts = cursor.getLong(1);
			for (int i=0;i<3;++i){
				acc[i] = cursor.getInt(i+6);
				used[i] = cursor.getInt(i+9);
			}
		}
		cursor.close();
		
		sql ="SELECT  * FROM EmotionManage WHERE emotion <> -1 AND type <> -1 ORDER BY id DESC LIMIT 1";
		cursor  = db.rawQuery(sql, null);
		if (cursor.moveToFirst())
			_ts = cursor.getLong(1);
		cursor.close();
		
		Calendar cal2 = Calendar.getInstance();
		cal2.setTimeInMillis(_ts);
		int _year = cal2.get(Calendar.YEAR);
		int _month = cal2.get(Calendar.MONTH);
		int _day = cal2.get(Calendar.DAY_OF_MONTH);
		int _tb = TimeBlock.getTimeBlock(cal2.get(Calendar.HOUR_OF_DAY));
		
		if (StartDateCheck.check(context))
		if (year!=_year || month != _month || day != _day || tb != _tb){
			++acc[tb];
			addAcc = true;
		}
		sql = "INSERT INTO EmotionManage (ts,emotion,type,reason," +
				"acc_tb0, acc_tb1, acc_tb2,used_tb0, used_tb1, used_tb2"+
				") VALUES ("+ts+","+emotion+","+type+","+"'"+reason+"'"+","+
				acc[0]+","+acc[1]+","+acc[2]+","+used[0]+","+used[1]+","+used[2]+
				")";
		db.execSQL(sql);
		db.close();
		return addAcc;
	}
	
	public EmotionData getLatestEmotion(){
		db =dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;
		sql = "SELECT * FROM Emotion ORDER BY id DESC LIMIT 1";
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0){
			cursor.close();
			db.close();
			return new EmotionData(0,0,null,null,null);
		}
		cursor.moveToFirst();
		int[] acc = new int[3];
		int[] used = new int[3];
		for (int j=0; j<3;++j){
			acc[j] = cursor.getInt(j+5);
			used[j] = cursor.getInt(j+8);
		}
		cursor.close();
		db.close();
		return new EmotionData(0,0,null,acc,used);
	}
	
	
	public EmotionData[] getNotUploadedEmotion(){
		EmotionData[] data = null;
		db =dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;

		sql = "SELECT * FROM Emotion WHERE upload = 0";
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0){
			cursor.close();
			db.close();
			return null;
		}
		
		data = new EmotionData[count];
		long ts;
		int selection;
		String call;
		int[] acc = new int[3];
		int[] used = new int[3];
		for (int i=0;i<count;++i){
			cursor.moveToPosition(i);
			ts = cursor.getLong(1);
			selection = cursor.getInt(2);
			
			for (int j=0;j<acc.length;++j){
				acc[j] = cursor.getInt(j+5);
				used[j] = cursor.getInt(j+8);
			}
			if (selection !=3)
				data[i] = new EmotionData(ts,selection,null,acc,used);
			else{
				call = cursor.getString(3);
				data[i] = new EmotionData(ts,selection,call,acc,used);
			}
		}
		
		cursor.close();
		db.close();
		
		return data;
	}
	
	public void setEmotionUploaded(long ts){
		db = dbHelper.getWritableDatabase();
		String sql = "UPDATE Emotion SET upload = 1 WHERE ts = "+ts;
		db.execSQL(sql);
		db.close();
	}
	
	public EmotionManageData getLatestEmotionManage(){
		db =dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;
		sql = "SELECT * FROM EmotionManage ORDER BY id DESC LIMIT 1";
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0){
			cursor.close();
			db.close();
			return new EmotionManageData(0,0,0,null,null,null);
		}
		cursor.moveToFirst();
		int[] acc = new int[3];
		int[] used = new int[3];
		for (int j=0; j<3;++j){
			acc[j] = cursor.getInt(j+6);
			used[j] = cursor.getInt(j+9);
		}
		cursor.close();
		db.close();
		return new EmotionManageData(0,0,0,null,acc,used);
	}
	
	
	public EmotionManageData[] getNotUploadedEmotionManage(){
		EmotionManageData[] data = null;
		db =dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;

		sql = "SELECT * FROM EmotionManage WHERE upload = 0";
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0){
			cursor.close();
			db.close();
			return null;
		}
		
		data = new EmotionManageData[count];
		long ts;
		int emotion;
		int type;
		String reason;
		int[] acc = new int[3];
		int[] used = new int[3];
		cursor.moveToFirst();
		for (int i=0;i<count;++i){
			cursor.moveToPosition(i);
			ts = cursor.getLong(1);
			emotion = cursor.getInt(2);
			type = cursor.getInt(3);
			reason = cursor.getString(4);
			
			for (int j=0;j<acc.length;++j){
				acc[j] = cursor.getInt(j+6);
				used[j] = cursor.getInt(j+9);
			}
			data[i] = new EmotionManageData(ts,emotion,type,reason,acc,used);
		}
		
		cursor.close();
		db.close();
		
		return data;
	}
	
	public void setEmotionManageUploaded(long ts){
		db = dbHelper.getWritableDatabase();
		String sql = "UPDATE EmotionManage SET upload = 1 WHERE ts = "+ts;
		db.execSQL(sql);
		db.close();
	}
	
    public String[] getInsertedReason(int type){
    	db = dbHelper.getReadableDatabase();
    	String sql = "SELECT DISTINCT reason FROM EmotionManage WHERE type = "+type+" ORDER BY id DESC LIMIT 4";
    	String[] out = null;
    	
    	Cursor cursor = db.rawQuery(sql, null);
    	if (cursor.getCount() == 0){
    		cursor.close();
        	db.close();
    		return null;
    	}
    	out = new String[cursor.getCount()];
    	
    	for (int i=0;i<out.length;++i)
    		if (cursor.moveToPosition(i))
    			out[i] = cursor.getString(0);
    	
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
    
    public void cleanAcc(long ts){
    	db = dbHelper.getWritableDatabase();
    	String sql = "SELECT acc_tb0,acc_tb1,acc_tb2 FROM Emotion ORDER BY id DESC LIMIT 1";
    	Cursor cursor = db.rawQuery(sql, null);
    	if (cursor.moveToFirst()){
    		int a0 = cursor.getInt(0);
    		int a1 = cursor.getInt(1);
    		int a2 = cursor.getInt(2);
    		sql = "INSERT INTO Emotion (ts,selection,acc_tb0,acc_tb1,acc_tb2,used_tb0,used_tb1,used_tb2) VALUES(" +
    				ts+","+(-1)+","+a0+","+a1+","+a2+","+a0+","+a1+","+a2+
    				")";
    		db.execSQL(sql);
    	}else{
    		int a0 = 0;
    		int a1 = 0;
    		int a2 = 0;
    		sql = "INSERT INTO Emotion (ts,selection,acc_tb0,acc_tb1,acc_tb2,used_tb0,used_tb1,used_tb2) VALUES(" +
    				ts+","+(-1)+","+a0+","+a1+","+a2+","+a0+","+a1+","+a2+
    				")";
    		db.execSQL(sql);
    	}
    	cursor.close();
    	
    	sql = "SELECT acc_tb0,acc_tb1,acc_tb2 FROM EmotionManage ORDER BY id DESC LIMIT 1";
    	cursor = db.rawQuery(sql, null);
    	if (cursor.moveToFirst()){
    		int a0 = cursor.getInt(0);
    		int a1 = cursor.getInt(1);
    		int a2 = cursor.getInt(2);
    		sql = "INSERT INTO EmotionManage (ts,emotion,type,reason,acc_tb0,acc_tb1,acc_tb2,used_tb0,used_tb1,used_tb2) VALUES(" +
    				ts+","+(-1)+","+(-1)+","+"'CLEAN SELF HELP COUNTER'"+","+a0+","+a1+","+a2+","+a0+","+a1+","+a2+
    				")";
    		db.execSQL(sql);
    	}else{
    		int a0 = 0;
    		int a1 = 0;
    		int a2 = 0;
    		sql = "INSERT INTO EmotionManage (ts,emotion,type,reason,acc_tb0,acc_tb1,acc_tb2,used_tb0,used_tb1,used_tb2) VALUES(" +
    				ts+","+(-1)+","+(-1)+","+"'CLEAN SELF HELP COUNTER'"+","+a0+","+a1+","+a2+","+a0+","+a1+","+a2+
    				")";
    		db.execSQL(sql);
    	}
    	cursor.close();
    	
    	sql = "SELECT * FROM Questionnaire ORDER BY id DESC LIMIT 1";
    	cursor = db.rawQuery(sql, null);
    	if (cursor.moveToFirst()){
    		int[] acc = new int[12];
    		for (int i=0;i<acc.length;++i)
    			acc[i] = cursor.getInt(i+5);
    		sql = "INSERT INTO Questionnaire (ts,type,sequence," +
    				"acc_tb0_0, acc_tb1_0, acc_tb2_0,used_tb0_0, used_tb1_0, used_tb2_0,"+
    				"acc_tb0_1, acc_tb1_1, acc_tb2_1,used_tb0_1, used_tb1_1, used_tb2_1,"+
    				"acc_tb0_2, acc_tb1_2, acc_tb2_2,used_tb0_2, used_tb1_2, used_tb2_2,"+
    				"acc_tb0_3, acc_tb1_3, acc_tb2_3,used_tb0_3, used_tb1_3, used_tb2_3"+
    				") VALUES ("+ts+","+(-2)+","+"'"+"-1,-1"+"'"+","+
    				acc[0]+","+acc[1]+","+acc[2]+","+acc[0]+","+acc[1]+","+acc[2]+","+
    				acc[3]+","+acc[4]+","+acc[5]+","+acc[3]+","+acc[4]+","+acc[5]+","+
    				acc[6]+","+acc[7]+","+acc[8]+","+acc[6]+","+acc[7]+","+acc[8]+","+
    				acc[9]+","+acc[10]+","+acc[11]+","+acc[9]+","+acc[10]+","+acc[11]+
    				")";
    		db.execSQL(sql);
    	}else{
    		int acc = 0;
    		sql = "INSERT INTO Questionnaire (ts,type,sequence," +
    				"acc_tb0_0, acc_tb1_0, acc_tb2_0,used_tb0_0, used_tb1_0, used_tb2_0,"+
    				"acc_tb0_1, acc_tb1_1, acc_tb2_1,used_tb0_1, used_tb1_1, used_tb2_1,"+
    				"acc_tb0_2, acc_tb1_2, acc_tb2_2,used_tb0_2, used_tb1_2, used_tb2_2,"+
    				"acc_tb0_3, acc_tb1_3, acc_tb2_3,used_tb0_3, used_tb1_3, used_tb2_3"+
    				") VALUES ("+ts+","+(-2)+","+"'"+"-1,-1"+"'"+","+
    				acc+","+acc+","+acc+","+acc+","+acc+","+acc+","+
    				acc+","+acc+","+acc+","+acc+","+acc+","+acc+","+
    				acc+","+acc+","+acc+","+acc+","+acc+","+acc+","+
    				acc+","+acc+","+acc+","+acc+","+acc+","+acc+
    				")";
    		db.execSQL(sql);
    	}
    	
    	sql = "SELECT * FROM StorytellingUsage ORDER BY id DESC LIMIT 1";
    	cursor = db.rawQuery(sql, null);
    	int acc = 0;
		int used = 0;
    	if (cursor.moveToFirst()){
    		acc = cursor.getInt(cursor.getColumnIndex("acc"));
    		used = cursor.getInt(cursor.getColumnIndex("used"));
    	}

    	used = acc;
    	int usage = -1;
    	
    	sql = "INSERT INTO StorytellingUsage (ts, daily_usage, acc, used) VALUES (" +
				ts+","+usage+","+acc+","+used+
				" )";
    		db.execSQL(sql);
    	cursor.close();
    	db.close();
    }
    
    public void restoreData(EmotionData ed, EmotionManageData emd, QuestionnaireData qd){
    	db = dbHelper.getWritableDatabase();
    	String sql;
    	
    	if (ed!=null){
    		sql = "INSERT INTO Emotion (ts,selection," +
				"acc_tb0, acc_tb1, acc_tb2,used_tb0, used_tb1, used_tb2, upload"+
				") VALUES ("+ed.ts+","+ed.selection+","+
				ed.acc[0]+","+ed.acc[1]+","+ed.acc[2]+","+ed.used[0]+","+ed.used[1]+","+ed.used[2]+
				",1)";
    		db.execSQL(sql);
    	}
    	if (emd!=null){
    		sql = "INSERT INTO EmotionManage (ts,emotion,type,reason," +
				"acc_tb0, acc_tb1, acc_tb2,used_tb0, used_tb1, used_tb2, upload"+
				") VALUES ("+emd.ts+","+emd.emotion+","+emd.type+","+"'"+emd.reason+"'"+","+
				emd.acc[0]+","+emd.acc[1]+","+emd.acc[2]+","+emd.used[0]+","+emd.used[1]+","+emd.used[2]+
				",1)";
    		db.execSQL(sql);
    	}
    	if (qd!=null){
    		sql = "INSERT INTO Questionnaire (ts,type,sequence," +
				"acc_tb0_0, acc_tb1_0, acc_tb2_0,used_tb0_0, used_tb1_0, used_tb2_0,"+
				"acc_tb0_1, acc_tb1_1, acc_tb2_1,used_tb0_1, used_tb1_1, used_tb2_1,"+
				"acc_tb0_2, acc_tb1_2, acc_tb2_2,used_tb0_2, used_tb1_2, used_tb2_2,"+
				"acc_tb0_3, acc_tb1_3, acc_tb2_3,used_tb0_3, used_tb1_3, used_tb2_3"+
				") VALUES ("+qd.ts+","+qd.type+","+"'"+qd.seq+"'"+","+
				qd.acc[0]+","+qd.acc[1]+","+qd.acc[2]+","+qd.used[0]+","+qd.used[1]+","+qd.used[2]+","+
				qd.acc[3]+","+qd.acc[4]+","+qd.acc[5]+","+qd.used[3]+","+qd.used[4]+","+qd.used[5]+","+
				qd.acc[6]+","+qd.acc[7]+","+qd.acc[8]+","+qd.used[6]+","+qd.used[7]+","+qd.used[8]+","+
				qd.acc[9]+","+qd.acc[10]+","+qd.acc[11]+","+qd.used[9]+","+qd.used[10]+","+qd.used[11]+
				")";
    		db.execSQL(sql);
    	}
    }
    
    public void restoreData(StorytellingUsage su){
    	db = dbHelper.getWritableDatabase();
    	String sql;
    	
    	if (su!=null){
    		sql = "INSERT INTO StorytellingUsage (ts, daily_usage, acc, used, upload) VALUES (" +
    				su.ts+","+su.daily_usage+","+su.acc+","+su.used+","+1+
    				" )";
    		db.execSQL(sql);
    	}
    }
    
    public long[] getNotUploadSHCUpdate(){
    	db = dbHelper.getReadableDatabase();
    	String sql = "SELECT * FROM SelfHelpCounterUpdate WHERE upload = 0";
    	Cursor cursor = db.rawQuery(sql , null);
    	int len = cursor.getCount();
    	if (len == 0){
    		cursor.close();
    		db.close();
    		return null;
    	}
    	long[] ts = null;
    	ts = new long[len];
    	int ts_idx = cursor.getColumnIndex("ts");
    	for(int i=0;i<len;++i){
    		cursor.moveToPosition(i);
    		ts[i] = cursor.getLong(ts_idx);
    	}
    	cursor.close();
    	db.close();
		return ts;
    }
    
    public void updateNotUploadSHCUpdate(long ts){
    	db = dbHelper.getWritableDatabase();
    	String sql = "UPDATE SelfHelpCounterUpdate SET upload = 1 WHERE ts =" +ts;
    	db.execSQL(sql);
    	db.close();
    }
    
    private static final int DATE_USAGE_LIMIT = 3; 
    private static final long FIVE_MINS = 5*60*1000L;
    
    
    public boolean insertStorytellingUsage(String name, int minutes){
    	Log.d("STORYTELLING","INSERT");
    	boolean addAcc = false;
		Calendar cal = Calendar.getInstance();
		long ts = cal.getTimeInMillis();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
		String sql;
		Cursor cursor;
		db = dbHelper.getWritableDatabase();
		
		sql = "SELECT ts, daily_usage, acc, used  FROM StorytellingUsage ORDER BY id DESC LIMIT 1";
		cursor  = db.rawQuery(sql, null);
		int acc = 0;
		int usage = 0;
		int used = 0;
		long _ts = 0L;
		if (cursor.moveToFirst()){
			_ts = cursor.getLong(cursor.getColumnIndex("ts"));
			acc = cursor.getInt(cursor.getColumnIndex("acc"));
			usage = cursor.getInt(cursor.getColumnIndex("daily_usage"));
			used = cursor.getInt(cursor.getColumnIndex("used"));
		}
		Calendar cal2 = Calendar.getInstance();
		cal2.setTimeInMillis(_ts);
		int _year = cal2.get(Calendar.YEAR);
		int _month = cal2.get(Calendar.MONTH);
		int _day = cal2.get(Calendar.DAY_OF_MONTH);
		
		if (usage < 0)//for used condition
			usage = 0;
		
		if (year==_year && month == _month && day == _day)
			++usage;
		else
			usage = 1;
		
		if (StartDateCheck.check(context)){
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
			long share_time = sp.getLong("share_storytelling_time", 0);
			long cur_time = System.currentTimeMillis();
			if (cur_time - share_time > FIVE_MINS){
				if (usage <= DATE_USAGE_LIMIT ){
					Log.d("STORYTELLING","ADD ACC");
					addAcc = true;
					++acc;
				}
			}
		}
		sql = "INSERT INTO StorytellingUsage (ts, daily_usage, acc, used, name, minutes) VALUES (" +
				ts+","+usage+","+acc+","+used+","+"'"+name+"'"+","+minutes+
				" )";
		db.execSQL(sql);
		
		cursor.close();
		db.close();
		return addAcc;
    }
    
    public StorytellingUsage[] getNotUploadedStorytellingUsage(){
		StorytellingUsage[] data = null;
		db =dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;
		
		sql = "SELECT * FROM StorytellingUsage WHERE upload = 0";
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0){
			cursor.close();
			db.close();
			return null;
		}
		
		data = new StorytellingUsage[count];
		for (int i=0;i<count;++i){
			cursor.moveToPosition(i);
			long ts = cursor.getLong(cursor.getColumnIndex("ts"));
			int acc = cursor.getInt(cursor.getColumnIndex("acc"));
			int usage = cursor.getInt(cursor.getColumnIndex("daily_usage"));
			int used = cursor.getInt(cursor.getColumnIndex("used"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			int minutes = cursor.getInt(cursor.getColumnIndex("minutes"));
			data[i] = new StorytellingUsage(ts,usage,acc,used,name,minutes);
		}
		return data;
    }
    
    public StorytellingUsage getLatestStorytellingUsage(){
		db =dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;
		sql = "SELECT * FROM StorytellingUsage ORDER BY id DESC LIMIT 1";
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0){
			cursor.close();
			db.close();
			return new StorytellingUsage(0,0,0,0,"NONE",-1);
		}
		cursor.moveToFirst();
		long ts = cursor.getLong(cursor.getColumnIndex("ts"));
		int acc = cursor.getInt(cursor.getColumnIndex("acc"));
		int usage = cursor.getInt(cursor.getColumnIndex("daily_usage"));
		int used = cursor.getInt(cursor.getColumnIndex("used"));
		String name = cursor.getString(cursor.getColumnIndex("name"));
		int minutes = cursor.getInt(cursor.getColumnIndex("minutes"));
		cursor.close();
		db.close();
		return new StorytellingUsage(ts,usage,acc,used,name,minutes);
	}
    
    public void setStorytellingUsageUploaded(long ts){
		db = dbHelper.getWritableDatabase();
		String sql = "UPDATE StorytellingUsage SET upload = 1 WHERE ts = "+ts;
		db.execSQL(sql);
		db.close();
	}
}
