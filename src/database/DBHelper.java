package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	/*SQLiteOpenHelper. need to migrate with */
	private static final String DATABASE_NAME = "Alcohol Project";
	private static final int DB_VERSION = 6;
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE HistoryGame (" +
                        " _ID INTEGER PRIMARY KEY, " +
                        " _LEVEL INTEGER NOT NULL," +
                        " _YEAR INTEGER NOT NULL," +
        				" _MONTH INTEGER NOT NULL," +
        				" _DATE INTEGER NOT NULL,"+
        				" _TS INTEGER NOT NULL," +
        				" _TIMEBLOCK INTEGER NOT NULL,"+
                        " _BRAC FLOAT NOT NULL,"+
                        " _EMOTION INTEGER NOT NULL,"+
                        " _DESIRE INTEGER NOT NULL"+
                ")"
        );
        db.execSQL(
        		"CREATE TABLE InteractionGame ("+
        				"_ID INTEGER PRIMERY KEY," +
        				"_UID CHAR[10] NOT NULL," +
        				"_LEVEL INTEGER NOT NULL" +
        				")"
        );
        db.execSQL(
        		"CREATE TABLE NotUploadedTS ("+
        				"_ID INTEGER PRIMARY KEY," +
        				"_TS INTEGER NOT NULL )"
        		);
        
        //Add after ver 2, Change after ver 4, ver 5
        db.execSQL(
        		"CREATE TABLE RecDB ("+
        				"_ID INTEGER PRIMARY KEY," +
        				"_YEAR INTEGER NOT NULL," +
        				"_MONTH INTEGER NOT NULL," +
        				"_DATE INTEGER NOT NULL," +
        				"_FILENAME CHAR[255] NOT NULL," +
        				"_UPLOAD INTEGER NOT NULL DEFAULT 0," +
        				"_TS INTEGER NOT NULL)"
        		);
        
        //Change after ver3 ,ver 7
        db.execSQL(
        		"CREATE TABLE EmotionDB ("+
        				"_ID INTEGER PRIMARY KEY," +
        				"_TS INTEGER NOT NULL," +
        				"_EMOTION INTEGER NOT NULL," +
        				"_CALL CHAR[255]," +
        				"_UPLOAD INTEGER NOT NULL)"
        		);
        //Change after ver3, clean after ver 7
        db.execSQL(
        		"CREATE TABLE EmotionManageDB ("+
        				"_ID INTEGER PRIMARY KEY," +
        				"_TS INTEGER NOT NULL," +
        				"_EMOTION INTEGER NOT NULL," +
        				"_TYPE INTEGER NOT NULL," +
        				"_REASON CHAR[255] NOT NULL," +
        				"_UPLOAD INTEGER NOT NULL)"
        		);
        
        //Add after ver3, clean after ver 6
        db.execSQL(
        		"CREATE TABLE QuestionnaireDB ("+
        				"_ID INTEGER PRIMARY KEY," +
        				"_TS INTEGER NOT NULL," +
        				"_SEQUENCE CHAR[255] NOT NULL," +
        				"_UPLOAD INTEGER NOT NULL)"
        		);
        
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int old_ver, int new_ver){
		if (old_ver < 2){
	        db.execSQL(
	        		"CREATE TABLE RecDB ("+
	        				"_ID INTEGER PRIMARY KEY," +
	        				"_YEAR INTEGER NOT NULL," +
	        				"_MONTH INTEGER NOT NULL," +
	        				"_DATE INTEGER NOT NULL," +
	        				"_FILENAME CHAR[255] NOT NULL)"
	        		);
		}
		if (old_ver < 3){
			db.execSQL("DROP TABLE IF EXISTS EmotionDB");
			db.execSQL("DROP TABLE IF EXISTS EmotionManageDB");

			db.execSQL(
	        		"CREATE TABLE EmotionDB ("+
	        				"_ID INTEGER PRIMARY KEY," +
	        				"_TS INTEGER NOT NULL," +
	        				"_EMOTION INTEGER NOT NULL," +
	        				"_UPLOAD INTEGER NOT NULL)"
	        		);
	        
	        db.execSQL(
	        		"CREATE TABLE EmotionManageDB ("+
	        				"_ID INTEGER PRIMARY KEY," +
	        				"_TS INTEGER NOT NULL," +
	        				"_EMOTION INTEGER NOT NULL," +
	        				"_TYPE INTEGER NOT NULL," +
	        				"_REASON CHAR[255] NOT NULL," +
	        				"_UPLOAD INTEGER NOT NULL)"
	        		);
	        
	        db.execSQL(
	        		"CREATE TABLE QuestionnaireDB ("+
	        				"_ID INTEGER PRIMARY KEY," +
	        				"_TS INTEGER NOT NULL," +
	        				"_SEQUENCE CHAR[255] NOT NULL," +
	        				"_UPLOAD INTEGER NOT NULL)"
	        		);
		}
		
		if (old_ver < 4){
	        db.execSQL(
	        		"ALTER TABLE RecDB ADD _UPLOAD INTEGER NOT NULL DEFAULT 0"
	        		);
		}
		if (old_ver < 5){
			db.execSQL(
	        		"ALTER TABLE RecDB ADD _TS INTEGER NOT NULL DEFAULT 0"
	        		);
		}
		if (old_ver < 6){
			db.execSQL("DROP TABLE IF EXISTS QuestionnaireDB");
	        db.execSQL(
	        		"CREATE TABLE QuestionnaireDB ("+
	        				"_ID INTEGER PRIMARY KEY," +
	        				"_TS INTEGER NOT NULL," +
	        				"_SEQUENCE CHAR[255] NOT NULL," +
	        				"_UPLOAD INTEGER NOT NULL)"
	        		);
		}
		if (old_ver < 7){
			db.execSQL("DROP TABLE IF EXISTS EmotionDB");
			db.execSQL(
	        		"CREATE TABLE EmotionDB ("+
	        				"_ID INTEGER PRIMARY KEY," +
	        				"_TS INTEGER NOT NULL," +
	        				"_EMOTION INTEGER NOT NULL," +
	        				"_CALL CHAR[255]," +
	        				"_UPLOAD INTEGER NOT NULL)"
	        		);
			db.execSQL("DROP TABLE IF EXISTS EmotionManageDB");
			db.execSQL(
	        		"CREATE TABLE EmotionManageDB ("+
	        				"_ID INTEGER PRIMARY KEY," +
	        				"_TS INTEGER NOT NULL," +
	        				"_EMOTION INTEGER NOT NULL," +
	        				"_TYPE INTEGER NOT NULL," +
	        				"_REASON CHAR[255] NOT NULL," +
	        				"_UPLOAD INTEGER NOT NULL)"
	        		);
		}
		/*
		else{
			db.execSQL("DROP TABLE IF EXISTS HistoryGame");
			db.execSQL("DROP TABLE IF EXISTS InteractionGame");
			db.execSQL("DROP TABLE IF EXISTS NotUploadedTS");
			db.execSQL("DROP TABLE IF EXISTS EmotionDB");
			db.execSQL("DROP TABLE IF EXISTS EmotionManageDB");
			onCreate(db);
		}
		*/
	}
	
	public void onOpen(SQLiteDatabase db){
		super.onOpen(db);
	}
	public synchronized void close(){
		super.close();
	}

}
