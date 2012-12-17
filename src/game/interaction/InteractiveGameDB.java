package game.interaction;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import database.DBHelper;

public class InteractiveGameDB {
	private SQLiteOpenHelper gDBHelper = null;
    private SQLiteDatabase gDB = null;
    
    public InteractiveGameDB(Activity gActivity){
		gDBHelper = new DBHelper(gActivity);
    }
    
    
    public void updateState(InteractiveGameState[] newStates){
    	gDB = gDBHelper.getWritableDatabase();
    	if (newStates == null)
    		return;
    	boolean need_to_create = false;
		Cursor cursor;
		cursor = gDB.rawQuery("SELECT _ID FROM AlcoholInteractiveGame",null);
		if (cursor.getCount()==0)
			need_to_create = true;
    	for (int i=0;i<newStates.length;++i){
    		int state = newStates[i].state;
    		int coin = newStates[i].coin;
    		String pid = newStates[i].PID;
    		if (need_to_create){
    			gDB.execSQL(
    					"INSERT INTO AlcoholInteractiveGame ( _STATE,_COIN,_PID ) VALUES (" + String.valueOf(state)+","+String.valueOf(coin)+",'"+pid+"')"
    					);
    		}
    		else{
    			gDB.execSQL(
    					"UPDATE AlcoholInteractiveGame SET _STATE = "+String.valueOf(state) + ", _COIN ="+String.valueOf(coin)+" WHERE _PID ='"+pid+"'"
    					);
    		}
    	}
    	gDB.close();
    }
    
    public InteractiveGameState[] getStates(){
    	gDB = gDBHelper.getReadableDatabase();
    	Cursor cursor;
    	
    	cursor = gDB.rawQuery("SELECT _ID,_STATE,_COIN,_PID FROM AlcoholInteractiveGame ORDER BY _PID ASC",null);
    	if (cursor.getCount()==0){
    		gDB.close();
    		return null;
    	}
    	int list_size = cursor.getCount();
    	
    	InteractiveGameState[] stateList = new InteractiveGameState[list_size];
    	
    	cursor.moveToFirst();
    	
    	int state = cursor.getInt(1);
    	int coin = cursor.getInt(2);
    	String pid = cursor.getString(3);
    	stateList[0]=new InteractiveGameState(state,coin,pid);
    	int i = 1;
    	while(cursor.moveToNext()){
        	state = cursor.getInt(1);
        	coin = cursor.getInt(2);
        	stateList[i]=new InteractiveGameState(state,coin,pid);
        	++i;
    	}
    	
    	gDB.close();
    	return stateList;
    }
}