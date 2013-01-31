package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	/*SQLiteOpenHelper. need to migrate with */
	private static final String DATABASE_NAME = "Alcohol Project";
	private static final int DB_VERSION = 4;
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE AlcoholTreeGame (" +
                        " _ID INTEGER PRIMARY KEY, " +
                        " _STAGE INTEGER NOT NULL, " +
                        " _COIN INTEGER NOT NULL," +
                        " _DATE INTEGER NOT NULL,"+
                        " _BRAC FLOAT NOT NULL"+
                ")"
        );
        db.execSQL(
        		"CREATE TABLE AlcoholInteractiveGame ("+
        				"_ID INTEGER PRIMARY KEY," +
        				"_PID CHAR[50] NOT NULL," +
        				"_NAME CHAR[50] NOT NULL,"+
        				"_STAGE INTEGER NOT NULL," +
        				"_COIN INTEGER NOT NULL )" 
        		);
        db.execSQL(
        		"CREATE TABLE NotUploadedTimeStamp ("+
        				"_ID INTEGER PRIMARY KEY," +
        				"_TS CHAR[50] NOT NULL )"
        		);
        db.execSQL(
        		"CREATE TABLE DayCompletion ("+
        				" _ID INTEGER PRIMARY KEY," +
        				" _YEAR INTEGER NOT NULL," +
        				" _MONTH INTEGER NOT NULL," +
        				" _DATE INTEGER NOT NULL," +
        				" _BLOCK INTEGER NOT NULL)"
        		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int old_ver, int new_ver){
		db.execSQL("DROP TABLE IF EXISTS AlcoholTreeGame");
		db.execSQL("DROP TABLE IF EXISTS  AlcoholInteractiveGame");
		db.execSQL("DROP TABLE IF EXISTS NotUploadedTimeStamp");
		db.execSQL("DROP TABLE IF EXISTS DayCompletion");
		onCreate(db);
	}
	
	public void onOpen(SQLiteDatabase db){
		super.onOpen(db);
	}
	public synchronized void close(){
		super.close();
	}

}
