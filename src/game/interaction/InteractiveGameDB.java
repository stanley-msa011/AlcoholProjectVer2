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
    	//boolean need_to_create = false;
		Cursor cursor;
		//cursor = gDB.rawQuery("SELECT _ID FROM AlcoholInteractiveGame",null);
		//if (cursor.getCount()==0)
		//	need_to_create = true;
    	for (int i=0;i<newStates.length;++i){
    		int stage = newStates[i].stage;
    		int coin = newStates[i].coin;
    		String pid = newStates[i].PID;
    		String name = newStates[i].name;
    		
    		cursor = gDB.rawQuery("SELECT _ID FROM AlcoholInteractiveGame WHERE _PID='"+pid+"'",null);
    		if (cursor.getCount() == 0){
    			gDB.execSQL(
    					"INSERT INTO AlcoholInteractiveGame ( _STAGE,_COIN,_PID,_NAME ) VALUES (" + String.valueOf(stage)+","+String.valueOf(coin)+",'"+pid+"',"+"'"+name+"')"
    					);
    		}
    		else{
    			gDB.execSQL(
    					"UPDATE AlcoholInteractiveGame SET _STAGE = "+String.valueOf(stage) 
    					+ ", _COIN ="+String.valueOf(coin)
    					+ ", _NAME = '"+name+"'"
    					+" WHERE _PID ='"+pid+"'"
    					);
    		}
    		/*if (need_to_create){
    			gDB.execSQL(
    					"INSERT INTO AlcoholInteractiveGame ( _STAGE,_COIN,_PID,_NAME ) VALUES (" + String.valueOf(stage)+","+String.valueOf(coin)+",'"+pid+"',"+"'"+name+"')"
    					);
    		}
    		else{
    			gDB.execSQL(
    					"UPDATE AlcoholInteractiveGame SET _STAGE = "+String.valueOf(stage) + ", _COIN ="+String.valueOf(coin)+" WHERE _PID ='"+pid+"'"
    					);
    		}*/
    	}
    	gDB.close();
    }
    public String getCodeName(String pid){
    	gDB = gDBHelper.getReadableDatabase();
    	Cursor cursor;
    	
    	cursor = gDB.rawQuery("SELECT _NAME FROM AlcoholInteractiveGame WHERE _PID ='"+pid+"'",null);
    	if (cursor.getCount()==0){
    		gDB.close();
    		return null;
    	}
    	cursor.moveToFirst();
    	String name = cursor.getString(0);
    	gDB.close();
    	return name;
    }
    
    public InteractiveGameState[] getStates(){
    	gDB = gDBHelper.getReadableDatabase();
    	Cursor cursor;
    	
    	cursor = gDB.rawQuery("SELECT _ID,_STAGE,_COIN,_PID,_NAME FROM AlcoholInteractiveGame ORDER BY _NAME ASC",null);
    	if (cursor.getCount()==0){
    		gDB.close();
    		return null;
    	}
    	int list_size = cursor.getCount();
    	
    	InteractiveGameState[] stateList = new InteractiveGameState[list_size];
    	
    	cursor.moveToFirst();
    	
    	int stage = cursor.getInt(1);
    	int coin = cursor.getInt(2);
    	String pid = cursor.getString(3);
    	String name = cursor.getString(4);
    	stateList[0]=new InteractiveGameState(stage,coin,pid,name);
    	int i = 1;
    	while(cursor.moveToNext()){
        	stage = cursor.getInt(1);
        	coin = cursor.getInt(2);
        	pid = cursor.getString(3);
        	name = cursor.getString(4);
        	stateList[i]=new InteractiveGameState(stage,coin,pid,name);
        	++i;
    	}
    	
    	gDB.close();
    	return stateList;
    }
}
