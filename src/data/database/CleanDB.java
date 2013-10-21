package data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CleanDB {

	private SQLiteOpenHelper dbHelper = null;
    private SQLiteDatabase db = null;
	
	public CleanDB(Context context){
		dbHelper = new DBHelper(context);
	}
	public void clean(){
		db = dbHelper.getWritableDatabase();
		String sql;
		sql = "DELETE FROM Detection";
		db.execSQL(sql);
		sql = "DELETE FROM AccDetection";
		db.execSQL(sql);
		sql = "DELETE FROM UsedDetection";
		db.execSQL(sql);
		sql = "DELETE FROM Ranking";
		db.execSQL(sql);
		sql = "DELETE FROM Record";
		db.execSQL(sql);
		sql = "DELETE FROM Emotion";
		db.execSQL(sql);
		sql = "DELETE FROM EmotionManage";
		db.execSQL(sql);
		sql = "DELETE FROM Questionnaire";
		db.execSQL(sql);
		sql = "DELETE FROM SelfHelpCounterUpdate";
		db.execSQL(sql);
		sql = "DELETE FROM StorytellingUsage";
		db.execSQL(sql);
		sql = "DELETE FROM StorytellingFling";
		db.execSQL(sql);
		db.close();
	}
	
}
