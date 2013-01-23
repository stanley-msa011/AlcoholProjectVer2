package ioio.examples.hello;

import database.DBHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BracDbAdapter {
//	private final static String TAG = "BracDbAdapter";
	
	public final static String KEY_ROWID = "_id";
	public final static String KEY_DATE = "date";
	public final static String KEY_BRAC = "brac";
	
	private final static String DATABASE_TABLE = "user_history";
	
	private final Context mContext;
	
    private SQLiteOpenHelper mDbHelper;
	private SQLiteDatabase mDb;
    
    public BracDbAdapter(Context context) {
    	mContext = context;
    }
    
    public BracDbAdapter open() throws SQLException {
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
