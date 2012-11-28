package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	/*SQLiteOpenHelper. need to migrate with */
	private static final String DATABASE_NAME = "Brac_History";
	private static final int DB_VERSION = 2;
	
	
	public final static String KEY_ROWID = "_id";
	public final static String KEY_DATE = "date";
	public final static String KEY_BRAC = "brac";
	
	private final static String DATABASE_TABLE = "user_history";
	
	private final static String DATABASE_CREATE =
			"create table " + DATABASE_TABLE + " (_id integer primary key autoincrement, "
	        + KEY_DATE + " text not null, " + KEY_BRAC + " text not null);";
	
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE AlcoholTreeGame (" +
                        " _ID INTEGER PRIMARY KEY, " +
                        " _STATE INTEGER NOT NULL, " +
                        " _COIN INTEGER NOT NULL" +
                ")"
        );
        db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int old_ver, int new_ver){
		db.execSQL("DROP TABLE IF EXISTS AlcoholTreeGame");
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
		onCreate(db);
	}
	
	public void onOpen(SQLiteDatabase db){
		super.onOpen(db);
	}
	public synchronized void close(){
		super.close();
	}

}
