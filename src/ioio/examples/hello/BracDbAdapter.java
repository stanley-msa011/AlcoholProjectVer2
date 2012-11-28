package ioio.examples.hello;

import database.DBHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BracDbAdapter {
//	private final static String TAG = "BracDbAdapter";
	
	public final static String KEY_ROWID = "_id";
	public final static String KEY_DATE = "date";
	public final static String KEY_BRAC = "brac";
	
//	private final static String DATABASE_NAME = "Brac_History";
	private final static String DATABASE_TABLE = "user_history";
//	private final static int DATABASE_VERSION = 2;
	
//	private final static String DATABASE_CREATE =
//			"create table " + DATABASE_TABLE + " (_id integer primary key autoincrement, "
//	        + KEY_DATE + " text not null, " + KEY_BRAC + " text not null);";
	
	private final Context mContext;
	
	//private DatabaseHelper mDbHelper;
    private SQLiteOpenHelper mDbHelper;
	private SQLiteDatabase mDb;
    
    /*private class DatabaseHelper extends SQLiteOpenHelper {
    	
    	DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
    	
    	@Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }*/
    
    public BracDbAdapter(Context context) {
    	mContext = context;
    }
    
    public BracDbAdapter open() throws SQLException {
        //mDbHelper = new DatabaseHelper(mContext);
    	mDbHelper = new DBHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
    
    public Cursor fetchAllHistory() {
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_DATE, KEY_BRAC}, null, null, null, null, null);
    }
    
    public long createEntry(String date, String brac) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_BRAC, brac);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    public boolean updateEntry(long rowId, String date, String brac) {
        ContentValues args = new ContentValues();
        args.put(KEY_DATE, date);
        args.put(KEY_BRAC, brac);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
