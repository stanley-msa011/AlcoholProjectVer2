package database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ReuploadDB {
	private SQLiteOpenHelper gDBHelper = null;
    private SQLiteDatabase gDB = null;
    
    public ReuploadDB(Context activity){
		gDBHelper = new DBHelper(activity);
    }
    
    public void storeNotUploadedTimeStamp(String ts){
    	gDB = gDBHelper.getWritableDatabase();
    	gDB.execSQL( 
    	    	"INSERT INTO NotUploadedTimeStamp (_TS) VALUES ("+ts+")");
    	gDB.close();
    }
    
    public void removeNotUploadedTimeStamp(String ts){
    	gDB = gDBHelper.getWritableDatabase();
    	gDB.execSQL( "DELETE FROM NotUploadedTimeStamp WHERE _TS = '"+ts+"'");
    	gDB.close();
    }

    public String[] getNotUploadedTimeStamps(){
    	gDB = gDBHelper.getReadableDatabase();
    	Cursor cursor = 	gDB.rawQuery( "SELECT _ID,_TS FROM NotUploadedTimeStamp ORDER BY _ID ASC",null);
    	
    	if (cursor.getCount()==0){
    		gDB.close();
    		return null;
    	}
    	String[] uploadTS = new String[cursor.getCount()];
    	cursor.moveToFirst();
    	uploadTS[0] =new String(cursor.getString(1));
    	int i = 1;
    	while(cursor.moveToNext()){
    		uploadTS[i] =new String(cursor.getString(1));
        	++i;
    	}
    	gDB.close();
    	return uploadTS;
    }
}
