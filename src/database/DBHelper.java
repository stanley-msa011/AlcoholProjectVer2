package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	/*SQLiteOpenHelper. need to migrate with */
	private static final String DATABASE_NAME = "Alcohol Project";
	private static final int DB_VERSION = 5;
	
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
                        " _BRAC FLOAT NOT NULL"+
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
        		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int old_ver, int new_ver){
		db.execSQL("DROP TABLE IF EXISTS HistoryGame");
		db.execSQL("DROP TABLE IF EXISTS InteractionGame");
		db.execSQL("DROP TABLE IF EXISTS NotUploadedTS");
		onCreate(db);
	}
	
	public void onOpen(SQLiteDatabase db){
		super.onOpen(db);
	}
	public synchronized void close(){
		super.close();
	}

}
